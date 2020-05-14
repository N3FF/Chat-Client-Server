package network.client;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Was used to take in a sprite sheet of emoticons to use, but I didn't end up having the time to
 * make everything work as well as I was hoping to.
 * 
 * @author Justin
 *
 */
public class Pics {

  public static final int USER_IMG_WIDTH = 178;
  public static final int USER_IMG_HEIGHT = 179;
  public static final int USER_IMG_COLUMNS = 3;
  public static final int USER_IMG_ROWS = 3;

  public static BufferedImage[] userIcon = new BufferedImage[USER_IMG_ROWS * USER_IMG_COLUMNS];

  Pics() throws IOException {

    BufferedImage userIcons = ImageIO.read(new File("images/squaresmileys.png"));
    for (int row = 0; row < USER_IMG_ROWS; row++) {
      for (int col = 0; col < USER_IMG_COLUMNS; col++) {
        userIcon[row * USER_IMG_COLUMNS + col] = userIcons.getSubimage(col * USER_IMG_WIDTH,
            row * USER_IMG_HEIGHT, USER_IMG_WIDTH, USER_IMG_HEIGHT);
      }
    }
  }

  public BufferedImage[] getUserIcons() {
    return userIcon;
  }

  public BufferedImage resizeImage(BufferedImage img, int width, int height) {
    Image temp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = newImg.createGraphics();
    g.drawImage(temp, 0, 0, null);
    g.dispose();

    return newImg;
  }
}
