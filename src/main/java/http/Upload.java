package http;


import config.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Log4j
@RequiredArgsConstructor
public class Upload {

    private final HttpQueue queue;

    public static final String POST = "/api/post";

    public void uploadTempContent(final File file) throws IOException {
        log.info("Uploading Temp Content");
        queue.addToQueue(new HttpEvent(prepareEntity(file),
                new FileUploadCallBack(Config.getInstance().getProperties().getProperty("url"), file, true)));
    }

    public void uploadFile(final File file) throws IOException {
        log.info("Uploading File");
        queue.addToQueue(new HttpEvent(prepareEntity(file),
                new FileUploadCallBack(Config.getInstance().getProperties().getProperty("url"), false)));
    }

    private static HttpEntity prepareEntity(final File file) {
        String key = Config.getInstance().getProperties().getProperty("key");
        StringBody keyBody = new StringBody(key, ContentType.TEXT_PLAIN);

        return MultipartEntityBuilder.create().addPart("file", new FileBody(file)).addPart("key", keyBody).build();
    }

    public void signalFinished() {
        queue.waitAndStop();
    }

    static HttpResponse uploadDataToServer(HttpEntity httpEntity) throws IOException {
        String target = Config.getInstance().getProperties().getProperty("url");
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, (TrustStrategy) (arg0, arg1) -> true).build()).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }

        HttpPost httpPost = new HttpPost(target + POST);
        httpPost.setEntity(httpEntity);
        HttpResponse response = httpClient.execute(httpPost);

        return response;
    }

    public void start() {
        queue.start();
    }
}
