package com.yang.face.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.yang.face.constant.Properties;
import com.yang.face.constant.enums.PhotoType;
import com.yang.face.engine.FaceUserInfo;
import com.yang.face.entity.db.UserInfo;
import com.yang.face.entity.midle.ByteFile;
import com.yang.face.entity.post.ImportFeaturePost;
import com.yang.face.entity.show.FaceRecoShow;
import com.yang.face.entity.show.ImportFeatrueShow;
import com.yang.face.entity.show.MessageVO;
import com.yang.face.mapper.UserInfoMapper;
import com.yang.face.service.FaceEngineService;
import com.yang.face.service.FaceService;
import com.yang.face.util.Base64Util;
import com.yang.face.util.NetUtil;
import com.yang.face.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yangyuyang
 */
@Service
public class FaceServiceImpl implements FaceService {

    private static final Logger logger = LoggerFactory.getLogger(FaceServiceImpl.class);

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private FaceEngineService faceEngineService;

    /**
     * 人脸识别, userIds.isEmpty() 时对比全部
     *
     * @param type
     * @param photo   目前只支持base64，url地址需要下载
     * @param userIds
     * @return
     */
    @Override
    public List<FaceRecoShow> recoImage(Integer type, String photo, List<String> userIds) {

        List<FaceRecoShow> list = new ArrayList<>();
        try {
            // 1. 图片转转byte
            ByteFile byteFile = getPhotoByteFile(type, photo);
            if (byteFile == null) {
                return list;
            }
            ImageInfo imageInfo = ImageFactory.getRGBData(byteFile.getBytes());

            // 2. 人脸特征获取
            byte[] bytes = faceEngineService.extractFaceFeature(imageInfo);
            if (bytes == null) {
                return list;
            }

            // 3. 从数据库中取出人脸库
            List<UserInfo> userInfoList = new ArrayList<>();
            if (userIds.isEmpty()) {
                userInfoList = userInfoMapper.selectAll();
            } else {
                Example example = new Example(UserInfo.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andIn("userId", userIds);
                userInfoList = userInfoMapper.selectByExample(example);
            }
            if (userInfoList.isEmpty())
                return list;

            // 4. 识别到的人脸列表
            List<FaceUserInfo> faces = faceEngineService.compareFaceFeature(bytes, userInfoList);

            for (FaceUserInfo o : faces) {
                list.add(new FaceRecoShow(o.getUserId(), o.getSimilarityScore(), o.getPhotoUrl()));
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return list;
    }

    /**
     * 清理人脸库特征
     *
     * @param userIds
     * @return
     */
    @Override
    public MessageVO cleanFeature(List<String> userIds) {

        try {
            Example example = new Example(UserInfo.class);
            Example.Criteria criteria = example.createCriteria();

            if (userIds.isEmpty()) {
                userInfoMapper.deleteByExample(example);
            } else {
                criteria.andIn("userId", userIds);
                userInfoMapper.deleteByExample(example);
            }
        } catch (Exception e) {

        }

        return null;
    }

    /**
     * 批量导入人脸库，并更新
     *
     * @param list
     * @return
     */
    @Override
    @Transactional
    public List<ImportFeatrueShow> importFeatures(List<ImportFeaturePost> list) {

        List<ImportFeatrueShow> res = new ArrayList<>();

        try {

            // 0. 获取所有已存在的用户
            List<UserInfo> users = userInfoMapper.selectAll();
            Map<String, UserInfo> userMap = users.stream().collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));

            List<UserInfo> usersAdd = new ArrayList<>();
            for (ImportFeaturePost o : list) {

                // 1. 图片转转byte
                ByteFile byteFile = getPhotoByteFile(o.getType(), o.getPhoto());
                if (byteFile == null) {
                    continue;
                }
                ImageInfo imageInfo = ImageFactory.getRGBData(byteFile.getBytes());


                // 2. 人脸特征获取
                byte[] bytes = faceEngineService.extractFaceFeature(imageInfo);
                if (bytes == null) {
                    res.add(new ImportFeatrueShow(o.getUserId(), "", o.getType(), "", "", false, "未检测到人脸"));
                    continue;
                } else {
                    res.add(new ImportFeatrueShow(o.getUserId(), "", o.getType(), "", "", true, ""));
                }

                // 3.保存图片
                String filePath = Properties.SERVER_RESOURCE_IMAGE_FEATRUE + byteFile.getFileName();
                if (!NetUtil.byte2File(byteFile.getBytes(), filePath)) {
                    filePath = "";
                }


                // 更新特征
                if (!userMap.containsKey(o.getUserId())) {
                    usersAdd.add(new UserInfo(o.getUserId(), o.getType(), o.getUserId(), 0, 0, PathUtil.getRelPath(filePath), bytes, DateUtil.date(), DateUtil.date()));
                } else {
                    Example example = new Example(UserInfo.class);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("userId", o.getUserId());

                    UserInfo userInfo = new UserInfo(null, null, null, null, null, PathUtil.getRelPath(filePath), bytes, null, DateUtil.date());
                    userInfoMapper.updateByExampleSelective(userInfo, example);
                }
            }

            // 批量插入特征, 每次插入100条
            if (!usersAdd.isEmpty()) {
                int index = usersAdd.size() / 100;
                for (int i = 0; i <= index; i++) {
                    //stream流表达式，skip表示跳过前i*100条记录，limit表示读取当前流的前100条记录
                    userInfoMapper.insertList(usersAdd.stream().skip(i * 100).limit(100).collect(Collectors.toList()));
                }
            }


        } catch (Exception e) {
            logger.error("", e);
        }

        return res;
    }

    /**
     * 更新人脸库特征
     *
     * @return
     */
    @Override
    public MessageVO updateFeatures() {
        return null;
    }

    public ByteFile getPhotoByteFile(int type, String srcPhoto) throws IOException {
        // url
        ByteFile byteFile = new ByteFile();
        if (type == PhotoType.IMAGE.getKey()) {
            byteFile.setBytes(NetUtil.image2byte(srcPhoto));
            byteFile.setExt(srcPhoto.substring(srcPhoto.lastIndexOf('.')));
            byteFile.setFileName(DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_MS_PATTERN) + byteFile.getExt());
            return byteFile;
        } else if (type == PhotoType.BASE64.getKey()) {
            String dstPhotoExt = ".jpg";

            byteFile.setBytes(Base64.decode(Base64Util.base64Process(srcPhoto)));
            byteFile.setExt(".jpg");
            byteFile.setFileName(DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_MS_PATTERN) + dstPhotoExt);
            return byteFile;
        }

        return null;
    }


}
