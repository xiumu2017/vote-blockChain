package com.gaoshan.linkvote.base.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelExportUtils implements Serializable {

    private static final long serialVersionUID = -5308899153464707168L;

    private String sheetName; // sheet名称
    private String title; // 导出表格的表名
    private HttpServletResponse response;
    private HttpServletRequest request;
    private String[] rowName;// 导出表格的列名
    private List<Object[]> dataList = new ArrayList<>(); // 对象数组的List集合

    public ExcelExportUtils() {
    }

    /**
     * 实例化导出类
     *
     * @param title    导出表格的表名，最好是英文，中文可能出现乱码
     * @param rowName  导出表格的列名数组
     * @param dataList 对象数组的List集合
     * @param response http响应
     */
    public ExcelExportUtils(String title, String[] rowName, List<Object[]> dataList, HttpServletRequest request, HttpServletResponse response) {
        this.title = title;
        this.rowName = rowName;
        this.dataList = dataList;
        this.response = response;
        this.request = request;
    }

    /**
     * 实例化导出类
     *
     * @param sheetName sheet名称
     * @param title     导出表格的表名，最好是英文，中文可能出现乱码
     * @param rowName   导出表格的列名数组
     * @param dataList  对象数组的List集合
     * @param response  http响应
     */
    public ExcelExportUtils(String sheetName, String title, String[] rowName, List<Object[]> dataList, HttpServletRequest request, HttpServletResponse response) {
        this.sheetName = sheetName;
        this.title = title;
        this.rowName = rowName;
        this.dataList = dataList;
        this.response = response;
        this.request = request;
    }

    private SXSSFWorkbook getSXSSFWorkbook() {
        // 声明一个工作薄 Excel 2007 OOXML (.xlsx)格式  (默认的内存滑动窗口为100，这里根据业务设置为10000)
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
        // 创建表格
        String temp = title;
        if (sheetName != null) {
            temp = sheetName;
        }
        SXSSFSheet sheet = workbook.createSheet(temp);
        // 根据列名设置每一列的宽度
        for (int i = 1; i < rowName.length; i++) {
            int length = rowName[i].length();
            sheet.setColumnWidth(i, 2 * (length + 1) * 256);
        }
        // 设置默认的高度
        sheet.setDefaultRowHeightInPoints(18.5f);
        // sheet样式定义
        // 头样式
        CellStyle columnTopStyle = this.getColumnTopStyle(workbook);
        // 单元格样式
        CellStyle style = this.getStyle(workbook, 12);
        // 产生表格标题行
        // 合并第一行的所有列
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, (rowName.length - 1)));
        // 行
        SXSSFRow sxssfRow = sheet.createRow(0);
        sxssfRow.setHeightInPoints(31f);
        // 单元格
        SXSSFCell cellTitle = sxssfRow.createCell(0);
        cellTitle.setCellStyle(columnTopStyle);
        cellTitle.setCellValue(title);

        // 产生第二行（列名）
        // 表格列的长度
        int columnNum = rowName.length;
        // 在第二行创建行
        SXSSFRow rowRowName = sheet.createRow(1);
        rowRowName.setHeightInPoints(21f);
        CellStyle cells = workbook.createCellStyle();
        cells.setBottomBorderColor(IndexedColors.BLACK.index);
        rowRowName.setRowStyle(cells);
        for (int i = 0; i < columnNum; i++) {
            SXSSFCell sxssfCell = rowRowName.createCell(i);
            // 单元格类型
            sxssfCell.setCellType(CellType.STRING);
            // 得到列的值
            XSSFRichTextString text = new XSSFRichTextString(rowName[i]);
            // 设置列的值
            sxssfCell.setCellValue(text);
            sxssfCell.setCellStyle(this.getColumnStyle(workbook));
        }

        // 产生其它行（将数据列表设置到对应的单元格中）注意：默认添加了第一列的序号，如果不要可以注释掉
        for (int i = 0; i < dataList.size(); i++) {
            //遍历每个对象
            Object[] obj = dataList.get(i);
            //创建所需的行数
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
                // 样式
                cell.setCellStyle(style);
            }
        }
        // 根据内容自动调整列宽 (适应英文、数字)
        sheet.trackAllColumnsForAutoSizing();
        for (int i = 0; i < rowName.length; i++) {
            sheet.autoSizeColumn(i);
        }
        // 根据内容自动调整列宽 (适应中文)
        setSizeColumn(sheet, rowName.length);

        return workbook;
    }

    // 根据内容自适应宽度(适应中文)
    private void setSizeColumn(SXSSFSheet sheet, int size) {
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
    public File getFile() throws IOException {
        String targetPath = System.getProperty("user.dir");
        SXSSFWorkbook workbook = getSXSSFWorkbook();
        File file = new File(targetPath + File.separator + title + ".xlsx");
        if (workbook != null && StrUtil.isNotEmpty(targetPath)) {
            // 输出到服务器上
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);//将数据写出去
            fileOutputStream.close();//关闭输出流
        }
        return file;
    }

    // 导出数据
    public void exportData() throws IOException {
        SXSSFWorkbook workbook = getSXSSFWorkbook();
        if (workbook != null) {
            // 输出到用户浏览器上
            // excel 表文件名
            String fileName = title + ".xlsx";
            // 中文乱码问题
            String userAgent = request.getHeader("USER-AGENT");
            if (StringUtils.contains(userAgent, "Firefox") || StringUtils.contains(userAgent, "firefox")) {
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            } else {
                fileName = encodingFileName(fileName);
            }
            response.setHeader("content-type", "application/octet-stream;");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            File file = getFile();
            FileUtils.copyFile(file, response.getOutputStream());
            boolean d = file.delete();
        }
    }


    public static String encodingFileName(String fileName) {
        String returnFileName = "";
        try {
            returnFileName = URLEncoder.encode(fileName, "UTF-8");
            returnFileName = StringUtils.replace(returnFileName, "+", "%20");
            if (returnFileName.length() > 150) {
                returnFileName = new String(fileName.getBytes("GB2312"), "ISO8859-1");
                returnFileName = StringUtils.replace(returnFileName, " ", "%20");
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return returnFileName;
    }

    /**
     * 设置工作表的标题行样式
     *
     * @param workbook 工作表对象
     * @return 样式
     */
    private CellStyle getColumnTopStyle(SXSSFWorkbook workbook) {
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
    private CellStyle getColumnStyle(SXSSFWorkbook workbook) {
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

    private void setCommonStyle(CellStyle style) {
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
     * @param fontSize 字体大小
     * @return 样式
     * @author dzhang
     */
    private CellStyle getStyle(SXSSFWorkbook workbook, int fontSize) {
        //设置字体
        Font font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) fontSize);
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
