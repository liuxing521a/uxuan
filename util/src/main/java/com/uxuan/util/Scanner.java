package com.uxuan.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.uxuan.util.Objects;
import com.uxuan.util.logger.Logger;
import com.uxuan.util.logger.LoggerFactory;

/**
 * 扫描类
 * 
 * @author liuzhen(liuxing521a@gmail.com)
 * @time 2016年4月26日 下午4:18:39
 */
public class Scanner {
	
	private static final Logger logger = LoggerFactory.getLogger(Scanner.class);

	private String basePackage;
	private ClassLoader cl;

	/**
	 * Construct an instance and specify the base package it should scan.
	 * 
	 * @param basePackage
	 *            The base package to scan.
	 */
	public Scanner(String basePackage) {
		this.basePackage = basePackage;
		this.cl = getClass().getClassLoader();
	}

	/**
	 * Construct an instance with base package and class loader.
	 * 
	 * @param basePackage
	 *            The base package to scan.
	 * @param cl
	 *            Use this class load to locate the package.
	 */
	public Scanner(String basePackage, ClassLoader cl) {
		this.basePackage = basePackage;
		this.cl = cl;
	}

	/**
	 * Get all fully qualified names located in the specified package and its
	 * sub-package.
	 *
	 * @return A list of fully qualified names.
	 * @throws IOException
	 */
	// @Override
	public List<String> getFullyQualifiedClassNameList() throws IOException {
		return doScan(basePackage, new ArrayList<>());
	}

	/**
	 * Actually perform the scanning procedure.
	 *
	 * @param basePackage
	 * @param nameList
	 *            A list to contain the result.
	 * @return A list of fully qualified names.
	 *
	 * @throws IOException
	 */
	private List<String> doScan(String basePackage, List<String> nameList) throws IOException {
		// replace dots with splashes
		String splashPath = dotToSplash(basePackage);

		// get file path
		URL url = cl.getResource(splashPath);
		String filePath = getRootPath(url);

		// Get classes in that package.
		// If the web server unzips the jar file, then the classes will exist in
		// the form of
		// normal file in the directory.
		// If the web server does not unzip the jar file, then classes will
		// exist in jar file.
		List<String> names = null; // contains the name of the class file. e.g.,
									// Apple.class will be stored as "Apple"
		if (isJarFile(filePath)) {
			// jar file
			names = readFromJarFile(filePath, splashPath);
		} else {
			// directory
			names = readFromDirectory(filePath);
		}

		for (String name : names) {
			if (isClassFile(name)) {
				// nameList.add(basePackage + "." +
				// StringUtil.trimExtension(name));
				nameList.add(toFullyQualifiedName(name, basePackage));
			} else {
				// this is a directory
				// check this directory for more classes
				// do recursive invocation
				doScan(basePackage + "." + name, nameList);
			}
		}

		return nameList;
	}

	/**
	 * Convert short class name to fully qualified name. e.g., String ->
	 * java.lang.String
	 */
	private String toFullyQualifiedName(String shortName, String basePackage) {
		shortName = shortName.replace("/", ".").replace("\\", ".");
		
		int lastIndex = shortName.indexOf(basePackage);
		if (lastIndex != -1) {
			shortName = shortName.substring(lastIndex, shortName.length());
		}
		
		lastIndex = shortName.indexOf(".class");
		if (lastIndex != -1) {
			shortName = shortName.substring(0, lastIndex);
		}
		logger.trace("{}", shortName);

		return shortName;
	}

	private List<String> readFromJarFile(String jarPath, String splashedPackageName) throws IOException {
		try (JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath))) {
			JarEntry entry = jarIn.getNextJarEntry();
	
			List<String> nameList = new ArrayList<>();
			while (null != entry) {
				String name = entry.getName();
				if (name.startsWith(splashedPackageName) && isClassFile(name)) {
					nameList.add(name);
				}
	
				entry = jarIn.getNextJarEntry();
			}
	
			return nameList;
		}
	}

	private List<String> readFromDirectory(String path) {
		File file = new File(path);
		
		List<String> nameList = new ArrayList<>();
		loadFiles(nameList, file);

		return nameList;
	}
	
	private void loadFiles(List<String> nameList, File file) {
		File[] files = file.listFiles();

		if (Objects.isEmpty(files)) {
			return;
		}
		
		for (File f : files) {
			if (f.isDirectory()) 
				loadFiles(nameList, f);
			else 
				nameList.add(f.getAbsolutePath());
		}
	}

	private boolean isClassFile(String name) {
		return name.endsWith(".class");
	}

	private boolean isJarFile(String name) {
		return name.endsWith(".jar");
	}


	private String getRootPath(URL url) {
		String fileUrl = url.getFile();
		int pos = fileUrl.indexOf('!');

		if (-1 == pos) {
			return fileUrl;
		}

		return fileUrl.substring(5, pos);
	}

	public static String dotToSplash(String name) {
		return name.replaceAll("\\.", "/");
	}
	
}

