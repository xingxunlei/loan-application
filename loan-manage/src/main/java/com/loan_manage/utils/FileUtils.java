package com.loan_manage.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * 文件操作工具类
 * @author xingmin
 */
public class FileUtils {

    //图片转化成base64字符串
    public static String getImageStr(String path) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        String imgFile = path;//待处理的图片
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(data == null){
            return null;
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回Base64编码过的字节数组字符串
    }

    public static boolean generateImage(String imgStr, String path) {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }
            //生成jpeg图片
            File imageFile = new File(path);//新生成的图片
            if (!imageFile.getParentFile().exists()) {
                imageFile.getParentFile().mkdirs();
            }
            OutputStream out = new FileOutputStream(imageFile);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * BASE64加密文件
     * @param path
     * @since jdk1.8
     * @return
     */
    public static String encodeBase64File(String path) {
        try {
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            fileInputStream.read(buffer);
            fileInputStream.close();
            return Base64.getEncoder().encodeToString(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * BASE64解密文件
     * @param base64Code
     * @param targetPath
     * @since jdk1.8
     */
    public static void decodeBase64File(String base64Code, String targetPath) {
        try {
            byte[] buffer = Base64.getDecoder().decode(base64Code);
            FileOutputStream fileOutputStream = new FileOutputStream(targetPath);
            fileOutputStream.write(buffer);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 下载远程文件
     * @param remoteUrl 文件访问url
     * @param filePath 文件保存路径
     */
    public static void downloadFile(String remoteUrl, String filePath) {
        HttpURLConnection connection = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            File file = new File(filePath);
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            URL url = new URL(remoteUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            inputStream = connection.getInputStream();
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4 * 1024];
            int byteRead;
            while ((byteRead = (inputStream.read(buffer))) != -1) {
                outputStream.write(buffer, 0, byteRead);
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 复制文件
     * @param oldPath 文件原路径
     * @param newPath 文件新路径
     */
    public static void copyFile(String oldPath, String newPath) throws Exception{ // 复制文件
        File oldFile = new File(oldPath);
        if (!oldFile.exists()) {
            throw new FileNotFoundException("File is not found......【"+ oldPath +"】........");
        }

        saveFile(oldFile, newPath);
    }

    /**
     * 保存文件至指定目录
     * @param file File文件
     * @param filePath 保存目录
     */
    public static void saveFile(File file, String filePath) throws Exception{
        if (file == null) {
            throw new FileNotFoundException("File is null........");
        }

        try {
            File newFile = new File(filePath);
            if (!newFile.getParentFile().exists()) {// 目录不存在时，创建目录
                newFile.getParentFile().mkdirs();
            }

            InputStream inStream = new FileInputStream(file);
            FileOutputStream fs = new FileOutputStream(newFile);

            byte[] buffer = new byte[1024];
            int byteRead;

            while ((byteRead = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteRead);
            }
            fs.flush();
            fs.close();
            inStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    /**
     * 逐行读取文件内容至list
     * @param filePath 文件地址
     * @return List<String> 文件内容列表
     */
    public static List<String> readFileByLine(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            List<String> list = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }

            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    /**
     * 列出目录(包含子目录)下所有文件的文件名
     * @param path 文件夹目录
     * @return List<String> 所有文件的绝对路径
     */
    public static List<String> listFile(String path) {
        List<String> fileList = new ArrayList<>();
        File file = new File(path);
        if (!file.isDirectory()) {
            fileList.add(file.getAbsolutePath());
            return fileList;
        }

        listFile(file, fileList);

        return fileList;
    }

    private static void listFile(File file, List<String> list) {
        if (!file.isDirectory()) {
            list.add(file.getAbsolutePath());
            return;
        }

        for (File f : file.listFiles()) {
            listFile(f, list);
        }
    }

}
