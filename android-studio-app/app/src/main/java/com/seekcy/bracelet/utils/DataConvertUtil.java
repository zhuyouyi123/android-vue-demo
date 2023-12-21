package com.seekcy.bracelet.utils;

import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.db.database.daoobject.CommunicationDataDO;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DataConvertUtil {
    private static final DecimalFormat df = new DecimalFormat("#0.0");
    private static final DecimalFormat DF_INT = new DecimalFormat("#");

    public static byte[] mergeBytes(byte[] array1, byte[] array2) {
        byte[] mergedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, mergedArray, 0, array1.length);
        System.arraycopy(array2, 0, mergedArray, array1.length, array2.length);
        return mergedArray;
    }

    public static byte[] getSubArray(byte[] array, int startIndex, int length) {
        if (startIndex < 0 || startIndex >= array.length || length <= 0) {
            throw new IllegalArgumentException("Invalid start index or length.");
        }

        int endIndex = startIndex + length;
        if (endIndex > array.length) {
            throw new IllegalArgumentException("Invalid start index or length.");
        }

        return Arrays.copyOfRange(array, startIndex, endIndex);
    }

    public static boolean checkSum(byte[] bytes) {
        byte[] analysisByStartBytes = ProtocolUtil.copyBytes(bytes, 0, bytes.length - 2);
        byte calcAddSum = ProtocolUtil.calcAddSum(analysisByStartBytes);

        return calcAddSum == bytes[bytes.length - 2];
    }

    public static int getListStringMax(List<String> list) {
        List<Integer> integers = convertStringToInt(list, false);
        return Collections.max(integers);
    }

    public static String getDoubleListMax(List<Double> list, boolean needDouble) {
        if (needDouble) {
            return roundedDoubleToString(Collections.max(list));
        }
        return String.valueOf(Collections.max(list).intValue());

    }


    public static String getListStringMax(List<String> list, boolean needDouble) {
        if (needDouble) {
            List<Double> doubles = convertStringToDouble(list, false);
            return String.valueOf(Collections.max(doubles));
        }
        List<Integer> integers = convertStringToInt(list, false);
        return String.valueOf(Collections.max(integers));
    }

    public static int getMax(List<Integer> list) {
        return Collections.max(list);
    }


    public static int getListStringMin(List<String> list) {
        List<Integer> integers = convertStringToInt(list, true);
        return Collections.min(integers);
    }

    public static String getDoubleListMin(List<Double> list, boolean needDouble) {
        if (needDouble) {
            return roundedDoubleToString(Collections.min(removeDoubleListZero(list)));
        }
        return String.valueOf(Collections.min(removeDoubleListZero(list)).intValue());
    }

    public static String getListStringMin(List<String> list, boolean needDouble) {
        if (needDouble) {
            List<Double> doubles = convertStringToDouble(list, true);
            return String.valueOf(Collections.min(doubles));
        }
        List<Integer> integers = convertStringToInt(list, true);
        return String.valueOf(Collections.min(integers));
    }

    public static int getMin(List<Integer> list) {
        List<Integer> integers = new ArrayList<>();
        for (Integer integer : list) {
            if (integer == 0) {
                continue;
            }
            integers.add(integer);
        }

        if (CollectionUtils.isEmpty(integers)) {
            return 0;
        }

        return Collections.min(integers);
    }

    public static List<Integer> convertStringToInt(List<String> list, boolean removeZero) {
        List<Integer> integers = new ArrayList<>();
        for (String s : list) {
            int i = Integer.parseInt(s);
            if (removeZero && i == 0) {
                continue;
            }
            integers.add(i);
        }

        if (CollectionUtils.isEmpty(integers)) {
            return Collections.singletonList(0);
        }

        return integers;
    }

    public static List<Double> convertStringToDouble(List<String> list, boolean removeZero) {
        List<Double> integers = new ArrayList<>();
        for (String s : list) {
            double i = Double.parseDouble(s);
            if (removeZero && i == 0) {
                continue;
            }
            integers.add(i);
        }

        if (CollectionUtils.isEmpty(integers)) {
            return Collections.singletonList(0.0);
        }

        return integers;
    }

    public static List<Double> removeDoubleListZero(List<Double> list) {
        List<Double> integers = new ArrayList<>();
        for (Double s : list) {
            if (s == 0) {
                continue;
            }
            integers.add(s);
        }

        return integers;
    }

    public static int calcBloodPressureAverageToInt(List<Integer> list) {
        return Double.valueOf(calcBloodPressureAverage(list)).intValue();
    }

    /**
     * 计算平均 去除0
     *
     * @param list
     * @return
     */
    public static double calcBloodPressureAverage(List<Integer> list) {
        List<Integer> res = new ArrayList<>();
        for (Integer integer : list) {
            if (0 != integer) {
                res.add(integer);
            }
        }

        if (CollectionUtils.isEmpty(res)) {
            return 0;
        }

        int sum = 0;
        for (int num : res) {
            sum += num;
        }

        return roundedDouble((double) sum / res.size());
    }

    public static double calcListDoubleAverage(List<Double> list) {
        List<Double> res = new ArrayList<>();
        for (Double integer : list) {
            if (0 != integer) {
                res.add(integer);
            }
        }

        if (CollectionUtils.isEmpty(res)) {
            return 0;
        }

        int sum = 0;
        for (double num : res) {
            sum += num;
        }

        return roundedDouble((double) sum / res.size());
    }

    public static double roundedDouble(double d) {
        // 创建 DecimalFormat 对象，指定数字格式为 "#0.0"
        // 调用 setRoundingMode 方法设置四舍五入模式
        df.setRoundingMode(RoundingMode.HALF_UP);
        // 调用 format 方法格式化数字并返回结果
        return Double.parseDouble(df.format(d));
    }

    public static int roundedDoubleToInt(double d) {
        DF_INT.setRoundingMode(RoundingMode.HALF_UP);
        // 调用 format 方法格式化数字并返回结果
        return Integer.parseInt(DF_INT.format(d));
    }

    public static String roundedDoubleToString(double d) {
        // 调用 setRoundingMode 方法设置四舍五入模式
        df.setRoundingMode(RoundingMode.HALF_UP);
        // 调用 format 方法格式化数字并返回结果
        return df.format(d);
    }

    public static double calcDataAverage(String data) {
        String[] split = data.split(",");
        List<String> list = new ArrayList<>(Arrays.asList(split));
        return Double.parseDouble(calcDoubleStringAverage(list, true));
    }

    /**
     * 计算平均 去除0
     *
     * @param list
     * @return
     */
    public static double calcStringAverage(List<String> list) {
        List<Integer> integers = new ArrayList<>();
        for (String integer : list) {
            integers.add(Integer.parseInt(integer));
        }

        return calcBloodPressureAverage(integers);
    }

    /**
     * 计算平均 去除0
     *
     * @param list
     * @return
     */
    public static String calcStringAverage(List<String> list, boolean needDouble) {
        List<Double> integers = new ArrayList<>();
        for (String integer : list) {
            integers.add(Double.parseDouble(integer));
        }
        if (needDouble) {
            return String.valueOf(calcListDoubleAverage(integers));
        }

        return String.valueOf(Double.valueOf(calcListDoubleAverage(integers)).intValue());
    }

    public static String calcDoubleStringAverage(List<String> list, boolean needDouble) {
        List<Double> integers = new ArrayList<>();
        for (String integer : list) {
            if (integer.contains(".0")) {
                integers.add(0.0);
                continue;
            }
            integers.add(Double.parseDouble(integer));
        }
        if (needDouble) {
            return String.valueOf(calcListDoubleAverage(integers));
        }

        return String.valueOf(Double.valueOf(calcListDoubleAverage(integers)).intValue());
    }

    public static int calcStringAverageAndToIOnt(List<String> list) {
        double v = calcStringAverage(list);
        return Double.valueOf(v).intValue();
    }

    public static String calculateIntAverage(List<Double> list) {
        if (list == null || list.isEmpty()) {
            return "0";
        }

        int sum = 0;
        for (double num : list) {
            sum += num;
        }

        return String.valueOf(sum / list.size());
    }

    public static String calculateDoubleAverage(List<Double> list) {
        if (list == null || list.isEmpty()) {
            return "0";
        }

        int sum = 0;
        for (double num : list) {
            sum += num;
        }

        return String.valueOf((double) sum / list.size());
    }

    /**
     * 计算连续达标次数
     *
     * @param list 数据列表
     * @return 达标数量
     */
    public static int countConsecutiveOccurrences(List<Boolean> list) {
        int count = 0;
        int consecutiveCount = 0;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i)) {
                consecutiveCount++;
            } else {
                consecutiveCount = 0;
            }

            if (consecutiveCount > count) {
                count = consecutiveCount;
            }
        }

        return count;
    }

    public static int countOccurrencesInRange(List<Boolean> list) {
        int count = 0;
        for (Boolean b : list) {
            if (b) {
                count++;
            }
        }
        return count;
    }

    public static List<Integer> getHeartRateStringToxIntList(CommunicationDataDO communicationDataDO) {
        if (Objects.isNull(communicationDataDO)) {
            List<Integer> list = new ArrayList<>();
            for (int i = 0; i < 288; i++) {
                list.add(0);
            }
            return list;
        }
        String[] split = communicationDataDO.getData().split(",");
        return convertStringToInt(Arrays.asList(split), false);
    }

    /**
     * 计算最大值
     *
     * @param doList
     * @return
     */
    public static String calcDOMaxValue(List<CommunicationDataDO> doList, boolean isMax, boolean needDouble) {
        if (CollectionUtils.isNotEmpty(doList)) {
            List<String> list = new ArrayList<>();

            for (CommunicationDataDO communicationDataDO : doList) {
                String[] split = communicationDataDO.getData().split(",");
                list.addAll(Arrays.asList(split));
            }
            if (isMax) {
                return getListStringMax(list, needDouble);
            }

            return getListStringMin(list, needDouble);
        }
        return "0";
    }
}
