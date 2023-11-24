package svydovets.web.dto;

public class RequestInfoHolder {

  // todo: Rewrite to save Class<?> classType instead String className
  private Class<?> classType;
  private String className;

  private String methodName;

  private String[] parameterNames;

  private Class<?>[] parameterTypes;

  public RequestInfoHolder(String className, Class<?> classType) {
    this.className = className;
    this.classType = classType;
  }

  public RequestInfoHolder(String className) {
    this.className = className;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public String[] getParameterNames() {
    return parameterNames;
  }

  public void setParameterNames(String[] parameterNames) {
    this.parameterNames = parameterNames;
  }

  public Class<?>[] getParameterTypes() {
    return parameterTypes;
  }

  public void setParameterTypes(Class<?>[] parameterTypes) {
    this.parameterTypes = parameterTypes;
  }

  public Class<?> getClassType() {
    return classType;
  }

  public void setClassType(Class<?> classType) {
    this.classType = classType;
  }
}
