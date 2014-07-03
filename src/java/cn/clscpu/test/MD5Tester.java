/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.clscpu.test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author CPU
 */
public class MD5Tester {

	public static void main(String args[]) {
		Md5("aaa");
		System.out.println("" + true);
	}
	
	private static void Md5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuilder sb = new StringBuilder();
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					sb.append("0");
				}
				sb.append(Integer.toHexString(i));
			}

			System.out.println("result: " + sb.toString());//32位的加密 

			System.out.println("result: " + sb.toString().substring(8, 24));//16位的加密 

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
