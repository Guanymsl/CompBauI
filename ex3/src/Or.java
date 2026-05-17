import java.util.*;

class Or extends RegexTree{
    RegexTree left;
    RegexTree right;

    Or(RegexTree left, RegexTree right){
        super();

        this.left = left;
        this.right = right;

        empty = left.empty || right.empty;

        first.addAll(left.first);
        first.addAll(right.first);

        last.addAll(left.last);
        last.addAll(right.last);
    }

    void computeNext(Set<Integer> inheritedNext){
        next.addAll(inheritedNext);

        left.computeNext(inheritedNext);
        right.computeNext(inheritedNext);
    }
}
