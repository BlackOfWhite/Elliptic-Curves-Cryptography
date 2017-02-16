package cryptography;

import java.nio.ByteBuffer;

/**
 *
 * @author niewinskip
 */
public class SHA1 {

    private final int H[];
    private long byteCounter;
    private final byte[] byteBlock;

    public SHA1() {
        H = new int[5];
        H[0] = 0x67452301;
        H[1] = 0xEFCDAB89;
        H[2] = 0x98BADCFE;
        H[3] = 0x10325476;
        H[4] = 0xC3D2E1F0;
        byteBlock = new byte[64];
        byteCounter = 0;
    }

    //add length of message
    public void load(byte input[]) {
        for (int i = 0; i < input.length; i++) {
            int j = (int) ((byteCounter / 8) % 64);
            byteCounter += 8;
            byteBlock[j] = input[i];
            if (j == 63) {
                doAlgorithm(byteBlock);
            }
        }
    }

    void doAlgorithm(byte[] block) {
        int x[] = new int[80];
        int a, b, c, d, e, k, temp;
        a = H[0];
        b = H[1];
        c = H[2];
        d = H[3];
        e = H[4];
        for (int off = 0; off < 16; off++) {
            x[off] = byteToInt(new byte[]{block[off * 4], block[off * 4 + 1], block[off * 4 + 2], block[off * 4 + 3]});
        }
        for (int i = 16; i < 80; i++) {
            x[i] = rotateLeft(x[i - 3] ^ x[i - 8] ^ x[i - 14] ^ x[i - 16], 1);
        }
        for (int i = 0; i < 80; i++) {
            if (0 <= i && i <= 19) {
                k = 0x5A827999;
                temp = rotateLeft(a, 5) + F1(b, c, d) + e + k + x[i];
                e = d;
                d = c;
                c = rotateLeft(b, 30);
                b = a;
                a = temp;
            } else if (20 <= i && i <= 39) {
                k = 0x6ED9EBA1;
                temp = rotateLeft(a, 5) + F2(b, c, d) + e + k + x[i];
                e = d;
                d = c;
                c = rotateLeft(b, 30);
                b = a;
                a = temp;
            } else if (40 <= i && i <= 59) {
                k = 0x8F1BBCDC;
                temp = rotateLeft(a, 5) + F3(b, c, d) + e + k + x[i];
                e = d;
                d = c;
                c = rotateLeft(b, 30);
                b = a;
                a = temp;
            } else if (60 <= i && i <= 79) {
                k = 0xCA62C1D6;
                temp = rotateLeft(a, 5) + F2(b, c, d) + e + k + x[i];
                e = d;
                d = c;
                c = rotateLeft(b, 30);
                b = a;
                a = temp;
            }
        }
        H[0] += a;
        H[1] += b;
        H[2] += c;
        H[3] += d;
        H[4] += e;
    }

    //rotate function Sn(X) = (X << n)and(X >> 32 − n)
    private int rotateLeft(int a, int n) {
        return ((a << n) | (a >>> (32 - n)));
    }

    private int F1(int b, int c, int d) {//(B AND C) OR ((NOT B) AND D)
        return ((b & c) | ((~b) & d));
    }

    private int F2(int b, int c, int d) {//B XOR C XOR D
        return b ^ c ^ d;
    }

    private int F3(int b, int c, int d) {//(B AND C) OR (B AND D) OR (C AND D)
        return (b & c) | (b & d) | (c & d);
    }

    public static int byteToInt(byte[] b) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }

    public String endHash() {
        byte textLength[];
        byte impletion[];
        int lastBlockLength, impletionLength;
        textLength = ByteBuffer.allocate(8).putLong(byteCounter).array();
        lastBlockLength = (int) ((byteCounter / 8) % 64);
        if (lastBlockLength < 56) {
            impletionLength = 56 - lastBlockLength;
        } else {
            impletionLength = 120 - lastBlockLength;
        }
        impletion = new byte[impletionLength];
        impletion[0] = (byte) 0x80;
        for (int y = 1; y < impletionLength; y++) {
            impletion[y] = 0x00;
        }
        load(impletion);
        load(textLength);
        String result = Integer.toHexString(H[0])
                + Integer.toHexString(H[1])
                + Integer.toHexString(H[2])
                + Integer.toHexString(H[3])
                + Integer.toHexString(H[4]);
        return result;
    }
}
