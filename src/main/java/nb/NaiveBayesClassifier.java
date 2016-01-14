package nb;

import com.google.common.io.Resources;
import zemberek.core.io.SimpleTextReader;
import zemberek.morphology.ambiguity.Z3MarkovModelDisambiguator;
import zemberek.morphology.apps.TurkishMorphParser;
import zemberek.morphology.apps.TurkishSentenceParser;
import zemberek.morphology.parser.MorphParse;
import zemberek.morphology.parser.SentenceMorphParse;
import zemberek.tokenizer.SentenceBoundaryDetector;
import zemberek.tokenizer.SimpleSentenceBoundaryDetector;
import zemberek.tokenizer.ZemberekLexer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    TurkishSentenceParser sentenceParser;
    SentenceBoundaryDetector detector;

    // fill features
    // update N with count(features)
    public NaiveBayesClassifier() throws IOException {
        URL multi_line_text_file = Resources.getResource("fWords.txt");
        List<String> f_list = new SimpleTextReader(multi_line_text_file.getFile(), "windows-1254").asStringList();
        features = f_list.toArray(new String[f_list.size()]);
        //String[] featsToRemeve = removeUnused();
        //f_list.removeAll(Arrays.asList(featsToRemeve));
        //features = f_list.toArray(new String[f_list.size()]);
        N = features.length;
        featuresArr = Arrays.asList(features);
        //Morphology
        TurkishMorphParser morphParser = TurkishMorphParser.createWithDefaults();
        Z3MarkovModelDisambiguator disambiguator = new Z3MarkovModelDisambiguator();
        this.sentenceParser = new TurkishSentenceParser(
                morphParser,
                disambiguator
        );
        this.detector = new SimpleSentenceBoundaryDetector();
    }

    public int[][] getFeatureCounts() {
        return featureCounts;
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
        //ArrayList<String> tokens = returnStems(text);
        List<String> tokens = tokenize(text);
        for (String token : tokens) {
            if(featuresArr.contains(token)) {
                int w = featuresArr.indexOf(token);
                featureCounts[c][w] += 1;
            }
        }
    }

    public ArrayList<String> returnStems(String text) {
        List<String> sentences = detector.getSentences(text);
        ArrayList<String> lemmasStems = new ArrayList<>();
        for (String sentence : sentences) {
            SentenceMorphParse sentenceParse = sentenceParser.parse(sentence);
            //System.out.println("Best Parse");
            List<MorphParse> morphParses = sentenceParser.bestParse(sentence);
            for (MorphParse morphParse : morphParses) {
                //System.out.format("%s %s\n", morphParse.getLemma(), morphParse.getStemAndEndıng().stem);
                //lemmasStems.add(morphParse.getLemma());
                lemmasStems.add(morphParse.getStemAndEndıng().stem);
            }
        }
        return lemmasStems;
    }

    public String[] removeUnused(){
        int [] unusedFeatures = new int[]{11,16,42,54,57,62,72,96,97,103,108,110,112,114,115,120,122,126,130,131,133,134,135,139,140,141,
                143,145,147,152,155,157,163,168,169,170,171,172,175,176,182,183,185,193,195,200,201,202,203,204,206,207,
                208,210,211,213,215,216,217,221,222,223,225,226,227,228,229,230,232,234,236,237,238,239,241,244,245,246,
                247,249,251,253,254,255,256,257,258,259,260,261,262,266,268,269,270,271,274,277,278,280,283,286,287,291,
                292,294,295,296,298,299,300,301,302,303,304,305,306,307,309,310,312,315,317,318,319,321,322,323,326,327,
                328,329,330,332,334,335,336,337,338,339,340,342,344,345,346,347,348,349,350,352,353,354,355,356,357,358,
                359,360,361,362,363,364,365,366,367,368,369,370,371,372,373,374,375,376,377,378,379,381,382,383,385,386,
                387,388,389,390,391,392,393,395,396,397,398,399,400,401,402,403,405,406,407,408,409,410,412,414,415,416,
                417,418,419,420,421,422,423,424,426,427,428,429,430,431,432,434,435,436,437,438,439,440,441,442,443,444,
                445,446,447,448,449,451,453,454,455,456,457,458,459,460,461,462,463,464,465,466,467,468,469,470,471,472,
                474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,499,501,
                502,503,505,506,507,508,509,510,511,512,513,514,516,517,518,519,520,521,522,523,524,525,526,528,529,530,
                531,532,533,534,535,536,537,538,539,540,541,542,543,544,545,546,547,548,549,550,552,553,554,555,556,557,
                558,559,560,561,562,563,564,565,566,567,568,569,570,571,572,573,574,576,577,578,579,580,581,582,583,584,
                585,586,587,588,589,590,591,592,593,594,595,596,597,598,599,600,601,602,603,604,605,606,607,608,609,610,
                611,612,613,614,615,616,617,618,619};
        String [] toRemove = new String[unusedFeatures.length];
        for (int i =0; i<unusedFeatures.length;i++) {
            toRemove[i] = features[unusedFeatures[i]];
        }
        return toRemove;
    }

    private List<String> tokenize(String text) {
        ZemberekLexer lexer = new ZemberekLexer();
        List<String> tokens = lexer.tokenStrings(text);
        return tokens;
    }
}