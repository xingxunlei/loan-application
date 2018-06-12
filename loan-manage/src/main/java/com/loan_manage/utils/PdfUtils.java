package com.loan_manage.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

/**
 * PDF工具类
 * @author xingmin
 */
public class PdfUtils {

    /**
     * 根据表格内容生成PDF文件
     * @param datas 要展示的内容列表
     * @param headerNames 表头数组
     * @param bodyNames 内容字段名数组
     * @param fileName 存放文件名
     */
    public static void generateTablePDF(List<Map<String, Object>> datas, String[] headerNames, String[] bodyNames, String fileName) {
        generateTablePDF(datas, headerNames, bodyNames, fileName, false);
    }

    /**
     * 根据表格内容生成PDF文件
     * @param datas 要展示的内容列表
     * @param headerNames 表头数组
     * @param bodyNames 内容字段名数组
     * @param fileName 存放文件名
     * @param isTransverse 是否横向展示
     */
    public static void generateTablePDF(List<Map<String, Object>> datas, String[] headerNames, String[] bodyNames, String fileName, boolean isTransverse) {
        generateTablePDF(datas, headerNames, bodyNames, fileName, "", "", isTransverse);
    }

    /**
     * 根据表格内容生成PDF文件
     * @param datas 要展示的内容列表
     * @param headerNames 表头数组
     * @param bodyNames 内容字段名数组
     * @param fileName 存放文件名
     * @param signName 签名信息
     * @param imgPath 公章图片地址
     * @param isTransverse 是否横向展示
     */
    public static void generateTablePDF(List<Map<String, Object>> datas, String[] headerNames, String[] bodyNames, String fileName, String signName, String imgPath, boolean isTransverse) {
        Document document;
        if (isTransverse) {
            document = new Document(new Rectangle(PageSize.A4).rotate());
        } else {
            document = new Document(new Rectangle(PageSize.A4));
        }

        try {
            File file = new File(fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            PdfPTable table = new PdfPTable(headerNames.length);
            table.setWidthPercentage(100); // Width 100%
            table.setSpacingBefore(10f); // Space before table
            table.setSpacingAfter(10f); // Space after table

            float[] columnWidths = new float[headerNames.length];
            for (int i = 0; i < headerNames.length; i++) {
                columnWidths[i] = 1f;
            }
            table.setWidths(columnWidths);

            for (String headerName : headerNames) {
                PdfPCell cell = new PdfPCell(new Paragraph(headerName, getFont()));
                cell.setPaddingLeft(10);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
            }

            datas.forEach(map -> {
                for (String bodyName : bodyNames) {
                    PdfPCell cell = new PdfPCell(new Paragraph(null == map.get(bodyName) ? "": map.get(bodyName).toString(), getFont()));
                    table.addCell(cell);
                }
            });

            document.add(table);

            if (!"".equals(signName)) {
                Paragraph paragraph = new Paragraph(signName, getFont());
                paragraph.setSpacingBefore(50f);
                paragraph.setSpacingAfter(-50f);
                paragraph.setAlignment(Paragraph.ALIGN_RIGHT);
                document.add(paragraph);
            }

            if (!"".equals(imgPath)) {
                pdfWriter.setStrictImageSequence(true);
                Image image = Image.getInstance(imgPath);
                image.scaleAbsolute(100, 100);
                image.setAlignment(Image.RIGHT|Image.UNDERLYING);
                document.add(image);
            }

            document.close();
            pdfWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static Font getFont() {
        try {
            return new Font(BaseFont.createFont("STSong-Light","UniGB-UCS2-H", BaseFont.NOT_EMBEDDED));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
