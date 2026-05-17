import java.util.Map;

public class Or extends RegexTree {

    public final RegexTree left;
    public final RegexTree right;

    public Or(RegexTree l, RegexTree r) {
        left = l;
        right = r;
    }

    @Override
    public int assignIndex(int i, Map<Integer, Letter> m) {
        return right.assignIndex(left.assignIndex(i, m), m);
    }

    @Override
    public void compEmptyFirstLastAttr() {
        left.compEmptyFirstLastAttr();
        right.compEmptyFirstLastAttr();

        empty = left.empty || right.empty;
        first = left.first | right.first;
        last = left.last | right.last;
    }

    @Override
    public void compNextAttr() {
        left.next = next;
        right.next = next;

        left.compNextAttr();
        right.compNextAttr();
    }

    @Override
    public String toString() { return "(" + left + "|" + right + ")"; }
}

