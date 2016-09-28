package image;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.PushClient;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScreenGrab {

    private PushClient instance;
    private Point2D begin;
    private Point2D end;
    private double width;
    private double height;
    private boolean hasSelected = false;
    private Stage stage;

    public ScreenGrab(PushClient pushClient) {
        this.instance = pushClient;
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

    public void getPartOfScreen(Stage primaryStage) {
        this.stage = primaryStage;

        stage.setX(instance.getOffset());
        stage.setY(0);
        stage.setOpacity(.2);
        stage.setTitle("Push");
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        final Group root = new Group();
        final Scene mainScene = new Scene(root);
        stage.setScene(mainScene);

        Rectangle2D result = new Rectangle2D.Double();
        GraphicsEnvironment localGE = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice gd : localGE.getScreenDevices()) {
            for (GraphicsConfiguration graphicsConfiguration : gd.getConfigurations()) {
                Rectangle2D.union(result, graphicsConfiguration.getBounds(), result);
            }
        }

        final Canvas canvas = new Canvas(result.getWidth(), result.getHeight());
        root.getChildren().add(canvas);

        mainScene.setOnMousePressed(event -> this.begin = new Point2D(event.getX(), event.getY()));

        mainScene.setOnMouseDragged(event -> {

            this.end = new Point2D(event.getX(), event.getY());

            final GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
            graphicsContext2D.setFill(new javafx.scene.paint.Color(1f, 1f, 1f, 0f));
            graphicsContext2D.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            width = Math.abs(end.getX() - begin.getX());
            height = Math.abs(end.getY() - begin.getY());

            Point2D start = calculateStartPoint();


            graphicsContext2D.setFill(javafx.scene.paint.Color.GRAY);
            graphicsContext2D.strokeRect(start.getX(), start.getY(), width, height);
            graphicsContext2D.fillRect(start.getX(), start.getY(), width, height);


        });

        mainScene.setOnMouseReleased(event -> {

            stage.setOpacity(0);

        });

        mainScene.setOnKeyPressed(event -> {
            if (width > 0 && height > 0) {
                if (event.getCode().toString().equals("ENTER")) {
                    captureImage();
                }
            }
        });

        stage.show();
    }

    private Point2D calculateStartPoint() {
        Point2D start;
        if (begin.getX() > end.getX() && begin.getY() > end.getY()) {
            start = new Point2D(begin.getX() - width, begin.getY() - height);
        } else if (begin.getX() > end.getX()) {
            start = new Point2D(begin.getX() - width, begin.getY());
        } else if (begin.getY() > end.getY()) {
            start = new Point2D(begin.getX(), begin.getY() - height);
        } else {
            start = begin;
        }
        return start;
    }


    private void captureImage() {

        BufferedImage capture;

        try {

            Point2D start = calculateStartPoint();
            capture = new Robot().createScreenCapture(new Rectangle(
                    (int) start.getX() + instance.getOffset(),
                    (int) start.getY(),
                    (int) width,
                    (int) height
            ));
            File imageFile = new File("image.png");
            ImageIO.write(capture, "png", imageFile);
            // TODO: 9/28/2016 Switched to saving to file for debug
            //ByteArrayOutputStream os = new ByteArrayOutputStream();
            // ImageIO.write(capture, "png ", os);
            // InputStream is = new ByteArrayInputStream(os.toByteArray());
            // Upload.uploadDataToServer(is, "test");
        } catch (AWTException | IOException ex) {
            ex.printStackTrace();
        }

        System.exit(0);


    }
}