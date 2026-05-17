import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.PrintStream;
import java.io.IOException;

public abstract class RegexTree {

    boolean empty;
    // you should use java.util.BitSet instead of int;
    // we only make use of int in order to demonstrate what a BitSet basically does;
    // the advantage of a BitSet is that it automagically enlarges its size on demand,
    // i.e., it's not limited to 32 Bits like an int
    // alternatively you can use java.math.BigInteger which has the advantage of beeing immutable :-)
    int first;
    int next;
    int last;

    public static Epsilon epsilon() { return Epsilon.instance; }
    public static Letter letter(int l) { return new Letter(l); }
    public static Concat concat(RegexTree l, RegexTree r) { return new Concat(l, r); }
    public static Or or(RegexTree l, RegexTree r) { return new Or(l, r); }
    public static Star star(RegexTree regex) { return new Star(regex); }

    /**
     * Postorder DFS traversal, assigning an id to each Letter while registering them in a Map
     */
    abstract int assignIndex(int i, Map<Integer, Letter> m);
    /**
     * Postorder DFS traversal, computing Empty, First and Last at the same time
     */
    abstract void compEmptyFirstLastAttr();
    /**
     * Preorder DFS traversal, computing Next
     */
    abstract void compNextAttr();

    /**
     * Fancy solution with Java streams and closures
     */
    public String getNFA(PrintStream out) {
        final Map<Integer, Letter> indexToLetter = new HashMap<>();
        final BiConsumer<Integer, IntConsumer> biterator = (i, f) -> BitSet.valueOf(new long[]{i}).stream().forEach(f);
        final BiConsumer<Integer, Integer> genDotEdge =
                (i, j) -> out.append(String.format("\t%d -> %d [label=\"%s\"];%n", i, j, new String(Character.toChars(indexToLetter.get(j).x))));

        assignIndex(1, indexToLetter);
        compEmptyFirstLastAttr();
        compNextAttr();
        final int allStates = indexToLetter.keySet().stream().map(i -> 1 << i).reduce(0, (x, y) -> x | y);

        out.append("digraph nfa {\n\trankdir=LR;\n\tsize=\"8,5\"\n\tnode [shape=none,width=0,height=0,margin=0]; start [label=\"\"];\n\tnode [shape=doublecircle];\n\t");
        biterator.accept(last, i -> out.append(Integer.toString(i)).append(";"));
        if (empty) out.append("0;");
        out.append(System.lineSeparator());

        out.append("\tnode [shape=circle];\n");
        biterator.accept(first, i -> genDotEdge.accept(0, i));
        biterator.accept(allStates, i -> biterator.accept(indexToLetter.get(i).next, j -> genDotEdge.accept(i, j)));
        out.append("\tstart -> 0;\n");
        out.append("}");

        return out.toString();
    }

    /**
     * Boring Solution with loops
     */
    public String getNFAsimple() {
    	final StringBuilder out = new StringBuilder();
    	final Map<Integer, Letter> indexToLetter = new HashMap<>();

    	assignIndex(1, indexToLetter);
    	compEmptyFirstLastAttr();
    	compNextAttr();

        out.append("digraph nfa {\n\trankdir=LR;\n\tsize=\"8,5\"\n\tnode [shape=none,width=0,height=0,margin=0]; start [label=\"\"];\n\tnode [shape=doublecircle];\n\t");
        for (int l : BitSet.valueOf(new long[] {last}).stream().toArray())
        	out.append(l).append(";");
        if (empty) out.append("0;");

        out.append("\n\tnode [shape=circle];\n");
        for (int l : BitSet.valueOf(new long[] {first}).stream().toArray())
        	out.append("\t0 -> ").append(Integer.toString(l)).append(" [label=\"").appendCodePoint(indexToLetter.get(l).x).append("\"];\n");
        for (int i : indexToLetter.keySet())
        	for (int j : BitSet.valueOf(new long[] {indexToLetter.get(i).next}).stream().toArray())
        		out.append("\t").append(Integer.toString(i)).append(" -> ").append(j).append(" [label=\"").appendCodePoint(indexToLetter.get(j).x).append("\"];\n");
        out.append("\tstart -> 0;\n");
        out.append("}");

    	return out.toString();
    }

  public static void main(String[] args) {
        RegexTree t;
        // (ε|ba)[c(a|b)∗]
        t = concat(or(epsilon(), concat(letter('b'), letter('a'))), concat(letter('c'), star(or(letter('a'), letter('b')))));
        // (a|b)*[a(a|b)]
        // t = concat(star(or(letter('a'), letter('b'))), concat(letter('a'), or(letter('a'), letter('b'))));

        try {
          PrintStream file = new PrintStream("file.gv");
          t.getNFA(file);
          Runtime.getRuntime().exec("xdot file.gv").waitFor();
        } catch (IOException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
    }
}
