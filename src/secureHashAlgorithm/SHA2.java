package secureHashAlgorithm;

import java.nio.charset.StandardCharsets;

public class SHA2 {
    /// This is an implementation of the SHA-2 (256 bits) standard by Idrees Munshi

    //***********************  Fields  ***********************//
    // Input
    public String input;

    // Hash Values (h)
    // These are hardcoded into the algorithm and derived from the square roots of the first 8 primes.
    public static final int[] h = {
            0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
            0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
    };

    public int[] H = new int[8];

    // Round Constants (k)
    // These are derived from the cube roots of the first 64 primes.
    public static final int[] k = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    int a = h[0];
    int b = h[1];
    int c = h[2];
    int d = h[3];
    int e = h[4];
    int f = h[5];
    int g = h[6];
    int hn = h[7];

    // Message Schedule, once computed:
    public int[] W;

    // First, set up the constructor. This will take a message and then the this.hash function
    // will return the (hopefully) hashed input (as a fixed length 32 byte signature.)

    public SHA2(String input) {
        this.input = input;
        System.out.println(input);
    }

    public String hash() {
        String input = this.input;
        byte[] processedInput = this.preProc(input);

        System.out.println();

        // Part 2 Message Schedule
        this.W = this.messageSchedule(processedInput);

        // Part 3 Compression
        this.compressionLoop();

        // Finally, the digest is ready, all that needs to be done is the concatenation of the H array.
        StringBuilder hash = new StringBuilder();
        for (int hp : H) {
            hash.append(String.format("%08x", hp));
        }

        String finalHash = hash.toString();
        System.out.println(finalHash);
        return finalHash;
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
        int z = (448 - x + 512) % 512 / 8;
        // Append z bytes to pad0 to get pad1
        byte[] pad1 = new byte[pad0.length + z];
        System.arraycopy(pad0, 0, pad1, 0, pad0.length);
        for (int i = 0; i < z; i++) {
            pad1[pad0.length + i] = 0;
        }

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
//        int count = 0;
//        for (byte b : pad2) {
//            String binary = String.format("%8s", Integer.toBinaryString(b & 0xFF))
//                    .replace(' ', '0');
//            System.out.print(binary + " ");
//            count++;
//            if (count % 8 == 0) {
//                System.out.println();
//            }
//        }
        return pad2;
    }

    public int[] messageSchedule(byte[] procIn) {
        // Split into chunks of 32 bit words
        int[] w =  new int[64];
        for (int i = 0; i < 16; i++) {
            int j = i * 4;
            // Each word is 32 bits. So, taking sets of 4 and combining them with the bitwise OR operator: |
            w[i] =  ((procIn[j] & 0xFF) << 24)     | // Moved to top of the word with bit shift 24
                    ((procIn[j + 1] & 0xFF) << 16) | // j + 1 is the next byte, & 0xFF converts signed to 0-255.
                    ((procIn[j + 2] & 0xFF) << 8)  | // Bitwise OR acts to concatenate the bytes, 'cause it's comparing
                    ((procIn[j + 3] & 0xFF));        // with the trailing zeros from the bit shift and 32 bit int datatype
        }

        // Modify from w[16...63] with sigma 0 and 1 operators
        for (int i = 16; i < 64; i++) {
            int sigma0 = rightRotate(w[i - 15], 7) ^ rightRotate(w[i - 15], 18) ^ w[i - 15] >>> 3;
            int sigma1 = rightRotate(w[i - 2], 17) ^ rightRotate(w[i - 2], 19) ^ w[i - 2] >>> 10;
            w[i] = w[i - 16] + sigma0 + w[i - 7] + sigma1;
        }

//        int count = 0;
//        for (int b : w) {
//            String binary = String.format("%32s", Long.toBinaryString(b & 0xFFFFFFFFL))
//                    .replace(' ', '0');
//            System.out.print(binary + " ");
//            count++;
//            if (count % 2 == 0) {
//                System.out.println();
//            }
//        }
        return w;
    }

    static int rightRotate(int n, int d) {

        // Rotation of 32 is same as rotation of 0
        d = d % 32;

        // Moving bits right and wrapping around
        return (n >>> d) | (n << (32 - d));
    }

    public void compressionLoop() {
        for (int i = 0; i < 64; i++) {
            int Sigma1 = rightRotate(e, 6) ^ rightRotate(e, 11) ^ rightRotate(e, 25);
            int choiceFunc = (e & f) ^ ((~e) & g);
            int temp1 = hn + Sigma1 + choiceFunc + k[i] + W[i];
            int Sigma0 = rightRotate(a, 2) ^ rightRotate(a, 13) ^ rightRotate(a, 22);
            int majorFunc = (a & b) ^ (a & c) ^ (b & c);
            int temp2 = Sigma0 + majorFunc;
            hn = g;
            g = f;
            f = e;
            e = d + temp1;
            d = c;
            c = b;
            b = a;
            a = temp2 + temp1;


        }

        H[0] = h[0] + a;
        H[1] = h[1] + b;
        H[2] = h[2] + c;
        H[3] = h[3] + d;
        H[4] = h[4] + e;
        H[5] = h[5] + f;
        H[6] = h[6] + g;
        H[7] = h[7] + hn;

//        System.out.println(Integer.toBinaryString(a));
//        System.out.println(Integer.toBinaryString(b));
//        System.out.println(Integer.toBinaryString(c));
//        System.out.println(Integer.toBinaryString(d));
//        System.out.println(Integer.toBinaryString(e));
//        System.out.println(Integer.toBinaryString(f));
//        System.out.println(Integer.toBinaryString(g));
//        System.out.println(Integer.toBinaryString(hn));
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