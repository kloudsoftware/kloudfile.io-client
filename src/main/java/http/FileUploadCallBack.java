package http;

import com.google.gson.Gson;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.http.HttpResponse;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.function.Consumer;

@Log4j
@RequiredArgsConstructor
public class FileUploadCallBack implements Consumer<HttpResponse> {

    private final String target;
    private final boolean isTemp;
    private File file;

    public FileUploadCallBack(String target, File file, boolean isTemp) {
        this(target, isTemp);
        this.file = file;
    }

    private static UrlDTO parseResponse(HttpResponse response) throws IOException {
        final Gson gson = new Gson();

        @Cleanup final Scanner s = new Scanner(response.getEntity().getContent()).useDelimiter("\\A");
        if (!s.hasNext()) {
            return null;
        }

        final String jsonString = s.next();
        s.close();
        return gson.fromJson(jsonString, UrlDTO.class);
    }

    @Override
    public void accept(HttpResponse response) {
        UrlDTO urlDTO = null;
        if (response.getStatusLine().getStatusCode() == 200) {
            try {
                urlDTO = parseResponse(response);
            } catch (IOException e) {
                log.error(e);
            }
            StringSelection stringSelection;
            if (urlDTO != null) {
                stringSelection = new StringSelection(target + urlDTO.getFileViewUrl());
            } else {
                return;
            }
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, stringSelection);
            log.info("Pasted link to clipboard: " + target + urlDTO.getFileViewUrl());
            log.info("Delete link: " + target + urlDTO.getFileDeleteUrl());
        } else {
            log.error("Statuscode: " + response.getStatusLine().getStatusCode());
        }

        if(isTemp) {
            if (!file.delete()) {
                log.error("Cannot delete Temp file");
            }
        }
    }
}
