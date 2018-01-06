package com.zhupiter.hspot.Utils;

import android.text.TextUtils;

import java.util.Locale;

/**
 * Created by zhupiter on 17-1-31.
 */

public class DBUtils {
    //得到每一字段的数据类型
    public static String getColumnType(String type){
        String value = null;
        if (type.contains("String")){
            value = " text ";
        }else if (type.contains("int")){
            value = " interger ";
        }else if (type.contains("boolean")){
            value = " boolean ";
        }else if (type.contains("float")){
            value = " float ";
        }else if (type.contains("double")){
            value = " double ";
        }else if (type.contains("char")){
            value = " char ";
        }else if (type.contains("long")){
            value = " long ";
        }
        return value;
    }

    //得到表名
    public static String getTableName(Class<?> clazz){
        return clazz.getSimpleName();
    }

    //将数组的第一个字母大写 eg: class->Class
    public static String capitalize(String string){
        if (!TextUtils.isEmpty(string)){
            return string.substring(0,1).toUpperCase(Locale.US)+string.substring(1);
        }
        return string == null ? null : "";
    }

}
