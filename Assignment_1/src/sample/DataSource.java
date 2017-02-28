package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by clyve on 14/02/17.
 */
public class DataSource {

    public static ObservableList<TestFile> getResult(){

        ObservableList<TestFile> result = FXCollections.observableArrayList();
        Set<String> keys = FileOpener.predictOfHam.keySet();
        Iterator<String> keyIterator = keys.iterator();
        while(keyIterator.hasNext()){
            String key = keyIterator.next();
            double prob = FileOpener.predictOfHam.get(key);
            result.add(new TestFile(key, prob, "Ham"));
        }

        keys = FileOpener.predictOfSpam.keySet();
        keyIterator = keys.iterator();
        while(keyIterator.hasNext()){
            String key = keyIterator.next();
            double prob = FileOpener.predictOfSpam.get(key);
            result.add(new TestFile(key, prob, "Spam"));
        }
        return result;
    }
}
