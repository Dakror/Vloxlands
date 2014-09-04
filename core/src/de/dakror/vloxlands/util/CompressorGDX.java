package de.dakror.vloxlands.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.badlogic.gdx.utils.compression.Lzma;

/**
 * @author Dakror
 */
public class CompressorGDX
{
	public static byte[] compress(byte[] b)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			Lzma.compress(new ByteArrayInputStream(b), baos);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return baos.toByteArray();
	}
	
	public static byte[] decompress(byte[] b)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			Lzma.decompress(new ByteArrayInputStream(b), baos);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return baos.toByteArray();
	}
	
	public static byte[] compressRow(byte[] b)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte active = b[0];
			byte same = (byte) -127;
			for (int i = 1; i < b.length; i++)
			{
				if (b[i] == active && same < 127) same += 1;
				else
				{
					baos.write(new byte[] { same, active });
					same = -127;
					active = b[i];
				}
			}
			
			baos.write(new byte[] { same, active });
			
			return baos.toByteArray();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] decompressRow(byte[] b)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i = 0; i < b.length; i += 2)
		{
			for (int j = 0; j < b[i] + 128; j++)
			{
				baos.write(b[i + 1]);
			}
		}
		return baos.toByteArray();
	}
}
