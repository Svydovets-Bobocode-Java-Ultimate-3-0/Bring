package svydovets.web;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import svydovets.web.dto.RequestInfoHolder;
import svydovets.web.path.PathFinder;
import svydovets.web.path.PathFinderImpl;

public class DispatcherServlet extends HttpServlet {

    private final WebApplicationContext webApplicationContext;
    private final PathFinder pathFinder;

    public DispatcherServlet(String basePackage) {
        this.webApplicationContext = new AnnotationConfigWebApplicationContext(basePackage);
        this.pathFinder = new PathFinderImpl();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        config.getServletContext().setAttribute("WebApplicationContext", webApplicationContext);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);

        //TODO add processing ResponseEntity;
        //if (result instance of ResponseEntity){
        // fill(resp, result)
        // }

    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        super.service(req, res);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Set<String> patternPaths = webApplicationContext.getMethodPatterns(MethodNameEnum.GET);

        String foundPattern = pathFinder.find(req.getPathInfo(), patternPaths);

        RequestInfoHolder requestInfoHolder = webApplicationContext.getRequestInfoHolder(MethodNameEnum.GET, foundPattern);
        //todo: if this value is null, throws exception
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Set<String> patternPaths = webApplicationContext.getMethodPatterns(MethodNameEnum.POST);

        String foundPattern = pathFinder.find(req.getPathInfo(), patternPaths);

        RequestInfoHolder requestInfoHolder = webApplicationContext.getRequestInfoHolder(MethodNameEnum.POST, foundPattern);
        //todo: if this value is null, throws exception
    }

    @Override
    protected long getLastModified(HttpServletRequest req) {
        return super.getLastModified(req);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doHead(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doOptions(req, resp);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doTrace(req, resp);
    }
}
