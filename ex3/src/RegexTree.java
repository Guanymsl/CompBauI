import java.util.*;

abstract class RegexTree{
    boolean empty;
    Set<Integer> first;
    Set<Integer> last;
    Set<Integer> next;

    RegexTree(){
        first = new HashSet<>();
        last = new HashSet<>();
        next = new HashSet<>();
    }

    abstract void computeNext(Set<Integer> inheritedNext);

    static Epsilon epsilon(){
        return new Epsilon();
    }

    static Letter letter(char c, int pos){
        return new Letter(c, pos);
    }

    static Concat concat(RegexTree l, RegexTree r){
        return new Concat(l, r);
    }

    static Or or(RegexTree l, RegexTree r){
        return new Or(l, r);
    }

    static Star star(RegexTree r){
        return new Star(r);
    }
}
