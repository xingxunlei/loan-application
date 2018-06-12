package com.loan_manage.utils;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

/**
 * 文件压缩、解压缩工具包
 * @author xingmin
 */
public class ZipUtils {

    private static final String CHARSET_GBK = "GBK";
    private static final int CACHE_SIZE = 1024;

    /**
     * 压缩一个或多个文件
     *
     * @param isParent    是否压缩文件目录 false 不压缩目录  true 压缩目录
     * @param zipFilePath 压缩后的文件存放地址
     * @param srcFilePath 压缩前的文件存放地址
     */
    public static void compress(boolean isParent, String zipFilePath, String... srcFilePath) {
        File zipFile = new File(zipFilePath);
        if (!zipFile.getParentFile().exists()) {
            zipFile.getParentFile().mkdirs();
        }

        FileOutputStream fileOutputStream = null;
        CheckedOutputStream checkedOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        try {

            fileOutputStream = new FileOutputStream(zipFile);
            checkedOutputStream = new CheckedOutputStream(fileOutputStream, new CRC32());
            bufferedOutputStream = new BufferedOutputStream(checkedOutputStream);

            zipOutputStream = new ZipOutputStream(bufferedOutputStream);
            zipOutputStream.setEncoding(CHARSET_GBK);

            for (String srcPath : srcFilePath) {
                File file = new File(srcPath);
                if (!file.exists()) {
                    throw new RuntimeException(srcPath + "不存在！");
                }

                compress(isParent, file, zipOutputStream);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (zipOutputStream != null) {
                    zipOutputStream.closeEntry();
                    zipOutputStream.close();
                }
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                if (checkedOutputStream != null) {
                    checkedOutputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 解压缩文件
     *
     * @param zipFilePath 压缩包存放路径
     * @param destPath    解压目录
     */
    public static void unCompress(String zipFilePath, String destPath) {
        ZipFile zipFile = null;
        try {
            BufferedInputStream bufferedInputStream;
            FileOutputStream fileOutputStream;
            BufferedOutputStream bufferedOutputStream;
            zipFile = new ZipFile(zipFilePath, CHARSET_GBK);
            Enumeration<ZipEntry> zipEntries = zipFile.getEntries();
            File file, parentFile;
            ZipEntry entry;
            byte[] data = new byte[CACHE_SIZE];
            while (zipEntries.hasMoreElements()) {
                entry = zipEntries.nextElement();
                if (entry.isDirectory()) {
                    new File(destPath + File.separator + entry.getName()).mkdirs();
                    continue;
                }
                bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(entry));
                file = new File(destPath + entry.getName());
                parentFile = file.getParentFile();
                if (parentFile != null && (!parentFile.exists())) {
                    parentFile.mkdirs();
                }
                fileOutputStream = new FileOutputStream(file);
                bufferedOutputStream = new BufferedOutputStream(fileOutputStream, CACHE_SIZE);
                int readIndex;
                while ((readIndex = bufferedInputStream.read(data, 0, CACHE_SIZE)) != -1) {
                    fileOutputStream.write(data, 0, readIndex);
                }
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                fileOutputStream.close();
                bufferedInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                zipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private static void compress(boolean isParent, File file, ZipOutputStream zipOutputStream) {
        if (!file.isDirectory()) {
            compressFile(isParent, file, zipOutputStream);
            return;
        }

        compressDir(isParent, file, zipOutputStream);
    }

    private static void compressDir(boolean isParent, File file, ZipOutputStream zipOutputStream) {
        if (!file.exists()) {
            return;
        }
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            compress(isParent, files[i], zipOutputStream);
        }
    }

    private static void compressFile(boolean isParent, File file, ZipOutputStream zipOutputStream) {
        if (!file.exists()) {
            return;
        }

        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));

            String fileName = file.getName();
            if (isParent) {
                fileName = file.getParent() + File.separator + file.getName();
            }
            fileName = fileName.indexOf(":") > -1 ? fileName.substring(fileName.indexOf(":") + 2, fileName.length()) : fileName;

            ZipEntry entry = new ZipEntry(fileName);
            zipOutputStream.putNextEntry(entry);
            int readIndex;
            byte data[] = new byte[CACHE_SIZE];
            while ((readIndex = bufferedInputStream.read(data, 0, CACHE_SIZE)) != -1) {
                zipOutputStream.write(data, 0, readIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

}
