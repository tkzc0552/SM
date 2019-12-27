package com.zhm.util;

/**
 * Created by 赵红明 on 2019/7/9.
 */
public class WDWUtil {
    /**
     * @描述：是否是2003的excel，返回true是2003
     * @param filePath
     * @return
     */
    public static boolean isExcel2003(String filePath)  {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    /**
     * @描述：是否是2007的excel，返回true是2007
     * @param filePath
     * @return
     */
    public static boolean isExcel2007(String filePath)  {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    /**
     * 验证是否是EXCEL文件
     * @param filePath
     * @return
     */
    public static boolean validateExcel(String filePath){
        if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))){
            return false;
        }
        return true;
    }
    public static boolean isChinese(String url) {
        // 不需要编码的正则表达式
//     String allowChars = SystemConfig.getValue("ENCODING_ALLOW_REGEX",
//             Constants.ENCODING_ALLOW_REGEX);
        if (url.matches("^[0-9a-zA-Z.:/?=&%~`#()-+]+$")) {
            return false;
        }

        return true;
    }
}