package tuniclanghelp.data;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Database
{
	private Map<String, String> words;
	private Map<String, Symbol> symbols;
	
	public Path databaseDirPath;
	public File databaseFile;
	
	public final String baseName = "database";
	public final String baseExtension = ".txt";
	
	public Database()
	{
		words = new HashMap<String, String>();
		symbols = new HashMap<String, Symbol>();
		databaseDirPath = new File(System.getProperty("user.dir")).toPath().resolve("tuniclanghelp_db");
		databaseDirPath.toFile().mkdirs();
		databaseFile = getRealDatabaseFile();
	}
	
	public File getRealDatabaseFile()
	{
		return databaseDirPath.resolve(baseName+baseExtension).toFile();
	}
	
	public File getNewBackupDatabaseFile()
	{
		return databaseDirPath.resolve(baseName+"_"+System.currentTimeMillis()+baseExtension).toFile();
	}
	
	public String getWordWithCode(String code)
	{
		return words.get(code);
	}
	
	public Symbol getSymbolWithCode(String code)
	{
		return symbols.get(code);
	}
	
	public void populateWordsFromFile() throws Exception
	{
		boolean flip = false;
		String pendingKey = "";
		
		if(databaseFile.exists())
		{
			try(Scanner scn = new Scanner(databaseFile))
			{
				while(scn.hasNextLine())
				{
					String line = scn.nextLine();
					
					if(!flip)
					{
						pendingKey = line;
					}
					else
					{
						this.words.put(pendingKey, line);
						registerSymbol(pendingKey);
					}
					
					flip = !flip;
				}
			}
		}
		else
		{
			writeDatabase();
		}
	}
	
	private void registerSymbol(String code)
	{
		this.symbols.put(code, new Symbol(code));
	}
	
	public void writeDatabase() throws Exception
	{
		writeDatabaseToFile(this.databaseFile);
		writeDatabaseToFile(this.getNewBackupDatabaseFile());
	}
	
	private void writeDatabaseToFile(File f) throws Exception
	{
		try(PrintWriter pw = new PrintWriter(f))
		{
			for(Map.Entry<String, String> entry : words.entrySet())
			{
				pw.println(entry.getKey());
				pw.println(entry.getValue());
			}
		}
	}
	
	public void debugPrintWords()
	{
		for(Map.Entry<String, String> entry : words.entrySet())
		{
			System.out.println(entry.getValue() + " : " + entry.getKey());
		}
	}
	
	public String findBestCodeFromCode(String code)
	{
		if(this.symbols.containsKey(code))
			return code;
		
		float rating = -1f;
		String winner = "";
		
		Symbol sym = new Symbol(code);
		
		for(Map.Entry<String, Symbol> entry : this.symbols.entrySet())
		{
			float check = sym.compareToThis(entry.getValue());
			if(check > rating)
			{
				rating = check;
				winner = entry.getKey();
			}
		}
		
		if(rating == -1)
			return null;
		
		//System.out.println(this.words.get(winner)+" : "+rating);
		
		return winner;
	}
	
	public void saveWord(String code, String word)
	{
		registerSymbol(code);
		this.words.put(code, word);
	}
	
	public void deleteCode(String code)
	{
		this.words.remove(code);
		this.symbols.remove(code);
	}
	
	public boolean hasCode(String code)
	{
		return this.symbols.containsKey(code);
	}
	
	public void renameWordToWord(String old, String newer)
	{
		List<String> matches = new ArrayList<>();
		
		for(Map.Entry<String, String> entry : words.entrySet())
		{
			if(entry.getValue().equals(old))
			{
				matches.add(entry.getKey());
			}
		}
		
		for(String match : matches)
		{
			words.put(match, newer);
		}
	}
	
	//TODO some feature to type out a word and have the symbol pop out
}
