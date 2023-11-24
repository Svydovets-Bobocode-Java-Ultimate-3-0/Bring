package svydovets.web.dto;

import jakarta.annotation.Nullable;

import java.util.Map;

public class ResponseEntity<T>{

    private final T body;
    private HttpStatus status;
    private Map<String, String> headers;

    public ResponseEntity(T body) {
        this.body = body;
    }

    public T getBody() {
        return body;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public static BodyBuilder ok() {
        return status(HttpStatus.OK);
    }

    public static BodyBuilder status(int status) {
        return new DefaultBuilder(status);
    }

    public static BodyBuilder status(HttpStatus status) {
        return new DefaultBuilder(status);
    }

    public interface HeadersBuilder{
        BodyBuilder header(String headerName, String headerValues);
    }

    public interface BodyBuilder extends HeadersBuilder{
        BodyBuilder contentLength(String type);
        BodyBuilder contentType(MediaType contentType);
        <T> ResponseEntity<T> body(@Nullable T body);
        <T> ResponseEntity<T> build();

    }

    private static class DefaultBuilder implements BodyBuilder {

        private final int statusCode;
        private final HttpHeaders headers = new HttpHeaders();

        private DefaultBuilder(int statusCode) {
            this.statusCode = statusCode;
        }

        private DefaultBuilder(HttpStatus httpStatus) {
            this.statusCode = httpStatus.getStatus();
        }

        public BodyBuilder header(String headerName, String headerValue) {
              headers.setHeader(headerName, headerValue);
              return this;
            }

            public <T> ResponseEntity<T> build() {
                return body(null);
            }

            @Override
            public BodyBuilder contentLength(String type) {
                return this;
            }

            @Override
            public BodyBuilder contentType(MediaType contentType) {
                return this;
            }

            public <T> ResponseEntity<T> body(@Nullable T body) {
                return new ResponseEntity<>(body);
            }

        }
}
