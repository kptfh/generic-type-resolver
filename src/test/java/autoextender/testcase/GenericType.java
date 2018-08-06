package autoextender.testcase;

public class GenericType<L, R> {

    public final L left;
    public final Pair<L, R> straight;
    public final Pair<R, L> reverse;

    public GenericType(L left, R right) {
        this.left = left;
        this.straight = new Pair<>(left, right);
        this.reverse = new Pair<>(right, left);
    }
}
