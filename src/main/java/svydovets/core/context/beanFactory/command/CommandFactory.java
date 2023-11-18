package svydovets.core.context.beanFactory.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CommandFactory {

    private Map<CommandFunctionName, Function<Class<?>, Object>> commands = new HashMap<>();

    public void registryCommand(CommandFunctionName key, Function<Class<?>, Object> command){
        commands.put(key, command);
    }

    public Function<Class<?>, Object> execute(CommandFunctionName key){
        return commands.get(key);
    }

}