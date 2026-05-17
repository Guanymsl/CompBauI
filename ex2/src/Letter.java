import java.util.*;

class Letter extends RegexTree{
    char c;
    int pos;

    Letter(char c, int pos){
        super();

        this.c = c;
        this.pos = pos;

        empty = false;
        first.add(pos);
        last.add(pos);
    }

    void computeNext(Set<Integer> inheritedNext){
        next.addAll(inheritedNext);
    }
}
