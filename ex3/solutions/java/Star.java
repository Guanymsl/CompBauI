import java.util.Map;

public class Star extends RegexTree {

    public final RegexTree r;

    public Star(RegexTree r) { this.r = r; }

    @Override
    public int assignIndex(int i, Map<Integer, Letter> m) {
        return r.assignIndex(i, m);
    }

    @Override
    public void compEmptyFirstLastAttr() {
        r.compEmptyFirstLastAttr();

        empty = true;
        first = r.first;
        last = r.last;
    }

    @Override
    public void compNextAttr() {
        r.next = r.first | next;

        r.compNextAttr();
    }

    @Override
    public String toString() { return "(" + r + ")*"; }
}

