/*
  recursive descent parser for the LL(1)-grammar
  S -> As
  A -> aA | 𝝐
*/
public class ParserSolution {
    private String input;
    private char lookahead;

    /*
    initialize and start parsing of s
    */
    public void parse(String s) {
        // initialize attributes
        input = s + "$";
        lookahead = input.charAt(0);
        input = input.substring(1);

        // start parsing with the start symbol if input is not empty
        System.out.println("\nparsing " + s);
        System.out.print("lookahead: " + lookahead);
        if (parse_S() && lookahead == '$') {
            System.out.println("\n" + s + " accepted");
        } else {
            System.out.println("\n" + "end of input expected");
        }
    }

    /* consumes next input character, and compares to c
       returns true if c equals next input character, false otherwise
       sideeffect: shifts lookahead window one step forward
    */
    private boolean nextChar(char c) {
        if (lookahead != c) return false;
        lookahead = input.charAt(0);
        System.out.print(" " + lookahead);
        input = input.substring(1);
        return true;
    }

    /* S -> As
        returns true if parsing according to rule S -> As succeeds
        returns false otherwise
    */
    private boolean parse_S() {
        if (parse_A()) {
            if (nextChar('s')) return true;
            else {
                System.out.print("\nparsing failed; expected s");
                return false;
            }
        }
        return false;
    }

    /* A -> aA | 𝝐
        returns true if parsing according to rule A -> aA | 𝝐 succeeds
        returns false otherwise
    */
    private boolean parse_A() {
        switch(lookahead) { // consider lookahead for advice
            case 'a':       // a ∈ First_1(A)
                if (nextChar('a')) return parse_A();
            case 's':       // s ∈ Follow_1(A)
                return true;
        }
        System.out.print("\nparsing failed; expected a or s");
        return false;
    }

    public static void main(String[] args) {
        // test the implementation
        ParserSolution p = new ParserSolution();
        p.parse("s");
        p.parse("a");
        p.parse("b");
        p.parse("sa");
        p.parse("aaaas");
        p.parse("aaaass");
   }

}
