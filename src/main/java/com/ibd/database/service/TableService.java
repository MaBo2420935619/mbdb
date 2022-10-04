package com.ibd.database.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ibd.database.tree.KeyAndValue;
import com.ibd.database.utils.FileUtils;
import com.ibd.database.utils.RandomAccessFileUtils;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.util.*;
@Slf4j
public class TableService {

    private static String filePath="data//";

    private static String definitionFileName ="//tableDefinition.mbdb";

    private static String dataFileName ="//data.mbdb";

    private static String indexFileName ="//index.mbdb";



    public static int insert(JSONArray jsonArray,String tableName){
        List<String> collNames = getCollNames(tableName);
        String primary = getPrimary(tableName);
        File file = new File(filePath+tableName + dataFileName);
        File fileIndex = new File(filePath+tableName + indexFileName);
        boolean hasIndex=true;
        if (!fileIndex.exists()){
            hasIndex=false;
        }
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                String data="delete";
                for (int j = 0; j < collNames.size(); j++) {
                    String s = collNames.get(j);
                    data+="|"+s;
                }
                FileUtils.saveAsFileWriter(file.getAbsolutePath(),data,true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<KeyAndValue> list=new ArrayList();
        List<String> datas=new ArrayList();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.getString(primary)==null){
                throw new RuntimeException("主键不存在"+jsonObject);
            }
            String data="0";
            for (int j = 0; j < collNames.size(); j++) {
                String s = collNames.get(j);
                String string = jsonObject.getString(s);
                data+="|"+string;
            }
            int key = jsonObject.getInteger(primary);
            if (hasIndex){
                String s = selectByIndex(tableName, String.valueOf(key));
                if (s!=null){
                    throw new RuntimeException("主键重复,请检查，重复的主键为:"+key);
                }
            }
            KeyAndValue keyAndValue = new KeyAndValue(key,"-1");
            list.add(keyAndValue);
            datas.add(data);
        }
        List<KeyAndValue> objects = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            String data = datas.get(i);
            KeyAndValue keyAndValue1 = list.get(i);
            Long insert = RandomAccessFileUtils.insert(file.getAbsolutePath(), data);
            objects.add(new KeyAndValue(keyAndValue1.getKey(),insert));
            if ((i+1)%10000==0){
                log.info("新增数据当前条数为"+i);
            }
        }
        createIndex(tableName,objects);
        return jsonArray.size();
    }

    public static Boolean deleteByIndex(String tableName,String key){
        String s2 = selectByIndex(tableName, key);
        if (s2==null){
            log.info("数据查询失败，无法删除"+key);
            return false;
        }
        Long indexStart = getIndexStart(tableName, key);
        boolean b = RandomAccessFileUtils.deleteByIndex(filePath + tableName + dataFileName, indexStart);
        List<String> list = FileUtils.readFile02(filePath + tableName + indexFileName);
        //重新生成索引
        String s1 = FileUtils.readLine(filePath + tableName + indexFileName, 0);
        String[] split1 = s1.split("\\|");
        int count = Integer.parseInt(split1[2]);
        count=count-1;
        FileUtils.saveAsFileWriter(filePath + tableName +indexFileName,"key|position|"+count,false);
        for (int i = 1; i < list.size(); i++) {
            String s = list.get(i);
            String[] split = s.split("\\|");
            if (!split[0].equals(key)){
                FileUtils.saveAsFileWriter(filePath + tableName +indexFileName,s,true);
            }
        }
        return b;
    }

    public static int updateByIndex(String tableName, String key, JSONObject value){
        Boolean aBoolean = deleteByIndex(tableName, key);
        if (aBoolean){
            JSONArray array = new JSONArray();
            array.add(value);
            int insert = insert(array, tableName);
            return insert;
        }else {
            return 0;
        }
    }

    public static String selectByIndex(String tableName, String key) {
        //优化为二分法查找
        String indexFile = filePath + tableName + indexFileName;
        File file=new File(indexFile);
        if ( !file.exists()){
            throw new RuntimeException("索引文件不存在，查找失败"+indexFile);
        }
        List<String> list = FileUtils.readFileToLineGoLine(indexFile, 0, 1);
        String[] split = list.get(0).split("\\|");
        int max = Integer.parseInt(split[2]);
        if(max==0){
            return null;
        }
        int  i = binarySearch(1, max, key, indexFile);
        if (i==-1){
            return null;
        }
        String s = FileUtils.readLine(filePath + tableName + indexFileName, i);
        String[] split1 = s.split("\\|");
        log.info(key+"指针位置为:"+Long.valueOf(split1[1]));
        String s1 = RandomAccessFileUtils.selectByIndex(filePath + tableName + dataFileName, Long.valueOf(split1[1]));
        return s1;
    }

    public static String selectByPrimaryKey(String tableName, String key){
        return  FileUtils.selectDataByPrimary(filePath + tableName + dataFileName, key, getPrimaryPosition(tableName));
    }


    public static Long getIndexStart(String tableName, String key) {
        //优化为二分法查找
        String indexFile = filePath + tableName + indexFileName;
        List<String> list = FileUtils.readFileToLineGoLine(indexFile, 0, 1);
        String[] split = list.get(0).split("\\|");
        int max = Integer.parseInt(split[2]);
        int  i = binarySearch(1, max, key, indexFile);
        if (i==-1){
            return null;
        }
        String s = FileUtils.readLine(filePath + tableName + indexFileName, i);
        String[] split1 = s.split("\\|");
        return Long.valueOf(split1[1]);
    }
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
        FileUtils.saveAsFileWriter(file.getAbsolutePath(),table.toJSONString().toLowerCase(),true);
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

    public static void createIndex(String  tableName,List<KeyAndValue> index) {
        log.info("开始为表"+tableName+"创建索引");
        String indexFilePath = filePath + tableName + indexFileName;
        File file = new File(indexFilePath);
        JSONObject jsonIndex = new JSONObject();
        for (int i = 0; i < index.size(); i++) {
            KeyAndValue keyAndValue = index.get(i);
            int key = keyAndValue.getKey();
            String position = keyAndValue.getValue().toString();
            jsonIndex.put(String.valueOf(key),position);
        }
        int count=0;
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            List<String> strings = FileUtils.readFile02(indexFilePath);
            for (int i = 1; i < strings.size(); i++) {
                String s = strings.get(i);
                String[] split = s.split("\\|");
                String key =String.valueOf(split[0]);
                String position =split[1];
                String string = jsonIndex.getString(key);
                if (string==null){
                    FileUtils.saveAsFileWriter(file.getAbsolutePath(),key+"|"+position,true);
                    jsonIndex.put(String.valueOf(key),position);
                    KeyAndValue keyAndValue = new KeyAndValue(Integer.parseInt(split[0]),split[1]);
                    index.add(keyAndValue);
                }
                if (i%1000==0){
                    log.info("还需要将"+(strings.size()-i)+"写入索引");
                }
            }
        }
        Collections.sort(index);
        count=jsonIndex.size();
        //索引写入缓存
        FileUtils.saveAsFileWriter(file.getAbsolutePath(),"key|position|"+ count,false);
        for (int i = 0; i < index.size(); i++) {
            KeyAndValue keyAndValue = index.get(i);
            int key = keyAndValue.getKey();
            String position = keyAndValue.getValue().toString();
            FileUtils.saveAsFileWriter(file.getAbsolutePath(),key+"|"+position,true);
        }
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
            int middleIndex = getLineIndex(filePath, middle);
            int keyIndex = Integer.parseInt(key);
            if (keyIndex==minIndex){
                return min;
            }else if (keyIndex==maxIndex){
                return max;
            }
            if (keyIndex<minIndex||keyIndex>maxIndex){
                return -1;
            }
            if (minIndex==middleIndex||maxIndex==middleIndex){
                return -1;
            }
            else {
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
        String[] split = s.split("\\|");
        String s1 = split[0];
        int i = Integer.parseInt(s1);
        return i;
    }
}
