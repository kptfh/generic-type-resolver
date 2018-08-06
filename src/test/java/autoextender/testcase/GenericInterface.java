package autoextender.testcase;

public interface GenericInterface<L, R> {

    L left();

    R mirror(L right);

    Pair<L, R> pair();

}
