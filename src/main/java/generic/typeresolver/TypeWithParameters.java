package generic.typeresolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;


/**
 * @author Sergii Karpenko
 */
public class TypeWithParameters {

    public final Type type;
    public final Type[] parameters;

    TypeWithParameters(Type type, Type[] parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    TypeWithParameters(Type type) {
        this.type = type;
        this.parameters = null;
    }

    @Override
    public String toString() {
        return type + "<" + Arrays.toString(parameters) + ">";
    }

}
