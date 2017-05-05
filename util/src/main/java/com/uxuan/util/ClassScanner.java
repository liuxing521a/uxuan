/*
 * Copyright [2015] [liuxing521a]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uxuan.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.uxuan.util.logger.Logger;
import com.uxuan.util.logger.LoggerFactory;

/**
 * 类文件扫描
 * 
 * @author liuzhen(liuxing521a@163.com)
 * @createTime 2016-05-07 14:34:46
 */
public class ClassScanner {
	
	static final Logger logger = LoggerFactory.getLogger(ClassScanner.class);

	private String basePackage;
	private ClassLoader cl;

	public ClassScanner(String basePackage) {
		this.basePackage = basePackage;
		this.cl = getClass().getClassLoader();
	}

	public ClassScanner(String basePackage, ClassLoader cl) {
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
	//@Override
	public List<String> getFullyClassNameList() throws IOException {
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
			names = readFromJarFile(filePath, splashPath);
		} else {
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
		shortName = shortName.replace("/", ".");
		
		int lastIndex = shortName.indexOf(basePackage);
		if (lastIndex != -1) {
			shortName = shortName.substring(lastIndex, shortName.length());
		}
		
		lastIndex = shortName.indexOf(".class");
		if (lastIndex != -1) {
			shortName = shortName.substring(0, lastIndex);
		}

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

	private String dotToSplash(String name) {
		return name.replaceAll("\\.", "/");
	}
	

}
