package tuniclanghelp;


import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JFrame;

import tuniclanghelp.gui.Assets;
import tuniclanghelp.gui.Canvas;

public class TunicLanguageHelper
{
	public static TunicLanguageHelper instance;
	public JFrame frame;
	public Canvas canvas;
	
	public static int DEFAULT_WIDTH = 1150;
	public static int DEFAULT_HEIGHT = 925;

	public static Path workingDir;
	public static Path saveFile;

	public static void main(String[] args)
	{
		//Load assets from jar
		try
		{
			Assets.instance = new Assets();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		
		instance = new TunicLanguageHelper();
	}
	
	public TunicLanguageHelper()
	{
		//Load the data first
		
		
		
		
		//Set up the frame
		
		frame = new JFrame("TunicLanguageHelper");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setLocationRelativeTo(null);
		frame.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		frame.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		canvas.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		canvas.addMouseListener(canvas);
		canvas.addMouseMotionListener(canvas);
		
		frame.add(canvas);
		frame.setVisible(true);
		
		//Now that things are packed in, update the canvas default scale
		canvas.updateDefaultDimensions();
		
	}

}
