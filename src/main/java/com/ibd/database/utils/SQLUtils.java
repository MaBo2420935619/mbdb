package com.ibd.database.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibd.database.service.DefinitionService;
import com.ibd.database.service.TableService;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SQLUtils {
    public static String subString(String str, String strStart, String strEnd) {
        /* 找出指定的2个字符在 该字符串里面的 位置 */
        int strStartIndex = str.indexOf(strStart);
        int strEndIndex = str.indexOf(strEnd);
        // 如果俩个字符相同，结束字符为第二个
        if (strStart.equalsIgnoreCase(strEnd)) {
            int fromIndex = getFromIndex(str,strEnd,2);
            strEndIndex = fromIndex;
        }

        /* index 为负数 即表示该字符串中 没有该字符 */
        if (strStartIndex < 0) {
            return "字符串 :---->" + str + "<---- 中不存在 " + strStart + ", 无法截取目标字符串";
        }
        if (strEndIndex < 0) {
            return "字符串 :---->" + str + "<---- 中不存在 " + strEnd + ", 无法截取目标字符串";
        }
        /* 开始截取 */
        String result = str.substring(strStartIndex, strEndIndex).substring(strStart.length());
        return result;
    }

    //子字符串modelStr在字符串str中第count次出现时的下标
    private static int getFromIndex(String str, String modelStr, Integer count) {
        //对子字符串进行匹配
        Matcher slashMatcher = Pattern.compile(modelStr).matcher(str);
        int index = 0;
        //matcher.find();尝试查找与该模式匹配的输入序列的下一个子序列
        while(slashMatcher.find()) {
            index++;
            //当modelStr字符第count次出现的位置
            if(index == count){
                break;
            }
        }
        //matcher.start();返回以前匹配的初始索引。
        return slashMatcher.start();
    }
}
