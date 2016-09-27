package image;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import main.PushClient;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class ScreenGrab {

    private PushClient instance;
    private Point2D begin;
    private Point2D end;
    private int x;
    private int y;
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

        stage.setOpacity(.5);

        stage.setTitle("Push");
        stage.setResizable(false);
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

        final javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(result.getWidth(), result.getHeight());
        root.getChildren().add(canvas);
        final java.util.List<String> input = new ArrayList<>();


        mainScene.setOnKeyPressed(e -> {
            String code = e.getCode().toString();
            if (!input.contains(code)) {
                input.add(code);
            }
        });

        mainScene.setOnKeyReleased(e -> {
            String code = e.getCode().toString();
            input.remove(code);
        });

        mainScene.setOnMousePressed(event -> this.begin = new Point2D(event.getX(), event.getY()));

        mainScene.setOnMouseDragged(event -> {

            this.end = new Point2D(event.getX(), event.getY());

            final GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
            graphicsContext2D.setFill(new javafx.scene.paint.Color(1f, 1f, 1f, 1f));
            graphicsContext2D.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            width = end.getX() - begin.getX();
            height = end.getY() - begin.getY();

            if (width < 0) {
                width *= -1;
                begin = new Point2D(begin.getX() - width, begin.getY());
            }

            if (height < 0) {
                height *= -1;
                begin = new Point2D(begin.getX(), begin.getY() - height);
            }

            graphicsContext2D.setFill(javafx.scene.paint.Color.RED);
            graphicsContext2D.strokeRect(begin.getX(), begin.getY(), width, height);
            graphicsContext2D.fillRect(begin.getX(), begin.getY(), width, height);
        });

        mainScene.setOnMouseReleased(event -> captureImage());

        stage.show();


    }


    private void captureImage() {
        BufferedImage capture;

        stage.hide();
        try {
            capture = new Robot().createScreenCapture(new Rectangle(
                    (int) begin.getX(),
                    (int) begin.getY(),
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


    public void start(Stage primaryStage) throws Exception {

    }
}