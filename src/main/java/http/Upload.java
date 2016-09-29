package http;


import config.Config;
import main.PushClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Upload {

    public static final String POST = "/post";

    public static void uploadDataToServer(File file, String target, Config config) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String key = config.getProperties().getProperty("key");
        HttpPost httpPost = new HttpPost(target + POST);
        StringBody keyBody = new StringBody(key, ContentType.TEXT_PLAIN);
        HttpEntity httpEntity = MultipartEntityBuilder.create().addPart("file", new FileBody(file)).addPart("key", keyBody).build();
        httpPost.setEntity(httpEntity);
        try {
            HttpEntity response = httpClient.execute(httpPost).getEntity();
            java.util.Scanner s = new java.util.Scanner(response.getContent()).useDelimiter("\\A");
            String url = s.hasNext() ? s.next() : "Empty response";
            StringSelection stringSelection = new StringSelection(target + url);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, stringSelection);
            response.getContent().close();
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
