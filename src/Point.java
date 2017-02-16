import java.math.BigInteger;

/**
 * Created by niewinskip on 2016-12-13.
 */
public class Point {

    public BigInteger getX() {
        return x;
    }

    public BigInteger getY() {
        return y;
    }

    private BigInteger x;
    private BigInteger y;


    public Point(BigInteger x, BigInteger y) {
        this.x = x;
        this.y = y;
    };

    public static final Point INFINITY = new Point(new BigInteger("-1232342348239415"), new BigInteger("-12391239321323123"));

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }


}
