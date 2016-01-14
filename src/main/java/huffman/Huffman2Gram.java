package huffman;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Created by cagil on 11/01/16.
 */
public class Huffman2Gram extends HuffmanCode {
    HashMap<String,String> codebook;
    HashMap<String,Integer> twoGramFreqs;
    String [] twoGrams; //init ve doldur

    public Huffman2Gram(Path filename) throws IOException {
        this(new String(Files.readAllBytes(filename)));
    }

    public Huffman2Gram(String text) {
        super(text);
    }


    public Huffman2Gram(String compressedFilename, String treeFilename) throws IOException, ClassNotFoundException {
        super(compressedFilename, treeFilename);
    }


    @Override
    public StringBuffer getStringToCompress() {
        final StringBuffer stringBuilder = new StringBuffer();
        for (int i = 0; i < getText().length()-1; i++) {
            String atom = text.substring(i,i+2);
            String codeword = super.codebook.get(atom);
            stringBuilder.append(codeword);
        }
        return stringBuilder;
    }

    @Override
    public String getAtom(int i) {
        return twoGrams[i];
    }

    @Override
    public int getFreq(int i) {
        return twoGramFreqs.get(twoGrams[i]);
    }

    // find frequencies of chars in input string
    @Override
    public void calculateFrequencies() {
        twoGramFreqs = new HashMap<String,Integer> ();
        for (int i = 0; i < text.length()-1; i++) {
            String key = text.substring(i,i+2);
            if(!twoGramFreqs.containsKey(key))
                twoGramFreqs.put(key,0);
            twoGramFreqs.put(key,twoGramFreqs.get(key)+1);
        }
        N = twoGramFreqs.size();
        twoGrams = twoGramFreqs.keySet().toArray(new String [N]);
    }

    /* HuffmanCode compress input.txt output.bin tree.bin ---> Compresses file input.txt to a file output and serialize Tree into file tree
     * HuffmanCode expand output.bin tree.bin             ---> Expands binary file output to StdOut
     *
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length == 0) {
            System.out.println("java HuffmanCode compress input.txt huffman.out tree.out");
            System.out.println("java HuffmanCode expand huffman.out tree.out");
        }
        else if (args[0].equals("expand")) {  //Expands binary file output to StdOut
            String compressedInFilename = args[1];
            String treeInFilename = args[2];
            Huffman2Gram huff = new Huffman2Gram(compressedInFilename, treeInFilename);
            huff.expand();
        }
        else if (args[0].equals("compress")) {
            String filename = args[1];
            String compressedOutFilename = args[2];
            String treeOutFilename = args[3];
            Huffman2Gram huff = new Huffman2Gram(new File(filename).toPath());
            huff.compress(compressedOutFilename,treeOutFilename);
        }
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
