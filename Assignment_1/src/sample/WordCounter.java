package sample;

import java.util.*;

/**
 * Created by clyve on 14/02/17.
 */
public class WordCounter {
    private static Map<String,Integer> wordCounts;
    public WordCounter(){
        wordCounts = new TreeMap<>();
    }

    public static boolean isWord(String token){
        String pattern = "^[a-zA-Z0-9@\\#$%&*()_+\\]\\[';:?.,!^-]*$";
        if (token.matches(pattern)){
            return true;
        } else {
            return false;
        }
    }
}
