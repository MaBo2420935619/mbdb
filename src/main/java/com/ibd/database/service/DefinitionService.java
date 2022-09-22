package com.ibd.database.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibd.database.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DefinitionService {

    private static String filePath="data//";

    private static String definitionFileName ="//tableDefinition.mbDB";

    private static String dataFileName ="//data.mbDB";

    public static void createTable(JSONArray array, String tableName){
        File file = new File(filePath+tableName + definitionFileName);
        file.delete();
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        FileUtils.saveAsFileWriter(file.getAbsolutePath(),array.toJSONString());
    }

    public static JSONArray getTable(String tableName){
        List<String> strings = FileUtils.readFile02(filePath+tableName + definitionFileName);
        return  JSONArray.parseArray(strings.get(0));
    }

    public static List<String> getCollNames(String tableName){
        List<String> strings = FileUtils.readFile02(filePath+tableName + definitionFileName);
        JSONArray jsonArray = JSONArray.parseArray(strings.get(0));
        List<String> collList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            collList.add(jsonObject.getString("name")) ;
        }
        return collList;
    }

    public static int insert(JSONArray jsonArray,String tableName){
        List<String> collNames = getCollNames(tableName);
        File file = new File(filePath+tableName + dataFileName);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                String data="";
                for (int j = 0; j < collNames.size()    ; j++) {
                    String s = collNames.get(j);
                    if (data.equals("")){
                        data=s;
                    }else {
                        data+="|"+s;
                    }
                }
                FileUtils.saveAsFileWriter(file.getAbsolutePath(),data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String data="";
            for (int j = 0; j < collNames.size()    ; j++) {
                String s = collNames.get(j);
                String string = jsonObject.getString(s);
                if (data.equals("")){
                    data=string;
                }else {
                    data+="|"+string;
                }
            }
            FileUtils.saveAsFileWriter(file.getAbsolutePath(),data);
        }
        return jsonArray.size();
    }

    public static void main(String[] args) {
        JSONObject js = new JSONObject();
        js.put("primary","true");
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
        DefinitionService.createTable(jsonArray,"user");
        JSONArray user = DefinitionService.getTable("user");
        System.out.println(user);
    }
}
