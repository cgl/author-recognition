package Ngrams;

import com.google.common.io.Resources;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by cagil on 08/01/16.
 */
public class HuffmanCode {

    // alphabet size of extended ASCII
    private static final int N = 256;

    HuffmanTree root;
    String text;
    BitSet compressedText;
    String decodedText;
    HashMap<Character,String> codebook;
    private boolean debug = false;

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    public void setText(String text) {
        this.text = text;
    }

    public static class HuffmanTree implements Comparable<HuffmanTree>, Serializable {
        char ch;
        int freq;
        HuffmanTree left;
        HuffmanTree right;

        public HuffmanTree(char ch, int freq, HuffmanTree left, HuffmanTree right) {
            this.ch = ch;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        public boolean isLeaf(){
            return (left == null) && (right == null);
        }

        @Override
        public int compareTo(HuffmanTree other) {
            return this.freq - other.freq;
        }

        @Override
        public String toString() {
            return "HuffmanTree{" +
                    "ch=" + ch +
                    ", freq=" + freq +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
        }

        public HuffmanTree getLeft() {
            return left;
        }

        public HuffmanTree getRight() {
            return right;
        }

        public String getText() {
            return ch+":"+freq;
        }

        public void print(HuffmanTree root)
        {
            List<List<String>> lines = new ArrayList<>();
            List<HuffmanTree> level = new ArrayList<>();
            List<HuffmanTree> next = new ArrayList<HuffmanTree>();

            level.add(root);
            int nn = 1;

            int widest = 0;

            while (nn != 0) {
                List<String> line = new ArrayList<String>();

                nn = 0;

                for (HuffmanTree n : level) {
                    if (n == null) {
                        line.add(null);

                        next.add(null);
                        next.add(null);
                    } else {
                        String aa = String.valueOf(n.getText());
                        line.add(aa);
                        if (aa.length() > widest) widest = aa.length();

                        next.add(n.getLeft());
                        next.add(n.getRight());

                        if (n.getLeft() != null) nn++;
                        if (n.getRight() != null) nn++;
                    }
                }

                if (widest % 2 == 1) widest++;

                lines.add(line);

                List<HuffmanTree> tmp = level;
                level = next;
                next = tmp;
                next.clear();
            }

            int perpiece = lines.get(lines.size() - 1).size() * (widest + 4);
            for (int i = 0; i < lines.size(); i++) {
                List<String> line = lines.get(i);
                int hpw = (int) Math.floor(perpiece / 2f) - 1;

                if (i > 0) {
                    for (int j = 0; j < line.size(); j++) {

                        // split node
                        char c = ' ';
                        if (j % 2 == 1) {
                            if (line.get(j - 1) != null) {
                                c = (line.get(j) != null) ? '┴' : '┘';
                            } else {
                                if (j < line.size() && line.get(j) != null) c = '└';
                            }
                        }
                        System.out.print(c);

                        // lines and spaces
                        if (line.get(j) == null) {
                            for (int k = 0; k < perpiece - 1; k++) {
                                System.out.print(" ");
                            }
                        } else {

                            for (int k = 0; k < hpw; k++) {
                                System.out.print(j % 2 == 0 ? " " : "─");
                            }
                            System.out.print(j % 2 == 0 ? "┌" : "┐");
                            for (int k = 0; k < hpw; k++) {
                                System.out.print(j % 2 == 0 ? "─" : " ");
                            }
                        }
                    }
                    System.out.println();
                }

                // myPrint line of numbers
                for (int j = 0; j < line.size(); j++) {

                    String f = line.get(j);
                    if (f == null) f = "";
                    int gap1 = (int) Math.ceil(perpiece / 2f - f.length() / 2f);
                    int gap2 = (int) Math.floor(perpiece / 2f - f.length() / 2f);

                    // a number
                    for (int k = 0; k < gap1; k++) {
                        System.out.print(" ");
                    }
                    System.out.print(f);
                    for (int k = 0; k < gap2; k++) {
                        System.out.print(" ");
                    }
                }
                System.out.println();

                perpiece /= 2;
            }
        }
    }

    // constructor for compressing from a file
    public HuffmanCode(Path filename) throws IOException {
        // read text from file
        this.text = new String(Files.readAllBytes(filename));
        codebook = new HashMap<Character,String>();
    }
    // constructor for decoding from a file
    public HuffmanCode(String compressedFilename, String treeFilename) throws IOException, ClassNotFoundException {
        // deserialize HuffmanTree from treeFilename
        deserializeHuffmanTree(treeFilename);
        //  read compressed input from  String compressedFilename
        String path = Resources.getResource(compressedFilename).getPath();
        this.compressedText = (BitSet) new ObjectInputStream(new FileInputStream(path)).readObject();
    }

    // constructor for compressing given text
    public HuffmanCode(String text) {
        this.text = text;
        codebook = new HashMap<Character,String>();
    }

    /* HuffmanCode compress input.txt output tree ---> Compresses file input.txt to a file output and serialize Tree into file tree
     * HuffmanCode expand output tree             ---> Expands binary file output to StdOut
     *
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args[0].equals("expand")) {  //Expands binary file output to StdOut
            String compressedInFilename = args[1];
            String treeInFilename = args[2];
            HuffmanCode huff = new HuffmanCode(compressedInFilename, treeInFilename);
            huff.expand();
        }
        else if (args[0].equals("compress")) {
            String filename = args[1];
            String compressedOutFilename = args[2];
            String treeOutFilename = args[3];
            HuffmanCode huff = new HuffmanCode(new File(filename).toPath());
            huff.compress(compressedOutFilename,treeOutFilename);
        }
        else throw new IllegalArgumentException("Illegal command line argument");
    }

    void compress(String compressedFilename, String treeOutFilename) throws IOException {
        myPrint("Compressing "+text);
        // check if HuffmanTree is built?
        if(root == null) { // can we use previously built tree for different text?
            // find frequencies of chars
            int[] freqs = calculateFrequencies();
            // build HuffmanTree
            buildHuffmanTree(freqs);
            // built HashMap<Character,String> or String[]
            buildCodebook("",root);
            if(debug)
                myPrint(String.valueOf(codebook));
        }
        // encode input    (String compressedFilename)
        encode();
        // write encoded input to file
        String path = Resources.getResource(compressedFilename).getPath();
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(path));
        outputStream.writeObject(compressedText);

        // serialize HuffmanTree     (String treeOutFilename)
        serializeHuffmanTree(treeOutFilename);

        // extra check if encoded file is smaller than the original

    }
    // Build the codebook that includes char: codeword mappings
    // HashMap<Character,String> or String[]
    public void buildCodebook(String codeword,HuffmanTree tree) {
       if (! tree.isLeaf()){
           buildCodebook(codeword+'0',tree.left);
           buildCodebook(codeword+'1',tree.right);
       }
        else {
           codebook.put(tree.ch,codeword);
       }
    }

    public void encode() {
        final StringBuffer stringBuilder = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            stringBuilder.append(codebook.get(text.charAt(i)));
        }
        stringBuilder.append("1"); // for bitSet boundry
        compressedText = new BitSet(stringBuilder.length());
        for (int i = 0; i < stringBuilder.length(); i++) {
            if(stringBuilder.charAt(i) == '1')
                compressedText.set(i);
        }
        if(debug){
            myPrint(String.valueOf(stringBuilder));
            myPrint(String.format("%d, %d",stringBuilder.length(),compressedText.length()));
            final StringBuffer stringB = new StringBuffer();
            for(int i=0; i< compressedText.length(); i++) {
                if(compressedText.get(i))
                    stringB.append("1");
                else
                    stringB.append("0");
            }
            myPrint(String.valueOf(stringB));
        }
    }


    public void buildHuffmanTree(int[] freqs) {//Collections.reverseOrder()
        PriorityQueue<HuffmanTree> priQueue = new PriorityQueue<HuffmanTree>();
        int n = 0; // number of codewords
        for (int i = 0; i < freqs.length; i++) {
            if (freqs[i] > 0) {
                n++;
                HuffmanTree node = new HuffmanTree((char) i, freqs[i], null, null);
                priQueue.add(node);
                //myPrint(i+":"+String.valueOf((char) i));
            }
        }
        HuffmanTree x; HuffmanTree y;  HuffmanTree z = null ;
        for (int i = 1; i < n; i++) {
            x = priQueue.poll();
            y = priQueue.poll();
            z = new HuffmanTree('\0',x.freq+y.freq,x,y);
            priQueue.add(z);
        }
        z.print(z);
        root = z;
    }

    // find frequencies of chars in input string
    public int[] calculateFrequencies() {
        int [] freqs = new int[N];
        for (int i = 0; i < text.length(); i++) {
            freqs[text.charAt(i)]+=1;
        }
        return freqs;
    }

    /************************************************************
    *
    *  Decodes the given binary file with the given Huffman Tree
     ***********************************************************/
    private void expand(){
        // check if HuffmanTree is null?  give error
        if(root==null)
            myPrint("Please make sure the tree is present");
        // deserialize HuffmanTree    --> done in constructor
        // built HashMap<String,Character> or String[] TO DO think about this --> done in constructor
        // decode input
        decode();
        // write decoded input to StdOut
        myPrint(decodedText);
    }

    public void decode() {
        int size = compressedText.length();
        decodedText = "";
        HuffmanTree tree = this.root;
        for(int i=0; i< compressedText.length(); i++){
            if(compressedText.get(i))
                tree = tree.right;
            else
                tree = tree.left;
            if (tree.isLeaf()) {
                decodedText += tree.ch;
                tree = root;
            }
        }
        myPrint(decodedText);
    }

    private void innerDecode(HuffmanTree tree, String decodedText) {
        int size = compressedText.length();
        for(int i=0; i<compressedText.length(); i++){
            if (tree.isLeaf()) {
                decodedText += tree.ch;
                tree = root;
            }
            if(compressedText.get(i))
                tree = tree.right;
            else
                tree = tree.left;
        }
    }

    private void serializeHuffmanTree(String treeOutFilename) {
        try(
                ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(treeOutFilename)))
        ){
            oos.writeObject(root);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deserializeHuffmanTree(String treeFilename) {
        try(
                ObjectInputStream oos = new ObjectInputStream(new BufferedInputStream(new FileInputStream(treeFilename)))
        ){
            root = (HuffmanTree) oos.readObject();
            oos.close();
        }catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public void myPrint(String i) {
        System.out.println(i);
    }

}
