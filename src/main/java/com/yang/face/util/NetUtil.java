package com.yang.face.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author yangyuyang
 */
public class NetUtil {

    private static final Logger logger = LoggerFactory.getLogger(NetUtil.class);

    /**
     * 图片转为byte数组
     *
     * @param path
     * @return
     */
    public static byte[] image2byte(String path) throws IOException {
        byte[] data = null;
        URL url = null;
        InputStream input = null;
        try {
            url = new URL(path);
            HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            httpUrl.getInputStream();
            input = httpUrl.getInputStream();
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int numBytesRead = 0;
        while ((numBytesRead = input.read(buf)) != -1) {
            output.write(buf, 0, numBytesRead);
        }
        data = output.toByteArray();
        output.close();
        input.close();
        return data;
    }

    // 保持图片
    public static boolean byte2File(byte[] b, String filePath) {

        try {

            OutputStream out = new FileOutputStream(filePath);
            out.write(b);
            out.flush();
            out.close();

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }

        return true;
    }
}
