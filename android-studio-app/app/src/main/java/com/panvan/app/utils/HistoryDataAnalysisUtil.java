package com.panvan.app.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.db.database.cache.CommunicationDataCache;
import com.db.database.daoobject.CommunicationDataDO;
import com.db.database.utils.DataConvertUtils;
import com.panvan.app.data.constants.ChartConstants;
import com.panvan.app.data.enums.HistoryDataTypeEnum;
import com.panvan.app.data.holder.chart.ChartInfo;
import com.panvan.app.data.holder.chart.TemperatureChartInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.N)
public class HistoryDataAnalysisUtil {
    private static final String TAG = HistoryDataAnalysisUtil.class.getSimpleName();

    private static final String ZERO_DATA = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

    private static final DecimalFormat df = new DecimalFormat("#.0");

    public static String[] analysisEveryHourData(byte[] bytes, HistoryDataTypeEnum historyDataTypeEnum, int intervalSeconds, int intervalNum) {
        // 每小时包含的字节数
        int bytesPerHour = 3600 / intervalSeconds * intervalNum;

        String[] dataArr = new String[24 / historyDataTypeEnum.getTotalPacket()];

        if (Objects.isNull(bytes) || bytes.length == 0) {
            return dataArr;
        }

        bytes = DataConvertUtils.subBytes(bytes, 10, bytes.length - 10 - 2);

        switch (historyDataTypeEnum) {
            case STEP:
            case CALORIE:
            case BLOOD_OXYGEN:
                return defaultAnalysis(bytes, bytesPerHour, intervalNum, dataArr);
            case TEMPERATURE:
                return temperatureAnalysis(bytes, bytesPerHour, intervalNum, dataArr);
        }
        return dataArr;
    }

    /**
     * 解析分钟数据
     *
     * @param bytes      bytes
     * @param byteTime   每个byte时间
     * @param packetTime 每包时间
     * @return 结果
     */
    public static String[] analysisMinuteData(byte[] bytes, int byteTime, int packetTime) {
        // 每五分钟的数据包数量
        int packetsPerFiveMinutes = packetTime / byteTime;

        bytes = DataConvertUtils.subBytes(bytes, 10, bytes.length - 10 - 2);

        String[] arr = new String[bytes.length / packetsPerFiveMinutes];

        // 计算每个五分钟段的平均值
        int index = 0;
        for (int i = 0; i < bytes.length; i += packetsPerFiveMinutes) {
            int sum = 0;
            for (int j = i; j < i + packetsPerFiveMinutes; j++) {
                // 将每5秒钟的数据累加起来
                int num = bytes[j] & 0xff;
                if (num == 255) {
                    num = 0;
                }
                sum += num;
            }
            // 求平均值
            int average = sum / packetsPerFiveMinutes;
            arr[index++] = String.valueOf(average);

        }
        return arr;
    }


    /**
     * 默认解析
     *
     * @param bytes
     * @param bytesPerHour
     * @param intervalNum
     * @param dataArr
     * @return
     */
    private static String[] defaultAnalysis(byte[] bytes, int bytesPerHour, int intervalNum, String[] dataArr) {
        for (int i = 0; i < bytes.length; i += bytesPerHour) {
            // 计算当前小时的索引
            int hourIndex = i / bytesPerHour;
            int hourData = 0;

            for (int j = i; j < i + bytesPerHour; j += intervalNum) {
                if (j + intervalNum < bytes.length) {
                    // 合并两个byte
                    int value = (bytes[j + 1] & 0xFF) << 8 | (bytes[j] & 0xFF);
                    if (value == ChartConstants.MAX) {
                        value = 0;
                    }
                    hourData += value;
                }
            }
            // 将每小时的数据存入数组中
            dataArr[hourIndex] = String.valueOf(hourData);
        }

        return dataArr;
    }

    private static String[] temperatureAnalysis(byte[] bytes, int bytesPerHour, int intervalNum, String[] dataArr) {
        List<Double> hourDataList = new ArrayList<>();

        for (int i = 0; i < bytes.length; i += bytesPerHour) {
            // 每小时
            double bodySurfaceSum = 0;
            double environmentSum = 0;
            int size = 0;
            for (int j = i; j < i + bytesPerHour; j += intervalNum) {
                if (j + intervalNum <= bytes.length) {
                    byte[] bytes1 = new byte[2];
                    bytes1[0] = bytes[j];
                    bytes1[1] = bytes[j + 1];
                    // 合并两个byte
                    double bodySurface = ProtocolUtil.byteArrayToInt(bytes1, false);
                    if (bodySurface == 65535) {
                        bodySurface = 0;
                    }

                    byte[] bytes2 = new byte[2];
                    bytes2[0] = bytes[j + 2];
                    bytes2[1] = bytes[j + 3];
                    double environment = ProtocolUtil.byteArrayToInt(bytes2, false);
                    if (environment == 65535) {
                        environment = 0;
                    }

                    if (bodySurface != 0 && environment != 0) {
                        bodySurfaceSum += (bodySurface * 0.005);
                        environmentSum += (environment * 0.005);
                        size++;
                    }
                }
            }

            if (size == 0) {
                hourDataList.add(0.0);
            } else {
                double y = bodySurfaceSum / size;
                double x = environmentSum / size;
                hourDataList.add(0.0337 * y * y - 0.545 * y + 1.7088 * x - 0.0519 * x * y + 17.626);
            }
        }

        for (int i = 0; i < hourDataList.size(); i++) {
            dataArr[i] = df.format(hourDataList.get(i));
        }
        return dataArr;
    }


    /**
     * 基础解析 目前支持步数 卡路里解析
     *
     * @param type 类型
     * @return 结果
     */
    public static List<ChartInfo<String>> getChartDataByType(String type) {
        List<ChartInfo<String>> chartInfoList = new ArrayList<>();
        // 根据类型获取所有数据
        List<CommunicationDataDO> list = CommunicationDataCache.listByType(type);

        for (CommunicationDataDO dataDO : list) {
            String data = dataDO.getData();
            String[] split = data.split(",");
            chartInfoList.add(new ChartInfo<>(dataDO.getDataDate(), new ArrayList<>(Arrays.asList(split))));
        }

        return chartInfoList;
    }


    /**
     * 解析血氧
     */
    public static List<ChartInfo<Integer>> analysisBloodOxygen() {
        List<ChartInfo<Integer>> chartInfoList = new ArrayList<>();
        // 根据类型获取所有数据
        // getCommunicationMap(HistoryDataTypeEnum.BLOOD_OXYGEN).forEach((k, v) -> {
        //     if (v.stream().anyMatch(i -> StringUtils.isBlank(i.getData()))) {
        //         chartInfoList.add(new TemperatureChartInfo<>(k, new ArrayList<>()));
        //         return;
        //     }
        //     chartInfoList.add(analysisBloodOxygenHex(k, handleJoinDataHex(v, HistoryDataTypeEnum.BLOOD_OXYGEN)));
        // });
        return chartInfoList;
    }

    /**
     * 根据类型获取缓存数据
     *
     * @param historyDataTypeEnum {@link HistoryDataTypeEnum}
     * @return map
     */
    private static Map<Integer, List<CommunicationDataDO>> getCommunicationMap(HistoryDataTypeEnum historyDataTypeEnum) {
        // 根据类型获取所有数据
        List<CommunicationDataDO> list = CommunicationDataCache.listByType(historyDataTypeEnum.getKeyDes());
        return list.stream().collect(Collectors.groupingBy(CommunicationDataDO::getDataDate));
    }

    /**
     * 统计每天的数据 每包5分钟
     *
     * @return 每天数据
     */
    public static List<Integer> defaultAnalysis(List<CommunicationDataDO> dataList) {
        List<Integer> hourlyData = new ArrayList<>();

        StringBuilder hex = new StringBuilder();

        for (CommunicationDataDO dataDO : dataList) {
            if (StringUtils.isBlank(dataDO.getData())) {
                hex.append(ZERO_DATA);
            } else {
                hex.append(dataDO.getData());
            }
        }

        byte[] bytes = ProtocolUtil.hexStrToBytes(hex.toString());

        int data = 0;

        for (int i = 0; i < bytes.length; i += 2) {
            byte[] subArray = DataConvertUtil.getSubArray(bytes, i, 2);
            int num = ProtocolUtil.byteArrayToInt(subArray, false);

            if (ChartConstants.MAX == num) {
                num = 0;
            }

            data += num;

            if (i % 24 == 0) {
                hourlyData.add(data);
                data = 0;
            }
        }

        return hourlyData;
    }


    /**
     * 解析温度hex
     *
     * @param date 日期
     * @param hex  hex
     * @return 结果
     */
    private static TemperatureChartInfo<Double> analysisTemperatureHex(Integer date, String hex) {
        byte[] bytes = ProtocolUtil.hexStrToBytes(hex);

        List<Double> bodySurface = new ArrayList<>();
        List<Double> environment = new ArrayList<>();

        for (int i = 0; i < bytes.length; i += 2) {
            byte[] subArray = DataConvertUtil.getSubArray(bytes, i, 2);
            double temperature = ProtocolUtil.byteArrayToInt(subArray, false);
            temperature = temperature == ChartConstants.MAX ? ChartConstants.MAX : ((temperature * 0.005 - 32) * 5 / 9);

            if (i % 4 == 0) {
                bodySurface.add(temperature);
            } else {
                environment.add(temperature);
            }
        }

        List<Double> resList = new ArrayList<>();
        for (int i = 0; i < bodySurface.size(); i++) {
            double x = environment.get(i);
            double y = bodySurface.get(i);

            if (x == ChartConstants.MAX || y == ChartConstants.MAX) {
                resList.add(0.0);
            } else {
                resList.add(0.0337 * y * y - 0.545 * y + 1.7088 * x - 0.0519 * x * y + 17.626);
            }
        }

        List<Double> hourList = new ArrayList<>();
        List<Double> hourDetails = new ArrayList<>();
        for (int i = 0; i < resList.size(); i++) {
            Double v = resList.get(i);
            if (v != 0) {
                hourDetails.add(v);
            }
            if ((i + 1) % 12 == 0) {
                if (CollectionUtils.isEmpty(hourDetails)) {
                    hourList.add(0.0);
                } else {
                    double sumValue = hourDetails.stream().mapToDouble(a -> a).sum();

                    hourList.add(Double.parseDouble(df.format(sumValue / hourDetails.size())));
                    hourDetails = new ArrayList<>();
                }
            }
        }

        TemperatureChartInfo<Double> temperatureChartInfo = new TemperatureChartInfo<>(date, hourList);

        List<Double> list = hourList.stream().filter(e -> e != 0).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(list)) {
            DoubleSummaryStatistics summaryStatistics = hourList.stream().filter(e -> e != 0).collect(Collectors.summarizingDouble(e -> e));
            temperatureChartInfo.getExtendedInfo().setAve(Double.parseDouble(df.format(summaryStatistics.getAverage())));
            temperatureChartInfo.getExtendedInfo().setMax(Double.parseDouble(df.format(summaryStatistics.getMax())));
            temperatureChartInfo.getExtendedInfo().setMin(Double.parseDouble(df.format(summaryStatistics.getMin())));
        }

        return temperatureChartInfo;
    }

    private static ChartInfo<Integer> analysisBloodOxygenHex(Integer date, String hex) {
        byte[] bytes = ProtocolUtil.hexStrToBytes(hex);

        Map<Integer, List<Integer>> hourlyStatistics = new HashMap<>();
        int intervalMinutes = 5;


        for (int i = 0; i < bytes.length; i++) {
            // Calculate the corresponding hour
            int hour = i * intervalMinutes / 60;
            List<Integer> orDefault = hourlyStatistics.getOrDefault(hour, new ArrayList<>());
            int unsignedInt = Byte.toUnsignedInt(bytes[i]);
            if (unsignedInt == 255) {
                unsignedInt = 0;
            }

            if (unsignedInt != 0) {
                if (CollectionUtils.isEmpty(orDefault)) {
                    orDefault = new ArrayList<>();
                }
                orDefault.add(unsignedInt);
            }

            hourlyStatistics.put(hour, orDefault);
        }

        return new ChartInfo<>(date, hourlyStatistics.values().stream().map(integers -> {
            if (CollectionUtils.isNotEmpty(integers)) {
                int sum = integers.stream().mapToInt(Integer::intValue).sum();
                return sum / integers.size();
            } else {
                return 0;
            }
        }).collect(Collectors.toList()));
    }


    public static String[] analysisEveryTenMinuteData(byte[] bytes, HistoryDataTypeEnum historyDataTypeEnum, int intervalSeconds, int intervalBitNum) {
        return new String[1];
    }
}
