package de.codecentric.spa.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.codecentric.spa.utils.ZipClassLoader;

/**
 * Utility that scans given package for entity classes and returns {@link EntityMetaData} containing the information how
 * should the given class be persisted. It is heavily dependent on {@link EntityScanner}.
 */
public class PackageScanner {

	/**
	 * Method scans the given package and returns list of class descriptors in shape of {@link EntityMetaData}.
	 * 
	 * @param pckgName
	 *            package name
	 * @return list of class descriptors
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @see EntityScanner#scanClass(Class)
	 */
	public static List<EntityMetaData> scanPackage(String packageName) throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;

		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			String fileName = resource.getFile();
			String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
			dirs.add(new File(fileNameDecoded));
		}

		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}

		ArrayList<EntityMetaData> result = new ArrayList<EntityMetaData>();
		if (classes != null && !classes.isEmpty()) {
			for (Class<?> cls : classes) {
				EntityScanner.scanClass(cls, true);

				EntityMetaData scanResult = EntityMetaDataProvider.getInstance().getMetaData(cls);
				if (scanResult != null) {
					result.add(scanResult);
				}
			}
		}

		return result;
	}

	/**
	 * Recursive method used to find all classes in a given directory and sub-directories.
	 * 
	 * @param directory
	 *            base directory
	 * @param packageName
	 *            package name for classes found inside the base directory
	 * @return list of classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName) throws Exception {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}

		File[] files = directory.listFiles();
		for (File file : files) {
			String fileName = file.getName();

			if (file.isDirectory()) {
				assert !fileName.contains(".");
				classes.addAll(findClasses(file, packageName + "." + fileName));
			} else if (fileName.endsWith(".class") && !fileName.contains("$")) {
				Class<?> _class;
				try {
					_class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
				} catch (ExceptionInInitializerError e) {
					_class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6), false,
							Thread.currentThread().getContextClassLoader());
				}
				classes.add(_class);
			}
		}

		return classes;
	}

	/**
	 * Method used to retrieve all jar class path entries.
	 * 
	 * @return list of jar class path entries
	 */
	public static List<String> getJarClassPathEntries() {
		List<String> jars = new ArrayList<String>(0);

		String classPath = System.getProperty("java.class.path");
		String[] classPathMembers = classPath.split(";");
		for (String s : classPathMembers) {
			if (s.endsWith("jar")) {
				jars.add(s);
			}
		}

		return jars;
	}

	/**
	 * Method scans given package within a jar file on a given path.
	 * 
	 * @param path
	 * @param packageName
	 * @return list of class descriptors
	 * @throws Exception
	 */
	public static List<EntityMetaData> scanJar(String path, String packageName) throws Exception {
		String pckg = packageName.replaceAll("\\.", "/");
		List<Class<?>> classes = new ArrayList<Class<?>>(0);

		ZipInputStream zip = new ZipInputStream(new FileInputStream(path));
		ZipEntry entry = zip.getNextEntry();
		while (entry != null) {
			String entryName = entry.getName();
			if (!entry.isDirectory() && entryName.startsWith(pckg) && entryName.endsWith("class")) {
				File jarFile = new File(path);

				String s = jarFile.getAbsolutePath();
				ZipClassLoader zipClsLdr = new ZipClassLoader(s);
				classes.add(zipClsLdr.loadClass(entryName));
			}

			entry = zip.getNextEntry();
		}
		zip.close();

		List<EntityMetaData> result = new ArrayList<EntityMetaData>();
		if (classes != null && !classes.isEmpty()) {
			for (Class<?> cls : classes) {
				EntityScanner.scanClass(cls, true);

				EntityMetaData scanResult = EntityMetaDataProvider.getInstance().getMetaData(cls);
				if (scanResult != null) {
					result.add(scanResult);
				}
			}
		}

		return result;
	}
}
