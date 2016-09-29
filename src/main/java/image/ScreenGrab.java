package image;

import config.Config;
import http.Upload;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.PushClient;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static javafx.scene.paint.Color.*;

public class ScreenGrab {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    private final PushClient pushClientInstance;
    private final GammaCorrector gammaCorrector;
    private final Config config;
    private final Stage stage;

    private Canvas canvas;
    private Point2D begin;
    private Point2D end;
    private double width;
    private double height;


    public ScreenGrab(final PushClient pushClient, final Config config, final Stage stage) {
        this.pushClientInstance = pushClient;
        this.config = config;
        this.stage = stage;
        this.gammaCorrector = new GammaCorrector();
    }

    private static boolean isWindows() {
        return (OS.contains("win"));
    }

    private static boolean isMac() {
        return (OS.contains("mac") || OS.contains("darvin"));
    }

    private static boolean isUnix() {
        return (OS.contains("nux"));
    }

    public BufferedImage getFullScreen() {
        Rectangle screenRect = new Rectangle(0, 0, 0, 0);
        for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
        }
        BufferedImage capture = null;
        try {
            capture = new Robot().createScreenCapture(screenRect);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return capture;
    }

    public void getPartOfScreen() {
        stage.setX(Integer.valueOf(config.getProperties().getProperty("offset")));
        stage.setY(0);
        stage.setOpacity(.1);
        stage.setTitle("Push");
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        final Group root = new Group();
        final Scene mainScene = new Scene(root);
        if (!isWindows()) {
            mainScene.setFill(null);
        }
        stage.setScene(mainScene);

        Rectangle2D result = getScreens();

        canvas = new Canvas(result.getWidth(), result.getHeight());
        root.getChildren().add(canvas);
        stage.show();

        mainScene.setOnMousePressed(handleMousePressed());

        mainScene.setOnMouseDragged(handleMouseDragged());

        mainScene.setOnMouseReleased(handleMouseReleased());

        mainScene.setOnKeyPressed(handleKeyboardEvent());
    }

    private Rectangle2D getScreens() {
        Rectangle2D result = new Rectangle2D.Double();
        GraphicsEnvironment localGE = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice gd : localGE.getScreenDevices()) {
            for (GraphicsConfiguration graphicsConfiguration : gd.getConfigurations()) {
                Rectangle2D.union(result, graphicsConfiguration.getBounds(), result);
            }
        }
        return result;
    }

    private EventHandler<MouseEvent> handleMouseReleased() {
        return event -> {
            stage.hide();
            stage.close();
            GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            Point2D start = calculateStartPoint();
            graphicsContext.clearRect(start.getX(), start.getY(), width, height);
            try {

                BufferedImage capture;

                Point2D start1 = calculateStartPoint();
                capture = new Robot().createScreenCapture(new Rectangle(
                        (int) start1.getX() + Integer.valueOf(config.getProperties().getProperty("offset")),
                        (int) start1.getY(),
                        (int) width,
                        (int) height
                ));
                if (isMac()) {
                    capture = gammaCorrector.gammaCorrection(capture, 1.134);
                }
                File imageFile = new File(
                        System.getProperty("user.home")
                                + "/.push/"
                                + System.currentTimeMillis()
                                + "screengrab.png");
                ImageIO.write(capture, "png", imageFile);
                Upload.uploadScreenshot(imageFile, config.getProperties().getProperty("url"), config);

                System.exit(0);
            } catch (AWTException | IOException e) {
                showError(e.getLocalizedMessage());
            }

        };
    }

    private EventHandler<KeyEvent> handleKeyboardEvent() {
        return event -> {
            if (event.getCode().getName().equals("S")) {
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

                dragDropScene.setOnDragOver(handleDragOver());

                // Dropping over surface
                dragDropScene.setOnDragDropped(handleDragDropped());

                dragDropScene.setFill(GREY);
            }
        };
    }

    private EventHandler<DragEvent> handleDragDropped() {
        return event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                String filePath = null;
                for (File file:db.getFiles()) {
                    filePath = file.getAbsolutePath();
                    try {
                        Upload.uploadFile(new File(filePath), config.getProperties().getProperty("url"), config);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();

            System.exit(0);
        };
    }

    private EventHandler<DragEvent> handleDragOver() {
        return event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        };
    }

    private EventHandler<MouseEvent> handleMousePressed() {
        return event -> this.begin = new Point2D(event.getX(), event.getY());
    }

    private EventHandler<MouseEvent> handleMouseDragged() {
        return event -> {
            this.end = new Point2D(event.getX(), event.getY());

            final GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
            graphicsContext2D.setFill(new Color(1f, 1f, 1f, 0f));
            graphicsContext2D.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            width = Math.abs(end.getX() - begin.getX());
            height = Math.abs(end.getY() - begin.getY());
            graphicsContext2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            Point2D start = calculateStartPoint();

            graphicsContext2D.setFill(GRAY);
            graphicsContext2D.strokeRect(start.getX(), start.getY(), width, height);
            graphicsContext2D.fillRect(start.getX(), start.getY(), width, height);


        };
    }

    private Point2D calculateStartPoint() {
        if (begin.getX() > end.getX() && begin.getY() > end.getY()) {
            return new Point2D(begin.getX() - width, begin.getY() - height);
        }
        if (begin.getX() > end.getX()) {
            return new Point2D(begin.getX() - width, begin.getY());
        }
        if (begin.getY() > end.getY()) {
            return new Point2D(begin.getX(), begin.getY() - height);
        }
        return begin;
    }


    private void showError(final String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setContentText(error);

        alert.showAndWait();
    }
}