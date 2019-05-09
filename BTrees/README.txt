# FinalProject CS321
# Authors: Zach Luciano, Brendon Yoshino, Mason Humpherys

# Description:

 The project is a BTree creation tool that reads in GeneBank files and takes an input of
 sequence length to create substrings of the gene sequences and stores them into a BTree
 for later searching using the provided class.

# Usage

 To begin using the program you need to first compile the classes using the following command
---
javac GeneBankCreateBTree.java
---

 Then the project can be run using some command line arguments
---
java GeneBankCreateBTree <degree> <gbk-file> <sequence-length> [<debug-level>]
---
 The degree is the degree of BTree to use, if 0 is inputted the program uses a read and write
 size of 4096 bytes as default to find the optimal degree. The gbk-file is the file to use as the
 input to read from.

 Once the BTree is created then you can begin searching through it to find how many times a
 certain subsequence appears in the gene sequence. First compile the search program using
---
javac GeneBankSearch.java
---

 Then you can begin searching using the following command
---
java GeneBankSearch <btree-file> <query-file> [<debug-level>]
---
 The input btree-file is the file created by the first class that writes a btree file, the
 file name is outputted by the program when the btree is created. The query file is text document
 created by you that has different sequences to search, all sequences must be the same length
 and only 1 sequence per line.

# Testing


