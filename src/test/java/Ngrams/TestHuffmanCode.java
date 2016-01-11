package Ngrams;

import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by cagil on 08/01/16.
 * http://www.vogella.com/tutorials/JUnit/article.html#unittesting
 */
public class TestHuffmanCode {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //testFrequencyCount();
        String testText = "ali eve giderkennnnn";
        //testText = "rkee";
        //testText =  "aabc";

        testText = "I have an exam today.";
        //HuffmanCode hfc = new HuffmanCode(testText);
        //hfc.compress("huffman.out", "tree.out");

        //testBuildHuffmanTree(hfc);
        //testBuildCodebook(hfc);
        //hfc.setDebug(true);
        //testEncoding(hfc,testText);
        testDecoding(testText);
    }

    @Test
    private static void testDecoding(String testText) throws IOException, ClassNotFoundException {
        //HuffmanCode hfc = new HuffmanCode(testText);
        //hfc.compress("huffman.out", "tree.out");
        HuffmanCode hfc2 = new HuffmanCode("huffman.out", "tree.out");
        hfc2.decode();
        assertEquals(testText,hfc2.decodedText);

    }
    @Test
    private static void testEncoding(HuffmanCode hfc, String testText) throws IOException {
        hfc.compress("huffman.out", "tree.out");
        hfc.decode();
        assertEquals(hfc.text,hfc.decodedText);
        assertEquals(testText,hfc.decodedText);

    }

    public static void testBuildCodebook(HuffmanCode hfc) throws IOException {

        hfc.compress("huffman.out", "tree.out");
        //System.out.format("Codebook: %s",hfc.codebook);
    }

    @Test
    public static void testBuildHuffmanTree(HuffmanCode hfc) {
        int[] counts = new int[256];
        for(int i= 0; i<hfc.text.length();i++){
           counts[hfc.text.charAt(i)]+=1;
        }
        HashMap<Character,Integer> charFreqs = findUniqueChars(hfc.text);

        int[] freqs = hfc.calculateFrequencies();
        hfc.buildHuffmanTree(freqs);
        checkLeafs(hfc.root,charFreqs);
        assertEquals(charFreqs.size(),0);
        //System.out.format("%s\n",hfc.root);
    }

    @Test
    private static void checkLeafs(HuffmanCode.HuffmanTree root, HashMap<Character, Integer> charFreqs) {
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
        HuffmanCode hfc1 = new HuffmanCode("qbaca");
        int[] freqs = hfc1.calculateFrequencies();
        assertEquals(freqs['q'],1);
        assertEquals(freqs['b'],1);
        assertEquals(freqs['c'],1);
        assertEquals(freqs['a'],2);
        assertEquals(freqs['e'],0);
    }
}
