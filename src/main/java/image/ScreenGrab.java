package image;

import config.Config;
import http.Upload;
import image.gif.GifWriter;
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
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static javafx.scene.paint.Color.GRAY;
import static javafx.scene.paint.Color.GREY;

public class ScreenGrab {

    private static final Logger LOGGER = Logger.getLogger(ScreenGrab.class.getName());
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private final Config config;
    private final Stage stage;

    private Canvas canvas;
    private Point2D begin;
    private Point2D end;
    private double width;
    private double height;
    private boolean wantsGif;


    public ScreenGrab(final Config config, final Stage stage) {
        this.config = config;
        this.stage = stage;
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

        Rectangle2D result = getScreens();

        canvas = new Canvas(result.getWidth(), result.getHeight());
        root.getChildren().add(canvas);
        stage.show();

        mainScene.setOnMousePressed(handleMousePressed());

        mainScene.setOnMouseDragged(handleMouseDragged());

        mainScene.setOnMouseReleased(handleMouseReleased());

        mainScene.setOnKeyPressed(handleKeyboardEvent());
    }

    private EventHandler<MouseEvent> handleMouseReleased() {
        return event -> {
            LOGGER.info("Mouse released");
            stage.hide();
            stage.close();
            GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            Optional<Point2D> startOptional = calculateStartPoint();
            startOptional.ifPresent(start -> {
                graphicsContext.clearRect(start.getX(), start.getY(), width, height);
                if (!wantsGif) {
                    try {
                        makePartialScreenShot(start);
                    } catch (AWTException | IOException e) {
                        showError(e.getLocalizedMessage());
                        System.exit(0);
                    }
                } else {
                    try {
                        makePartialGif(start);
                    } catch (AWTException | IOException | InterruptedException e) {
                        showError(e.getLocalizedMessage());
                        System.exit(0);
                        e.printStackTrace();
                    }
                }
            });
        };
    }


    private EventHandler<DragEvent> handleDragDropped() {
        return event -> {
            LOGGER.info("file drag dropped");
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                if (db.getFiles().size() == 1) {
                    uploadSingeFileFromDragBoard(db);
                } else {
                    try {
                        uploadMultipleFilesFromDragboard(db);
                    } catch (IOException e) {
                        showError(e.getLocalizedMessage());
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

    private EventHandler<MouseEvent> handleMouseDragged() {
        return event -> {
            this.end = new Point2D(event.getX(), event.getY());

            final GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
            graphicsContext2D.setFill(new Color(1f, 1f, 1f, 0f));
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
        };
    }


    private EventHandler<KeyEvent> handleKeyboardEvent() {
        return event -> {
            final String key = event.getCode().getName();
            if (key.equals(config.getProperties().getProperty("uploadFile"))) {
                LOGGER.info("dragDrop key pressed Key: " + key);
                setUpDragDropScene();
            } else if (key.equals(config.getProperties().getProperty("captureFullScreen"))) {
                LOGGER.info("Fullscreen Screenshot key pressed Key: " + key);
                try {
                    makeFullscreenScreenShot();
                    System.exit(0);
                } catch (IOException e) {
                    showError(e.getLocalizedMessage());
                    e.printStackTrace();
                    System.exit(0);
                }
            } else if (key.equals(config.getProperties().getProperty("captureGIF"))) {
                LOGGER.info("captureGIF key pressed Key: " + key);
                this.wantsGif = true;
            } else {
                LOGGER.info("Some other key pressed, exited Key: " + key);
                System.exit(0);
            }
        };
    }

    private EventHandler<MouseEvent> handleMousePressed() {
        return event -> {
            LOGGER.info("Mouse pressed");
            this.begin = new Point2D(event.getX(), event.getY());
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


    private void uploadMultipleFilesFromDragboard(Dragboard db) throws IOException {
        String path = System.getProperty("user.home")
                + "/.push/"
                + System.currentTimeMillis() + "archive.zip";
        try (FileOutputStream fout = new FileOutputStream(path)) {

            ZipOutputStream zout = new ZipOutputStream(fout);
            byte[] buffer = new byte[1024];

            for (File file : db.getFiles()) {
                FileInputStream fsin = new FileInputStream(file);
                zout.putNextEntry(new ZipEntry(file.getName()));

                int len;

                while ((len = fsin.read(buffer)) > 0) {
                    zout.write(buffer, 0, len);
                }

                zout.closeEntry();
                fsin.close();
            }
            zout.close();

            Upload.uploadTempContent(new File(path), config.getProperties().getProperty("url"), config);

        }
    }

    private void uploadSingeFileFromDragBoard(Dragboard db) {
        String filePath;
        filePath = db.getFiles().get(0).getAbsolutePath();
        try {
            Upload.uploadFile(new File(filePath), config.getProperties().getProperty("url"), config);
        } catch (IOException e) {
            showError(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


    private Optional<Point2D> calculateStartPoint() {
        if (end == null || begin == null) {
            return Optional.empty();
        }

        if (begin.getX() > end.getX() && begin.getY() > end.getY()) {
            return Optional.of(new Point2D(begin.getX() - width, begin.getY() - height));
        }
        if (begin.getX() > end.getX()) {
            return Optional.of(new Point2D(begin.getX() - width, begin.getY()));
        }
        if (begin.getY() > end.getY()) {
            return Optional.of(new Point2D(begin.getX(), begin.getY() - height));
        }
        return Optional.of(begin);
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

    private void makePartialGif(Point2D start) throws AWTException, IOException, InterruptedException {
        stage.hide();
        stage.close();

        final List<BufferedImage> imageList = new ArrayList<>();
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


}