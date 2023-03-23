public class Util {

	final static double[] affine(double[][] a, double[] b) {
		int aRow = a.length;
		int bRow = b.length;
		double[] ret = new double[aRow];
		for (int i = 0; i < aRow; i++) {
			for (int j = 0; j < bRow; j++) {
				ret[i] += a[i][j] * b[j];
			}
		}
		return ret;
	}

	final static int bilinear(int leftTop, int rightTop, int leftBottom, int rightBottom, double alpha, double beta) {
		double left = linear(leftTop, rightTop, alpha);
		double right = linear(leftBottom, rightBottom, alpha);
		double value = linear(left, right, beta);
		return checkPixelBounds((int) value);
	}

	final static double linear(double v1, double v2, double weight) {

		return v1 + (v2 - v1) * weight;
	}

	final static int checkPixelBounds(int value) {
		if (value > 255) {
			value = 255;
		} else if (value < 0) {
			value = 0;
		}
		return value;
	}

	// get red channel from color space (4 bytes)
	final static int getR(int rgb) {
		return checkPixelBounds((rgb & 0x00ff0000) >>> 16);
	}

	// get green channel from color space (4 bytes)
	final static int getG(int rgb) {
		return checkPixelBounds((rgb & 0x0000ff00) >>> 8);
	}

	// get blue channel from colors pace (4 bytes)
	final static int getB(int rgb) {
		return checkPixelBounds(rgb & 0x000000ff);
	}

	// make ARGB color format from R, G, and B channels
	final static int makeColor(int r, int g, int b) {
		return (255 << 24 | r << 16 | g << 8 | b);

	}

	final static int checkBound(double number, int range) {
		if (number + 1 >= range) {
			number = range - 2;
		}
		return (int) number;

	}

}