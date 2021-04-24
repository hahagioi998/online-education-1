package com.atguigu.eduservice.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DemoData {

    @ExcelProperty(value = "学生编号",index = 0)  //设置excel表头名称
    private Integer sno;
    @ExcelProperty(value = "学生姓名",index = 1)
    private String sname;
}
