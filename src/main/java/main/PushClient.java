package main;


import config.Config;
import image.ScreenGrab;
import javafx.application.Application;
import javafx.stage.Stage;

public class PushClient extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        PushClient pushClient = new PushClient();
        Config config = new Config();
        ScreenGrab screenGrab = new ScreenGrab(config, primaryStage);

        screenGrab.start();
    }
}
