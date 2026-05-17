import java.util.Map;

public class Letter extends RegexTree {

    final int x; // codePoint of some character
    int i;

    public Letter(int l) { x = l; }

    @Override
    public int assignIndex(int i, Map<Integer, Letter> m) {
        this.i = i;
        m.put(i, this);
        return ++i;
    }

    @Override
    public void compEmptyFirstLastAttr() {
        empty = false;
        first = last = 1 << i;
    }

    @Override
    public void compNextAttr() { }

    public Letter setIndex(int i) {
        this.i = i;
        return this;
    }

    public Letter setNext(int next) {
        this.next = next;
        return this;
    }

    @Override
    public String toString() {
        return new String(Character.toChars(x));
    }
}
