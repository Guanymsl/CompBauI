import java.util.Map;

public class Concat extends RegexTree {

    public final RegexTree left;
    public final RegexTree right;

    public Concat(RegexTree l, RegexTree r) {
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

        empty = left.empty && right.empty;
        first = left.first | (left.empty ? right.first : 0);
        last = right.last | (right.empty ? left.last : 0);
    }

    @Override
    public void compNextAttr() {
        left.next = right.first | (right.empty ? next : 0);
        right.next = next;

        left.compNextAttr();
        right.compNextAttr();
    }

    @Override
    public String toString() { return "(" + left + right + ")"; }
}
