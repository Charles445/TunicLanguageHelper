package tuniclanghelp.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import tuniclanghelp.data.Database;
import tuniclanghelp.data.Symbol;

public class SyllableDisplay
{
	public Font font;
	public float oldScale = 0.0f;
	
	public String resultText = "";
	
	public Symbol[] symbolList = new Symbol[8];
	
	public SyllableDisplay()
	{
		for(int i=0;i<symbolList.length;i++)
		{
			symbolList[i] = new Symbol();
		}
	}
	
	public void recalculate(String code, Canvas canvas, Database database)
	{
		Symbol sym = new Symbol(code);
		boolean[] symData = sym.data;

		int shift = 0;
		
		StringBuilder sb = new StringBuilder();
		
		String workingCode = "";
		
		for(int i=0; i < symbolList.length; i++)
		{
			Symbol cached = symbolList[i];
			
			cached.clear();
			boolean[] cacheData = cached.data;
			shift = i * 14;
			
			//Set the cached symbol to just the individual syllable
			for(int j = 0; j < 14; j++)
			{
				cacheData[j] = symData[j + shift];
			}
			
			
			//Check the syllable
			
			if(cached.isEmpty())
			{
				sb.append(" ");
			}
			else
			{
				workingCode = Symbol.toCode(cached);
				
				String word = database.getWordWithCode(workingCode);
				
				if(word != null)
				{
					sb.append(word);
				}
				else
				{
					sb.append("_");
				}
			}
			
			//Add a space between syllables
			sb.append(" ");
		}
		
		this.resultText = sb.toString();
	}
	
	public void render(Graphics2D g2d, float scale, int xOffset, int yOffset)
	{
		if(this.font == null)
			this.font = g2d.getFont().deriveFont(20.0f);
		
		g2d.setColor(Color.BLACK);
		g2d.setFont(font);
		g2d.drawString(this.resultText, scale(scale, 25 + xOffset), scale(scale, 25 + yOffset));
	}
	
	public int scale(float scale, int val)
	{
		return (int)((float) val * scale);
	}
}
