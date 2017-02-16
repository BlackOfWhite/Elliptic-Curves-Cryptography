package cryptography;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author niewinskip
 */
public class Main2 {

    private static final int M = 23;
    private static final int A = 10;
    private static final int B = 10;
    private static final String MESSAGE = "niewinski piotr";
    private static EllipticModel ellipticModel;

    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ellipticModel = new EllipticModel(M, A, B);
//        int[] params = ElipticModel.getRandomParameters();
//        System.out.println("A: " + params[0] + " B: " + params[1]);
        System.out.println("Elliptic Model: " + ellipticModel.toString());

        List<Point> EGPoints = ellipticModel.generateEllipticGroup();
        Point g = ellipticModel.getGPoint(EGPoints);
        int c = ellipticModel.generateOrder(g);
        int nA = ellipticModel.createPrivateKey(c);
        int nB = ellipticModel.createPrivateKey(c);
        Point pA = ellipticModel.multiplyPoints(nA, g);
        Point pB = ellipticModel.multiplyPoints(nB, g);
        Point cskA = ellipticModel.multiplyPoints(nA, pB);
        Point cskB = ellipticModel.multiplyPoints(nB, pA);

        String ecGroupOutString = "EC Group: ";
        for (Point point : EGPoints) {
            ecGroupOutString += "(" + point.getX() + "," + point.getY() + ");";
        }

        System.out.println(ecGroupOutString);
        System.out.println("Generation Point: (" + g.x + "," + g.y + ")");
        System.out.println("Order C: " + c);
        System.out.println("\n\n+++ Key Exchange +++");
        System.out.println("User A: private key nA = " + nA + ", public key pA = (" + pA.x + "," + pA.y + ")");
        System.out.println("User B: nB = " + nB + " pB = (" + pB.x + "," + pB.y + ")");
        System.out.println("User A CSK: (" + cskA.x + "," + cskA.y + ") || User B CSK: (" + cskB.x + "," + cskB.y + ")");
        List<Point> ellipticGroupECDSA = ellipticModel.generateEllipticGroup();

        Point gECDSA = ellipticModel.getGPoint(ellipticGroupECDSA);
        int cECDSA = ellipticModel.generateOrder(gECDSA);
        int nAECDSA = ellipticModel.createPrivateKey(cECDSA);
        Point pAECDSA = ellipticModel.multiplyPoints(nAECDSA, gECDSA);

        Point sign = ellipticModel.sign(cECDSA, nAECDSA, gECDSA, MESSAGE);
        boolean verification = ellipticModel.verify(sign, MESSAGE, pAECDSA, cECDSA, gECDSA);

//        ecGroupOutString = "EC Group: ";
//        for (int i = 0; i < ellipticGroupECDSA.size(); i++) {
//            ecGroupOutString += "(" + ellipticGroupECDSA.get(i).x + "," + ellipticGroupECDSA.get(i).y + ");";
//        }

        System.out.println("\n\n+++ Digital signature ECDSA +++");
        System.out.println("User A: nA = " + nAECDSA + " pA = (" + pAECDSA.getX() + "," + pAECDSA.getY() + ")");
        System.out.println("Signature: = (" + sign.getX() + "," + sign.getY() + ")");
        if (verification) {
            System.out.println("Verification successful.");
        } else {
            System.out.println("Verification failed.");
        }

    }

}
