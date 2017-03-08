/*
 * This DataSource class will have a get result function
 * where all the file in test folder categorized as ham or spam
 * @Author Clyve Widjaya
*/
package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by clyve on 14/02/17.
 */
public class DataSource {
    /*
    This function will return result of observable array list,
    which will be displayed on the main stage. This function will
    also add each file's name, probability, actual category, and
    category from the program into either trueHam or trueSpam map.
    @Param -
    @Return result, result is the observable array list.
    */
    public static ObservableList<TestFile> getResult(){

        ObservableList<TestFile> result = FXCollections.observableArrayList();
        Set<String> keys = FileOpener.predictOfHam.keySet();
        Iterator<String> keyIterator = keys.iterator();
        while(keyIterator.hasNext()){
            String key = keyIterator.next();
            double prob = FileOpener.predictOfHam.get(key);
            if (FileOpener.trueHam.containsKey(key)){
                result.add(new TestFile(key, prob, "Ham", "Ham"));
            } else {
                result.add(new TestFile(key, prob, "Ham", "Spam"));
            }
        }

        keys = FileOpener.predictOfSpam.keySet();
        keyIterator = keys.iterator();
        while(keyIterator.hasNext()){
            String key = keyIterator.next();
            double prob = FileOpener.predictOfSpam.get(key);
            if (FileOpener.trueSpam.containsKey(key)){
                result.add(new TestFile(key, prob, "Spam", "Spam"));
            } else {
                result.add(new TestFile(key, prob, "Spam", "Ham"));
            }
        }
        return result;
    }
}
