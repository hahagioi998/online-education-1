package com.atguigu.eduservice.excel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class TestEasyExcel {
    public static void main(String[] args) {
        //实现excel写的操作
        //1.设置写入的文件夹地址和excel文件名称
        //String filename = "E:\\write.xlsx";

        //2.调用easyExcel里面的方法实现写操作
        //参数1：文件路径名称，参数2：实体类.class
        //EasyExcel.write(filename,DemoData.class).sheet("学生列表").doWrite(getData());

        //实现excel读操作
        String filename = "E:\\write.xlsx";
        EasyExcel.read(filename,DemoData.class,new ExcelListener()).sheet().doRead();
    }

    //创建方法返回List集合
    public static List<DemoData> getData(){
        List<DemoData> demoDataList = new ArrayList<>();
        for (int i=0; i<10; i++){
            DemoData demoData = new DemoData();
            demoData.setSno(i);
            demoData.setSname("lucy"+i);
            demoDataList.add(demoData);
        }
        return demoDataList;
    }
}
