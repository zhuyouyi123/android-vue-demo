package com.ble.blescansdk.ble.utils;

/**
 * SeekcyBeacon的加密解密函数
 * 
 * @author frank
 * 
 */
public class SKYBeaconEncryptUtil {
	/**
	 * 解密Response数据
	 * 
	 * @param rawDatas 原始数据
	 * @param index 第几个数据开始加密
	 * @return
	 */
	public static byte[] decryptResponseDatas(byte[] rawDatas, int index) {
		byte[] decrypted = new byte[27];
		
		for(int i = 0; i< 27; i++){
			decrypted[i] = (byte) (rawDatas[index+i] ^ rawDatas[index+26]);
		}
		
		return decrypted;
	}

	/**
	 * 解密配置通信协议数据
	 * 
	 * @param encryptDatas
	 * @param randomKey
	 * @return
	 */
	public static byte[] decryptCharacteristicDatas(byte[] encryptDatas, byte randomKey) {
		byte[] decrypted = new byte[encryptDatas.length];
		
		for (int i = 0; i < encryptDatas.length; i++) {
			decrypted[i] = (byte) (randomKey ^ encryptDatas[i]);
		}

		return decrypted;
	}

	/**
	 * 加密配置通信协议数据
	 * 
	 * @param rawDatas
	 * @param randomKey
	 * @return
	 */
	public static byte[] encryptCharacteristicDatas(byte[] rawDatas, byte randomKey) {
		byte[] encrypted = new byte[rawDatas.length];

		for (int i = 0; i < rawDatas.length; i++) {
			encrypted[i] = (byte) (randomKey ^ rawDatas[i]);
		}
		
		return encrypted;
	}
}
