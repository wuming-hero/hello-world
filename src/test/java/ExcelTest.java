import com.wuming.util.excel.PoiExcelHelper;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wuming
 * Created on 2018/4/28 19:30
 */
public class ExcelTest {

    /**
     * excel 读取
     */
    @Test
    public void readTest() throws Exception {
        String path = "/file/excel/read.xls";
        String filePath = this.getClass().getResource(path).getPath();
        System.out.println("filePath:" + filePath);

        PoiExcelHelper excelHelper = PoiExcelHelper.create(filePath.substring(filePath.lastIndexOf("."), filePath.length()));
        List<String> sheetList = excelHelper.getSheetList(filePath);

        for (int i = 0; i < sheetList.size(); i++) {
            // 表头字段列表
            List<String> fieldList = new ArrayList<>();
            // 循环读取每个sheet的内容
            List<List<String>> dataList = excelHelper.readExcel(filePath, i);
            // windows上新建excel文件，默认生成3个sheet,有两个空的备用sheet
            if (dataList.isEmpty()) continue;

            // 打印表头数据，因为有合并行，最后一列为合并行标志，即标志当前单元格是否在合并行范围内，且数值代表在当前合并行的行号
            List<String> keyList = dataList.get(0);
            for (int j = 0; j < keyList.size(); j++) {
                fieldList.add(keyList.get(j));
            }
            System.out.println("---->>>>fieldList: " + fieldList);

            // 打印所有数据
            for (List<String> strings : dataList) {
                System.out.println("count: " + strings.size() + ", data: " + strings);
            }
        }
    }

    /**
     * 生成 Excel
     */
    @Test
    public void writeTest() throws Exception {
        String originPath = "/file/excel/read.xls";
        String filePath = this.getClass().getResource(originPath).getPath();
        PoiExcelHelper excelHelper = PoiExcelHelper.create(filePath.substring(filePath.lastIndexOf("."), filePath.length()));
        List<String> sheetList = excelHelper.getSheetList(filePath);

        // 创建存储工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (int i = 0; i < sheetList.size(); i++) {
            // 循环读取每个sheet的内容
            List<List<String>> dataList = excelHelper.readExcel(filePath, i);
            // windows上新建excel文件，默认生成3个sheet,有两个空的备用sheet
            if (dataList.isEmpty()) continue;
            Map<String, CellStyle> cellStyleMap = PoiExcelHelper.cellStyleMap(workbook);
            HSSFSheet sheet = workbook.createSheet("sheet" + i);
            HSSFRow row;
            List<String> cellValueList;
            int columnLength = dataList.get(0).size() - 1;
            for (int j = 0; j < dataList.size(); j++) {
                cellValueList = dataList.get(j);
                row = sheet.createRow(j);
                for (int m = 0; m < columnLength; m++) {
                    HSSFCell cell = row.createCell(m);
                    CellStyle commonStyle = cellStyleMap.get("commonCellStyle");
                    cell.setCellValue(cellValueList.get(m));
                    if (j == 0) {
                        // 表头不作处理
                        cell.setCellValue(cellValueList.get(m));
                        HSSFFont font = workbook.createFont();
                        font.setBold(Boolean.TRUE); // 是否加粗
                        font.setFontHeightInPoints((short) 18); // 设置字体大小
                        font.setFontName("Courier New"); // 设置字体
                        commonStyle.setFont(font);
                        cell.setCellStyle(commonStyle);
                    } else {
                        if (m == 1) {
                            cell.setCellValue(Double.parseDouble(cellValueList.get(m)));
                            CellStyle cellStyle = cellStyleMap.get("moneyCellStyle");
                            HSSFFont font = workbook.createFont();
                            font.setColor(HSSFColor.HSSFColorPredefined.GREEN.getIndex());
                            font.setFontName("宋体");
                            font.setBold(Boolean.TRUE);
                            cellStyle.setFont(font);
                            cell.setCellStyle(cellStyle);
                        } else if (m == 3) {
                            cell.setCellStyle(cellStyleMap.get("longTextCellStyle"));
                        } else if (m == 4) {
                            cell.setCellValue(Double.parseDouble(cellValueList.get(m)));
                            cell.setCellStyle(cellStyleMap.get("colorMoneyCellStyle"));
                        } else if (m == 7) {
                            cell.setCellStyle(cellStyleMap.get("colorCellStyle"));
                        } else {
                            cell.setCellValue(cellValueList.get(m));
                        }
                    }
                }
            }

            // 处理合并区域
            CellRangeAddress cellRangeAddress = new CellRangeAddress(1, 3, 0, 0);
            CellRangeAddress cellRangeAddress1 = new CellRangeAddress(1, 3, 1, 1);
            CellRangeAddress cellRangeAddress2 = new CellRangeAddress(1, 3, 2, 2);
            CellRangeAddress cellRangeAddress3 = new CellRangeAddress(1, 3, 7, 7);
            CellRangeAddress cellRangeAddress4 = new CellRangeAddress(4, 5, 0, 0);
            CellRangeAddress cellRangeAddress5 = new CellRangeAddress(4, 5, 1, 1);
            CellRangeAddress cellRangeAddress6 = new CellRangeAddress(4, 5, 2, 2);
            CellRangeAddress cellRangeAddress7 = new CellRangeAddress(4, 5, 7, 7);
            sheet.addMergedRegion(cellRangeAddress);
            sheet.addMergedRegion(cellRangeAddress1);
            sheet.addMergedRegion(cellRangeAddress2);
            sheet.addMergedRegion(cellRangeAddress3);
            sheet.addMergedRegion(cellRangeAddress4);
            sheet.addMergedRegion(cellRangeAddress5);
            sheet.addMergedRegion(cellRangeAddress6);
            sheet.addMergedRegion(cellRangeAddress7);

            //单元格内容自适应，需要放在所有单元格内容写完后再调用
            for (int m = 0; m < dataList.get(0).size() - 1; m++) {
                sheet.autoSizeColumn(m, true);
            }

            // 写入文件
            File file = new File(this.getClass().getResource("/file/excel/").getPath() + "write.xls");
            if (!file.exists()) {
                boolean ret = file.createNewFile();
                System.out.println("createFile ret: " + ret);
            }
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
        }

    }
}
