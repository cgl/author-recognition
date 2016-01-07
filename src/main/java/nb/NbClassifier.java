package nb;

import zemberek.tokenizer.SentenceBoundaryDetector;
import zemberek.tokenizer.SimpleSentenceBoundaryDetector;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

/**
 * Created by cagil on 06/01/16.
 *  @param <T> A feature class
 *  @param <K> A category class
 */
public class NbClassifier {

    private Dictionary<String, HashMap<String, Integer>> featureCountPerCategory;

    public void increaseFeature(String feature, String category) {
        HashMap<String, Integer> features = this.featureCountPerCategory.get(category);
        Integer count = features.get(feature);
        features.put(feature,++count);
    }

    public void getSentences(String input){
        SentenceBoundaryDetector detector = new SimpleSentenceBoundaryDetector();
        List<String> sentences = detector.getSentences(input);
        System.out.println("Sentences:");
        for (String sentence : sentences) {
            System.out.println(sentence);
        }
    }
    public void calculateFeatureVectors(HashMap<String,ArrayList<String>> dataset){
        for (String key : dataset.keySet() ) {
            for (String text : dataset.get(key)) {
                getSentences(text);

            }

        }
    }

}
