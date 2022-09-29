package com.ibd.database.tree;

public class KeyAndValue implements Comparable<KeyAndValue>{
    /*存储索引关键字*/
    private int key;
    /*存储数据*/
    private Object value;

    @Override
    public int compareTo(KeyAndValue o) {
        //根据key的值升序排列
        return this.key - o.key;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    KeyAndValue(int key, Object value) {
        this.key = key;
        this.value = value;
    }
}
