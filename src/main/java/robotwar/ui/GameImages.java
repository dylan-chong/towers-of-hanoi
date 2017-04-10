package robotwar.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * Created by Dylan on 11/04/17.
 */
public enum GameImages {
	DEAD_BOT("DeadRobot.png"),
	RANDOM_BOT("Robot5.png"),
	GUARD_BOT("Robot4.png");

	/**
	 * The image path simply determines where images are stored relative to this
	 * class.
	 */
	private static final String IMAGE_PATH = "images/";
	private final Image image;

	GameImages(String imageName) {
		image = loadImage(imageName);
	}

	private static Image loadImage(String filename) {
		try {
			return loadImageWithoutSlash(filename);
		} catch (IOException | IllegalArgumentException e) {
			return loadImageWithSlash(filename);
		}
	}

	private static Image loadImageWithoutSlash(String filename) throws IOException {
		// using the URL means the image loads when stored
		// in a jar or expanded into individual files.
		java.net.URL imageURL = BattleCanvas.class.getResource(IMAGE_PATH
				+ filename);
		if (imageURL == null) throw new IllegalArgumentException();

		Image img = ImageIO.read(imageURL);
		return img;
	}

	private static Image loadImageWithSlash(String filename) {
		// using the URL means the image loads when stored
		// in a jar or expanded into individual files.
		java.net.URL imageURL = BattleCanvas.class.getResource("/" + IMAGE_PATH
				+ filename);

		try {
			Image img = ImageIO.read(imageURL);
			return img;
		} catch (IOException e) {
			// we've encountered an error loading the image. There's not much we
			// can actually do at this point, except to abort the game.
			throw new RuntimeException("Unable to load image: " + filename);
		}
	}

	public Image getImage() {
		return image;
	}
}
