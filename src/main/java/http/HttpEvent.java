package http;

import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.util.function.Consumer;

@Data
public class HttpEvent {
    private final HttpEntity httpRequest;
    private final Consumer<HttpResponse> callBack;
}
