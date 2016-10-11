package image;

import components.ComponentContainer;
import config.Config;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.input.DragEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static javafx.scene.paint.Color.GREY;

public class ScreenGrab {

    private static final Logger LOGGER = Logger.getLogger(ScreenGrab.class.getName());
    private final Config config;
    private final Stage stage;


    private Canvas canvas;

    private Point2D begin, end;

    private double width, height;

    private ComponentContainer componentContainer = new ComponentContainer();

    public ScreenGrab(final Config config, final Stage stage) {
        this.config = config;
        this.stage = stage;
    }

    public void start() {
        stage.setX(Integer.valueOf(config.getProperties().getProperty("offset")));
        stage.setY(0);
        stage.setOpacity(.1);
        stage.setTitle("Push");
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        final Group root = new Group();
        final Scene mainScene = new Scene(root);

        stage.setScene(mainScene);
        Rectangle2D screens = getScreens();
        canvas = new Canvas(screens.getWidth(), screens.getHeight());
        root.getChildren().add(canvas);
        stage.show();

        mainScene.setOnMousePressed(event -> componentContainer.getActiveComponent().handleMousePressed(event));

        mainScene.setOnMouseDragged(event -> componentContainer.getActiveComponent().handleMouseDragged(event));

        mainScene.setOnMouseReleased(event -> componentContainer.getActiveComponent().handleMouseReleased(event));

        mainScene.setOnKeyPressed(event -> componentContainer.getActiveComponent().handleKeyPressed(event));

        mainScene.setOnDragDropped(event -> componentContainer.getActiveComponent().handleDragDropped(event));

        mainScene.setOnDragOver(event -> componentContainer.getActiveComponent().handleDragOver(event));
    }


    private EventHandler<DragEvent> handleDragDropped() {
        return event -> {

        };
    }

    private EventHandler<DragEvent> handleDragOver() {
        return event -> {
        };
    }

    private void setUpDragDropScene() {
        final Group root = new Group();
        final Scene dragDropScene = new Scene(root);
        stage.hide();
        stage.setScene(dragDropScene);
        stage.setX(Integer.valueOf(config.getProperties().getProperty("offset")));
        stage.setWidth(100);
        stage.setHeight(200);
        stage.setY(getScreens().getHeight() / 2 - stage.getHeight());

        stage.setOpacity(1);

        stage.show();
        stage.setAlwaysOnTop(true);

        dragDropScene.setOnDragOver(handleDragOver());

        dragDropScene.setOnDragDropped(handleDragDropped());

        dragDropScene.setFill(GREY);
    }

    private void showError(final String error) {
        LOGGER.fatal(error);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(error);

        alert.showAndWait();
    }


    private Rectangle2D getScreens() {
        Rectangle2D result = new Rectangle2D.Double();
        GraphicsEnvironment localGE = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice gd : localGE.getScreenDevices()) {
            for (GraphicsConfiguration graphicsConfiguration : gd.getConfigurations()) {
                Rectangle2D.union(result, graphicsConfiguration.getBounds(), result);
            }
        }
        LOGGER.info(String.format("Screensize calculated: X = %s Y = %s Width = %s Height = %s",
                result.getX(), result.getY(), result.getWidth(), result.getHeight()));
        return result;
    }








}