package com.uxuan.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class UUIDGenerator {
	
	private String valueBeforeMD5;
	private String valueAfterMD5;

	private static Random myRand;
	private static SecureRandom mySecureRand;

	private static String hostAddress;
	private static final int PAD_BELOW = 0x10;
	private static final int TWO_BYTES = 0xFF;

	static {
		mySecureRand = new SecureRandom();
		long secureInitializer = mySecureRand.nextLong();
		myRand = new Random(secureInitializer);
		try {
			hostAddress = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}

	}

	public UUIDGenerator() {
		getRandomGUID(false);
	}

	public UUIDGenerator(boolean secure) {
		getRandomGUID(secure);
	}
	
	public static String randomUUID() {
		UUIDGenerator uuid = new UUIDGenerator(true);
		return uuid.valueAfterMD5.toLowerCase();
	}
	
	public static String randomUUID(boolean secure) {
		UUIDGenerator uuid = new UUIDGenerator(secure);
		return uuid.valueAfterMD5.toLowerCase();
	}

	private String getRandomGUID(boolean secure) {
		MessageDigest md5 = null;
		StringBuffer sbValueBeforeMD5 = new StringBuffer(128);

		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		try {
			long time = System.currentTimeMillis();
			long rand = 0;

			if (secure) {
				rand = mySecureRand.nextLong();
			} else {
				rand = myRand.nextLong();
			}
			sbValueBeforeMD5.append(hostAddress);
			sbValueBeforeMD5.append(":");
			sbValueBeforeMD5.append(Long.toString(time));
			sbValueBeforeMD5.append(":");
			sbValueBeforeMD5.append(Long.toString(rand));

			valueBeforeMD5 = sbValueBeforeMD5.toString();
			md5.update(valueBeforeMD5.getBytes());

			byte[] array = md5.digest();
			StringBuffer sb = new StringBuffer(32);
			for (byte bs : array) {
				int b = bs & TWO_BYTES;
				if (b < PAD_BELOW) {
					sb.append('0');
				}
				sb.append(Integer.toHexString(b));
			}

			valueAfterMD5 = sb.toString().toUpperCase();
			return valueAfterMD5;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(64);
		sb.append("info:").append(valueBeforeMD5).append("\n");
		sb.append("uid:").append(valueAfterMD5);

		return sb.toString();
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 20; i++) {
			System.out.println(randomUUID(true));
		}
	}

}
