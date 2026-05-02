package org.example;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.List;

/**
 * DispensingScreen — shows the product drop animation and a live
 * "thread trace" panel that visualises the DispensingState flow:
 *
 *   1. DispensingState.dispense()  → JavaFX App Thread
 *   2. TranslateTransition         → JavaFX Animation Thread
 *   3. TransactionLog.logSale()    → JavaFX App Thread
 *   4. machine.setState(idle)      → JavaFX App Thread
 */
public class Dispensingscreen extends VBox {

    private VendingMachine machine;
    private Scenemanager   sceneManager;
    private VBox           productBox;
    private VBox           traceBox;

    public Dispensingscreen(VendingMachine machine, Scenemanager sceneManager) {
        this.machine      = machine;
        this.sceneManager = sceneManager;
        buildShell();
    }

    private void buildShell() {
        setStyle("-fx-background-color: #0e0e18;");
        setSpacing(16);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);
    }

    public void show(Product product, double change, List<Integer> changeCoins) {
        getChildren().clear();
        getChildren().addAll(
                buildHeader(),
                buildProductCard(product),
                buildThreadTrace(),
                buildChangePanel(change, changeCoins),
                buildBackButton()
        );
        playDropAndTrace(product);
    }

    // ── Header ────────────────────────────────────────────────────────────

    private HBox buildHeader() {
        Label lbl = new Label("DISPENSING");
        lbl.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        lbl.setTextFill(Color.web("#c084fc"));

        Label state = new Label("[ DISPENSING ]");
        state.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        state.setTextFill(Color.web("#c084fc"));

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bar = new HBox(lbl, spacer, state);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10, 14, 10, 14));
        bar.setStyle("-fx-background-color: #1a1a3e; -fx-background-radius: 10;");
        return bar;
    }

    // ── Product Card ──────────────────────────────────────────────────────

    private VBox buildProductCard(Product product) {
        productBox = new VBox(8);
        productBox.setAlignment(Pos.CENTER);
        productBox.setPadding(new Insets(24));
        productBox.setStyle("-fx-background-color: #12122a; -fx-background-radius: 12; " +
                "-fx-border-color: #3a3a70; -fx-border-radius: 12; -fx-border-width: 1;");

        String emoji = switch (product.getCategory()) {
            case "DRINK" -> "🥤";
            case "SNACK" -> "🍟";
            case "HOT"   -> "☕";
            default      -> "📦";
        };
        Label emojiLbl = new Label(emoji);
        emojiLbl.setFont(Font.font(52));

        Label nameLbl = new Label(product.getName());
        nameLbl.setFont(Font.font("Courier New", FontWeight.BOLD, 22));
        nameLbl.setTextFill(Color.WHITE);

        Label priceLbl = new Label("₹" + (int) product.getPrice());
        priceLbl.setFont(Font.font("Courier New", 16));
        priceLbl.setTextFill(Color.web("#fbbf24"));

        productBox.getChildren().addAll(emojiLbl, nameLbl, priceLbl);
        return productBox;
    }

    // ── Thread Trace Panel ────────────────────────────────────────────────

    private VBox buildThreadTrace() {
        traceBox = new VBox(5);
        traceBox.setPadding(new Insets(10, 12, 10, 12));
        traceBox.setStyle("-fx-background-color: #12122a; -fx-background-radius: 8; " +
                "-fx-border-color: #2a2a50; -fx-border-radius: 8; -fx-border-width: 1;");

        Label header = new Label("Thread trace — DispensingState.dispense()");
        header.setFont(Font.font("Courier New", FontWeight.BOLD, 10));
        header.setTextFill(Color.web("#6666aa"));
        traceBox.getChildren().add(header);
        return traceBox;
    }

    /** Adds one line to the trace panel with a slide-in animation */
    private void addTraceLine(String text, Color color, long delayMs) {
        PauseTransition pause = new PauseTransition(Duration.millis(delayMs));
        pause.setOnFinished(e -> {
            Label line = new Label("> " + text);
            line.setFont(Font.font("Courier New", 10));
            line.setTextFill(color);
            line.setOpacity(0);
            line.setTranslateX(-12);

            FadeTransition ft = new FadeTransition(Duration.millis(180), line);
            ft.setFromValue(0); ft.setToValue(1); ft.play();
            TranslateTransition tt = new TranslateTransition(Duration.millis(180), line);
            tt.setFromX(-12); tt.setToX(0); tt.play();

            traceBox.getChildren().add(line);
        });
        pause.play();
    }

    // ── Change Panel ──────────────────────────────────────────────────────

    private VBox buildChangePanel(double change, List<Integer> changeCoins) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(12));
        box.setStyle("-fx-background-color: #0a1a12; -fx-background-radius: 8; " +
                "-fx-border-color: #1a3a22; -fx-border-radius: 8; -fx-border-width: 1;");

        if (change > 0) {
            Label changeLbl = new Label("Change: ₹" + (int) change);
            changeLbl.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
            changeLbl.setTextFill(Color.web("#4ade80"));

            Label coinsLbl = new Label("Coins dispensed: " + changeCoins);
            coinsLbl.setFont(Font.font("Courier New", 12));
            coinsLbl.setTextFill(Color.web("#888888"));

            box.getChildren().addAll(changeLbl, coinsLbl);
        } else {
            Label noChange = new Label("Exact payment — no change.");
            noChange.setFont(Font.font("Courier New", 13));
            noChange.setTextFill(Color.web("#4ade80"));
            box.getChildren().add(noChange);
        }
        return box;
    }

    // ── Back Button ───────────────────────────────────────────────────────

    private Button buildBackButton() {
        Button btn = new Button("Back to Products");
        btn.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        btn.setPrefWidth(200);
        btn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-cursor: hand;");
        btn.setOnAction(e -> sceneManager.showHomeScreen());
        return btn;
    }

    // ── Animations ────────────────────────────────────────────────────────

    /**
     * 1. Product card slides down (TranslateTransition — JavaFX Animation Thread).
     * 2. Thread trace lines appear sequentially (PauseTransition chain).
     *
     * Each trace line represents a real method call in DispensingState.dispense().
     */
    private void playDropAndTrace(Product product) {
        // Product drop — TranslateTransition (JavaFX Animation Thread)
        productBox.setTranslateY(-140);
        TranslateTransition drop = new TranslateTransition(Duration.millis(650), productBox);
        drop.setFromY(-140);
        drop.setToY(0);
        drop.setInterpolator(Interpolator.SPLINE(0.2, 0.9, 0.3, 1.0));
        drop.play();

        // Trace lines — timed to match what actually happens in DispensingState
        addTraceLine("[JavaFX Thread] DispensingState.dispense() called",
                Color.web("#60a5fa"), 50);
        addTraceLine("[JavaFX Thread] CoinProcessor.calculateChange() → ₹"
                        + (int)(machine.getCoinProcessor().getInsertedAmount()),
                Color.web("#60a5fa"), 280);
        addTraceLine("[Animation Thread] TranslateTransition started (600ms)",
                Color.web("#c084fc"), 450);
        addTraceLine("[JavaFX Thread] Inventory.dispense(\"" + product.getId() + "\")",
                Color.web("#60a5fa"), 680);
        addTraceLine("[JavaFX Thread] TransactionLog.logSale() recorded",
                Color.web("#4ade80"), 850);
        addTraceLine("[JavaFX Thread] CoinProcessor.reset()",
                Color.web("#60a5fa"), 980);
        addTraceLine("[JavaFX Thread] machine.setState(IDLE)",
                Color.web("#4ade80"), 1100);
    }
}