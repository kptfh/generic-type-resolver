package generic.typeresolver;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

/**
 * @author Sergii Karpenko
 */
public class GenericTypeResolver {

    private Map<Type, Type> typeMap;

    public GenericTypeResolver(Type type){
        typeMap = buildTypeMap(type);
    }

    /**
     * Resolves generic field with type arguments
     * @param field
     * @return
     */
    public TypeWithParameters resolveField(Field field) {
        Type fieldType = field.getGenericType();
        return resolveTypeParameters(fieldType, typeMap);
    }

    /**
     * Resolves generic method with type arguments
     * @param method
     * @return
     */
    public MethodWithParameters resolveMethod(Method method) {
        return new MethodWithParameters(
                Stream.of(method.getGenericParameterTypes())
                        .map(type -> resolveTypeParameters(type, typeMap))
                        .toArray(TypeWithParameters[]::new),
                resolveTypeParameters(method.getGenericReturnType(), typeMap)
        );
    }

    private static Map<Type, Type> buildTypeMap(Type type) {
        if (type instanceof Class) {
            Class classType = (Class) type;
            Map<Type, Type> typeMap = new HashMap<>();

            while(classType != null) {
                Type genericSuperclass = classType.getGenericSuperclass();
                if (genericSuperclass instanceof ParameterizedType) {
                    fillTypeMap(typeMap, (ParameterizedType) genericSuperclass);
                }

                for (Type genericInterface : classType.getGenericInterfaces()) {
                    if (genericInterface instanceof ParameterizedType) {
                        fillTypeMap(typeMap, (ParameterizedType) genericInterface);
                    }
                }
                classType = classType.getSuperclass();
            }

            return typeMap;
        } else {
            ParameterizedType pType = (ParameterizedType) type;

            return buildTypeMap(((Class<?>) pType.getRawType()).getTypeParameters(),
                    pType.getActualTypeArguments());
        }
    }

    private static TypeWithParameters resolveTypeParameters(Type type, Map<Type, Type> typeMap) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Type[] parameters = stream(pType.getActualTypeArguments())
                    .map(type1 -> typeMap.getOrDefault(type1, type1))
                    .toArray(Type[]::new);
            return new TypeWithParameters(pType, parameters);
        }
        else if (type instanceof TypeVariable) {
            return new TypeWithParameters(type, new Type[]{typeMap.getOrDefault(type, type)});
        }
        else {
            return new TypeWithParameters(type);
        }
    }

    private static void fillTypeMap(Map<Type, Type> typeMap, ParameterizedType parameterizedType) {
        Type[] typeParameter = ((Class<?>) parameterizedType.getRawType()).getTypeParameters();
        Type[] actualTypeArgument = parameterizedType.getActualTypeArguments();

        fillTypeMap(typeMap, typeParameter, actualTypeArgument);
    }

    private static Map<Type, Type> buildTypeMap(Type[] typeParameter, Type[] typeArgument) {
        Map<Type, Type> typeMap = new HashMap<>(typeParameter.length);
        fillTypeMap(typeMap, typeParameter, typeArgument);
        return typeMap;
    }

    private static void fillTypeMap(Map<Type, Type> typeMap, Type[] typeParameter, Type[] typeArgument) {
        for (int i = 0; i < typeParameter.length; i++) {
            if (typeMap.containsKey(typeArgument[i])) {
                typeArgument[i] = typeMap.get(typeArgument[i]);
            }
            typeMap.put(typeParameter[i], typeArgument[i]);
        }
    }

}
