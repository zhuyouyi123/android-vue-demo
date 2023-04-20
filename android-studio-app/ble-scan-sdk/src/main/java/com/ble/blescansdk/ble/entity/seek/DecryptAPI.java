package com.ble.blescansdk.ble.entity.seek;

public class DecryptAPI {
	/**
	 * Description: Init Decrypt api
	 * 
	 *            encrypt key
	 * 
	 */
	public native static void init(byte key[]);

	/**
	 * Description
	 * 
	 * @param uuid
	 * 
	 * @param major
	 * 
	 * @param minor
	 * 
	 * @return 0--success, decrypt the encrypted ibeacon 1--success, decrypt the
	 *         not encrypted ibeacon -1--local time invalid -2--Time is not
	 *         synchronized
	 */
	public native static int getUuidMajorMinor(byte uuid[], byte major[],
			byte minor[]);

	/**
	 * Description
	 * 
	 * @param electricity
	 * @return result
	 */
	public native static int electricity(byte electricity);

	/**
	 * Description
	 * 
	 * @param uuid
	 * 
	 * @param major
	 * 
	 * @param minor
	 * 
	 * @return 0--success, decrypt the encrypted ibeacon 1--success, decrypt the
	 *         not encrypted ibeacon -1--local time invalid -2--Time is not
	 *         synchronized
	 */
	public native static int getUuidMajorMinorV2(byte uuid[], byte major[],
			byte minor[]);
	
	/**
	 * Description
	 * 
	 * @param uuid
	 * 
	 * @param major
	 * 
	 * @param minor
	 * 
	 * @return 0--success, decrypt the encrypted ibeacon 1--success, decrypt the
	 *         not encrypted ibeacon -1--local time invalid -2--Time is not
	 *         synchronized
	 */
	public native static int getUuidMajorMinorV3(byte uuid[], byte major[],
			byte minor[]);

	static {
		System.loadLibrary("DecryptAPI");
	}
}
