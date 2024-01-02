package com.hjj.homieMatching.once;

import com.alibaba.excel.EasyExcel;

import java.util.List;

/**
 * 导入excel数据
 */
public class ImportExcel {
    /**
     * 读取数据
     */
    public static void main(String[] args) {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName = "E:\\Java星球项目\\homieMatching\\homieMatching\\src\\main\\resources\\alarm.csv";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        readByListener(fileName);
        synchronousRead(fileName);
    }

    /**
     * 监听器读取数据，一条一条处理数据，适合数据量很多的场景
     * @param fileName
     */
    public static void readByListener(String fileName) {
        EasyExcel.read(fileName, PlanetUserInfo.class, new TableListener()).sheet().doRead();
    }

    /**
     * 同步读取数据，一次读取全部数据，不适合数据量很多的场景，可能会导致内存溢出
     * @param fileName
     */
    public static void synchronousRead(String fileName){
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<PlanetUserInfo> totalDataList = EasyExcel.read(fileName).head(PlanetUserInfo.class).sheet().doReadSync();
        for (PlanetUserInfo planetUserInfo : totalDataList) {
            System.out.println(planetUserInfo.getID());
        }
    }
}
