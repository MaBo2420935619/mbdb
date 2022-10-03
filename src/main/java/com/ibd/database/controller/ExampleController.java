package com.ibd.database.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
        SQLService service=new SQLService();
//        service.update("update person set  name=第二,age=121321 where id=2");
//        service.inset("insert into person (id,name,age) values (1234,123,132)");
//        service.delete("delete from person where id=1234");
        service.select("select * from person where id=123");
    }
}
