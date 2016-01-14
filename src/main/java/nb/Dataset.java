package nb;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cagil on 13/01/16.
 */
public class Dataset {
    public static HashMap<String, ArrayList<String>> dataset_test = new HashMap<String, ArrayList<String>>();
    public static HashMap<String, ArrayList<String>> dataset_training = new HashMap<String, ArrayList<String>>();

    public static HashMap<String, ArrayList<String>> prepareDataset(String filename) {
        dataset_training = new HashMap<String, ArrayList<String>>();
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
                            if (a++ % 10 < 6)
                                training.add(contents);
                            else {
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

    public static void datasetWithComplexFeatures(String filename,HashMap<String, ArrayList<String>> dataset) throws IOException {
        String datafile = "/Users/cagil/Documents/boun/yeterlilik/author_recognition/Dataset_from_69_yazar";
        Dataset.prepareDataset(datafile);
        StringBuilder training = new StringBuilder();
        int a = 0;
        for (String author : dataset.keySet()) {
            for (String text : dataset.get(author)) {
                Document doc = new Document(author, text);
                float[] vec = doc.getComplexityFeatures();
                for (int i = 0; i < vec.length; i++) {
                    training.append(vec[i]+" ");
                }
                training.append(a + "\n");
            }
            a++; //change author
        }
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
        oos.write(String.valueOf(training).getBytes());
        System.out.println(String.valueOf(training));

    }


    public static HashMap<String, ArrayList<String>> getDataset_test() {
        return dataset_test;
    }

    public static HashMap<String, ArrayList<String>> getDataset_training() {
        return dataset_training;
    }
    public static void main(String[] args) throws IOException {
        //datasetWithComplexFeatures("/Users/cagil/Documents/boun/yeterlilik/author_recognition/trainin.txt",dataset_training);
        System.out.println();
        datasetWithComplexFeatures("/Users/cagil/Documents/boun/yeterlilik/author_recognition/test.txt",dataset_test);
    }
}

