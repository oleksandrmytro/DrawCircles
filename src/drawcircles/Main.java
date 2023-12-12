package drawcircles;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class Main extends Application {

    private final Pane pane = new Pane();
    private double startX;
    private double startY;
    private int pocet = 0;
    private final Label lblVysledek = new Label();
    private final Button btnKonec = new Button("Konec");
    private final Button btnVymazat = new Button("Vymazat");
    private final Label lblPocet = new Label("Pocet kruznic:");
    private final ObservableList<Shape> shapes = FXCollections.observableArrayList();
    private Circle one;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        VBox root = new VBox();
        HBox controlPanel = new HBox();
        
        addControl(controlPanel);
        root.getChildren().addAll(pane, controlPanel);
        VBox.setVgrow(pane, Priority.ALWAYS);
        pane.widthProperty().addListener(o -> resetToDefault());
        pane.heightProperty().addListener(o -> resetToDefault());
        updatePocet();

        Scene scene = new Scene(root, 800, 600);
        pane.setPrefSize(scene.getWidth(), scene.getHeight());
        stage.setTitle("Draw circles");
        stage.setScene(scene);
        stage.show();
    }

    private void addControl(HBox pane) {
        
        pane.getChildren().addAll(btnKonec, btnVymazat, lblPocet, lblVysledek);
        setControlPaneInsets(pane);
        pane.setAlignment(Pos.CENTER);
        resetToDefault();

        btnKonec.setOnAction(o -> {
            if (showConfirmationAlert()) {
                Platform.exit();
                System.exit(0);
            }
        });

        btnVymazat.setOnAction(o -> {
            pocet = 0;
            resetToDefault();
        });

        drawCircle();
    }

    private boolean showConfirmationAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("End?");
        alert.setHeaderText("Opravdu chcete ukoncit?");
        return alert.showAndWait().get() == ButtonType.OK;
    }

    private void resetToDefault() {
        pocet = 0;
        shapes.clear();
        updatePocet();
        setGradientBackground();
        pane.getChildren().clear();
    }

    private void setGradientBackground() {
        LinearGradient gradient = new LinearGradient(
                0,
                0,
                pane.getWidth(),
                pane.getHeight(),
                false,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.WHITE),
                new Stop(1, Color.RED)
        );
        pane.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void drawCircle() {

        pane.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Circle circle = createCircle(event);
                setCircle(circle);

                circle.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                    if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                        removeCircle(circle);
                    }
                });
            }
        });

        pane.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double radius = calculateRadius(event);
                one.setRadius(radius);
            }
        });

        pane.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double radius = calculateRadius(event);
                one.setRadius(radius);
                shapes.add(one);
                pocet++;
                one.setFill(Color.GREEN);
                checkBounds(one);
                updatePocet();
            }
        });

    }

    private Circle createCircle(MouseEvent event) {
        Circle circle = new Circle();
        circle.setStrokeWidth(1);
        circle.setFill(Color.GREEN);
        circle.setStroke(Color.BLACK);
        circle.getStrokeDashArray().add(10d);
        startX = event.getX();
        startY = event.getY();
        circle.setCenterX(startX);
        circle.setCenterY(startY);
        circle.setRadius(0);
        pane.getChildren().add(circle);
        return circle;
    }

    private void removeCircle(Circle circle) {
        pane.getChildren().remove(circle);
        shapes.remove(circle);
        pocet--;
        shapes.forEach(this::checkBounds);
        updatePocet();
    }

    private double calculateRadius(MouseEvent event) {
        return Math.sqrt(Math.pow(event.getX() - startX, 2) + Math.pow(event.getY() - startY, 2));
    }

    private void updatePocet() {
        lblVysledek.setText(String.valueOf(pocet));
    }

    private void checkBounds(Shape block) {
        boolean collisionDetected = false;
        for (Shape static_bloc : shapes) {
            if (static_bloc != block) {
                if (block.getBoundsInParent().intersects(static_bloc.getBoundsInParent())) {
                    collisionDetected = true;
                    static_bloc.setFill(new Color(0, 0, 1, 0.5));
                }
            }
        }
        if (collisionDetected) {
            block.setFill(new Color(0, 0, 1, 0.5));
        } else {
            block.setFill(Color.GREEN);
        }
    }

    private void setControlPaneInsets(HBox pane) {
        HBox.setMargin(btnKonec, new Insets(15, 10, 10, 0));
        HBox.setMargin(btnVymazat, new Insets(15, 10, 10, 0));
        HBox.setMargin(lblPocet, new Insets(20, 10, 10, 0));
        HBox.setMargin(lblVysledek, new Insets(20, 10, 10, 0));
    }

    private void setCircle(Circle circle) {
        one = circle;
    }
}
