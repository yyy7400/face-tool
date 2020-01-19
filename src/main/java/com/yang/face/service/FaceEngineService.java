package com.yang.face.service;

import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageInfo;
import com.yang.face.engine.FaceUserInfo;
import com.yang.face.engine.ProcessInfo;
import com.yang.face.entity.db.UserInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;


public interface FaceEngineService {

    /**
     * 人脸检测
     * @param imageInfo
     * @return
     */
    List<FaceInfo> detectFaces(ImageInfo imageInfo);

    /**
     * 提取性别和年龄
     * @param imageInfo
     * @return
     */
    List<ProcessInfo> process(ImageInfo imageInfo);

    /**
     * 人脸特征
     * @param imageInfo
     * @return
     */
    byte[] extractFaceFeature(ImageInfo imageInfo) throws InterruptedException;

    /**
     * 人脸比对
     * @param userInfos
     * @param faceFeature
     * @return
     */
    List<FaceUserInfo> compareFaceFeature(byte[] faceFeature, List<UserInfo> userInfos) throws InterruptedException, ExecutionException;

}
