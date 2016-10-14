package components;

import com.sun.org.apache.bcel.internal.generic.BREAKPOINT;
import components.container.IComponent;
import components.container.input.InputType;
import components.container.scene.DragDropScene;
import components.container.scene.SceneManager;
import config.Config;
import http.Upload;
import javafx.event.*;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.input.*;
import org.apache.log4j.Logger;

import java.awt.*;
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
    private final SceneManager sceneManager;

    public DragDropComponent(Config config, SceneManager sceneManager) {
        this.config = config;
        this.sceneManager = sceneManager;
    }

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

    private void handleDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }

    }

    @Override
    public Scene getScene() {
        return sceneManager.getScene(DragDropScene.class);
    }

    @Override
    public void handle(final InputType inputType, final Event event) {

        switch (inputType) {
            case DRAG_OVER:
                this.handleDragOver(((DragEvent) event));
                break;
            case DRAG_DROPPED:
                this.handleDragDropped(((DragEvent) event));
                break;
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
