package com.atguigu.eduservice.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

//创建监听j进行excel文件读取
public class ExcelListener extends AnalysisEventListener {

    //一行一行读取excel内容(默认第二行读取)
    /*@Override
    public void invoke(DemoData data, AnalysisContext analysisContext) {
        System.out.println("****" + data);
    }*/

    @Override
    public void invoke(Object o, AnalysisContext analysisContext) {
        DemoData data = (DemoData) o;
        System.out.println("***" + data);
    }

    //读取表头内容
    @Override
    public void invokeHeadMap(Map headMap, AnalysisContext context) {
        System.out.println("表头：" + headMap);
    }

    //读取完成之后
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
