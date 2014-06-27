package de.dakror.vloxlands.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Dakror
 */
public interface Savable
{
	public void save(ByteArrayOutputStream baos) throws IOException;
}
