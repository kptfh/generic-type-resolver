package autoextender.testcase;

public abstract class AbstractType<L, R> extends GenericType<R, L> implements GenericInterface<L, R>{

    public AbstractType(R left, L right) {
        super(left, right);
    }
}
