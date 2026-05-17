import java.util.*;

public class Main {
    static List<Letter> letters = new ArrayList<>();

    public static void main(String[] args){
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

        r.computeNext(new HashSet<>());
        collectLetters(r);
        printDot(r);
    }

    static void collectLetters(RegexTree r){
        if(r instanceof Letter){
            letters.add((Letter)r);
        }else if(r instanceof Concat){
            Concat c = (Concat)r;
            collectLetters(c.left);
            collectLetters(c.right);
        }else if(r instanceof Or){
            Or o = (Or)r;
            collectLetters(o.left);
            collectLetters(o.right);
        }else if(r instanceof Star){
            Star s = (Star)r;
            collectLetters(s.r);
        }
    }

    static void printDot(RegexTree r){
        System.out.println("digraph nfa {");
        System.out.println("rankdir=LR;");
        System.out.println("size=\"8,5\"");
        System.out.println(
            "node [shape=none,width=0,height=0,margin=0]; start [label=\"\"];"
        );
        System.out.println("node [shape=doublecircle];");
        if(r.empty){
            System.out.println("0;");
        }
        for(int i : r.last){
            System.out.println(i + ";");
        }
        System.out.println("node [shape=circle];");
        for(Letter l : letters){
            if (r.first.contains(l.pos)){
                System.out.println(
                    "0 -> " + l.pos +
                    " [label=\"" + l.c + "\"];"
                );
            }
        }
        for(Letter l : letters){
            for(int j : l.next){
                Letter target = findLetter(j);
                System.out.println(
                    l.pos + " -> " +
                    j +
                    " [label=\"" + target.c + "\"];"
                );
            }
        }
        System.out.println("start -> 0;");
        System.out.println("}");
    }

    static Letter findLetter(int pos){
        for(Letter l : letters){
            if(l.pos == pos){
                return l;
            }
        }
        return null;
    }
}