package components;

import components.container.IComponent;
import components.container.input.InputType;
import components.container.scene.SceneManager;
import components.container.scene.ScreenShotScene;
import config.Config;
import helper.ScreenHelper;
import http.Upload;
import image.GammaCorrector;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
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
public class ScreenShotComponentMac implements IComponent {
    private static final Logger LOGGER = Logger.getLogger(ScreenShotComponentMac.class.getName());

    public ScreenShotComponentMac() {
        try {
            final File tmp = new java.io.File(System.getProperty("user.home") + "/.push/tmp.png");
            final Process process = Runtime.getRuntime().exec("/usr/sbin/screencapture -i " + tmp.getAbsolutePath());
            process.waitFor();
            final Config config = new Config();
            Upload.uploadTempContent(tmp, config.getProperties().getProperty("url"), config);
            System.exit(0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Scene getScene() {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean handle(InputType inputType, javafx.event.Event event) {
        return false;
    }
}
