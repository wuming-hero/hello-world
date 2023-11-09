package com.wuming.tool.easyexcel;

import com.alibaba.excel.EasyExcelFactory;
import com.wuming.model.UserExtData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 借助easyexcel 将表格数据，转换为指定的数据格式后输出
 *
 * @author manji
 * Created on 2023/10/26 16:55
 */
public class ReadExcel {

    public static void main(String[] args) throws FileNotFoundException {

        String file = "/Users/manji/Downloads/test2.xlsx";
        InputStream content = new FileInputStream(file);
        EasyExcelFactory.read(content, UserExtData.class, new UserExtExcelListener()).sheet().doRead();
    }

}
