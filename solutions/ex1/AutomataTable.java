import java.util.function.Consumer;
import java.util.stream.IntStream;

public class AutomataTable {
    static final int[][] table = new int[4][26];

    static {
        IntStream.range(0, table.length).forEach(x -> IntStream.range(0, table[x].length).forEach(y -> table[x][y] = -1));
        table[0]['a' - 'a'] = 1;
        table[0]['b' - 'a'] = 2;
        table[1]['c' - 'a'] = 3;
        table[1]['d' - 'a'] = 2;
        table[2]['f' - 'a'] = 3;
        table[3]['e' - 'a'] = 1;
    }

    static boolean isAccepting(int state) {
        return state == 2;
    }

    static int run(String in) {
        System.out.println("check " + in);
        int state = 0;
        for (int token : in.codePoints().toArray())
            state = state == -1 ? -1 : table[state][token - 'a'];
        return state;
    }

    public static void main(String[] args) {
        Consumer<Integer> f = x -> System.out.println("Result state " + x + " is " + (isAccepting(x) ? "accepting" : "not accepting"));

        f.accept(run("acecedfed"));
        f.accept(run("acecedfe"));
        f.accept(run("acecedff"));
    }
}
