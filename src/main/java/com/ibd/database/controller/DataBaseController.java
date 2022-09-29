package com.ibd.database.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibd.database.service.DefinitionService;
import com.ibd.database.utils.RandUtils;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class DataBaseController {
    /**
     * 测试50万条数据
     */
    public static void createData(){

        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < 500000; i++) {
            JSONObject js = new JSONObject();
            int num = RandUtils.num(10, 30);
            String chinese = RandUtils.getChinese();
            js.put("id",i);
            js.put("userName",chinese);
            js.put("userAge",num);
            jsonArray.add(js);
        }
        int user = DefinitionService.insert(jsonArray, "user1");
        System.out.println(user);
    }

    public static void main(String[] args) throws IOException {
//        createData();
        System.out.println(new Date());
        String key = DefinitionService.selectByPrimaryKey("user1", "11057");
        System.out.println(key);
        System.out.println(new Date());
        String user1 = DefinitionService.selectByPrimaryByAll("user1", "11057");
        System.out.println(user1);
        System.out.println(new Date());
    }
}
