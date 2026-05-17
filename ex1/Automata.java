public class Automata{
    private int currentState;

    public Automata(){ reset(); }

    public void reset(){ currentState = 0; }

    public boolean transition(char input){
        switch(currentState){
            case 0:
                if(input == 'a'){
                    currentState = 1;
                    return true;
                }else if(input == 'b'){
                    currentState = 2;
                    return true;
                }
                break;

            case 1:
                if(input == 'c'){
                    currentState = 3;
                    return true;
                }else if(input == 'd'){
                    currentState = 2;
                    return true;
                }
                break;

            case 2:
                if(input == 'f'){
                    currentState = 3;
                    return true;
                }
                break;

            case 3:
                if(input == 'e'){
                    currentState = 1;
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean isAcceptState(){ return currentState == 2; }

    public boolean process(String input){
        reset();
        for(int i=0; i < input.length(); i++){
            char c = input.charAt(i);
            boolean valid = transition(c);
            if(!valid) return false;
        }
        return isAcceptState();
    }

    public static void main(String[] args){
        Automata automata = new Automata();

        String[] testInputs = args;

        for(String s : testInputs){
            boolean accepted = automata.process(s);
            System.out.println(
                s + " -> " +
                (accepted ? "ACCEPTED" : "REJECTED")
            );
        }
    }
}
