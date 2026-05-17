import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AutomataSimple {
    // idea 1: bad complexity
    static int delta(int state, int token) {
        if (state == 0 && token == 'a') return 1;
        if (state == 0 && token == 'b') return 2;
        if (state == 1 && token == 'c') return 3;
        if (state == 1 && token == 'd') return 2;
        if (state == 2 && token == 'f') return 3;
        if (state == 3 && token == 'e') return 1;
        return -1;
    }

    // idea 2: better complexity then idea 1 but still ...
    static final Map<Integer, Map<Character, Integer>> delta = new HashMap<>();

    static {
        delta.put(0, new HashMap<>());
        delta.get(0).put('a', 1);
        delta.get(0).put('b', 2);
        delta.put(1, new HashMap<>());
        delta.get(1).put('c', 3);
        delta.get(1).put('d', 2);
        delta.put(2, new HashMap<>());
        delta.get(2).put('f', 3);
        delta.put(3, new HashMap<>());
        delta.get(3).put('e', 1);
    }

    static boolean isAccepting(int state) {
        return state == 2;
    }

    static int run_idea1(String in) {
        System.out.println("check " + in);
        int state = 0;
        for (int token : in.codePoints().toArray())
            state = delta(state, token);
        return state;
    }

    static int run_idea2(String in) {
        System.out.println("check " + in);
        int state = 0;
        for (int token : in.codePoints().toArray())
            state = state == -1 ? -1 : delta.getOrDefault(state, new HashMap<>()).getOrDefault((char) token, -1);
        return state;
    }

    public static void main(String[] args) {
        Consumer<Integer> f = x -> System.out.println("Result state " + x + " is " + (isAccepting(x) ? "accepting" : "not accepting"));

        System.out.println("idea1");
        f.accept(AutomataSimple.run_idea1("acecedfed"));
        f.accept(AutomataSimple.run_idea1("acecedfe"));
        f.accept(AutomataSimple.run_idea1("acecedff"));

        System.out.println("idea2");
        f.accept(AutomataSimple.run_idea2("acecedfed"));
        f.accept(AutomataSimple.run_idea2("acecedfe"));
        f.accept(AutomataSimple.run_idea2("acecedff"));
    }
}
