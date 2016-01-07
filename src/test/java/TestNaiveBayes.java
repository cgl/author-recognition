import nb.NbClassifier;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cagil on 07/01/16.
 */
public class TestNaiveBayes {
    public static void main(String[] args) {
        ArrayList<String> texts = new ArrayList<String>();
        NbClassifier nbc = new NbClassifier();
        HashMap<String,ArrayList<String>> dataset = new HashMap<String,ArrayList<String>>();
        final File folder = new File("/Users/cagil/Documents//boun/yeterlilik/author_recognition/69yazar/test_texts");
        for (final File folderEntry : folder.listFiles()) {
            if (folderEntry.isDirectory()) {
                texts = new ArrayList<String>();
                for (final File fileEntry : folderEntry.listFiles()) {
                    if (fileEntry.isFile()) {
                        try {
                            String contents = new String(Files.readAllBytes(Paths.get(fileEntry.getAbsolutePath())), Charset.forName("windows-1254"));
                            texts.add(contents);
                        } catch (IOException e) {
                            System.out.println(e);
                        }
                        System.out.println(fileEntry.getAbsoluteFile());
                    }
                }
                System.out.println(folderEntry.getName());

            }
        }

        dataset.put("melihAsik", texts);
        nbc.calculateFeatureVectors(dataset);
    }

}
