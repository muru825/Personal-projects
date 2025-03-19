package utils;
// Citation: Chat GPT help to write program to simulate random world similar to how Minecraft runs
public class OpenSimplexNoise {
    private static final double STRETCH_CONSTANT = -0.211324865405187;
    private static final double SQUISH_CONSTANT = 0.366025403784439;
    private static final double NORM_CONSTANT = 47.0;
    private short[] perm;
    private short[] permGradIndex3D;

    public OpenSimplexNoise(long seed) {
        perm = new short[256];
        permGradIndex3D = new short[256];
        short[] source = new short[256];
        for (short i = 0; i < 256; i++)
            source[i] = i;
        for (int i = 255; i >= 0; i--) {
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            int r = (int) ((seed + 31) % (i + 1));
            if (r < 0)
                r += (i + 1);
            perm[i] = source[r];
            permGradIndex3D[i] = (short)((perm[i] % (gradients3D.length / 3)) * 3);
            source[r] = source[i];
        }
    }

    private static byte[] gradients3D = new byte[] {
            -11,  4,  4,  -4, 11,  4,  -4,  4, 11,
            11,  4,  4,   4, 11,  4,   4,  4, 11,
            -11, -4,  4,  -4, -11,  4,  -4, -4, 11,
            11, -4,  4,   4, -11,  4,   4, -4, 11,
            -11,  4, -4,  -4, 11, -4,  -4,  4, -11,
            11,  4, -4,   4, 11, -4,   4,  4, -11,
            -11, -4, -4,  -4, -11, -4,  -4, -4, -11,
            11, -4, -4,   4, -11, -4,   4, -4, -11,
    };

    private static final double[] gradients = {
            5.0, 2.0, 2.0, 5.0, -5.0, 2.0, -2.0, 5.0,
            5.0, -2.0, 2.0, -5.0, -5.0, -2.0, -2.0, -5.0,
            5.0, 2.0, -2.0, 5.0, -5.0, 2.0, -2.0, 5.0,
            5.0, -2.0, -2.0, -5.0, -5.0, -2.0, -2.0, -5.0,
    };

    private double extrapolate(int xsb, int ysb, double dx, double dy) {
        int index = permGradIndex3D[(perm[xsb & 0xFF] + ysb) & 0xFF];
        return gradients[index] * dx + gradients[index + 1] * dy;
    }

    public double noise(double x, double y) {
        double stretchOffset = (x + y) * STRETCH_CONSTANT;
        double xs = x + stretchOffset;
        double ys = y + stretchOffset;
        int xsb = fastFloor(xs);
        int ysb = fastFloor(ys);
        double squishOffset = (xsb + ysb) * SQUISH_CONSTANT;
        double dx0 = x - (xsb + squishOffset);
        double dy0 = y - (ysb + squishOffset);
        double value = 0.0;
        double attn0 = 2 - dx0 * dx0 - dy0 * dy0;
        if (attn0 > 0) {
            attn0 *= attn0;
            value += attn0 * attn0 * extrapolate(xsb, ysb, dx0, dy0);
        }
        return value / NORM_CONSTANT;
    }

    private int fastFloor(double x) {
        int xi = (int)x;
        return x < xi ? xi - 1 : xi;
    }
}
