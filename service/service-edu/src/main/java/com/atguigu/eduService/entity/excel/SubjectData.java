package com.atguigu.eduService.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SubjectData {
    //设置表头名称
    @ExcelProperty(index = 0)
    private String oneObjectName;

    //设置表头名称
    @ExcelProperty(index = 1)
    private String twoObjectName;
}
