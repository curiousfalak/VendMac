package org.example;


/**
 * DAY 1 - STEP 1: Abstract base class for all products
 *
 * WHY abstract? Because you never sell a generic "Product" —
 * you sell a Drink, Snack, or HotBeverage. Abstract forces subclasses.
 */
public abstract class Product {

    private String id;        // Unique code like "A1", "B2"
    private String name;      // Display name like "Coca-Cola"
    private double price;     // Price in rupees
    private int quantity;     // Stock count

    // Constructor
    public Product(String id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Abstract method — every subclass MUST implement this
    // Returns a category label like "DRINK", "SNACK", "HOT"
    public abstract String getCategory();

    // --- Getters ---
    public String getId()       { return id; }
    public String getName()     { return name; }
    public double getPrice()    { return price; }
    public int getQuantity()    { return quantity; }

    public boolean isInStock()  { return quantity > 0; }

    // --- Stock management ---
    public void decrementQuantity() {
        if (quantity > 0) quantity--;
    }

    public void restock(int amount) {
        this.quantity += amount;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + name + " ₹" + price + " (qty:" + quantity + ")";
    }
}
