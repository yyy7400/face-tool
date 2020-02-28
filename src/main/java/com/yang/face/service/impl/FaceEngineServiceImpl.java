package com.yang.face.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.toolkit.ImageInfo;
import com.google.common.collect.Lists;
import com.yang.face.engine.FaceUserInfo;
import com.yang.face.engine.ProcessInfo;
import com.yang.face.entity.db.UserInfo;
import com.yang.face.service.FaceEngineFactory;
import com.yang.face.service.FaceEngineService;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author yangyuyang
 */
@Service
public class FaceEngineServiceImpl implements FaceEngineService {

    public final static Logger logger = LoggerFactory.getLogger(FaceEngineServiceImpl.class);

    //@Value("${config.arcface-sdk.sdk-lib-path}")
    //public String sdkLibPath;
    @Value("${config.arcface-sdk.app-id}")
    public String appId;

    @Value("${config.arcface-sdk.sdk-key}")
    public String sdkKey;

    @Value("${config.arcface-sdk.thread-pool-size}")
    public Integer threadPoolSize;
    @Value("${config.arcface-sdk.passRate}")
    private Integer passRate;

    private ThreadPoolExecutor threadPoolExecutor;

    private GenericObjectPool<FaceEngine> faceEngineObjectPool;


    @PostConstruct
    public void init() {

        String jarDir = ClassUtils.getDefaultClassLoader().getResource("").getPath().substring(1);
        String sdkLibPath = jarDir + "lib";

        // alibaba 推荐手动重建线程池，以便了解线程池运行机制，规避资源耗尽的风险
        threadPoolExecutor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5));
        //threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize)
        //executorService = Executors.newFixedThreadPool(threadPoolSize);
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(threadPoolSize);
        poolConfig.setMaxTotal(threadPoolSize);
        poolConfig.setMinIdle(threadPoolSize);
        poolConfig.setLifo(false);

        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_0_ONLY);

        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);

        //底层库算法对象池
        faceEngineObjectPool = new GenericObjectPool(new FaceEngineFactory(sdkLibPath, appId, sdkKey, engineConfiguration), poolConfig);

    }

    /**
     * 人脸检测
     *
     * @param imageInfo
     * @return
     */
    @Override
    public List<FaceInfo> detectFaces(ImageInfo imageInfo) {
        FaceEngine faceEngine = null;
        try {
            //获取引擎对象
            faceEngine = faceEngineObjectPool.borrowObject();

            //人脸检测得到人脸列表
            List<FaceInfo> faceInfoList = new ArrayList<>();

            //人脸检测
            faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
            return faceInfoList;
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (faceEngine != null) {
                //释放引擎对象
                faceEngineObjectPool.returnObject(faceEngine);
            }
        }
        return null;
    }

    /**
     * 提取性别和年龄
     *
     * @param imageInfo
     * @return
     */
    @Override
    public List<ProcessInfo> process(ImageInfo imageInfo) {

        List<ProcessInfo> processInfoList = Lists.newLinkedList();
        FaceEngine faceEngine = null;

        try {
            //获取引擎对象
            faceEngine = faceEngineObjectPool.borrowObject();
            //人脸检测得到人脸列表
            List<FaceInfo> faceInfoList = new ArrayList<>();
            //人脸检测
            faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
            int processResult = faceEngine.process(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList, FunctionConfiguration.builder().supportAge(true).supportGender(true).build());

            List<GenderInfo> genderInfoList = new ArrayList<>();
            //性别提取
            int genderCode = faceEngine.getGender(genderInfoList);
            //年龄提取
            List<AgeInfo> ageInfoList = new ArrayList<>();
            int ageCode = faceEngine.getAge(ageInfoList);
            for (int i = 0; i < genderInfoList.size(); i++) {
                ProcessInfo processInfo = new ProcessInfo();
                processInfo.setGender(genderInfoList.get(i).getGender());
                processInfo.setAge(ageInfoList.get(i).getAge());
                processInfoList.add(processInfo);
            }

        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (faceEngine != null) {
                //释放引擎对象
                faceEngineObjectPool.returnObject(faceEngine);
            }
        }

        return processInfoList;
    }

    /**
     * 人脸特征
     *
     * @param imageInfo
     * @return
     */
    @Override
    public byte[] extractFaceFeature(ImageInfo imageInfo) throws InterruptedException {

        FaceEngine faceEngine = null;

        try {
            //获取引擎对象
            faceEngine = faceEngineObjectPool.borrowObject();

            //人脸检测得到人脸列表
            List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();

            //人脸检测
            int i = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);

            if (CollectionUtil.isNotEmpty(faceInfoList)) {
                FaceFeature faceFeature = new FaceFeature();
                //提取人脸特征
                faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);

                return faceFeature.getFeatureData();
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (faceEngine != null) {
                //释放引擎对象
                faceEngineObjectPool.returnObject(faceEngine);
            }
        }

        return null;
    }

    /**
     * 特征对比
     *
     * @param faceFeature
     * @param userInfoList
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public List<FaceUserInfo> compareFaceFeature(byte[] faceFeature, List<UserInfo> userInfoList) throws InterruptedException, ExecutionException {
        //识别到的人脸列表
        List<FaceUserInfo> resultFaceInfoList = Lists.newLinkedList();

        FaceFeature targetFaceFeature = new FaceFeature();
        targetFaceFeature.setFeatureData(faceFeature);

        //分成1000一组，多线程处理
        List<List<UserInfo>> userInfoPartList = Lists.partition(userInfoList, 1000);
        CompletionService<List<FaceUserInfo>> completionService = new ExecutorCompletionService(threadPoolExecutor);
        for (List<UserInfo> part : userInfoPartList) {
            completionService.submit(new CompareFaceTask(part, targetFaceFeature));
        }
        for (int i = 0; i < userInfoPartList.size(); i++) {
            List<FaceUserInfo> faceUserInfoList = completionService.take().get();
            if (CollectionUtil.isNotEmpty(userInfoList)) {
                resultFaceInfoList.addAll(faceUserInfoList);
            }
        }

        //从大到小排序
        resultFaceInfoList.sort((h1, h2) -> h2.getSimilarityScore().compareTo(h1.getSimilarityScore()));

        return resultFaceInfoList;
    }

    private int plusHundred(Float value) {
        BigDecimal target = new BigDecimal(value);
        BigDecimal hundred = new BigDecimal(100f);
        return target.multiply(hundred).intValue();

    }

    private class CompareFaceTask implements Callable<List<FaceUserInfo>> {

        private List<UserInfo> userInfoList;
        private FaceFeature targetFaceFeature;


        public CompareFaceTask(List<UserInfo> userInfoList, FaceFeature targetFaceFeature) {
            this.userInfoList = userInfoList;
            this.targetFaceFeature = targetFaceFeature;
        }

        @Override
        public List<FaceUserInfo> call() throws Exception {
            FaceEngine faceEngine = null;
            //识别到的人脸列表
            List<FaceUserInfo> resultFaceInfoList = Lists.newLinkedList();
            try {
                faceEngine = faceEngineObjectPool.borrowObject();
                for (UserInfo userInfo : userInfoList) {
                    FaceFeature sourceFaceFeature = new FaceFeature();
                    sourceFaceFeature.setFeatureData(userInfo.getFaceFeature());
                    FaceSimilar faceSimilar = new FaceSimilar();
                    faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
                    //获取相似值
                    Integer similarValue = plusHundred(faceSimilar.getScore());
                    //相似值大于配置预期，加入到识别到人脸的列表
                    if (similarValue > passRate) {

                        FaceUserInfo info = new FaceUserInfo();
                        info.setId(userInfo.getId());
                        info.setUserId(userInfo.getUserId());
                        info.setSex(userInfo.getSex());
                        info.setAge(userInfo.getAge());
                        // 转为url地址
                        info.setPhotoUrl(userInfo.getPhotoUrl());
                        info.setFaceFeature(userInfo.getFaceFeature());
                        info.setCreateTime(userInfo.getCreateTime());
                        info.setUpdateTime(userInfo.getUpdateTime());
                        info.setSimilarityScore(similarValue);
                        resultFaceInfoList.add(info);
                    }
                }
            } catch (Exception e) {
                logger.error("", e);
            } finally {
                if (faceEngine != null) {
                    faceEngineObjectPool.returnObject(faceEngine);
                }
            }

            return resultFaceInfoList;
        }

    }
}
