package components;

import config.Config;
import http.Upload;
import image.GammaCorrector;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static javafx.scene.paint.Color.GRAY;

/**
 * Created by fr3d63 on 10/10/16.
 */
public class ScreenShotComponent implements IComponent {
    private static final Logger LOGGER = Logger.getLogger(ScreenShotComponent.class.getName());

    private final Stage stage;
    private final Config config;
    private final Rectangle2D screens;
    private Canvas canvas;
    private Point2D begin;
    private Point2D end;
    private double width;
    private double height;
    private final GammaCorrector gammaCorrector = new GammaCorrector();

    public ScreenShotComponent(Stage stage, Config config, Rectangle2D screens) {
        this.stage = stage;
        this.config = config;
        this.screens = screens;
    }

    private static final String OS = System.getProperty("os.name").toLowerCase();

    private static boolean isMac() {
        return (OS.contains("mac") || OS.contains("darvin"));
    }

    @Override
    public void handleMouseReleased(MouseEvent event) {
        LOGGER.info("Mouse released");
        stage.hide();
        stage.close();
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        Optional<Point2D> startOptional = calculateStartPoint();
        startOptional.ifPresent(start -> {
            graphicsContext.clearRect(start.getX(), start.getY(), width, height);

            try {
                makePartialScreenShot(start);
            } catch (AWTException | IOException e) {
                // showError(e.getLocalizedMessage());
                System.exit(0);
            }
        });

    }

    @Override
    public void handleKeyPressed(KeyEvent event) {
        final String key = event.getCode().getName();

        if (key.equals(config.getProperties().getProperty("captureFullScreen"))) {
            LOGGER.info("Fullscreen Screenshot key pressed Key: " + key);
            try {
                makeFullscreenScreenShot();
                System.exit(0);
            } catch (IOException e) {
                // showError(e.getLocalizedMessage());
                e.printStackTrace();
                System.exit(0);
            }
        } else {
            LOGGER.info("Some other key pressed, exited Key: " + key);
            System.exit(0);
        }

    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
        this.end = new Point2D(event.getX(), event.getY());

        final GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        graphicsContext2D.setFill(new javafx.scene.paint.Color(1f, 1f, 1f, 0f));
        graphicsContext2D.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        width = Math.abs(end.getX() - begin.getX());
        height = Math.abs(end.getY() - begin.getY());
        graphicsContext2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        Optional<Point2D> startOptional = calculateStartPoint();
        startOptional.ifPresent(start -> {
            graphicsContext2D.setFill(GRAY);
            graphicsContext2D.strokeRect(start.getX(), start.getY(), width, height);
            graphicsContext2D.fillRect(start.getX(), start.getY(), width, height);
        });

    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        LOGGER.info("Mouse pressed");
        this.begin = new Point2D(event.getX(), event.getY());
    }

    @Override
    public void handleDragDropped(DragEvent event) {

    }

    @Override
    public void handleDragOver(DragEvent event) {

    }

    private void makePartialScreenShot(Point2D start) throws AWTException, IOException {
        if (width > 0 && height > 0) {
            BufferedImage capture;

            final Rectangle rect = new Rectangle(
                    (int) start.getX() + Integer.valueOf(config.getProperties().getProperty("offset")),
                    (int) start.getY(),
                    (int) width,
                    (int) height
            );
            capture = new Robot().createScreenCapture(rect);
            // TODO: 04/10/16 figure out gamma value
            if (isMac()) {
                capture = this.gammaCorrector.gammaCorrection(capture, 3.2);
            }
            LOGGER.info(String.format("Size for partial screenshot: X = %s Y = %s Width = %s Height = %s",
                    rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
            pushScreenshotToServer(capture);
        }

        System.exit(0);
    }

    private void pushScreenshotToServer(BufferedImage capture) throws IOException {
        File imageFile = new File(
                System.getProperty("user.home")
                        + "/.push/"
                        + System.currentTimeMillis()
                        + "screengrab.png");
        ImageIO.write(capture, "png", imageFile);
        Upload.uploadTempContent(imageFile, config.getProperties().getProperty("url"), config);
    }


    private void makeFullscreenScreenShot() throws IOException {
        stage.hide();
        stage.close();
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
        pushScreenshotToServer(capture);
    }

}
