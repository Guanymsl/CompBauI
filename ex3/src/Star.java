import java.util.*;

class Star extends RegexTree {
    RegexTree r;

    Star(RegexTree r){
        super();

        this.r = r;

        empty = true;
        first.addAll(r.first);
        last.addAll(r.last);
    }

    void computeNext(Set<Integer> inheritedNext){
        next.addAll(inheritedNext);

        Set<Integer> childNext = new HashSet<>(first);
        childNext.addAll(inheritedNext);

        r.computeNext(childNext);
    }
}
