import java.util.*;

class Epsilon extends RegexTree{

    Epsilon(){
        super();

        empty = true;
    }

    void computeNext(Set<Integer> inheritedNext){
        next.addAll(inheritedNext);
    }
}
