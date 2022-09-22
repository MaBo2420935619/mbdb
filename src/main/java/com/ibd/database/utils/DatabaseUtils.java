package com.ibd.database.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibd.database.service.DefinitionService;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils<T> {
    public static void main(String[] args) {
        JSONObject js = new JSONObject();
        js.put("userName","张三");
        js.put("userAge","10");
        JSONObject js1 = new JSONObject();
        js1.put("userName","王五");
        js1.put("userAge","12");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(js);
        jsonArray.add(js1);
        int user = DefinitionService.insert(jsonArray, "user");
        System.out.println(user);
    }
    /**
     * 增
     */
    public static<T> int insert(List<T> list){
        return 0;
    }
    /**
     * 删
     */
    public static int delete(){
        return 0;
    }
    /**
     * 改
     */
    public static int update(){
        return 0;
    }
    /**
     * 查
     */
    public static<T> List<T> select(){
        return new ArrayList<T>();
    }
}
