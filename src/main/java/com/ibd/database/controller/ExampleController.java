package com.ibd.database.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibd.database.service.TableService;
import com.ibd.database.utils.RandUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExampleController {
    private String tableName="person";
    public static void main(String[] args) {
        ExampleController exampleController=new ExampleController();
//        log.info("创建表:");
//        exampleController.createTable();
        log.info("添加数据:");
        exampleController.insert();
        exampleController.select();
        log.info("删除数据:");
        exampleController.delete();
        exampleController.select();
        log.info("修改数据:");
        exampleController.update();
        exampleController.select();
    }
    public void createTable(){
        JSONObject js2 = new JSONObject();
        js2.put("primary","true");
        js2.put("name","id");
        js2.put("type","char");
        js2.put("length","10");
        js2.put("remark","ID");
        JSONObject js = new JSONObject();
        js.put("primary","false");
        js.put("name","userName");
        js.put("type","varchar");
        js.put("length","10");
        js.put("remark","用户姓名");
        JSONObject js1 = new JSONObject();
        js1.put("primary","false");
        js1.put("name","userAge");
        js1.put("type","int");
        js1.put("length","10");
        js1.put("remark","用户年龄");
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(js);
        jsonArray.add(js1);
        jsonArray.add(js2);
        TableService.createTable(jsonArray,tableName);
        JSONArray person = TableService.getTable(tableName);
        log.info("创建的数据库信息为:"+person);
    }

    public void insert(){
        JSONArray jsonArray = new JSONArray();
        for (int i = 3000; i <4000; i++) {
            JSONObject js = new JSONObject();
            int num = RandUtils.num(10, 30);
            String chinese = RandUtils.name();
            js.put("id",i);
            js.put("userName",chinese);
            js.put("userAge",num);
            jsonArray.add(js);
        }
        int user = TableService.insert(jsonArray, tableName);
        System.out.println(user);
    }

    public void delete(){
        TableService.deleteByIndex(tableName, "3565");
    }

    public void update(){
        String key="3565";
        JSONObject js = new JSONObject();
        int num = RandUtils.num(10, 30);
        String chinese = RandUtils.name();
        js.put("id",key);
        js.put("userName",chinese);
        js.put("userAge",num);
        TableService.updateByIndex(tableName, "3565",js);
    }

    public void select(){
        String key="35165";
        String s = TableService.selectByIndex(tableName, "3565");
        log.info("查询道德数据为:"+s);
    }
}
