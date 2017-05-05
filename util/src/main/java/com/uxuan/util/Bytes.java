package com.uxuan.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * 字节和整形相互转换
 * 
 * @author liuzhen
 */
public final class Bytes {
	
	/**
	 * byte数组转String 
	 * 
	 * @param bytes
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String byte2String(byte[] bytes) throws UnsupportedEncodingException {  
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		
    	return  new String(bytes, CharEncoding.UTF8);  
	}  
	  
	/**
	 *  String转byte数组
	 * 
	 * @param bytes
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static byte[] String2Byte(String data) {  
		return data.getBytes(CharEncoding.UTF8);
	}
	
	/**
	 * byte数组转short 
	 * 
	 * @param bytes
	 * @return
	 */
	public static short byte2Short(byte[] bytes) {  
		short value = 0; 
		
		value |= ((bytes[0] & 0xFF) << 8);
		value |= (bytes[1] & 0xFF);
		
	    return  value;  
	}  
	  
	/**
	 *  short转byte数组
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] short2Byte(short data) {  
		byte[] bytes = new byte[2];
		
		bytes[0] = (byte)((data >> 8) & 0XFF);
		bytes[1] = (byte)(data & 0XFF);
		
		return bytes;
	}
	
	/**
	 * byte数组转int 
	 * 
	 * @param bytes
	 * @return
	 */
	public static int byte2Int(byte[] bytes) {  
		int value = 0; 
		
		value |= ((bytes[0] & 0xFF) << 24);
		value |= ((bytes[1] & 0xFF) << 16);
		value |= ((bytes[2] & 0xFF) << 8);
		value |= (bytes[3] & 0xFF);
		
	    return  value;  
	}  
	  
	/**
	 *  int转byte数组
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] int2byte(int data) {  
		byte[] bytes = new byte[4];
		
		bytes[0] = (byte)((data >> 24) & 0XFF);
		bytes[1] = (byte)((data >> 16) & 0XFF);
		bytes[2] = (byte)((data >> 8) & 0XFF);
		bytes[3] = (byte)(data & 0XFF);
		
		return bytes;
	}  
	
	/**
	 * byte数组转int 
	 * 
	 * @param bytes
	 * @return
	 */
	public static int byte2Long(byte[] bytes) {  
		int value = 0; 
		
		value |= ((bytes[0] & 0xFF) << 56);
		value |= ((bytes[1] & 0xFF) << 48);
		value |= ((bytes[2] & 0xFF) << 40);
		value |= ((bytes[3] & 0xFF) << 32);
		value |= ((bytes[4] & 0xFF) << 24);
		value |= ((bytes[5] & 0xFF) << 16);
		value |= ((bytes[6] & 0xFF) << 8);
		value |= (bytes[7] & 0xFF);
		
	    return  value;  
	}  
	  
	/**
	 *  int转byte数组
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] long2Byte(int data) {  
		byte[] bytes = new byte[8];
		
		bytes[0] = (byte)((data >> 56) & 0XFF);
		bytes[1] = (byte)((data >> 48) & 0XFF);
		bytes[2] = (byte)((data >> 40) & 0XFF);
		bytes[3] = (byte)((data >> 32) & 0XFF);
		bytes[4] = (byte)((data >> 24) & 0XFF);
		bytes[5] = (byte)((data >> 16) & 0XFF);
		bytes[6] = (byte)((data >> 8) & 0XFF);
		bytes[7] = (byte)(data & 0XFF);
		
		return bytes;
	}

	public static int byte2int(byte[] bs, int pos, int len)  {
		byte[] current = getByte(bs, pos, len);
		
		return byte2Int(current);
	}
	
	public static byte[] getByte(byte[] src, int pos, int len) {
		if (pos + len > src.length) {
			throw new IndexOutOfBoundsException("bound: " + src.length + ", max: " + (len + pos));
		}
		
		byte[] bs = new byte[len];
		System.arraycopy(src, pos, bs, 0, len);
		return bs;
	}
	
	public static void main(String[] args) {
		byte[] bs = short2Byte((short)11200);
		ByteBuffer buf = ByteBuffer.wrap(bs);
		System.out.println(buf.getShort());
		System.out.println(byte2Short(bs));
	}
	
	private Bytes() {
	}
	
}
