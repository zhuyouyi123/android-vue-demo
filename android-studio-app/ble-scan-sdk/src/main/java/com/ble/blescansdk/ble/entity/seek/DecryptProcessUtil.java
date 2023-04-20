package com.ble.blescansdk.ble.entity.seek;

public class DecryptProcessUtil {
	private static boolean isInit = false;

	/**
	 * Init decryption
	 * 
	 * @param key
	 * 
	 */
	public static void init(byte key[]) {
		isInit = true;
		DecryptAPI.init(key);
	}

	/**
	 * Descryption
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
	public static int getUuidMajorMinor(byte uuid[], byte major[], byte minor[]) {
		int result = -1;
		if (isInit) {
			result = DecryptAPI.getUuidMajorMinor(uuid, major, minor);
		}
		return result;
	}

	/**
	 * Descryption
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
	public static int getUuidMajorMinorV2(byte uuid[], byte major[], byte minor[]) {
		int result = -1;
		if (isInit) {
			result = DecryptAPI.getUuidMajorMinorV2(uuid, major, minor);
		}
		return result;
	}

	/**
	 * Descryption
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
	public static int getUuidMajorMinorV3(byte uuid[], byte major[], byte minor[]) {
		int result = -1;
		if (isInit) {
			result = DecryptAPI.getUuidMajorMinorV3(uuid, major, minor);
		}
		return result;
	}

	/**
	 * Decryption for ele.me
	 * 
	 * @param major
	 * @param minor
	 * @param mac
	 * @return
	 */
	public static int getMajorMinorMacV3(byte major[], byte minor[], byte[] mac) {
		int result = -1;
		if (isInit) {
			byte uuid[] = new byte[12];
			byte majorTmp[] = new byte[] { major[0], major[1] };
			byte macTmp[] = new byte[] { mac[4], mac[5] };
			result = DecryptAPI.getUuidMajorMinorV3(uuid, major, minor);
			if (result == 0) {
				result = DecryptAPI.getUuidMajorMinorV3(uuid, majorTmp, macTmp);
				if (result == 0) {
					mac[4] = macTmp[0];
					mac[5] = macTmp[1];
				}
			}
		}
		return result;
	}

	/**
	 * Descryption electricity
	 * 
	 * @param electricity
	 * @return electricity * 10
	 */
	public static int electricity(byte electricity) {
		int result = -1;
		if (isInit) {
			result = DecryptAPI.electricity(electricity);
		}
		return result;
	}
}
