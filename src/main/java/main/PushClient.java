package main;


import config.Config;
import image.ScreenGrab;
import javafx.application.Application;
import javafx.stage.Stage;

public class PushClient extends Application {

    private int offset;


    public static void main(String[] args) {
        launch(args);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        PushClient pushClient = new PushClient();
        Config config = new Config();
        pushClient.offset = config.queryConfig();
        System.out.println(pushClient.offset);
        ScreenGrab screenGrab = new ScreenGrab(pushClient, config);
        screenGrab.getPartOfScreen(primaryStage);
    }
}
