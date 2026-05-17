import java.io.*;

public class Main{
    public static void main(String[] args)
        throws Exception {
        RegexTree r =
            RegexTree.concat(
                RegexTree.star(
                    RegexTree.or(
                        RegexTree.letter('a', 1),
                        RegexTree.letter('b', 2)
                    )
                ),
                RegexTree.concat(
                    RegexTree.letter('a', 3),
                    RegexTree.or(
                        RegexTree.letter('a', 4),
                        RegexTree.letter('b', 5)
                    )
                )
            );

        DFA dfa = new DFA(r);

        String dot1 = dfa.getPartialDFA("bba");
        PrintWriter out1 = new PrintWriter("dfa1.gv");
        out1.print(dot1);
        out1.close();

        String dot2 = dfa.getPartialDFA("aaa");
        PrintWriter out2 = new PrintWriter("dfa2.gv");
        out2.print(dot2);
        out2.close();

        String dot3 = dfa.getPartialDFA("aababb");
        PrintWriter out3 = new PrintWriter("dfa3.gv");
        out3.print(dot3);
        out3.close();
    }
}
