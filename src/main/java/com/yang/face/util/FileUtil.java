package com.yang.face.util;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author Yang
 * 文件处理工具类
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static String readFile(String path) {
        try {
            File file = new File(path);//定义一个file对象，用来初始化FileReader
            FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
            BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
            StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
            String s = "";
            while ((s = bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
                sb.append(s).append("\n");//将读取的字符串添加换行符后累加存放在缓存中
            }
            bReader.close();
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String readFile2(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n"); // 补上换行符
            }
            br.close();
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    //获取文件夹下所有文件名
    public static List<String> getFileNames(String path) {

        List<String> list = new ArrayList<>();

        File file = new File(path);
        File[] files = file.listFiles();

        for (File f : files) {
            if (f.isFile()) {
                list.add(f.getName());
            }
        }

        return list;
    }

    // 获取当前目录下所有文件
    public static List<File> getFilesWithoutFolder(String path) {

        List<File> list = new ArrayList<>();

        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            return list;
        }

        for (File f : files) {
            if (f.isFile()) {
                list.add(f);
            }
        }

        return list;
    }

    public static List<File> getFilesAll(String path) {

        List<File> list = new ArrayList<>();

        File file = new File(path);
        if (!file.isDirectory()) {
            return list;
        }

        File[] files = file.listFiles();


        for (File f : files) {
            if (f.isFile()) {
                list.add(f);
            }

            if (f.isDirectory()) {
                list.addAll(getFilesAll(f.getAbsolutePath()));
            }
        }
        return list;
    }

    //移动文件
    public static boolean moveFile(String pathName, String fileName, String destPath) {

        String startPath = pathName + fileName;
        String endPath = destPath;
        return moveFile(startPath, endPath);

    }

    //移动文件
    public static boolean moveFile(String srcFile, String destPath) {

        try {

            File startFile = new File(srcFile);
            File tmpFile = new File(destPath);//获取文件夹路径
            if (!tmpFile.exists()) {//判断文件夹是否创建，没有创建则创建新文件夹
                tmpFile.mkdirs();
            }
            System.out.println(tmpFile.getAbsolutePath());
            File newFile = new File(tmpFile.getAbsolutePath() + File.separator + startFile.getName());
            if (newFile.exists()) {
                newFile.delete();
            }
            boolean state = startFile.renameTo(new File(tmpFile.getAbsolutePath() + File.separator + startFile.getName()));
            return state;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //移动文件
    public static String moveFile2(String srcFile, String destPath) {

        String startPath = srcFile;
        String endPath = destPath;

        try {

            File startFile = new File(startPath);
            File tmpFile = new File(endPath);//获取文件夹路径
            if (!tmpFile.exists()) {//判断文件夹是否创建，没有创建则创建新文件夹
                tmpFile.mkdirs();
            }
            System.out.println(tmpFile.getAbsolutePath());
            File newFile = new File(tmpFile.getAbsolutePath() + File.separator + startFile.getName());
            if (newFile.exists()) {
                newFile.delete();
            }

            String newFileName = startFile.getName();
            boolean state = startFile.renameTo(new File(tmpFile.getAbsolutePath() + File.separator + startFile.getName()));
            if (state) {
                return newFileName;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return "";
    }

    public static boolean deleteDir(String dir) {
        File file = new File(dir);
        return deleteDir(file);
    }

    //递归删除目录下的所有文件及子目录下所有文件
    public static boolean deleteDir(File dir) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    //递归删除目录下的所有文件及子目录下所有文件
    public static boolean deleteSubFileAndFolder(String path) {

        File dir = new File(path);

        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return true;
    }

    // 删除某目录下的文件
    public static boolean deleteFiles(File dir, List<String> fileNameWithoutExts) {

        for (File file : dir.listFiles()) {

            String fileName = file.getName();
            String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf("."));
            if (fileNameWithoutExts.contains(fileNameWithoutExt)) {
                file.delete();
            }
        }

        return true;
    }

    //返回绝对路径
    public static String downloadUrl(String pathUrl, String dirSave) {
        return downloadUrl(pathUrl, dirSave, "");
    }

    //返回绝对路径
    public static String downloadUrl(String pathUrl, String dirSave, String fileName) {

        try {
            URL url = new URL(pathUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //得到输入流
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = readInputStream(inputStream);

            if (fileName.isEmpty()) {
                fileName = String.valueOf(System.currentTimeMillis());
            }
            //后缀，不包含。
            String ext = pathUrl.substring(pathUrl.lastIndexOf(".") + 1);


            //文件保存位置
            File saveDir = new File(dirSave);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            String fullPath = saveDir + File.separator + fileName + "." + ext;
            File file = new File(fullPath);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            if (fos != null) {
                fos.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            return fullPath;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "";
        }

    }

    //返回绝对路径
    public static String downloadUrl2(String pathUrl, String fileAbsSave) {
        try {
            URL url = new URL(pathUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //得到输入流
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = readInputStream(inputStream);


            //文件位置
            File file = new File(fileAbsSave);
            String dirSave = file.getParent();
            //文件保存位置
            File saveDir = new File(dirSave);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            if (fos != null) {
                fos.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            return fileAbsSave;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "";
        }

    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    /**
     * @param imgStr base64编码字符串
     * @param path   图片路径-具体到文件
     *  将base64编码字符串转换为图片
     */
    public static boolean base64ToImage(String imgStr, String path) {


        if (imgStr == null) {
            return false;
        }
        // 解密
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            String base64Header = "data:image/jpeg;base64,";
            if (imgStr.startsWith(base64Header)) {
                imgStr = imgStr.substring(base64Header.length());
            }

            byte[] b = decoder.decode(imgStr);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }

            OutputStream out = new FileOutputStream(path);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * @Description 根据图片地址转换为base64编码字符串
     */
    public static String imageToBase64(String imgFile) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 加密
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(data);
    }

    /**
     * BASE64Encoder 加密
     *
     * @param data 要加密的数据
     * @return 加密后的字符串
     */
    public static String encryptBase64(byte[] data) {
        // BASE64Encoder encoder = new BASE64Encoder();
        // String encode = encoder.encode(data);
        // 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Encoder
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(data);
    }

    /**
     * BASE64Decoder 解密
     *
     * @param data 要解密的字符串
     * @return 解密后的byte[]
     */
    public static byte[] decryptBase4(String data) {
        // BASE64Decoder decoder = new BASE64Decoder();
        // byte[] buffer = decoder.decodeBuffer(data);
        // 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Decoder
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(data);
    }

    /**
     * 获取一个文件的md5值(可处理大文件)
     *
     * @return md5 value
     */
    public static String getMD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(md5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        String base64file = "F:\\C557CAFB-7716-41c1-B3A3-5F42B64049D7.txt";
        String base64Str = readFile(base64file);

        boolean tmp = base64ToImage(base64Str, "F:\\2.jpg");
        System.out.println();

    }
}
