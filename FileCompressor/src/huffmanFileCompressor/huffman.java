package huffmanFileCompressor;

import java.io.*;
import java.util.*;

// Node class for the Huffman Tree
class Node {
    char data;
    int freq;
    Node left, right;
    String code = "";

    Node() {
        this.data = 0;
        this.freq = 0;
        this.left = this.right = null;
    }

    Node(char data, int freq) {
        this.data = data;
        this.freq = freq;
        this.left = this.right = null;
    }
}

// Custom Comparator for PriorityQueue
class Compare implements Comparator<Node> {
    public int compare(Node a, Node b) {
        return a.freq - b.freq;
    }
}

// Huffman class
public class huffman {
    private ArrayList<Node> arr = new ArrayList<>();
    private PriorityQueue<Node> minHeap = new PriorityQueue<>(new Compare());
    private Node root;
    private String inFileName, outFileName;

    public huffman(String inFileName, String outFileName) {
        this.inFileName = inFileName;
        this.outFileName = outFileName;
    }

    // Method to create an array of 128 ASCII Nodes
    public void createArr() {
        for (int i = 0; i < 128; i++) {
            Node node = new Node((char) i, 0);
            arr.add(node);
        }
    }

    // Traverse Huffman Tree and assign codes
    public void traverse(Node r, String str) {
        if (r.left == null && r.right == null) {
            r.code = str;
            return;
        }
        traverse(r.left, str + '0');
        traverse(r.right, str + '1');
    }

    // Convert binary string to decimal
    public int binToDec(String inStr) {
        int res = 0;
        for (char c : inStr.toCharArray()) {
            res = res * 2 + (c - '0');
        }
        return res;
    }

    // Convert decimal to 8-bit binary string
    public String decToBin(int inNum) {
        StringBuilder temp = new StringBuilder();
        StringBuilder res = new StringBuilder();

        while (inNum > 0) {
            temp.append(inNum % 2);
            inNum /= 2;
        }

        while (temp.length() < 8) {
            temp.append('0');
        }

        res.append(temp.reverse());
        return res.toString();
    }

    // Build Huffman Tree using given code
    public void buildTree(char aCode, String path) {
        Node curr = root;
        for (char ch : path.toCharArray()) {
            if (ch == '0') {
                if (curr.left == null) curr.left = new Node();
                curr = curr.left;
            } else {
                if (curr.right == null) curr.right = new Node();
                curr = curr.right;
            }
        }
        curr.data = aCode;
    }

    // Create Min Heap from frequency of characters
    public void createMinHeap() throws IOException {
        FileInputStream inFile = new FileInputStream(inFileName);
        int id;

        while ((id = inFile.read()) != -1) {
            arr.get(id).freq++;
        }
        inFile.close();

        for (Node node : arr) {
            if (node.freq > 0) minHeap.add(node);
        }
    }

    // Create Huffman Tree
    public void createTree() {
        while (minHeap.size() > 1) {
            Node left = minHeap.poll();
            Node right = minHeap.poll();
            Node newNode = new Node();
            newNode.freq = left.freq + right.freq;
            newNode.left = left;
            newNode.right = right;
            root = newNode;
            minHeap.add(newNode);
        }
    }

    // Assign codes to characters
    public void createCodes() {
        traverse(root, "");
    }

    // Save the encoded file
    public void saveEncodedFile() throws IOException {
        FileInputStream inFile = new FileInputStream(inFileName);
        FileOutputStream outFile = new FileOutputStream(outFileName);
        StringBuilder in = new StringBuilder();
        StringBuilder s = new StringBuilder();
        int id;

        in.append((char) minHeap.size());
        PriorityQueue<Node> tempPQ = new PriorityQueue<>(minHeap);
        
        while (!tempPQ.isEmpty()) {
            Node curr = tempPQ.poll();
            in.append(curr.data);
            s.append("0".repeat(Math.max(0, 127 - curr.code.length()))).append("1").append(curr.code);
            for (int i = 0; i < 16; i++) {
                in.append((char) binToDec(s.substring(0, 8)));
                s.delete(0, 8);
            }
        }
        
        s.setLength(0);
        while ((id = inFile.read()) != -1) {
            s.append(arr.get(id).code);
            while (s.length() > 8) {
                in.append((char) binToDec(s.substring(0, 8)));
                s.delete(0, 8);
            }
        }
        
        if (s.length() < 8) s.append("0".repeat(8 - s.length()));
        in.append((char) binToDec(s.toString()));
        outFile.write(in.toString().getBytes());
        inFile.close();
        outFile.close();
    }

    // Decode the file
    public void saveDecodedFile() throws IOException {
        FileInputStream inFile = new FileInputStream(inFileName);
        FileOutputStream outFile = new FileOutputStream(outFileName);
        int size = inFile.read();
        root = new Node();

        for (int i = 0; i < size; i++) {
            char aCode = (char) inFile.read();
            byte[] hCodeC = new byte[16];
            inFile.read(hCodeC);
            StringBuilder hCodeStr = new StringBuilder();
            for (byte b : hCodeC) hCodeStr.append(decToBin(b & 0xFF));
            hCodeStr = new StringBuilder(hCodeStr.substring(hCodeStr.indexOf("1") + 1));
            buildTree(aCode, hCodeStr.toString());
        }
        
        Node curr = root;
        int textSeg;
        while ((textSeg = inFile.read()) != -1) {
            String path = decToBin(textSeg);
            for (char ch : path.toCharArray()) {
                curr = (ch == '0') ? curr.left : curr.right;
                if (curr.left == null && curr.right == null) {
                    outFile.write(curr.data);
                    curr = root;
                }
            }
        }

        inFile.close();
        outFile.close();
    }
}
