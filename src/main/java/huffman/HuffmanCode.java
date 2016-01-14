package huffman;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by cagil on 08/01/16.
 *
 * java Ngrams.HuffmanCode compress ../resources/huffman_test.txt ../resources/huffman.out tree.out
 * java Ngrams.Huffman2Gram compress ../resources/huffman_test.txt ../resources/huffman2.out tree2.out
 * java Ngrams/HuffmanCode expand ../resources/huffman.out tree.out
 * java Ngrams/Huffman2Gram expand ../resources/huffman2.out tree2.out
 *
 */
public class HuffmanCode {

    // alphabet size of extended ASCII
    public static int N = 1024;

    HuffmanTree root;
    String text;
    int[] charFreqs;
    BitSet compressedText;
    String decodedText;
    public HashMap<String, String> codebook;
    public boolean debug = false;

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    public void setText(String text) {
        this.text = text;
    }

    public static class HuffmanTree implements Comparable<HuffmanTree>, Serializable {
        String ch;
        int freq;
        HuffmanTree left;
        HuffmanTree right;

        public HuffmanTree(String ch, int freq, HuffmanTree left, HuffmanTree right) {
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
        this(new String(Files.readAllBytes(Paths.get(filename.toString()).toAbsolutePath())));

    }

    // constructor for compressing given text
    public HuffmanCode(String text) {
        this.text = text;
        codebook = new HashMap<>();
    }

    // constructor for decoding from a file
    public HuffmanCode(String compressedFilename, String treeFilename) throws IOException, ClassNotFoundException {
        // deserialize HuffmanTree from treeFilename
        deserializeHuffmanTree(treeFilename);
        //  read compressed input from  String compressedFilename
        //String path = Resources.getResource(compressedFilename).getPath();
        String path = Paths.get(compressedFilename).toAbsolutePath().toString();
        this.compressedText = (BitSet) new ObjectInputStream(new FileInputStream(path)).readObject();
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
        if(debug)
            myPrint("Compressing "+text);
        // check if HuffmanTree is built?
        if(root == null) { // can we use previously built tree for different text?
            // find frequencies of chars
            calculateFrequencies();
            // build HuffmanTree
            buildHuffmanTree();
            // built HashMap<Character,String> or String[]
            buildCodebook("",root);
            if(debug)
                myPrint(String.valueOf(codebook));
        }
        // encode input    (String compressedFilename)
        encode();
        // write encoded input to file
        //String path = Resources.getResource(compressedFilename).getPath();
        String path = Paths.get(compressedFilename).toAbsolutePath().toString();
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(path)); //To do
        outputStream.writeObject(compressedText);

        // serialize HuffmanTree     (String treeOutFilename)
        serializeHuffmanTree(treeOutFilename);

        // extra check if encoded file is smaller than the original

    }

    public void encode() {
        final StringBuffer stringBuilder = getStringToCompress();
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

    public StringBuffer getStringToCompress() {
        final StringBuffer stringBuilder = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            stringBuilder.append(codebook.get(String.valueOf(text.charAt(i))));
        }
        return stringBuilder;
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

    public void buildHuffmanTree() {//Collections.reverseOrder()
        PriorityQueue<HuffmanTree> priQueue = new PriorityQueue<HuffmanTree>();
        int n = 0; // number of codewords
        for (int i = 0; i < N; i++) {
            if (getFreq(i) > 0) {
                n++;
                HuffmanTree node = new HuffmanTree(getAtom(i), getFreq(i), null, null);
                priQueue.add(node);
                //myPrint(i+":"+String.valueOf((char) i));
            }
        }
        HuffmanTree x; HuffmanTree y;  HuffmanTree z = null ;
        for (int i = 1; i < n; i++) {
            x = priQueue.poll();
            y = priQueue.poll();
            z = new HuffmanTree(String.valueOf('\0'),x.freq+y.freq,x,y);
            priQueue.add(z);
        }
        if(debug)
            z.print(z);
        root = z;
    }

    public String getAtom(int i) {
        return String.valueOf((char) i);
    }

    public int getFreq(int i) {
        return charFreqs[i];
    }

    // find frequencies of chars in input string
    public void calculateFrequencies() {
        charFreqs = new int[N];
        for (int i = 0; i < text.length(); i++) {
            charFreqs[text.charAt(i)]+=1;
        }
    }

    /************************************************************
    *
    *  Decodes the given binary file with the given Huffman Tree
     ***********************************************************/
    public void expand(){
        // check if HuffmanTree is null?  give error
        if(root==null)
            myPrint("Please make sure the tree is present");
        // deserialize HuffmanTree    --> done in constructor
        // built HashMap<String,Character> or String[] TO DO think about this --> done in constructor
        // decode input
        decode();
        // write decoded input to StdOut
        if(debug)
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

    public String getText() {
        return text;
    }

    public HashMap<String, String> getCodebook() {
        return codebook;
    }
}
