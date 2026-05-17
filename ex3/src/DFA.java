import java.util.*;

class DFA{
    RegexTree regex;

    List<Letter> letters = new ArrayList<>();
    Map<Set<Integer>, DFAState> states = new HashMap<>();
    Map<Integer, Map<Character, Integer>> transitions = new HashMap<>();

    int nextId = 0;

    DFA(RegexTree regex){
        this.regex = regex;

        regex.computeNext(new HashSet<>());
        collectLetters(regex);
    }

    void collectLetters(RegexTree r){
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

    Letter findLetter(int pos){
        for(Letter l : letters){
            if(l.pos == pos){
                return l;
            }
        }
        return null;
    }

    Set<Integer> move(Set<Integer> current, char c){
        Set<Integer> result = new HashSet<>();

        if(current.isEmpty()){
            for(int pos : regex.first){
                Letter l = findLetter(pos);

                if(l.c == c){
                    result.add(pos);
                }
            }
            return result;
        }

        for(int pos : current){
            Letter l = findLetter(pos);

            for(int nxt : l.next){
                Letter target = findLetter(nxt);

                if(target.c == c){
                    result.add(nxt);
                }
            }
        }

        return result;
    }

    String getPartialDFA(String in){
        StringBuilder dot = new StringBuilder();
        Set<Integer> startSubset = new HashSet<>();
        DFAState start;

        if(!states.containsKey(startSubset)){
            start = new DFAState(nextId++, startSubset);
            states.put(startSubset, start);
        }else{
            start = states.get(startSubset);
        }

        DFAState current = start;
        for(char c : in.toCharArray()){
            Set<Integer> nextSubset = move(current.subset, c);
            DFAState nextState;

            if(!states.containsKey(nextSubset)){
                nextState = new DFAState(nextId++, nextSubset);
                states.put(nextSubset, nextState);
            }else{
                nextState = states.get(nextSubset);
            }

            transitions.putIfAbsent(current.id, new HashMap<>());
            transitions.get(current.id).put(c, nextState.id);
            current = nextState;
        }

        boolean accepted = false;
        for(int x : current.subset){
            if(regex.last.contains(x)){
                accepted = true;
            }
        }
        if(accepted){
            System.out.println("Accepted");
        }else{
            System.out.println("Rejected");
        }

        dot.append("digraph dfa {\n");
        dot.append("rankdir=LR;\n");
        dot.append("node [shape=none]; start;\n");
        dot.append("node [shape=doublecircle];\n");
        for(DFAState s : states.values()){
            boolean isFinal = false;
            for(int x : s.subset){
                if(regex.last.contains(x)){
                    isFinal = true;
                }
            }
            if(isFinal){
                dot.append(
                    s.id + ";\n"
                );
            }
        }
        dot.append("node [shape=circle];\n");
        for(DFAState s : states.values()){
            if(s.subset.isEmpty()){
                dot.append(
                    s.id +
                    " [label=\"[0]\"];\n"
                );
            }else{
                dot.append(
                    s.id +
                    " [label=\"" +
                    s.subset +
                    "\"];\n"
                );
            }
        }
        for(int from : transitions.keySet()){
            Map<Character, Integer> map = transitions.get(from);
            for(char c : map.keySet()){
                int to = map.get(c);
                dot.append(
                    from +
                    " -> " +
                    to +
                    " [label=\"" +
                    c +
                    "\"];\n"
                );
            }
        }
        dot.append(
            "start -> " +
            start.id +
            ";\n"
        );
        dot.append("}\n");
        return dot.toString();
    }
}
