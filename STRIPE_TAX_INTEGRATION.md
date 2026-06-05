# Stripe Tax API Integration

## Overview

This document describes the implementation of Stripe Tax API integration for automatic tax calculation based on customer location and product classification.

## Database Changes (V24 Migration)

### Orders Table
- `tax_breakdown` (JSON) - Stores detailed tax breakdown from Stripe Tax API
- `stripe_tax_calculation_id` (VARCHAR) - Reference to Stripe Tax Calculation for audit

### Invoices Table
- `tax_breakdown` (JSON) - Copies tax breakdown from order

### Products Table
- `tax_code` (VARCHAR) - Stripe Tax Code for product classification (default: `txcd_99999999`)
  - `txcd_99999999` = General Tangible Goods
  - `txcd_10000000` = Digital Products
  - `txcd_20030000` = Clothing
  - See: https://stripe.com/docs/tax/tax-codes

## Entity Changes

### Order Entity
```java
private String taxBreakdown;              // JSON field
private String stripeTaxCalculationId;    // Stripe reference
```

### Invoice Entity
```java
private String taxBreakdown;              // JSON field
```

### Product Entity
```java
private String taxCode;                   // Stripe tax code
```

## DTOs

### TaxBreakdownDto
Represents the complete tax breakdown structure:
- `total` - Total tax amount
- `breakdown` - List of tax components
- `stripeTaxCalculationId` - Stripe calculation reference

### TaxComponentDto
Individual tax component (e.g., state tax, local tax):
- `type` - Tax type (e.g., "state_sales_tax", "local_sales_tax", "vat")
- `jurisdiction` - Jurisdiction name
- `rate` - Tax rate as decimal
- `taxableAmount` - Amount this tax applies to
- `amount` - Calculated tax amount

### TaxCalculationResult
Result from tax calculation:
- `taxAmount` - Total tax
- `stripeTaxCalculationId` - Stripe reference (null for fallback)
- `breakdown` - Detailed breakdown (null for fallback)

## Services

### StripeService
New method: `calculateTax()`
- Calls Stripe Tax API with line items and customer address
- Returns `Calculation` object with tax breakdown

### TaxService
Main tax calculation service:
- `calculateTax()` - Calculates tax using Stripe Tax API
- **Throws `ServiceUnavailableException` (503) if Stripe API fails** - No fallback
- Converts order items to Stripe tax line items using product tax codes
- Serializes tax breakdown to JSON for storage

### TaxBreakdownMapper (Utility)
Helper for deserializing JSON tax breakdown:
- `populateTaxBreakdown(OrderDto, Order)` - Populates OrderDto
- `populateTaxBreakdown(InvoiceDto, Invoice)` - Populates InvoiceDto

## API Response Changes

### OrderDto
```json
{
  "orderId": 123,
  "tax": 12.50,
  "taxBreakdown": {
    "total": 12.50,
    "breakdown": [
      {
        "type": "state_sales_tax",
        "jurisdiction": "California",
        "rate": 0.0725,
        "taxableAmount": 100.00,
        "amount": 7.25
      },
      {
        "type": "local_sales_tax",
        "jurisdiction": "Los Angeles",
        "rate": 0.0525,
        "taxableAmount": 100.00,
        "amount": 5.25
      }
    ],
    "stripeTaxCalculationId": "taxcalc_1234567890"
  },
  "stripeTaxCalculationId": "taxcalc_1234567890"
}
```

### InvoiceDto
Same structure as OrderDto for tax fields.

## Usage Example

```java
// In OrderService or OrderSummaryCalculator
TaxCalculationResult taxResult = taxService.calculateTax(
    order,
    customerAddress,
    "eur",
    shippingCostInCents
);

order.setTax(taxResult.getTaxAmount());
order.setStripeTaxCalculationId(taxResult.getStripeTaxCalculationId());
order.setTaxBreakdown(taxService.serializeTaxBreakdown(taxResult.getBreakdown()));
```

## Error Handling

**No Fallback Tax Calculation**: If Stripe Tax API fails, the order creation will fail with:
- **HTTP 503 Service Unavailable**
- Error message: "Tax calculation service is currently unavailable. Please try again later."

This ensures orders are never created with incorrect tax amounts.

## Current Limitations

1. **No postal code in CustomerAddress** - The current schema doesn't have a postal_code field
   - Tax calculation uses: country, state, city, street
   - Consider adding postal_code for more accurate tax rates

2. **Tax code per product, not per variant** - Tax code is on Product entity
   - Fine for most cases, but variants can't have different tax classifications

## Next Steps (Optional Enhancements)

1. **Add postal code to CustomerAddress**
   - More accurate tax calculations
   - Better compliance with jurisdiction requirements

2. **Update OrderSummaryCalculator to use TaxService**
   - Replace hardcoded tax calculation with Stripe Tax API
   - Add tax breakdown storage when creating orders

3. **Copy tax breakdown to Invoice**
   - When creating invoice from order, copy taxBreakdown field
   - Update PaymentService.createInvoiceForOrder()

4. **Product tax code management**
   - Add tax_code field to ProductDto
   - Allow admins to set tax codes when creating/updating products
   - Provide dropdown with common tax codes in admin UI

5. **Tax reporting**
   - Use stored tax breakdowns for reporting
   - Export tax data for filing
