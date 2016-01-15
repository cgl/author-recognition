import authorRecognition.Dataset;
import authorRecognition.NaiveBayesClassifier;
import authorRecognition.NbClassifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by cagil on 07/01/16.
 */
public class TestNaiveBayes {

    public void testnbc() throws IOException {
        NbClassifier nbc = new NbClassifier();
        HashMap<String, ArrayList<String>> datasets = Dataset.prepareDataset("/Users/cagil/Documents/boun/yeterlilik/author_recognition/69yazar/test_texts");
        HashMap<String, ArrayList<String>> dataset =  new HashMap<String, ArrayList<String>>();
        ArrayList<String> yazilar = new ArrayList<String> ();
        yazilar.add("ya ve");
        dataset.put("ayseArman", yazilar);
        nbc.calculateFeatureVectors(dataset);

    }

    public static void main(String[] args) throws IOException {
        String datafile = "src/main/resources/Dataset_from_69_yazar";
        HashMap<String, ArrayList<String>> dataset = Dataset.prepareDataset(datafile);
        NaiveBayesClassifier naive = new NaiveBayesClassifier();
        naive.learn(dataset);
        printConfusionMatrix(naive);
        //printFeatureCounts(naive);

    }

    private static void printConfusionMatrix(NaiveBayesClassifier naive) {
        int [][] confusionMatrix = new int[naive.cats.length][naive.cats.length];
        int total = 0 ; int correct = 0;
        for (String author : Dataset.getDataset_test().keySet()) {
            for (String text : Dataset.getDataset_test().get(author)) {
                String guess = naive.classify(text);
                System.out.format("Author: %s guess: %s\n", author, guess);
                if (author.equals(guess))
                    correct++;
                confusionMatrix[naive.cats_indexes.get(author)][naive.cats_indexes.get(guess)] += 1;
                total++;
            }
        }
        System.out.format("%d correct out of %d (%.2f). Feature Count:%d\n",correct,total,(float)correct/total,naive.featuresArr.size());
        for (int i = 0; i<confusionMatrix[0].length;i++) {
            System.out.format("%s\t%s\n",naive.cats[i], Arrays.toString(confusionMatrix[i]));
        }
    }

    private static void printFeatureCounts(NaiveBayesClassifier naive) {
        int[][] featureCounts = naive.getFeatureCounts();
        for(int i =0;i<featureCounts.length;i++){
            boolean flag =true;
                for(int j =0;j<featureCounts[0].length;j++){
                    System.out.format("%d\t",featureCounts[i][j]);
                    if(featureCounts[i][j]>0)
                        flag = false;
            }
            System.out.format("\n");
        }
    }

}
