package de.dakror.vloxlands.util;

import java.io.BufferedReader;
import java.io.IOException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class CSVReader
{
	BufferedReader br;
	public String sep = ";";
	
	String[] segments;
	int index, lIndex;
	int lineLength = -1;
	
	public CSVReader(FileHandle fh)
	{
		try
		{
			br = new BufferedReader(fh.reader());
			index = 0;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void skipRow()
	{
		try
		{
			br.readLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public int getIndex()
	{
		return lIndex;
	}
	
	public String[] readRow()
	{
		try
		{
			String l = br.readLine();
			
			if (l == null)
			{
				br.close();
				return null;
			}
			return l.split(sep);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String readNext()
	{
		try
		{
			if (segments == null || index == lineLength)
			{
				String l = br.readLine();
				if (l == null)
				{
					br.close();
					return null;
				}
				while (l != null && l.length() == 0)
					l = br.readLine();
				
				if (l == null)
				{
					br.close();
					return null;
				}
				
				segments = splitCells(l);
				if (lineLength == -1)
				{
					lineLength = segments.length;
				}
				if (lineLength != -1 && segments.length != lineLength) throw new Exception("Each row has to have exactly " + lineLength + " cells! \n    This row only has " + segments.length + ": " + l);
				
				lIndex = index;
				index = 0;
			}
			
			lIndex = index;
			return segments[index++].trim();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private String[] splitCells(String row)
	{
		Array<String> cells = new Array<String>();
		String r = row;
		
		while (r.indexOf(sep) > -1)
		{
			cells.add(r.substring(0, r.indexOf(sep)));
			r = r.substring(r.indexOf(sep) + 1);
		}
		cells.add(r);
		
		return cells.toArray(String.class);
	}
}
