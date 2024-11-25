package huffmanFileCompressor;


public class HuffmanCompressMain {
    public static void main(String[] args) {
        // Check if the correct number of arguments is provided
        if (args.length != 2) {
            System.out.println("Failed to detect files. Please provide input and output file names.");
            System.exit(1);
        }

        // Initialize HuffmanWrapper with input and output file names
        HuffmanHeader huffmanWrapper = new HuffmanHeader(args[0], args[1]);

        // Call the compress method
        huffmanWrapper.compress();
        System.out.println("Compressed successfully");
    }
}

