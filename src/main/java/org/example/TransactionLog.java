package org.example;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAY 1 - STEP 5: TransactionLog
 *
 * Records every sale, cancellation, and coin return.
 * Useful for admin report and debugging.
 */
public class TransactionLog {

    /**
     * Inner class representing one transaction entry.
     * Using a record-like structure.
     */
    public static class Entry {
        private final String timestamp;
        private final String type;      // "SALE", "CANCEL", "TIMEOUT"
        private final String productId;
        private final String productName;
        private final double amountPaid;
        private final double changeGiven;

        public Entry(String type, String productId, String productName,
                     double amountPaid, double changeGiven) {
            this.timestamp   = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.type        = type;
            this.productId   = productId;
            this.productName = productName;
            this.amountPaid  = amountPaid;
            this.changeGiven = changeGiven;
        }

        @Override
        public String toString() {
            return timestamp + " | " + type + " | " + productId + " - " + productName
                    + " | Paid: ₹" + amountPaid + " | Change: ₹" + changeGiven;
        }

        // Getters
        public String getType()        { return type; }
        public String getProductId()   { return productId; }
        public String getProductName() { return productName; }
        public double getAmountPaid()  { return amountPaid; }
        public double getChangeGiven() { return changeGiven; }
        public String getTimestamp()   { return timestamp; }
    }

    private List<Entry> log = new ArrayList<>();

    /** Log a successful sale */
    public void logSale(String productId, String productName,
                        double amountPaid, double changeGiven) {
        log.add(new Entry("SALE", productId, productName, amountPaid, changeGiven));
    }

    /** Log a cancellation (user pressed cancel) */
    public void logCancel(double amountReturned) {
        log.add(new Entry("CANCEL", "-", "-", amountReturned, amountReturned));
    }

    /** Log a session timeout (auto-return) */
    public void logTimeout(double amountReturned) {
        log.add(new Entry("TIMEOUT", "-", "-", amountReturned, amountReturned));
    }

    /** Get all entries */
    public List<Entry> getAll() {
        return new ArrayList<>(log);
    }

    /** Print to console (for debugging) */
    public void printAll() {
        System.out.println("=== TRANSACTION LOG ===");
        log.forEach(System.out::println);
        System.out.println("=======================");
    }

    /** Total revenue from sales */
    public double getTotalRevenue() {
        return log.stream()
                .filter(e -> e.getType().equals("SALE"))
                .mapToDouble(e -> e.getAmountPaid() - e.getChangeGiven())
                .sum();
    }
}