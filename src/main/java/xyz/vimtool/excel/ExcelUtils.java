package xyz.vimtool.excel;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * excel文件处理公共类
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-7
 */
public class ExcelUtils {
    public static void main(String[] args) throws IOException {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet xssfSheet = xssfWorkbook.createSheet("111");

        //设置富文本字符串
        XSSFFont superscript = xssfWorkbook.createFont();
        superscript.setTypeOffset(HSSFFont.SS_SUPER);//上标
        superscript.setColor(new XSSFColor(java.awt.Color.BLACK));

        XSSFWorkbook workbook = new XSSFWorkbook("./test.xlsx");
        XSSFSheet sheet = workbook.getSheetAt(2);
        for(int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if(null == row) {
                continue;
            }

            XSSFRow xssfRow = xssfSheet.createRow(i);

            for(int j=1;j<=9;j++)
            {
                XSSFCell cell = row.getCell(j);
                XSSFCell xssfCell = xssfRow.createCell(j);
                if(null==cell) {
                    continue;
                }

                String value;
                if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                    value = cell.getNumericCellValue() + "";
                } else {
                    value = cell.getStringCellValue();
                }

                if (value.matches("\\d+(.\\d+)?[a-zA-Z]+")) {
                    Pattern pattern = Pattern.compile("\\d+(.\\d+)?");
                    Matcher matcher = pattern.matcher(value);
                    if (matcher.find(0)) {
                        XSSFRichTextString rts = new XSSFRichTextString(value);
                        rts.applyFont(matcher.end(), value.length(), superscript);
                        xssfCell.setCellValue(rts);
                    }
                } else {
                    xssfCell.setCellValue(value);
                }
            }
        }

        //写到磁盘上
        try (FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\A3.xlsx"))){
            xssfWorkbook.write(fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
