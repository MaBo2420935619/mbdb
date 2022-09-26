package com.ibd.database.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                list.add(line);
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

    /**
     * @param file 文件路径
     * @param startLine 第n行开始读，Java 下标为 0
     * @param limit 每次读取的行数
     * @return
     */
    public  static List<String> readFileToLineGoLine(String file , int startLine, int limit) throws IOException {
        Path path = Paths.get(file);
        //读取文件
        Stream<String> linesAll = Files.lines(path);
        List<String> collect = linesAll.skip(startLine)
                .limit(limit)
                .collect(Collectors.toList());
        return collect;
    }

}
