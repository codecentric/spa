package de.codecentric.spa.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Class loader used to load classes contained inside a jar file.
 */
public class ZipClassLoader extends ClassLoader {

	private final ZipFile file;

	public ZipClassLoader(String filename) throws IOException {
		this.file = new ZipFile(filename);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String escaped = name.replace("/", ".").substring(0, name.lastIndexOf('.'));
		ZipEntry entry = this.file.getEntry(name);
		if (entry == null) {
			throw new ClassNotFoundException(escaped);
		}
		try {
			byte[] array = new byte[1024];
			InputStream in = this.file.getInputStream(entry);
			ByteArrayOutputStream out = new ByteArrayOutputStream(array.length);
			int length = in.read(array);
			while (length > 0) {
				out.write(array, 0, length);
				length = in.read(array);
			}
			
			return defineClass(escaped, out.toByteArray(), 0, out.size());
		} catch (IOException exception) {
			throw new ClassNotFoundException(escaped, exception);
		}
	}

}
