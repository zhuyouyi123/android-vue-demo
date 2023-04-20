package com.seek.config.vue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.seek.config.Config;
import com.seek.config.annotation.AppAutowired;
import com.seek.config.annotation.AppController;
import com.seek.config.annotation.AppRequestMapper;
import com.seek.config.entity.constants.Constants;
import com.seek.config.utils.DateFormatUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dalvik.system.DexFile;

/**
 * @author zhuyouyi
 */
public class CoreEventHandlerOld {
    private static boolean isInitHandler = false;
    private static Map<String, Class> classNameMap = null;
    private static final Map<String, Method> METHOD_MAP = new HashMap<>();
    private static final Map<String, String> CLASS_MAP = new HashMap<>();

    private static final String NULL = "null";

    private static final String BLACK = "";

    /**
     * 执行反射方法
     *
     * @param action
     * @param jsonParam
     * @param _mContext
     * @return
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static Object executeMethodOfController(String action, String jsonParam, Context _mContext) throws Exception {
        if ("undefined".equals(jsonParam)) {
            jsonParam = "";
        }
        init();
        Method method = METHOD_MAP.get(action);
        if (method == null) {
            throw new Exception("请求" + action + "无效，请确保类添加@AppController,方法添加@AppRequestMapper注解。确保请求方法类型一致且注解中的path和请求地址一致！");
        }

        String className = CLASS_MAP.get(action);
        Class clzss = classNameMap.get(className);
        Object[] obj = getParam(method, jsonParam);
        Object classObject = clzss.newInstance();
        setFiledByAutowired(classObject, clzss);
        if (obj == null) {
            return method.invoke(classObject);
        }
        return method.invoke(classObject, obj);
    }

    /**
     * 添加 @AppAutowired注解类的自动实例化支持
     *
     * @param object
     * @param clzss
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void setFiledByAutowired(Object object, Class<?> clzss) throws InstantiationException, IllegalAccessException {
        Field[] fields = clzss.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.isAnnotationPresent(AppAutowired.class)) {
                //动态注入
                field.setAccessible(true);
                Class<?> _fieldClzs = field.getType();
                field.set(object, _fieldClzs.newInstance());
            }
        }
    }

    /**
     * 初始化 ，获取控制器，注入自动注解的类
     */
    public static void init() throws IOException {
        if (isInitHandler) {
            return;
        }

        classNameMap = classNameMap != null ? classNameMap : getControllerClassList();
        // classNameMap = getControllerClassList();
        for (String key : classNameMap.keySet()) {
            Class clzss = classNameMap.get(key);

            AppController appController = (AppController) clzss.getAnnotation(AppController.class);
            String path = appController.path();
            Method[] methods = clzss.getMethods();
            for (int m = 0; m < methods.length; m++) {
                Method _methods = methods[m];
                AppRequestMapper appRequestMapper = _methods.getAnnotation(AppRequestMapper.class);
                if (appRequestMapper == null) {
                    continue;
                }
                String _key = appRequestMapper.method() + ":" + path + appRequestMapper.path();
                //获取map
                METHOD_MAP.put(_key, _methods);
                CLASS_MAP.put(_key, clzss.getName());
            }
        }

        isInitHandler = true;

    }


    /**
     * 获取参数
     *
     * @param method
     * @param json
     * @return
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static Object[] getParam(Method method, String json) throws Exception {
        if (json == null || "".equals(json + "")) {
            return null;
        }

        Class<?>[] parameterTypes = method.getParameterTypes();
//        Parameter[] parameters = method.getParameters();
        Object[] object = new Object[parameterTypes.length];
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        if (jsonObject != null) {
            int i = 0;
//            for (Parameter parameter : parameters) {
//                getParamObject(jsonObject, parameter, object, i, parameters.length);
//                i++;
//            }
            for (Class<?> parameterType : parameterTypes) {
                getParamObject(jsonObject, parameterType, object, i, parameterTypes.length, method);
                i++;
            }
            return object;
        }
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        if (jsonArray != null) {
            int i = 0;
            for (Class<?> parameterType : parameterTypes) {
                getParamObject(jsonArray, parameterType, object, i, parameterTypes.length, method);
                i++;
            }
//            for (Parameter parameter : parameters) {
//                getParamObject(jsonArray, parameter, object, i, parameters.length);
//                i++;
//            }

        }
        if (jsonArray == null && parameterTypes.length >= 1) {
            System.out.println(111);
//            String split = !parameters[0].getType().getTypeName().equals(Integer.class.getTypeName()) ? "\"" : "";
//            String str = "{\"" + parameters[0].getName() + "\":" + split + json + split + "}";
//            JSONObject obj = new JSONObject(str);
//            getParamObject(obj, parameters[0], object, 0, 1);
        }
        return object;

    }

    /**
     * 根据传入参数获取参数 Array
     *
     * @param json
     * @param parameter
     * @param objects
     * @param index
     * @param len
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void getParamObject(JSONArray json, Class<?> parameter, Object[] objects, int index, int len, Method method) throws Exception {
        try {
            Object val = len == 1 ? json : null;
            Class<?> clzss = parameter;
            getValue(val, clzss, objects, index, parameter, method);
            //获取方法参数数组
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 根据传入参数获取参数 Object
     *
     * @param json
     * @param parameter
     * @param objects
     * @param index
     * @param len
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void getParamObject(JSONObject json, Class<?> parameter, Object[] objects, int index, int len, Method method) throws Exception {
        try {
            String name = parameter.getName();
            Class<?> clzss = parameter;
            Object val;
            if (clzss == List.class || clzss == ArrayList.class) {
                JSONArray names = json.names();
                if (null != names && names.length() > 0 && Constants.NULL.equals(json.get(names.get(0).toString()))) {
                    val = new Gson().toJson(Collections.emptyList());
                } else {
                    if (null != names && names.length() > 0) {
                        val = json.get(names.get(index).toString());
                    } else {
                        return;
                    }
                }
            } else {
                val = !json.has(name) && len == 1 ? json : name.equals("arg" + index) ? json.get(json.names().get(index).toString()) : json.get(name);
            }
            getValue(val, clzss, objects, index, parameter, method);
            //获取方法参数数组
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 计算并进行参数类型转换
     *
     * @param val
     * @param clzss
     * @param objects
     * @param index
     * @param parameter
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static void getValue(Object val, Class<?> clzss, Object[] objects, int index, Class<?> parameter, Method method) throws Exception {
        if (val == null) {
            return;
        }
        String varStr = val.toString();
        if (clzss == String.class) {
            objects[index] = varStr;
            return;
        }
        if (clzss == Boolean.class || clzss == boolean.class) {
            objects[index] = Boolean.parseBoolean(varStr);
            return;
        }
        if (clzss == Integer.class) {
            if (NULL.equals(varStr) || BLACK.equals(varStr)) {
                objects[index] = null;
                return;
            }
            objects[index] = Integer.parseInt(varStr);
            return;
        }
        if (clzss == int.class) {
            if (NULL.equals(varStr) || BLACK.equals(varStr)) {
                objects[index] = 0;
                return;
            }
            objects[index] = Integer.parseInt(varStr);
            return;
        }
        if (clzss == Double.class) {
            if (NULL.equals(varStr) || BLACK.equals(varStr)) {
                objects[index] = null;
                return;
            }
            objects[index] = Double.parseDouble(varStr);
            return;
        }
        if (clzss == double.class || BLACK.equals(varStr)) {
            if (NULL.equals(varStr)) {
                objects[index] = 0.0;
                return;
            }
            objects[index] = Double.parseDouble(varStr);
            return;
        }
        if (clzss == Float.class || clzss == float.class) {
            objects[index] = Float.parseFloat(varStr);
            return;
        }
        if (clzss == Long.class || clzss == long.class) {
            objects[index] = Long.parseLong(varStr);
            return;
        }
        if (clzss == Byte.class || clzss == byte.class) {
            objects[index] = Byte.parseByte(varStr);
            return;
        }
        if (clzss == Short.class || clzss == short.class) {
            objects[index] = Short.parseShort(varStr);
            return;
        }
        if (clzss == Character.class || clzss == char.class) {
            objects[index] = (char) val;
            return;
        }

        if (clzss == Date.class) {
            try {
                objects[index] = DateFormatUtil.parseDate(varStr, 0);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return;
        }

        if (clzss.isArray()) {
            //尝试转换数据为数组
            Class<?> _type = clzss.getComponentType();
            JSONArray jsonArray = new JSONArray(varStr);
            Object[] newVarargArray = new Object[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                getValue(jsonArray.get(i), _type, newVarargArray, i, parameter, method);
            }
            Object newVarargArray2 = Array.newInstance(_type, jsonArray.length());
            System.arraycopy(newVarargArray, 0, newVarargArray2, 0, jsonArray.length());
            objects[index] = newVarargArray2;
        }
        //实体类
        if (clzss.getName().contains(Config.basePackages)) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(varStr);
            } catch (Exception e) {

            }
            if (jsonObject == null) {
                objects[index] = null;
                return;
            }
            //实例化对象
            // 获取对象的字段，Field
            Field[] fields = clzss.getDeclaredFields();
            Object objClass = clzss.newInstance();
            for (Field f : fields) {

                // 字段名
                String fieldName = f.getName();
                Object o = jsonObject.isNull(fieldName) || !jsonObject.has(fieldName) ? null : jsonObject.get(fieldName);
                if (o == null) {
                    continue;
                }
                // 序列化字段除外
                if ("serialVersionUID".equals(fieldName)) {
                    continue;
                }
                Class<?> typeClzss = clzss.getDeclaredField(fieldName).getType();
                f.setAccessible(true);
                Object[] obj = new Object[1];
                getValue(o, typeClzss, obj, 0, parameter, method);
                if (obj[0] != null) {
                    try {
                        f.set(objClass, obj[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            objects[index] = objClass;
        }
        if (clzss == List.class || clzss == ArrayList.class) {
            JSONArray jsonArray = new JSONArray(varStr);
            if (jsonArray.length() <= 0) {
                objects[index] = null;
                return;
            }
            //            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ArrayList arrayList = new ArrayList();
            Object[] obj = new Object[1];
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
//                    Type type = ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0];
                    if (method.getGenericParameterTypes().length == 0) {
                        continue;
                    }
                    String type = method.getGenericParameterTypes()[0].toString().replaceAll("java.util.List<", "").replaceAll(">", "");
                    getValue(jsonArray.get(i), Class.forName(type), obj, 0, parameter, method);
                    if (obj[0] != null) {
                        arrayList.add(obj[0]);
                    }
                } catch (Exception ignored) {
                    Log.e("getValue", Objects.requireNonNull(ignored.getMessage()));
                }
            }
            objects[index] = arrayList;
        }

    }


    /**
     * 获取控制器 反射调用只设计 支持controller包下的类
     *
     * @return
     */
    public static Map<String, Class> getControllerClassList() throws IOException {
        Map<String, Class> classNameList = new HashMap<>();
        DexFile df = null;
        try {
            df = new DexFile(Config.mainContext.getPackageCodePath());
            Enumeration<String> enumeration = df.entries();
            while (enumeration.hasMoreElements()) {
                String className = enumeration.nextElement();
                if (className.contains("$")) {
                    continue;
                }
                if (className.contains(Config.basePackages + ".controller")) {
                    classNameList.put(className, Class.forName(className));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != df) {
                df.close();
            }
        }
        return classNameList;
    }


}
