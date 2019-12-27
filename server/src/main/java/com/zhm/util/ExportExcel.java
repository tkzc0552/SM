package com.zhm.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * 利用开源组件POI动态导出EXCEL文档
 * Created by 赵红明 on 2019/6/17.
 */
public class ExportExcel<T> {

    private static final int maxSize = 65535;

    /**
     * 传入map的导入方法
     *
     * @param title
     * @param headers
     * @param datalist
     * @param fieldNames
     * @param res
     * @throws IOException
     */
    public void exportMapExcel(String title, String[] headers, List<Map<String, Object>> datalist, String[] fieldNames, HttpServletResponse res) throws IOException {
        HSSFWorkbook wb = exportExcel(title, headers, fieldNames, datalist, null, null);
        exportExcel(res, title, wb);

    }

    /**
     * 传入实体的导出方法
     *
     * @param title
     * @param headers
     * @param dataset
     * @param fieldNames
     * @param res
     * @param pattern
     * @throws IOException
     */
    public void exportExcel(String title, String[] headers, Collection<T> dataset, String[] fieldNames, HttpServletResponse res, String pattern) throws IOException {
        HSSFWorkbook wb = exportExcel(title, headers, fieldNames, null, dataset, pattern);
        exportExcel(res, title, wb);

    }

    /**
     * 把生成的ecxel写在响应里
     *
     * @param res
     * @param title
     * @param wb
     * @throws IOException
     */
    public void exportExcel(HttpServletResponse res, String title, HSSFWorkbook wb) throws IOException {
        res.setHeader("Connection", "close");
        res.setHeader("Content-Type", "application/vnd.ms-excel;charset=UTF-8");
        res.setHeader("Content-Disposition", "attachment;filename=" + title);
        OutputStream out = res.getOutputStream();
        try {
            wb.write(out);
        } finally {
            if (null != out) {
                out.close();
            }
        }
    }

    /**
     * 根据传入的信息生成excel
     *
     * @param title
     * @param headers
     * @param fieldNames
     * @param dataList
     * @param dataset
     * @return
     */
    public HSSFWorkbook exportExcel(String title, String[] headers, String[] fieldNames, List<Map<String, Object>> dataList, Collection<T> dataset, String pattern) {
        if (ArrayUtils.isEmpty(fieldNames) && CollectionUtils.isEmpty(dataset) && CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(pattern)) {
            pattern = "yyyy-MM-dd";
        }
        int collectionSize = 0;
        Map<String, Object> map;
        boolean isMap = false;
        if (CollectionUtils.isNotEmpty(dataList)) {
            isMap = true;
            map = dataList.get(0);
            fieldNames = getMethods(fieldNames, map);
            collectionSize = dataList.size();
        }
        ArrayList<T> list = null;
        T t;
        if (CollectionUtils.isNotEmpty(dataset)) {
            list = (ArrayList<T>) dataset;
            t = list.get(0);
            fieldNames = getMethods(t, fieldNames);
            collectionSize = dataset.size();
        }

        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个样式
        HSSFFont font = createFont(workbook);
        font.setBold(true);
        HSSFFont font2 = createFont(workbook);
        HSSFCellStyle style = createCellStyle(workbook, font);
        HSSFCellStyle style2 = createCellStyle(workbook, font2);
        HSSFPatriarch patriarch;
        HSSFSheet sheet;
        HSSFRow row;
        int len = maxSize;
        if (collectionSize > maxSize) {
            //根据导出条数，计算生成几个sheet
            int m = collectionSize % maxSize;
            int n = 0;
            if (m == 0) {
                n = collectionSize / maxSize;
            } else {
                n = collectionSize / maxSize + 1;
            }

            for (int j = 0; j < n; j++) {
                int index = 0;
                sheet = workbook.createSheet(j + title);
                // 设置表格默认列宽度为15个字节
                sheet.setDefaultColumnWidth(15);
                // 声明一个画图的顶级管理器
                patriarch = sheet.createDrawingPatriarch();
                //产生表格标题行
                row = sheet.createRow(0);
                createHeaders(headers, row, style);

                HSSFFont font3 = workbook.createFont();
                font3.setFontHeightInPoints((short) 12);
                ArrayList list1 = null;
                if (dataList.size() > maxSize) {
                    if (isMap) {
                        len = dataList.size();
                    } else {
                        len = dataset.size();
                    }
                }

                list1 = new ArrayList<>(len);
                for (int k = 0; k < collectionSize; k++) {
                    index++;
                    if (index > maxSize) {
                        if (isMap) {
                            dataList.removeAll(list1);
                        } else {
                            dataset.removeAll(list1);
                        }
                        break;
                    }
                    if (isMap) {
                        map = dataList.get(k);
                        list1.add(map);
                        createRow(workbook, sheet, style2, patriarch, font3, index, map, fieldNames, pattern);
                    } else {
                        t = list.get(k);
                        list1.add(t);
                        createRow(workbook, sheet, style2, patriarch, font3, index, t, fieldNames, pattern);
                    }


                }
            }
        } else {
            // 生成一个表格
            sheet = workbook.createSheet(title);
            // 设置表格默认列宽度为15个字节
            sheet.setDefaultColumnWidth(15);
            // 声明一个画图的顶级管理器
            patriarch = sheet.createDrawingPatriarch();
            /**
             // 定义注释的大小和位置,详见文档
             HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
             // 设置注释内容
             comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
             // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
             comment.setAuthor("leno");
             */
            //产生表格标题行
            row = sheet.createRow(0);
            createHeaders(headers, row, style);
            //遍历集合数据，产生数据行
            Iterator it;
            if (isMap) {
                if (CollectionUtils.isEmpty(dataList)) {
                    return workbook;
                }
                it = dataList.iterator();
            } else {
                if (CollectionUtils.isEmpty(dataset)) {
                    return workbook;
                }
                it = dataset.iterator();
            }
            int index = 0;
            HSSFFont font3 = workbook.createFont();
            font3.setFontHeightInPoints((short) 12);
            while (it.hasNext()) {
                index++;
                if (isMap) {
                    map = (Map<String, Object>) it.next();
                    createRow(workbook, sheet, style2, patriarch, font3, index, map, fieldNames, pattern);
                } else {
                    t = (T) it.next();
                    createRow(workbook, sheet, style2, patriarch, font3, index, t, fieldNames, pattern);
                }
            }
        }
        return workbook;
    }

    /**
     * 获取map需要获取的属性
     *
     * @param fieldNames
     * @param map
     * @return
     */
    private String[] getMethods(String[] fieldNames, Map<String, Object> map) {
        if (null == fieldNames) {
            fieldNames = new String[map.size()];
            int index = 0;
            for (String key : map.keySet()) {
                fieldNames[index] = key;
                index++;
            }
        }
        return fieldNames;
    }

    /**
     * 获取实体反射方法
     *
     * @param t
     * @param fieldNames
     * @return
     */
    private String[] getMethods(T t, String[] fieldNames) {
        String[] arr = null;
        if (null == fieldNames) {
            //利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
            Field[] fields = t.getClass().getDeclaredFields();
            fieldNames = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                fieldNames[i] = field.getName();
            }
        }
        if (null != fieldNames) {
            String fieldName;
            StringBuffer sb;
            arr = new String[fieldNames.length];
            for (int i = 0; i < fieldNames.length; i++) {
                sb = new StringBuffer("get");
                fieldName = fieldNames[i];
                sb.append(fieldName.substring(0, 1).toUpperCase());
                sb.append(fieldName.substring(1));
                arr[i] = sb.toString();
            }
        }
        return arr;
    }

    /**
     * 创建表格头
     *
     * @param headers
     * @param row
     * @param style
     */
    private void createHeaders(String[] headers, HSSFRow row, HSSFCellStyle style) {
        HSSFRichTextString text;
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            text = new HSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
    }

    /**
     * 创建一行 传入T
     *
     * @param workbook
     * @param sheet
     * @param style2
     * @param patriarch
     * @param font3
     * @param index
     * @param t
     * @param arr
     * @param pattern
     * @return
     */
    private HSSFRow createRow(HSSFWorkbook workbook, HSSFSheet sheet, HSSFCellStyle style2, HSSFPatriarch patriarch, HSSFFont font3, int index, T t, String[] arr, String pattern) {
        HSSFRow row = sheet.createRow(index);
        Object value;
        for (int i = 0; i < arr.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style2);
            Class tCls = t.getClass();
            try {
                Method getMethod = tCls.getMethod(arr[i],
                        new Class[]{});
                value = getMethod.invoke(t, new Object[]{});
                setValue(workbook, sheet, row, cell, patriarch, font3, pattern, index, i, value);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return row;
    }

    /**
     * 创建一行 传入map
     *
     * @param workbook
     * @param sheet
     * @param style2
     * @param patriarch
     * @param font3
     * @param index
     * @param t
     * @param arr
     * @param pattern
     * @return
     */
    private HSSFRow createRow(HSSFWorkbook workbook, HSSFSheet sheet, HSSFCellStyle style2, HSSFPatriarch patriarch, HSSFFont font3, int index, Map<String, Object> t, String[] arr, String pattern) {
        HSSFRow row = sheet.createRow(index);
        Object value;
        for (int i = 0; i < arr.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style2);
            value = t.get(arr[i]);
            setValue(workbook, sheet, row, cell, patriarch, font3, pattern, index, i, value);
        }
        return row;
    }

    /**
     * 值转换
     *
     * @param workbook
     * @param sheet
     * @param row
     * @param cell
     * @param patriarch
     * @param font3
     * @param pattern
     * @param index
     * @param i
     * @param value
     */
    private void setValue(HSSFWorkbook workbook, HSSFSheet sheet, HSSFRow row, HSSFCell cell, HSSFPatriarch patriarch, HSSFFont font3, String pattern, int index, int i, Object value) {
        //判断值的类型后进行强制类型转换
        String textValue = null;
        if (value instanceof Boolean) {
            boolean bValue = (Boolean) value;
            textValue = "是";
            if (!bValue) {
                textValue = "否";
            }
        } else if (value instanceof Date) {
            Date date = (Date) value;
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            textValue = sdf.format(date);
        } else if (value instanceof byte[]) {
            // 有图片时，设置行高为60px;
            row.setHeightInPoints(60);
            // 设置图片所在列宽度为80px,注意这里单位的一个换算
            sheet.setColumnWidth(i, (int) (35.7 * 80));
            // sheet.autoSizeColumn(i);
            byte[] bsValue = (byte[]) value;
            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,
                    1023, 255, (short) 6, index, (short) 6, index);
            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
            patriarch.createPicture(anchor, workbook.addPicture(
                    bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
        } else if (value instanceof Long) {
            if (value.toString().length() == 13) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                textValue = sdf.format(new Date(Long.valueOf(value.toString())));
            } else {
                textValue = value.toString();
            }
        } else {
            //其它数据类型都当作字符串简单处理
            textValue = value == null ? "" : value.toString();
        }
        //如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
        if (textValue != null) {
            Pattern p = Pattern.compile("^//d+(//.//d+)?$");
            Matcher matcher = p.matcher(textValue);
            if (matcher.matches()) {
                //是数字当作double处理
                cell.setCellValue(Double.parseDouble(textValue));
            } else {
                HSSFRichTextString richString = new HSSFRichTextString(textValue);
                font3.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
                richString.applyFont(font3);
                cell.setCellValue(richString);
            }
        }
    }

    /**
     * 创建字体样式
     *
     * @param workbook
     * @return
     */
    private HSSFFont createFont(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        //创建第一种字体样式（用于列名）
        font.setFontHeightInPoints((short) 14);
        font.setColor(IndexedColors.BLACK.getIndex());
        return font;
    }

    /**
     * 创建格子样式
     *
     * @param workbook
     * @param font
     * @return
     */
    private HSSFCellStyle createCellStyle(HSSFWorkbook workbook, HSSFFont font) {
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置第一种单元格的样式（用于列名）
        style.setFont(font);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }



}