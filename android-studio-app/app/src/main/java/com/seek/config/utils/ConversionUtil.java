package com.seek.config.utils;

public class ConversionUtil {

    /**
     * 十进制转换为二进制字符串
     */

    public static String decToBin(int number) {

        return Long.toBinaryString(number);

    }

    /**
     * 二进制字符串转换为十进制数字
     */

    public static Long binToDec(String number) {

        return Long.valueOf(number, 2);

    }

    /**
     * 十进制转换为十六进制字符串
     */

    public static String decToHex(int number) {

        return Long.toHexString(number);

    }

    /**
     * 十六进制字符串转十进制数字
     */

    public static Long hexToDec(String number) {

        return Long.valueOf(number, 16);

    }

    /**
     * 十六进字符串制转二进制字符串
     * <p>
     * 这是单个转
     */

    public static String hexToBin(String number) {

        Integer hex = Integer.valueOf(number, 16);

        return Integer.toBinaryString(hex);

    }

    /**
     * 十六进字符串制转二进制字符串
     * <p>
     * 这是整个转
     *
     * @return 10100110000110000110000110000
     */

    public static String hexToBins(String hexStr) {

        if (hexStr.length() < 1)

            return null;

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < hexStr.length() / 2; i++) {

            String hex = String.valueOf(hexStr.charAt(i * 2)) + hexStr.charAt(i * 2 + 1);

            String binStr = Integer.toBinaryString(Integer.valueOf(hex, 16));

            String str = "";

            int shao = 8 - binStr.length();

            for (int k = 0; k < shao; k++) {

                str = str + "0";

            }

            binStr = str + binStr;

            result.append(binStr);

//            Log.e("----",hex);

//            Log.e("----",binStr);

        }

        return result.toString();

    }

    /**
     * 原码转反码
     *
     * @param number
     * @return
     */

    public static String binOriginalToBack(String number) {

        if (number.startsWith("0")) {

            return number;

        } else {

            StringBuffer sbf = new StringBuffer();

            sbf.append("1");

            String f_str = number.substring(1);

            for (int i = 0; i < f_str.length(); i++) {

                String s_str = String.valueOf(f_str.charAt(i));

                if (s_str.equals("0")) {

                    sbf.append("1");

                } else if (s_str.equals("1")) {

                    sbf.append("0");

                }

            }

            return sbf.toString();

        }

    }

    /**
     * 反码转补码
     *
     * @param
     * @return
     */

    public static String binBackToRepair(String a, String b) {

        if (a.startsWith("0")) {

            return a;

        }

        StringBuilder sb = new StringBuilder();

        int x = 0;

        int y = 0;

        int pre = 0;//进位

        int sum = 0;//存储进位和另两个位的和

        while (a.length() != b.length()) {//将两个二进制的数位数补齐,在短的前面添0

            if (a.length() > b.length()) {

                b = "0" + b;

            } else {

                a = "0" + a;

            }

        }

        for (int i = a.length() - 1; i >= 0; i--) {
            x = a.charAt(i) - '0';
            y = b.charAt(i) - '0';
            //从低位做加法
            sum = x + y + pre;
            if (sum >= 2) {
                pre = 1;//进位
                sb.append(sum - 2);
            } else {
                pre = 0;
                sb.append(sum);
            }
        }
        if (pre == 1) {
            sb.append("1");
        }
        return sb.reverse().toString();//翻转返回

    }

    /**
     * 补码转十进制
     *
     * @param
     * @return
     */
    public String binRepairToDec(String str) {
        String h_number = str.substring(1);
        if (str.startsWith("0")) {
            return "" + Long.valueOf(h_number, 2);
        } else if (str.startsWith("1")) {
            return "-" + Long.valueOf(h_number, 2);
        }
        return null;
    }
}
