package com.loan_manage.utils.arbitration.util;

import com.loan_manage.utils.arbitration.config.Config;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileUitl {
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return new BASE64Encoder().encode(buffer);
    }

    public static void decoderBase64File(String base64Code, String targetPath) throws Exception {
        byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    public static void toFile(String base64Code, String targetPath)
            throws Exception {
        byte[] buffer = base64Code.getBytes();
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            UtilPrint.log_debug("创建目录失败,目标目录已存在!", "");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        if (dir.mkdirs()) {
            UtilPrint.log_debug("创建目录成功!", destDirName);
            return true;
        }
        UtilPrint.log_debug("创建目录失败!", "");
        return false;
    }

    public static List<File> getDirectory(String fileDir) throws Exception {
        File file = new File(fileDir);
        File[] flist = file.listFiles();
        int count = 0;
        UtilPrint.log_debug("Dir==>", "开始查看目录，是否有待上传文件");
        List<File> list = new ArrayList();
        if ((flist == null) || (flist.length == 0)) {
            return new ArrayList();
        }
        File[] arrayOfFile1;
        int j = (arrayOfFile1 = flist).length;
        for (int i = 0; i < j; i++) {
            File f = arrayOfFile1[i];
            if (f.isDirectory()) {
                UtilPrint.log_debug("Dir==>", f.getAbsolutePath());
            } else {
                String fileName = f.getName();
                String filePath = f.getAbsolutePath();
                if (count == Config.getCountList()) {
                    break;
                }
                UtilPrint.debugLog("fileName:" + fileName + ",filePath:" + filePath);
                count++;
                list.add(f);
            }
        }
        return list;
    }

    public static boolean MoveFile(File afile, String renameToafile) {
        try {
            if (afile.renameTo(new File(renameToafile + afile.getName()))) {
                UtilPrint.log_debug("File is moved successful!", renameToafile + afile.getName());
            } else {
                UtilPrint.debugLog("File is failed to move!");
                afile.delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
