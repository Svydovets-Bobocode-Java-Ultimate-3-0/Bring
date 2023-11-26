package svydovets.web.path;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.exception.ExtractRequestBodyException;

import java.io.IOException;
import java.util.stream.Collectors;

import static jakarta.servlet.RequestDispatcher.INCLUDE_REQUEST_URI;
import static svydovets.util.ErrorMessageConstants.ERROR_READING_JSON_REQUEST_BODY;

public class RequestInfoCreator {

    private static final Logger log = LoggerFactory.getLogger(RequestInfoCreator.class);

    private static final RequestPathParser REQUEST_PATH_PARSER = new RequestPathParserImpl();

    public static RequestInfo create(HttpServletRequest req) {
        log.trace("Call create() by request");
        RequestInfo requestInfo = new RequestInfo(REQUEST_PATH_PARSER.parse(req.getPathInfo(),
                (String) req.getAttribute(INCLUDE_REQUEST_URI)), req.getParameterMap(), extractRequestBody(req));

        log.trace("Created requestInfo: {}", requestInfo);

        return requestInfo;
    }

    private static String extractRequestBody(HttpServletRequest request) {
        try {
            return request.getReader()
                    .lines()
                    .collect(Collectors.joining());
        } catch (IOException exception) {
            log.error(ERROR_READING_JSON_REQUEST_BODY);
            log.error(exception.getMessage());

            throw new ExtractRequestBodyException(ERROR_READING_JSON_REQUEST_BODY, exception);
        }
    }
}
