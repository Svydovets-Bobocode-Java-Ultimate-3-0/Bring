/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package svydovets.core.context.beanFactory.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CommandFactory {

    private Map<CommandFactoryEnum, Function<Class<?>, ?>> commands = new HashMap<>();

    public void registryCommand(CommandFactoryEnum key, Function<Class<?>, ?> command){
        commands.put(key, command);
    }

    public Function<Class<?>, ?> execute(CommandFactoryEnum key){
        return commands.get(key);
    }

}