package tuniclanghelp.data;

public class Symbol
{
	public boolean[] data = new boolean[128]; 
	
	public Symbol()
	{
		
	}
	
	public Symbol(String dataStr)
	{
		super();
		setFromString(dataStr);
	}
	
	public static String toCode(Symbol symbol)
	{
		StringBuilder sb = new StringBuilder();
		
		boolean[] dt = symbol.data;
		
		int pendingZeroes = 0;
		
		for(int i=0;i<dt.length;i++)
		{
			if(dt[i])
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
		
		return sb.toString();
	}
	
	public float compareToThis(Symbol sym)
	{
		//Returns -1f if invalidated
		
		//SOMETHING IS WRONG WITH THE NAMES
		//SO TWO OF THE CHECKS ARE SWAPPED AROUND
		//CAN'T BE BOTHERED TO PICK UP THE BREAD CRUMBS
		//IT JUST WORKS
		
		boolean paramSymbol;
		boolean thisSymbol;
		
		boolean[] param = sym.data;
		
		float result = 0.0f;
		
		for(int i=0; i < data.length; i++)
		{
			paramSymbol = param[i];
			thisSymbol = data[i];
			
			if(paramSymbol && thisSymbol)
			{
				//Data has a match, add a lot of points
				result += 200f;
			}
			else if(!paramSymbol && thisSymbol) //swapping because I got confused
			{
				//Parameter has it, but this doesn't
				//Immediately invalidate
				return -1.0f;
			}
			else if(paramSymbol && !thisSymbol) //swapping because I got confused
			{
				//This symbol has it, but parameter doesn't
				//Subtract a point to lower the weight of larger symbols
				
				result -= 1f;
			}
		}
		
		return result;
	}
	
	public void setFromString(String dataStr)
	{
		clear();

		int j = 0;
		
		for(char c : dataStr.toCharArray())
		{
			if(c == '1')
				data[j] = true;
				
			j++;
		}
	}
	
	//Could aldo just get rid of the array, is GC good at doing that here?
	public void clear()
	{
		for (int i=0; i < data.length; i++)
		{
			data[i] = false;
		}
	}
	
	//Could cache this but it's whatever
	public boolean isEmpty()
	{
		for (int i=0; i < data.length; i++)
		{
			if(data[i])
				return false;
		}
		
		return true;
	}
}
