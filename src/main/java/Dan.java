/**
 * By no one on 19.12.2018.
 */
public class Dan {
    public static long DAN_COUNT = 0;

    public final double distance;
    public final Vector3d normal;

    public Dan(double distance, Vector3d normal) {
        DAN_COUNT ++;
//        if(distance < 0) {
//            System.out.println("I just want to be ensure that this is not a case");
//        }
        this.distance = distance;
        this.normal = normal;
    }

    public static Dan of(double distance, Vector3d normal) {
        return new Dan(distance, normal);
    }

    public static Dan min(Dan a, Dan b) {
        return (a.distance < b.distance) ? a : b;
    }
}
