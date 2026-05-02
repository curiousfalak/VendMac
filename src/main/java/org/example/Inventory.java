package org.example;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAY 1 - STEP 4: Inventory
 *
 * Manages the collection of all products in the machine.
 * Uses a HashMap: productId → Product for O(1) lookup.
 *
 * Responsibilities:
 *  - Store all products
 *  - Look up product by ID
 *  - Check stock
 *  - Decrement stock on purchase
 *  - Restock products (admin)
 */
public class Inventory {

    // Key = product ID (e.g., "A1"), Value = Product object
    private Map<String, Product> products = new HashMap<>();

    /**
     * Add a product to the inventory.
     * Called once at startup to seed the machine.
     */
    public void addProduct(Product product) {
        products.put(product.getId(), product);
    }

    /**
     * Get a product by its slot ID.
     * Returns null if not found.
     */
    public Product getProduct(String id) {
        return products.get(id);
    }

    /**
     * Get all products (for displaying the grid on HomeScreen)
     */
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    /**
     * Check if a product is available for purchase.
     */
    public boolean isAvailable(String id) {
        Product p = products.get(id);
        return p != null && p.isInStock();
    }

    /**
     * Dispense a product — reduces its stock by 1.
     * @throws IllegalStateException if out of stock
     */
    public void dispense(String id) {
        Product p = products.get(id);
        if (p == null) throw new IllegalArgumentException("Product not found: " + id);
        if (!p.isInStock()) throw new IllegalStateException("Out of stock: " + id);
        p.decrementQuantity();
    }

    /**
     * Restock a specific product (admin mode, Day 7)
     */
    public void restock(String id, int amount) {
        Product p = products.get(id);
        if (p != null) p.restock(amount);
    }

    /**
     * Seed the machine with default products.
     * Call this in your main/app startup.
     */
    public void loadDefaultProducts() {
        // Drinks
        addProduct(new Drink("A1", "Coca-Cola",  25.0, 5, true));
        addProduct(new Drink("A2", "Pepsi",       25.0, 5, true));
        addProduct(new Drink("A3", "Water",       15.0, 8, false));
        addProduct(new Drink("A4", "Mango Juice", 30.0, 4, false));

        // Snacks
        addProduct(new Snack("B1", "Lays Classic", 20.0, 6, 150));
        addProduct(new Snack("B2", "Kurkure",       15.0, 0, 130)); // Out of stock!
        addProduct(new Snack("B3", "Biscuit",       10.0, 7, 80));
        addProduct(new Snack("B4", "Chocolate Bar", 40.0, 3, 250));

        // Hot Beverages
        addProduct(new HotBeverage("C1", "Tea",            10.0, 10, 30));
        addProduct(new HotBeverage("C2", "Coffee",         15.0, 8,  45));
        addProduct(new HotBeverage("C3", "Hot Chocolate",  20.0, 5,  60));
    }
}