import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by niewinskip on 2016-12-13.
 */
public class Main {

    /**
    1.	For given M = 23 and a =     design the Elliptic Group.
2.	Select the random value of b < M for which two positive integer numbers given a and randomly chosen b, are satisfying to the inequality 4a3 + 27b2 != 0 (mod M),
            3.	For your value of a and b, implement the software application for Elliptic Group elements (points) generation (see section 1).
            4.	Implement the Arithmetic (Adding distinct points P = (xP,yP) and Q = (xQ,yQ) and Doubling the point P=(xP,yP)) in an Elliptic Curve group (see section 2).
            5.	Determine the generation point G with the order c represented by big prime number (see section 3).
*/

    private final static BigInteger M = new BigInteger("7"); // Must be prime number
    private final static BigInteger a = new BigInteger("1");
    private static BigInteger b = new BigInteger("0");

    private static List<Point> points;


    public static void main(String[] args) {
        System.out.println("(a,b,M) = {" + a + "," + b + "," + M + "}");
        if (a.compareTo(M) >= 0 || b.compareTo(M) >= 0) {
            System.out.println("a or b is greater than M!");
            return;
        }

        points = getAllGroupPoints();
        System.out.println("\nThe elliptic group E" + M.toString() + "(" + a.toString() + "," + b.toString() + ") has " + points.size() +  " following points:\n" + points);

        Point p = new Point(new BigInteger("5"), new BigInteger("1"));
        Point tmp = p;

        // Check if group contains point.
        boolean has = false;
        for (Point P : points) {
            if (P.getX().compareTo(p.getX()) == 0 && P.getY().compareTo(p.getY()) == 0) {
                has = true;
                break;
            }
        }

        if (!has) {
            System.out.println("Point is not within group!");
            return;
        }

        BigInteger x = BigInteger.ONE;
        while (tmp.getX().compareTo(Point.INFINITY.getX()) != 0 && tmp.getY().compareTo(Point.INFINITY.getY()) != 0) {
            System.out.println(x + ". " + tmp);
            if (arePointsEqual(p, tmp)) {
                tmp = pointDouble(p);
            } else {
                tmp = pointAddition(p,tmp);
            }
            x = x.add(BigInteger.ONE);
        }
        System.out.println(x + ". INF");
    }

    private static boolean satisfiesInequality(BigInteger b) {
        BigInteger bia = new BigInteger(String.valueOf(a));
        BigInteger bib = new BigInteger(String.valueOf(b));
        bia = bia.pow(3).multiply(new BigInteger("4"));
        bib = bib.pow(2).multiply(new BigInteger("27"));
        bia = bia.add(bib).mod(new BigInteger(String.valueOf(M)));
        if (bia.signum() == 0) {
            return false;
        }
        return true;
    }

    private static BigInteger randomBigInteger(BigInteger max) {
        Random rnd = new Random();
        do {
            BigInteger b = new BigInteger(max.bitLength(), rnd);
            if (b.compareTo(max) <= 0) {
                if (satisfiesInequality(b)) {
                    return b;
                }
            }
        } while (true);
    }

    private static List<Point> getAllGroupPoints() {
        List<Point> list = new ArrayList<>();
        BigInteger x = M.subtract(BigInteger.ONE);
        BigInteger first, second;
        while (x.compareTo(BigInteger.ZERO) >= 0) {
            // calc y value(s)
            first = x.pow(3);
            second = a.multiply(x);
            BigInteger output = first.add(second).add(b).mod(M);

            if (output.compareTo(BigInteger.ZERO) == 0) {
                list.add(new Point(x, BigInteger.ZERO));
            } else {
                BigInteger y = M.subtract(BigInteger.ONE);
                int count = 0;
                // while > 0
                while (y.compareTo(BigInteger.ZERO) > 0 && count < 2) {
                    if (y.modPow(new BigInteger("2"), M).compareTo(output) == 0) {
                        list.add(new Point(x, y));
                        count++;
                    }
                    y = y.subtract(BigInteger.ONE);
                }
            }
            x = x.subtract(BigInteger.ONE);
        }
        return list;
    }

    public static BigInteger sqrt(BigInteger x) {
        if (BigInteger.ZERO.compareTo(x) == 0) {
            return BigInteger.ZERO;
        }
        BigInteger div = BigInteger.ZERO.setBit(x.bitLength()/2);
        BigInteger div2 = div;
        // Loop until we hit the same value twice in a row, or wind
        // up alternating.
        for(;;) {
            BigInteger y = div.add(x.divide(div)).shiftRight(1);
            if (y.equals(div) || y.equals(div2)) {
                if (y.pow(2).compareTo(x) == 0) {
                    return y;
                } else {
                    return null;
                }
            }
            div2 = div;
            div = y;
        }
    }

    /**
     * P + Q
     * When points are distinct, so P != Q && P != -Q
     * @param P
     * @param Q
     * @return
     */
    private static Point pointAddition(Point P, Point Q) {
        if (P.getX().compareTo(Q.getX()) == 0 && P.getY().compareTo(Q.getY().negate().mod(M)) == 0) {
            return Point.INFINITY;
        }

        BigInteger xP = P.getX();
        BigInteger yP = P.getY();
        BigInteger xQ = Q.getX();
        BigInteger yQ = Q.getY();

        // s = (yP − yQ)/(xP − xQ) mod M;
        BigInteger sTop = yP.subtract(yQ);
        BigInteger sBottom = xP.subtract(xQ);
        BigInteger s = divideModulo(sTop, sBottom, M);

        // v = yP - sxP mod M
        BigInteger v = yP.subtract(s.multiply(xP)).mod(M);
        // xR  = s2 − xP − xQ mod M;
        BigInteger xR = s.pow(2).subtract(xP).subtract(xQ).mod(M);
        // yR = -(sxR + v) mod M
        BigInteger yR = s.multiply(xR).add(v).negate().mod(M);
        return new Point(xR, yR);
    }

    /**
     * 2P
     * When points are equal, so P == Q || P == -Q
     * @param P
     * @return
     */
    private static Point pointDouble(Point P) {
        if (P.getY().compareTo(BigInteger.ZERO) == 0) {
            return Point.INFINITY;
        }
        // s = (3xP2 + a)/(2yP) mod M;
        BigInteger sTop = new BigInteger("3").multiply(P.getX().pow(2)).add(a);
        BigInteger sBottom = new BigInteger("2").multiply(P.getY());
        BigInteger s = divideModulo(sTop, sBottom, M);
        // xR =s2 − 2xP  mod M;
        BigInteger xR = s.pow(2).subtract(P.getX().multiply(new BigInteger("2"))).mod(M);
        // yR = − yP + s(xP – xR) mod M;
        BigInteger yR = s.multiply(P.getX().subtract(xR)).subtract(P.getY()).mod(M);
        return new Point(xR, yR);
    }

    private static boolean arePointsEqual(Point P, Point Q) {
        if (P.getX().compareTo(Q.getX()) == 0 && P.getY().compareTo(Q.getY()) == 0) {
            return true;
        }
        return false;
    }

    private static BigInteger divideModulo(BigInteger a, BigInteger b, BigInteger M) {
        BigInteger x = BigInteger.ONE;
        while (x.compareTo(M) <= 0) {
            if (b.multiply(x).mod(M).compareTo(BigInteger.ONE) == 0) {
                return a.multiply(x).mod(M);
            }
            x = x.add(BigInteger.ONE);
        }
        return  x;
    }

    private static BigDecimal fractionMod (BigDecimal b, BigInteger m) {
        if (b.compareTo(BigDecimal.ZERO) >= 0) {
            return b.remainder(new BigDecimal(m));
        } else {
            BigDecimal fr  =b.remainder(new BigDecimal(m)).add(new BigDecimal(m)).remainder(new BigDecimal(m));
            return fr;
        }
    }

    private static int divideModulo(Integer aa, Integer bb, Integer M) {
        return ((aa % M) * new BigInteger(bb.toString())
                .modInverse(new BigInteger(M.toString()))
                .intValue()) % M;
    }

}
