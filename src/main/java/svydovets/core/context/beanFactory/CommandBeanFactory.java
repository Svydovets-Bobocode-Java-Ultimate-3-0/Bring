/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package svydovets.core.context.beanFactory;

import java.util.Map;
import java.util.function.Function;

public interface CommandBeanFactory {

    <T> Function<Class<? extends T>, T> getBeanCommand();
    <T> Function<Class<? extends T>, Map<String, ? extends T>> getBeansOfTypeCommand();

}