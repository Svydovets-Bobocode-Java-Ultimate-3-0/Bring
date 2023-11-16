package svydovets.core.context.injector;

public class BeanInjector extends AbstractInjector {

    @Override
    public void inject(InjectorConfig config) {
        var autowireCandidateType = config.getBeanField().getType();

        Object autowireCandidate = config.getBeanReceiver().apply(autowireCandidateType);

        setDependency(config.getBean(), config.getBeanField(), autowireCandidate);
    }

}
