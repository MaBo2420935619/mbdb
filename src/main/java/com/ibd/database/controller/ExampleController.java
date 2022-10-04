package com.ibd.database.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibd.database.service.DefinitionService;
import com.ibd.database.service.SQLService;
import com.ibd.database.service.TableService;
import com.ibd.database.utils.RandUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExampleController {
    /**
     * 可以基于增删改查操作进行封装，实现madbClient
     * @param args
     */
    public static void main(String[] args) {
        createTable();
        SQLService service=new SQLService();
//        service.inset("insert into user (id,name,age) values (123,123,132)");
//        service.delete("delete from user where id=123");
//        service.inset("insert into user (id,name,age) values (456,123,132)");
//        service.update("update user set  name=第二,age=121321 where id=456");
        service.select("select * from user where id=456");
    }

    public static void createTable(){
        JSONObject js2 = new JSONObject();
        js2.put("primary","true");
        js2.put("name","id");
        js2.put("type","char");
        js2.put("length","10");
        js2.put("remark","ID");
        JSONObject js = new JSONObject();
        js.put("primary","false");
        js.put("name","name");
        js.put("type","varchar");
        js.put("length","10");
        js.put("remark","用户姓名");
        JSONObject js1 = new JSONObject();
        js1.put("primary","false");
        js1.put("name","age");
        js1.put("type","int");
        js1.put("length","10");
        js1.put("remark","用户年龄");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(js);
        jsonArray.add(js1);
        jsonArray.add(js2);
        TableService.createTable(jsonArray,"user");
        JSONArray user = TableService.getTable("user");
        System.out.println(user);
    }
}
