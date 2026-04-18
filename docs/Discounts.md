# Discounts

This document explains how discounts are designed and why

We need a flexible system that allows to have different types of discounts and application rules.

## Example of common discounts

These are some common discounts in e-commerce that we want to support. The list can grow in time.

- 10% off for orders above €100
- Free shipping for orders above €200
- Free shipping for VIP users
- 5% off for product variant ABC
- Buy N get X%, e.g. Buy 3 of product variant ABC and get 10% off
- This month, product variant ABC is discounted by X%
- Get 10% off of your order with coupon SUMMER10

## Domain definition

From the examples above, it becomes clear that discounts can be modeled with 2 components

1. What gets discounted. This is the **discount rule**:
	- 10% off of order
	- 5% off of product
	- free shipping

2. How it gets applied. This can be called a **trigger**, or **condition**
	- applies for order above €100
	- applies for user group (e.g. VIP)
	- applies for specific product variant ABC
	- applies for N of type ABC
	- applies for specific product variant ABC, for this month
	- applies with code SUMMER10

**Note**: copuon codes are not a different type of discount. They are just a special case of trigger. Unlike all other discounts, which are applied automatically by the app, when a condition is met, the coupon is applied manually by the user.

### Discount scope

A discount can be applied to a **product variant**, an **order**, **shipping** or potentially something else.

### Discount type

A discount type can be a **% off**, **fixed**, **buy_x_get_y**

### Discount condition type

Unlike discount types, which are normally around 3-5, condition types could be many

- min order amount
- variant related: applies for specific variants
- category related: applies for specific categories
- brand related: applies for specific brands
- min quantity
- customer group
- customer id
- time: valid from / valid until

**Note**: One discount can have multiple conditions. For example "variant_id: 5" + "valid until: end of the mont".

### Discount condition value

This is just the specific value of the condtion. For example if the condition is "variant", the value could be 5.

### Tracking

For accounting purposes it's good to have some way to tell what discounts were applied historically.

### Stacking discounts, exclusivity, priority

TBD

### Edge cases to consider

- What happens when discount expires while cart is active?
- What happens if e.g. variant price change after discount calculation?
- Rounding? Half up? Half down?
- Currency is not taken into account for now.

## DB schema design

From the Domain definition above it appears that we would need at least 4 new tables:

- `discounts` table
- `discount_conditions` table
- `coupons` table
- `applied_discounts` table (optional, for tracking)

This design needs to take into account, practicality, performance and ease of use, not just relational purity.

```sql
CREATE TABLE discounts (
    discount_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),

    discount_type ENUM('PERCENTAGE', 'FIXED', 'BUY_X_GET_Y'),

    scope ENUM('VARIANT', 'ORDER', 'SHIPPING'),

    value DECIMAL(10,2),

    min_order_amount DECIMAL(10,2),
    max_discount_amount DECIMAL(10,2),

    valid_from DATETIME,
    valid_until DATETIME,

    is_active BOOLEAN DEFAULT TRUE
);

```

**Note**: Notice how `min_order_amount`, `max_discount_amount`, `valid_from`, `valid_until` were modelled as discount conditions in the domain design, but in the DB design are part of the `discounts`. This will make it easier to query which discounts apply for an order, without joins. For example:

```sql
WHERE now BETWEEN valid_from AND valid_until
AND subtotal >= min_order_amount
```

If we put everything in `discount_conditions` it makes the logic more complex, because we need to do more joins.

Some discounts will have `NULL` for these columns. For example a VIP user group discount is a type of perpetual discount and will have `valid_from` / `valid_until` as `NULL`. This is interpretted as "infinite" / "no time limit". And that's fine as long as it simplifies things, which it does.


```sql
CREATE TABLE discount_conditions (
    condition_id INT AUTO_INCREMENT PRIMARY KEY,
    discount_id INT,

    condition_type ENUM(
        'MIN_QUANTITY',
        'CUSTOMER_GROUP',
        'VARIANT',
        'CATEGORY',
        ...
    ),

    condition_value DECIMAL(10, 2),

    FOREIGN KEY (discount_id) REFERENCES discounts(discount_id)
);
```

```sql
CREATE TABLE coupons (
    coupon_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE,
    discount_id INT,
    usage_limit INT,
    times_used INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,

    FOREIGN KEY (discount_id) REFERENCES discounts(discount_id)
);
```

```sql
CREATE TABLE applied_discounts (
    applied_discount_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    order_item_id INT NULL,

    discount_id INT,
    coupon_id INT NULL,

    discount_amount DECIMAL(10,2),

    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);
```

## Application layer

- How discounts are being applied on app level: TBD
- How disocunts affect orders, variants, invoices etc.: TBD

### API design

TBD