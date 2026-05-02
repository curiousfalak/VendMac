package org.example;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.List;

/**
 * HomeScreen — product grid + live thread indicator panel.
 *
 * Thread indicator shows:
 *   • JavaFX App Thread  (always blue/running)
 *   • SessionTimer-Thread (amber pulse when active)
 *   • Platform.runLater label (flashes on cross-thread callbacks)
 */
public class Homescreen extends VBox {

    private VendingMachine machine;
    private Scenemanager   sceneManager;

    private Label  insertedLabel;
    private Label  messageLabel;
    private Label  stateLabel;
    private GridPane productGrid;

    // ── Thread indicator widgets ─────────────────────────────────────────
    private Circle  timerDot;
    private Label   timerThreadLabel;
    private Label   runLaterLabel;
    private FadeTransition runLaterFade;

    public Homescreen(VendingMachine machine, Scenemanager sceneManager) {
        this.machine      = machine;
        this.sceneManager = sceneManager;
        buildUI();
        registerCallbacks();
    }

    private void buildUI() {
        setStyle("-fx-background-color: #0e0e18;");
        setSpacing(12);
        setPadding(new Insets(16));
        setAlignment(Pos.TOP_CENTER);
        getChildren().addAll(
                buildTitleBar(),
                buildThreadPanel(),
                buildProductGrid(),
                buildMessageBar(),
                buildBottomBar()
        );
    }

    // ── Title bar ────────────────────────────────────────────────────────

    private HBox buildTitleBar() {
        Label title = new Label("VENDING MACHINE");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#a0a8ff"));

        stateLabel = new Label("[ IDLE ]");
        stateLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        stateLabel.setTextFill(Color.web("#4ade80"));

        HBox bar = new HBox(title, new Region(), stateLabel);
        HBox.setHgrow(bar.getChildren().get(1), Priority.ALWAYS);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(10, 14, 10, 14));
        bar.setStyle("-fx-background-color: #1a1a3e; -fx-background-radius: 10;");
        return bar;
    }

    // ── Thread indicator panel ───────────────────────────────────────────

    private VBox buildThreadPanel() {
        VBox panel = new VBox(6);
        panel.setPadding(new Insets(10, 12, 10, 12));
        panel.setStyle("-fx-background-color: #12122a; -fx-background-radius: 8; " +
                "-fx-border-color: #2a2a50; -fx-border-radius: 8; -fx-border-width: 1;");

        Label header = new Label("Thread Activity");
        header.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
        header.setTextFill(Color.web("#6666aa"));

        HBox fxRow    = buildThreadRow("JavaFX App Thread", Color.web("#60a5fa"), true, false);
        HBox timerRow = buildTimerRow();
        HBox rlRow    = buildRunLaterRow();

        panel.getChildren().addAll(header, fxRow, timerRow, rlRow);
        return panel;
    }

    private HBox buildThreadRow(String name, Color dotColor, boolean running, boolean daemon) {
        Circle dot = new Circle(5, dotColor);
        Label lbl = new Label(name);
        lbl.setFont(Font.font("Courier New", 11));
        lbl.setTextFill(Color.web("#cccccc"));

        Label badge = new Label(daemon ? "DAEMON" : running ? "RUNNING" : "IDLE");
        badge.setFont(Font.font("Courier New", 9));
        badge.setPadding(new Insets(2, 6, 2, 6));
        badge.setStyle(daemon
                ? "-fx-background-color: #1f1f3e; -fx-text-fill: #818cf8; -fx-background-radius: 4;"
                : "-fx-background-color: #0f2e1f; -fx-text-fill: #4ade80; -fx-background-radius: 4;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(8, dot, lbl, spacer, badge);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox buildTimerRow() {
        timerDot = new Circle(5, Color.web("#444444"));
        timerThreadLabel = new Label("SessionTimer-Thread  [daemon, inactive]");
        timerThreadLabel.setFont(Font.font("Courier New", 11));
        timerThreadLabel.setTextFill(Color.web("#666666"));

        Label badge = new Label("DAEMON");
        badge.setFont(Font.font("Courier New", 9));
        badge.setPadding(new Insets(2, 6, 2, 6));
        badge.setStyle("-fx-background-color: #1f1f3e; -fx-text-fill: #818cf8; -fx-background-radius: 4;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(8, timerDot, timerThreadLabel, spacer, badge);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox buildRunLaterRow() {
        Circle dot = new Circle(5, Color.web("#c084fc"));
        runLaterLabel = new Label("Platform.runLater() — idle");
        runLaterLabel.setFont(Font.font("Courier New", 11));
        runLaterLabel.setTextFill(Color.web("#555555"));

        runLaterFade = new FadeTransition(Duration.millis(600), runLaterLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(8, dot, runLaterLabel, spacer);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /** Flash the Platform.runLater label — called from timer thread via callback */
    public void flashRunLater(String detail) {
        runLaterLabel.setText("Platform.runLater()  " + detail);
        runLaterLabel.setTextFill(Color.web("#c084fc"));
        runLaterFade.stop();
        runLaterFade.setFromValue(1.0);
        runLaterFade.setToValue(0.3);
        runLaterFade.setCycleCount(4);
        runLaterFade.setAutoReverse(true);
        runLaterFade.play();
        runLaterFade.setOnFinished(e -> {
            runLaterLabel.setText("Platform.runLater() — idle");
            runLaterLabel.setTextFill(Color.web("#555555"));
        });
    }

    /** Activate timer dot with amber pulse */
    public void activateTimerDot() {
        timerDot.setFill(Color.web("#fbbf24"));
        timerThreadLabel.setTextFill(Color.web("#fbbf24"));
        timerThreadLabel.setText("SessionTimer-Thread  [running — 30s countdown]");
        FadeTransition pulse = new FadeTransition(Duration.millis(700), timerDot);
        pulse.setFromValue(1.0); pulse.setToValue(0.3);
        pulse.setCycleCount(Animation.INDEFINITE); pulse.setAutoReverse(true);
        timerDot.getProperties().put("pulse", pulse);
        pulse.play();
    }

    /** Deactivate timer dot */
    public void deactivateTimerDot() {
        Object p = timerDot.getProperties().get("pulse");
        if (p instanceof FadeTransition) ((FadeTransition) p).stop();
        timerDot.setFill(Color.web("#444444")); timerDot.setOpacity(1);
        timerThreadLabel.setTextFill(Color.web("#666666"));
        timerThreadLabel.setText("SessionTimer-Thread  [daemon, inactive]");
    }

    // ── Product Grid ─────────────────────────────────────────────────────

    private GridPane buildProductGrid() {
        productGrid = new GridPane();
        productGrid.setHgap(10); productGrid.setVgap(10);
        productGrid.setAlignment(Pos.CENTER);
        populateGrid();
        return productGrid;
    }

    private void populateGrid() {
        productGrid.getChildren().clear();
        List<Product> products = machine.getInventory().getAllProducts();
        products.sort((a, b) -> a.getId().compareTo(b.getId()));
        int col = 0, row = 0;
        for (Product p : products) {
            Button btn = createProductButton(p);
            // Staggered entrance animation
            btn.setOpacity(0);
            btn.setTranslateY(20);
            PauseTransition delay = new PauseTransition(Duration.millis((col + row * 3) * 45));
            delay.setOnFinished(e -> {
                FadeTransition ft = new FadeTransition(Duration.millis(200), btn);
                ft.setFromValue(0); ft.setToValue(1); ft.play();
                TranslateTransition tt = new TranslateTransition(Duration.millis(200), btn);
                tt.setFromY(20); tt.setToY(0); tt.play();
            });
            delay.play();
            productGrid.add(btn, col, row);
            col++; if (col == 3) { col = 0; row++; }
        }
    }

    private Button createProductButton(Product product) {
        String emoji = switch (product.getCategory()) {
            case "DRINK" -> "🥤";
            case "SNACK" -> "🍟";
            case "HOT"   -> "☕";
            default      -> "📦";
        };
        String label = product.getId() + "\n" + emoji + " " + product.getName()
                + "\n₹" + (int) product.getPrice();
        Button btn = new Button(label);
        btn.setPrefSize(136, 88);
        btn.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
        btn.setWrapText(true);

        if (product.isInStock()) {
            String base = "-fx-background-color: #1e1e40; -fx-text-fill: #ccccff; " +
                    "-fx-background-radius: 8; -fx-cursor: hand; " +
                    "-fx-border-color: #2a2a60; -fx-border-width: 1; -fx-border-radius: 8;";
            String hover = "-fx-background-color: #2a2a60; -fx-text-fill: white; " +
                    "-fx-background-radius: 8; -fx-cursor: hand; " +
                    "-fx-border-color: #e94560; -fx-border-width: 1.5; -fx-border-radius: 8;";
            btn.setStyle(base);
            btn.setOnMouseEntered(e -> btn.setStyle(hover));
            btn.setOnMouseExited(e -> btn.setStyle(base));
            btn.setOnAction(e -> onProductSelected(product));
        } else {
            btn.setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: #444444; " +
                    "-fx-background-radius: 8; -fx-border-color: #2a2a2a; -fx-border-radius: 8;");
            btn.setText(label + "\n[SOLD OUT]");
            btn.setDisable(true);
        }
        return btn;
    }

    // ── Message bar ──────────────────────────────────────────────────────

    private HBox buildMessageBar() {
        messageLabel = new Label("Insert coins, then select a product.");
        messageLabel.setFont(Font.font("Courier New", 12));
        messageLabel.setTextFill(Color.web("#4ade80"));
        HBox bar = new HBox(messageLabel);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(7, 12, 7, 12));
        bar.setStyle("-fx-background-color: #0a0a18; -fx-background-radius: 7;");
        return bar;
    }

    // ── Bottom bar ───────────────────────────────────────────────────────

    private HBox buildBottomBar() {
        insertedLabel = new Label("Inserted: ₹0");
        insertedLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        insertedLabel.setTextFill(Color.web("#fbbf24"));

        Button payBtn = new Button("Insert Coins");
        payBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        payBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; " +
                "-fx-background-radius: 7; -fx-cursor: hand;");
        payBtn.setOnAction(e -> sceneManager.showPaymentScreen());

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        cancelBtn.setStyle("-fx-background-color: #2a1010; -fx-text-fill: #f87171; " +
                "-fx-border-color: #4a2020; -fx-border-radius: 7; -fx-background-radius: 7; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> machine.cancel());

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bar = new HBox(12, insertedLabel, spacer, payBtn, cancelBtn);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10, 14, 10, 14));
        bar.setStyle("-fx-background-color: #1a1a3e; -fx-background-radius: 10;");
        return bar;
    }

    // ── Product selected ─────────────────────────────────────────────────

    private void onProductSelected(Product product) {
        machine.selectProduct(product.getId());
    }

    // ── Register machine callbacks ────────────────────────────────────────

    private void registerCallbacks() {
        machine.setOnAmountChanged(() -> {
            double amt = machine.getCoinProcessor().getInsertedAmount();
            insertedLabel.setText("Inserted: ₹" + (int) amt);
        });

        machine.setOnMessage(msg -> {
            messageLabel.setText(msg);
            // Pulse the message label
            ScaleTransition st = new ScaleTransition(Duration.millis(120), messageLabel);
            st.setFromX(1); st.setToX(1.03); st.setCycleCount(2); st.setAutoReverse(true);
            st.play();
        });

        machine.setOnStateChanged(() -> {
            double amt = machine.getCoinProcessor().getInsertedAmount();
            insertedLabel.setText("Inserted: ₹" + (int) amt);
            String stateName = machine.getCurrentState().getStateName();
            stateLabel.setText("[ " + stateName + " ]");
            stateLabel.setTextFill(switch (stateName) {
                case "HAS_MONEY"   -> Color.web("#fbbf24");
                case "DISPENSING"  -> Color.web("#c084fc");
                default            -> Color.web("#4ade80");
            });
            // Manage timer dot
            if (stateName.equals("HAS_MONEY")) activateTimerDot();
            else deactivateTimerDot();
        });

        machine.setOnDispensing((product, change, coins) -> {
            sceneManager.showDispensingScreen(product, change, coins);
        });

        machine.setOnCoinsReturned(amount -> {
            // This may be called from SessionTimer background thread via Platform.runLater
            flashRunLater("← coins returned ₹" + (int) amount);
            messageLabel.setText("₹" + (int) amount + " returned. Thank you!");
            messageLabel.setTextFill(Color.web("#4ade80"));
            insertedLabel.setText("Inserted: ₹0");
            deactivateTimerDot();
        });
    }

    public void refresh() {
        getChildren().clear();
        buildUI();
        registerCallbacks();
    }
}