package com.wuming.util.excel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 读取（97-2003格式）
 *
 * @author wuming
 * Created on 2017/8/22 19:35
 */
public class PoiExcel2k3Helper extends PoiExcelHelper {

    /**
     * 获取sheet列表
     */
    public List<String> getSheetList(String filePath) {
        List<String> sheetList = new ArrayList<>(0);
        try {
            return getSheetList(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return sheetList;
    }

    public List<String> getSheetList(File file) {
        List<String> sheetList = new ArrayList<>(0);
        try {
            return getSheetList(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return sheetList;
    }

    public List<String> getSheetList(InputStream inputStream) {
        List<String> sheetList = new ArrayList<>(0);
        try {
            HSSFWorkbook wb = new HSSFWorkbook(inputStream);
            int i = 0;
            while (true) {
                try {
                    String name = wb.getSheetName(i);
                    sheetList.add(name);
                    i++;
                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sheetList;
    }

    /**
     * 读取Excel文件内容
     */
    public List<List<String>> readExcel(String filePath, int sheetIndex, String rows, String columns) {
        List<List<String>> dataList = new ArrayList<>();
        try {
            return readExcel(new FileInputStream(filePath), sheetIndex, rows, columns);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public List<List<String>> readExcel(File file, int sheetIndex, String rows, String columns) {
        List<List<String>> dataList = new ArrayList<>();
        try {
            return readExcel(new FileInputStream(file), sheetIndex, rows, columns);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public List<List<String>> readExcel(InputStream inputStream, int sheetIndex, String rows, String columns) {
        List<List<String>> dataList = new ArrayList<>();
        try {
            HSSFWorkbook wb = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = wb.getSheetAt(sheetIndex);
            // 如果最后一行的移行号 == 0，即该页签没有行，空页签，直接返回空列表
            if(sheet.getLastRowNum() == 0){
                return dataList;
            }
            dataList = readExcel(sheet, rows, getColumnNumber(sheet, columns));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * 读取Excel文件内容
     */
    public List<List<String>> readExcel(String filePath, int sheetIndex, String rows, int[] cols) {
        List<List<String>> dataList = new ArrayList<>();
        try {
            return readExcel(new FileInputStream(filePath), sheetIndex, rows, cols);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public List<List<String>> readExcel(File file, int sheetIndex, String rows, int[] cols) {
        List<List<String>> dataList = new ArrayList<>();
        try {
            return readExcel(new FileInputStream(file), sheetIndex, rows, cols);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public List<List<String>> readExcel(InputStream inputStream, int sheetIndex, String rows, int[] cols) {
        List<List<String>> dataList = new ArrayList<>();
        try {
            HSSFWorkbook wb = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = wb.getSheetAt(sheetIndex);
            dataList = readExcel(sheet, rows, cols);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }

}
