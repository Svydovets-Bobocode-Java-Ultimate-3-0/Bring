package svydovets.core.context.beanFactory.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CommandFactory {

    private static final Logger log = LoggerFactory.getLogger(CommandFactory.class);

    private Map<CommandFunctionName, Function<Class<?>, Object>> commands = new HashMap<>();

    public void registryCommand(CommandFunctionName key, Function<Class<?>, Object> command) {
        log.trace("Call registryCommand({}, {})", key, command);
        commands.put(key, command);
    }

    public Function<Class<?>, Object> execute(CommandFunctionName key) {
        log.trace("Call execute({})", key);

        return commands.get(key);
    }

}