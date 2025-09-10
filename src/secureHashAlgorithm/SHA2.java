package secureHashAlgorithm;

import java.nio.charset.StandardCharsets;

public class SHA2 {
    /// This is an implementation of the SHA-2 (256 bits) standard by Idrees Munshi

    // Fields
    public String input;

    // First, set up the constructor. This will take a message and then the this.hash function
    // will return the (hopefully) hashed input (as a fixed length 32 byte signature.)
    public SHA2(String input) {
        this.input = input;
        System.out.println(input);
    }

    public String hash() {
        String input = this.input;
        byte[] processedInput = this.preProc(input);


        String hash = "";
        return hash;
    }

    // Old work...
//    public String preProcess(String input){
//        // This is best for humans.
//        // Begin by converting to binary:
//        // Using Java's StandardCharset library, the byte[] data type, and getBytes() to
//        // "get the underlying bytes"
//        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
//        // Convert the underlying bytes to their binary representations
//        StringBuilder binary = new StringBuilder();
//        for (byte b : bytes) {
//            // For each byte, convert it to an 8 bit string of 0s and 1s
//            int val = b;
//            for (int i = 0; i < 8; i++) {
//                // Perform binary AND operator... why exactly... I'm not certain.
//                // Something about checking if the highest bit is set, if so, set it to one, otherwise 0.
//                binary.append((val & 128) == 0 ? 0 : 1);
//                // Then perform a bitwise shift operation (to the left in this case) of 1.
//                // This tests all 8 bits, I guess?
//                val <<= 1;
//            }
//            binary.append(' ');
//        }
//        System.out.println(binary.toString());
//        return binary.toString();
//    }

    public byte[] preProc(String input) {
        // This is to make it easy for the computer (and for me to code).
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);

        // We can't add more bytes to an array in Java??? WTF :/ Guess I have to make a new array for every
        // Fucking padding step? So not ok.
        byte[] pad0 = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, pad0, 0, bytes.length);
        pad0[pad0.length-1] = -128;


        // For debug, so I can see the damn binary.
        for (byte b : pad0) {
            System.out.printf("%8s", Integer.toBinaryString(b & 0xFF));
        }


        return bytes;
    }
}
