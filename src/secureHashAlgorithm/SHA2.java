package secureHashAlgorithm;

import java.nio.charset.StandardCharsets;

public class SHA2 {
    /// This is an implementation of the SHA-2 (256 bits) standard by Idrees Munshi

    //***********************  Fields  ***********************//
    // Input
    public String input;

    // Hash Values (h)
    // These are hardcoded into the algorithm and derived from the square roots of the first 8 primes.



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



    public byte[] preProc(String input) {
        // This is to make it easy for the computer (and for me to code).
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);

        // We can't add more bytes to an array in Java??? WTF :/ Guess I have to make a new array for every
        // Fucking padding step? So not ok.
        byte[] pad0 = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, pad0, 0, bytes.length);
        pad0[pad0.length-1] = -128;

        // After adding the 10000000 byte (-128), now pad until the data is a multiple of 512, less 64
        // This means data.length = 448 mod 512
        // First, find length currently: This is just the length * 8, as bytes always come in 8s.
        int len = pad0.length*8;
        int x = len % 512;
        // 448 - x = number of bits to add (call it y)
        // y/8 is the number of bytes to append (call it z)
        int z = (448 - x)/8;
        // Append z bytes to pad0 to get pad1
        byte[] pad1 = new byte[pad0.length + z];
        System.arraycopy(pad0, 0, pad1, 0, pad0.length);
        for (int i = 0; i < z; i++) {
            pad1[pad0.length + i] = 0;
        }
        System.out.println(z);

        // Now I need to pad the last 64 bits with the fucking... "BIG-ENDIAN" integer representing the length of the
        // original string in binary. That's something like stringLength*8? or bytes.length*8, for me.
        byte[] pad2 = new byte[pad1.length + 8];
        System.arraycopy(pad1, 0, pad2, 0, pad1.length);
        long BEint = (long) (bytes.length*8);
        for (int i = 0; i < 8; i++) {
            pad2[pad1.length + i] = (byte) (BEint >>> (8*(7 - i))); // Line *NOT* written by me.
        }
        // And it is done. This is the pre-processing complete.
        // Following is for debug, so I can see the damn binary. *Mostly* not written by me.
        int count = 0;
        for (byte b : pad2) {
            String binary = String.format("%8s", Integer.toBinaryString(b & 0xFF))
                    .replace(' ', '0');
            System.out.print(binary + " ");
            count++;
            if (count % 8 == 0) {
                System.out.println();
            }
        }

        return pad2;
    }
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
//                // Then perform a bitwise shift operation (to the left(?) in this case) of 1.
//                // This tests all 8 bits, I guess?
//                val <<= 1;
//            }
//            binary.append(' ');
//        }
//        System.out.println(binary.toString());
//        return binary.toString();
//    }