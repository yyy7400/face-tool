package com.yang.face.controller;

import com.yang.face.constant.enums.FaceFeatureTypeEnum;
import com.yang.face.entity.db.SystemSetting;
import com.yang.face.entity.post.FaceRecognitionImageActionPost;
import com.yang.face.entity.post.ImportFeaturePost;
import com.yang.face.entity.post.RecoImageWithUserPost;
import com.yang.face.entity.post.TypeAndPhotoPost;
import com.yang.face.entity.show.Response;
import com.yang.face.service.FaceService;
import com.yang.face.service.FaceStrageService;
import com.yang.face.service.SystemSettingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yangyuyang
 */
@RestController
public class FaceController {

    @Resource
    private FaceStrageService FaceStrageService;


    //近距离人脸识别（电子班牌）
    @RequestMapping(value = "/face/recoImage", method = RequestMethod.POST)
    public Response recoImage(@RequestBody @Validated TypeAndPhotoPost o) {
        List<String> users = new ArrayList<>();
        return Response.show(FaceStrageService.recoImage(o.getType(), o.getPhoto(), users));
    }

    //近距离人脸识别（电子班牌）指定用户）
    @RequestMapping(value = "/face/recoImageWithUser", method = RequestMethod.POST)
    public Response recoImageWithUser(@RequestBody @Validated RecoImageWithUserPost o) {
        return Response.show(FaceStrageService.recoImage(o.getType(), o.getPhoto(), o.getUserIds()));
    }

    //清理特征库
    @RequestMapping(value = "/face/cleanFeature", method = RequestMethod.POST)
    public Response cleanFeature() {
        return Response.show(FaceStrageService.cleanFeature(new ArrayList<>()));
    }

    //清理特征库by user
    @RequestMapping(value = "/face/cleanFeatureByUser", method = RequestMethod.POST)
    public Response cleanFeatureByUser(@RequestBody List<String> userIds) {
        return Response.show(FaceStrageService.cleanFeature(userIds));
    }

    //批量图片导入特征库
    @RequestMapping(value = "/face/importFeatures", method = RequestMethod.POST)
    public Response importFeatures(@RequestBody List<ImportFeaturePost> list) {
        return Response.show(FaceStrageService.importFeatures(list));
    }

    //单张图片导入特征库（无更新数据库）
    @Deprecated
    @RequestMapping(value = "/face/importFeaturesNoUpdate", method = RequestMethod.POST)
    public Response importFeaturesNoUpdate(@RequestBody List<ImportFeaturePost> list) {
        return Response.show(FaceStrageService.importFeaturesNoUpdate(list));
    }

    //更新特征库
    @Deprecated
    @RequestMapping(value = "/face/importFeaturesUpdateFeatures", method = RequestMethod.POST)
    public Response importFeaturesUpdateFeatures(@RequestBody List<String> list) {
        return Response.show(FaceStrageService.updateFeatures());
    }

    //更新特征库
    @RequestMapping(value = "/face/updateFeatures", method = RequestMethod.POST)
    public Response updateFeatures() {
        return Response.show(FaceStrageService.updateFeatures());
    }


    /****************   无感考勤  ****************************/
    //教室内远距离人脸识别（无感考勤）
    @RequestMapping(value = "/face/recoImageRoom", method = RequestMethod.POST)
    public Response recoImageRoom(@RequestBody @Validated TypeAndPhotoPost o) {
        List<String> users = new ArrayList<>();
        return Response.show(FaceStrageService.recoImageRoom(o.getType(), o.getPhoto(), users));
    }

    //教室内远距离人脸识别（无感考勤）
    @RequestMapping(value = "/face/recoImageWithUserRoom", method = RequestMethod.POST)
    public Response recoImageWithUserRoom(@RequestBody @Validated RecoImageWithUserPost o) {
        long t1 = new Date().getTime();
            Object object = FaceStrageService.recoImageRoom(o.getType(), o.getPhoto(), o.getUserIds());
        long t2 = new Date().getTime();
        System.out.println("time:" + (t2 - t1));
        return Response.show(object);
    }

    //开始人脸检测视频流（无感考勤） tested
    @RequestMapping(value = "/face/startDetectionVideo", method = RequestMethod.GET)
    public Response startDetectionVideo(String url) {
        return Response.show(FaceStrageService.startDetectionVideo(url));
    }

    //关闭人脸检测视频流（无感考勤）tested
    @Deprecated
    @RequestMapping(value = "/face/stopDetectionVideo", method = RequestMethod.GET)
    public Response stopDetectionVideo(String url) {
        return Response.show(FaceStrageService.stopDetectionVideo(url));
    }


    /****************   学情分析  ****************************/
    //开始人体姿势检测视频流（学情分析）
    @RequestMapping(value = "/face/startDetectionVideoAction", method = RequestMethod.GET)
    public Response startDetectionVideoAction(String url) {
        return Response.show(FaceStrageService.startDetectionVideoAction(url));
    }

    //关闭人体姿势检测视频流（学情分析）
    @Deprecated
    @RequestMapping(value = "/face/stopDetectionVideoAction", method = RequestMethod.GET)
    public Response stopDetectionVideoAction(String url) {
        return Response.show(FaceStrageService.stopDetectionVideoAction(url));
    }

    //人体姿势识别分析（学情分析）
    @RequestMapping(value = "/face/faceRecognitionImageAction", method = RequestMethod.POST)
    public Response faceRecognitionImageAction(@RequestBody FaceRecognitionImageActionPost o) {
        return Response.show(FaceStrageService.faceRecognitionImageAction(o.getType(), o.getPhoto(), o.getScheduleId(), o.getUserIds()));
    }

}
