import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Util {

	final static int checkPixelBounds(int value) {
		if (value > 255)
			return 255;
		if (value < 0)
			return 0;
		return value;
	}

	// get red channel from colorspace (4 bytes)
	final static int getR(int rgb) {
		return checkPixelBounds((rgb & 0x00ff0000) >>> 16);
	}

	// get green channel from colorspace (4 bytes)
	final static int getG(int rgb) {
		return checkPixelBounds((rgb & 0x0000ff00) >>> 8);
	}

	// get blue channel from colorspace (4 bytes)
	final static int getB(int rgb) {
		return checkPixelBounds(rgb & 0x000000ff);
	}

	final static int makeColor(int r, int g, int b) {
		return (255 << 24 | r << 16 | g << 8 | b);
	}

	final static int covertToGray(int r, int g, int b) {
		return checkPixelBounds((int) (0.2126 * r + 0.7152 * g + 0.0722 * b));
	}

	final static double[] affine(double[][] a, double[] b) {
		int aRow = a.length;
		int bRow = b.length;
		double[] result = new double[aRow];

		for (int i = 0; i < aRow; i++) {
			for (int j = 0; j < bRow; j++) {
				result[i] += a[i][j] * b[j];
			}
		}
		return result;
	}

	final static int bilinear(int leftTop, int rightTop, int leftBottom, int rightBottom, double alpha, double beta) {
		double left = linear(leftTop, leftBottom, alpha);
		double right = linear(rightTop, rightBottom, alpha);
		double value = linear(left, right, beta);
		return checkPixelBounds((int) value);
	}

	final static double linear(double v1, double v2, double weight) {
		return v1 + (v2 - v1) * weight; // 10 + (20-10) * 0.2
	}

	final static int checkImageBounds(int value, int length) {
		if (value > length - 1)
			return length - 1;
		else if (value < 0)
			return 0;
		else
			return value;
	}

	final static int[][] DELTA(int[][] a, int[][] b) {
		int DELTA[][] = new int[a.length][a[0].length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				DELTA[i][j] = a[i][j] - b[i][j];
			}

		}
		return DELTA;
	}

	static HSL RGB2HSL(double r, double g, double b) {
		double h = 0;
		double s;
		double l;
		r = r / 255.0;
		g = g / 255.0;
		b = b / 255.0;
		double min = Math.min(r, Math.min(g, b));
		double max = Math.max(r, Math.max(g, b));
		l = 0.5 * (min + max);
		s = getS(min, max, l);

		if (s == 0) {
			h = 0.0;
		} else if (max == r) {
			h = ((g - b) / (max - min)) % 6;
		} else if (max == g) {
			h = 2.0 + (b - r) / (max - min);
		} else if (max == b) {
			h = 4.0 + (r - g) / (max - min);
		}
		h *= 60;
		if (h < 0.0) {
			h += 360;
		}
		if (h > 360) {
			h -= 360;
		}
		return new HSL(h, s, l);
	}

	static double getS(double min, double max, double l) {
		double ans = 0.0;
		if (min == max) {
			return ans;
		}
		if (l < 0.5) {
			ans = (max - min) / (max + min);
		}
		if (l > 0.5) {
			ans = (max - min) / (max + min);
		}
		return ans;
	}

	static int[] paint(double hue, double sat, double lum) {
		int [] a = new int[3] ;
		if (hue < 0) {
			hue += 360;
		}
		if (hue > 360) {
			hue -= 360;
		}
		if (sat < 0.0) {
			sat = 0.0;
		}
		if (sat > 1.0) {
			sat = 1.0;
		}
		if (lum < 0.0) {
			lum = 0.0;
		}
		if (lum > 1.0) {
			lum = 1.0;
		}
		double r =0, g=0 , b=0 ;
		double c = (1.0 - Math.abs(2 * lum - 1.0)) * sat;
		double x = c * (1 - Math.abs(((hue / 60) % 2) - 1));
		double m = lum - (c /2.0);

		if (hue > 0 && hue < 60) {
			r = c;
			g = x;
			b = 0;
		} else if (hue >= 60 && hue < 120) {
			r = x;
			g = c;
			b = 0;
		} else if (hue >= 120 && hue < 180) {
			r = 0;
			g = c;
			b = x;
		} else if (hue >= 180 && hue < 240) {
			r = 0;
			g = x;
			b = c;
		} else if (hue >= 240 && hue < 300) {
			r = c;
			g = 0;
			b = x;
		}
		r += m;
		g += m;
		b += m;

		a[0] = checkPixelBounds((int) (255 * r));
		a[1] = checkPixelBounds((int) (255 * g));
		a[2] = checkPixelBounds((int) (255 * b));
		return a;
	}
}