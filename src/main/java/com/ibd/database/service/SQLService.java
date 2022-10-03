package com.ibd.database.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibd.database.utils.SQLUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SQLService {

    public  void inset(String sql){
        String tableName = null;
        JSONArray jsonArray = null;
        try {
            sql = sql.toLowerCase();
            String[] s1 = sql.split("values");
            String[] split1 = s1[1].split(",\\(");
            tableName = SQLUtils.subString(s1[0], "into", "(");
            tableName=tableName.replace(" ","");
            jsonArray = new JSONArray();
            String[] keys = SQLUtils.subString(sql, tableName, "values").replace("(", "").replace(")", "").replace(" ", "").split(",");
            for (int i = 0; i < split1.length; i++) {
                String s = split1[i];
                String[] values =s.replace("(", "").replace(")", "").replace(" ", "").split(",");
                JSONObject jsonObject = new JSONObject();
                for (int j = 0; j <values.length ; j++) {
                    jsonObject.put(keys[j],values[j]);
                }
                jsonArray.add(jsonObject);
            }
        } catch (Exception e) {
            throw new RuntimeException("sql is error :"+sql);
        }
        TableService.insert(jsonArray,tableName);
    }


    //删除所有分割
    public void delete(String sql) {
        try {
            sql = sql.toLowerCase();
            String tableName = SQLUtils.subString(sql, "from", "where");
            tableName=tableName.replace(" ","");
            String[] s1 = sql.split("where");
            String replace = s1[1].replace(" ", "");
            String[] split = replace.split("\\=");
            TableService.deleteByIndex(tableName,split[1]);
        } catch (Exception e) {
            log.info(e.getMessage(),e);
            throw new RuntimeException("sql is error :"+sql);
        }
    }

    //修改分割
    public void update(String sql) {
        sql = sql.toLowerCase();
        String tableName = SQLUtils.subString(sql, "update", "set");
        tableName=tableName.replace(" ","");
        String[] s1 = sql.split("where");
        String replace = s1[1].replace(" ", "");
        String[] split = replace.split("\\=");
        String s2 = SQLUtils.subString(sql, "set", "where").replace(" ","");
        String[] split1=s2.split(",");
        JSONObject jsonObject = new JSONObject();
        String s = TableService.selectByIndex(tableName, split[1]);
        String[] split3 = s.split("\\|");
        List<String> collNames = DefinitionService.getCollNames(tableName);
        for (int i = 0; i < collNames.size(); i++) {
            jsonObject.put(collNames.get(i),split3[i+1]);
        }
        for (int i = 0; i < split1.length; i++) {
            String s3 = split1[i];
            String[] split2 = s3.split("\\=");
            jsonObject.put(split2[0],split2[1]);
        }
        TableService.updateByIndex(tableName,split[1],jsonObject);
    }

    //查询一条分割
    public String select(String sql) {
        try {
            sql = sql.toLowerCase();
            String tableName = SQLUtils.subString(sql, "from", "where");
            tableName=tableName.replace(" ","");
            String[] s1 = sql.split("where");
            String replace = s1[1].replace(" ", "");
            String[] split = replace.split("\\=");
            String s = TableService.selectByIndex(tableName, split[1]);
            log.info("查询到的数据为: "+s);
            return s;
        } catch (Exception e) {
            throw new RuntimeException("sql is error :"+sql);
        }
    }

}

