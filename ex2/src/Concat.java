import java.util.*;

class Concat extends RegexTree{
    RegexTree left;
    RegexTree right;

    Concat(RegexTree left, RegexTree right){
        super();

        this.left = left;
        this.right = right;

        empty = left.empty && right.empty;

        if(left.empty){
            first.addAll(left.first);
            first.addAll(right.first);
        }else{
            first.addAll(left.first);
        }

        if(right.empty){
            last.addAll(left.last);
            last.addAll(right.last);
        }else{
            last.addAll(right.last);
        }
    }

    void computeNext(Set<Integer> inheritedNext){
        next.addAll(inheritedNext);

        Set<Integer> leftNext = new HashSet<>(right.first);
        if(right.empty){
            leftNext.addAll(inheritedNext);
        }

        left.computeNext(leftNext);
        right.computeNext(inheritedNext);
    }
}
