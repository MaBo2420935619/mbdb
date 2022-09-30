package com.ibd.database.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 根据主键修改，删除，查询数据，增加数据
 */
public class RandomAccessFileUtils {
//    public static void main(String[] args) {
//        Long dataStart = RandomAccessFileUtils.insert("data//user1//data.mbDB", "0|苻|10|2608187");
//        System.out.println(dataStart);
//        String s = selectByIndex("data//user1//data.mbDB", dataStart);
//        System.out.println(s);
//        String s1 = selectFromAll("data//user1//data.mbDB", "1001", 2);
//        System.out.println(s1);
//        deleteByIndex("data//user1//data.mbDB", dataStart);
//        updateByIndex("data//user1//data.mbDB",dataStart,"1|苻|10|2608185");
//    }

    public static String selectByIndex(String fileName, Long start) {
        RandomAccessFile raf = null;
        int count=0;
        try {
            raf = new RandomAccessFile(fileName, "rw");
            // 记住上一次的偏移量
            raf.seek(start);
            return new String(raf.readLine().getBytes("iso-8859-1"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



    public static Long insert(String filePath,String value){
        RandomAccessFile raf = null;
        long fileLength=-1L;
        try {
            raf = new RandomAccessFile(filePath, "rw");
            fileLength=raf.length();
            raf.seek(fileLength);
            raf.write((value+"\n").getBytes(StandardCharsets.UTF_8));
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileLength;
    }
    public static Long updateByIndex(String fileName, Long start,String value){
        deleteByIndex(fileName,start);
        return insert(fileName, value);
    }
    public static boolean deleteByIndex(String fileName, Long start){
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(fileName, "rw");
            raf.seek(start);
            String line= new String(raf.readLine().getBytes("iso-8859-1"));
            //delete
            String str = line.replaceFirst("0", "1");
            raf.write((str+"\n").getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static String selectFromAll(String fileName, String key,int primaryPosition) {
        RandomAccessFile raf = null;
        int count=0;
        try {
            raf = new RandomAccessFile(fileName, "rw");
            // 记住上一次的偏移量
            long lastPoint = 0;
            String line= new String(raf.readLine().getBytes("iso-8859-1"));
            while (line != null) {
                // 文件当前偏移量
                final long ponit = raf.getFilePointer();
                // 查找要替换的内容
                boolean update=false;
                String[] split = line.split("\\|");
                String s1 = split[primaryPosition + 1];
                if (s1.equals(key)){
                    return line;
                }
                lastPoint = ponit;
                String s = raf.readLine();
                if (s!=null){
                    line= new String(s.getBytes("iso-8859-1"));
                }else {
                    line=null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Long update(String fileName, String key,String definitionAllFilePath,String value) {
        delete(fileName, key, definitionAllFilePath);
        Long insert = insert(fileName, value);
        return insert;
    }
    public static Long delete(String fileName, String key,String definitionAllFilePath) {
        long filePointer=-1L;
        RandomAccessFile raf = null;
        int primaryPosition = getPrimaryPosition(definitionAllFilePath);
        try {
            raf = new RandomAccessFile(fileName, "rw");
            // 记住上一次的偏移量
            long lastPoint = 0;
            String line= new String(raf.readLine().getBytes("iso-8859-1"));
            while (line != null) {
                // 文件当前偏移量
                final long ponit = raf.getFilePointer();
                String[] split = line.split("\\|");
                if (split[primaryPosition+1].equals(key)){
                    //delete
                    String str = line.replaceFirst("0", "1");
                    raf.seek(lastPoint);
                    raf.write(str.getBytes());
                    filePointer=raf.getFilePointer();
                }
                lastPoint = ponit;
                String s = raf.readLine();
                if (s!=null){
                    line= new String(s.getBytes("iso-8859-1"));
                }else {
                    line=null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePointer;
    }

    public static int getPrimaryPosition(String definitionAllFilePath){
        List<String> strings = FileUtils.readFile02(definitionAllFilePath);
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
}
