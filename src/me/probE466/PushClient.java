package me.probE466;

import me.probE466.config.Config;
import me.probE466.image.ScreenGrab;

/**
 * Created by larsg on 26.09.2016.
 */
public class PushClient {

    private int offset;
    public static void main(String[] args) {
        PushClient pushClient = new PushClient();
        Config config = new Config();
        pushClient.offset = config.queryConfig();
        System.out.println(pushClient.offset);

        ScreenGrab screenGrab = new ScreenGrab(pushClient);
        screenGrab.getPartOfScreen();
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
