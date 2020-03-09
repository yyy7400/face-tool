package com.yang.face.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.yang.face.constant.Constants;
import com.yang.face.constant.Properties;
import com.yang.face.entity.show.FacePathShow;
import com.yang.face.entity.show.Response;
import com.yang.face.util.PathUtil;
import com.yang.face.util.Plupload;
import com.yang.face.util.PluploadUtil;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
 * yangyuyang 2020-03-07
 * 分片上传文件, 前端需要使用Plupload 插件
 * */
@RestController
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    String uploadPath;
    public String FileDir = Constants.Dir.UPLOAD;

    @RequestMapping(value = "/upload/uploadFile", headers = "content-type=multipart/*", method = RequestMethod.POST)
    public Response uploadFile(Plupload plupload, HttpServletRequest request, HttpServletResponse response) {

        plupload.setRequest(request);
        //String userId = request.getParameter("userID");
        String type = request.getParameter("type");
        //String runtime = request.getParameter("runtime");

        if (type.equals(Constants.UploadType.IMAGE_FACE))
            FileDir = Constants.Dir.IMAGE_FACE;
        else if (type.equals(Constants.UploadType.NORMAL_FILE))
            FileDir = Constants.Dir.UPLOAD;

        //文件存储路径
        File dir = new File(Properties.SERVER_RESOURCE + FileDir);

        FacePathShow path = new FacePathShow();

        try {
            String name = plupload.getName();
            String ext = name.substring(name.indexOf("."));
            String fileName = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_MS_PATTERN) + ext;
            //上传文件
            PluploadUtil.upload(plupload, dir, fileName);
            //判断文件是否上传成功（被分成块的文件是否全部上传完成）
            if (PluploadUtil.isUploadFinish(plupload)) {
                String filePath = FileDir + fileName;
                path.setPath(filePath);
                path.setUrl(PathUtil.getUrl(filePath));

                //缩略图
                if (type.equals(Constants.UploadType.IMAGE_FACE)) {
                    String iconPathNew = filePath.substring(0, filePath.lastIndexOf("."))
                            + "_c" + filePath.substring(filePath.lastIndexOf("."), filePath.length());

                    //获取截图宽高
                    String photoAbs = PathUtil.getAbsPath(filePath);
                    Thumbnails.Builder<File> fileBuilder = Thumbnails.of(photoAbs).scale(1.0).outputQuality(1.0);
                    BufferedImage src = fileBuilder.asBufferedImage();
                    int size = src.getWidth() < src.getHeight() ? src.getWidth() : src.getHeight();
                    //Thumbnails.of(photoAbs).sourceRegion(Positions.CENTER, size, size).outputQuality(1.0).size(300, 300).toFile(PathUnits.getAbsolutePath(iconPathNew));

                    fileBuilder.toFile(photoAbs);
                    Thumbnails.of(photoAbs).sourceRegion(Positions.CENTER, size, size).outputQuality(1.0).size(300, 300).toFile(PathUtil.getAbsPath(iconPathNew));

                    path.setPath(filePath);
                    path.setUrl(PathUtil.getUrl(iconPathNew));
                }

                return Response.show(path);
            }

        } catch (IllegalStateException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return Response.show(path);

    }
}
