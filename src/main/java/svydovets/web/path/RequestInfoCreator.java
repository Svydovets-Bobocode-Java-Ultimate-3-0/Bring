package svydovets.web.path;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.stream.Collectors;

import static jakarta.servlet.RequestDispatcher.INCLUDE_REQUEST_URI;

public class RequestInfoCreator {
    private static final RequestPathParser REQUEST_PATH_PARSER = new RequestPathParserImpl();

    public static RequestInfo create(HttpServletRequest req) {
        return new RequestInfo(
                REQUEST_PATH_PARSER.parse(req.getPathInfo(), (String) req.getAttribute(INCLUDE_REQUEST_URI)),
                req.getParameterMap(),
                extractRequestBody(req)
        );
    }

    private static String extractRequestBody(HttpServletRequest request) {
        try {
            return request.getReader()
                    .lines()
                    .collect(Collectors.joining());
        } catch (IOException e) {
            // todo: LOG ERROR
            throw new RuntimeException("Error reading JSON request body", e);
        }
    }
}
