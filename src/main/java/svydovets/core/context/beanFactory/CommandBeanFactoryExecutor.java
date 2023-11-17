/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package svydovets.core.context.beanFactory;

import java.util.Map;
import java.util.function.Function;

public class CommandBeanFactoryExecutor implements CommandBeanFactory {

    private BeanFactory beanFactory;

    public CommandBeanFactoryExecutor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public <T> Function<Class<? extends T>, T> getBeanCommand() {
        return beanFactory::getBean;
    }

    @Override
    public <T> Function<Class<? extends T>, Map<String, ? extends T>> getBeansOfTypeCommand() {
        return beanFactory::getBeansOfType;
    }
}