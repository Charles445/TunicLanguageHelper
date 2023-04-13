package tuniclanghelp.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import tuniclanghelp.TunicLanguageHelper;

public class Assets
{
	public static Assets instance;

	public BufferedImage buttonClear;
	public BufferedImage buttonSave;
	public BufferedImage buttonRename;
	public BufferedImage buttonDelete;
	public BufferedImage buttonCopy;
	public BufferedImage buttonArrowLeft;
	public BufferedImage buttonArrowRight;
	
	public Assets() throws IOException
	{
		buttonClear = ImageIO.read(TunicLanguageHelper.class.getResource("/assets/buttonClear.png"));
		buttonSave = ImageIO.read(TunicLanguageHelper.class.getResource("/assets/buttonSave.png"));
		buttonRename = ImageIO.read(TunicLanguageHelper.class.getResource("/assets/buttonRename.png"));
		buttonDelete = ImageIO.read(TunicLanguageHelper.class.getResource("/assets/buttonDelete.png"));
		buttonCopy = ImageIO.read(TunicLanguageHelper.class.getResource("/assets/buttonCopy.png"));
		buttonArrowLeft = ImageIO.read(TunicLanguageHelper.class.getResource("/assets/buttonArrowLeft.png"));
		buttonArrowRight = ImageIO.read(TunicLanguageHelper.class.getResource("/assets/buttonArrowRight.png"));
	}
	
}
