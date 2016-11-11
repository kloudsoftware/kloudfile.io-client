package image;

import components.ScreenShotComponent;
import components.ScreenShotComponentMac;
import components.container.ComponentContainer;
import components.container.input.InputManager;
import components.container.input.InputType;
import components.container.scene.SceneManager;
import config.Config;
import helper.ScreenHelper;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

public class ScreenGrab {

    private static final Logger LOGGER = Logger.getLogger(ScreenGrab.class.getName());
    private final Config config;
    private final Stage stage;
    private final SceneManager sceneManager;
    private final InputManager inputManager;
    private final ComponentContainer componentContainer;

    public ScreenGrab(final Config config, final Stage stage) {
        this.config = config;
        this.stage = stage;
        inputManager = new InputManager();
        componentContainer = new ComponentContainer(inputManager);
        sceneManager = new SceneManager(config, stage, inputManager);
        componentContainer.add( isMac() ? new ScreenShotComponentMac() :
                new ScreenShotComponent(stage, config, ScreenHelper.getScreens(), sceneManager), InputType.KEY_PRESSED, InputType.MOUSE_DRAGGED, InputType.MOUSE_RELEASED, InputType.MOUSE_PRESSED);
    }

    public void start() {
    }


    private void setUpDragDropScene() {
    }

    private void showError(final String error) {
        LOGGER.fatal(error);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(error);

        alert.showAndWait();
    }


    public static final String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isMac() {
        return (OS.contains("mac") || OS.contains("darvin"));
    }


}