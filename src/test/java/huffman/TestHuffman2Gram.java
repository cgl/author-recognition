package huffman;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by cagil on 08/01/16.
 * http://www.vogella.com/tutorials/JUnit/article.html#unittesting
 */
public class TestHuffman2Gram {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        boolean debug = false;
        //testFrequencyCount();
        String testText = "ali eve giderkennnnn";
        //testText = "rkee";
        testText =  "aabc";

        testText = "I have an exam today.";
        Huffman2Gram hfc = new Huffman2Gram(testText);
        //Huffman2Gram hfc = new Huffman2Gram(new File("/Users/cagil/work/author-recognition/src/main/resources/in.txt").toPath());
        hfc.setDebug(debug);
        hfc.compress("huffman2.out", "tree2.out");

        //testBuildHuffmanTree(hfc);
        //testBuildCodebook(hfc);
        //testEncoding(hfc,testText);
        testDecoding(testText,debug);
    }

    @Test
    private static void testDecoding(String testText, boolean debug) throws IOException, ClassNotFoundException {
        //Huffman2Gram hfc = new Huffman2Gram(testText);
        //hfc.compress("huffman.out", "tree.out");
        Huffman2Gram hfc2 = new Huffman2Gram("huffman.out", "tree.out");
        hfc2.setDebug(debug);
        hfc2.decode();
        assertEquals(testText,hfc2.decodedText);

    }
    @Test
    private static void testEncoding(Huffman2Gram hfc, String testText) throws IOException {
        hfc.compress("huffman.out", "tree.out");
        hfc.decode();
        assertEquals(hfc.text,hfc.decodedText);
        assertEquals(testText,hfc.decodedText);

    }

    public static void testBuildCodebook(Huffman2Gram hfc) throws IOException {

        hfc.compress("huffman.out", "tree.out");
        //System.out.format("Codebook: %s",hfc.codebook);
    }

    @Test
    public static void testBuildHuffmanTree(Huffman2Gram hfc) {
        int[] counts = new int[256];
        for(int i= 0; i<hfc.text.length();i++){
           counts[hfc.text.charAt(i)]+=1;
        }
        HashMap<Character,Integer> charFreqs = findUniqueChars(hfc.text);

        hfc.calculateFrequencies();
        hfc.buildHuffmanTree();
        checkLeafs(hfc.root,charFreqs);
        assertEquals(charFreqs.size(),0);
        //System.out.format("%s\n",hfc.root);
    }

    @Test
    private static void checkLeafs(Huffman2Gram.HuffmanTree root, HashMap<Character, Integer> charFreqs) {
         if(root.isLeaf()) {
             assertEquals((int) charFreqs.remove(root.ch), root.freq);
         }
        else {
             checkLeafs(root.getLeft(), charFreqs);
             checkLeafs(root.getRight(), charFreqs);
         }
    }

    private static HashMap<Character, Integer> findUniqueChars(String testText) {
        HashMap<Character,Integer> charFreqs = new HashMap<Character,Integer>();
        for(int i= 0; i<testText.length();i++){
            if (!charFreqs.containsKey(testText.charAt(i)))
                charFreqs.put(testText.charAt(i),0);
            charFreqs.put(testText.charAt(i),charFreqs.get(testText.charAt(i))+1);
        }
        return charFreqs;
    }

    @Test
    public static void testFrequencyCount() {
        //System.out.format("%c %d\n",testText.charAt(0),freqs[testText.charAt(0)]);
        Huffman2Gram hfc1 = new Huffman2Gram("qbaca");
        hfc1.calculateFrequencies();

        assertEquals(hfc1.getFreq('q'),1);
        assertEquals(hfc1.getFreq('b'),1);
        assertEquals(hfc1.getFreq('c'),1);
        assertEquals(hfc1.getFreq('a'),2);
        assertEquals(hfc1.getFreq('e'),0);
    }
}
