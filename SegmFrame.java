import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SegmFrame extends JFrame {
	String filename = "segm_2.png";
	String title = "8 Neighbor";
	JPanel cotrolPanel;
	JPanel imagePanelLeft;
	JPanel imagePanelRight;
	JButton btnShow;
	JButton btnSegm;
	JButton btnNext;
	JButton btnPrev;

	int[][][] data;
	int[][][] newdata;
	int[][] label;
	int height;
	int width;
	int count;
	int number = 1;
	static BufferedImage img = null;
	static BufferedImage newimg = null;

	SegmFrame() {
		setTitle(title);
		setLayout(null);
		btnShow = new JButton("Show Original Image");
		btnSegm = new JButton("Segment");
		btnNext = new JButton("Next Object");
		btnPrev = new JButton("Prev Object");
		cotrolPanel = new JPanel();
		cotrolPanel.setBounds(0, 0, 1500, 200);
		getContentPane().add(cotrolPanel);
		cotrolPanel.add(btnShow);
		cotrolPanel.add(btnSegm);
		cotrolPanel.add(btnNext);
		cotrolPanel.add(btnPrev);
		imagePanelLeft = new ImagePanel();
		imagePanelLeft.setBounds(0, 120, 700, 700);
		getContentPane().add(imagePanelLeft);
		imagePanelRight = new ImagePanel();
		imagePanelRight.setBounds(750, 120, 700, 700);
		getContentPane().add(imagePanelRight);

		btnShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadImg();
				Graphics g = imagePanelLeft.getGraphics();
				g.drawImage(img, 0, 0, null);
			}
		});

		btnSegm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				ImgObj();

			}
		});

		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				next(number);
				number += 2;
			}
		});

		btnPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				next(number);
				number -= 2;
			}
		});
	}// end of constructor

	void loadImg() {
		try {
			img = ImageIO.read(new File(filename));
			newimg = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println("IO exception");
		}
		height = img.getHeight();
		width = img.getWidth();
		data = new int[height][width][3];
		newdata = new int[height][width][3];
		label = new int[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = img.getRGB(x, y);
				data[y][x][0] = Util.getR(rgb);
				data[y][x][1] = Util.getG(rgb);
				data[y][x][2] = Util.getB(rgb);
				label[y][x] = 0;
			}
		}
	}

	void ImgObj() {
		count = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (data[y][x][0] != 255 && label[y][x] == 0) {

					findobj(x, y);
					count++;
				}
			}
		}
	}

	void next(int number) {
		if (number < 0) {
			number = 1;
		} else if (number > 25) {
			number = 25;
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (label[y][x] == number || label[y][x] == number + 1) {
					newdata[y][x][0] = 0;
					newdata[y][x][1] = 0;
					newdata[y][x][2] = 0;
				} else {
					newdata[y][x][0] = 255;
					newdata[y][x][1] = 255;
					newdata[y][x][2] = 255;
				}
			}
		}

		System.out.println(number);
		Draw(newdata);
	}

	public void findobj(int x, int y) {
		if (data[y][x][0] == 255 || label[y][x] == count) {
			return;
		}

		label[y][x] = count;
		for (int Height = -1; Height < 2; Height += 2) {
			for (int Width = -1; Width < 2; Width += 2) {
				int H = Util.checkImageBounds(y + Height, height);
				int W = Util.checkImageBounds(x + Width, width);
				findobj(W, H);
			}
		}

	}

	void Draw(int Data[][][]) {
		for (int i = 0; i < Data.length; i++) {
			for (int j = 0; j < Data[0].length; j++) {
				int r = newdata[i][j][0];
				int g = newdata[i][j][1];
				int b = newdata[i][j][2];
				newimg.setRGB(j, i, Util.makeColor(r, g, b));
			}
		}
		Graphics g = imagePanelRight.getGraphics();
		g.drawImage(newimg, 0, 0, null);
	}

	public static void main(String[] args) {
		SegmFrame frame = new SegmFrame();
		frame.setSize(1500, 1500);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
