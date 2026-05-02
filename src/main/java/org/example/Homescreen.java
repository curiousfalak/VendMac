package org.example;



import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * DAY 3 - STEP 1: HomeScreen
 *
 * This is the main product selection screen.
 * Layout:
 *   ┌─────────────────────────────────────┐
 *   │       🥤 VENDING MACHINE 🥤         │  ← Title bar
 *   ├─────────────────────────────────────┤
 *   │  [A1 Coke ₹25] [A2 Pepsi ₹25] ...  │  ← Product grid (3 columns)
 *   │  [B1 Lays ₹20] [B2 Kurkure SOLD]   │
 *   │  [C1 Tea ₹10]  [C2 Coffee ₹15] ... │
 *   ├─────────────────────────────────────┤
 *   │  💰 Inserted: ₹0      [→ Payment]  │  ← Bottom bar
 *   └─────────────────────────────────────┘
 *
 * KEY JAVAFX CONCEPT:
 *   VBox  = vertical box (stacks children top-to-bottom)
 *   HBox  = horizontal box (stacks children left-to-right)
 *   GridPane = grid layout for the product buttons
 */
public class Homescreen extends VBox {

    private VendingMachine machine;
    private Scenemanager sceneManager;

    private Label insertedLabel; // Shows current inserted amount
    private Label messageLabel;  // Shows status messages
    private GridPane productGrid;

    public Homescreen(VendingMachine machine, Scenemanager sceneManager) {
        this.machine      = machine;
        this.sceneManager = sceneManager;

        buildUI();
        registerCallbacks();
    }

    // ─── Build the complete UI ────────────────────────────────────────────

    private void buildUI() {
        setStyle("-fx-background-color: #1a1a2e;");
        setSpacing(15);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);

        getChildren().addAll(
                buildTitleBar(),
                buildProductGrid(),
                buildMessageBar(),
                buildBottomBar()
        );
    }

    // ─── Title Bar ────────────────────────────────────────────────────────

    private HBox buildTitleBar() {
        Label title = new Label("🥤  VENDING MACHINE  🥤");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);

        HBox bar = new HBox(title);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10));
        bar.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;");
        return bar;
    }

    // ─── Product Grid ─────────────────────────────────────────────────────

    private GridPane buildProductGrid() {
        productGrid = new GridPane();
        productGrid.setHgap(12);
        productGrid.setVgap(12);
        productGrid.setAlignment(Pos.CENTER);

        List<Product> products = machine.getInventory().getAllProducts();

        // Sort by ID so grid is ordered A1, A2, B1, B2...
        products.sort((a, b) -> a.getId().compareTo(b.getId()));

        int col = 0, row = 0;
        for (Product product : products) {
            Button btn = createProductButton(product);
            productGrid.add(btn, col, row);
            col++;
            if (col == 3) { col = 0; row++; } // 3 columns per row
        }
        return productGrid;
    }

    /**
     * Create one product button.
     * In-stock: colored, clickable
     * Out-of-stock: grey, disabled appearance
     */
    private Button createProductButton(Product product) {
        String label = product.getId() + "\n" + product.getName()
                + "\n₹" + (int) product.getPrice();

        Button btn = new Button(label);
        btn.setPrefSize(130, 90);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        btn.setWrapText(true);

        if (product.isInStock()) {
            btn.setStyle(
                    "-fx-background-color: #0f3460; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 10; " +
                            "-fx-cursor: hand;"
            );
            // Hover effect
            btn.setOnMouseEntered(e ->
                    btn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; " +
                            "-fx-background-radius: 10; -fx-cursor: hand;")
            );
            btn.setOnMouseExited(e ->
                    btn.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; " +
                            "-fx-background-radius: 10; -fx-cursor: hand;")
            );
            btn.setOnAction(e -> onProductSelected(product));
        } else {
            // Out of stock — greyed out
            btn.setStyle(
                    "-fx-background-color: #3d3d3d; " +
                            "-fx-text-fill: #888888; " +
                            "-fx-background-radius: 10;"
            );
            btn.setDisable(true);
            btn.setText(label + "\n[OUT OF STOCK]");
        }

        return btn;
    }

    // ─── Message Bar ─────────────────────────────────────────────────────

    private HBox buildMessageBar() {
        messageLabel = new Label("Welcome! Select a product after inserting coins.");
        messageLabel.setTextFill(Color.LIGHTGREEN);
        messageLabel.setFont(Font.font("Arial", 13));

        HBox bar = new HBox(messageLabel);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(8));
        bar.setStyle("-fx-background-color: #0d0d1a; -fx-background-radius: 8;");
        return bar;
    }

    // ─── Bottom Bar (inserted amount + go to payment) ─────────────────────

    private HBox buildBottomBar() {
        insertedLabel = new Label("💰 Inserted: ₹0");
        insertedLabel.setTextFill(Color.GOLD);
        insertedLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        Button payBtn = new Button("💳 Insert Coins →");
        payBtn.setStyle(
                "-fx-background-color: #e94560; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"
        );
        payBtn.setOnAction(e -> sceneManager.showPaymentScreen());

        HBox bar = new HBox(20, insertedLabel, payBtn);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(12));
        bar.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;");
        return bar;
    }

    // ─── Event: product selected ──────────────────────────────────────────

    private void onProductSelected(Product product) {
        // Tell the machine — it delegates to current state
        machine.selectProduct(product.getId());
    }

    // ─── Register machine callbacks to update UI ──────────────────────────

    private void registerCallbacks() {
        machine.setOnAmountChanged(() -> {
            double amt = machine.getCoinProcessor().getInsertedAmount();
            insertedLabel.setText("💰 Inserted: ₹" + (int) amt);
        });

        machine.setOnMessage(msg -> {
            messageLabel.setText(msg);
            messageLabel.setTextFill(Color.ORANGE);
        });

        machine.setOnStateChanged(() -> {
            double amt = machine.getCoinProcessor().getInsertedAmount();
            insertedLabel.setText("💰 Inserted: ₹" + (int) amt);
        });

        machine.setOnDispensing((product, change, coins) -> {
            // Navigate to dispensing screen
            sceneManager.showDispensingScreen(product, change, coins);
        });

        machine.setOnCoinsReturned(amount -> {
            messageLabel.setText("Returned ₹" + (int) amount + ". Thank you!");
            messageLabel.setTextFill(Color.LIGHTGREEN);
            insertedLabel.setText("💰 Inserted: ₹0");
        });
    }

    /** Call this when returning to home to refresh grid */
    public void refresh() {
        // Rebuild grid (stock may have changed)
        getChildren().clear();
        buildUI();
        registerCallbacks();
    }
}
