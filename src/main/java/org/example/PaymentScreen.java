package org.example;



import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * DAY 4 - STEP 1: PaymentScreen
 *
 * Coin keypad UI — user inserts coins here.
 *
 * Layout:
 *   ┌──────────────────────────┐
 *   │  INSERT COINS            │
 *   │  ──────────────────────  │
 *   │  Inserted: ₹0            │
 *   │                          │
 *   │  [₹1] [₹2] [₹5] [₹10]  │  ← Coin buttons
 *   │                          │
 *   │  [← Back] [❌ Cancel]   │
 *   └──────────────────────────┘
 *
 * KEY JAVAFX CONCEPTS USED:
 *   - VBox for vertical stacking
 *   - HBox for horizontal coin row
 *   - Lambda event handlers (btn.setOnAction)
 */
public class PaymentScreen extends VBox {

    private VendingMachine machine;
    private Scenemanager sceneManager;
    private Label          insertedLabel;
    private Label          messageLabel;

    public PaymentScreen(VendingMachine machine, Scenemanager sceneManager) {
        this.machine      = machine;
        this.sceneManager = sceneManager;
        buildUI();
        registerCallbacks();
    }

    private void buildUI() {
        setStyle("-fx-background-color: #1a1a2e;");
        setSpacing(20);
        setPadding(new Insets(30));
        setAlignment(Pos.CENTER);

        getChildren().addAll(
                buildTitle(),
                buildAmountDisplay(),
                buildCoinKeypad(),
                buildMessageLabel(),
                buildActionButtons()
        );
    }

    // ─── Title ────────────────────────────────────────────────────────────

    private Label buildTitle() {
        Label lbl = new Label("💰 INSERT COINS");
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lbl.setTextFill(Color.GOLD);
        return lbl;
    }

    // ─── Amount Display ───────────────────────────────────────────────────

    private VBox buildAmountDisplay() {
        insertedLabel = new Label("₹0");
        insertedLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        insertedLabel.setTextFill(Color.WHITE);

        Label subLabel = new Label("inserted so far");
        subLabel.setTextFill(Color.GRAY);
        subLabel.setFont(Font.font("Arial", 13));

        VBox box = new VBox(5, insertedLabel, subLabel);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 12;");
        box.setPadding(new Insets(20, 40, 20, 40));
        return box;
    }

    // ─── Coin Keypad ──────────────────────────────────────────────────────

    private HBox buildCoinKeypad() {
        int[] coins = {1, 2, 5, 10};
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER);

        for (int coin : coins) {
            Button btn = createCoinButton(coin);
            row.getChildren().add(btn);
        }
        return row;
    }

    private Button createCoinButton(int rupees) {
        Button btn = new Button("₹" + rupees);
        btn.setPrefSize(80, 80);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        btn.setStyle(
                "-fx-background-color: #e94560; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 40; " +   // Circle shape
                        "-fx-cursor: hand;"
        );

        // Hover effect — brighten
        btn.setOnMouseEntered(e ->
                btn.setStyle("-fx-background-color: #ff6b81; -fx-text-fill: white; " +
                        "-fx-background-radius: 40; -fx-cursor: hand;")
        );
        btn.setOnMouseExited(e ->
                btn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; " +
                        "-fx-background-radius: 40; -fx-cursor: hand;")
        );

        // On click: tell machine to insert this coin
        btn.setOnAction(e -> {
            machine.insertCoin(rupees);
            // Amount label updates via callback
        });

        return btn;
    }

    // ─── Message Label ────────────────────────────────────────────────────

    private Label buildMessageLabel() {
        messageLabel = new Label("Tap a coin to insert it");
        messageLabel.setTextFill(Color.LIGHTGREEN);
        messageLabel.setFont(Font.font("Arial", 13));
        return messageLabel;
    }

    // ─── Action Buttons ───────────────────────────────────────────────────

    private HBox buildActionButtons() {
        Button backBtn = new Button("← Back to Products");
        backBtn.setStyle(
                "-fx-background-color: #0f3460; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-cursor: hand;"
        );
        backBtn.setOnAction(e -> sceneManager.showHomeScreen());

        Button cancelBtn = new Button("❌ Cancel & Return Coins");
        cancelBtn.setStyle(
                "-fx-background-color: #555; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-cursor: hand;"
        );
        cancelBtn.setOnAction(e -> {
            machine.cancel(); // Returns coins, goes to IdleState
            sceneManager.showHomeScreen();
        });

        HBox box = new HBox(15, backBtn, cancelBtn);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    // ─── Register Callbacks ───────────────────────────────────────────────

    private void registerCallbacks() {
        machine.setOnAmountChanged(() -> {
            double amt = machine.getCoinProcessor().getInsertedAmount();
            insertedLabel.setText("₹" + (int) amt);
        });

        machine.setOnStateChanged(() -> {
            double amt = machine.getCoinProcessor().getInsertedAmount();
            insertedLabel.setText("₹" + (int) amt);
        });

        machine.setOnMessage(msg -> {
            messageLabel.setText(msg);
        });
    }
}