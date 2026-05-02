package org.example;


/**
 * DAY 1 - STEP 2b: Snack subclass
 * Examples: Lays, Kurkure, Biscuit
 */
public class Snack extends Product {

    private int calories; // Extra field specific to snacks

    public Snack(String id, String name, double price, int quantity, int calories) {
        super(id, name, price, quantity);
        this.calories = calories;
    }

    @Override
    public String getCategory() {
        return "SNACK";
    }

    public int getCalories() { return calories; }
}