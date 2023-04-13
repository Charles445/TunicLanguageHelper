package tuniclanghelp.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class InteractibleBox
{
	public static Color DARK_GRAY = new Color(77,77,77);
	public static Color GRAY = new Color(140,140,140);
	
	public int x;
	public int y;
	public int width;
	public int height;
	
	private int centerX;
	private int centerY;
	
	private int cornerX;
	private int cornerY;
	
	public int fontsize = 12;
	
	public String text = "";
	
	public boolean selected = false;
	public boolean hidden = false;
	
	public InteractibleBox(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.centerX = x + (width/2);
		this.centerY = y + (height/2);
		
		this.cornerX = x + width;
		this.cornerY = y + height;
	}
	
	public void render(Graphics2D g2d, float scale, int xoffset, int yoffset)
	{
		if(this.hidden)
			return;
		
		g2d.setColor(this.getColor());
		
		BufferedImage image = null;
		
		switch(this.text)
		{
			case "Clear":
				image = Assets.instance.buttonClear;
				break;
			case "Save":
				image = Assets.instance.buttonSave;
				break;
			case "Rename":
				image = Assets.instance.buttonRename;
				break;
			case "Delete":
				image = Assets.instance.buttonDelete;
				break;
			case "Copy":
				image = Assets.instance.buttonCopy;
				break;
			case "Left":
				image = Assets.instance.buttonArrowLeft;
				break;
			case "Right":
				image = Assets.instance.buttonArrowRight;
				break;
			default:
				break;
		}
		
		if(image != null)
			g2d.drawImage(image, scale(scale, this.x), scale(scale, this.y), scale(scale, cornerX), scale(scale, cornerY), 0, 0, image.getWidth(), image.getHeight(), null);
		
		
		g2d.drawRect(scale(scale, x + xoffset), scale(scale, y + yoffset), scale(scale, width), scale(scale, height));
		
	}
	
	public boolean getMouseInbounds(int mouseX, int mouseY, float scale)
	{
		double mx = ((double) mouseX / (double) scale);
		double my = ((double) mouseY / (double) scale);
		
		if(mx > cornerX)
			return false;
		
		if(mx < x)
			return false;
		
		if(my > cornerY)
			return false;
		
		if(my < y)
			return false;
		
		return true;
	}
	
	public int scale(float scale, int val)
	{
		return (int)((float) val * scale);
	}
	
	public Color getColor()
	{
		if(selected)
			return GRAY;
		
		return DARK_GRAY;
	}
}
