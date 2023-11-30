package svydovets.web.dto;

public class ResponseEntity<T> {

    private final T body;
    private HttpStatus status;
    private HttpHeaders headers;

    public ResponseEntity(T body) {
        this.body = body;
    }

    public ResponseEntity(T body, HttpHeaders headers, HttpStatus status) {
        this.body = body;
        this.headers = headers;
        this.status = status;
    }

    public T getBody() {
        return body;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public static BodyBuilder ok() {
        return status(HttpStatus.OK);
    }

    public static BodyBuilder status(HttpStatus status) {
        return new DefaultBuilder(status);
    }

    public interface HeadersBuilder {
        BodyBuilder header(String headerName, String headerValues);
    }

    public interface BodyBuilder extends HeadersBuilder {
        BodyBuilder contentLength(int length);
        BodyBuilder contentType(String contentType);
        <T> ResponseEntity<T> body(T body);
        <T> ResponseEntity<T> build();

    }

    private static class DefaultBuilder implements BodyBuilder {

        private final HttpStatus status;
        private final HttpHeaders headers = new HttpHeaders();

        private DefaultBuilder(HttpStatus status) {
            this.status = status;
        }

        public BodyBuilder header(String headerName, String headerValue) {
              headers.setHeader(headerName, headerValue);
              return this;
            }

            public <T> ResponseEntity<T> build() {
                return body(null);
            }

            @Override
            public BodyBuilder contentLength(int length) {
                headers.setHeader("Content-Length", String.valueOf(length));
                return this;
            }

            @Override
            public BodyBuilder contentType(String contentType) {
                headers.setHeader("Content-Type", contentType);
                return this;
            }

            public <T> ResponseEntity<T> body(T body) {
                return new ResponseEntity<>(body, this.headers, this.status);
            }
        }
}
