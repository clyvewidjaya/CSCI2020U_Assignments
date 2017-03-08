/*
 * This WordCounter class will determine whether the word
 * passed on the isWord function is actually a word or
 * a set of characters or not.
 * @Author Clyve Widjaya
*/
package sample;

public class WordCounter {
    /*
    This function will return true or false depends on the pattern in the function
    I developed the string patterns to capture more words or characters. In spam mails
    the '!!!' could also has a high spam probability. Also with uppercase and lower case,
    the and THE have different spam probability, as they are going to be counted differently
    using this string pattern.
    @Param String token, token will be the word that is going to be checked
    @Return True or false
    */
    public static boolean isWord(String token){
        String pattern = "^[a-zA-Z0-9@\\#$%&*()_+\\]\\[';:?.,!^-]*$";
        if (token.matches(pattern)){
            return true;
        } else {
            return false;
        }
    }
}
