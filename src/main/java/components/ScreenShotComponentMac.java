package components;

import components.container.interfaces.IComponent;
import components.container.input.InputType;
import config.Config;
import http.Upload;
import javafx.scene.Scene;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

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
