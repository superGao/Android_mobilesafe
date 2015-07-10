package com.superGao.mobilesafe.utils;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密的工具类
 * @author gao
 *
 */
public class MD5Utils {

	public static String encode(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bytes = digest.digest(password.getBytes());

			StringBuffer sb = new StringBuffer();
			for (byte b : bytes) {
				int i = b & 0xff;// 获取低8位内容
				String hexString = Integer.toHexString(i);

				if (hexString.length() == 1) {
					hexString = "0" + hexString;
				}

				sb.append(hexString);
			}

			String md5 = sb.toString();
			return md5;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}
	/**
	 * 将文件MD5加密
	 * 
	 * @param path
	 * @return
	 */
	public static String encodeFile(String path) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");

			FileInputStream in = new FileInputStream(path);

			int len = 0;
			byte[] buffer = new byte[1024];
			while ((len = in.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}

			byte[] bytes = digest.digest();

			StringBuffer sb = new StringBuffer();
			for (byte b : bytes) {
				int i = b & 0xff;// 获取低8位内容
				String hexString = Integer.toHexString(i);

				if (hexString.length() == 1) {
					hexString = "0" + hexString;
				}

				sb.append(hexString);
			}

			String md5 = sb.toString();
			return md5;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
