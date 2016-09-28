package image;

import http.Upload;
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
import java.io.*;

public class ScreenGrab {

    private static String OS = System.getProperty("os.name").toLowerCase();
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

    public void getPartOfScreen(Stage primaryStage) {
        this.stage = primaryStage;

        stage.setX(instance.getOffset());
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

        Rectangle2D result = new Rectangle2D.Double();
        GraphicsEnvironment localGE = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice gd : localGE.getScreenDevices()) {
            for (GraphicsConfiguration graphicsConfiguration : gd.getConfigurations()) {
                Rectangle2D.union(result, graphicsConfiguration.getBounds(), result);
            }
        }

        final Canvas canvas = new Canvas(result.getWidth(), result.getHeight());
        root.getChildren().add(canvas);
        stage.show();


        mainScene.setOnMousePressed(event -> this.begin = new Point2D(event.getX(), event.getY()));

        mainScene.setOnMouseDragged(event -> {
            this.end = new Point2D(event.getX(), event.getY());

            final GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
            graphicsContext2D.setFill(new javafx.scene.paint.Color(1f, 1f, 1f, 0f));
            graphicsContext2D.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            width = Math.abs(end.getX() - begin.getX());
            height = Math.abs(end.getY() - begin.getY());
            graphicsContext2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            Point2D start = calculateStartPoint();

            graphicsContext2D.setFill(javafx.scene.paint.Color.GRAY);
            graphicsContext2D.strokeRect(start.getX(), start.getY(), width, height);
            graphicsContext2D.fillRect(start.getX(), start.getY(), width, height);


        });

        mainScene.setOnMouseReleased(event -> {

//            stage.setOpacity(0);
            stage.hide();
            stage.close();
            GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            Point2D start = calculateStartPoint();
            graphicsContext.clearRect(start.getX(), start.getY(), width, height);
            capturePartialImage();

        });

//        mainScene.setOnKeyPressed(event -> {
//            if (width > 0 && height > 0) {
//                if (event.getCode().toString().equals("ENTER")) {
//                    capturePartialImage();
//                }
//            }
//        });

    }

    private BufferedImage gammaCorrection(BufferedImage original, double gamma) {

        int alpha, red, green, blue;
        int newPixel;

        double gamma_new = 1 / gamma;
        int[] gamma_LUT = gamma_LUT(gamma_new);

        BufferedImage gamma_cor = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        for (int i = 0; i < original.getWidth(); i++) {
            for (int j = 0; j < original.getHeight(); j++) {

                // Get pixels by R, G, B
                alpha = new Color(original.getRGB(i, j)).getAlpha();
                red = new Color(original.getRGB(i, j)).getRed();
                green = new Color(original.getRGB(i, j)).getGreen();
                blue = new Color(original.getRGB(i, j)).getBlue();

                red = gamma_LUT[red];
                green = gamma_LUT[green];
                blue = gamma_LUT[blue];

                // Return back to original format
                newPixel = colorToRGB(alpha, red, green, blue);

                // Write pixels into image
                gamma_cor.setRGB(i, j, newPixel);

            }

        }

        return gamma_cor;

    }

    // Create the gamma correction lookup table
    private int[] gamma_LUT(double gamma_new) {
        int[] gamma_LUT = new int[256];

        for (int i = 0; i < gamma_LUT.length; i++) {
            gamma_LUT[i] = (int) (255 * (Math.pow((double) i / (double) 255, gamma_new)));
        }

        return gamma_LUT;
    }

    // Convert R, G, B, Alpha to standard 8 bit
    private int colorToRGB(int alpha, int red, int green, int blue) {

        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;

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


    private void capturePartialImage() {

        BufferedImage capture;

        try {

            Point2D start = calculateStartPoint();
            capture = new Robot().createScreenCapture(new Rectangle(
                    (int) start.getX() + instance.getOffset(),
                    (int) start.getY(),
                    (int) width,
                    (int) height
            ));
            if (isMac()) {
                capture = gammaCorrection(capture, 1.134);
            }
            File imageFile = new File("image.png");
            ImageIO.write(capture, "png", imageFile);
            // TODO: 9/28/2016 Switched to saving to file for debug
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(capture, "png ", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            Upload.uploadDataToServer(imageFile);
        } catch (AWTException | IOException ex) {
            ex.printStackTrace();
        }

        System.exit(0);


    }
}