package http;


import config.Config;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Upload {

    public static String uploadDataToServer(File file) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String key = Config.getProperties().getProperty("key");
        HttpPost httpPost = new HttpPost(Config.getProperties().getProperty("url"));
        StringBody keyBody = new StringBody(key, ContentType.TEXT_PLAIN);
        HttpEntity httpEntity = MultipartEntityBuilder.create().addPart("file", new FileBody(file)).addPart("key", keyBody).build();
        httpPost.setEntity(httpEntity);
        try {
            HttpEntity response = httpClient.execute(httpPost).getEntity();
            java.util.Scanner s = new java.util.Scanner(response.getContent()).useDelimiter("\\A");
            System.out.println(s.hasNext() ? s.next() : "Empty response");
            response.getContent().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
