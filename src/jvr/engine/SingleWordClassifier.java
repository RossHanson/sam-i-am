package jvr.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ross
 * Date: 12/3/13
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleWordClassifier {


    private static final String NEGATIVE_WORD_FNAME="data/single_word_list/negative-words.txt";
    private static final String POSITIVE_WORD_FNAME="data/single_word_list/positive-words.txt";

    private Set<String> positiveWords = new HashSet<String>();
    private Set<String> negativeWords = new HashSet<String>();

    private static SingleWordClassifier instance;

    private SingleWordClassifier(String posFname, String negFname) {
        try{
            iterateAndBuildSet(new File(posFname),positiveWords);
            iterateAndBuildSet(new File(negFname),negativeWords);
        } catch (IOException e){
            System.err.println("IOException when constructing single word classifier!");
            System.err.println(e);
            //Log this for real eventually
        }
    }

    private void iterateAndBuildSet(File targetReader, Set<String> targetSet) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(targetReader));
        String line;
        while((line = br.readLine()) != null){
            targetSet.add(line);
        }
    }

    public static SingleWordClassifier getInstance(){
        if (instance==null) {
            instance = new SingleWordClassifier(POSITIVE_WORD_FNAME,NEGATIVE_WORD_FNAME);
        }
        return instance;
    }

}
