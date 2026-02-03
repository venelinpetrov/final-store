# Stripe Payment Testing Guide (Without Frontend)

This guide shows you how to test the Stripe payment integration using **cURL** or **Postman** without a frontend.

---

## 🔑 Prerequisites

1. **Stripe Test API Keys** - Get them from [Stripe Dashboard](https://dashboard.stripe.com/test/apikeys)
2. **Test Mode** - Make sure you're using test keys (they start with `sk_test_` and `pk_test_`)
3. **Running Application** - Your Spring Boot app should be running

---

## 📋 Testing Flow

### **Step 1: Create a Cart and Add Items**

First, create a cart and add some products to it (use existing cart endpoints).

```bash
# Create a cart
curl -X POST http://localhost:8080/api/carts

# Add item to cart (replace {cartId} and {variantId})
curl -X POST http://localhost:8080/api/carts/{cartId}/items \
  -H "Content-Type: application/json" \
  -d '{"variantId": 1}'
```

---

### **Step 2: Create PaymentIntent**

Call your backend to create a PaymentIntent:

```bash
curl -X POST http://localhost:8080/api/payments/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "cartId": "your-cart-uuid",
    "customerId": 1
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

**Save the `paymentIntentId` and `clientSecret`!**

---

### **Step 3: Confirm Payment Using Stripe CLI**

Since you don't have a frontend, use the **Stripe CLI** to simulate payment confirmation:

#### **Option A: Install Stripe CLI**

```bash
# Install Stripe CLI (macOS)
brew install stripe/stripe-cli/stripe

# Login to Stripe
stripe login

# Confirm the payment
stripe payment_intents confirm pi_1234567890 \
  --payment-method=pm_card_visa
```

#### **Option B: Use Stripe API Directly with cURL**

```bash
curl https://api.stripe.com/v1/payment_intents/pi_1234567890/confirm \
  -u sk_test_YOUR_SECRET_KEY: \
  -d payment_method=pm_card_visa
```

---

### **Step 4: Verify Payment Status**

Check if the payment succeeded:

```bash
curl -X GET http://localhost:8080/api/payments/verify/pi_1234567890 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```
succeeded
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

## 📝 Complete Testing Example

Here's a complete end-to-end test:

```bash
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

# 4. Create PaymentIntent
PAYMENT_INTENT=$(curl -X POST http://localhost:8080/api/payments/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"cartId\":\"$CART_ID\",\"customerId\":1}")

echo $PAYMENT_INTENT | jq '.'

# Extract paymentIntentId
PAYMENT_INTENT_ID=$(echo $PAYMENT_INTENT | jq -r '.paymentIntentId')

# 5. Confirm payment with Stripe CLI
stripe payment_intents confirm $PAYMENT_INTENT_ID \
  --payment-method=pm_card_visa

# 6. Verify payment status
curl -X GET http://localhost:8080/api/payments/verify/$PAYMENT_INTENT_ID \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🎯 Next Steps

After confirming the payment is successful, you would typically:

1. **Create an Order** - Call your order creation endpoint with the `paymentIntentId`
2. **Create Invoice** - Generate an invoice for the order
3. **Save Payment Record** - Use `PaymentService.createPaymentForInvoice()`

---

## 🐛 Troubleshooting

### **"Payment already succeeded"**
- The PaymentIntent can only be confirmed once
- Create a new PaymentIntent for each test

### **"Invalid API Key"**
- Make sure you're using test keys (`sk_test_...`)
- Check your `.env` file has the correct keys

### **"PaymentIntent not found"**
- Make sure you're using the correct `paymentIntentId`
- Check Stripe Dashboard to see if it was created

---

## 📚 Resources

- [Stripe Testing Guide](https://stripe.com/docs/testing)
- [Stripe CLI Documentation](https://stripe.com/docs/stripe-cli)
- [PaymentIntents API](https://stripe.com/docs/api/payment_intents)

