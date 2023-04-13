package tuniclanghelp.gui;

import java.awt.Color;
import java.awt.Graphics2D;

public class InteractibleLine
{
	public static Color LIGHT_GRAY = new Color(235, 235, 235);
	public static Color GRAY_FILLED = new Color(100, 100, 100);
	public static Color GRAY_UNFILLED = new Color(170, 170, 170);
	
	public int x1;
	public int y1;
	public int x2;
	public int y2;
	public boolean selected = false;
	public boolean filled = false;
	public boolean hidden = false;
	public boolean isCircle = false;
	public boolean isRounded = true; //On by default
	public boolean isDebug = false;
	
	private int centerX;
	private int centerY;
	
	public final int circledimension = 30;
	public final int halfcircledimension = circledimension / 2;
	
	public InteractibleLine(int x1, int y1, int x2, int y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		
		this.centerX = (x2 + x1)/2;
		this.centerY = (y2 + y1)/2;
	}
	
	public void setCircle(boolean isCircle)
	{
		this.isCircle = isCircle;
	}
	
	public Color getColor()
	{
		if(selected)
		{
			if(filled)
				return GRAY_FILLED;
			
			return GRAY_UNFILLED;
		}
		
		if(filled)
			return Color.BLACK;
					
		return LIGHT_GRAY;
	}
	
	public void render(Graphics2D g2d, float scale, int xoffset, int yoffset)
	{
		if(this.hidden)
			return;
		
		g2d.setColor(this.getColor());
		
		//NOTE that circle draws on top bound
		
		if(!isCircle)
			g2d.drawLine(scale(scale, this.x1 + xoffset), scale(scale, this.y1 + yoffset), scale(scale, this.x2 + xoffset), scale(scale, this.y2 + yoffset));
		else
			g2d.drawOval(scale(scale, this.x1 - this.halfcircledimension + xoffset), scale(scale, this.y1 + yoffset), scale(scale, this.circledimension), scale(scale, this.circledimension));
	}
	
	public int scale(float scale, int val)
	{
		return (int)((float) val * scale);
	}
	
	public double getDistanceToMouse(int mouseX, int mouseY, float scale)
	{
		
		if(this.isCircle)
			return getCircleDistanceToMouse(mouseX, mouseY, scale);
		
		double mx = ((double) mouseX / (double) scale);
		double my = ((double) mouseY / (double) scale);
		
		double mxmcx = mx - (double) centerX;
		double mymcy = my - (double) centerY;
		
		return (mxmcx * mxmcx) + (mymcy * mymcy);
		
	}
	
	private double getCircleDistanceToMouse(int mouseX, int mouseY, float scale)
	{
		double x0 = (double) mouseX / (double) scale;
		double y0 = (double) mouseY / (double) scale;
		
		//x, y + halfdimension
		
		double x3mx0 = (double) x1 - x0;
		double y3my0 = (double) y1 + (double) halfcircledimension - y0;
		
		//Bump it a bit for the circle?
		return Math.max(0, ((x3mx0 * x3mx0) + (y3my0 * y3my0) - 300));
	}
	
}
