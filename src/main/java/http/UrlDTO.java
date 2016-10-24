package http;

/**
 * Created by fred on 10/23/2016.
 */
public class UrlDTO {
    private String fileViewUrl;
    private String fileDeleteUrl;

    public UrlDTO(String fileViewUrl, String fileDeleteUrl) {
        this.fileViewUrl = fileViewUrl;
        this.fileDeleteUrl = fileDeleteUrl;
    }

    public String getFileViewUrl() {
        return fileViewUrl;
    }

    public void setFileViewUrl(String fileViewUrl) {
        this.fileViewUrl = fileViewUrl;
    }

    public String getFileDeleteUrl() {
        return fileDeleteUrl;
    }

    public void setFileDeleteUrl(String fileDeleteUrl) {
        this.fileDeleteUrl = fileDeleteUrl;
    }
}
