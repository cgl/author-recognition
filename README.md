# exam

How to initilize:

    git clone git@github.com:cgl/exam.git
    
After downloading the repository, you can run the java code from a java IDE or from command line. 
Matlab codes are written and tested in octave. And for the python code please use python interpreter or ipython.

## Huffman Coding

For Huffman coding there are two different types of model implemented. One is the original version. 
For the word ‘exam’ it will encode ’e’, ’x’, ’a’, ’m’
The second version only encodes 2-gram character sets into Huffman codes. 
For the word ‘exam’ it will encode ’ex’, ’xa’, ’am’

    cd exam/src/main/java/
    javac huffman/HuffmanCode.java huffman/Huffman2Gram.java
    java huffman.HuffmanCode compress ../resources/huffman_test.txt ../resources/huffman.out ../resources/tree.out
    java huffman.Huffman2Gram compress ../resources/huffman_test.txt ../resources/huffman2.out ../resources/tree2.out
    java huffman/HuffmanCode expand ../resources/huffman.out ../resources/tree.out
    java huffman/Huffman2Gram expand ../resources/huffman2.out ../resources/tree2.out

The output files can be found in resource folder.
    
    ls -l ../resources
