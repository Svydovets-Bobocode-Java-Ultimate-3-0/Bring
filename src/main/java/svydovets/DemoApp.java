package svydovets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoApp {
    private static final Logger log = LoggerFactory.getLogger(DemoApp.class);

    public static void main(String[] args) {
        var context = BringApplication.run(DemoApp.class);
    }
}