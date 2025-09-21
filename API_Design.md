# API Design notes

Notes while designing the API. This is not final.

## Product

This section includes `products`, `variants`, `tags`, `categories`, `images`, `brands` and all other related tables operations. 
The code is organized in the same way - all these entities live in the `product` package.

### Products

The product is just a metadata. What customers really buy is a specific variant.

```
GET    /products                             list products
GET    /products/{id}                        get product details
POST   /products                             create a new product with at least one variant
PUT    /products/{id}                        update product metadata (name, brandId, categories, tags, description)
DELETE /products/{id}                        delete (or soft-delete) product
```

### Variants

Variants are children of products, but they’re often managed separately (like SKU, price, stock). 

```
GET    /products/{productId}/variants        list variants of a product
GET    /variants/{variantId}                 get single variant
POST   /products/{productId}/variants        add a new variant to a product
PUT    /variants/{variantId}                 update variant (sku, unitPrice, stock, etc.)
DELETE /variants/{variantId}                 delete variant
```
Example `VariantUpdateDto`

```json
{
  "sku": "IPH15PRO-BLK-128",
  "unitPrice": 1099.99
}
```

**Important:** The stock quantity update should happen through the `inventory_movements` table only. This makes the system resilient to race conditions. Here is why. If I allow to set the stock quantity directly with the variant endpoint, one user can set 20 and another can set 25, immediately after, overriding the previous value. That is why `inventory_movements` and `inventory_levels` are for. I'll keep all writes going through `inventory_movements` and treat `inventory_levels` as read-only.

Moreover, quantity should not be assigned to variant, because if I want to support multiple warehouses, the quantity will be different for each.

**Note**: Variants are important enough to be separated in a top level resource, because they are references by the orders.

### Images

Images are reusable across products and variants, so treat them as their own resource. Then create assignment endpoints.

#### Image resource
```
GET    /images                                    list images (with search/filter)
POST   /images                                    upload or register a new image
GET    /images/{imageId}                          fetch image metadata
DELETE /images/{imageId}                          delete image (if unused)
```

#### Product - image relations

````
POST   /products/{productId}/images               assign image(s) to product
DELETE /products/{productId}/images/{imageId}     unassign image from product
````

#### Variant - image relation

```
POST   /variants/{variantId}/images               assign image(s) to variant
DELETE /variants/{variantId}/images/{imageId}     unassign image from variant
```

Example assign image to product

```json
{
  "imageId": 123,
  "isPrimary": true
}
```

### Brands

### Tags

### Categories