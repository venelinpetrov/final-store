# Stripe Payment Testing Guide (Webhook-Based Flow)

This guide shows you how to test the **webhook-based** Stripe payment integration using **cURL** and **Stripe CLI**.

## ⚡ What Changed?

**Old Flow (Removed):** Frontend → `/complete` endpoint → Create order
**New Flow (Webhook-Based):** Frontend → Stripe → Webhook → Create order ✅

**Why?** Webhooks are more reliable - even if the user closes their browser, Stripe will notify your backend and the order will be created.

---

## 🔑 Prerequisites

1. **Stripe Test API Keys** - Get them from [Stripe Dashboard](https://dashboard.stripe.com/test/apikeys)
2. **Stripe CLI** - Required for webhook testing: `brew install stripe/stripe-cli/stripe`
3. **Test Mode** - Make sure you're using test keys (they start with `sk_test_` and `pk_test_`)
4. **Running Application** - Your Spring Boot app should be running on `http://localhost:8080`

---

## 📋 Testing Flow

### **Step 1: Set Up Webhook Forwarding**

The Stripe CLI forwards webhook events from Stripe to your local server:

```bash
# Login to Stripe (opens browser)
stripe login

# Forward webhooks to your local server
stripe listen --forward-to localhost:8080/api/webhooks/stripe
```

**Important:** Copy the webhook signing secret that appears (starts with `whsec_`). You'll need it in Step 2.

**Expected output:**
```
> Ready! Your webhook signing secret is whsec_xxxxxxxxxxxxx
> Listening for events...
```

Keep this terminal window open!

---

### **Step 2: Configure Webhook Secret**

Add the webhook secret to your `.env` file or environment variables:

```bash
STRIPE_WEBHOOK_SECRET=whsec_xxxxxxxxxxxxx
```

Restart your Spring Boot application to load the new secret.

---

### **Step 3: Create a Cart and Add Items**

```bash
# Create a cart
curl -X POST http://localhost:8080/api/carts

# Add item to cart (replace {cartId} and {variantId})
curl -X POST http://localhost:8080/api/carts/{cartId}/items \
  -H "Content-Type: application/json" \
  -d '{"variantId": 1}'
```

---

### **Step 4: Create PaymentIntent**

```bash
curl -X POST http://localhost:8080/api/payments/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "cartId": "your-cart-uuid",
    "customerId": 1,
    "addressId": 1
  }'
```

**Response:**
```json
{
  "clientSecret": "pi_xxx_secret_yyy",
  "paymentIntentId": "pi_1234567890",
  "amount": 99.99,
  "currency": "eur"
}
```

**Save the `paymentIntentId`!**

---

### **Step 5: Confirm Payment**

Use Stripe CLI to confirm the payment:

```bash
stripe payment_intents confirm pi_1234567890 \
  --payment-method=pm_card_visa
```

**What happens:**
1. Stripe confirms the payment
2. Stripe sends `payment_intent.succeeded` webhook to your server
3. Your webhook handler automatically creates the order, invoice, and payment record
4. Check the Stripe CLI terminal - you should see the webhook event!

---

### **Step 6: Verify Order Was Created**

Check your application logs for:
```
✅ Payment succeeded: pi_1234567890
Created order XXX from cart YYY via webhook
Created invoice ZZZ for order XXX via webhook
Created payment record AAA for invoice ZZZ via webhook
```

You can also verify via API:

```bash
# Check payment status
curl -X GET http://localhost:8080/api/payments/verify/pi_1234567890 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🧪 Stripe Test Cards

Use these test card numbers to simulate different scenarios:

| Card Number | Scenario |
|------------|----------|
| `4242 4242 4242 4242` | ✅ Successful payment |
| `4000 0025 0000 3155` | ✅ Requires 3D Secure authentication |
| `4000 0000 0000 9995` | ❌ Declined (insufficient funds) |
| `4000 0000 0000 0002` | ❌ Declined (generic decline) |
| `4000 0000 0000 0069` | ❌ Expired card |

**For all test cards:**
- **Expiry:** Any future date (e.g., `12/34`)
- **CVC:** Any 3 digits (e.g., `123`)
- **ZIP:** Any 5 digits (e.g., `12345`)

---

## 🎴 Stripe Pre-made Test Payment Methods

Stripe provides pre-configured test payment methods you can use directly without entering card details:

| Payment Method ID | Card Number | Card Type | Result |
|------------------|-------------|-----------|--------|
| `pm_card_visa` | `4242 4242 4242 4242` | Visa | ✅ Always succeeds |
| `pm_card_mastercard` | `5555 5555 5555 4444` | Mastercard | ✅ Always succeeds |
| `pm_card_amex` | `3782 822463 10005` | American Express | ✅ Always succeeds |
| `pm_card_visa_debit` | `4000 0566 5566 5556` | Visa Debit | ✅ Always succeeds |
| `pm_card_chargeDeclined` | `4000 0000 0000 0002` | Visa | ❌ Always declined |
| `pm_card_chargeDeclinedInsufficientFunds` | `4000 0000 0000 9995` | Visa | ❌ Insufficient funds |
| `pm_card_threeDSecure2Required` | `4000 0025 0000 3155` | Visa | ✅ Requires 3D Secure |

**Usage:**
```bash
# Use pre-made payment method directly
stripe payment_intents confirm pi_YOUR_PAYMENT_INTENT_ID \
  --payment-method=pm_card_visa

# Or with cURL
curl https://api.stripe.com/v1/payment_intents/pi_YOUR_PAYMENT_INTENT_ID/confirm \
  -u sk_test_YOUR_SECRET_KEY: \
  -d payment_method=pm_card_visa
```

---

## 🔧 Testing with Stripe Dashboard

### **Method 1: Use Stripe Dashboard UI**

1. Go to [Stripe Dashboard → Payments](https://dashboard.stripe.com/test/payments)
2. Find your PaymentIntent by ID (`pi_1234567890`)
3. Click on it to see details
4. Use the "Confirm" button to manually confirm it

---

### **Method 2: Use Stripe Test Payment Methods**

Use the pre-made payment methods from the table above:

```bash
# Confirm with Visa test card (4242 4242 4242 4242)
stripe payment_intents confirm pi_1234567890 \
  --payment-method=pm_card_visa

# Confirm with Mastercard test card (5555 5555 5555 4444)
stripe payment_intents confirm pi_1234567890 \
  --payment-method=pm_card_mastercard

# Confirm with card that requires 3D Secure (4000 0025 0000 3155)
stripe payment_intents confirm pi_1234567890 \
  --payment-method=pm_card_threeDSecure2Required

# Test a declined payment (4000 0000 0000 0002)
stripe payment_intents confirm pi_1234567890 \
  --payment-method=pm_card_chargeDeclined
```

**See the full list of pre-made payment methods in the section above.**

---

## 📝 Complete Testing Example (Webhook-Based)

Here's a complete end-to-end test with webhooks:

```bash
# 0. Start webhook listener in a separate terminal
stripe listen --forward-to localhost:8080/api/webhooks/stripe

# 1. Login and get JWT token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}' \
  | jq -r '.accessToken')

# 2. Create cart
CART_ID=$(curl -X POST http://localhost:8080/api/carts \
  | jq -r '.cartId')

# 3. Add item to cart
curl -X POST http://localhost:8080/api/carts/$CART_ID/items \
  -H "Content-Type: application/json" \
  -d '{"variantId": 1}'

# 4. Create PaymentIntent (now requires addressId!)
PAYMENT_INTENT=$(curl -X POST http://localhost:8080/api/payments/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"cartId\":\"$CART_ID\",\"customerId\":1,\"addressId\":1}")

echo $PAYMENT_INTENT | jq '.'

# Extract paymentIntentId
PAYMENT_INTENT_ID=$(echo $PAYMENT_INTENT | jq -r '.paymentIntentId')

# 5. Confirm payment with Stripe CLI
# This triggers the webhook automatically!
stripe payment_intents confirm $PAYMENT_INTENT_ID \
  --payment-method=pm_card_visa

# 6. Check webhook terminal - you should see:
# payment_intent.succeeded [evt_xxx]
# And your app logs should show order creation

# 7. Verify payment status
curl -X GET http://localhost:8080/api/payments/verify/$PAYMENT_INTENT_ID \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🎯 Testing Different Webhook Events

### Test Failed Payment

```bash
# Confirm with a card that will be declined
stripe payment_intents confirm $PAYMENT_INTENT_ID \
  --payment-method=pm_card_chargeDeclined

# Check webhook terminal for: payment_intent.payment_failed
```

### Test Refund

```bash
# First, complete a successful payment
stripe payment_intents confirm $PAYMENT_INTENT_ID \
  --payment-method=pm_card_visa

# Then refund it
stripe refunds create --payment-intent=$PAYMENT_INTENT_ID

# Check webhook terminal for: charge.refunded
```

### Test Dispute

```bash
# Use the special test card that triggers disputes
stripe payment_intents confirm $PAYMENT_INTENT_ID \
  --payment-method=pm_card_createDispute

# Check webhook terminal for: charge.dispute.created
```

---

## 🐛 Troubleshooting

### **Webhook not receiving events**
- Make sure `stripe listen` is running in a separate terminal
- Check that the webhook secret in `.env` matches the one from `stripe listen`
- Restart your Spring Boot app after updating the webhook secret
- Check your app logs for webhook signature verification errors

### **"Invalid signature" error**
- The webhook secret doesn't match
- Copy the secret from `stripe listen` output (starts with `whsec_`)
- Update `STRIPE_WEBHOOK_SECRET` in your `.env` file
- Restart the application

### **Order not created after payment**
- Check the Stripe CLI terminal for webhook events
- Look for `payment_intent.succeeded` event
- Check your application logs for errors in the webhook handler
- Verify that `cartId`, `customerId`, and `addressId` are in the PaymentIntent metadata

### **"Payment already succeeded"**
- The PaymentIntent can only be confirmed once
- Create a new PaymentIntent for each test

### **"Invalid API Key"**
- Make sure you're using test keys (`sk_test_...`)
- Check your `.env` file has the correct keys

---

## 📚 Resources

- [Stripe Testing Guide](https://stripe.com/docs/testing)
- [Stripe CLI Documentation](https://stripe.com/docs/stripe-cli)
- [Stripe Webhooks Guide](https://stripe.com/docs/webhooks)
- [PaymentIntents API](https://stripe.com/docs/api/payment_intents)
- [Webhook Events Reference](https://stripe.com/docs/api/events/types)

