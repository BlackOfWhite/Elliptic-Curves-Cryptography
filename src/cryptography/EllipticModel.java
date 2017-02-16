package cryptography;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author niewinskip
 */
public class EllipticModel {

    private int m, a, b;

    public EllipticModel(int m, int a, int b) {
        this.m = m;
        this.a = a;
        this.b = b;
    }

//    public int[] getRandomParameters() {
//        ArrayList<int[]> allFoundedParams = new ArrayList<int[]>();
//        for (int a = 0; a < m; a++) {
//            for (int b = 0; b < m; b++) {
//                int tmp = (4 * (a * a * a) + 27 * (b * b)) % m;
//                if (tmp != 0) {
//                    int[] paramsToAdd = new int[2];
//                    paramsToAdd[0] = a;
//                    paramsToAdd[1] = b;
//                    allFoundedParams.add(paramsToAdd);
//                }
//            }
//        }
//        int number = (new Random()).nextInt(allFoundedParams.size());
//        int[] params = new int[2];
//        params[0] = allFoundedParams.get(number)[0];
//        params[1] = allFoundedParams.get(number)[1];
//        return params;
//    }

    public List<Point> generateEllipticGroup() {
        List<Point> pointsList = new ArrayList<>();
        for (int x = 0; x < m; x++) {
            for (int y = 0; y < m; y++) {
                if (((y * y) % m) == ((x * x * x) + a * x + b) % m) {
                    pointsList.add(new Point(x, y));
                }
            }
        }
        return pointsList;
    }

    public Point addPoints(Point p, Point q) {
        int temp1 = (p.y - q.y) % m;
        int temp2 = p.x - q.x;

        BigInteger modInv = new BigInteger(temp2 + "");
        modInv = modInv.modInverse((new BigInteger(m + "")));
        temp2 = modInv.intValue();
        int s = (temp1 * temp2) % m;
        int x = ((s * s) - p.x - q.x) % m;
        int y = (s * (p.x - x) - p.y) % m;

        if (x < 0) {
            x += m;
        }
        if (y < 0) {
            y += m;
        }
        return new Point(x, y);
    }

    public Point doublePoints(Point p) {
        int temp1 = (3 * (p.x * p.x) + a) % m;
        int temp2 = 2 * p.y;
        BigInteger modInv = new BigInteger(temp2 + "");
        modInv = modInv.modInverse((new BigInteger(m + "")));
        temp2 = modInv.intValue();
        int s = (temp1 * temp2) % m;
        int x = (s * s - 2 * p.x) % m;
        int y = (s * (p.x - x) - p.y) % m;
        if (x < 0) {
            x += m;
        }
        if (y < 0) {
            y += m;
        }
        return new Point(x, y);
    }

    public int generateOrder(Point p) {
        int c = 1;
        Point temp = p;
        boolean isNeutral = false;
        while (!isNeutral) {
            try {
                if (temp.equals(p)) {
                    temp = doublePoints(temp);
                } else {
                    temp = addPoints(temp, p);
                }
            } catch (ArithmeticException e) {
                isNeutral = true;
            }
            c++;
        }
        return c;
    }

    boolean isPrime(int n) {
        for (int i = 2; 2 * i < n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    public Point getGPoint(List<Point> ellipticGroup) {
        int temp;
        int c = -1;
        Point p = new Point(-1, -1);
        for (Point x : ellipticGroup) {
            temp = generateOrder(x);
            if (temp > c && isPrime(c)) {
                c = temp;
                p = x;
            }
        }
        return p;
    }

    public int createPrivateKey(int c) {
        int result = -1;
        Random random = new Random();
        do {
            result = random.nextInt(c);
        } while (result == 0);
        return result;
    }

    public Point multiplyPoints(int n, Point g) {
        Point pub = g;
        if (n == 1) {
            return g;
        }
        if (n == 2) {
            return doublePoints(g);
        }
        if (n == 3) {
            return addPoints(g, doublePoints(g));
        }
        int modulo = n % 2;
        if (modulo == 1) {
            pub = addPoints(g, doublePoints(g));
            for (int i = 4; i <= n; i++) {
                pub = addPoints(pub, g);
            }
        }
        if (modulo == 0) {
            pub = doublePoints(g);
            for (int i = 4; i <= n + 1; i++) {
                pub = addPoints(pub, g);
            }
        }
        return pub;
    }

    public Point sign(int c, int nA, Point g, String message) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        Random rand = new Random();
        int k = 0;
        Point kG = new Point(-1, -1);
        int r = 0;
        BigInteger s = BigInteger.ZERO;

        while (k <= 1 && r == 0 && s == BigInteger.ZERO) {
            k = rand.nextInt(c - 1);
            if (k <= 1) {
                continue;
            }
            kG = multiplyPoints(k, g);
            r = kG.x % c;
            if (r == 0) {
                continue;
            }
            BigInteger k2 = (new BigInteger(k + "")).modInverse(new BigInteger(c + ""));
            String hash = getHash(message);
            System.out.println("Hash: " + hash);
            BigInteger hashBI = new BigInteger(hash.getBytes());
            s = (k2.multiply(hashBI.add(new BigInteger(nA * r + "")))).mod(new BigInteger(c + ""));
            if (s.intValue() == 0) {
                continue;
            }
        }
        return new Point(r, s.intValue());
    }

    public boolean verify(Point sign, String message, Point pA, int c, Point g) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String hash = getHash(message);
        System.out.println("Verification hash: " + hash);
        BigInteger hashBI = new BigInteger(hash.getBytes());

        if ((sign.x < 1 && sign.x > c - 1) || (sign.y < 1 && sign.y > c - 1)) {
            return false;
        }

        BigInteger w = new BigInteger(sign.y + "").modInverse(new BigInteger(c + ""));
        BigInteger u1 = hashBI.multiply(w).mod(new BigInteger(c + ""));
        BigInteger u2 = new BigInteger(sign.x + "").multiply(w).mod(new BigInteger(c + ""));

        Point tmp = addPoints(multiplyPoints(u1.intValue(), g), multiplyPoints(u2.intValue(), pA));
        int r2 = tmp.x % c;
        if (r2 == sign.x) {
            return true;
        }
        return false;
    }

    public String getHash(String message) {
        SHA1 sha1 = new SHA1();
        sha1.load(message.getBytes());
        String hash = sha1.endHash();
        return hash;
    }

    @Override
    public String toString() {
        return "M" + m + "(" + a + "," + b + ")";
    }

}