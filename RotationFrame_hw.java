import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RotationFrame_hw extends JFrame {
	private static final long serialVersionUID = 1L;
	JPanel cotrolPanelMain = new JPanel();
	JPanel cotrolPanelShow = new JPanel();;
	JPanel cotrolPanelBackColor = new JPanel();;
	JPanel cotrolPanelRotate = new JPanel();
	ImagePanel imagePanel;
	JButton btnShow;
	JButton btnRotate;
	JTextField tfRed = new JTextField(5);
	JTextField tfGreen = new JTextField(5);
	JTextField tfBlue = new JTextField(5);
	BufferedImage img;
	BufferedImage newImg;
	JTextField tfTheta = new JTextField(5);
	JLabel lbRed = new JLabel("背景 (R)");
	JLabel lbGreen = new JLabel("背景 (G)");
	JLabel lbBlue = new JLabel("背景 (B)");
	JLabel lbTheta = new JLabel("旋轉角度 (1~89度)");
	final int[][][] data;
	int height;
	int width;

	RotationFrame_hw() {
		setBounds(0, 0, 1500, 1500);
		getContentPane().setLayout(null);
		tfRed.setText("0");
		tfGreen.setText("0");
		tfBlue.setText("0");
		tfTheta.setText("0");
		setTitle("Rotation Homework");
		try {
			img = ImageIO.read(new File("plate.png"));
		} catch (IOException e) {
			System.out.println("IO exception");
		}
		height = img.getHeight();
		width = img.getWidth();
		data = new int[height][width][3];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = img.getRGB(x, y);
				data[y][x][0] = Util.getR(rgb);
				data[y][x][1] = Util.getG(rgb);
				data[y][x][2] = Util.getB(rgb);
			}
		}
		btnShow = new JButton("顯示");
		btnRotate = new JButton("旋轉");
		cotrolPanelMain = new JPanel();
		cotrolPanelMain.setLayout(new GridLayout(1, 3));
		cotrolPanelShow.add(btnShow);
		cotrolPanelMain.add(cotrolPanelShow);
		cotrolPanelBackColor.setLayout(new GridLayout(3, 2));
		cotrolPanelBackColor.add(lbRed);
		cotrolPanelBackColor.add(tfRed);
		cotrolPanelBackColor.add(lbGreen);
		cotrolPanelBackColor.add(tfGreen);
		cotrolPanelBackColor.add(lbBlue);
		cotrolPanelBackColor.add(tfBlue);
		cotrolPanelMain.add(cotrolPanelBackColor);
		cotrolPanelRotate.add(lbTheta);
		cotrolPanelRotate.add(tfTheta);
		cotrolPanelRotate.add(btnRotate);
		cotrolPanelMain.add(cotrolPanelRotate);
		cotrolPanelMain.setBounds(0, 0, 360, 80);
		getContentPane().add(cotrolPanelMain);
		imagePanel = new ImagePanel();
		imagePanel.setBounds(0, 100, 1500, 1500);
		getContentPane().add(imagePanel);
		btnShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Graphics g = imagePanel.getGraphics();
				imagePanel.paintComponent(g);
				g.drawImage(img, 0, 0, null);
			}
		});

		btnRotate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int deg = Integer.parseInt(tfTheta.getText());
				if (deg > 89 || deg < 1)
					return;
				double theta = 1.0 * deg / 180 * Math.PI;
				double heightCos = height * Math.cos(theta);
				double heightSin = height * Math.sin(theta);
				double widthCos = width * Math.cos(theta);
				double widthSin = width * Math.sin(theta);
				int newWidth = (int) (widthCos + heightSin);
				int newHeight = (int) (heightCos + widthSin);
				newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
				double[][] matrix = { { Math.cos(theta), 1.0 * Math.sin(theta), 0.0 },
						{ -1.0 * Math.sin(theta), Math.cos(theta), 0.0 }, { 0.0, 0.0, 1 } };
				fillColor(newImg);
				for (int y = 0; y < newHeight; y++) {
					for (int x = 0; x < newWidth; x++) {
						double[] Postion1 = { 1.0 * x - newWidth / 2.0, 1.0 * newHeight - y - newHeight / 2.0, 1.0 };
						double[] Position2 = Util.affine(matrix, Postion1);
//						int x2 = (int) Position2[0] + newWidth / 2;
//						int y2 = newHeight - ((int) Position2[1] + newHeight / 2);
//						int x1 = Util.checkImageBounds(x2, newWidth);
//						int y1 = Util.checkImageBounds(y2, newHeight);
//						int rgb = Util.makeColor(data[y][x][0], data[y][x][1], data[y][x][2]);
//						newImg.setRGB(x1, y1, rgb);
						double new_x = Position2[0] + width / 2.0;
						double new_y = height - (Position2[1] + height / 2.0);
						if (new_x < width && new_x >= 0 && new_y < height && new_y >= 0) {
							int rgb = exeBilinear(new_x, new_y);
							newImg.setRGB(x, y, rgb);
						}
					}
				}
				Graphics g = imagePanel.getGraphics();
				imagePanel.paintComponent(g);
				g.drawImage(newImg, 0, 0, null);
			}
		});
	}

	void fillColor(BufferedImage img) {
		int r = Integer.parseInt(tfRed.getText());
		int g = Integer.parseInt(tfGreen.getText());
		int b = Integer.parseInt(tfBlue.getText());
		int rgb = Util.makeColor(r, g, b);
		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				img.setRGB(j, i, rgb);
			}
		}
	}

	int exeBilinear(double x, double y) {
		int left = Util.checkImageBounds((int) Math.floor(x), width);
		int right = Util.checkImageBounds((int) Math.ceil(x), width);
		int top = Util.checkImageBounds((int) Math.floor(y), height);
		int bottom = Util.checkImageBounds((int) Math.ceil(y), height);
		double alpha = 0.0;
		double beta = 0.0;
		if (left < right)
			alpha = x - left;
		if (top < bottom)
			beta = y - top;
		int[] rgbColor = new int[3];
		for (int i = 0; i < 3; i++)
			rgbColor[i] = Util.bilinear(data[top][left][i], data[top][right][i], data[bottom][left][i],
					data[bottom][right][i], alpha, beta);
		return Util.makeColor(rgbColor[0], rgbColor[1], rgbColor[2]);
	}

	public static void main(String[] args) {
		RotationFrame_hw frame = new RotationFrame_hw();
		frame.setSize(1500, 1500);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
