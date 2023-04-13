package tuniclanghelp.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import tuniclanghelp.TunicLanguageHelper;
import tuniclanghelp.data.Database;
import tuniclanghelp.data.Symbol;

public class Canvas extends JComponent implements MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = -5725317435776694904L;
	
	public InteractibleLine[] regularLines = new InteractibleLine[10];
	public InteractibleLine[] selectableLines = new InteractibleLine[128];
	
	public InteractibleLine[] symbolLines = new InteractibleLine[128];
	
	public static float lineThickness = 12.0f;
	public static float boxThickness = 4.0f;
	
	boolean isDirty = false;
	boolean needsLogic = false;
	
	
	//Some constants
	//TODO really proper scaling, aka how things are 150 wide or so
	final int BOTTOM_LINE_SCOOT = 115;

	final int DISPLAY_SECTION_SCOOT_X = 0;
	final int DISPLAY_SECTION_SCOOT_Y = 250;
	
	final int LINE_ENTRY_ZONE_X = 50;
	final int LINE_ENTRY_ZONE_Y = 40;
	
	public int default_width;
	public int default_height;
	
	public InteractibleLine displayHorizontalBar;
	public InteractibleLine symbolHorizontalBar;
	
	public InteractibleBox clearButton;
	public InteractibleBox saveButton;
	public InteractibleBox renameButton;
	public InteractibleBox deleteButton;
	public InteractibleBox copyButton;
	public InteractibleBox leftButton;
	public InteractibleBox rightButton;
	
	public String resultText = "";
	
	public Font font;
	public float oldScale = 0.0f;
	
	public Database database;
	
	//FEATURES
	
	//TODO add a way to turn on/off the syllable display, as it's a fairly big language spoiler
	public boolean shouldCalculateSyllableDisplay = true;
	public boolean shouldDrawSyllableDisplay = true;
	public SyllableDisplay syllableDisplay;
	
	public Canvas()
	{
		super();
		
		this.database = new Database();
		
		try
		{
			this.database.populateWordsFromFile();
		}
		catch (Exception e)
		{
			System.out.println("Database failed to write, exiting to try and avoid data loss");
			e.printStackTrace();
		}
		
		createLineEntryZone(LINE_ENTRY_ZONE_X, LINE_ENTRY_ZONE_Y);
		
		createSymbolDisplayFromLineEntry(0, DISPLAY_SECTION_SCOOT_Y * 2);
		
		createButtons();
		//selectableLines[1] = new InteractibleLine(50, 380, 950, 580);
		//selectableLines[1].setCircle(true);
		
		this.default_width = TunicLanguageHelper.DEFAULT_WIDTH;
		this.default_height = TunicLanguageHelper.DEFAULT_HEIGHT;
		
		
		//FEATURES
		this.syllableDisplay = new SyllableDisplay();
	}
	
	public void buttonClear()
	{
		InteractibleLine line = null;
		for(int i=0;i<selectableLines.length;i++)
		{
			line = selectableLines[i];
			if(line != null)
			{
				line.filled = false;
			}
		}
		
		this.markDirty();
		this.recalculateLogic();
	}
	
	public void buttonSave()
	{
		String result = JOptionPane.showInputDialog(this, "Input the word for this symbol");
		if(result == null || result.isEmpty())
			return;
		
		String code = gatherInputCode();
		
		if(database.hasCode(code))
		{
			int confirm = JOptionPane.showConfirmDialog(this, "Okay to overwrite '"+database.getWordWithCode(code)+"' with '"+result+"'?");
			if(confirm != JOptionPane.YES_OPTION)
				return;
		}
		
		database.saveWord(code, result);
		
		this.recalculateLogic();
		
		this.flushDatabase();
	}
	
	public void buttonRename()
	{
		String code = gatherResultCode();
		if(!database.hasCode(code))
			return;
		
		String result = JOptionPane.showInputDialog(this, "Input new word for this symbol");
		if(result == null || result.isEmpty())
			return;
		
		int confirm = JOptionPane.showConfirmDialog(this, "Okay to overwrite '"+database.getWordWithCode(code)+"' with '"+result+"'?");
		if(confirm != JOptionPane.YES_OPTION)
			return;
		
		database.saveWord(code, result);
		
		this.recalculateLogic();
		this.flushDatabase();
	}
	
	public void buttonDelete()
	{
		String code = gatherResultCode();
		
		if(!database.hasCode(code))
			return;
	
		int confirm = JOptionPane.showConfirmDialog(this, "Okay to delete '"+database.getWordWithCode(code)+"'?");
		if(confirm != JOptionPane.YES_OPTION)
			return;
		
		database.deleteCode(code);
		
		this.recalculateLogic();
		
		this.flushDatabase();
	}
	
	public void buttonCopy()
	{
		InteractibleLine line = null;
		for(int i=0;i<selectableLines.length;i++)
		{
			line = selectableLines[i];
			if(line != null && this.symbolLines[i] != null)
			{
				line.filled = symbolLines[i].filled;
			}
		}
		
		this.markDirty();
		this.recalculateLogic();
	}
	
	public void buttonLeft()
	{
		//Shift active left
		
		//Check for leftmost column
		for(int i=0;i<14;i++)
		{
			if(this.selectableLines[i].filled)
				return;
		}
		
		InteractibleLine a = null;
		InteractibleLine b = null;
		
		for(int i = 0;i < this.selectableLines.length - 14; i++)
		{
			a = this.selectableLines[i];
			b = this.selectableLines[i+14];
			
			if(a == null || b == null)
				break;

			//112 and 113 are outer line
			//So i at 98 and 99 is handled separately
			if(i==98)
			{
				//104
				a = this.selectableLines[104];
			}
			else if(i==99)
			{
				//110
				a = this.selectableLines[110];
			}
			
			
			a.filled = b.filled;
			b.filled = false;
		}
		
		this.markDirty();
		this.recalculateLogic();
	}
	
	public void buttonRight()
	{
		//Shift active right
		
		boolean fill_104 = false;
		boolean fill_110 = false;
		
		//Check for rightmost column
		//Must add two here...
		for(int i=7*14; i<8*14 + 2;i++)
		{
			if(this.selectableLines[i].filled)
			{
				if(i != 104 && i != 110)
				{
					return;
				}
				else
				{
					if(i == 104)
						fill_104 = this.selectableLines[i].filled;
					else
						fill_110 = this.selectableLines[i].filled;
				}
			}
		};
		
		InteractibleLine a = null;
		InteractibleLine b = null;
		
		//Should put in some sanity checks in here, or not
		for(int i = this.selectableLines.length - 1; i >= 14; i--)
		{
			a = this.selectableLines[i];
			b = this.selectableLines[i-14];
			
			if(a == null || b == null)
				continue;
			
			a.filled = b.filled;
			b.filled = false;
		}
		
		//Fix for last two
		this.selectableLines[112].filled = fill_104;
		this.selectableLines[113].filled = fill_110;
		
		
		this.markDirty();
		this.recalculateLogic();
	}
	
	public void flushDatabase()
	{
		try
		{
			database.writeDatabase();
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(this, "Database failed to write, exiting to try and avoid data loss");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void recalculateLogic()
	{
		String code = this.gatherInputCode();
		
		String bestCode = database.findBestCodeFromCode(code);

		if(bestCode == null)
		{
			this.clearSymbolLines();
			this.resultText = "";
		}
		else
		{
			this.updateSymbolLines(database.getSymbolWithCode(bestCode));
			this.resultText = database.getWordWithCode(bestCode);
		}
		
		if(this.shouldCalculateSyllableDisplay)
		{
			this.syllableDisplay.recalculate(this.gatherInputCode(), this, database);
		}
		
		//TODO find better place for GC
		System.gc();
	}
	
	public void clearSymbolLines()
	{
		InteractibleLine line = null;
		for(int i=0;i<this.symbolLines.length;i++)
		{
			line = symbolLines[i];
			if(line != null)
			{
				line.filled = false;
			}
		}
		
		this.markDirty();
	}
	
	public void updateSymbolLines(Symbol symbol)
	{
		InteractibleLine line = null;
		for(int i=0; i < this.symbolLines.length; i++)
		{
			line = symbolLines[i];
			if(line != null)
			{
				//TODO unsafe array size stuff I guess
				line.filled = symbol.data[i];
			}
		}
		
		this.markDirty();
	}
	
	public String gatherInputCode()
	{
		return gatherInteractibleLineCode(this.selectableLines);
	}
	
	public String gatherResultCode()
	{
		return gatherInteractibleLineCode(this.symbolLines);
	}
	
	public String gatherInteractibleLineCode(InteractibleLine[] selector)
	{
		//Take all the symbols in interactibleline and serialize it into a string of 0's and 1's
		//Because it's EASIER that way
		//GOSH
		StringBuilder sb = new StringBuilder();
		
		InteractibleLine line = null;
		
		int pendingZeroes = 0;
		
		for(int i=0;i<selector.length;i++)
		{
			line = selector[i];
			
			if(line != null)
			{
				if(line.filled)
				{
					for(int j=0;j<pendingZeroes;j++)
					{
						sb.append("0");
					}
					
					pendingZeroes = 0;
					
					sb.append("1");
				}
				else
				{
					pendingZeroes += 1;
				}
			}
		}
		
		return sb.toString();
	}
	
	public void updateDefaultDimensions()
	{
		this.default_width = this.getWidth();
		this.default_height = this.getHeight();
	}
	
	public void createButtons()
	{
		//Create the clickable buttons
		int xpos = LINE_ENTRY_ZONE_X + (8 * 100) + 100;
		int ypos = LINE_ENTRY_ZONE_Y;
		
		int buttonwidth = 150;
		int buttonheight = 60;
		
		this.clearButton = new InteractibleBox(xpos, LINE_ENTRY_ZONE_Y + ypos + 17, buttonwidth, buttonheight);
		this.clearButton.text = "Clear";
		this.saveButton = new InteractibleBox(xpos, LINE_ENTRY_ZONE_Y + ypos + DISPLAY_SECTION_SCOOT_Y + 17, buttonwidth, buttonheight);
		this.saveButton.text = "Save";
		this.renameButton = new InteractibleBox(xpos, LINE_ENTRY_ZONE_Y + ypos + (DISPLAY_SECTION_SCOOT_Y *2) - 20, buttonwidth, buttonheight);
		this.renameButton.text = "Rename";
		this.deleteButton = new InteractibleBox(xpos, LINE_ENTRY_ZONE_Y + ypos + (DISPLAY_SECTION_SCOOT_Y * 2) + 80, buttonwidth, buttonheight);
		this.deleteButton.text = "Delete";
		this.copyButton = new InteractibleBox(xpos, LINE_ENTRY_ZONE_Y + ypos + (DISPLAY_SECTION_SCOOT_Y * 2) + 207, buttonwidth, buttonheight);
		this.copyButton.text = "Copy";
		this.leftButton = new InteractibleBox(xpos, LINE_ENTRY_ZONE_Y + ypos + 90, 60, 60);
		this.leftButton.text = "Left";
		this.rightButton = new InteractibleBox(xpos + 90, LINE_ENTRY_ZONE_Y + ypos + 90, 60, 60);
		this.rightButton.text = "Right";
	}
	
	public void createSymbolDisplayFromLineEntry(int xscoot, int yscoot)
	{
		InteractibleLine line = null;
		
		//Clone and copy over lines
		for(int i=0; i < selectableLines.length; i++)
		{
			line = selectableLines[i];
			
			if(line != null)
			{
				symbolLines[i] = new InteractibleLine(line.x1 + xscoot, line.y1 + yscoot, line.x2 + xscoot, line.y2 + yscoot);
				symbolLines[i].isCircle = selectableLines[i].isCircle;
			}
		}
	}
	
	public void createLineEntryZone(int xpos, int ypos)
	{
		//A hexagon's angle is 120 degrees
		ypos = ypos + 29; //Quick adjustment...
		
		//Base line
		regularLines[0] = new InteractibleLine(xpos, ypos + 58, xpos + (100 * 8), ypos + 58);
		regularLines[0].isRounded = false;
		regularLines[0].filled = true;
		
		regularLines[1] = new InteractibleLine(xpos + DISPLAY_SECTION_SCOOT_X, ypos + 58 + DISPLAY_SECTION_SCOOT_Y, xpos + (100 * 8) + DISPLAY_SECTION_SCOOT_X, ypos + 58 + DISPLAY_SECTION_SCOOT_Y);
		regularLines[1].isRounded = false;
		regularLines[1].filled = true;
		
		regularLines[2] = new InteractibleLine(0, ypos + DISPLAY_SECTION_SCOOT_Y - 51, 2500, ypos + DISPLAY_SECTION_SCOOT_Y - 51);
		
		regularLines[3] = new InteractibleLine(0, ypos + (DISPLAY_SECTION_SCOOT_Y * 2) - 51, 2500, ypos + (DISPLAY_SECTION_SCOOT_Y * 2) - 51);
		
		regularLines[4] = new InteractibleLine(xpos + DISPLAY_SECTION_SCOOT_X, ypos + 58 + (DISPLAY_SECTION_SCOOT_Y * 2), xpos + (100 * 8) + DISPLAY_SECTION_SCOOT_X, ypos + 58 + (DISPLAY_SECTION_SCOOT_Y * 2));
		regularLines[4].isRounded = false;
		regularLines[4].filled = true;
		
		displayHorizontalBar = regularLines[1];
		symbolHorizontalBar = regularLines[4];
		
		//Repeated segments have 14 each
		for(int i=0; i < 8;i++)
		{
			createRepeatedSegment(xpos, ypos, i * 14);
			xpos += 100;
		}
		
		//Add 2 to finalize
		selectableLines[112] = new InteractibleLine(xpos, ypos, xpos, ypos + 58);
		selectableLines[113] = new InteractibleLine(xpos, ypos + BOTTOM_LINE_SCOOT, xpos, ypos + BOTTOM_LINE_SCOOT - 29);
		
		//DEBUG
		//selectableLines[4].isDebug = true;
	}
	
	public void createRepeatedSegment(int xpos, int ypos, int id)
	{
		//Top diamond
		selectableLines[id] = new InteractibleLine(xpos, ypos, xpos + 50, ypos - 29);
		selectableLines[id + 1] = new InteractibleLine(xpos+50, ypos - 29, xpos + 100, ypos);
		selectableLines[id + 2] = new InteractibleLine(xpos, ypos, xpos + 50, ypos + 29);
		selectableLines[id + 3] = new InteractibleLine(xpos+50, ypos + 29, xpos + 100, ypos);
		//Inner line top bottom
		selectableLines[id + 4] = new InteractibleLine(xpos + 50, ypos - 29, xpos + 50, ypos + 29);
		selectableLines[id + 5] = new InteractibleLine(xpos + 50, ypos + 29, xpos + 50, ypos + 58);
		//Leftmost top line
		selectableLines[id + 6] = new InteractibleLine(xpos, ypos, xpos, ypos + 58);
		
		//Scoot ypos a bit
		ypos = ypos + BOTTOM_LINE_SCOOT;
		
		//Bottom diamond
		selectableLines[id + 7] = new InteractibleLine(xpos, ypos, xpos + 50, ypos - 29);
		selectableLines[id + 8] = new InteractibleLine(xpos+50, ypos - 29, xpos + 100, ypos);
		selectableLines[id + 9] = new InteractibleLine(xpos, ypos, xpos + 50, ypos + 29);
		selectableLines[id + 10] = new InteractibleLine(xpos+50, ypos + 29, xpos + 100, ypos);
		//Inner line
		selectableLines[id + 11] = new InteractibleLine(xpos + 50, ypos - 29, xpos + 50, ypos + 29);
		//Leftmost bottom line
		selectableLines[id + 12] = new InteractibleLine(xpos, ypos, xpos, ypos - 29);
		//bottom circular
		selectableLines[id + 13] = new InteractibleLine(xpos + 50, ypos + 29, xpos + 50, ypos + 29);
		selectableLines[id + 13].isCircle = true;
		
	}
	
	public float getScale()
	{
		float s_w = (float) this.getWidth() / (float) default_width;
		float s_h = (float) this.getHeight() / (float) default_height;
		
		return Math.min(s_w, s_h);
	}
	
	public void markDirty()
	{
		this.isDirty = true;
	}
	
	public void repaintIfDirty()
	{
		if(this.isDirty)
		{
			this.isDirty = false;
			TunicLanguageHelper.instance.frame.repaint();
		}
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		
		if(this.font == null)
			this.font = g2d.getFont().deriveFont(75.0f);
		
		g2d.setColor(Color.WHITE);
		g2d.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		
		//Set up line rules
		float scale = this.getScale();
		
		//Draw Lines in three passes
		drawSelectedPass(0, scale, g2d, 0, 0);
		drawSelectedPass(1, scale, g2d, 0, 0);
		drawSelectedPass(2, scale, g2d, 0, 0);
		
		
		InteractibleLine line = null;

		//Adjust horizontal bar
		int farthestX = -1;
		
		for(int i = 0; i < selectableLines.length; i++)
		{
			line = selectableLines[i];
			
			if(line != null && line.filled)
			{
				farthestX = Math.max(farthestX, line.x1);
				farthestX = Math.max(farthestX, line.x2);
			}
		}
		
		if(farthestX == -1)
		{
			this.displayHorizontalBar.hidden = true;
		}
		else
		{
			farthestX -= this.LINE_ENTRY_ZONE_X;
			
			if(farthestX == 0)
				farthestX = 1;
			
			this.displayHorizontalBar.hidden = false;

			double v = Math.ceil((double)farthestX / 100.0) * 100.0;
			v += this.LINE_ENTRY_ZONE_X;
			this.displayHorizontalBar.x2 = (int)v;
		}
		
		//Draw filled lines only for display section
		drawSelectedPass(3, scale, g2d, 0, DISPLAY_SECTION_SCOOT_Y);
		
		//Draw all for the symbol line for now
		//Will end up only drawing filled ones at completion but this is for debug
		farthestX = -1;
		for(int i = 0; i < symbolLines.length; i++)
		{
			line = symbolLines[i];
			
			if(line != null && line.filled)
			{
				g2d.setStroke(new BasicStroke(lineThickness * scale, line.isRounded? BasicStroke.CAP_ROUND : BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
				line.render(g2d, scale, 0, 0);
				farthestX = Math.max(farthestX, line.x1);
				farthestX = Math.max(farthestX, line.x2);
			}
		}
		
		if(farthestX == -1)
		{
			this.symbolHorizontalBar.hidden = true;
		}
		else
		{
			farthestX -= this.LINE_ENTRY_ZONE_X;
			
			if(farthestX == 0)
				farthestX = 1;
			
			this.symbolHorizontalBar.hidden = false;

			double v = Math.ceil((double)farthestX / 100.0) * 100.0;
			v += this.LINE_ENTRY_ZONE_X;
			this.symbolHorizontalBar.x2 = (int)v;
		}
		
		g2d.setStroke(new BasicStroke(boxThickness * scale, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
		
		//Draw buttons
		this.clearButton.render(g2d, scale, 0, 0);
		this.saveButton.render(g2d, scale, 0, 0);
		this.renameButton.render(g2d, scale, 0, 0);
		this.deleteButton.render(g2d, scale, 0, 0);
		this.copyButton.render(g2d, scale, 0, 0);
		this.leftButton.render(g2d, scale, 0, 0);
		this.rightButton.render(g2d, scale, 0, 0);
		
		g2d.setFont(this.font);
		
		if(scale != oldScale)
		{
			this.font = this.font.deriveFont(75.0f * scale);
			g2d.setFont(this.font);
			oldScale = scale;
		}
		
		//Draw result text
		g2d.drawString(this.resultText, scale(scale, LINE_ENTRY_ZONE_X), scale(scale, DISPLAY_SECTION_SCOOT_Y * 3 + 92));
		
		//Draw regular lines last
		for(int i = 0; i < regularLines.length; i++)
		{
			line = regularLines[i];
			
			if(line != null)
			{
				g2d.setStroke(new BasicStroke(lineThickness * scale, line.isRounded? BasicStroke.CAP_ROUND : BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
				line.render(g2d, scale, 0, 0);
			}
		}
		
		//Draw FEATURES
		if(this.shouldDrawSyllableDisplay)
		{
			this.syllableDisplay.render(g2d, scale, 0, 0);
		}
	}
	
	public int scale(float scale, int val)
	{
		return (int)((float) val * scale);
	}
	
	public void drawSelectedPass(int phase, float scale, Graphics2D g2d, int xoffset, int yoffset)
	{
		InteractibleLine line = null;
		
		for(int i = 0; i < selectableLines.length; i++)
		{
			line = selectableLines[i];
			
			if(line != null)
			{
				boolean a = phase == 0 && !line.filled && !line.selected;
				boolean b = phase == 1 && line.filled && !line.selected;
				boolean c = phase == 2 && line.selected;
				boolean d = phase == 3 && line.filled;
						
				if(a || b || c || d)
				{
					g2d.setStroke(new BasicStroke(lineThickness * scale, line.isRounded? BasicStroke.CAP_ROUND : BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
					line.render(g2d, scale, xoffset, yoffset);
				}
			}
		}
	}
	

	@Override
	public void mouseMoved(MouseEvent e)
	{
		checkLinesHover(e);
		
		

		this.repaintIfDirty();
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		checkLinesInput(e);
		checkButtonsInput(e);
		
		
		this.recalculateLogicIfNeeded();
		this.repaintIfDirty();
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		checkLinesHover(e);
		checkLinesInput(e);
		

		this.recalculateLogicIfNeeded();
		this.repaintIfDirty();
	}
	
	public void checkLinesHover(MouseEvent e)
	{
		float scale = this.getScale();
		int mouseX = e.getX();
		int mouseY = e.getY();
		
		double closestdist = 10000;
		int closestid = -1;
		
		//Highlight the selected line
		//Could optimize this a bit with scaling and whatever but it's insignificant
		
		//First pick the closest line
		for(int i = 0; i < selectableLines.length; i++)
		{
			InteractibleLine line = selectableLines[i];
			if(line != null)
			{
				double distance = line.getDistanceToMouse(mouseX, mouseY, scale);
				if(distance < closestdist)
				{
					closestdist = distance;
					closestid = i;
				}
			}
		}
		
		//Now check if the closest is close enough
		if(closestdist > 150)
			closestid = -1;
		
		//If closestid is set, there's an available line
		//Loop through and change lines appropriately
		
		for(int i = 0; i < selectableLines.length; i++)
		{
			InteractibleLine line = selectableLines[i];
			if(line != null)
			{
				if(closestid == i)
				{
					//SELECTED
					if(!line.selected)
					{
						line.selected = true;
						this.markDirty();
					}
				}
				else
				{
					//NOT SELECTED
					if(line.selected)
					{
						line.selected = false;
						this.markDirty();
					}
				}
			}
		}
	}
	
	public void recalculateLogicIfNeeded()
	{
		if(this.needsLogic)
		{
			this.needsLogic = false;
			this.recalculateLogic();
		}
	}
	
	public void markLogic()
	{
		this.needsLogic = true;
	}
	
	public void checkLinesInput(MouseEvent e)
	{
		boolean isLeft = SwingUtilities.isLeftMouseButton(e);
		boolean isRight = SwingUtilities.isRightMouseButton(e);
		
		if(isLeft || isRight)
		{
			for(int i = 0; i < selectableLines.length; i++)
			{
				InteractibleLine line = selectableLines[i];
				if(line != null && line.selected)
				{
					//Set line fill appropriately
					if(!line.filled && isLeft)
					{
						//Add line
						line.filled = !line.filled;
						this.markDirty();
						this.markLogic();
					}
					else if(line.filled && isRight)
					{
						//Remove line
						line.filled = !line.filled;
						this.markDirty();
						this.markLogic();
					}
				}
			}
		}

		this.recalculateLogicIfNeeded();
	}
	
	public void checkButtonsInput(MouseEvent e)
	{
		int mouseX = e.getX();
		int mouseY = e.getY();
		float scale = this.getScale();
		
		if(this.clearButton.getMouseInbounds(mouseX, mouseY, scale))
		{
			buttonClear();
		}
		else if(this.saveButton.getMouseInbounds(mouseX, mouseY, scale))
		{
			buttonSave();
		}
		else if(this.renameButton.getMouseInbounds(mouseX, mouseY, scale))
		{
			buttonRename();
		}
		else if(this.deleteButton.getMouseInbounds(mouseX, mouseY, scale))
		{
			buttonDelete();
		}
		else if(this.copyButton.getMouseInbounds(mouseX, mouseY, scale))
		{
			buttonCopy();
		}
		else if(this.leftButton.getMouseInbounds(mouseX, mouseY, scale))
		{
			buttonLeft();
		}
		else if(this.rightButton.getMouseInbounds(mouseX, mouseY, scale))
		{
			buttonRight();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		
	}
}
