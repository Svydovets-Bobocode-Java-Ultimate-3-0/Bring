package svydovets.core.enums;

public enum ScopeEnum {
    SCOPE_SINGLETON("singleton"),
    SCOPE_PROTOTYPE("prototype");

    ScopeEnum(String value) {
        this.value = value;
    }

    private String value;

    public String value() {
        return value;
    }
}
