package autoextender;


import autoextender.testcase.*;
import generic.typeresolver.GenericTypeResolver;
import generic.typeresolver.MethodWithParameters;
import generic.typeresolver.TypeWithParameters;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sergii Karpenko
 */
public class GenericTypeResolverTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldResolveFieldOfTypeVariable() throws NoSuchFieldException {
        GenericTypeResolver typeResolver = new GenericTypeResolver(TestPair.class);

        TypeWithParameters resolvedTypeLeft = typeResolver.resolveField(TestPair.class.getField("left"));

        assertThat(resolvedTypeLeft.type).isInstanceOfAny(TypeVariable.class);
        ParameterizedType parameterLeft = (ParameterizedType)resolvedTypeLeft.parameters[0];
        assertThat(parameterLeft.getRawType()).isEqualTo(Pair.class);
        assertThat(parameterLeft.getActualTypeArguments()).isEqualTo(new Type[]{Integer.class, String.class});

        TypeWithParameters resolvedTypeRight = typeResolver.resolveField(TestPair.class.getField("right"));

        assertThat(resolvedTypeRight.type).isInstanceOfAny(TypeVariable.class);
        ParameterizedType parameterRight = (ParameterizedType)resolvedTypeRight.parameters[0];
        assertThat(parameterRight.getRawType()).isEqualTo(List.class);
        assertThat(parameterRight.getActualTypeArguments()).isEqualTo(new Type[]{String.class});
    }

    @Test
    public void shouldResolveFieldOfParameterizedType() throws NoSuchFieldException {
        GenericTypeResolver typeResolver = new GenericTypeResolver(
                new GenericType<String, Integer>("test", 1){}.getClass());

        TypeWithParameters resolvedTypeLeft = typeResolver.resolveField(GenericType.class.getField("left"));

        assertThat(resolvedTypeLeft.type).isInstanceOfAny(TypeVariable.class);
        assertThat(resolvedTypeLeft.parameters).isEqualTo(new Type[]{String.class});

        TypeWithParameters resolvedTypeStraight = typeResolver.resolveField(GenericType.class.getField("straight"));

        assertThat(resolvedTypeStraight.type).isInstanceOfAny(ParameterizedType.class);
        assertThat(resolvedTypeStraight.parameters).isEqualTo(new Type[]{String.class, Integer.class});

        TypeWithParameters resolvedTypeReverse = typeResolver.resolveField(GenericType.class.getField("reverse"));

        assertThat(resolvedTypeReverse.type).isInstanceOfAny(ParameterizedType.class);
        assertThat(resolvedTypeReverse.parameters).isEqualTo(new Type[]{Integer.class, String.class});
    }

    @Test
    public void shouldResolveMethod() throws NoSuchMethodException {
        GenericTypeResolver typeResolver = new GenericTypeResolver(
                new GenericInterface<String, Integer>(){
                    @Override
                    public String left() { return null; }

                    @Override
                    public Integer mirror(String right) {
                        return null;
                    }

                    @Override
                    public Pair<String, Integer> pair() { return null;}
                }.getClass());

        MethodWithParameters resolvedTypeLeft = typeResolver.resolveMethod(
                GenericInterface.class.getMethod("left"));

        assertThat(resolvedTypeLeft.returnType.type).isInstanceOfAny(TypeVariable.class);
        assertThat(resolvedTypeLeft.returnType.parameters[0]).isEqualTo(String.class);

        MethodWithParameters resolvedTypePair = typeResolver.resolveMethod(
                GenericInterface.class.getMethod("pair"));

        ParameterizedType returnTypeMirror = (ParameterizedType) resolvedTypePair.returnType.type;
        assertThat(returnTypeMirror.getRawType()).isEqualTo(Pair.class);
        assertThat(resolvedTypePair.returnType.parameters)
                .isEqualTo(new Type[]{String.class, Integer.class});

        MethodWithParameters resolvedTypeMirror = typeResolver.resolveMethod(
                findMethod(GenericInterface.class, "mirror"));

        assertThat(resolvedTypeMirror.returnType.type).isInstanceOfAny(TypeVariable.class);
        assertThat(resolvedTypeMirror.returnType.parameters[0]).isEqualTo(Integer.class);
        assertThat(resolvedTypeMirror.argumentTypes[0].type).isInstanceOfAny(TypeVariable.class);
        assertThat(resolvedTypeMirror.argumentTypes[0].parameters[0]).isEqualTo(String.class);
    }

    @Test
    public void shouldResolveFieldsAndMethods() throws NoSuchMethodException, NoSuchFieldException {
        GenericTypeResolver typeResolver = new GenericTypeResolver(
                new AbstractType<String, Integer>(1, "test"){
                    @Override
                    public String left() { return null; }

                    @Override
                    public Integer mirror(String right) {
                        return null;
                    }

                    @Override
                    public Pair<String, Integer> pair() { return null;}
                }.getClass());

        TypeWithParameters resolvedTypeStraight = typeResolver.resolveField(AbstractType.class.getField("straight"));

        assertThat(resolvedTypeStraight.type).isInstanceOfAny(ParameterizedType.class);
        assertThat(resolvedTypeStraight.parameters).isEqualTo(new Type[]{Integer.class, String.class});

        TypeWithParameters resolvedTypeReverse = typeResolver.resolveField(AbstractType.class.getField("reverse"));

        assertThat(resolvedTypeReverse.type).isInstanceOfAny(ParameterizedType.class);
        assertThat(resolvedTypeReverse.parameters).isEqualTo(new Type[]{String.class, Integer.class});

        MethodWithParameters resolvedTypeLeft = typeResolver.resolveMethod(
                GenericInterface.class.getMethod("left"));

        assertThat(resolvedTypeLeft.returnType.type).isInstanceOfAny(TypeVariable.class);
        assertThat(resolvedTypeLeft.returnType.parameters[0]).isEqualTo(String.class);

        MethodWithParameters resolvedTypePair = typeResolver.resolveMethod(
                GenericInterface.class.getMethod("pair"));

        ParameterizedType returnTypePair = (ParameterizedType) resolvedTypePair.returnType.type;
        assertThat(returnTypePair.getRawType()).isEqualTo(Pair.class);
        assertThat(resolvedTypePair.returnType.parameters)
                .isEqualTo(new Type[]{String.class, Integer.class});

        MethodWithParameters resolvedTypeMirror = typeResolver.resolveMethod(
                findMethod(GenericInterface.class, "mirror"));

        assertThat(resolvedTypeMirror.returnType.type).isInstanceOfAny(TypeVariable.class);
        assertThat(resolvedTypeMirror.returnType.parameters[0]).isEqualTo(Integer.class);
        assertThat(resolvedTypeMirror.argumentTypes[0].type).isInstanceOfAny(TypeVariable.class);
        assertThat(resolvedTypeMirror.argumentTypes[0].parameters[0]).isEqualTo(String.class);
    }

    @Test
    public void shouldResolveFieldsAndMethodsOnNonGeneric() throws NoSuchMethodException, NoSuchFieldException {
        GenericTypeResolver typeResolver = new GenericTypeResolver(NonGenericType.class);

        TypeWithParameters resolvedTypeString = typeResolver.resolveField(NonGenericType.class.getField("string"));

        assertThat(resolvedTypeString.type).isEqualTo(String.class);
        assertThat(resolvedTypeString.parameters).isNull();

        TypeWithParameters resolvedTypePair = typeResolver.resolveField(NonGenericType.class.getField("pair"));

        assertThat(resolvedTypePair.type).isInstanceOfAny(ParameterizedType.class);
        assertThat(resolvedTypePair.parameters).isEqualTo(new Type[]{String.class, Integer.class});

        MethodWithParameters resolvedTypeReverse = typeResolver.resolveMethod(
                findMethod(NonGenericType.class, "reverse"));

        assertThat(resolvedTypeReverse.returnType.type).isInstanceOfAny(ParameterizedType.class);
        assertThat(resolvedTypeReverse.returnType.parameters).isEqualTo(new Type[]{Integer.class, String.class});
        assertThat(resolvedTypeReverse.argumentTypes[0].type).isInstanceOfAny(ParameterizedType.class);
        assertThat(resolvedTypeReverse.argumentTypes[0].parameters).isEqualTo(new Type[]{String.class, Integer.class});
    }

    private static Method findMethod(Class type, String name){
        return Stream.of(type.getMethods())
                .filter(method -> method.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
