package com.yang.face.entity.middle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangyuyang
 * @date 2020/3/11 17:19
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FaceScoreImageMod {

    /**
     * 分数(0 - 100)；综合计算人脸得分
     */
    private Integer score;

    /**
     * 是否人脸
     */
    private Boolean state;

    /**
     * 人脸占比评价得分
     */
    private Integer retioScore;

    /**
     * 是否人脸占比过滤
     */
    private Boolean retioState;

    /**
     * 预测人脸得分
     */
    private Integer faceScore;

    /**
     * 是否人脸得分过滤
     */
    private Boolean faceState;

    /**
     * 人脸位置评分得分
     */
    private Integer locScore;

    /**
     * 是否人脸位置过滤
     */
    private Boolean locState;

    /**
     * 照片清晰度评分得分
     */
    private Integer clearScore;

    /**
     * 是否照片清晰度过滤
     */
    private Boolean clearState;

    /**
     * 照片分辨率评分得分
     */
    private Integer pxScore;

    /**
     * 是否照片分辨率过滤
     */
    private Boolean pxState;


}
