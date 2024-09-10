package Players;

import java.util.Arrays;
import java.util.HashSet;

public class Operative extends Player {
    private HashSet<Integer> guesses;
    public Operative(String name){
        super(name);
    }
    public void setGuesses(Integer ... currGuesses){
        guesses = new HashSet<Integer>(Arrays.asList(currGuesses));
    }

    public HashSet<Integer> getGuesses(){
        return guesses;
    }


}
