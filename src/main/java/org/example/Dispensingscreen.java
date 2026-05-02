package org.example;



import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.List;

/**
 * DAY 4 - STEP 2: DispensingScreen
 *
 * Shown after a successful purchase.
 * Displays product name, change, and a drop animation.
 *
 * KEY JAVAFX ANIMATION:
 *   TranslateTransition moves a node from Y=-100 to Y=0
 *   simulating a product dropping from the slot.
 *
 *   TranslateTransition tt = new TranslateTransition(Duration.millis(600), productBox);
 *   tt.setFromY(-100);
 *   tt.setToY(0);
 *   tt.play();
 */
public class Dispensingscreen extends VBox {

    private VendingMachine machine;
    private Scenemanager sceneManager;

    // These are set dynamically when the screen is shown
    private Label  productNameLabel;
    private Label  changeLabel;
    private Label  coinsLabel;
    private VBox   productBox; // The animated box

    public Dispensingscreen(VendingMachine machine, Scenemanager sceneManager) {
        this.machine      = machine;
        this.sceneManager = sceneManager;
        buildUI();
    }

    private void buildUI() {
        setStyle("-fx-background-color: #1a1a2e;");
        setSpacing(25);
        setPadding(new Insets(40));
        setAlignment(Pos.CENTER);
    }

    /**
     * Called by SceneManager when navigating here.
     * Populates the screen with actual transaction data.
     */
    public void show(Product product, double change, List<Integer> changeCoins) {
        getChildren().clear();

        getChildren().addAll(
                buildSuccessHeader(),
                buildProductCard(product),
                buildChangeInfo(change, changeCoins),
                buildReturnButton()
        );

        // Play drop animation after a short delay
        playDropAnimation();
    }

    // ─── Success Header ───────────────────────────────────────────────────

    private Label buildSuccessHeader() {
        Label lbl = new Label("✅  ENJOY YOUR PURCHASE!");
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lbl.setTextFill(Color.LIGHTGREEN);
        return lbl;
    }

    // ─── Product Card (the animated element) ─────────────────────────────

    private VBox buildProductCard(Product product) {
        productBox = new VBox(10);
        productBox.setAlignment(Pos.CENTER);
        productBox.setPadding(new Insets(30));
        productBox.setStyle(
                "-fx-background-color: #0f3460; " +
                        "-fx-background-radius: 15; " +
                        "-fx-effect: dropshadow(gaussian, #e94560, 20, 0.3, 0, 0);"
        );

        // Big emoji based on category
        String emoji = switch (product.getCategory()) {
            case "DRINK" -> "🥤";
            case "SNACK" -> "🍿";
            case "HOT"   -> "☕";
            default      -> "📦";
        };

        Label emojiLabel = new Label(emoji);
        emojiLabel.setFont(Font.font(60));

        productNameLabel = new Label(product.getName());
        productNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        productNameLabel.setTextFill(Color.WHITE);

        Label priceLabel = new Label("₹" + (int) product.getPrice());
        priceLabel.setFont(Font.font("Arial", 18));
        priceLabel.setTextFill(Color.GOLD);

        productBox.getChildren().addAll(emojiLabel, productNameLabel, priceLabel);
        return productBox;
    }

    // ─── Change Info ──────────────────────────────────────────────────────

    private VBox buildChangeInfo(double change, List<Integer> changeCoins) {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;");

        if (change > 0) {
            Label changeLbl = new Label("💰 Change: ₹" + (int) change);
            changeLbl.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            changeLbl.setTextFill(Color.GOLD);

            Label coinsLbl = new Label("Coins: " + changeCoins + " rupees");
            coinsLbl.setTextFill(Color.LIGHTGRAY);
            coinsLbl.setFont(Font.font("Arial", 13));

            box.getChildren().addAll(changeLbl, coinsLbl);
        } else {
            Label noChange = new Label("✅ Exact change — no coins returned.");
            noChange.setTextFill(Color.LIGHTGREEN);
            box.getChildren().add(noChange);
        }
        return box;
    }

    // ─── Return Button ────────────────────────────────────────────────────

    private Button buildReturnButton() {
        Button btn = new Button("← Back to Products");
        btn.setStyle(
                "-fx-background-color: #e94560; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-background-radius: 8; " +
                        "-fx-font-size: 16; -fx-cursor: hand;"
        );
        btn.setPrefWidth(220);
        btn.setOnAction(e -> {
            sceneManager.showHomeScreen();
        });
        return btn;
    }

    // ─── Drop Animation ───────────────────────────────────────────────────

    /**
     * TranslateTransition: moves the productBox from Y=-120 (above) to Y=0 (normal).
     * Duration: 600ms with ease-in-out feel.
     * This simulates the product dropping from the dispensing slot!
     */
    private void playDropAnimation() {
        if (productBox == null) return;

        TranslateTransition drop = new TranslateTransition(
                Duration.millis(600), productBox
        );
        drop.setFromY(-120); // Start 120px ABOVE its real position
        drop.setToY(0);      // End at its normal position
        drop.setCycleCount(1);

        // Bounce effect: overshoot then come back
        drop.setInterpolator(javafx.animation.Interpolator.SPLINE(0.2, 0.9, 0.3, 1.0));
        drop.play();
    }
}