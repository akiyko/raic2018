/**
 * By no one on 13.01.2019.
 */
public class SinCos {
    public final double sin;
    public final double cos;

    private SinCos(double sin, double cos) {
        this.sin = sin;
        this.cos = cos;
    }

    public static SinCos of(double sin, double cos) {
        return new SinCos(sin, cos);
    }

    public static SinCos ofCos(double cos) {
        return of(Math.sqrt(1 - cos * cos), cos);
    }

    public SinCos negateSin() {
        return of(-sin, cos);
    }

    public SinCos negateCos() {
        return of(sin, -cos);
    }

    public SinCos negateSinCos() {
        return of(-sin, -cos);
    }

    @Override
    public String toString() {
        return "SinCos{" +
                "sin=" + sin +
                ", cos=" + cos +
                '}';
    }
}
