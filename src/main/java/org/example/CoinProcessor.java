package org.example;


import java.util.ArrayList;
import java.util.List;

/**
 * DAY 1 - STEP 3: CoinProcessor
 *
 * Handles all coin-related logic:
 *  - Accepts coins and accumulates total
 *  - Calculates change
 *  - Returns coin breakdown for change
 *
 * Indian coin denominations: ₹1, ₹2, ₹5, ₹10
 */
public class CoinProcessor {

    // Valid coin values in India (in paise to avoid floating-point issues)
    // We store in int (paise = paisa): ₹1 = 100, ₹2 = 200, ₹5 = 500, ₹10 = 1000
    private static final int[] VALID_COINS_PAISE = {1000, 500, 200, 100}; // ₹10, ₹5, ₹2, ₹1

    private int insertedAmountPaise = 0; // Total inserted by user

    /**
     * Insert a coin. Returns true if coin is valid.
     * @param rupees The coin value in rupees (1, 2, 5, or 10)
     */
    public boolean insertCoin(int rupees) {
        int paise = rupees * 100;
        for (int valid : VALID_COINS_PAISE) {
            if (paise == valid) {
                insertedAmountPaise += paise;
                return true;
            }
        }
        return false; // Invalid coin rejected
    }

    /**
     * Returns the inserted amount in rupees (double)
     */
    public double getInsertedAmount() {
        return insertedAmountPaise / 100.0;
    }

    /**
     * Calculate change needed after a purchase.
     * @param productPriceRupees Price of the selected product
     * @return Change in rupees (always >= 0)
     */
    public double calculateChange(double productPriceRupees) {
        int productPaise = (int) Math.round(productPriceRupees * 100);
        int changePaise = insertedAmountPaise - productPaise;
        return changePaise / 100.0;
    }

    /**
     * Returns the coin denominations to give as change.
     * Uses greedy algorithm (largest coins first).
     * Example: ₹17 change → [₹10, ₹5, ₹2]
     */
    public List<Integer> getChangeCoins(double changeRupees) {
        int changePaise = (int) Math.round(changeRupees * 100);
        List<Integer> coins = new ArrayList<>();

        for (int coinPaise : VALID_COINS_PAISE) {
            while (changePaise >= coinPaise) {
                coins.add(coinPaise / 100); // Add coin in rupees
                changePaise -= coinPaise;
            }
        }
        return coins;
    }

    /**
     * Reset after transaction (or coin return)
     */
    public void reset() {
        insertedAmountPaise = 0;
    }

    /**
     * Can the user afford this product?
     */
    public boolean canAfford(double productPriceRupees) {
        int productPaise = (int) Math.round(productPriceRupees * 100);
        return insertedAmountPaise >= productPaise;
    }
}
