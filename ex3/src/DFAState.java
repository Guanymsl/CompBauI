import java.util.*;

class DFAState{
    int id;
    Set<Integer> subset;

    DFAState(int id, Set<Integer> subset){
        this.id = id;
        this.subset = new HashSet<>(subset);
    }
}
