package http;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by fred on 10/23/2016.
 */
@Data
@AllArgsConstructor
public class UrlDTO {
    private String fileViewUrl;
    private String fileDeleteUrl;
}
