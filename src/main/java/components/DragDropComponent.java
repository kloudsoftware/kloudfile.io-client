package components;

import config.Config;
import http.Upload;
import javafx.scene.input.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by fr3d63 on 11/10/16.
 */
public class DragDropComponent implements IComponent {
    private static final Logger LOGGER = Logger.getLogger(DragDropComponent.class.getName());

    private final Config config;

    public DragDropComponent(Config config) {
        this.config = config;
    }

    @Override
    public void handleMouseReleased(MouseEvent event) {

    }

    @Override
    public void handleKeyPressed(KeyEvent event) {

    }

    @Override
    public void handleMouseDragged(MouseEvent event) {

    }

    @Override
    public void handleMousePressed(MouseEvent event) {

    }

    @Override
    public void handleDragDropped(DragEvent event) {
        LOGGER.info("file drag dropped");
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            success = true;
            if (db.getFiles().size() == 1) {
                uploadSingeFileFromDragBoard(db);
            } else {
                try {
                    uploadMultipleFilesFromDragboard(db);
                } catch (IOException e) {
                    //showError(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }
        event.setDropCompleted(success);
        event.consume();

        System.exit(0);
    }

    @Override
    public void handleDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }

    }

    private void uploadMultipleFilesFromDragboard(Dragboard db) throws IOException {
        String path = System.getProperty("user.home")
                + "/.push/"
                + System.currentTimeMillis() + "archive.zip";
        try (FileOutputStream fout = new FileOutputStream(path)) {

            ZipOutputStream zout = new ZipOutputStream(fout);
            byte[] buffer = new byte[1024];

            for (File file : db.getFiles()) {
                FileInputStream fsin = new FileInputStream(file);
                zout.putNextEntry(new ZipEntry(file.getName()));

                int len;

                while ((len = fsin.read(buffer)) > 0) {
                    zout.write(buffer, 0, len);
                }

                zout.closeEntry();
                fsin.close();
            }
            zout.close();

            Upload.uploadTempContent(new File(path), config.getProperties().getProperty("url"), config);

        }
    }

    private void uploadSingeFileFromDragBoard(Dragboard db) {
        String filePath;
        filePath = db.getFiles().get(0).getAbsolutePath();
        try {
            Upload.uploadFile(new File(filePath), config.getProperties().getProperty("url"), config);
        } catch (IOException e) {
           // showError(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

}
