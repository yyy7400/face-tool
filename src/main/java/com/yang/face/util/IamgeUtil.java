package com.yang.face.util;

import com.yang.face.constant.Constants;
import com.yang.face.constant.Properties;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author yangyuyang
 * @date 2020/3/18 11:52
 */
public class IamgeUtil {

    private static final Logger logger = LoggerFactory.getLogger(IamgeUtil.class);

    // 生成人脸头像缩略图
    public static String getFaceIcon(String photo) {

        if(photo.isEmpty()) {
            return "";
        }

        try {
            // 移动到image/face中，压缩图片，最大不超过300*300
            String photoAbs = PathUtil.getAbsPath(photo);
            Thumbnails.Builder<File> fileBuilder = Thumbnails.of(photoAbs).scale(1.0).outputQuality(1.0);
            BufferedImage src = fileBuilder.asBufferedImage();
            int size = Math.min(src.getWidth(), src.getHeight());
            fileBuilder.toFile(photoAbs); // 重置照片，放正

            String fileName = new File(photoAbs).getName();
            String photoIconAbsNew = Properties.SERVER_RESOURCE + Constants.Dir.IMAGE_FACE
                    + fileName.substring(0, fileName.lastIndexOf(".")) + "_c"
                    + fileName.substring(fileName.lastIndexOf("."));

            Thumbnails.of(photoAbs).sourceRegion(Positions.CENTER, size, size).outputQuality(1.0).size(300, 300)
                    .toFile(photoIconAbsNew);

            return photoIconAbsNew;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return "";
        }
    }

    /**
     * 截图，并移动到 face/image 路径下
     * @param photo
     * @return originPhoto, iconPhoto
     */
    public static String[] getFaceIconPlus(String photo) {

        if(photo.isEmpty()) {
            return new String[] {"", ""};
        }

        try {
            // 移动到image/face中，压缩图片，最大不超过300*300
            String photoAbs = PathUtil.getAbsPath(photo);
            Thumbnails.Builder<File> fileBuilder = Thumbnails.of(photoAbs).scale(1.0).outputQuality(1.0);
            BufferedImage src = fileBuilder.asBufferedImage();
            int size = Math.min(src.getWidth(), src.getHeight());
            fileBuilder.toFile(photoAbs); // 重置照片，放正

            String fileName = new File(photoAbs).getName();
            String photoIconAbsNew = Properties.SERVER_RESOURCE + Constants.Dir.IMAGE_FACE
                    + fileName.substring(0, fileName.lastIndexOf(".")) + "_c"
                    + fileName.substring(fileName.lastIndexOf("."));

            String photoAbsPath = Properties.SERVER_RESOURCE + Constants.Dir.IMAGE_FACE;

            Thumbnails.of(photoAbs).sourceRegion(Positions.CENTER, size, size).outputQuality(1.0).size(300, 300)
                    .toFile(photoIconAbsNew);

            FileUtil.moveFile(photoAbs, photoAbsPath);

            return new String[] {photoAbsPath + fileName, photoIconAbsNew};

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return new String[] {"", ""};
        }
    }
}
