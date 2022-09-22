package com.ibd.database.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static void saveAsFileWriter(String filePath, String content) {
        FileWriter fwriter = null;
        try {
            // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
            fwriter = new FileWriter(filePath, true);
            fwriter.write(content+"\r\n");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * 读取一个文本 一行一行读取
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static List<String> readFile02(String path){
        // 使用一个字符串集合来存储文本中的路径 ，也可用String []数组
        List<String> list = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(path);
            // 防止路径乱码   如果utf-8 乱码  改GBK     eclipse里创建的txt  用UTF-8，在电脑上自己创建的txt  用GBK
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line = "";
            while ((line = br.readLine()) != null) {
                // 如果 t x t文件里的路径 不包含---字符串       这里是对里面的内容进行一个筛选
                if (line.lastIndexOf("---") < 0) {
                    list.add(line);
                }
            }
            br.close();
            isr.close();
            fis.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
//    public static void main(String[] args) {
//        TxtFileUtils.saveAsFileWriter("G://a.txt","123");
//        TxtFileUtils.saveAsFileWriter("G://a.txt","456");
//    }
}
