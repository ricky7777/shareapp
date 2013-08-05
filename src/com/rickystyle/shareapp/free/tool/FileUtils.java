package com.rickystyle.shareapp.free.tool;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 檔案處理小工具
 * 
 * @author Ricky
 * 
 */
public class FileUtils {
    public static void copyFile(String originalFileName, String destFileName) {

        try {
            byte[] buffer = new byte[4096];
            int size = -1;

            FileInputStream fis = new FileInputStream(originalFileName);
            FileOutputStream fos = new FileOutputStream(destFileName);

            while ((size = fis.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, size);
            }

            fis.close();
            fos.close();

            fis = null;
            fos = null;
        } catch (Exception ex) {
            LogUtils.d("log", "copy file error:%1$s", ex);
        }

    }
}
