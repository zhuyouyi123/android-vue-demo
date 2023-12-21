package com.panvan.app;

import com.panvan.app.annotation.AppAutowired;
import com.panvan.app.annotation.AppController;
import com.panvan.app.annotation.AppRequestMapper;
import com.panvan.app.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.Deflater;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import dalvik.system.DexFile;

public class CoreEventHander {
    private static boolean isInitHander = false;
    private static Map<String, Class> classNameList = null;
    private static final Map<String, Method> methodMap = new HashMap<>();
    private static final Map<String, String> classMap = new HashMap<>();
    private static Context mContext;

    /**
     * 执行反射方法
     *
     * @param action
     * @param jsonParam
     * @param _mContext
     * @return
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Object executeMethodOfController(String action, String jsonParam, Context _mContext) throws Exception {
        if (jsonParam.equals("undefined")) {
            jsonParam = "";
        }
        mContext = _mContext;
        init();
        Method method = methodMap.get(action);
        if (method == null) {
            throw new Exception("请求" + action + "无效，请确保类添加@AppController,方法添加@AppRequestMapper注解。确保请求方法类型一致且注解中的path和请求地址一致！");
        }
        String className = classMap.get(action);
        Class clzss = classNameList.get(className);
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
        for (Field field : fields) {
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
    public static void init() {
        if (isInitHander) {
            return;
        }

        classNameList = classNameList != null ? classNameList : getControllerClassList();
        for (String key : classNameList.keySet()) {
            Class clzss = classNameList.get(key);

            AppController appController = (AppController) clzss.getAnnotation(AppController.class);
            String path = appController.path();
            Method[] methods = clzss.getMethods();
            for (Method _methods : methods) {
                AppRequestMapper appRequestMapper = _methods.getAnnotation(AppRequestMapper.class);
                if (appRequestMapper == null) {
                    continue;
                }
                String _key = appRequestMapper.method() + ":" + path + appRequestMapper.path();
                //获取map
                methodMap.put(_key, _methods);
                classMap.put(_key, clzss.getName());
            }
        }

        isInitHander = true;

    }


    /**
     * 获取参数
     *
     * @param method
     * @param json
     * @return
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Object[] getParam(Method method, String json) throws Exception {
        Parameter[] parameters = method.getParameters();
        if (json == null || (json + "").equals("")) {
            if (parameters.length > 0) {
                return new Object[]{false};
            }
            return null;
        }
        Object[] object = new Object[method.getParameters().length];
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        if (jsonObject != null) {
            int i = 0;
            for (Parameter parameter : parameters) {
                getParamObject(jsonObject, parameter, object, i, parameters.length);
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
            for (Parameter parameter : parameters) {
                getParamObject(jsonArray, parameter, object, i, parameters.length);
                i++;
            }
        }
        if (jsonObject == null && jsonArray == null && parameters.length >= 1) {

            String split = parameters[0].getType().getTypeName() != Integer.class.getTypeName() ? "\"" : "";
            String str = "{\"" + parameters[0].getName() + "\":" + split + json + split + "}";
            JSONObject obj = new JSONObject(str);
            getParamObject(obj, parameters[0], object, 0, 1);
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getParamObject(JSONArray json, Parameter parameter, Object[] objects, int index, int len) throws Exception {
        try {
            Object val = len == 1 ? json : null;
            Class<?> clzss = parameter.getType();
            getValue(val, clzss, objects, index, parameter);
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getParamObject(JSONObject json, Parameter parameter, Object[] objects, int index, int len) throws Exception {
        try {
            String name = parameter.getName();
            Object val = !json.has(name) && len == 1 ? json : name.equals("arg" + index) ? json.get(json.names().get(index).toString()) : json.get(name);
            Class<?> clzss = parameter.getType();
            getValue(val, clzss, objects, index, parameter);
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getValue(Object val, Class<?> clzss, Object[] objects, int index, Parameter parameter) throws Exception {
        if (val == null) {
            return;
        }
        if (clzss == String.class) {
            objects[index] = val.toString();
            return;
        }
        if (clzss == Boolean.class || clzss == boolean.class) {
            objects[index] = Boolean.parseBoolean(val.toString());
            return;
        }
        if (clzss == Integer.class || clzss == int.class) {
            objects[index] = Integer.parseInt(val.toString());
            return;
        }
        if (clzss == Double.class || clzss == double.class) {
            objects[index] = Double.parseDouble(val.toString());
            return;
        }
        if (clzss == Float.class || clzss == float.class) {
            objects[index] = Float.parseFloat(val.toString());
            return;
        }
        if (clzss == Long.class || clzss == long.class) {
            if (StringUtils.isBlank(val.toString())) {
                objects[index] = null;
            } else {
                objects[index] = Long.parseLong(val.toString());
            }
            return;
        }
        if (clzss == Byte.class || clzss == byte.class) {
            objects[index] = Byte.parseByte(val.toString());
            return;
        }
        if (clzss == Short.class || clzss == short.class) {
            objects[index] = Short.parseShort(val.toString());
            return;
        }
        if (clzss == Character.class || clzss == char.class) {
            objects[index] = (char) val;
            return;
        }

        if (clzss == Date.class) {
            try {
                objects[index] = Config.parseDate(val.toString(), 0);
            } catch (ParseException e) {
                e.printStackTrace();
//                throw new Exception("将" + val.toString() + "转换成日期时失败！");
            }
            return;
        }

        if (clzss.isArray()) {
            //尝试转换数据为数组
            Class<?> _type = clzss.getComponentType();
            JSONArray jsonArray = new JSONArray(val.toString());
            Object[] newVarargArray = new Object[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                getValue(jsonArray.get(i), _type, newVarargArray, i, parameter);
            }
            Object newVarargArray2 = Array.newInstance(_type, jsonArray.length());
            System.arraycopy(newVarargArray, 0, newVarargArray2, 0, jsonArray.length());
            objects[index] = newVarargArray2;
        }
        //实体类
        if (clzss.getName().contains(Config.basePackages)) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(val.toString());
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
                Object _val = jsonObject.has(fieldName) ? jsonObject.get(fieldName) : null;
                if (_val == null) {
                    continue;
                }
                // 序列化字段除外
                if (fieldName.equals("serialVersionUID")) {
                    continue;
                }
                Class<?> typeClzss = clzss.getDeclaredField(fieldName).getType();
                f.setAccessible(true);
                Object[] obj = new Object[1];
                getValue(_val, typeClzss, obj, 0, parameter);
                if (obj != null && obj.length > 0 && obj[0] != null) {
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
            JSONArray jsonArray = new JSONArray(val.toString());
            if (jsonArray == null || jsonArray.length() <= 0) {
                objects[index] = null;
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ArrayList arrayList = new ArrayList();
                Object[] obj = new Object[1];
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        Type type = ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0];
                        getValue(jsonArray.get(i), Class.forName(type.getTypeName()), obj, 0, parameter);
                        if (obj != null && obj.length > 0 && obj[0] != null) {
                            arrayList.add(obj[0]);
                        }
                    } catch (Exception e) {
                    }
                }
                objects[index] = arrayList;
            }
        }

    }


    /**
     * 获取控制器 反射调用只设计 支持controller包下的类
     *
     * @return
     */
    public static Map<String, Class> getControllerClassList() {
        Map<String, Class> classNameList = new HashMap<>();
        try {
            DexFile df = new DexFile(CoreEventHander.mContext.getPackageCodePath());
            Enumeration<String> enumeration = df.entries();
            while (enumeration.hasMoreElements()) {
                String className = enumeration.nextElement();
                if (className.contains(Config.basePackages + ".controller") && !className.contains("$")) {
                    classNameList.put(className, Class.forName(className));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classNameList;
    }


}
