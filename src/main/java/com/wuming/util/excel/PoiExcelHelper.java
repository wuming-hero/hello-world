package com.wuming.util.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel统一POI处理类（针对2003以前和2007以后两种格式的兼容处理）
 *
 * @author wuming
 * Created on 2017/8/22 19:32
 */
public abstract class PoiExcelHelper {

    public static final String SEPARATOR = ",";
    public static final String CONNECTOR = "-";

    /**
     * Excel 2003版及以前版本
     */
    public static final String XLS = ".xls";
    /**
     * Excel 2007版及以后版本
     */
    public static final String XLSX = ".xlsx";

    public static PoiExcelHelper create(String fileExt) throws Exception {
        switch (fileExt) {
            case XLS:
                return new PoiExcel2k3Helper();
            case XLSX:
                return new PoiExcel2k7Helper();
            default:
                throw new Exception("不支持的格式:" + fileExt);
        }
    }

    /**
     * 单元格常用样式封装
     *
     * @param workbook
     * @return
     */
    public static Map<String, CellStyle> cellStyleMap(Workbook workbook) {
        CellStyle commonCellStyle = workbook.createCellStyle();
        commonCellStyle.setAlignment(HorizontalAlignment.CENTER);
        commonCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        commonCellStyle.setWrapText(true);

        CellStyle moneyCellStyle = workbook.createCellStyle();
        DataFormat moneyDataFormat = workbook.createDataFormat();
        moneyCellStyle.cloneStyleFrom(commonCellStyle);
        moneyCellStyle.setAlignment(HorizontalAlignment.RIGHT);
        moneyCellStyle.setDataFormat(moneyDataFormat.getFormat("#,##0.00"));

        CellStyle colorCellStyle = workbook.createCellStyle();
        colorCellStyle.cloneStyleFrom(commonCellStyle);
        colorCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        // 指定单元格的填充信息模式和纯色填充单元,设置前景色和背景色时，此项必须指定
        colorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle longTextCellStyle = workbook.createCellStyle();
        longTextCellStyle.cloneStyleFrom(commonCellStyle);
        longTextCellStyle.setAlignment(HorizontalAlignment.LEFT);

        CellStyle colorMoneyCellStyle = workbook.createCellStyle();
        colorMoneyCellStyle.cloneStyleFrom(moneyCellStyle);
        colorMoneyCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        colorMoneyCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        colorMoneyCellStyle.setBorderLeft(BorderStyle.THIN);
        colorMoneyCellStyle.setBorderRight(BorderStyle.THIN);

        Map<String, CellStyle> cellStyleMap = new HashMap<>(5);
        // commonCellStyle 内容水平、上下居中
        cellStyleMap.put("commonCellStyle", commonCellStyle);
        // moneyCellStyle 内容上下居中、水平居右、使用千分计数 + 保留小数后两位
        cellStyleMap.put("moneyCellStyle", moneyCellStyle);
        // colorCellStyle 内容水平、上下居中
        cellStyleMap.put("colorCellStyle", colorCellStyle);
        // longTextCellStyle 内容水平居左、上下居中
        cellStyleMap.put("longTextCellStyle", longTextCellStyle);
        // colorMoneyCellStyle 继承 moneyCellStyle + colorCellStyle
        cellStyleMap.put("colorMoneyCellStyle", colorMoneyCellStyle);
        return cellStyleMap;
    }

    /**
     * 获取 sheet 列表，子类必须实现
     */
    public abstract List<String> getSheetList(String filePath);

    public abstract List<String> getSheetList(File file);

    public abstract List<String> getSheetList(InputStream inputStream);

    /**
     * 读取索引为 sheetIndex 页签的全部数据
     *
     * @param filePath
     * @param sheetIndex
     * @return
     */
    public List<List<String>> readExcel(String filePath, int sheetIndex) {
        return readExcel(filePath, sheetIndex, "1-", "1-");
    }

    public List<List<String>> readExcel(File file, int sheetIndex) {
        return readExcel(file, sheetIndex, "1-", "1-");
    }

    public List<List<String>> readExcel(InputStream inputStream, int sheetIndex) {
        return readExcel(inputStream, sheetIndex, "1-", "1-");
    }

    /**
     * 读取索引为 sheetIndex 页签的 rows 行的数据
     *
     * @param filePath
     * @param sheetIndex
     * @param rows
     * @return
     */
    public List<List<String>> readExcel(String filePath, int sheetIndex, String rows) {
        return readExcel(filePath, sheetIndex, rows, "1-");
    }

    public List<List<String>> readExcel(File file, int sheetIndex, String rows) {
        return readExcel(file, sheetIndex, rows, "1-");
    }

    public List<List<String>> readExcel(InputStream inputStream, int sheetIndex, String rows) {
        return readExcel(inputStream, sheetIndex, rows, "1-");
    }

    /**
     * 读取索引为 sheetIndex 页签的 rows 行 columns 列的数据
     *
     * @param filePath
     * @param sheetIndex
     * @param rows
     * @param columns
     * @return
     */
    public abstract List<List<String>> readExcel(String filePath, int sheetIndex, String rows, String columns);

    public abstract List<List<String>> readExcel(File file, int sheetIndex, String rows, String columns);

    public abstract List<List<String>> readExcel(InputStream inputStream, int sheetIndex, String rows, String columns);

    /**
     * 读取索引为 sheetIndex 页签的 columns 列的数据
     *
     * @param filePath
     * @param sheetIndex
     * @param columns
     * @return
     */
    public List<List<String>> readExcel(String filePath, int sheetIndex, String[] columns) {
        return readExcel(filePath, sheetIndex, "1-", columns);
    }

    public List<List<String>> readExcel(File file, int sheetIndex, String[] columns) {
        return readExcel(file, sheetIndex, "1-", columns);
    }

    public List<List<String>> readExcel(InputStream inputStream, int sheetIndex, String[] columns) {
        return readExcel(inputStream, sheetIndex, "1-", columns);
    }

    /**
     * 读取索引为 sheetIndex 页签的 columns 列的数据
     */
    public List<List<String>> readExcel(String filePath, int sheetIndex, String rows, String[] columns) {
        return readExcel(filePath, sheetIndex, rows, getColumnNumber(columns));
    }

    public List<List<String>> readExcel(File file, int sheetIndex, String rows, String[] columns) {
        return readExcel(file, sheetIndex, rows, getColumnNumber(columns));
    }

    public List<List<String>> readExcel(InputStream inputStream, int sheetIndex, String rows, String[] columns) {
        return readExcel(inputStream, sheetIndex, rows, getColumnNumber(columns));
    }

    /**
     * 读取Excel文件数据，子类必须实现
     */
    public abstract List<List<String>> readExcel(String filePath, int sheetIndex, String rows, int[] cols);

    public abstract List<List<String>> readExcel(File file, int sheetIndex, String rows, int[] cols);

    public abstract List<List<String>> readExcel(InputStream inputStream, int sheetIndex, String rows, int[] cols);

    /**
     * TODO
     * <p>
     * 读取Excel文件内容具体实现
     *
     * @param sheet
     * @param rows
     * @param cols
     * @return
     */
    protected List<List<String>> readExcel(Sheet sheet, String rows, int[] cols) {
        List<List<String>> dataList = new ArrayList<>();
        // 处理行信息，并逐行列块读取数据
        String[] rowList = rows.split(SEPARATOR);
        for (String rowStr : rowList) {
            if (rowStr.contains(CONNECTOR)) {
                String[] rowArr = rowStr.trim().split(CONNECTOR);
                int start = Integer.parseInt(rowArr[0]) - 1;
                int end;
                if (rowArr.length == 1) {
                    end = sheet.getLastRowNum();
                } else {
                    end = Integer.parseInt(rowArr[1].trim()) - 1;
                }
                dataList.addAll(getRowsValue(sheet, start, end, cols));
            } else {
                dataList.add(getRowValue(sheet, Integer.parseInt(rowStr) - 1, cols));
            }
        }
        return dataList;
    }

    /**
     * 获取连续行、列数据
     */
    protected List<List<String>> getRowsValue(Sheet sheet, int startRow, int endRow, int startCol, int endCol) {
        if (endRow < startRow || endCol < startCol) {
            return null;
        }
        List<List<String>> data = new ArrayList<>();
        for (int i = startRow; i <= endRow; i++) {
            data.add(getRowValue(sheet, i, startCol, endCol));
        }
        return data;
    }

    /**
     * 获取连续行、不连续列数据
     *
     * @param sheet
     * @param startRow
     * @param endRow
     * @param cols
     * @return
     */
    private List<List<String>> getRowsValue(Sheet sheet, int startRow, int endRow, int[] cols) {
        List<List<String>> data = new ArrayList<>();
        if (endRow < startRow) {
            return data;
        }
        for (int i = startRow; i <= endRow; i++) {
            data.add(getRowValue(sheet, i, cols));
        }
        return data;
    }

    /**
     * 获取行连续列数据
     */
    private List<String> getRowValue(Sheet sheet, int rowIndex, int startCol, int endCol) {
        if (endCol < startCol) {
            return null;
        }
        Row row = sheet.getRow(rowIndex);
        List<String> rowData = new ArrayList<>();
        for (int i = startCol; i <= endCol; i++) {
            rowData.add(getCellValue(row, i));
        }
        return rowData;
    }

    /**
     * 获取行不连续列数据
     * 注：返回列表的size = 表格的列数 + 1，多扩展一个字段用来标志该行是否是合并单元格
     * "-1" - 正常单元格，没有合并
     * "0" - 合并单元格的第1行
     * "1" - 合并单元格的第2行
     * ...
     * "n" - 合并单元格的第n+1行
     *
     * @param sheet
     * @param rowIndex
     * @param cols
     * @return
     */
    private List<String> getRowValue(Sheet sheet, int rowIndex, int[] cols) {
        Row row = sheet.getRow(rowIndex);
        List<String> rowData = new ArrayList<>();
        for (int colIndex : cols) {
            rowData.add(getCellValue(row, colIndex));
        }
        // 计算行在合并单元格下的索引
        int currentIndex = getCurrentIndexAtMergeArea(sheet, rowIndex);
        rowData.add(String.valueOf(currentIndex));
        return rowData;
    }

    /**
     * 获取单元格内容
     *
     * @param row
     * @param column a excel column string like 'A', 'C' or "AA".
     * @return
     */
    protected String getCellValue(Row row, String column) {
        return getCellValue(row, getColumnNumber(column));
    }

    /**
     * 获取单元格内容
     * 里面有判断单元格是否是合并的单元格
     * 1.如果是，则取合并组中第1行对应的单元格的内容
     * 2.如果否，则直接取当前单元格中的内容
     *
     * @param row
     * @param col a excel column index from 0 to 65535
     * @return
     */
    private String getCellValue(Row row, int col) {
        if (row == null) {
            return "";
        }
        // 对合并单元格进行处理，假如是行合并单元格，非第一行的取第一行的数据
        Sheet sheet = row.getSheet();
        int rowIndex = row.getRowNum();
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();
            if (rowIndex >= firstRow && rowIndex <= lastRow) {
                if (col >= firstColumn && col <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell);
                }
            }
        }
        Cell cell = row.getCell(col);
        return getCellValue(cell);
    }

    /**
     * 获取单元格内容
     * <p>
     * 1.POI中日期时间也当作数值进行存储，所以我们读取时先判断为数值NUMERIC类型，再用DateUtil.isCellDateFormatted(cell)，来判断是不是日期时间类型
     * 2.读取Excel数字列长度大于10位以上，poi读到的内容带有E等字符(科学计算法)
     *
     * @param cell
     * @return
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        String value;
        // type 有5种类型
        switch (cell.getCellTypeEnum()) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 对日期类型进行处理
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//非线程安全
                    value = sdf.format(cell.getDateCellValue());
                } else {
                    // 对数字类型进行处理，目前只考虑2位小数，如需考虑更多 可以使用 #.####即可
                    value = new DecimalFormat("#.##").format(cell.getNumericCellValue());
                }
                break;
            case FORMULA:
                value = cell.getCellFormula();
                break;
            case BLANK:
                value = "";
                break;
            default:
                // ERROR
                value = "";
        }
        return value;
    }

    /**
     * Change excel column letter to integer number
     *
     * @param columns column letter of excel file, like A,B,AA,AB
     * @return
     */
    private int[] getColumnNumber(String[] columns) {
        int[] cols = new int[columns.length];
        for (int i = 0; i < columns.length; i++) {
            cols[i] = getColumnNumber(columns[i]);
        }
        return cols;
    }

    /**
     * Change excel column letter to integer number
     *
     * @param column column letter of excel file, like A,B,AA,AB
     * @return
     */
    private int getColumnNumber(String column) {
        int length = column.length();
        short result = 0;
        for (int i = 0; i < length; i++) {
            char letter = column.toUpperCase().charAt(i);
            int value = letter - 'A' + 1;
            result += value * Math.pow(26, length - i - 1);
        }
        return result - 1;
    }

    /**
     * Change excel column string to integer number array
     *
     * @param sheet   excel sheet
     * @param columns column letter of excel file, like A,B,AA,AB
     * @return
     */
    protected int[] getColumnNumber(Sheet sheet, String columns) {
        // 拆分后的列为动态，采用List暂存
        List<Integer> result = new ArrayList<>();
        String[] colList = columns.split(SEPARATOR);
        for (String colStr : colList) {
            if (colStr.contains(CONNECTOR)) {
                String[] colArr = colStr.trim().split(CONNECTOR);
                int start = Integer.parseInt(colArr[0]) - 1;
                int end;
                if (colArr.length == 1) {
                    end = sheet.getRow(sheet.getFirstRowNum()).getLastCellNum() - 1;
                } else {
                    end = Integer.parseInt(colArr[1].trim()) - 1;
                }
                for (int i = start; i <= end; i++) {
                    result.add(i);
                }
            } else {
                result.add(Integer.parseInt(colStr) - 1);
            }
        }
        // 将List转换为数组
        int len = result.size();
        int[] cols = new int[len];
        for (int i = 0; i < len; i++) {
            cols[i] = result.get(i);
        }
        return cols;
    }

    /**
     * 判断合并了行
     *
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    private boolean isMergedRow(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row == firstRow && row == lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet
     * @param row    行下标
     * @param column 列下标
     * @return
     */
    private boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {

                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 计算当前行在当前合并单元格（行合并）区域中的行索引
     * -1 非合并行返回
     * 0 合并行的第1行
     * 1 合并行的第2行
     * ...
     * n 合并行的第n+1行
     *
     * @param sheet
     * @param row
     * @return
     */
    private int getCurrentIndexAtMergeArea(Sheet sheet, int row) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                for (int index = 0; index < lastRow - firstRow + 1; index++) {
                    if (row == firstRow + index) {
                        return index;
                    }
                }
            }
        }
        return -1;
    }

}
