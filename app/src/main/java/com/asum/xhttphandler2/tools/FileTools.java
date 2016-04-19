package com.asum.xhttphandler2.tools;

import java.io.File;

/**
 * 文件操作类
 * 
 * @author Asum
 *
 */
public class FileTools {
	/**
	 * 删除指定目录下的所有文件
	 * 
	 * @param rootDir
	 */
	public static void removeAll(String rootDir) {
		try {
			File dirFile = new File(rootDir);
			// 如果dir对应的文件不存在，或者不是一个目录，则退出
			if (!dirFile.exists() || !dirFile.isDirectory()) {
				return;
			} else {
				// 删除文件夹下的所有文件(包括子目录)
				File[] files = dirFile.listFiles();
				for (int i = 0; i < files.length; i++) {
					// 删除子文件
					if (files[i].isFile()) {
						files[i].delete();
					} else {
						// 删除子目录
						removeAll(files[i].getAbsolutePath());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除指定路径的文件
	 * 
	 * @param path
	 */
	public static void removeFile(String path) {
		File file = new File(path);
		// 判断目录或文件是否存在
		if (file.exists()) {
			// 判断是否为文件
			if (file.isFile()) { // 为文件时调用删除文件方法
				file.delete();
			}
		}
	}

	/**
	 * 创建文件夹
	 * 
	 * @param path
	 */
	public static void createFolder(String path) {
		File file = new File(path);
		// 如果文件夹不存在则创建
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
	}

	/**
	 * 获取指定目录下文件的总大小
	 * 
	 * @param path
	 * @return
	 */
	public static long getAllFileSize(String path) {
		long allSize = 0;
		try {
			File dirFile = new File(path);
			// 如果dir对应的文件不存在，或者不是一个目录，则退出
			if (!dirFile.exists() || !dirFile.isDirectory()) {
				if (dirFile.exists()) {
					return dirFile.length();
				} else {
					return 0;
				}
			} else {
				File[] files = dirFile.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						allSize += files[i].length();
					} else {
						allSize += getAllFileSize(files[i].getAbsolutePath());
					}
				}
				return allSize;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
