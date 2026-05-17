import java.util.function.Consumer;

interface State {
    default boolean isAccepting() { return false; }

    State next(int n);
}

public enum AutomataEnum implements State {
    Zero {
        @Override
        public State next(int token) {
            switch (token) {
                case 'a': return One;
                case 'b': return Two;
                default: return Error;
            }
        }
    },
    One {
        @Override
        public State next(int token) {
            switch (token) {
                case 'c': return Three;
                case 'd': return Two;
                default: return Error;
            }
        }
    },
    Two {
        @Override
        public State next(int token) {
            switch (token) {
                case 'f': return Three;
                default: return Error;
            }
        }

        @Override
        public boolean isAccepting() { return true; }
    },
    Three {
        @Override
        public State next(int token) {
            switch (token) {
                case 'e': return One;
                default: return Error;
            }
        }
    },
    Error {
        @Override
        public State next(int token) {
            return Error;
        }
    };

    public static State run(String in) {
        System.out.println("check " + in);
        State state = AutomataEnum.Zero;
        for (int token : in.codePoints().toArray())
            state = state.next(token);
        return state;
    }

    public static void main(String[] args) {
        Consumer<State> f = x -> System.out.println("Result state " + x + " is " + (x.isAccepting() ? "accepting" : "not accepting"));

        f.accept(AutomataEnum.run("acecedfed"));
        f.accept(AutomataEnum.run("acecedfe"));
        f.accept(AutomataEnum.run("acecedff"));
    }
}
