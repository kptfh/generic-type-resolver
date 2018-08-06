package generic.typeresolver;

public class MethodWithParameters {

    public final TypeWithParameters[] argumentTypes;
    public final TypeWithParameters returnType;

    public MethodWithParameters(TypeWithParameters[] argumentTypes, TypeWithParameters returnType) {
        this.argumentTypes = argumentTypes;
        this.returnType = returnType;
    }
}
