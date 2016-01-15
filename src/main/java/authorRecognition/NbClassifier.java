package authorRecognition;

import com.google.common.io.Resources;
import zemberek.core.io.SimpleTextReader;
import zemberek.tokenizer.SentenceBoundaryDetector;
import zemberek.tokenizer.SimpleSentenceBoundaryDetector;
import zemberek.tokenizer.ZemberekLexer;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by cagil on 06/01/16.
 */
public class NbClassifier {

    private Dictionary<String, HashMap<String, Integer>> featureCountPerCategory;
    private HashMap<String,HashMap<String, Integer>> FWfeatureCountsPerCategory;
    private HashMap<String, Integer> FWfeatureTotalCountsPerCategory;

    private HashMap<String, HashMap<String, Integer>> CNGfeatureCountsPerCategory;
    private List<String> FWlist;
    private final double alfa = 0.01;

    private HashSet<String> categoryLabels;

    public NbClassifier() throws IOException {
        //URL multi_line_text_file = Resources.getResource("fWords.txt");
        URL multi_line_text_file = Resources.getResource("fWords_test.txt");
        FWlist = new SimpleTextReader(multi_line_text_file.getFile(), "windows-1254").asStringList();
        FWfeatureCountsPerCategory = new HashMap<String,HashMap<String, Integer>>();
        categoryLabels = new HashSet<String>();
        FWfeatureTotalCountsPerCategory = new HashMap<String, Integer>();
    }

    public static Map sortByValue(Map unsortMap) {
        List list = new LinkedList(unsortMap.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        Map sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    /* Given a test set calculates the probabilities and make a guess for the author of the texts */
    public HashMap<String, Map<String, HashMap<String, Integer>>> test(HashMap<String,ArrayList<String>> texts){
        int correct = 0; int total = 0;

        HashMap<String, Map<String, HashMap<String, Integer>>> classifications = new HashMap<String, Map<String, HashMap<String, Integer>>>();
        Map<String, HashMap<String, Integer>> fwFeatureVector = null;
        for (String author : texts.keySet()) {
            for (String text : texts.get(author)) {
                // count(w_i,c_j) Map<label, HashMap<FW, counts>>
                fwFeatureVector = calculateFWFeatureVector(text);
                HashMap<String, Double> likelihoods = calculateLikelihood(fwFeatureVector);
                Double maxL = Collections.max(likelihoods.values());
                Map map = sortByValue(likelihoods);
                //System.out.println(maxL);
                //System.out.println(map);
                String guess = (String) map.keySet().toArray()[0];
                //System.out.format("%s %s\n", author,guess);
                if(author.equals(guess))
                    correct++;
                total++;
            }
            classifications.put(author,fwFeatureVector);
        }
        System.out.format("%d correct out of %d\n", correct,total);
        return classifications;
    }


    private HashMap<String, Double> calculateLikelihood(Map<String, HashMap<String, Integer>> FWfeatureCountsForLabels) {
        HashMap<String, Double> likelihoods = new HashMap<String,Double>();
        Double likelihood; Integer count; Double denominator; int N;
        for (String label : FWfeatureCountsForLabels.keySet()) {
            likelihood = 1.;
            for (String FW : FWfeatureCountsForLabels.get(label).keySet()) {
                count = FWfeatureCountsForLabels.get(label).get(FW);
                N = FWfeatureCountsForLabels.get(label).size();
                denominator = FWfeatureTotalCountsPerCategory.get(label) + (N*alfa);
                likelihood *= count/denominator;
            }
            likelihoods.put(label,likelihood);
        }
        return likelihoods;
    }

    /* Function to calculate the feature vector of a given text */
    private Map<String, HashMap<String, Integer>> calculateFWFeatureVector(String text) {
        Map<String, HashMap<String, Integer>> FWfeatureCountsForLabels = new TreeMap<String, HashMap<String, Integer>>();
        Integer count = null;
        for (String FW : FWlist) {
            if (text.contains(FW)) {
                for (String label : categoryLabels) {
                    if(FWfeatureCountsPerCategory.get(label).containsKey(FW))
                        count = FWfeatureCountsPerCategory.get(label).get(FW);
                    else
                        continue;
                    if (count == 0)
                        continue; //count = alfa;
                    if (!FWfeatureCountsForLabels.containsKey(label))
                        FWfeatureCountsForLabels.put(label,new HashMap<String, Integer>());
                    FWfeatureCountsForLabels.get(label).put(FW, count);
                }
            }
        }
        return FWfeatureCountsForLabels;
    }


    public void trainFWFeatures(String author, String text) throws IOException {
        HashMap<String, Integer> FWVectorForCategory = FWfeatureCountsPerCategory.get(author);
        Integer count;
        SentenceBoundaryDetector detector = new SimpleSentenceBoundaryDetector();
        List<String> sentences = detector.getSentences(text);
        for (String sentence : sentences) {
            //System.out.println(sentence);
            List<String> tokens = getTokens(sentence);
            for (String FW : FWlist) {
                if(tokens.contains(FW)){
                    count = 0;
                    if(FWVectorForCategory.containsKey(FW))
                        count = FWVectorForCategory.get(FW);
                    FWVectorForCategory.put(FW,++count);
                }
            }
        }
    }

    public List<String> getTokens(String input){
        ZemberekLexer lexer = new ZemberekLexer();
        List<String> tokens = lexer.tokenStrings(input);
        return tokens;
        //        Joiner.on("|").join(lexer.tokenStrings(input)));
    }

    /*
    * Given a dataset trains the system
    * */
    public void calculateFeatureVectors(HashMap<String,ArrayList<String>> dataset) throws IOException {
        // Read all text and fill the Feature Count Vector for FWs
        for (String author : dataset.keySet() ) {
            categoryLabels.add(author);
            for (String text : dataset.get(author)) {
                FWfeatureCountsPerCategory.put(author,new HashMap<String, Integer>());
                trainFWFeatures(author,text);
            }
        }
        //
        Integer totalCount;
        for (String label : categoryLabels) {
            HashMap<String, Integer> FWfeatureCounts = FWfeatureCountsPerCategory.get(label);
            totalCount = 0;
            for (String w: FWfeatureCounts.keySet()){
                totalCount += FWfeatureCounts.get(w);
            }
            FWfeatureTotalCountsPerCategory.put(label,totalCount);
        }

        HashMap<String, Map<String, HashMap<String, Integer>>> results = test(dataset);
        for (String trueAuthor : results.keySet()) {
            System.out.println(trueAuthor);
            for (String s : results.get(trueAuthor).keySet()) {
                System.out.print(s+": ");
                System.out.println(results.get(trueAuthor).get(s));
            }
        }
    }

}
