package com.yang.face.controller;

import com.yang.face.entity.post.ImportFeaturePost;
import com.yang.face.entity.post.RecoImageWithUserPost;
import com.yang.face.entity.post.TypeAndPhotoPost;
import com.yang.face.entity.post.UserIdsPost;
import com.yang.face.entity.show.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangyuyang
 */
@RestController
public class FaceController {

    //近距离人脸识别（电子班牌）
    @RequestMapping(value = "/face/recoImage", method = RequestMethod.POST)
    public Response recoImage(@RequestBody @Validated TypeAndPhotoPost o) {
        List<String> users = new ArrayList<>();
        return Response.show("");
        //return new UnifiedShow(new FaceServiceImpl().RecoImage(o.getType(), o.getPhoto(), users));
    }

    //人脸识别（指定用户）
    @RequestMapping(value = "/face/recoImageWithUser", method = RequestMethod.POST)
    public Response recoImageWithUser(@RequestBody @Validated RecoImageWithUserPost o) {
        return Response.show("");
        //return new UnifiedShow(new FaceServiceImpl().RecoImage(o.getType(), o.getPhoto(), o.getUserIds()));
    }

    //清理特征库
    @RequestMapping(value = "/face/cleanFeature", method = RequestMethod.GET)
    public Response cleanFeature() {
        return Response.show("");

        //return new UnifiedShow(new FaceServiceImpl().cleanFeature());
    }

    //单张图片导入特征库（无更新特征库操作）
    @RequestMapping(value = "/face/importFeatures", method = RequestMethod.POST)
    public Response importFeatures(@RequestBody List<ImportFeaturePost> list) {
        return Response.show("");

        //return new UnifiedShow(new FaceServiceImpl().importFeatures(list));
    }

    //批量导入特征库
    @Deprecated
    @RequestMapping(value = "/face/importFeaturesNoUpdate", method = RequestMethod.POST)
    public Response importFeaturesNoUpdate(@RequestBody List<ImportFeaturePost> list) {
        return Response.show("");

        //return new UnifiedShow(new FaceServiceImpl().importFeaturesNoUpdate(list));
    }

    //更新特征库
    @RequestMapping(value = "/face/importFeaturesUpdateFeatures", method = RequestMethod.POST)
    public Response importFeaturesUpdateFeatures(@RequestBody List<String> list) {
        return Response.show("");

        //return  Response.show("");return new UnifiedShow(new FaceServiceImpl().importFeaturesUpdateFeatures(list));
    }

    //清理特征库by user
    @RequestMapping(value = "/face/cleanFeatureByUser", method = RequestMethod.POST)
    public Response cleanFeatureByUser(@RequestBody UserIdsPost o) {
        return Response.show("");

        //return new UnifiedShow(new FaceServiceImpl().cleanFeature(o.getUserIds()));
    }
}
