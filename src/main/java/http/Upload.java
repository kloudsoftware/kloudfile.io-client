package http;


import config.Config;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;

public class Upload {

    private static final Logger LOGGER = Logger.getLogger(Upload.class.getName());

    public static final String POST = "/post";

    public static void uploadTempContent(final File file, final String target, final Config config) throws IOException {
        LOGGER.info("Uploading Temp Content");
        uploadDataToServer(file, target, config);
        file.delete();
    }

    public static void uploadFile(final File file, final String target, final Config config) throws IOException {
        LOGGER.info("Uploading File");
        uploadDataToServer(file, target, config);
    }

    private static void uploadDataToServer(File file, String target, final Config config) throws IOException {
        LOGGER.info("Uploading Data. File: " + file.getName() + " Target: " + target);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String key = config.getProperties().getProperty("key");
        HttpPost httpPost = new HttpPost(target + POST);
        StringBody keyBody = new StringBody(key, ContentType.TEXT_PLAIN);
        HttpEntity httpEntity = MultipartEntityBuilder.create().addPart("file", new FileBody(file)).addPart("key", keyBody).build();
        httpPost.setEntity(httpEntity);
        HttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200) {
            java.util.Scanner s = new java.util.Scanner(response.getEntity().getContent()).useDelimiter("\\A");
            String url = s.hasNext() ? s.next() : "Empty response";
            StringSelection stringSelection = new StringSelection(target + url);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, stringSelection);
            LOGGER.info("Pasted link to clipboard: " + target + url);
            response.getEntity().getContent().close();
        } else {
            throw new IOException("Statuscode: " + response.getStatusLine().getStatusCode());
        }
    }
}
