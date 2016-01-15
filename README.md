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
    
## Author Recognition

A naive bayes classifier which uses Function words feature.

13 correct out of 20 (0.65). Feature Count:620

Confusion matrix:
yigitBulut	[2, 1, 0, 0, 1]
canDundar	[0, 2, 1, 0, 1]
gulseBirsel	[0, 0, 4, 0, 0]
yilmazOzdil	[0, 0, 1, 2, 1]
nihalKaraca	[0, 0, 1, 0, 3]

Author: yigitBulut guess: yigitBulut
Author: yigitBulut guess: nihalKaraca
Author: yigitBulut guess: canDundar
Author: yigitBulut guess: yigitBulut
Author: canDundar guess: canDundar
Author: canDundar guess: nihalKaraca
Author: canDundar guess: gulseBirsel
Author: canDundar guess: canDundar
Author: gulseBirsel guess: gulseBirsel
Author: gulseBirsel guess: gulseBirsel
Author: gulseBirsel guess: gulseBirsel
Author: gulseBirsel guess: gulseBirsel
Author: yilmazOzdil guess: nihalKaraca
Author: yilmazOzdil guess: yilmazOzdil
Author: yilmazOzdil guess: yilmazOzdil
Author: yilmazOzdil guess: gulseBirsel
Author: nihalKaraca guess: nihalKaraca
Author: nihalKaraca guess: nihalKaraca
Author: nihalKaraca guess: nihalKaraca
Author: nihalKaraca guess: gulseBirsel

You can run the test class src/test/java/TestNaiveBayes.java from a ide that supports maven like Intellij or jou can run from command line:

     mvn compile
     mvn test

