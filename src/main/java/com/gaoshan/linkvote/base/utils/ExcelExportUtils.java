package com.gaoshan.linkvote.base.utils;

import cn.hutool.core.util.StrUtil;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ExcelExportUtils {

    @Getter
    @Builder
    public static class ExcelExportCfg {
        private String sheetName; // sheet名称
        private String title; // 导出表格的表名
        private HttpServletResponse response;
        private HttpServletRequest request;
        private String[] rowName;// 导出表格的列名
        private List<Object[]> dataList = new ArrayList<>(); // 对象数组的List集合
        // 可以补充的需要配置的参数：
        //        1. 每一列的宽度，数组
    }

    private static SXSSFWorkbook getSXSSFWorkbook(ExcelExportCfg cfg) {
        // 声明一个工作薄 Excel 2007 OOXML (.xlsx)格式  (默认的内存滑动窗口为100，这里根据业务设置为10000)
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
        // 创建表格
        String temp = cfg.getTitle();
        if (cfg.getSheetName() != null) {
            temp = cfg.getSheetName();
        }
        SXSSFSheet sheet = workbook.createSheet(temp);
        // 根据列名设置每一列的宽度
        for (int i = 1; i < cfg.getRowName().length; i++) {
            int length = cfg.getRowName()[i].length();
            sheet.setColumnWidth(i, 2 * (length + 1) * 256);
        }
        // 第一列，合并单元格，生成标题行
        sheet.setDefaultRowHeightInPoints(18.5f);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, (cfg.getRowName().length - 1)));
        SXSSFRow sxssfRow = sheet.createRow(0);
        sxssfRow.setHeightInPoints(31f);
        SXSSFCell cellTitle = sxssfRow.createCell(0);
        cellTitle.setCellStyle(getColumnTopStyle(workbook));
        cellTitle.setCellValue(cfg.getTitle());
        // 产生第二行（列名）
        SXSSFRow rowRowName = sheet.createRow(1);
        rowRowName.setHeightInPoints(21f);
        CellStyle cells = workbook.createCellStyle();
        cells.setBottomBorderColor(IndexedColors.BLACK.index);
        rowRowName.setRowStyle(cells);
        CellStyle rowNameCellStyle = getColumnStyle(workbook);
        for (int i = 0; i < cfg.getRowName().length; i++) {
            SXSSFCell sxssfCell = rowRowName.createCell(i);
            sxssfCell.setCellType(CellType.STRING);
            sxssfCell.setCellValue(new XSSFRichTextString(cfg.getRowName()[i]));
            sxssfCell.setCellStyle(rowNameCellStyle);
        }

        // 产生其它行（将数据列表设置到对应的单元格中）注意：默认添加了第一列的序号，如果不要可以注释掉
        CellStyle cellStyle = getStyle(workbook);
        for (int i = 0; i < cfg.getDataList().size(); i++) {
            Object[] obj = cfg.getDataList().get(i);
            SXSSFRow row = sheet.createRow(i + 2);
            row.setHeightInPoints(17.25f);
            for (int j = 0; j < obj.length; j++) {
                //设置单元格的数据类型
                SXSSFCell cell = row.createCell(j, CellType.STRING);
                if (!"".equals(obj[j]) && obj[j] != null) {
                    //设置单元格的值
                    cell.setCellValue(obj[j].toString());
                } else {
                    cell.setCellValue("");
                }
                cell.setCellStyle(cellStyle);
            }
        }
        // 根据内容自动调整列宽 (适应英文、数字)
        sheet.trackAllColumnsForAutoSizing();
        for (int i = 0; i < cfg.getRowName().length; i++) {
            sheet.autoSizeColumn(i);
        }
        // 根据内容自动调整列宽 (适应中文)
//        setSizeColumn(sheet, cfg.getRowName().length);
        return workbook;
    }

    // 根据内容自适应宽度(适应中文)
    private static void setSizeColumn(SXSSFSheet sheet, int size) {
        for (int columnNum = 0; columnNum < size; columnNum++) {
            // 列宽
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {
                // 行数据
                SXSSFRow currentRow = sheet.getRow(rowNum);
                if (currentRow != null && currentRow.getCell(columnNum) != null) {
                    SXSSFCell currentCell = currentRow.getCell(columnNum);
                    int length = currentCell.getStringCellValue().getBytes().length;
                    if (columnWidth < length) {
                        columnWidth = length;
                    }
                }
            }
            if (columnWidth < 255) {
                sheet.setColumnWidth(columnNum, columnWidth * 256 - 256);
            } else {
                // 单元格最大只能存放255 character 超过会报错
                sheet.setColumnWidth(columnNum, 6000);
            }
        }
    }

    /**
     * 导出excel到指定目录下
     *
     * @return 文件
     * @throws IOException e
     */
    private static File getFile(ExcelExportCfg cfg) throws IOException {
        String targetPath = System.getProperty("user.dir");
        SXSSFWorkbook workbook = getSXSSFWorkbook(cfg);
        File file = new File(targetPath + File.separator + cfg.getTitle() + ".xlsx");
        if (StrUtil.isNotEmpty(targetPath)) {
            // 输出到服务器上
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        }
        return file;
    }

    // 导出数据
    public static void exportData(ExcelExportCfg cfg) throws Exception {
        if (cfg == null) {
            throw new Exception("ExcelExportCfg must not be null");
        }
        String fileName = cfg.getTitle() + ".xlsx";
        fileName = encodingFileName(fileName);
        cfg.getResponse().setHeader("content-type", "application/octet-stream;");
        cfg.getResponse().setContentType("application/octet-stream");
        cfg.getResponse().setHeader("Content-Disposition", "attachment;filename=" + fileName);
        SXSSFWorkbook workbook = getSXSSFWorkbook(cfg);
        workbook.write(cfg.getResponse().getOutputStream());
    }

    private static String encodingFileName(String fileName) {
        String returnFileName = "";
        try {
            returnFileName = URLEncoder.encode(fileName, "UTF-8");
            returnFileName = StringUtils.replace(returnFileName, "+", "%20");
        } catch (UnsupportedEncodingException e) {
            // utf-8 能出问题？
        }
        return returnFileName;
    }

    /**
     * 设置工作表的标题行样式
     *
     * @param workbook 工作表对象
     * @return 样式
     */
    private static CellStyle getColumnTopStyle(SXSSFWorkbook workbook) {
        // 设置字体
        Font font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) 14);
        //字体加粗
        font.setBold(true);
        //设置字体名字
        font.setFontName("宋体");
        //设置样式;
        CellStyle style = workbook.createCellStyle();
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HorizontalAlignment.CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * 单元格样式设置
     *
     * @param workbook 工作表
     * @return 样式
     * @author dzhang
     */
    private static CellStyle getColumnStyle(SXSSFWorkbook workbook) {
        // 设置字体
        Font font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) 12);
        //字体加粗
        font.setBold(true);
        //设置字体名字
        font.setFontName("宋体");
        //设置样式;
        CellStyle style = workbook.createCellStyle();
        setCommonStyle(style);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置背景填充色（前景色）//设置别的颜色请去网上查询相关文档
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private static void setCommonStyle(CellStyle style) {
        //设置底边框;
        style.setBorderBottom(BorderStyle.THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(IndexedColors.BLACK.index);
        //设置左边框;
        style.setBorderLeft(BorderStyle.THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(IndexedColors.BLACK.index);
        //设置右边框;
        style.setBorderRight(BorderStyle.THIN);
        //设置右边框颜色;
        style.setRightBorderColor(IndexedColors.BLACK.index);
        //设置顶边框;
        style.setBorderTop(BorderStyle.THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(IndexedColors.BLACK.index);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HorizontalAlignment.CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    /**
     * 设置单元格样式和字体
     *
     * @param workbook 工作表
     * @return 样式
     * @author dzhang
     */
    private static CellStyle getStyle(SXSSFWorkbook workbook) {
        //设置字体
        Font font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) 12);
        //设置字体名字
        font.setFontName("宋体");
        //设置样式;
        CellStyle style = workbook.createCellStyle();
        setCommonStyle(style);
        //在样式用应用设置的字体;
        style.setFont(font);
        return style;
    }
}
