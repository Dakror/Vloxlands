package de.dakror.vloxlands.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

/**
 * This class creates a list of all internal assets files
 * to allow iterating through internal directories.<br>
 * To start the class call {@link #init()} in the startup of your program.
 * 
 * @author Dakror
 */
public class InternalAssetManager {
	/**
	 * Kindly copied from javax.swing.filechooser with removal of descriptions <br>
	 * <br>
	 * An implementation of {@link FileFilter} that filters using a
	 * specified set of extensions. The extension for a file is the
	 * portion of the file name after the last ".". Files whose name does
	 * not contain a "." have no file name extension. File name extension
	 * comparisons are case insensitive.
	 * <p>
	 * The following example creates a {@code FileNameExtensionFilter} that will show {@code jpg} files:
	 * 
	 * <pre>
	 * FileFilter filter = new FileNameExtensionFilter(&quot;jpg&quot;, &quot;jpeg&quot;);
	 * </pre>
	 *
	 * @see javax.swing.filechooser.FileNameExtensionFilter
	 * @see FileFilter
	 * @see javax.swing.JFileChooser#setFileFilter
	 * @see javax.swing.JFileChooser#addChoosableFileFilter
	 * @see javax.swing.JFileChooser#getFileFilter
	 */
	public static class FileNameExtensionFilter implements FileFilter {
		// Known extensions.
		private final String[] extensions;
		// Cached ext
		private final String[] lowerCaseExtensions;
		
		/**
		 * Creates a {@code FileNameExtensionFilter} with the specified
		 * description and file name extensions. The returned {@code FileNameExtensionFilter} will accept all directories and any
		 * file with a file name extension contained in {@code extensions}.
		 *
		 * @param extensions the accepted file name extensions
		 * @throws IllegalArgumentException if extensions is {@code null}, empty,
		 *           contains {@code null}, or contains an empty string
		 * @see #accept
		 */
		public FileNameExtensionFilter(String... extensions) {
			if (extensions == null || extensions.length == 0) {
				throw new IllegalArgumentException("Extensions must be non-null and not empty");
			}
			this.extensions = new String[extensions.length];
			lowerCaseExtensions = new String[extensions.length];
			for (int i = 0; i < extensions.length; i++) {
				if (extensions[i] == null || extensions[i].length() == 0) {
					throw new IllegalArgumentException("Each extension must be non-null and not empty");
				}
				this.extensions[i] = extensions[i];
				lowerCaseExtensions[i] = extensions[i].toLowerCase(Locale.ENGLISH);
			}
		}
		
		/**
		 * Tests the specified file, returning true if the file is
		 * accepted, false otherwise. True is returned if the extension
		 * matches one of the file name extensions of this {@code FileFilter}, or
		 * the file is a directory.
		 *
		 * @param f the {@code File} to test
		 * @return true if the file is to be accepted, false otherwise
		 */
		@Override
		public boolean accept(File f) {
			if (f != null) {
				if (f.isDirectory()) {
					return true;
				}
				// NOTE: we tested implementations using Maps, binary search
				// on a sorted list and this implementation. All implementations
				// provided roughly the same speed, most likely because of
				// overhead associated with java.io.File. Therefor we've stuck
				// with the simple lightweight approach.
				String fileName = f.getName();
				int i = fileName.lastIndexOf('.');
				if (i > 0 && i < fileName.length() - 1) {
					String desiredExtension = fileName.substring(i + 1).toLowerCase(Locale.ENGLISH);
					for (String extension : lowerCaseExtensions) {
						if (desiredExtension.equals(extension)) {
							return true;
						}
					}
				}
			}
			return false;
		}
		
		/**
		 * Returns the set of file name extensions files are tested against.
		 *
		 * @return the set of file name extensions files are tested against
		 */
		public String[] getExtensions() {
			String[] result = new String[extensions.length];
			System.arraycopy(extensions, 0, result, 0, extensions.length);
			return result;
		}
		
		/**
		 * Returns a string representation of the {@code FileNameExtensionFilter}.
		 * This method is intended to be used for debugging purposes,
		 * and the content and format of the returned string may vary
		 * between implementations.
		 *
		 * @return a string representation of this {@code FileNameExtensionFilter}
		 */
		@Override
		public String toString() {
			return super.toString() + "[extensions=" + java.util.Arrays.asList(getExtensions()) + "]";
		}
	}
	
	public static class FileNode {
		public FileNode parent;
		public FileHandle file;
		public Array<FileNode> children;
		public boolean directory;
		
		public FileNode(FileNode parent, FileHandle file, boolean directory) {
			this.parent = parent;
			this.file = file;
			this.directory = directory;
			
			children = new Array<FileNode>();
		}
		
		@Override
		public String toString() {
			return (parent != null && parent.file != null ? parent.file.name() : "") + ", " + (file != null ? file.name() : "") + ", " + children;
		}
	}
	
	static FileNode root;
	
	/**
	 * Initializes the InternalAssetManager.<br>
	 * Checks wether the program is currently running from a jar file.<br>
	 * If not, a list <i>FILES.txt</i> gets created containing all files and
	 * directories inside the assets folder.<br>
	 * Then all files and directories get loaded in a tree data structure.
	 */
	public static void init() {
		try {
			Reader reader = null;
			
			if (!isRunningFromJarFile()) {
				File parent = new File(InternalAssetManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile();
				
				File dst = new File(parent, "core/assets");
				if (!dst.exists()) dst = new File(parent, "android/assets");
				if (!dst.exists()) throw new FileNotFoundException("Could not locate assets folder");
				
				StringBuffer sb = new StringBuffer();
				writeDirectory(sb, dst, dst.getPath().length() + 1);
				FileWriter fw = new FileWriter(new File(dst, "FILES.txt"));
				String s = sb.toString();
				fw.write(s);
				fw.close();
				
				reader = new StringReader(s);
			}
			
			int files = 0;
			
			FileHandle fh = Gdx.files.internal("FILES.txt");
			BufferedReader br = new BufferedReader(reader == null ? fh.reader() : reader);
			String line = "";
			
			root = new FileNode(null, null, true);
			
			FileNode activeNode = root;
			int slashes = -1;
			
			while ((line = br.readLine()) != null) {
				boolean dir = line.startsWith("d");
				String path = line.substring(2);
				
				int sl = path.split("/").length - 1;
				if (slashes == -1) slashes = sl;
				
				while (slashes > sl) {
					activeNode = activeNode.parent;
					slashes--;
				}
				
				if (slashes == sl) {
					if (dir) {
						FileNode node = new FileNode(activeNode, Gdx.files.internal(path), true);
						activeNode.children.add(node);
						activeNode = node;
						slashes++;
					} else {
						FileNode node = new FileNode(activeNode, Gdx.files.internal(path), false);
						activeNode.children.add(node);
						files++;
					}
				}
			}
			
			Gdx.app.log("InternalAssetsManager.init", files + " files loaded.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param path the internal directory
	 * @return a list of all FILES in the specified directory
	 */
	public static FileNode[] listFiles(String path) {
		return listFiles(path, false);
	}
	
	/**
	 * @param path the internal directory
	 * @param recursive wether all files in all subfolders should be listed too
	 * @return a list of all FILES in the specified directory
	 */
	public static FileNode[] listFiles(String path, boolean recursive) {
		return list(path, true, false, recursive);
	}
	
	/**
	 * @param path the internal directory
	 * @return a list of all DIRECTORIES in the specified directory
	 */
	public static FileNode[] listDirectories(String path) {
		return listDirectories(path, false);
	}
	
	/**
	 * @param path the internal directory
	 * @param recursive wether all files in all subfolders should be listed too
	 * @return a list of all DIRECTORIES in the specified directory
	 */
	public static FileNode[] listDirectories(String path, boolean recursive) {
		return list(path, false, true, recursive);
	}
	
	/**
	 * @param path the internal directory
	 * @return a list of EVERYTHING in the specified directory
	 */
	public static FileNode[] list(String path) {
		return list(path, false);
	}
	
	/**
	 * @param path the internal directory
	 * @param recursive wether all files in all subfolders should be listed too
	 * @return a list of EVERYTHING in the specified directory
	 */
	public static FileNode[] list(String path, boolean recursive) {
		return list(path, true, true, recursive);
	}
	
	/**
	 * @return wether the current program is located in a file tree or packed jar
	 *         archive
	 */
	public static boolean isRunningFromJarFile() {
		try {
			return new File(InternalAssetManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).isFile();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Calls {@link AssetManager#load(String, Class)} for all files found in the
	 * directory.<br>
	 * If <code>recursive</code> is defined all files in all subfolders will
	 * get scheduled for loading as well.
	 * 
	 * @param assets the AssetManager to load the found assets
	 * @param path the directory to be loaded
	 * @param type the AssetManager Type e.g {@link Texture}
	 * @param recursive wether all files in all subfolders should be loaded too
	 */
	public static void scheduleDirectory(AssetManager assets, String path, Class<?> type, boolean recursive) {
		scheduleDirectory(assets, path, type, new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return true;
			}
		}, recursive);
	}
	
	/**
	 * Calls {@link AssetManager#load(String, Class)} for all files found in the
	 * directory.<br>
	 * If <code>recursive</code> is defined all files in all subfolders will
	 * get scheduled for loading as well.
	 * 
	 * @param assets the AssetManager to load the found assets
	 * @param path the directory to be loaded
	 * @param type the AssetManager Type e.g {@link Texture}
	 * @param fileFilter the {@link FileFilter} to apply. Use e.g a {@link FileNameExtensionFilter}
	 * @param recursive wether all files in all subfolders should be loaded too
	 */
	public static void scheduleDirectory(AssetManager assets, String path, Class<?> type, FileFilter fileFilter, boolean recursive) {
		for (FileNode fn : listFiles(path, recursive)) {
			if (fileFilter.accept(fn.file.file())) assets.load(fn.file.path(), type);
		}
	}
	
	static FileNode[] list(String path, boolean f, boolean d, boolean recursive) {
		FileHandle dir = Gdx.files.internal(path);
		FileNode fn = traverseTo(dir, root);
		
		Array<FileNode> files = new Array<FileNode>();
		
		Array<FileNode> dirs = new Array<FileNode>();
		
		for (FileNode file : fn.children) {
			if (file.directory) {
				if (d) files.add(file);
				if (recursive) dirs.add(file);
			}
			if (!file.directory && f) files.add(file);
		}
		
		if (recursive) {
			for (FileNode file : dirs)
				files.addAll(list(file.file.path(), f, d, true));
		}
		
		return files.toArray(FileNode.class);
	}
	
	static FileNode traverseTo(FileHandle searched, FileNode parent) {
		if (parent.file != null && searched.equals(parent.file)) return parent;
		for (FileNode fn : parent.children) {
			if (fn.directory && searched.path().substring(0, Math.min(fn.file.path().length(), searched.path().length())).equals(fn.file.path())) {
				return traverseTo(searched, fn);
			}
		}
		return null;
	}
	
	static void writeDirectory(StringBuffer sb, File dir, int pathOffset) throws IOException {
		File[] files = dir.listFiles();
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				boolean d1 = o1.isDirectory();
				boolean d2 = o2.isDirectory();
				
				if (d2 && !d1) return -1;
				else if (d1 && !d2) return 1;
				else return o1.getName().compareTo(o2.getName());
			}
		});
		for (File f : files) {
			sb.append((f.isDirectory() ? "d " : "f ") + f.getPath().substring(pathOffset).replace("\\", "/") + "\r\n");
			if (f.isDirectory()) writeDirectory(sb, f, pathOffset);
		}
	}
}
