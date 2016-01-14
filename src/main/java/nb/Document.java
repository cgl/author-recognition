package nb;

import zemberek.tokenizer.SentenceBoundaryDetector;
import zemberek.tokenizer.SimpleSentenceBoundaryDetector;
import zemberek.tokenizer.ZemberekLexer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

/**
 * Created by cagil on 13/01/16.
 */
public class Document {
    String author;
    String text;
    List<String> sentences;
    List<String> tokens;
    float numberOfSentences ;
    float numberOfWords ;
    float avgNumberOfWordsInASentence ;
    float avgWordLength ;
    float commaRatio;
    float dotRatio ;
    float numberOfUniqueWords;
    float numberOfUniqueWordsRatio;
    float [] complexityFeatures;

    public Document(String author, String text) {
        this.author = author;
        this.text = text;
        calculateComplexityFeatures();
    }

    /*
    SM1: # List<String> sentences
    SM2 # of words
    SM3 Average # of words in a sentence
    SM4 Average word length
    SM22 # of point
    SM23 # of comas
    SM6 # of different words
    SM7 Word richness
    */
    public void calculateComplexityFeatures(){
        tokens = tokenize(text);
        sentences = detectSentences();
        numberOfSentences = sentences.size();
        numberOfWords = tokens.size();
        avgNumberOfWordsInASentence = calculateAvgNumberOfWordsInASentence();
        avgWordLength = calculateAvgWordLength();
        commaRatio = calculateTokenRatio(",");
        dotRatio = calculateTokenRatio(".");
        numberOfUniqueWords = new HashSet<String>(tokens).size();
        numberOfUniqueWordsRatio = numberOfUniqueWords/numberOfWords;
        complexityFeatures = new float[]{numberOfSentences,numberOfWords,avgNumberOfWordsInASentence,
                                            avgWordLength,commaRatio,dotRatio,numberOfUniqueWords,numberOfUniqueWordsRatio};
    }


    private float calculateTokenRatio(String s) {
        float sum = 0;
        for (String token : tokens) {
            if(token.equals(s))
                sum+=1;
        }
        return  sum/tokens.size();
    }

    private float calculateAvgWordLength() {
        float sum = 0;
        for (String token : tokens) {
            sum += token.length();
        }
        return sum/tokens.size();
    }

    private float calculateAvgNumberOfWordsInASentence() {
        float sum=0;
        for (String sentence : sentences) {
             sum += tokenize(sentence).size();
        }
        return sum/sentences.size();
    }

    private List<String> tokenize(String text) {
        ZemberekLexer lexer = new ZemberekLexer();
        List<String> tokens = lexer.tokenStrings(text);
        return tokens;
    }

    private List<String> detectSentences() {
        SentenceBoundaryDetector detector = new SimpleSentenceBoundaryDetector();
        List<String> sentences = detector.getSentences(text);
        return sentences;
    }

    public float[] getComplexityFeatures() {
        return complexityFeatures;
    }

    @Override
    public String toString() {
        return "Document{" +
                "author='" + author + '\'' +
                //", text='" + text + '\'' +
                //", sentences=" + sentences +
                //", tokens=" + tokens +
                ", numberOfSentences=" + numberOfSentences +
                ", numberOfWords=" + numberOfWords +
                ", avgNumberOfWordsInASentence=" + avgNumberOfWordsInASentence +
                ", avgWordLength=" + avgWordLength +
                ", commaRatio=" + commaRatio +
                ", dotRatio=" + dotRatio +
                ", numberOfUniqueWords=" + numberOfUniqueWords +
                '}';
    }

    public static void main(String[] args) throws IOException {
        String filename = args[0];
        String author = args[1];
        filename ="/Users/cagil/Documents/boun/yeterlilik/author_recognition/Dataset_from_69_yazar/gulseBirsel/1.txt";
        author="gulseBirsel";
        byte[] encoded = Files.readAllBytes(Paths.get(filename));
        Document doc = new Document(author,new String(encoded, "windows-1254"));
        System.out.println(doc.toString());
        System.out.println(doc.getComplexityFeatures());

    }

    }
