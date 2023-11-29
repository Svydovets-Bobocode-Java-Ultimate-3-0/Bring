package svydovets.web.dto;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private Map<String, String > headers;

    public HttpHeaders() {
        this.headers = new HashMap<>();
    }

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
    }
}
