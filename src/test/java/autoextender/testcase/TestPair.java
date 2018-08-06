package autoextender.testcase;

import java.util.List;

public class TestPair extends Pair<Pair<Integer, String>, List<String>>{

    public TestPair(Pair<Integer, String> left, List<String> right) {
        super(left, right);
    }

}
