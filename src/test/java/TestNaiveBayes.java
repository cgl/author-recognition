import nb.NaiveBayesClassifier;
import nb.NbClassifier;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by cagil on 07/01/16.
 */
public class TestNaiveBayes {
    public static HashMap<String,ArrayList<String>> dataset_test = new HashMap<String,ArrayList<String>>();
    public static HashMap<String, ArrayList<String>> prepareDataset(String filename){
        HashMap<String,ArrayList<String>> dataset_training = new HashMap<String,ArrayList<String>>();
        final File folder = new File(filename);
        int a = 0;
        for (final File folderEntry : folder.listFiles()) {
            if (folderEntry.isDirectory()) {
                ArrayList<String> training = new ArrayList<String>();
                ArrayList<String> test = new ArrayList<String>();
                for (final File fileEntry : folderEntry.listFiles()) {
                    if (fileEntry.isFile()) {
                        try {
                            String contents = new String(Files.readAllBytes(Paths.get(fileEntry.getAbsolutePath())), Charset.forName("windows-1254"));
                            if(a++ % 10 < 6 )
                                training.add(contents);
                            else    {
                                test.add(contents);
                            }
                        } catch (IOException e) {
                            System.out.println(e);
                        }
                        //System.out.println(fileEntry.getAbsoluteFile());
                    }
                }
                dataset_training.put(folderEntry.getName(), training);
                dataset_test.put(folderEntry.getName(), test);

                //System.out.println(folderEntry.getName());
            }
        }

        return dataset_training;
    }

    public void testnbc() throws IOException {
        NbClassifier nbc = new NbClassifier();
        HashMap<String, ArrayList<String>> datasets = prepareDataset("/Users/cagil/Documents/boun/yeterlilik/author_recognition/69yazar/test_texts");
        HashMap<String, ArrayList<String>> dataset =  new HashMap<String, ArrayList<String>>();
        ArrayList<String> yazilar = new ArrayList<String> ();
        yazilar.add("ya ve");
        dataset.put("ayseArman", yazilar);
        nbc.calculateFeatureVectors(dataset);

    }

    public static void main(String[] args) throws IOException {
        String testfile = "/Users/cagil/Documents/boun/yeterlilik/author_recognition/69yazar/test_texts";
        String datafile = "/Users/cagil/Documents/boun/yeterlilik/author_recognition/69yazar/raw_texts";
        datafile = "/Users/cagil/Documents/boun/yeterlilik/author_recognition/Dataset_from_69_yazar";
        HashMap<String, ArrayList<String>> dataset = prepareDataset(datafile);
        NaiveBayesClassifier naive = new NaiveBayesClassifier();
        naive.learn(dataset);
        int [][] confusionMatrix = new int[naive.cats.length][naive.cats.length];
        int total = 0 ; int correct = 0;
        for (String author : dataset_test.keySet())
            for (String text : dataset_test.get(author)) {
                String guess = naive.classify(text);
                System.out.format("Athor: %s guess: %s\n", author, guess);
                if (author.equals(guess))
                    correct++;
                confusionMatrix[naive.cats_indexes.get(author)][naive.cats_indexes.get(guess)] += 1;
                total++;
            }
        System.out.format("%d correct out of %d (%.2f). Feature Count:%d\n",correct,total,(float)correct/total,naive.featuresArr.size());
        for (int i = 0; i<confusionMatrix[0].length;i++) {
            System.out.format("%s\t%s\n",naive.cats[i],Arrays.toString(confusionMatrix[i]));
        }
    }

}
