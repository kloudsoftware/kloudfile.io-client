package main;


import config.Config;
import http.HttpQueue;
import http.Upload;
import image.ScreenGrab;
import javafx.application.Application;
import javafx.stage.Stage;


public class PushClient extends Application {

    private HttpQueue queue = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        queue = new HttpQueue();
        Upload upload = new Upload(queue);

        Config config = new Config();
        ScreenGrab screenGrab = new ScreenGrab(config, primaryStage, upload);
        screenGrab.start();
    }
}
