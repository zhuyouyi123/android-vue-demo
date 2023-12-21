package com.seekcy.bracelet.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ble.blescansdk.ble.utils.CollectionUtils;
import com.ble.blescansdk.ble.utils.ProtocolUtil;
import com.ble.blescansdk.ble.utils.SharePreferenceUtil;
import com.db.database.cache.CommunicationDataCache;
import com.db.database.daoobject.AllDayDataDO;
import com.db.database.daoobject.CommunicationDataDO;
import com.db.database.service.AllDayDataService;
import com.db.database.utils.DataConvertUtils;
import com.db.database.utils.DateUtils;
import com.seekcy.bracelet.data.constants.ChartConstants;
import com.seekcy.bracelet.data.constants.SharePreferenceConstants;
import com.seekcy.bracelet.data.enums.HistoryDataTypeEnum;
import com.seekcy.bracelet.data.holder.DeviceHolder;
import com.seekcy.bracelet.data.holder.chart.ChartInfo;
import com.seekcy.bracelet.data.holder.chart.MonthChartInfo;
import com.seekcy.bracelet.data.holder.chart.MultipleChartInfo;
import com.seekcy.bracelet.data.holder.chart.MultipleChartInfoVO;
import com.seekcy.bracelet.data.holder.chart.SingleChartInfoVO;
import com.seekcy.bracelet.data.holder.chart.WeekChartInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.N)
public class HistoryDataAnalysisUtil {

    private static Integer firstUseTime = null;

    private static final Random RANDOM = new Random();

    public static String[] analysisEveryHourData(byte[] bytes, HistoryDataTypeEnum historyDataTypeEnum, int intervalSeconds, int intervalNum) {
        // 每小时包含的字节数
        int bytesPerHour = 3600 / intervalSeconds * intervalNum;

        String[] dataArr;

        if (historyDataTypeEnum == HistoryDataTypeEnum.BLOOD_PRESSURE) {
            dataArr = new String[24 / historyDataTypeEnum.getTotalPacket() * 2];
        } else {
            dataArr = new String[24 / historyDataTypeEnum.getTotalPacket()];
        }

        if (Objects.isNull(bytes) || bytes.length == 0) {
            return dataArr;
        }

        if ((bytes[8] & 0xff) == 0) {
            return dataArr;
        }
        bytes = DataConvertUtils.subBytes(bytes, 10, bytes.length - 10 - 2);


        switch (historyDataTypeEnum) {
            case STEP:
            case CALORIE:
                return defaultAnalysis(bytes, bytesPerHour, intervalNum, dataArr);
            case BLOOD_OXYGEN:
                return bloodOxygenAnalysis(bytes, bytesPerHour, intervalNum, dataArr);
            case TEMPERATURE:
                return temperatureAnalysis(bytes, bytesPerHour, intervalNum, dataArr);
            case BLOOD_PRESSURE:
                return bloodPressureAnalysis(bytes, bytesPerHour, intervalNum, dataArr);
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
    public static String[] analysisMinuteData(byte[] bytes, int byteTime, int packetTime, int minValue) {
        // 每五分钟的数据包数量
        int packetsPerFiveMinutes = packetTime / byteTime;

        bytes = DataConvertUtils.subBytes(bytes, 10, bytes.length - 10 - 2);

        String[] arr = new String[bytes.length / packetsPerFiveMinutes];

        if (arr.length > 200) {
            return arr;
        }

        // 计算每个五分钟段的平均值
        int index = 0;
        try {
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
                if (average != 0 && average < minValue) {
                    average = minValue + RANDOM.nextInt(10);
                }
                arr[index++] = String.valueOf(average);

            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error(ProtocolUtil.byteArrToHexStr(bytes));
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

    /**
     * 解析血压
     *
     * @return
     */
    private static String[] bloodPressureAnalysis(byte[] bytes, int bytesPerHour, int intervalNum, String[] strings) {
        for (int i = 0; i < bytes.length; i += bytesPerHour) {
            // 计算当前小时的索引
            int hourIndex = i / bytesPerHour;
            hourIndex = hourIndex * 2;


            List<Integer> lowList = new ArrayList<>();
            List<Integer> highList = new ArrayList<>();
            for (int j = i; j < i + bytesPerHour; j += intervalNum) {
                if (j + intervalNum < bytes.length) {
                    // 0 正常 1 高压过高， 2 高压过低 3 低压过高， 4 低压过低 5 无效数据
                    int value0 = bytes[j + 2] & 0xFF;

                    int value1 = bytes[j + 1] & 0xFF;
                    int value2 = bytes[j] & 0xFF;

                    if (value1 == 255 || value2 == 255) {
                        value1 = value2 = 0;
                    }

                    lowList.add(value1);
                    highList.add(value2);
                }
            }
            // 将每小时的数据存入数组中
            strings[hourIndex] = String.valueOf(Double.valueOf(DataConvertUtil.calcBloodPressureAverage(lowList)).intValue());
            strings[hourIndex + 1] = String.valueOf(Double.valueOf(DataConvertUtil.calcBloodPressureAverage(highList)).intValue());
        }

        return strings;
    }

    private static String[] bloodOxygenAnalysis(byte[] bytes, int bytesPerHour, int intervalNum, String[] dataArr) {
        for (int i = 0; i < bytes.length; i += bytesPerHour) {
            // 计算当前小时的索引
            int hourIndex = i / bytesPerHour;

            List<Integer> list = new ArrayList<>();
            for (int j = i; j < i + bytesPerHour; j += intervalNum) {
                if (j + intervalNum < bytes.length) {
                    int value = bytes[j] & 0xFF;
                    if (value == ChartConstants.MAX_255) {
                        value = 0;
                    }
                    list.add(value);
                }
            }
            // 将每小时的数据存入数组中
            dataArr[hourIndex] = String.valueOf(DataConvertUtil.calcBloodPressureAverageToInt(list));
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
            Double aDouble = hourDataList.get(i);
            if (aDouble == 0.0) {
                dataArr[i] = "0";
                continue;
            }
            dataArr[i] = DataConvertUtil.roundedDoubleToString(hourDataList.get(i));
        }
        return dataArr;
    }

    public static Object getChartData(String type, Integer dateType, Integer index) {
        if (Objects.isNull(dateType)) {
            dateType = 1;
        }
        HistoryDataTypeEnum typeEnum = HistoryDataTypeEnum.getType(type);

        boolean isMu = typeEnum == HistoryDataTypeEnum.BLOOD_PRESSURE;

        boolean isNeedInterval = typeEnum == HistoryDataTypeEnum.HEART_RATE || typeEnum == HistoryDataTypeEnum.TEMPERATURE;

        switch (dateType) {
            case 1:
                if (isMu) {
                    return multipleStatisticalDataByDay(type, index);
                }
                return statisticalDataByDay(type, index);
            case 2:
                if (isMu || isNeedInterval) {
                    return multipleStatisticalDataByWeek(type, index, isMu, isNeedInterval);
                }
                return statisticalDataByWeek(type, index);
            case 3:
                if (isMu || isNeedInterval) {
                    return multipleStatisticalDataByMonth(type, index, isMu, isNeedInterval);
                }
                return statisticalDataByMonth(type, index);
        }
        return null;
    }


    /**
     * 基础解析 目前支持步数 卡路里解析
     *
     * @param type 类型
     * @return 结果
     */
    public static SingleChartInfoVO<ChartInfo<String>> statisticalDataByDay(String type, Integer index) {
        SingleChartInfoVO<ChartInfo<String>> chartInfoVO = new SingleChartInfoVO<>();
        setFirstUseTime();
        // 设置大小
        int daysBetweenTodayAndFirstUseTime = DateUtil.getDaysBetweenTodayAndFirstUseTime(firstUseTime);
        chartInfoVO.setChartSize(daysBetweenTodayAndFirstUseTime);
        List<ChartInfo<String>> chartInfoList = new ArrayList<>();

        if (-1 == index) {
            index = daysBetweenTodayAndFirstUseTime - 1;
        }
        chartInfoVO.setDataIndex(daysBetweenTodayAndFirstUseTime - index - 1);
        Integer previousIntDate = DateUtils.getPreviousIntDate(daysBetweenTodayAndFirstUseTime - index - 1);


        ChartInfo<String> chartInfo = getChartInfo(previousIntDate, type);
        chartInfoList.add(chartInfo);

        HistoryDataTypeEnum typeEnum = HistoryDataTypeEnum.getType(type);
        boolean needDouble = typeEnum == HistoryDataTypeEnum.TEMPERATURE;

        if (typeEnum == HistoryDataTypeEnum.STEP) {
            DeviceHolder.StepInfo allDayData = getAllDayData(previousIntDate);
            chartInfoVO.setExtendedInfo(allDayData);
        }
        chartInfoVO.setAverage(DataConvertUtil.calcDoubleStringAverage(chartInfo.getHourlyData(), needDouble));
        chartInfoVO.setMax(DataConvertUtil.getListStringMax(chartInfo.getHourlyData(), needDouble));
        chartInfoVO.setMin(DataConvertUtil.getListStringMin(chartInfo.getHourlyData(), needDouble));
        chartInfoVO.setList(chartInfoList);
        return chartInfoVO;
    }

    private static DeviceHolder.StepInfo getAllDayData(Integer previousIntDate) {
        if (DateUtils.isSameDay(previousIntDate)) {
            DeviceHolder.StepInfo stepInfo = DeviceHolder.getInstance().getInfo().getStepInfo();
            double walkingSpeed = 5000; // 步行速度，单位：公里/小时
            double time = stepInfo.getMileage() / walkingSpeed; // 计算时间，单位：小时
            stepInfo.setUseTime(Double.valueOf(time * 60).intValue());
            return stepInfo;
        }

        DeviceHolder.StepInfo stepInfo = new DeviceHolder.StepInfo();
        AllDayDataDO allDayDataDO = CommunicationDataCache.getInstance().getAllDayMap().get(previousIntDate);
        if (null != allDayDataDO) {
            stepInfo.setCalories(allDayDataDO.getCalorie());
            stepInfo.setMileage(allDayDataDO.getMileage());
            stepInfo.setUseTime(allDayDataDO.getActiveTime());
        }
        return stepInfo;
    }

    public static MultipleChartInfoVO<MultipleChartInfo<List<String>>> multipleStatisticalDataByDay(String type, Integer index) {
        MultipleChartInfoVO<MultipleChartInfo<List<String>>> chartInfoVO = new MultipleChartInfoVO<>();
        setFirstUseTime();
        // 设置大小
        int daysBetweenTodayAndFirstUseTime = DateUtil.getDaysBetweenTodayAndFirstUseTime(firstUseTime);
        chartInfoVO.setChartSize(daysBetweenTodayAndFirstUseTime);

        if (-1 == index) {
            index = daysBetweenTodayAndFirstUseTime - 1;
        }
        chartInfoVO.setDataIndex(daysBetweenTodayAndFirstUseTime - index - 1);
        Integer previousIntDate = DateUtils.getPreviousIntDate(daysBetweenTodayAndFirstUseTime - index - 1);

        MultipleChartInfo<List<String>> multipleChartInfo = getMultipleChartInfo(previousIntDate, type, false, false);

        chartInfoVO.setData(multipleChartInfo);
        if (CollectionUtils.isNotEmpty(multipleChartInfo.getDataList())) {
            chartInfoVO.setAverage(DataConvertUtil.calcDoubleStringAverage(multipleChartInfo.getDataList().get(0), false) + "-" + DataConvertUtil.calcDoubleStringAverage(multipleChartInfo.getDataList().get(1), false));
            chartInfoVO.setMax(DataConvertUtil.getListStringMax(multipleChartInfo.getDataList().get(0)) + "-" + DataConvertUtil.getListStringMax(multipleChartInfo.getDataList().get(1)));
            chartInfoVO.setMin(DataConvertUtil.getListStringMin(multipleChartInfo.getDataList().get(0)) + "-" + DataConvertUtil.getListStringMin(multipleChartInfo.getDataList().get(1)));

        }
        return chartInfoVO;
    }

    private static ChartInfo<String> getChartInfo(Integer previousIntDate, String type) {
        CommunicationDataDO communicationDataDO = CommunicationDataCache.get(previousIntDate, type);
        if (null == communicationDataDO) {
            return new ChartInfo<>(previousIntDate, new ArrayList<>());
        } else {
            String data = communicationDataDO.getData();
            String[] split = data.split(",");
            return new ChartInfo<>(previousIntDate, new ArrayList<>(Arrays.asList(split)));
        }
    }

    private static MultipleChartInfo<List<String>> getMultipleChartInfo(Integer previousIntDate, String type, boolean isNeedInterval, boolean isTemp) {
        CommunicationDataDO communicationDataDO = CommunicationDataCache.get(previousIntDate, type);
        if (null == communicationDataDO) {
            return new MultipleChartInfo<>(previousIntDate, new ArrayList<>());
        } else {
            String data = communicationDataDO.getData();
            String[] split = data.split(",");

            if (isNeedInterval) {
                String[] list = new String[split.length];
                for (int i = 0; i < split.length; i++) {
                    if (isTemp) {
                        list[i] = String.valueOf(Double.valueOf(split[i]));
                    } else {
                        list[i] = String.valueOf(Double.valueOf(split[i]).intValue());
                    }
                }
                MultipleChartInfo<List<String>> listMultipleChartInfo = new MultipleChartInfo<>(previousIntDate, Collections.singletonList(Arrays.asList(list)));
                listMultipleChartInfo.setOriginalData(Arrays.asList(split));
                return listMultipleChartInfo;
            }
            int x = 0;
            String[] listA = new String[split.length / 2];
            String[] listB = new String[split.length / 2];
            for (int i = 0; i < split.length; i++) {
                if (i % 2 == 0) {
                    listA[x] = String.valueOf(Double.valueOf(split[i]).intValue());
                } else {
                    listB[x] = String.valueOf(Double.valueOf(split[i]).intValue());
                    x++;
                }
            }
            return new MultipleChartInfo<>(previousIntDate, Arrays.asList(Arrays.asList(listA), Arrays.asList(listB)));
        }
    }

    public static Integer getStepAveByDateType(boolean isWeek, int index) {
        int length;
        Map<Integer, List<Integer>> dates;
        if (isWeek) {
            // 根据第一次时间计算到今天有几周
            length = DateUtil.calculateWeeksToCurrentWeek(String.valueOf(firstUseTime));
            dates = DateUtil.getWeeksDates(length);
        } else {
            // 根据第一次时间计算到今天有几月
            length = DateUtil.calculateMonthsToCurrentDate(String.valueOf(firstUseTime));
            dates = DateUtil.getDatesForMonth(length);
        }

        List<Map.Entry<Integer, List<Integer>>> list = new ArrayList<>(dates.entrySet());

        if (CollectionUtils.isEmpty(list) || list.size() <= index) {
            return 0;
        }

        Map.Entry<Integer, List<Integer>> entry = list.get(index);

        List<Integer> value = entry.getValue();
        // 初始化集合 存放每天数据
        List<Double> dayData = new ArrayList<>();

        for (Integer date : value) {
            CommunicationDataDO communicationDataDO = CommunicationDataCache.get(date, HistoryDataTypeEnum.STEP.getKeyDes());
            if (null == communicationDataDO) {
                dayData.add(0.0);
            } else {
                dayData.add(calcDataSum(communicationDataDO.getData()));
            }
        }

        return Integer.parseInt(DataConvertUtil.calculateIntAverage(dayData));
    }

    public static SingleChartInfoVO<WeekChartInfo<String>> statisticalDataByWeek(String type, Integer index) {
        // 第一次使用时间
        setFirstUseTime();
        SingleChartInfoVO<WeekChartInfo<String>> chartInfoVO = new SingleChartInfoVO<>();
        // 根据第一次时间计算到今天有几周
        int weeks = DateUtil.calculateWeeksToCurrentWeek(String.valueOf(firstUseTime));
        chartInfoVO.setChartSize(weeks);

        Map<Integer, List<Integer>> weeksDates = DateUtil.getWeeksDates(weeks);

        List<WeekChartInfo<String>> weekChartInfoList = new ArrayList<>();

        if (-1 == index) {
            index = weeks - 1;
        }

        List<Map.Entry<Integer, List<Integer>>> list = new ArrayList<>(weeksDates.entrySet());

        chartInfoVO.setDataIndex(weeks - index - 1);
        Map.Entry<Integer, List<Integer>> entry = list.get(weeks - index - 1);

        HistoryDataTypeEnum typeEnum = HistoryDataTypeEnum.getType(type);
        boolean isTemperature = typeEnum == HistoryDataTypeEnum.TEMPERATURE;
        boolean isHeartRate = typeEnum == HistoryDataTypeEnum.HEART_RATE;
        boolean isBloodOxygen = typeEnum == HistoryDataTypeEnum.BLOOD_OXYGEN;

        List<Integer> value = entry.getValue();
        // 初始化集合 存放每天数据
        List<Double> dayData = new ArrayList<>();

        List<CommunicationDataDO> doList = new ArrayList<>();
        List<DeviceHolder.StepInfo> stepInfoList = new ArrayList<>();
        for (Integer date : value) {
            CommunicationDataDO communicationDataDO = CommunicationDataCache.get(date, type);
            if (null == communicationDataDO) {
                dayData.add(0.0);
            } else {
                doList.add(communicationDataDO);
                if (isTemperature || isHeartRate || isBloodOxygen) {
                    dayData.add(DataConvertUtil.calcDataAverage(communicationDataDO.getData()));
                } else {
                    dayData.add(calcDataSum(communicationDataDO.getData()));
                }
            }
            DeviceHolder.StepInfo allDayData = getAllDayData(date);
            if (Objects.nonNull(allDayData)) {
                stepInfoList.add(allDayData);
            }
        }

        if (typeEnum == HistoryDataTypeEnum.STEP) {
            DeviceHolder.StepInfo stepInfo = new DeviceHolder.StepInfo();
            for (DeviceHolder.StepInfo item : stepInfoList) {
                stepInfo.setUseTime(stepInfo.getUseTime() + item.getUseTime());
                stepInfo.setStepNumber(stepInfo.getStepNumber() + item.getStepNumber());
                stepInfo.setCalories(stepInfo.getCalories() + item.getCalories());
                stepInfo.setMileage(stepInfo.getMileage() + item.getMileage());
            }
            chartInfoVO.setExtendedInfo(stepInfo);
        }

        if (isTemperature) {
            chartInfoVO.setAverage(String.valueOf(DataConvertUtil.calcListDoubleAverage(dayData)));
        } else {
            chartInfoVO.setAverage(String.valueOf(DataConvertUtil.roundedDoubleToInt(DataConvertUtil.calcListDoubleAverage(dayData))));
        }

        chartInfoVO.setMax(DataConvertUtil.calcDOMaxValue(doList, true, isTemperature));
        chartInfoVO.setMin(DataConvertUtil.calcDOMaxValue(doList, false, isTemperature));

        List<String> stringList = new ArrayList<>();
        for (Double dayDatum : dayData) {
            if (isTemperature) {
                stringList.add(String.valueOf(dayDatum));
            } else if (isBloodOxygen) {
                int intValue = dayDatum.intValue();
                stringList.add(String.valueOf(Math.min(intValue, 100)));
            } else {
                stringList.add(String.valueOf(dayDatum.intValue()));
            }
        }

        weekChartInfoList.add(new WeekChartInfo<>(stringList));

        chartInfoVO.setList(weekChartInfoList);
        return chartInfoVO;
    }

    public static MultipleChartInfoVO<MultipleChartInfo<List<String>>> multipleStatisticalDataByWeek(String type, Integer index, boolean isBloodPressure, boolean isNeedInterval) {
        // 第一次使用时间
        setFirstUseTime();
        MultipleChartInfoVO<MultipleChartInfo<List<String>>> chartInfoVO = new MultipleChartInfoVO<>();
        chartInfoVO.setNeedSoar(isNeedInterval);
        // 根据第一次时间计算到今天有几周
        int weeks = DateUtil.calculateWeeksToCurrentWeek(String.valueOf(firstUseTime));
        chartInfoVO.setChartSize(weeks);

        Map<Integer, List<Integer>> weeksDates = DateUtil.getWeeksDates(weeks);

        if (-1 == index) {
            index = weeks - 1;
        }

        List<Map.Entry<Integer, List<Integer>>> list = new ArrayList<>(weeksDates.entrySet());

        chartInfoVO.setDataIndex(weeks - index - 1);
        Map.Entry<Integer, List<Integer>> entry = list.get(weeks - index - 1);

        List<Integer> value = entry.getValue();

        List<String> lowList = new ArrayList<>();
        List<String> highList = new ArrayList<>();

        List<String> allList = new ArrayList<>();
        boolean isTemp = HistoryDataTypeEnum.TEMPERATURE == HistoryDataTypeEnum.getType(type);

        for (Integer date : value) {
            MultipleChartInfo<List<String>> multipleChartInfo = getMultipleChartInfo(date, type, isNeedInterval, isTemp);
            List<List<String>> dataList = multipleChartInfo.getDataList();
            if (CollectionUtils.isEmpty(dataList)) {
                lowList.add("0");
                highList.add("0");
                continue;
            }
            if (Objects.nonNull(multipleChartInfo.getOriginalData())) {
                allList.addAll(multipleChartInfo.getOriginalData());
            }

            if (isNeedInterval) {
                lowList.add(DataConvertUtil.getListStringMin(dataList.get(0), isTemp));
                highList.add(DataConvertUtil.getListStringMax(dataList.get(0), isTemp));
            } else {
                lowList.add(DataConvertUtil.calcStringAverage(dataList.get(0), isTemp));
                highList.add(DataConvertUtil.calcStringAverage(dataList.get(1), isTemp));
            }
        }
        MultipleChartInfo<List<String>> multipleChartInfo = new MultipleChartInfo<>(null, Arrays.asList(lowList, highList));
        chartInfoVO.setData(multipleChartInfo);
        if (CollectionUtils.isNotEmpty(multipleChartInfo.getDataList())) {
            if (isBloodPressure) {
                chartInfoVO.setAverage(DataConvertUtil.calcStringAverage(multipleChartInfo.getDataList().get(0), isTemp) + "-" + DataConvertUtil.calcStringAverage(multipleChartInfo.getDataList().get(1), isTemp));
                chartInfoVO.setMax(DataConvertUtil.getListStringMax(multipleChartInfo.getDataList().get(0), isTemp) + "-" + DataConvertUtil.getListStringMax(multipleChartInfo.getDataList().get(1), isTemp));
                chartInfoVO.setMin(DataConvertUtil.getListStringMin(multipleChartInfo.getDataList().get(0), isTemp) + "-" + DataConvertUtil.getListStringMin(multipleChartInfo.getDataList().get(1), isTemp));
            } else {
                chartInfoVO.setAverage(DataConvertUtil.calcStringAverage(allList, isTemp));
                chartInfoVO.setMax(DataConvertUtil.getListStringMax(allList, isTemp));
                chartInfoVO.setMin(DataConvertUtil.getListStringMin(allList, isTemp));
            }
        }
        return chartInfoVO;
    }

    public static SingleChartInfoVO<MonthChartInfo<String>> statisticalDataByMonth(String type, Integer index) {
        SingleChartInfoVO<MonthChartInfo<String>> chartInfoVO = new SingleChartInfoVO<>();
        setFirstUseTime();
        // 根据第一次时间计算到今天有几月
        int months = DateUtil.calculateMonthsToCurrentDate(String.valueOf(firstUseTime));
        chartInfoVO.setChartSize(months);

        Map<Integer, List<Integer>> datesForMonth = DateUtil.getDatesForMonth(months);

        List<MonthChartInfo<String>> monthChartInfoList = new ArrayList<>();

        if (-1 == index) {
            index = months - 1;
        }

        List<Map.Entry<Integer, List<Integer>>> list = new ArrayList<>(datesForMonth.entrySet());

        chartInfoVO.setDataIndex(months - index - 1);
        Map.Entry<Integer, List<Integer>> entry = list.get(index);

        HistoryDataTypeEnum typeEnum = HistoryDataTypeEnum.getType(type);
        boolean isTemperature = typeEnum == HistoryDataTypeEnum.TEMPERATURE;
        boolean isHeartRate = typeEnum == HistoryDataTypeEnum.HEART_RATE;
        boolean isBloodOxygen = typeEnum == HistoryDataTypeEnum.BLOOD_OXYGEN;

        List<Integer> value = entry.getValue();
        // 初始化集合 存放每天数据
        List<Double> dayData = new ArrayList<>();
        List<String> xAxis = new ArrayList<>();
        List<CommunicationDataDO> doList = new ArrayList<>();
        List<DeviceHolder.StepInfo> stepInfoList = new ArrayList<>();

        for (Integer date : value) {
            CommunicationDataDO communicationDataDO = CommunicationDataCache.get(date, type);
            if (null == communicationDataDO) {
                dayData.add(0.0);
            } else {
                doList.add(communicationDataDO);
                if (isTemperature || isHeartRate || isBloodOxygen) {
                    dayData.add(DataConvertUtil.calcDataAverage(communicationDataDO.getData()));
                } else {
                    dayData.add(calcDataSum(communicationDataDO.getData()));
                }
            }
            xAxis.add(DateUtil.convertDate(String.valueOf(date)));
            DeviceHolder.StepInfo allDayData = getAllDayData(date);
            if (Objects.nonNull(allDayData)) {
                stepInfoList.add(allDayData);
            }
        }

        if (typeEnum == HistoryDataTypeEnum.STEP) {
            DeviceHolder.StepInfo stepInfo = new DeviceHolder.StepInfo();
            for (DeviceHolder.StepInfo item : stepInfoList) {
                stepInfo.setUseTime(stepInfo.getUseTime() + item.getUseTime());
                stepInfo.setStepNumber(stepInfo.getStepNumber() + item.getStepNumber());
                stepInfo.setCalories(stepInfo.getCalories() + item.getCalories());
                stepInfo.setMileage(stepInfo.getMileage() + item.getMileage());
            }
            chartInfoVO.setExtendedInfo(stepInfo);
        }

        List<String> stringList = new ArrayList<>();
        for (Double dayDatum : dayData) {
            if (isTemperature) {
                stringList.add(String.valueOf(dayDatum));
            } else if (isBloodOxygen) {
                int intValue = dayDatum.intValue();
                stringList.add(String.valueOf(Math.min(intValue, 100)));
            } else {
                stringList.add(String.valueOf(dayDatum.intValue()));
            }
        }

        monthChartInfoList.add(new MonthChartInfo<>(xAxis, stringList));

        if (isTemperature) {
            chartInfoVO.setAverage(String.valueOf(DataConvertUtil.calcListDoubleAverage(dayData)));
        } else {
            chartInfoVO.setAverage(String.valueOf(DataConvertUtil.roundedDoubleToInt(DataConvertUtil.calcListDoubleAverage(dayData))));
        }
        chartInfoVO.setMax(DataConvertUtil.calcDOMaxValue(doList, true, isTemperature));
        chartInfoVO.setMin(DataConvertUtil.calcDOMaxValue(doList, false, isTemperature));

        chartInfoVO.setList(monthChartInfoList);

        return chartInfoVO;
    }

    public static MultipleChartInfoVO<MultipleChartInfo<List<String>>> multipleStatisticalDataByMonth(String type, Integer index, boolean isBloodPressure, boolean isNeedInterval) {
        // 第一次使用时间
        setFirstUseTime();
        MultipleChartInfoVO<MultipleChartInfo<List<String>>> chartInfoVO = new MultipleChartInfoVO<>();
        chartInfoVO.setNeedSoar(isNeedInterval);
        // 根据第一次时间计算到今天有几月
        int months = DateUtil.calculateMonthsToCurrentDate(String.valueOf(firstUseTime));
        chartInfoVO.setChartSize(months);

        Map<Integer, List<Integer>> datesForMonth = DateUtil.getDatesForMonth(months);

        if (-1 == index) {
            index = months - 1;
        }

        List<Map.Entry<Integer, List<Integer>>> list = new ArrayList<>(datesForMonth.entrySet());

        chartInfoVO.setDataIndex(months - index - 1);
        Map.Entry<Integer, List<Integer>> entry = list.get(index);

        List<Integer> value = entry.getValue();

        List<String> lowList = new ArrayList<>();
        List<String> highList = new ArrayList<>();
        List<String> allList = new ArrayList<>();
        List<String> xAxis = new ArrayList<>();
        boolean isTemp = HistoryDataTypeEnum.TEMPERATURE == HistoryDataTypeEnum.getType(type);

        for (Integer date : value) {
            xAxis.add(DateUtil.convertDate(String.valueOf(date)));
            MultipleChartInfo<List<String>> multipleChartInfo = getMultipleChartInfo(date, type, isNeedInterval, isTemp);
            List<List<String>> dataList = multipleChartInfo.getDataList();
            if (CollectionUtils.isEmpty(dataList)) {
                lowList.add("0");
                highList.add("0");
                continue;
            }
            if (Objects.nonNull(multipleChartInfo.getOriginalData())) {
                allList.addAll(multipleChartInfo.getOriginalData());
            }
            if (isNeedInterval) {
                lowList.add(DataConvertUtil.getListStringMin(dataList.get(0), isTemp));
                highList.add(DataConvertUtil.getListStringMax(dataList.get(0), isTemp));
            } else {
                lowList.add(DataConvertUtil.calcStringAverage(dataList.get(0), isTemp));
                highList.add(DataConvertUtil.calcStringAverage(dataList.get(1), isTemp));
            }
        }
        MultipleChartInfo<List<String>> multipleChartInfo = new MultipleChartInfo<>(null, Arrays.asList(lowList, highList));
        multipleChartInfo.setxAxis(xAxis);
        chartInfoVO.setData(multipleChartInfo);
        if (isBloodPressure) {
            chartInfoVO.setAverage(DataConvertUtil.calcStringAverage(multipleChartInfo.getDataList().get(0), isTemp) + "-" + DataConvertUtil.calcStringAverage(multipleChartInfo.getDataList().get(1), isTemp));
            chartInfoVO.setMax(DataConvertUtil.getListStringMax(multipleChartInfo.getDataList().get(0), isTemp) + "-" + DataConvertUtil.getListStringMax(multipleChartInfo.getDataList().get(1), isTemp));
            chartInfoVO.setMin(DataConvertUtil.getListStringMin(multipleChartInfo.getDataList().get(0), isTemp) + "-" + DataConvertUtil.getListStringMin(multipleChartInfo.getDataList().get(1), isTemp));
        } else {
            chartInfoVO.setAverage(DataConvertUtil.calcStringAverage(allList, isTemp));
            chartInfoVO.setMax(DataConvertUtil.getListStringMax(allList, isTemp));
            chartInfoVO.setMin(DataConvertUtil.getListStringMin(allList, isTemp));
        }
        return chartInfoVO;
    }

    /**
     * 计算和
     *
     * @param data
     * @return
     */
    private static Double calcDataSum(String data) {
        if (StringUtils.isBlank(data)) {
            return 0.0;
        }

        String[] split = data.split(",");

        double sum = 0;

        for (String s : split) {
            if ("null".equals(s) || StringUtils.isBlank(s)) {
                continue;
            }
            sum += Double.parseDouble(s);
        }

        return sum;
    }


    public static String[] analysisEveryTenMinuteData(byte[] bytes, HistoryDataTypeEnum historyDataTypeEnum, int intervalSeconds, int intervalBitNum) {
        return new String[1];
    }

    /**
     * 解析全天数据
     *
     * @param bytes
     * @return
     */
    public static String[] analysisTotalData(byte[] bytes) {
        // 681706000B0C17000000B316
        // 681722000C0C17000101FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFB616
        if (bytes.length < 20) {
            return new String[0];
        }

        try {
            byte[] subBytes = DataConvertUtils.subBytes(bytes, 10, bytes.length - 10 - 2);
            int index = 0;
            // 步数信息
            byte[] stepArray = DataConvertUtils.subBytes(subBytes, index, 4);
            int step = ProtocolUtil.byteArrayToInt(stepArray, false);
            // 卡路里
            byte[] caArray = DataConvertUtils.subBytes(subBytes, index += 4, 4);
            int ca = ProtocolUtil.byteArrayToInt(caArray, false);
            // 里程
            byte[] meArray = DataConvertUtils.subBytes(subBytes, index += 4, 4);
            int me = ProtocolUtil.byteArrayToInt(meArray, false);
            // 活动时间
            byte[] activeTimeArray = DataConvertUtils.subBytes(subBytes, index += 4, 4);
            int activeTime = ProtocolUtil.byteArrayToInt(activeTimeArray, false);
            // 活动消耗
            byte[] activeCaArray = DataConvertUtils.subBytes(subBytes, index + 4, 4);
            int activeCa = ProtocolUtil.byteArrayToInt(caArray, false);

            AllDayDataDO allDayDataDO = new AllDayDataDO();
            allDayDataDO.setStep(step);
            allDayDataDO.setCalorie(ca);
            allDayDataDO.setMileage(me);
            allDayDataDO.setActiveTime(activeTime);
            allDayDataDO.setDateTime(DateUtils.formatDateToInt(bytes[4], bytes[5], bytes[6]));

            // 证明数据都是无效的数据 不保存
            if (step == -1 || ca == -1) {
                return new String[0];
            }

            AllDayDataService.getInstance().save(allDayDataDO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new String[0];
    }

    public static void setFirstUseTime() {
        if (null == firstUseTime) {
            String firstTime = SharePreferenceUtil.getInstance().shareGet(SharePreferenceConstants.FIRST_USE_TIME);
            firstUseTime = Integer.parseInt(firstTime);
        }
    }

}
