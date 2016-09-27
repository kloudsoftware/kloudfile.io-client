package http;



import config.Config;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;

import java.io.InputStream;

public class Upload {

    public static String uploadDataToServer(InputStream in, String fileName) {
        String key = Config.getProperties().getProperty("key");
        HttpPost httpPost = new HttpPost(Config.getProperties().getProperty("url"));
        return "";
    }
}
