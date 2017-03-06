/*
 * This FileOpener Class will open and read both test and train folder,
 * also count for the spam probability of each word,
 * spam prob of each file, accuracy, and precision
 * @Author Clyve Widjaya
*/
package sample;

import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

public class FileOpener {
    public static int countofSpamFiles = 0;
    public static int countofHamFiles = 0;
    public static int countofHam2Files = 0;
    public static int totalSpamFiles = 0;
    public static int totalHamFiles = 0;
    public static double accuracy = 0.0;
    public static double precision = 0.0;
    private static Map<String,Integer> trainSpam;
    private static Map<String,Integer> trainHam;
    private static Map<String,Integer> trainHam2;
    private static Map<String,Integer> trainHamComb;
    private static Map<String,Double> spamOfEachWord;
    public static Map<String,Double> predictOfSpam;
    public static Map<String,Double> predictOfHam;
    public static Map<String,String> trueSpam;
    public static Map<String,String> trueHam;


    public FileOpener(){
        trainSpam = new TreeMap<>();
        trainHam = new TreeMap<>();
        trainHam2 = new TreeMap<>();
        trainHamComb = new TreeMap<>();
        spamOfEachWord = new TreeMap<>();
        predictOfSpam = new TreeMap<>();
        predictOfHam = new TreeMap<>();
        trueSpam = new TreeMap<>();
        trueHam = new TreeMap<>();
    }

    public static void startClass(File mainDirectory) throws IOException{
        FileOpener trainSpam = new FileOpener();
        FileOpener trainHam = new FileOpener();
        FileOpener trainHam2 = new FileOpener();
        FileOpener trainHamComb = new FileOpener();
        FileOpener spamOfEachWord = new FileOpener();
        FileOpener predictOfSpam = new FileOpener();
        FileOpener predictOfHam = new FileOpener();
        FileOpener trueSpam = new FileOpener();
        FileOpener trueHam = new FileOpener();

        String equalWith = "spam";
        trainSpam.openFolder(mainDirectory, equalWith);
        //To print spam tree on a file, use code below
        //trainSpam.printWordCounts(equalWith);

        equalWith = "ham2";
        trainHam2.openFolder(mainDirectory, equalWith);
        //To print ham2 tree on a file, use code below
        //trainHam2.printWordCounts(equalWith);

        equalWith = "ham";
        trainHam.openFolder(mainDirectory, equalWith);
        //To print ham tree on a file, use code below
        //trainHam.printWordCounts(equalWith);

        //Combine trainHam and trainHam2 tree
        trainHamComb.combineTree();
        equalWith = "trainHamComb";
        //To print combined ham tree on a file, use code below
        //trainHamComb.printWordCounts(equalWith);

        //Calling the countProb function for spamOfEachWord tree
        spamOfEachWord.countProb();
        //To print spam value of each word on a file, use code below
        /*
        equalWith = "spamProb";
        spamOfEachWord.printWordCounts(equalWith);
        */

        //This will open and read test folder
        openTestFolder(mainDirectory);

        /*
        equalWith = "predictSpam";
        predictOfSpam.printWordCounts(equalWith);

        equalWith = "predictHam";
        predictOfHam.printWordCounts(equalWith);
        */

        //Counting accuracy and probability
        countAccuracy();
    }

    /*
    This function will count the accuracy of our program,
    It will count the accuracy and precision using the formula given
    on Assignment1's pdf. For this program, I used 50% as the threshold,
    so files that have spam probability of below 50% will be considered
    as ham and if more than 50%, it will be considered as spam
    @Param -
    @Return -
    */
    public static void countAccuracy() throws IOException{
        int correct = 0;
        int totalFiles = totalHamFiles + totalSpamFiles;
        int correctSpam = 0;
        int correctSpamGuess = 0;

        Set<String> keys = FileOpener.predictOfHam.keySet();
        Iterator<String> keyIterator = keys.iterator();
        while(keyIterator.hasNext()){
            String key = keyIterator.next();
            double prob = FileOpener.predictOfHam.get(key);
            if (prob < 0.5){
                correct++;
                trueHam.put(key,"Ham");
            } else {
                trueSpam.put(key,"Spam");
                correctSpam++;
            }
        }

        keys = FileOpener.predictOfSpam.keySet();
        keyIterator = keys.iterator();
        while(keyIterator.hasNext()){
            String key = keyIterator.next();
            double prob = FileOpener.predictOfSpam.get(key);
            if (prob >= 0.5){
                correct++;
                correctSpam++;
                correctSpamGuess++;
                trueSpam.put(key,"Spam");
            } else {
                trueHam.put(key,"Ham");
            }
        }
        accuracy = (double)correct / (double)totalFiles;
        precision = (double)correctSpamGuess/(double)correctSpam;
    }

    /*
    This function will open the test folder and read all the files inside.
    It will find folder called "test" in the chosen directory, and if it
    arrives to the files, this function will call another function, which
    is openTestFile function, to read the file.
    @Param File mainDirectory, this contains the path to the selected directory
    @Return -
    */
    public static void openTestFolder(File mainDirectory) throws IOException{
        if (mainDirectory.isDirectory()){
            File[] filesInDir = mainDirectory.listFiles();
            for (int i = 0; i < filesInDir.length; i++){
                if (filesInDir[i].getName().equals("test")){
                    //System.out.println("Folder -> " + filesInDir[i].getName());
                    openTestFolder(filesInDir[i]);
                } else if (filesInDir[i].isDirectory()){
                    //System.out.println("Folder -> " + filesInDir[i].getName());
                    if (filesInDir[i].getName().equals("spam") && filesInDir[i].getParentFile().getName().equals("test")){
                        openTestFile(filesInDir[i]);
                    } else if (filesInDir[i].getName().equals("ham") && filesInDir[i].getParentFile().getName().equals("test")){
                        openTestFile(filesInDir[i]);
                    }
                    openTestFolder(filesInDir[i]);
                }
            }
        }
    }

    /*
    This function will read and count the spam probability of each file.
    After it finished counting the probability, it saves the filename and its probability
    on a treemap called predictOfSpam or predictOfHam. This function will also count
    number of files in the ham and spam folder.
    @Param File wantedFile, this contains the path to the file that is going to be read
    @Return -
    */
    public static void openTestFile(File wantedFile) throws IOException{
        File[] filesToOpen = wantedFile.listFiles();
        for (int i = 0; i < filesToOpen.length; i++){
            double totalProb = 0.0;
            double probSpamOfFile = 0.0;
            String fileName;
            Scanner scanner = new Scanner(filesToOpen[i]);
            while (scanner.hasNext()){
                String word = scanner.next();
                if (WordCounter.isWord(word)){
                    if (spamOfEachWord.containsKey(word)){
                        double prob = spamOfEachWord.get(word);
                        //System.out.println("prob -> " + prob);
                        totalProb += ((Math.log(1.0-prob)) - (Math.log(prob)));
                    }
                }
            }

            probSpamOfFile = (1.0) / ((1.0) + (Math.pow(Math.E,totalProb)));
            //System.out.println("Prob " + probSpamOfFile);

            fileName = filesToOpen[i].getName();
            //System.out.println("Prob " + fileName + " -> " + probSpamOfFile + " - > ");

            if (filesToOpen[i].getParentFile().getName().equals("spam")){
                predictOfSpam.put(fileName,probSpamOfFile);
                totalSpamFiles++;
            } else if (filesToOpen[i].getParentFile().getName().equals("ham")){
                predictOfHam.put(fileName,probSpamOfFile);
                totalHamFiles++;
            }
        }
    }

    /*
    This function will combine 2 treemaps of ham from training folder,
    since we have 2 ham folders. The way I made the ham tree, was making
    separate ham tree, 1 for ham and 1 for ham2. So i needed a function
    which combined both.
    @Param -
    @Return -
    */
    public static void combineTree() throws IOException{
        Set<String> keys = trainHam.keySet();
        Iterator<String> keyIterator = keys.iterator();
        while(keyIterator.hasNext()){
            String key = keyIterator.next();
            int count = trainHam.get(key);
            trainHamComb.put(key,count);
        }

        keys = trainHam2.keySet();
        keyIterator = keys.iterator();
        while(keyIterator.hasNext()) {
            String key = keyIterator.next();
            if (trainHamComb.containsKey(key)) {
                int count = trainHam2.get(key);
                int oldVal = trainHamComb.get(key);
                trainHamComb.put(key, oldVal + count);
            } else {
                int count = trainHam2.get(key);
                trainHamComb.put(key, count);
            }
        }
    }

    /*
    This function will combine 2 treemaps of ham from training folder,
    since we have 2 ham folders. The way I made the ham tree, was making
    separate ham tree, 1 for ham and 1 for ham2. So i needed a function
    which combined both.
    @Param -
    @Return -
    */
    public static void countProb() throws IOException{
        Set<String> keys = trainSpam.keySet();
        Iterator<String> keyIterator = keys.iterator();
        double probWiS;
        double probWiH;
        double probSWi;
        while(keyIterator.hasNext()){
            String key = keyIterator.next();
            int countofSpamKey = trainSpam.get(key);
            probWiS = ((double)countofSpamKey) / ((double)countofSpamFiles);
            //System.out.println("This Key Spam Prob: " + key + " -> " + probWiS);

            if (trainHamComb.containsKey(key)){
                int countofKey = trainHamComb.get(key);
                probWiH = ((double)countofKey) / ((double)countofHamFiles + (double)countofHam2Files);
                //System.out.println("This Key Ham Prob: " + key + " -> " + probWiH);
            } else {
                probWiH = 0.0;
                //System.out.println("This Key Ham Prob: " + key + " -> " + probWiH);
            }

            //count and input to spam map tree
            probSWi = probWiS / (probWiS + probWiH);
            //System.out.println("This Key: " + key + " -> " + probSWi);
            if (probSWi != 1.0){
                spamOfEachWord.put(key,probSWi);
            }
        }
    }

    /*
    This function will open the train folder and read all the files inside.
    For each folder inside, ham, ham2, and spam, they will use a separate
    tree map, in this case it will use 3, 1 for ham2, 1 for ham, 1 for spam.
    This function will recurse until it reaches file, and when it does, it will
    call openFile function to read the file
    @Param File mainDirectory, the path to the directory or file.
    @Param String equalWith, it is just a flag so the program knows where to
            store the words read.
    @Return -
    */
    public static void openFolder(File mainDirectory, String equalWith) throws IOException{
        if (mainDirectory.isDirectory()){
            File[] filesInDir = mainDirectory.listFiles();
            for (int i = 0; i < filesInDir.length; i++){
                if (filesInDir[i].getName().equals("train")) {
                    //System.out.println("Folder -> " + filesInDir[i].getName());
                    openFolder(filesInDir[i], equalWith);
                } else if (filesInDir[i].isDirectory()){
                    //System.out.println("Folder -> " + filesInDir[i].getName());
                    if (filesInDir[i].getName().equals(equalWith) && filesInDir[i].getParentFile().getName().equals("train")){
                        if (equalWith == "spam"){
                            File[] filesInside = filesInDir[i].listFiles();
                            countofSpamFiles = filesInside.length;
                        }

                        if (equalWith == "ham2"){
                            File[] filesInside = filesInDir[i].listFiles();
                            countofHam2Files = filesInside.length;
                        }

                        if (equalWith == "ham"){
                            File[] filesInside = filesInDir[i].listFiles();
                            countofHamFiles = filesInside.length;
                        }
                        openFile(filesInDir[i], equalWith);
                    }
                    openFolder(filesInDir[i], equalWith);
                }
            }
        }
    }

    /*
    This function will record on number of each words appear in files.
    It doesnt count on number of specific word in a file. It will strore
    the word and count to the trees, depends on the equalWith. If the
    equalWith is spam then store to trainSpam tree, if ham2 then store to trainHam2,
    and if ham then store to trainHam.
    @Param File wantedFile, path to the file that is going to be read
    @Param String equalWith, flag to know which tree to store to
    @Return -
    */
    public static void openFile(File wantedFile, String equalWith) throws IOException{
        File[] filesToOpen = wantedFile.listFiles();

        for (int i = 0; i < filesToOpen.length; i++){
            Scanner scanner = new Scanner(filesToOpen[i]);
            while (scanner.hasNext()){
                String word = scanner.next();
                if (WordCounter.isWord(word)){
                    if (equalWith == "spam"){
                        if (trainSpam.containsKey(word)){
                            int oldCount = trainSpam.get(word);
                            if (oldCount <= i){
                                trainSpam.put(word,oldCount+1);
                            }
                        } else {
                            trainSpam.put(word,1);
                        }
                    } else if (equalWith == "ham"){
                        if (trainHam.containsKey(word)){
                            int oldCount = trainHam.get(word);
                            if (oldCount <= i){
                                trainHam.put(word,oldCount+1);
                            }
                        } else {
                            trainHam.put(word,1);
                        }
                    } else if (equalWith == "ham2") {
                        if (trainHam2.containsKey(word)) {
                            int oldCount = trainHam2.get(word);
                            if (oldCount <= i) {
                                trainHam2.put(word, oldCount + 1);
                            }
                        } else {
                            trainHam2.put(word, 1);
                        }
                    }
                }
            }
        }

    }
    /*
    public void printWordCounts(String equalWith) throws IOException{
        if (equalWith == "spam"){
            PrintWriter fout = new PrintWriter("spam.txt");
            Set<String> keys = trainSpam.keySet();
            Iterator<String> keyIterator = keys.iterator();

            while(keyIterator.hasNext()){
                String key = keyIterator.next();
                int count = trainSpam.get(key);

                fout.println(key + " -> " + count);
            }
            fout.close();
        } else if (equalWith == "ham"){
            PrintWriter fout = new PrintWriter("ham.txt");
            Set<String> keys = trainHam.keySet();
            Iterator<String> keyIterator = keys.iterator();

            while(keyIterator.hasNext()){
                String key = keyIterator.next();
                int count = trainHam.get(key);

                fout.println(key + " -> " + count);
            }
            fout.close();
        } else if (equalWith == "spamProb"){
            PrintWriter fout = new PrintWriter("spamProb.txt");
            Set<String> keys = spamOfEachWord.keySet();
            Iterator<String> keyIterator = keys.iterator();

            while(keyIterator.hasNext()){
                String key = keyIterator.next();
                double count = spamOfEachWord.get(key);

                fout.println(key + " -> " + count);
            }
            fout.close();
        } else if (equalWith == "ham2"){
            PrintWriter fout = new PrintWriter("ham2.txt");
            Set<String> keys = trainHam2.keySet();
            Iterator<String> keyIterator = keys.iterator();

            while(keyIterator.hasNext()){
                String key = keyIterator.next();
                int count = trainHam2.get(key);

                fout.println(key + " -> " + count);
            }
            fout.close();
        } else if (equalWith == "trainHamComb"){
            PrintWriter fout = new PrintWriter("hamComb.txt");
            Set<String> keys = trainHamComb.keySet();
            Iterator<String> keyIterator = keys.iterator();

            while(keyIterator.hasNext()){
                String key = keyIterator.next();
                int count = trainHamComb.get(key);

                fout.println(key + " -> " + count);
            }
            fout.close();
        } else if (equalWith == "predictSpam"){
            PrintWriter fout = new PrintWriter("predictSpam.txt");
            Set<String> keys = predictOfSpam.keySet();
            Iterator<String> keyIterator = keys.iterator();

            while(keyIterator.hasNext()){
                String key = keyIterator.next();
                double count = predictOfSpam.get(key);

                fout.println(key + " -> " + count);
            }
            fout.close();
        } else if (equalWith == "predictHam"){
            PrintWriter fout = new PrintWriter("predictHam.txt");
            Set<String> keys = predictOfHam.keySet();
            Iterator<String> keyIterator = keys.iterator();

            while(keyIterator.hasNext()){
                String key = keyIterator.next();
                double count = predictOfHam.get(key);

                fout.println(key + " -> " + count);
            }
            fout.close();
        }
    }
    */

}
