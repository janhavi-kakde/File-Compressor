package huffmanFileCompressor;


import java.io.*;

public class HuffmanHeader {
    private huffman huffman;

    // Constructor to initialize Huffman with input and output filenames
    public HuffmanHeader(String inFileName, String outFileName) {
        // Initialize the Huffman object
        huffman = new huffman(inFileName, outFileName);
    }

    // Method to compress the input file using Huffman encoding
    public void compress() {
        try {
            // Step 1: Create an array of ASCII Nodes
            huffman.createArr();
            // Step 2: Create a min heap based on character frequencies
            huffman.createMinHeap();
            // Step 3: Build the Huffman tree
            huffman.createTree();
            // Step 4: Generate Huffman codes for each character
            huffman.createCodes();
            // Step 5: Save the encoded file
            huffman.saveEncodedFile();
        } catch (IOException e) {
            System.err.println("Error during compression: " + e.getMessage());
        }
    }

    // Method to decompress the encoded file using Huffman decoding
    public void decompress() {
        try {
            // Step 1: Save the decoded file by reconstructing the Huffman tree
            huffman.saveDecodedFile();
        } catch (IOException e) {
            System.err.println("Error during decompression: " + e.getMessage());
        }
    }

    // Main method to test the compress and decompress functions
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java HuffmanWrapper <compress|decompress> <inputFile> <outputFile>");
            return;
        }

        String operation = args[0];
        String inFileName = args[1];
        String outFileName = args[2];

        HuffmanHeader wrapper = new HuffmanHeader(inFileName, outFileName);

        if (operation.equalsIgnoreCase("compress")) {
            wrapper.compress();
            System.out.println("Compression complete.");
        } else if (operation.equalsIgnoreCase("decompress")) {
            wrapper.decompress();
            System.out.println("Decompression complete.");
        } else {
            System.out.println("Invalid operation. Use 'compress' or 'decompress'.");
        }
    }
}


