import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private final Map<Integer, Map<Integer, Integer>> delta = new HashMap<>();

    public static Epsilon epsilon() { return Epsilon.instance; }
    public static Letter letter(int l) { return new Letter(l); }
    public static Concat concat(RegexTree l, RegexTree r) { return new Concat(l, r); }
    public static Or or(RegexTree l, RegexTree r) { return new Or(l, r); }
    public static Star star(RegexTree regex) { return new Star(regex); }

    abstract int assignIndex(int i, Map<Integer, Letter> m);
    abstract void compEmptyFirstLastAttr();
    abstract void compNextAttr();

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

    public String getPartialDFA(String in,PrintStream out) {

        final Function<Integer, IntStream> bitstream = s -> BitSet.valueOf(new long[]{s}).stream();

        final Map<Integer, Letter> indexToLetter = new HashMap<>();
        // number terminal leaf nodes starting from 1
        assignIndex(1, indexToLetter);
        // compute empty, first, next and last attributes
        compEmptyFirstLastAttr();
        compNextAttr();

        // use 0 indexed state as error state with letter representation '?'
        indexToLetter.put(0, new Letter('?').setIndex(0).setNext(first));

        // transision for current state s with input character a to returned next state
        BiFunction<Integer, Integer, Integer> next = (s, a) -> {
            // if in error state stay in error state
            if (s == 0) return 0;

            // computeIfAbsent(key, mapping_fun) applies mapping_function to key if key is not present in map
            // here we initialize the transition map of the current state with an empty map if missing
            Map<Integer, Integer> tmp = delta.computeIfAbsent(s, unused -> new HashMap<>());

            // add transition for input symbol a to transition map tmp of current set of states s (if not yet computed)
            return tmp.computeIfAbsent(a, unused ->
                    bitstream.apply(s)
                            // for every state i in the set s of NFA states
                                    // get all states in the next attribute
                            .map(i ->  bitstream.apply(indexToLetter.get(i).next)
                                    // filter to keep only those whose letter matches the input character
                                    .filter(j -> indexToLetter.get(j).x == a)
                                    // create bit representation of it
                                    .map(j -> 1 << j)
                                    // union all to reduce to the set of NFA states that can be next
                                    .reduce(0, (x, y) -> x | y))
                            // stream.reduce(identity, accumulator_fun)
                            // reduces the stream to a bitset again with union for the set of transitions for all NFA states in s'
                            .reduce(0, (x, y) -> x | y)
            );
        };

        // set start state to 1
        int state = 1 << 0;
        // apply next repeatedly for every character input to create/follow NFA edges
        for (int token : in.codePoints().toArray())
            state = next.apply(state, token);

        // final states are those in last attribute of root node and the start state in case top-level regular expression may be empty
        final int finStatesNFA = last | (empty ? (1 << 0) : 0);
        final Predicate<Integer> isFinalState = s -> (s & finStatesNFA) != 0;

        final Set<Integer> allStates = new HashSet<>(delta.keySet());
        delta.values().stream().map(Map::values).forEach(allStates::addAll);

        final Function<Integer, String> stateToDotId = s -> s == 0 ? "n_error" : "n_" + bitstream.apply(s).boxed().map(Object::toString).collect(Collectors.joining("_"));
        final Function<Integer, String> stateToDotLab = s -> s == 0 ? "∅" : "{" + bitstream.apply(s).boxed().map(Object::toString).collect(Collectors.joining(",")) + "}";
        final Consumer<Integer> genDotState =
                s -> out.append(String.format("\t%s [label=\"%s\"];%n", stateToDotId.apply(s), stateToDotLab.apply(s)));
        final BiConsumer<Integer, Integer> genDotEdge =
                (i, a) -> out.append(String.format("\t%s -> %s [label=\"%s\"];%n", stateToDotId.apply(i), stateToDotId.apply(delta.get(i).get(a)), new String(Character.toChars(a))));

        // header
        out.append("digraph dfa {\n\trankdir=LR;\n\tsize=\"8,5\"\n\tnode [shape=none,width=0,height=0,margin=0]; start [label=\"\"];\n");

        // final states
        out.append("\tnode [shape=doublecircle];\n");
        allStates.stream().filter(isFinalState).forEach(genDotState);

        // non-final states
        out.append("\tnode [shape=circle];\n");
        allStates.stream().filter(isFinalState.negate()).forEach(genDotState);

        // edges
        delta.forEach((s, m) -> m.forEach((a, unused) -> genDotEdge.accept(s, a)));
        out.append("\tstart -> n_0;\n");

        // footer
        out.append("}");

        System.out.println("The word \"" + in + "\" is " + (isFinalState.test(state) ? "accepted" : "not accepted"));

        return out.toString();
    }

    public static void main(String[] args) {
        RegexTree t;
        // (ε|ba)[c(a|b)∗]
        // t = concat(or(epsilon(), concat(letter('b'), letter('a'))), concat(letter('c'), star(or(letter('a'), letter('b')))));
        // (a|b)*[a(a|b)]
        t = concat(star(or(letter('a'), letter('b'))), concat(letter('a'), or(letter('a'), letter('b'))));

        try {
          PrintStream file = new PrintStream("nfa.gv");
          PrintStream file1 = new PrintStream("dfa1.gv");
          PrintStream file2 = new PrintStream("dfa2.gv");
          PrintStream file3 = new PrintStream("dfa3.gv");
          PrintStream file4 = new PrintStream("dfa4.gv");
          t.getNFA(file);
          t.getPartialDFA("baaa",file1);
          t.getPartialDFA("bbabbabaaab",file2);
          t.getPartialDFA("a",file3);
          t.getPartialDFA("aca",file4);
          Runtime.getRuntime().exec("dot -Tpdf nfa.gv -O").waitFor();
          Runtime.getRuntime().exec("dot -Tpdf dfa1.gv -O").waitFor();
          Runtime.getRuntime().exec("dot -Tpdf dfa2.gv -O").waitFor();
          Runtime.getRuntime().exec("dot -Tpdf dfa3.gv -O").waitFor();
          Runtime.getRuntime().exec("dot -Tpdf dfa4.gv -O").waitFor();
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
}
