package http;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.io.IOException;

@RequiredArgsConstructor
@Log4j
public class UploadThread implements Runnable {
    private final HttpEvent httpEvent;

    @Override
    public void run() {
        try {
            httpEvent.getCallBack().accept(Upload.uploadDataToServer(httpEvent.getHttpRequest()));
        } catch (IOException e) {
            log.error(e);
        }

    }
}
