import java.util.Map;

public class Epsilon extends RegexTree {

    public static final Epsilon instance = new Epsilon();

    private Epsilon() { }

    @Override
    public int assignIndex(int i, Map<Integer, Letter> m) {
        return i;
    }

    @Override
    public void compEmptyFirstLastAttr() {
        empty = true;
        first = 0;
        last = 0;
    }

    @Override
    public void compNextAttr() { }

    @Override
    public String toString() { return "ε"; }
}
