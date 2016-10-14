package components;

import components.container.IComponent;
import components.container.input.InputType;
import components.container.scene.SceneManager;
import components.container.scene.ScreenShotScene;
import config.Config;
import helper.ScreenHelper;
import http.Upload;
import image.gif.GifWriter;
import javafx.event.*;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static javafx.scene.paint.Color.GRAY;

/**
 * Created by fr3d63 on 11/10/16.
 */
public class GifComponent implements IComponent {
    private static final Logger LOGGER = Logger.getLogger(GifComponent.class.getName());

    private final Stage stage;
    private final Config config;
    private final Rectangle2D screens;
    private final SceneManager sceneManager;
    private Canvas canvas;

    private Point2D begin, end;

    private double width, height;

    public GifComponent(Stage stage, Config config, Rectangle2D screens, SceneManager sceneManager) {
        this.stage = stage;
        this.config = config;
        this.screens = screens;
        this.sceneManager = sceneManager;
    }


    private void makePartialGif(Point2D start) throws AWTException, IOException, InterruptedException {
        stage.hide();
        stage.close();

        final java.util.List<BufferedImage> imageList = new ArrayList<>();
        final Robot robot;
        final int frameCount = Integer.valueOf(config.getProperties().getProperty("GIFFrameCount"));
        final int timeBetweenFrames = Integer.valueOf(config.getProperties().getProperty("GIFTimeBetweenFrames"));

        final File gifFile = new File("test.gif");
        LOGGER.info("started to record gif");
        final Rectangle rect = new Rectangle(
                (int) start.getX() + Integer.valueOf(config.getProperties().getProperty("offset")),
                (int) start.getY(),
                (int) width,
                (int) height
        );
        LOGGER.info(String.format("Size for partial gif: X = %s Y = %s Width = %s Height = %s",
                rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
        try (ImageOutputStream imageOut = new FileImageOutputStream(gifFile)) {
            robot = new Robot();
            for (int i = 0; i < frameCount; i++) {
                imageList.add(robot.createScreenCapture(rect));
                Thread.sleep(timeBetweenFrames);
            }

            LOGGER.info("start to pack gif");

            GifWriter gifWriter = new GifWriter(imageOut, imageList.get(0).getType(), timeBetweenFrames, true);

            for (BufferedImage bufferedImage : imageList) {
                gifWriter.writeToSequence(bufferedImage);
            }

            LOGGER.info("finished packing gif");
            gifWriter.close();
            imageOut.close();

            Upload.uploadTempContent(gifFile, config.getProperties().getProperty("url"), config);
            System.exit(0);

        }
    }

    private void handleMouseReleased(MouseEvent event) {
        LOGGER.info("Mouse released");
        stage.hide();
        stage.close();
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        Optional<Point2D> startOptional = ScreenHelper.calculateStartPoint(begin, end, width, height);
        startOptional.ifPresent(start -> {
            graphicsContext.clearRect(start.getX(), start.getY(), width, height);

            try {
                makePartialGif(start);
            } catch (AWTException | IOException e) {
                // showError(e.getLocalizedMessage());
                System.exit(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


    private void handleKeyPressed(KeyEvent event) {
        final String key = event.getCode().getName();

        if (key.equals(config.getProperties().getProperty("captureGIF"))) {
            LOGGER.info("captureGIF key pressed Key: " + key);
            try {
                this.makePartialGif(begin);
            } catch (AWTException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.info("Some other key pressed, exited Key: " + key);
            System.exit(0);
        }

    }

    private void handleMouseDragged(MouseEvent event) {
        this.end = new Point2D(event.getX(), event.getY());

        final GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        graphicsContext2D.setFill(new javafx.scene.paint.Color(1f, 1f, 1f, 0f));
        graphicsContext2D.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        width = Math.abs(end.getX() - begin.getX());
        height = Math.abs(end.getY() - begin.getY());
        graphicsContext2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        Optional<Point2D> startOptional = ScreenHelper.calculateStartPoint(begin, end, width, height);
        startOptional.ifPresent(start -> {
            graphicsContext2D.setFill(GRAY);
            graphicsContext2D.strokeRect(start.getX(), start.getY(), width, height);
            graphicsContext2D.fillRect(start.getX(), start.getY(), width, height);
        });

    }

    private void handleMousePressed(MouseEvent event) {
        LOGGER.info("Mouse pressed");
        this.begin = new Point2D(event.getX(), event.getY());
    }


    @Override
    public Scene getScene() {
        return sceneManager.getScene(ScreenShotScene.class);
    }

    @Override
    public void handle(InputType inputType, Event event) {
        switch (inputType) {
            case MOUSE_PRESSED:
                this.handleMousePressed(((MouseEvent) event));
                break;
            case MOUSE_RELEASED:
                this.handleMouseReleased(((MouseEvent) event));
                break;
            case MOUSE_DRAGGED:
                this.handleMouseDragged(((MouseEvent) event));
                break;
            case KEY_PRESSED:
                this.handleKeyPressed(((KeyEvent) event));
        }
    }
}
