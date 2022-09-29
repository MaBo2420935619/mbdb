package com.ibd.database.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibd.database.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DefinitionService {

    private static String filePath="data//";

    private static String definitionFileName ="//tableDefinition.mbDB";

    private static String dataFileName ="//data.mbDB";

    private static String indexFileName ="//index.mbDB";

//    public static void main(String[] args) {
//        JSONObject js2 = new JSONObject();
//        js2.put("primary","false");
//        js2.put("name","id");
//        js2.put("type","char");
//        js2.put("length","10");
//        js2.put("remark","ID");
//        JSONObject js = new JSONObject();
//        js.put("primary","false");
//        js.put("name","userName");
//        js.put("type","varchar");
//        js.put("length","10");
//        js.put("remark","用户姓名");
//        JSONObject js1 = new JSONObject();
//        js1.put("primary","false");
//        js1.put("name","userAge");
//        js1.put("type","int");
//        js1.put("length","10");
//        js1.put("remark","用户年龄");
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(js);
//        jsonArray.add(js1);
//        jsonArray.add(js2);
//        DefinitionService.createTable(jsonArray,"user1");
//        JSONArray user = DefinitionService.getTable("user1");
//        System.out.println(user);
//    }
    public static void createTable(JSONArray array, String tableName){
        JSONArray table = new JSONArray();
        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            jsonObject.put("delete","1");
            table.add(jsonObject);
        }
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
        FileUtils.saveAsFileWriter(file.getAbsolutePath(),table.toJSONString(),true);
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
    public static String getPrimary(String tableName){
        List<String> strings = FileUtils.readFile02(filePath+tableName + definitionFileName);
        JSONArray jsonArray = JSONArray.parseArray(strings.get(0));
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Boolean primary = jsonObject.getBoolean("primary");
            if (primary){
                return  jsonObject.getString("name");
            }
        }
        return null;
    }
    public static int getPrimaryPosition(String tableName){
        List<String> strings = FileUtils.readFile02(filePath+tableName + definitionFileName);
        JSONArray jsonArray = JSONArray.parseArray(strings.get(0));
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Boolean primary = jsonObject.getBoolean("primary");
            if (primary){
                return  i;
            }
        }
        return -1;
    }
//    public static void main(String[] args) {
//        JSONObject js = new JSONObject();
//        js.put("id","1006");
//        js.put("userName","张三");
//        js.put("userAge","10");
//        JSONObject js1 = new JSONObject();
//        js1.put("id","1003");
//        js1.put("userName","王五");
//        js1.put("userAge","12");
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.add(js);
//        jsonArray.add(js1);
//        int user = DefinitionService.insert(jsonArray, "user1");
//        System.out.println(user);
//    }
    public static int insert(JSONArray jsonArray,String tableName){
        List<String> collNames = getCollNames(tableName);
        File file = new File(filePath+tableName + dataFileName);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                String data="delete";
                for (int j = 0; j < collNames.size()    ; j++) {
                    String s = collNames.get(j);
                    data+="|"+s;
                }
                FileUtils.saveAsFileWriter(file.getAbsolutePath(),data,true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String data="0";
            for (int j = 0; j < collNames.size()    ; j++) {
                String s = collNames.get(j);
                String string = jsonObject.getString(s);
                if (data.equals("")){
                    data=string;
                }else {
                    data+="|"+string;
                }
            }
            FileUtils.saveAsFileWriter(file.getAbsolutePath(),data,true);
            if (i%10000==0){
                System.out.println("当前为"+i);
            }

        }
        createIndexByPrimary(tableName);
        return jsonArray.size();
    }


    public static int delete(String  tableName, String key){
        int i = FileUtils.deleteData(filePath + tableName + dataFileName, key);
        createIndexByPrimary(tableName);
        return i;
    }
    //    public static void main(String[] args) {
//        JSONObject js = new JSONObject();
//        js.put("delete","0");
//        js.put("userName","李四");
//        js.put("userAge","10");
//        int user = update("user","张三", js);
//        System.out.println(user);
//    }
    public static int update(String  tableName, String key,JSONObject data){
        int update = FileUtils.deleteData(filePath + tableName + dataFileName, key);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(data);
        insert(jsonArray,tableName);
        createIndexByPrimary(tableName);
        return update;
    }

    public static List<String> selectAll(String tableName){
        List<String> strings = FileUtils.readFile02(filePath + tableName+ dataFileName);
        List<String> objects = new ArrayList<>();
        for (int i = 1; i < strings.size(); i++) {
            String s = strings.get(i);
            if (s.startsWith("0")){
                objects.add(s);
            }
        }
        return objects;
    }

    public static List<String> selectValueByLike(String tableName, String value){
        return FileUtils.getValueByLike(filePath + tableName+ dataFileName,value);
    }
    public static JSONObject  getIndex(String tableName){
        List<String> strings = FileUtils.readFile02(filePath + tableName+ indexFileName);
        String s = strings.get(0);
        return JSONObject.parseObject(s);
    }



    public static String selectByPrimaryByAll(String tableName,String value) throws IOException {
        String line="";
        int i=0;
        do{
            List<String> strings1 = FileUtils.readFileToLineGoLine(filePath + tableName + dataFileName, i, 1);
            String s = strings1.get(0);
            int primaryPosition = getPrimaryPosition(tableName);
            String[] split = s.split("\\|");
            if (split[primaryPosition+1].equals(value)){
                return s;
            }
            i++;
            if (i%10000==0){
                System.out.println("当前为"+i);
            }
        }while (line.equals(""));
        return null;
    }

    public static void main(String[] args) {
        createIndexByPrimary("user1");
    }

    public static void createIndexByPrimary(String tableName) {
        int primaryPosition = getPrimaryPosition(tableName);
        String primary = getPrimary(tableName);
        List<String> strings =selectAll("user1");
        //按照字符串的长度升序排序
        Collections.sort(strings, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                String[] split1 = s1.split("\\|");
                String ss1 = split1[primaryPosition+1];
                String[] split2 = s2.split("\\|");
                String ss2 = split2[primaryPosition+1];
                int integer = Integer.parseInt(ss1);
                int integer2 = Integer.parseInt(ss2);
                return integer-integer2;
            }
        });
        List<String> strings1 = FileUtils.readFileToLineGoLine(filePath + tableName + dataFileName, 0, 1);
        FileUtils.saveAsFileWriter(filePath+tableName+dataFileName,strings1.get(0),false);
        int count=0;
        for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i);
            if (s.startsWith("0")){
                count++;
                FileUtils.saveAsFileWriter(filePath+tableName+dataFileName,s,true);
            }
            if (i%10000==0){
                System.out.println("排序数据，当前为"+i);
            }
        }
        String indexFile = filePath + tableName + "//" + primary + indexFileName;
        //写入索引
        FileUtils.saveAsFileWriter(indexFile,"key:position:"+ count,false);
        count=0;
        for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i);
            if (s.startsWith("0")){
                String[] split = s.split("\\|");
                String key = split[primaryPosition+1];
                FileUtils.saveAsFileWriter(indexFile,key+":"+count,true);
                count++;
            }
            if (i%10000==0){
                System.out.println("写入索引,当前为"+i);
            }
        }
    }

    public static String selectByPrimaryIndexKey(String tableName, String key) throws IOException {
        //优化为二分法查找
        String primary = getPrimary(tableName);
        String indexFile = filePath + tableName + "//" + primary + indexFileName;
        List<String> list = FileUtils.readFileToLineGoLine(indexFile, 0, 1);
        String[] split = list.get(0).split(":");
        int max = Integer.parseInt(split[2]);
        int  i = binarySearch(1, max, key, indexFile);
        if (i==-1){
            return null;
        }
        String s = FileUtils.readLine(filePath + tableName + dataFileName, i);
        return s;
    }

    public static String selectByPrimaryKey(String tableName, String key) throws IOException {
       return  FileUtils.selectDataByPrimary(filePath + tableName + dataFileName, key, getPrimaryPosition(tableName));
    }
    /**
     * 二分法查找
     * @param min
     * @param max
     * @param key
     * @param filePath
     * @return
     */
    public static int binarySearch(int min,int max,String key,String filePath){
        int middle = (max + min) / 2;
        if (max>min){
            int minIndex = getLineIndex(filePath, min);
            int maxIndex = getLineIndex(filePath, max);
            int keyIndex = Integer.parseInt(key);
            if (keyIndex==minIndex){
                return min;
            }else if (keyIndex==maxIndex){
                return max;
            }
            if (keyIndex<minIndex||keyIndex>maxIndex){
                return -1;
            }
            else {

                int middleIndex = getLineIndex(filePath, middle);
                if (middleIndex>keyIndex){
                    return binarySearch(min,middle,key,filePath);
                }else  if (middleIndex<keyIndex){
                    return binarySearch(middle,max,key,filePath);
                }
                else {
                    return middle;
                }
            }
        }
        else {
            int minIndex = getLineIndex(filePath, min);
            int maxIndex = getLineIndex(filePath, max);
            int keyIndex = Integer.parseInt(key);
            if (keyIndex==minIndex){
                return min;
            }
            if (keyIndex==maxIndex){
                return max;
            }
        }
        return -1;

    }

    public static int  getLineIndex(String filePath,int line){
        String s = FileUtils.readLine(filePath, line);
        String[] split = s.split(":");
        String s1 = split[0];
        int i = Integer.parseInt(s1);
        return i;
    }
}
