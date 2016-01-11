package nb;

import com.google.common.io.Resources;
import zemberek.core.io.SimpleTextReader;
import zemberek.tokenizer.ZemberekLexer;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static java.lang.Math.log;

/**
 * Created by cagil on 09/01/16.
 *
 *
 *  private Dictionary<String, HashMap<String, Integer>> featureCountPerCategory;
 private HashMap<String,HashMap<String, Integer>> FWfeatureCountsPerCategory;
 private HashMap<String, Integer> FWfeatureTotalCountsPerCategory;

 private HashMap<String, HashMap<String, Integer>> CNGfeatureCountsPerCategory;
 private List<String> FWlist;
 private final double alfa = 0.01;

 private HashSet<String> categoryLabels;
 */
public class NaiveBayesClassifier {
    private final double alfa = 0.01;
    private int[] catFrequencies;
    public String[] cats;
    public HashMap<String,Integer> cats_indexes;
    String[] trainingSet;

    public List<String> featuresArr; // list version of features array
    String[] features;
    int N; // total number of features
    // Number of occurrences of w_k in class c documents  featureCounts[c_i][w_i]
    int [][] featureCounts;
    double [][] likelihoods;

    // fill features
    // update N with count(features)
    public NaiveBayesClassifier() throws IOException {
        URL multi_line_text_file = Resources.getResource("fWords.txt");
        List<String> f_list = new SimpleTextReader(multi_line_text_file.getFile(), "windows-1254").asStringList();
        features = f_list.toArray(new String[f_list.size()]);
        N = features.length;
        featuresArr = Arrays.asList(features);
    }

    // classify: given a text calculate the most probable class it fits in
    public String classify(String text){
        List<String> tokens = tokenize(text);
        // calculate p(w_1,w_2,...,w_n|c_j)p(c_j) for each class
        double [] probabilities = new double[cats.length];
        for (int c=0 ; c< cats.length ; c++){
            probabilities[c] = catFrequencies[c];
            for(String token : tokens){
                if(featuresArr.contains(token)) {
                    int w = featuresArr.indexOf(token);
                    probabilities[c] += log(likelihoods[c][w]);
                }
            }
            //System.out.format("%s : %f\n", cats[c],probabilities[c]);
        }
        // find the argmax p(w_k|c_j)p(c_j)
        return cats[maxIndex(probabilities)];
    }

    // assumes probabilities is not null or empty
    private int maxIndex(double[] probabilities) {
        int max = 0;
        for(int i =1;i<probabilities.length;i++){
            if (probabilities[i]> probabilities[max])
                max = i;
        }
        return max;
    }

    public void learn(HashMap<String, ArrayList<String>> dataset){
        cats = dataset.keySet().toArray(new String[dataset.size()]);
        catFrequencies = new int[cats.length];
        cats_indexes = new HashMap<String, Integer>();
        trainingSet = new String[cats.length];
        //calculate P(c_i) and append texts of each class
        for (int i=0 ; i< cats.length ; i++){
            catFrequencies[i] = dataset.get(cats[i]).size();
            cats_indexes.put(cats[i],i);
            // build one big String from all documents
            trainingSet[i] = Arrays.toString(dataset.get(cats[i]).toArray());
        }
        //calculate P(w_k|c_i) terms
        featureCounts = new int[cats.length][features.length];
        calculateFeatureLikelihoodEstimates();
    }
    //calculate P(w_k|c_i) terms
    public void calculateFeatureLikelihoodEstimates() {
        for (int c = 0; c < trainingSet.length; c++) {
            // calculate featureCounts per class featureCounts[c_i]
            count(c);
        }
        // calculate likelihoods
        likelihoods = new double[cats.length][features.length];
        for (int c = 0; c<likelihoods.length ; c++){
            int N_c = 0; // total feature counts for class c
            for(int i = 0; i < featureCounts[c].length; i++){
                N_c += featureCounts[c][i];
            }
            for (int w = 0; w<likelihoods[c].length ; w++){
                likelihoods[c][w] = (featureCounts[c][w] + alfa)/(N_c + (alfa*N));
            }
        }
    }

    // count the number of occurrences of features for class c
    public void count(int c) {
        String text = trainingSet[c];
        List<String> tokens = tokenize(text);
        for (String token : tokens) {
            if(featuresArr.contains(token)) {
                int w = featuresArr.indexOf(token);
                featureCounts[c][w] += 1;
            }
        }
    }

    private List<String> tokenize(String text) {
        ZemberekLexer lexer = new ZemberekLexer();
        List<String> tokens = lexer.tokenStrings(text);
        return tokens;
    }
}