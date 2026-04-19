package com.vpe.finalstore.seeder.services;

import com.github.javafaker.Faker;
import com.vpe.finalstore.customer.entities.AddressType;
import com.vpe.finalstore.customer.entities.Customer;
import com.vpe.finalstore.customer.entities.CustomerAddress;
import com.vpe.finalstore.customer.repositories.AddressTypeRepository;
import com.vpe.finalstore.customer.repositories.CustomerRepository;
import com.vpe.finalstore.inventory.entities.InventoryLevel;
import com.vpe.finalstore.inventory.enums.MovementType;
import com.vpe.finalstore.inventory.repositories.InventoryLevelRepository;
import com.vpe.finalstore.order.entities.Order;
import com.vpe.finalstore.order.entities.OrderStatus;
import com.vpe.finalstore.order.enums.OrderStatusType;
import com.vpe.finalstore.order.repositories.OrderStatusRepository;
import com.vpe.finalstore.payment.entities.PaymentMethod;
import com.vpe.finalstore.payment.entities.PaymentStatus;
import com.vpe.finalstore.payment.enums.PaymentStatusType;
import com.vpe.finalstore.payment.repositories.PaymentMethodRepository;
import com.vpe.finalstore.payment.repositories.PaymentStatusRepository;
import com.vpe.finalstore.product.entities.*;
import com.vpe.finalstore.product.repositories.*;
import com.vpe.finalstore.shipment.entities.Carrier;
import com.vpe.finalstore.shipment.enums.ShipmentStatusType;
import com.vpe.finalstore.shipment.repositories.CarrierRepository;
import com.vpe.finalstore.users.entities.Role;
import com.vpe.finalstore.users.entities.User;
import com.vpe.finalstore.users.enums.RoleEnum;
import com.vpe.finalstore.users.repositories.RoleRepository;
import com.vpe.finalstore.users.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseSeederService {

    private final EntityManager entityManager;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AddressTypeRepository addressTypeRepository;
    private final ProductCategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantOptionValueRepository variantOptionValueRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final InventoryLevelRepository inventoryLevelRepository;
    private final CarrierRepository carrierRepository;
    private final PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    @Transactional
    public void cleanDatabase() {
        log.info("Cleaning database...");

        // Delete in correct order to respect foreign key constraints
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        // Transactional data (orders, payments, invoices)
        entityManager.createNativeQuery("TRUNCATE TABLE payments").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE invoices").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE order_items").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE orders").executeUpdate();

        // Inventory data
        entityManager.createNativeQuery("TRUNCATE TABLE inventory_movements").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE inventory_levels").executeUpdate();

        // Product variant related
        entityManager.createNativeQuery("TRUNCATE TABLE product_variant_image_assignments").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE variant_option_assignments").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE product_variants").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE variant_option_values").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE variant_options").executeUpdate();

        // Product related
        entityManager.createNativeQuery("TRUNCATE TABLE product_tags").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE product_to_categories").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE product_image_assignments").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE products").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE product_images").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE tags").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE brands").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE product_categories").executeUpdate();

        // Customer and user data
        entityManager.createNativeQuery("TRUNCATE TABLE cart_items").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE carts").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE customer_payment_methods").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE customer_addresses").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE customers").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE user_roles").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE users").executeUpdate();

        // Reference data
        entityManager.createNativeQuery("TRUNCATE TABLE address_types").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE roles").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE order_statuses").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE payment_methods").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE payment_statuses").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE carriers").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE shipment_statuses").executeUpdate();

        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();

        log.info("Database cleaned successfully");
    }

    @Transactional
    public void seedRoles() {
        log.info("Seeding roles...");

        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleRepository.getRoleByName(roleEnum).isEmpty()) {
                Role role = new Role();
                role.setName(roleEnum);
                role.setDescription(roleEnum.name() + " role");
                roleRepository.save(role);
            }
        }

        log.info("Roles seeded successfully");
    }

    @Transactional
    public void seedOrderStatuses() {
        log.info("Seeding order statuses...");

        List<OrderStatus> statuses = new ArrayList<>();
        for (OrderStatusType statusType : OrderStatusType.values()) {
            if (orderStatusRepository.findByName(statusType).isEmpty()) {
                OrderStatus status = new OrderStatus();
                status.setName(statusType);
                statuses.add(status);
            }
        }

        orderStatusRepository.saveAll(statuses);
        log.info("Order statuses seeded successfully");
    }

    @Transactional
    public void seedPaymentMethods() {
        log.info("Seeding payment methods...");

        entityManager.createNativeQuery(
            "INSERT IGNORE INTO payment_methods (type, name) VALUES " +
            "('card', 'Credit Card'), " +
            "('card', 'Debit Card'), " +
            "('digital_wallet', 'PayPal'), " +
            "('digital_wallet', 'Apple Pay'), " +
            "('digital_wallet', 'Google Pay')"
        ).executeUpdate();

        log.info("Payment methods seeded successfully");
    }

    @Transactional
    public void seedPaymentStatuses() {
        log.info("Seeding payment statuses...");

        for (PaymentStatusType statusType : PaymentStatusType.values()) {
            entityManager.createNativeQuery(
                "INSERT IGNORE INTO payment_statuses (name) VALUES (?)"
            ).setParameter(1, statusType.name()).executeUpdate();
        }

        log.info("Payment statuses seeded successfully");
    }

    @Transactional
    public void seedCarriers() {
        log.info("Seeding carriers...");

        List<Carrier> carriers = new ArrayList<>();

        // DHL
        Carrier dhl = new Carrier();
        dhl.setName("DHL Express");
        dhl.setCode("DHL");
        dhl.setTrackingUrlTemplate("https://www.dhl.com/en/express/tracking.html?AWB={trackingNumber}");
        dhl.setApiEndpoint("https://api-eu.dhl.com/track/shipments");
        carriers.add(dhl);

        // FedEx
        Carrier fedex = new Carrier();
        fedex.setName("FedEx");
        fedex.setCode("FEDEX");
        fedex.setTrackingUrlTemplate("https://www.fedex.com/fedextrack/?tracknumbers={trackingNumber}");
        fedex.setApiEndpoint("https://apis.fedex.com/track/v1/trackingnumbers");
        carriers.add(fedex);

        // UPS
        Carrier ups = new Carrier();
        ups.setName("UPS");
        ups.setCode("UPS");
        ups.setTrackingUrlTemplate("https://www.ups.com/track?trackingNumber={trackingNumber}");
        ups.setApiEndpoint("https://onlinetools.ups.com/api/track/v1/details/{trackingNumber}");
        carriers.add(ups);

        // USPS
        Carrier usps = new Carrier();
        usps.setName("USPS");
        usps.setCode("USPS");
        usps.setTrackingUrlTemplate("https://tools.usps.com/go/TrackConfirmAction?tLabels={trackingNumber}");
        usps.setApiEndpoint("https://secure.shippingapis.com/ShippingAPI.dll");
        carriers.add(usps);

        // Royal Mail
        Carrier royalMail = new Carrier();
        royalMail.setName("Royal Mail");
        royalMail.setCode("ROYAL_MAIL");
        royalMail.setTrackingUrlTemplate("https://www.royalmail.com/track-your-item#/tracking-results/{trackingNumber}");
        royalMail.setApiEndpoint("https://api.royalmail.net/mailpieces/v2/summary");
        carriers.add(royalMail);

        carrierRepository.saveAll(carriers);
        log.info("Seeded {} carriers successfully", carriers.size());
    }

    @Transactional
    public void seedShipmentStatuses() {
        log.info("Seeding shipment statuses...");

        for (ShipmentStatusType statusType : ShipmentStatusType.values()) {
            entityManager.createNativeQuery(
                "INSERT IGNORE INTO shipment_statuses (name) VALUES (?)"
            ).setParameter(1, statusType.name()).executeUpdate();
        }

        log.info("Shipment statuses seeded successfully");
    }

    @Transactional
    public void seedUsers(int count) {
        log.info("Seeding {} users...", count);

        Role userRole = roleRepository.getRoleByName(RoleEnum.USER)
            .orElseThrow(() -> new RuntimeException("USER role not found"));

        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setEmail(faker.internet().emailAddress());
            user.setPasswordHash(passwordEncoder.encode("password123"));
            user.setIsActive(true);
            user.getRoles().add(userRole);
            users.add(user);
        }

        userRepository.saveAll(users);
        log.info("Seeded {} users successfully", count);
    }

    @Transactional
    public void seedCustomers() {
        log.info("Seeding customers...");

        List<User> users = userRepository.findAll();
        List<Customer> customers = new ArrayList<>();

        for (User user : users) {
            Customer customer = new Customer();
            customer.setUser(user);
            customer.setName(faker.name().fullName());
            customer.setPhone(faker.phoneNumber().phoneNumber());
            customer.setDateOfBirth(
                LocalDate.now().minusYears(random.nextInt(50) + 18)
            );
            customers.add(customer);
        }

        customerRepository.saveAll(customers);
        log.info("Seeded {} customers successfully", customers.size());
    }

    @Transactional
    public void seedAddressTypes() {
        log.info("Seeding address types...");

        String[] addressTypes = {"Home", "Work", "Billing", "Shipping", "Other"};
        List<AddressType> addressTypesList = new ArrayList<>();
        for (String typeName : addressTypes) {
            AddressType addressType = new AddressType();
            addressType.setTypeName(typeName);
            addressTypesList.add(addressType);
        }
        addressTypeRepository.saveAll(addressTypesList);

        log.info("Seeded {} address types successfully", addressTypes.length);
    }

    @Transactional
    public void seedCustomerAddresses() {
        log.info("Seeding customer addresses...");

        List<Customer> customers = customerRepository.findAll();
        List<AddressType> addressTypes = addressTypeRepository.findAll();

        if (addressTypes.isEmpty()) {
            log.warn("No address types found. Seeding address types first...");
            seedAddressTypes();
            addressTypes = addressTypeRepository.findAll();
        }

        for (Customer customer : customers) {
            int addressCount = random.nextInt(3) + 1; // 1-3 addresses per customer

            for (int i = 0; i < addressCount; i++) {
                CustomerAddress address = new CustomerAddress();
                address.setCustomer(customer);
                address.setCountry(faker.address().country());
                address.setState(faker.address().state());
                address.setCity(faker.address().city());
                address.setStreet(faker.address().streetAddress());
                address.setFloor(String.valueOf(random.nextInt(20) + 1));
                address.setApartmentNo(String.valueOf(random.nextInt(100) + 1));
                address.setIsDefault(i == 0); // First address is default
                var randomType = addressTypes.get(random.nextInt(addressTypes.size()));
                address.setAddressType(randomType);

                customer.getAddresses().add(address);
            }
        }

        customerRepository.saveAll(customers);
        log.info("Seeded customer addresses successfully");
    }

    @Transactional
    public void seedProductCategories() {
        log.info("Seeding product categories...");

        Map<String, List<String>> categoryHierarchy = Map.of(
            "Electronics", List.of("Smartphones", "Laptops", "Tablets", "Headphones", "Cameras"),
            "Clothing", List.of("Men's Clothing", "Women's Clothing", "Kids' Clothing", "Shoes", "Accessories"),
            "Home & Garden", List.of("Furniture", "Kitchen", "Bedding", "Decor", "Tools"),
            "Sports", List.of("Fitness", "Outdoor", "Team Sports", "Water Sports", "Winter Sports"),
            "Books", List.of("Fiction", "Non-Fiction", "Educational", "Comics", "Magazines")
        );

        for (Map.Entry<String, List<String>> entry : categoryHierarchy.entrySet()) {
            var parent = new ProductCategory();
            parent.setName(entry.getKey());
            categoryRepository.save(parent);

            for (String childName : entry.getValue()) {
                var child = new ProductCategory();
                child.setName(childName);
                child.setParentCategory(parent);
                categoryRepository.save(child);
            }
        }

        log.info("Product categories seeded successfully");
    }

    @Transactional
    public void seedBrands() {
        log.info("Seeding brands...");

        String[] brandNames = {
            "Apple", "Samsung", "Sony", "Nike", "Adidas", "Puma",
            "Dell", "HP", "Lenovo", "Canon", "Nikon", "LG",
            "Microsoft", "Google", "Amazon", "IKEA", "Zara", "H&M"
        };

        List<Brand> brands = new ArrayList<>();
        for (String name : brandNames) {
            Brand brand = new Brand();
            brand.setName(name);
            brand.setLogoUrl("https://logo.clearbit.com/" + name.toLowerCase() + ".com");
            brands.add(brand);
        }

        brandRepository.saveAll(brands);
        log.info("Seeded {} brands successfully", brands.size());
    }

    @Transactional
    public void seedTags() {
        log.info("Seeding tags...");

        String[] tagNames = {
            "New", "Sale", "Popular", "Trending", "Limited Edition",
            "Eco-Friendly", "Premium", "Budget", "Best Seller", "Featured"
        };

        List<Tag> tags = new ArrayList<>();
        for (String name : tagNames) {
            Tag tag = new Tag(name);
            tags.add(tag);
        }

        tagRepository.saveAll(tags);
        log.info("Seeded {} tags successfully", tags.size());
    }

    @Transactional
    public void seedProducts(int count) {
        log.info("Seeding {} products...", count);

        List<Brand> brands = brandRepository.findAll();
        List<ProductCategory> categories = categoryRepository.findAll();
        List<Tag> tags = tagRepository.findAll();

        if (brands.isEmpty() || categories.isEmpty()) {
            log.warn("Cannot seed products: brands or categories are empty");
            return;
        }

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Product product = Product.builder()
                .name(faker.commerce().productName())
                .description(faker.lorem().sentence(15))
                .brand(brands.get(random.nextInt(brands.size())))
                .isArchived(false)
                .build();

            // Add 1-3 random categories
            int categoryCount = random.nextInt(3) + 1;
            Set<ProductCategory> productCategories = new HashSet<>();
            for (int j = 0; j < categoryCount; j++) {
                productCategories.add(categories.get(random.nextInt(categories.size())));
            }
            product.setCategories(productCategories);

            // Add 1-4 random tags
            int tagCount = random.nextInt(4) + 1;
            Set<Tag> productTags = new HashSet<>();
            for (int j = 0; j < tagCount; j++) {
                productTags.add(tags.get(random.nextInt(tags.size())));
            }
            product.setTags(productTags);

            products.add(product);
        }

        productRepository.saveAll(products);
        log.info("Seeded {} products successfully", count);
    }

    @Transactional
    public void seedProductVariants() {
        log.info("Seeding product variants...");

        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            int variantCount = random.nextInt(3) + 1; // 1-3 variants per product

            for (int i = 0; i < variantCount; i++) {
                var variant = new ProductVariant();
                variant.setProduct(product);
                variant.setSku("SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                variant.setUnitPrice(new BigDecimal(faker.commerce().price(5.00, 10000.00)));
                variant.setIsArchived(false);

                variantRepository.save(variant);
            }
        }

        log.info("Product variants seeded successfully");
    }

    @Transactional
    public void seedVariantOptions() {
        log.info("Seeding variant options...");

        // Use native SQL since ProductVariantOption doesn't have @GeneratedValue
        entityManager.createNativeQuery(
            "INSERT IGNORE INTO variant_options (option_id, name) VALUES " +
            "(1, 'Size'), " +
            "(2, 'Color'), " +
            "(3, 'Material'), " +
            "(4, 'Style')"
        ).executeUpdate();

        log.info("Variant options seeded successfully");
    }

    @Transactional
    public void seedVariantOptionValues() {
        log.info("Seeding variant option values...");

        // Size values (option_id = 1)
        entityManager.createNativeQuery(
            "INSERT IGNORE INTO variant_option_values (option_id, value) VALUES " +
            "(1, 'S'), (1, 'M'), (1, 'L'), (1, 'XL'), (1, 'XXL')"
        ).executeUpdate();

        // Color values (option_id = 2)
        entityManager.createNativeQuery(
            "INSERT IGNORE INTO variant_option_values (option_id, value) VALUES " +
            "(2, 'Red'), (2, 'Blue'), (2, 'Green'), (2, 'Black'), (2, 'White'), (2, 'Yellow')"
        ).executeUpdate();

        // Material values (option_id = 3)
        entityManager.createNativeQuery(
            "INSERT IGNORE INTO variant_option_values (option_id, value) VALUES " +
            "(3, 'Cotton'), (3, 'Polyester'), (3, 'Leather'), (3, 'Wool'), (3, 'Silk')"
        ).executeUpdate();

        // Style values (option_id = 4)
        entityManager.createNativeQuery(
            "INSERT IGNORE INTO variant_option_values (option_id, value) VALUES " +
            "(4, 'Casual'), (4, 'Formal'), (4, 'Sport'), (4, 'Vintage'), (4, 'Modern')"
        ).executeUpdate();

        log.info("Variant option values seeded successfully");
    }

    @Transactional
    public void seedVariantOptionAssignments() {
        log.info("Seeding variant option assignments...");

        List<ProductVariant> variants = variantRepository.findAll();
        List<ProductVariantOptionValue> allValues = variantOptionValueRepository.findAll();

        if (allValues.isEmpty()) {
            log.warn("No variant option values found. Skipping variant option assignments.");
            return;
        }

        for (ProductVariant variant : variants) {
            // Assign 1-3 random option values to each variant
            int assignmentCount = random.nextInt(3) + 1;
            Set<Integer> usedOptionIds = new HashSet<>();

            for (int i = 0; i < assignmentCount && i < allValues.size(); i++) {
                var value = allValues.get(random.nextInt(allValues.size()));

                if (usedOptionIds.contains(value.getOption().getOptionId())) {
                    continue;
                }
                usedOptionIds.add(value.getOption().getOptionId());

                var assignment = new ProductVariantOptionAssignment(variant, value);
                entityManager.persist(assignment);
            }
        }

        entityManager.flush();
        log.info("Variant option assignments seeded successfully");
    }

    @Transactional
    public void seedProductImages() {
        log.info("Seeding product images...");

        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            // Create 1-3 images per product
            int imageCount = random.nextInt(3) + 1;
            List<Integer> productImageIds = new ArrayList<>();

            for (int i = 0; i < imageCount; i++) {
                var imageUrl = "https://picsum.photos/seed/" + UUID.randomUUID() + "/400/400";

                // Use native SQL to insert into product_images and product_image_assignments
                entityManager.createNativeQuery(
                    "INSERT INTO product_images (link, alt_text) VALUES (?, ?)"
                )
                .setParameter(1, imageUrl)
                .setParameter(2, product.getName() + " - Image " + (i + 1))
                .executeUpdate();

                // Get the last inserted image_id
                var imageId = ((Number) entityManager.createNativeQuery(
                    "SELECT LAST_INSERT_ID()"
                ).getSingleResult()).intValue();

                productImageIds.add(imageId);

                // Create product image assignment
                entityManager.createNativeQuery(
                    "INSERT INTO product_image_assignments (product_id, image_id, is_primary) VALUES (?, ?, ?)"
                )
                .setParameter(1, product.getProductId())
                .setParameter(2, imageId)
                .setParameter(3, i == 0 ? 1 : 0) // First image is primary
                .executeUpdate();
            }

            // Assign images to product variants
            List<ProductVariant> variants = entityManager
                .createQuery("SELECT pv FROM ProductVariant pv WHERE pv.product = :product", ProductVariant.class)
                .setParameter("product", product)
                .getResultList();

            for (ProductVariant variant : variants) {
                // Assign 1-2 random images from the product's images to each variant
                int variantImageCount = Math.min(random.nextInt(2) + 1, productImageIds.size());
                Set<Integer> usedImageIds = new HashSet<>();

                for (int i = 0; i < variantImageCount; i++) {
                    var imageId = productImageIds.get(random.nextInt(productImageIds.size()));

                    if (usedImageIds.contains(imageId)) {
                        continue;
                    }
                    usedImageIds.add(imageId);

                    entityManager.createNativeQuery(
                        "INSERT INTO product_variant_image_assignments (variant_id, image_id, is_primary) VALUES (?, ?, ?)"
                    )
                    .setParameter(1, variant.getVariantId())
                    .setParameter(2, imageId)
                    .setParameter(3, i == 0 ? 1 : 0) // First image is primary for variant
                    .executeUpdate();
                }
            }
        }

        log.info("Product images seeded successfully");
    }

    @Transactional
    public void seedInventoryLevels() {
        log.info("Seeding inventory levels...");

        List<ProductVariant> variants = variantRepository.findAll();

        // Create initial inventory level records with 0 stock
        // The stock will be built up by inventory movements
        for (ProductVariant variant : variants) {
            InventoryLevel inventoryLevel = new InventoryLevel();
            inventoryLevel.setVariant(variant);
            inventoryLevel.setQuantityInStock(0);
            inventoryLevelRepository.save(inventoryLevel);
        }

        log.info("Seeded {} inventory levels successfully", variants.size());
    }

    @Transactional
    public void seedInventoryMovements() {
        log.info("Seeding inventory movements...");

        List<ProductVariant> variants = variantRepository.findAll();

        for (ProductVariant variant : variants) {
            int currentStock = 0;
            List<InventoryMovementData> movements = new ArrayList<>();

            // Create 3-6 IN movements first to build up stock (oldest to newest)
            int inMovementCount = random.nextInt(4) + 3;
            for (int i = 0; i < inMovementCount; i++) {
                int daysAgo = 180 - (i * 30); // Spread over 180 days, oldest first
                var movementDate = LocalDateTime.now().minusDays(daysAgo)
                    .minusHours(random.nextInt(24))
                    .minusMinutes(random.nextInt(60));

                int quantity = random.nextInt(100) + 50; // 50-149 items
                currentStock += quantity;

                movements.add(new InventoryMovementData(
                    movementDate, MovementType.IN, quantity, "Stock replenishment"
                ));
            }

            // Create 2-4 OUT movements (sales/usage)
            int outMovementCount = random.nextInt(3) + 2;
            for (int i = 0; i < outMovementCount; i++) {
                int daysAgo = random.nextInt(90); // Within last 90 days
                LocalDateTime movementDate = LocalDateTime.now().minusDays(daysAgo)
                    .minusHours(random.nextInt(24))
                    .minusMinutes(random.nextInt(60));

                // OUT quantity should not exceed current stock
                int maxOut = Math.min(currentStock / 2, 50); // Max 50 or half of stock
                if (maxOut > 0) {
                    int quantity = random.nextInt(maxOut) + 1;
                    currentStock -= quantity;

                    movements.add(new InventoryMovementData(
                        movementDate, MovementType.OUT, quantity, "Sales/Usage"
                    ));
                }
            }

            // Note: ADJUSTMENT movements are ignored by the trigger (set to 0)
            // So we skip them to avoid confusion

            // Sort movements by date (oldest first) to maintain realistic timeline
            movements.sort((a, b) -> a.date.compareTo(b.date));

            // Insert all movements in chronological order
            for (InventoryMovementData movement : movements) {
                entityManager.createNativeQuery(
                    "INSERT INTO inventory_movements (variant_id, movement_type, quantity, reason, created_at) " +
                    "VALUES (?, ?, ?, ?, ?)"
                )
                .setParameter(1, variant.getVariantId())
                .setParameter(2, movement.type.name())
                .setParameter(3, movement.quantity)
                .setParameter(4, movement.reason)
                .setParameter(5, movement.date)
                .executeUpdate();
            }
        }

        log.info("Inventory movements seeded successfully");
    }

    // Helper class to hold inventory movement data
    private static class InventoryMovementData {
        LocalDateTime date;
        MovementType type;
        int quantity;
        String reason;

        InventoryMovementData(LocalDateTime date, MovementType type, int quantity, String reason) {
            this.date = date;
            this.type = type;
            this.quantity = quantity;
            this.reason = reason;
        }
    }

    @Transactional
    public void seedInvoicesAndPayments() {
        log.info("Seeding invoices and payments...");

        List<PaymentMethod> paymentMethods = paymentMethodRepository.findAll();
        List<PaymentStatus> paymentStatuses = paymentStatusRepository.findAll();

        if (paymentMethods.isEmpty() || paymentStatuses.isEmpty()) {
            log.warn("Cannot seed invoices: required data is missing");
            return;
        }

        var issuedStatusId = ((Number) entityManager.createNativeQuery(
            "SELECT status_id FROM invoice_statuses WHERE name = 'ISSUED'"
        ).getSingleResult()).intValue();

        var paidStatusId = ((Number) entityManager.createNativeQuery(
            "SELECT status_id FROM invoice_statuses WHERE name = 'PAID'"
        ).getSingleResult()).intValue();

        List<Order> orders = entityManager
            .createQuery("SELECT o FROM Order o", Order.class)
            .getResultList();

        for (Order order : orders) {
            var invoiceDate = order.getCreatedAt();

            var dueDate = invoiceDate.plusDays(random.nextInt(16) + 15);

            boolean isPaid = random.nextDouble() < 0.8;
            int invoiceStatusId = isPaid ? paidStatusId : issuedStatusId;

            entityManager.createNativeQuery(
                "INSERT INTO invoices (order_id, customer_id, status_id, invoice_total, tax, discount_amount, payment_total, invoice_date, due_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            )
            .setParameter(1, order.getOrderId())
            .setParameter(2, order.getCustomer().getCustomerId())
            .setParameter(3, invoiceStatusId)
            .setParameter(4, order.getTotal())
            .setParameter(5, order.getTax())
            .setParameter(6, order.getDiscountAmount())
            .setParameter(7, BigDecimal.ZERO)
            .setParameter(8, invoiceDate)
            .setParameter(9, dueDate)
            .executeUpdate();

            var invoiceId = ((Number) entityManager.createNativeQuery(
                "SELECT LAST_INSERT_ID()"
            ).getSingleResult()).intValue();

            if (isPaid) {
                // Payment date is between invoice date and due date
                long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(invoiceDate, dueDate);
                int paymentDaysAfterInvoice = random.nextInt((int) daysBetween + 1);
                var paymentDate = invoiceDate.plusDays(paymentDaysAfterInvoice)
                    .plusHours(random.nextInt(24))
                    .plusMinutes(random.nextInt(60));

                var method = paymentMethods.get(random.nextInt(paymentMethods.size()));
                var status = paymentStatuses.get(random.nextInt(paymentStatuses.size()));

                entityManager.createNativeQuery(
                    "INSERT INTO payments (invoice_id, amount, payment_date, method_id, status_id) " +
                    "VALUES (?, ?, ?, ?, ?)"
                )
                .setParameter(1, invoiceId)
                .setParameter(2, order.getTotal())
                .setParameter(3, paymentDate)
                .setParameter(4, method.getMethodId())
                .setParameter(5, status.getStatusId())
                .executeUpdate();
            }
        }

        log.info("Invoices and payments seeded successfully");
    }

    @Transactional
    public void seedOrders(int count) {
        log.info("Seeding {} orders...", count);

        List<Customer> customers = customerRepository.findAll();
        List<ProductVariant> variants = variantRepository.findAll();
        List<OrderStatus> statuses = entityManager
            .createQuery("SELECT os FROM OrderStatus os", OrderStatus.class)
            .getResultList();

        if (customers.isEmpty() || variants.isEmpty() || statuses.isEmpty()) {
            log.warn("Cannot seed orders: required data is missing");
            return;
        }

        for (int i = 0; i < count; i++) {
            var customer = customers.get(random.nextInt(customers.size()));

            if (customer.getAddresses().isEmpty()) {
                continue;
            }

            var address = customer.getAddresses().iterator().next();
            var status = statuses.get(random.nextInt(statuses.size()));

            // Generate random date within the last 365 days
            var daysAgo = random.nextInt(365);
            var orderDate = LocalDateTime.now().minusDays(daysAgo)
                .minusHours(random.nextInt(24))
                .minusMinutes(random.nextInt(60));

            // Create 1-5 order items
            var itemCount = random.nextInt(5) + 1;
            var subtotal = BigDecimal.ZERO;
            Set<Integer> usedVariantIds = new HashSet<>();
            List<OrderItemData> orderItemsData = new ArrayList<>();

            for (int j = 0; j < itemCount; j++) {
                var variant = variants.get(random.nextInt(variants.size()));

                // Skip if we already have this variant in the order (composite key constraint)
                if (usedVariantIds.contains(variant.getVariantId())) {
                    continue;
                }
                usedVariantIds.add(variant.getVariantId());

                var quantity = random.nextInt(5) + 1;
                var itemTotal = variant.getUnitPrice().multiply(BigDecimal.valueOf(quantity));
                subtotal = subtotal.add(itemTotal);

                orderItemsData.add(new OrderItemData(
                    variant.getVariantId(),
                    quantity,
                    variant.getUnitPrice(),
                    variant.getProduct().getName(),
                    variant.getSku(),
                    variant.getProduct().getBrand().getName()
                ));
            }

            var tax = subtotal.multiply(BigDecimal.valueOf(0.1)).setScale(2, RoundingMode.HALF_UP);
            var shippingCost = BigDecimal.valueOf(random.nextDouble() * 20 + 5).setScale(2, RoundingMode.HALF_UP);
            var total = subtotal.add(tax).add(shippingCost);

            entityManager.createNativeQuery(
                "INSERT INTO orders (created_at, updated_at, status_id, customer_id, address_id, subtotal, tax, shipping_cost, total) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            )
            .setParameter(1, orderDate)
            .setParameter(2, orderDate)
            .setParameter(3, status.getStatusId())
            .setParameter(4, customer.getCustomerId())
            .setParameter(5, address.getAddressId())
            .setParameter(6, subtotal)
            .setParameter(7, tax)
            .setParameter(8, shippingCost)
            .setParameter(9, total)
            .executeUpdate();

            var orderId = ((Number) entityManager.createNativeQuery(
                "SELECT LAST_INSERT_ID()"
            ).getSingleResult()).intValue();

            for (OrderItemData itemData : orderItemsData) {
                entityManager.createNativeQuery(
                    "INSERT INTO order_items (order_id, variant_id, quantity, unit_price, product_name, sku, brand_name) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)"
                )
                .setParameter(1, orderId)
                .setParameter(2, itemData.variantId)
                .setParameter(3, itemData.quantity)
                .setParameter(4, itemData.unitPrice)
                .setParameter(5, itemData.productName)
                .setParameter(6, itemData.sku)
                .setParameter(7, itemData.brandName)
                .executeUpdate();
            }
        }

        log.info("Seeded {} orders successfully", count);
    }

    // Helper class to hold order item data
    private static class OrderItemData {
        Integer variantId;
        Integer quantity;
        BigDecimal unitPrice;
        String productName;
        String sku;
        String brandName;

        OrderItemData(
            Integer variantId,
            Integer quantity,
            BigDecimal unitPrice,
            String productName,
            String sku,
            String brandName
        ) {
            this.variantId = variantId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.productName = productName;
            this.sku = sku;
            this.brandName = brandName;
        }
    }

    @Transactional
    public void runAllSeeds(boolean cleanStart, int userCount, int productCount, int orderCount) {
        log.info("Starting database seeding...");
        log.info("Parameters: cleanStart={}, users={}, products={}, orders={}",
            cleanStart, userCount, productCount, orderCount);

        if (cleanStart) {
            cleanDatabase();
        }

        // Basic reference data
        seedRoles();
        seedOrderStatuses();
        seedPaymentMethods();
        seedPaymentStatuses();
        seedAddressTypes();
        seedCarriers();
        seedShipmentStatuses();

        // User + customer data
        seedUsers(userCount);
        seedCustomers();
        seedCustomerAddresses();

        // Product catalog
        seedProductCategories();
        seedBrands();
        seedTags();
        seedProducts(productCount);
        seedProductVariants();
        seedVariantOptions();
        seedVariantOptionValues();
        seedVariantOptionAssignments();
        seedProductImages();
        seedInventoryLevels();
        seedInventoryMovements();

        // Transactional data
        seedOrders(orderCount);
        seedInvoicesAndPayments();

        log.info("Database seeding completed successfully!");
    }
}
