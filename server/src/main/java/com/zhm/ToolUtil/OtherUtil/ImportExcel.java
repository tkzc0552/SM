package com.zhm.ToolUtil.OtherUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EXCEL导入操作
 * Created by 赵红明 on 2019/3/28.
 */
public class ImportExcel {

    private static final Logger logger= LoggerFactory.getLogger(ImportExcel.class);
    final static Pattern pattern = Pattern.compile("\\S*[?]\\S*");

    public Map<Integer,Map<Integer,String>> readXls(String path) throws IOException {
        Map<Integer,Map<Integer,String>> totalMap=new HashMap<>();
        URL url = new URL(path);
        InputStream is = url.openStream();
        //判断是否是xlsx
        if(parseSuffix(path).equals("xlsx")){
            XSSFWorkbook hssfWorkbooks = new XSSFWorkbook(is);
            for (int numSheet = 0; numSheet < hssfWorkbooks.getNumberOfSheets(); numSheet++) {
                XSSFSheet hssfSheet = hssfWorkbooks.getSheetAt(numSheet);
                if (hssfSheet == null) {
                    continue;
                }
                for (int rowNum = 2; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                    XSSFRow hssfRow = hssfSheet.getRow(rowNum);
                    Map<Integer,String> rowMap=new HashMap<Integer,String>();
                    if (hssfRow != null) {
                        try {
                            int hlength=hssfRow.getPhysicalNumberOfCells();
                            for(int i=0;i<hlength;i++){
                                if(hssfRow.getCell(i).equals("")){
                                    rowMap.put(i,"");
                                }else{
                                    String info=getXValue(hssfRow.getCell(i));
                                    logger.info("info="+i+"=="+info);
                                    rowMap.put(i,info);
                                }


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("转换异常",e.getCause());
                        }
                    }
                    totalMap.put(rowNum-1,rowMap);
                }
            }
        }else{
            HSSFWorkbook hssfWorkbook =new HSSFWorkbook(is) ;
            for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
                HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
                if (hssfSheet == null) {
                    continue;
                }
                for (int rowNum = 2; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                    HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                    Map<Integer,String> rowMap=new HashMap<Integer,String>();
                    if (hssfRow != null) {
                        try {
                            int hlength=hssfRow.getPhysicalNumberOfCells();

                            for(int i=0;i<hlength;i++){
                                if(hssfRow.getCell(i).equals("")){
                                    rowMap.put(i,"");
                                }else{
                                    String info=getValue(hssfRow.getCell(i));
                                    logger.info("info="+i+"=="+info);
                                    rowMap.put(i,info);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("转换异常",e.getCause());
                        }
                    }
                    totalMap.put(rowNum-1,rowMap);
                }
            }
        }
        return totalMap;
    }
    @SuppressWarnings("static-access")
    private String getValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            hssfCell.setCellType(HSSFCell.CELL_TYPE_STRING);
            return String.valueOf(hssfCell.getStringCellValue());
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }

    /**
     * 获取XL
     * @param hssfCell
     * @return
     */
    @SuppressWarnings("static-access")
    private String getXValue(XSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            hssfCell.setCellType(HSSFCell.CELL_TYPE_STRING);
            return String.valueOf(hssfCell.getStringCellValue());
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }

    /**
     * 获取链接的后缀名
     * @return
     */
    private static String parseSuffix(String url) {

        Matcher matcher = pattern.matcher(url);

        String[] spUrl = url.toString().split("/");
        int len = spUrl.length;
        String endUrl = spUrl[len - 1];

        if(matcher.find()) {
            String[] spEndUrl = endUrl.split("\\?");
            return spEndUrl[0].split("\\.")[1];
        }
        return endUrl.split("\\.")[1];
    }
}