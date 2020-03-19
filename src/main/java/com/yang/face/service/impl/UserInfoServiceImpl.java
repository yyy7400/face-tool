package com.yang.face.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yang.face.constant.Constants;
import com.yang.face.constant.Properties;
import com.yang.face.constant.enums.*;
import com.yang.face.entity.db.UserInfo;
import com.yang.face.entity.post.ImportFeaturePost;
import com.yang.face.entity.show.*;
import com.yang.face.mapper.UserInfoMapper;
import com.yang.face.service.*;
import com.yang.face.service.yun.*;
import com.yang.face.util.DateTimeUtil;
import com.yang.face.util.FileUtil;
import com.yang.face.util.PathUtil;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yang
 */
@Service
@EnableCaching
public class UserInfoServiceImpl implements UserInfoService {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    @Resource
    UserInfoMapper userInfoMapper;
    @Resource
    FaceStrageService faceStrageService;

    @Cacheable(cacheNames = "userInfos")
    @Override
    public List<UserInfo> selectAll() {
        System.out.println("调用了db all");
        return userInfoMapper.selectAll();
    }

    @CacheEvict(value = "userInfos", allEntries = true)
    @Override
    public void clearSelectAllCache() {
    }


    @Override
    public PageShow search(String groupId, String gradeId, String classId, Integer userType, String userName, Integer pageIndex, Integer pageSize) {

        Example example = new Example(UserInfo.class);
        Example.Criteria criteria = example.createCriteria();

        if (userType >= 0) {
            criteria.andEqualTo("userType", userType);
        }
        if (!StringUtils.isEmpty(groupId)) {
            criteria.andEqualTo("groupId", groupId);
        }
        if (!StringUtils.isEmpty(gradeId)) {
            criteria.andEqualTo("gradeId", gradeId);
        }
        if (!StringUtils.isEmpty(classId)) {
            criteria.andEqualTo("groupId", classId);
        }
        if (!StringUtils.isEmpty(userName)) {
            criteria.andLike("userName", "%" + userName + "%");
        }

        PageHelper.startPage(pageIndex, pageSize);
        List<UserInfo> list = userInfoMapper.selectByExample(example);
        PageInfo<UserInfo> pageInfo = new PageInfo<>(list);

        return new PageShow(pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getList());
    }


    @Cacheable(value = "userInfos", key = "#userId")
    @Override
    public UserInfo selectByUserId(String userId) {
        //System.out.println("调用了db userId");
        return userInfoMapper.selectOne(new UserInfo(userId));
    }

    @Override
    public UserInfo selectById(Integer id) {
        Example example = new Example(UserInfo.class);
        example.createCriteria().andEqualTo("id", id);

        return userInfoMapper.selectOneByExample(example);
    }

    @Override
    public MessageVO updatePhoto(String userId, String photo) {

        List<ImportFeaturePost> list = new ArrayList<>();
        list.add( new ImportFeaturePost(userId, PhotoTypeEnum.IMAGE.getKey(), PathUtil.getUrl(photo)));

        List<ImportFeatureShow> res = faceStrageService.importFeatures(list);
        if(!res.isEmpty() && res.get(0).getState()) {
            return new MessageVO(MessageEnum.SUCCESS, res.size());
        } else {
            return new MessageVO(MessageEnum.FAIL, res.get(0).getMsg());
        }

    }

    @Override
    public MessageVO deletePhoto(String userId) {

        List<String> userIds = Arrays.asList(userId);
        return faceStrageService.cleanFeatureUpdate(userIds);
    }

    @Override
    public MessageVO deleteUser(Integer userType, String userId) {

        if(userType < 0 && "".equals(userId)) {
            return new MessageVO(false, "参数错误");
        }

        Example example = new Example(UserInfo.class);
        Example.Criteria criteria = example.createCriteria();
        if(userType >= 0) {
            criteria.andEqualTo("userType",userType);
        }

        if(!"".equals(userId)) {
            criteria.andEqualTo("userId",userId);
        }
        List<UserInfo> userInfos = userInfoMapper.selectByExample(example);
        if(userInfos.isEmpty()) {
            return new MessageVO(false, "无用户可删除");
        }

        List<String> userIds = new ArrayList<>();
        userInfos.forEach(o -> userIds.add(o.getUserId()));

        return faceStrageService.cleanFeature(userIds);
    }

    @Override
    public List<UserInfo> selectByUserIds(List<String> userIds, List<UserInfo> list) {

        List<UserInfo> res = new ArrayList<>();

        Map<String, UserInfo> map = list.stream().collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));
        for (String userId : userIds) {
            if (map.containsKey(userId)) {
                res.add(map.get(userId));
            }
        }

        return res;
    }

    @Override
    public FacePathShow getPhotoFromYun(String token, String userId, Integer userType) {
        FacePathShow fPath = new FacePathShow();
        try {
            // 默认是学生
            if (userType == null) {
                userType = UserTypeEnum.STUDENT.getKey();
            }

            String photoYun = "";
            if (userType.equals(UserTypeEnum.STUDENT.getKey())) {
                List<StudentStruct> list = new StudentStructUtil().getStuDetailInfo(token, userId);
                if (list == null || list.size() == 0) {
                    return fPath;
                }

                photoYun = list.get(0).getPhotoPath();
            } else if (userType.equals(UserTypeEnum.TEACHER.getKey())) {
                List<TeacherStruct> list = new TeacherStructUtil().getTeaDetailInfo(token, userId);
                if (list == null || list.size() == 0) {
                    return fPath;
                }

                photoYun = list.get(0).getPhotoPath();
            } else if (userType.equals(UserTypeEnum.ADMIN_BUREAU.getKey())
                    || userType.equals(UserTypeEnum.ADMIN.getKey())) {
                List<AdminStruct> list = new AdminStructUtil().getAdminDetailInfo(token, userId);
                if (list == null || list.size() == 0) {
                    return fPath;
                }

                photoYun = list.get(0).getPhotoPath();
            } else {
                return fPath;
            }

            String dirSave = Properties.SERVER_RESOURCE + Constants.Dir.IMAGE_YUN;
            String path = FileUtil.downloadUrl(photoYun, dirSave);

            if (path.isEmpty()) {
                return fPath;
            }

            // 重基础平台下载的头像为png,face opencv无法读取，需要另存为jpg
            BufferedImage bufferedImage = ImageIO.read(new File(path));
            BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
            String pathNew = path.substring(0, path.lastIndexOf(".")) + ".jpg";
            ImageIO.write(newBufferedImage, "jpg", new File(pathNew));

            // 生成缩略图
            String iconPathNew = pathNew.substring(0, path.lastIndexOf(".")) + "_c"
                    + pathNew.substring(pathNew.lastIndexOf("."));

            // 获取截图宽高
            String photoAbs = PathUtil.getAbsPath(pathNew);
            Thumbnails.Builder<File> fileBuilder = Thumbnails.of(photoAbs).scale(1.0).outputQuality(1.0);
            BufferedImage src = fileBuilder.asBufferedImage();
            int size = Math.min(src.getWidth(), src.getHeight());

            fileBuilder.toFile(photoAbs);
            Thumbnails.of(photoAbs).sourceRegion(Positions.CENTER, size, size).outputQuality(1.0).size(300, 300)
                    .toFile(PathUtil.getAbsPath(iconPathNew));

            String path2 = PathUtil.getRelPath(pathNew);
            fPath.setPath(path2);
            fPath.setUrl(PathUtil.getUrl(path2));

            // png图片才删除原图
            if (!pathNew.equals(path)) {
                new File(path).delete();
            }

            return fPath;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return fPath;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateYunUserInfo() {

        // 1 从基础平台获取用户
        String token = new YunToken().getTokenAdmin();
        List<AdminStruct> adminsYun = new AdminStructUtil().getAdminDetailInfo(token);
        List<TeacherStruct> teachersYun = new TeacherStructUtil().getTeaDetailInfo(token);
        List<StudentStruct> studentsYun = new StudentStructUtil().getStuDetailInfo(token);
        Map<String, ClassStruct> classMap = new HashMap<>();

        for (TeacherStruct o : teachersYun) {
            // 100000 —普通教师； 110000 —任课教师； 101000 —班主任； 100100 —教研者； 100010 —学科主管； 100001 校领导
            if ("1".equals(o.getUserClass().substring(2, 3))) {
                List<ClassStruct> temp = new SchoolInfoStructUtil().getClassInfoByUser(token, o.getUserID());
                if (!temp.isEmpty()) {
                    classMap.put(o.getUserID(), temp.get(0));
                }
            }
        }

        // 2 转换为userInfo格式,，转化为hashmap，通过hashmap遍历，时间复杂度为O(n)
        Map<String, UserInfo> mapYun = getUserInfo(adminsYun, teachersYun, studentsYun, classMap);

        // 3 获取userInfo用户数据库
        Map<String, UserInfo> mapDB = new HashMap<>();
        Example example = new Example(UserInfo.class);
        example.createCriteria().andLessThan("userType", UserTypeEnum.OTHER.getKey());
        List<UserInfo> usersDb = userInfoMapper.selectByExample(example);
        for (UserInfo obj : usersDb) {
            mapDB.put(obj.getUserId(), obj);
        }

        // 4 更新User_Info数据表
        for (Map.Entry<String, UserInfo> entry : mapYun.entrySet()) {
            UserInfo o = entry.getValue();
            UserInfo u = mapDB.get(o.getUserId());

            // 4.1 更新已存在的用户
            if (mapDB.containsKey(o.getUserId())) {
                if (!o.getUserName().equals(u.getUserName()) || !o.getUserType().equals(u.getUserType()) || !o.getSex().equals(u.getSex())
                        || !o.getGradeId().equals(u.getGradeId()) || !o.getGradeName().equals(u.getGradeName())
                        || !o.getClassId().equals(u.getClassId()) || !o.getClassName().equals(u.getClassName())
                        || !o.getGroupId().equals(u.getGroupId()) || !o.getGroupName().equals(u.getGroupName()) || !o.getPhotoUrl().equals(u.getPhotoUrl())) {

                    o.setId(u.getId());
                    userInfoMapper.updateByPrimaryKey(o);
                }

                // 4.2 添加新用户
            } else {
                userInfoMapper.insert(o);
            }
        }

        // 4.3 删除基础平台不存在的用户
        for (Map.Entry<String, UserInfo> entry : mapDB.entrySet()) {
            if (mapYun.containsKey(entry.getKey())) {
                continue;
            }
            userInfoMapper.deleteByPrimaryKey(entry.getValue().getId());
        }

        return true;
    }

    @Override
    public MessageVO resetUserInfo() {

        userInfoMapper.turncateTable();
        boolean state = updateYunUserInfo();
        faceStrageService.updateFeatures();

        return state ? new MessageVO(MessageEnum.SUCCESS)
                : new MessageVO(MessageEnum.FAIL);
    }


    @Override
    public List<TeacherGroup> getGroupInfo(String token) {

        List<TeacherGroup> list = new ArrayList<>();
        List<TeacherGroupStruct> teachers = new TeacherStructUtil().getTeaGroupInfo(token, "");
        for (TeacherGroupStruct o : teachers) {
            list.add(new TeacherGroup(o.getGroupID(), o.getGroupName(), o.getSchoolID()));
        }

        return list;
    }

    @Override
    public List<PubStudentFeatureShow> getStudentFeature(Boolean hasFeature, Date photoTime) {

        List<PubStudentFeatureShow> resList = new ArrayList<>();

        try {
            Example example = new Example(UserInfo.class);
            Example.Criteria criteria = example.createCriteria()
                    .andEqualTo("userType",UserTypeEnum.STUDENT.getKey())
                    .andGreaterThan("updateTime", photoTime);
            if(hasFeature) {
                criteria.andGreaterThan("score",0);
            }


            List<UserInfo> list = userInfoMapper.selectByExample(example);
            int size = list.size();

            for (int i = 0; i < size; i++) {

                PubStudentFeatureShow res = new PubStudentFeatureShow();
                res.setUserId(list.get(i).getUserId());
                res.setUserName(list.get(i).getUserName());
                res.setUserPhotoYun(PathUtil.getUrl(list.get(i).getPhotoUrl()));
                res.setSex(list.get(i).getSex());
                res.setGradeId(list.get(i).getGradeId());
                res.setGradeName(list.get(i).getGradeName());
                res.setClassId(list.get(i).getClassId());
                res.setClassName(list.get(i).getClassName());

                if (list.get(i).getScore() > 0 ) {
                    // 没有特征
                    if (hasFeature) {
                        continue;
                    }

                    res.setPhotoUrl("");
                    res.setPhotoIconUrl("");
                    res.setPhotoTime(new Date());

                } else {
                    String str = list.get(i).getPhotoUrl();
                    String[] strList = str.split("_");
                    String noIconStr = strList[0] + ".jpg";
                    res.setPhotoUrl(PathUtil.getUrl(noIconStr));
                    res.setPhotoIconUrl(PathUtil.getUrl(str));
                    res.setPhotoTime(list.get(i).getUpdateTime());

                }
                resList.add(res);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return resList;
    }

    @Override
    public List<PubTeacherFeatureShow> getTeacherFeature(Boolean hasFeature, Date photoTime) {

        List<PubTeacherFeatureShow> resList = new ArrayList<>();

        try {
            Example example = new Example(UserInfo.class);
            Example.Criteria criteria = example.createCriteria()
                    .andEqualTo("userType",UserTypeEnum.TEACHER.getKey())
                    .andGreaterThan("updateTime", photoTime);
            if(hasFeature) {
                criteria.andGreaterThan("score",0);
            }


            List<UserInfo> list = userInfoMapper.selectByExample(example);
            int size = list.size();

            for (int i = 0; i < size; i++) {

                PubTeacherFeatureShow res = new PubTeacherFeatureShow();
                res.setUserId(list.get(i).getUserId());
                res.setUserName(list.get(i).getUserName());
                res.setUserPhotoYun(PathUtil.getUrl(list.get(i).getPhotoUrl()));
                res.setSex(list.get(i).getSex());
                res.setGroupId(list.get(i).getGradeId());
                res.setGroupName(list.get(i).getGradeName());

                if (list.get(i).getScore() > 0 ) {
                    // 没有特征
                    if (hasFeature) {
                        continue;
                    }

                    res.setPhotoUrl("");
                    res.setPhotoIconUrl("");
                    res.setPhotoTime(new Date());

                } else {
                    String str = list.get(i).getPhotoUrl();
                    String[] strList = str.split("_");
                    String noIconStr = strList[0] + ".jpg";
                    res.setPhotoUrl(PathUtil.getUrl(noIconStr));
                    res.setPhotoIconUrl(PathUtil.getUrl(str));

                    res.setPhotoTime(list.get(i).getUpdateTime());

                }
                resList.add(res);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return resList;
    }

    @Override
    public List<PubAdminFeatureShow> getAdminFeature(Boolean hasFeature, Date photoTime) {
        List<PubAdminFeatureShow> resList = new ArrayList<>();

        try {
            Example example = new Example(UserInfo.class);
            Example.Criteria criteria = example.createCriteria()
                    .andEqualTo("userType",UserTypeEnum.ADMIN.getKey())
                    .andGreaterThan("updateTime", photoTime);
            if(hasFeature) {
                criteria.andGreaterThan("score",0);
            }


            List<UserInfo> list = userInfoMapper.selectByExample(example);
            int size = list.size();

            for (int i = 0; i < size; i++) {

                PubAdminFeatureShow res = new PubAdminFeatureShow();
                res.setUserId(list.get(i).getUserId());
                res.setUserName(list.get(i).getUserName());
                res.setUserPhotoYun(PathUtil.getUrl(list.get(i).getPhotoUrl()));
                res.setSex(list.get(i).getSex());

                if (list.get(i).getScore() > 0 ) {
                    // 没有特征
                    if (hasFeature) {
                        continue;
                    }

                    res.setPhotoUrl("");
                    res.setPhotoIconUrl("");
                    res.setPhotoTime(new Date());

                } else {
                    String str = list.get(i).getPhotoUrl();
                    String[] strList = str.split("_");
                    String noIconStr = strList[0] + ".jpg";
                    res.setPhotoUrl(PathUtil.getUrl(noIconStr));
                    res.setPhotoIconUrl(PathUtil.getUrl(str));

                    res.setPhotoTime(list.get(i).getUpdateTime());

                }
                resList.add(res);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return resList;
    }

    @Override
    public List<PubAdminFeatureShow> getOtherFeature(Boolean hasFeature, Date photoTime) {
        List<PubAdminFeatureShow> resList = new ArrayList<>();

        try {
            Example example = new Example(UserInfo.class);
            Example.Criteria criteria = example.createCriteria()
                    .andEqualTo("userType",UserTypeEnum.OTHER.getKey())
                    .andGreaterThan("updateTime", photoTime);
            if(hasFeature) {
                criteria.andGreaterThan("score",0);
            }


            List<UserInfo> list = userInfoMapper.selectByExample(example);
            int size = list.size();

            for (int i = 0; i < size; i++) {

                PubAdminFeatureShow res = new PubAdminFeatureShow();
                res.setUserId(list.get(i).getUserId());
                res.setUserName(list.get(i).getUserName());
                res.setUserPhotoYun(PathUtil.getUrl(list.get(i).getPhotoUrl()));
                res.setSex(list.get(i).getSex());

                if (list.get(i).getScore() > 0 ) {
                    // 没有特征
                    if (hasFeature) {
                        continue;
                    }

                    res.setPhotoUrl("");
                    res.setPhotoIconUrl("");
                    res.setPhotoTime(new Date());

                } else {
                    String str = list.get(i).getPhotoUrl();
                    String[] strList = str.split("_");
                    String noIconStr = strList[0] + ".jpg";
                    res.setPhotoUrl(PathUtil.getUrl(noIconStr));
                    res.setPhotoIconUrl(PathUtil.getUrl(str));

                    res.setPhotoTime(list.get(i).getUpdateTime());

                }
                resList.add(res);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return resList;
    }

    @Override
    public List<PubUserPhotoShow> getPhotoByUserIds(List<String> userIds) {


        List<PubUserPhotoShow> resList = new ArrayList<>();

        Example example = new Example(UserInfo.class);
        example.createCriteria().andIn("userId", userIds);
        List<UserInfo> userInfos = userInfoMapper.selectByExample(example);

        try {
            // SqlServer 对语句的条数和参数的数量都有限制，分别是 1000 和 2100。
            // Mysql 对语句的长度有限制，默认是 4M。
            // 此处分每1000个id查询一下

            for (UserInfo o : userInfos) {
                if (o.getScore() <= 0 ) {
                    resList.add(new PubUserPhotoShow(o.getUserId(), "", "", new Date()));
                } else {
                    resList.add(new PubUserPhotoShow(o.getUserId(), PathUtil.getUrl(o.getPhotoUrl()), "", null));
                }
            }

            // 补photoUrl和phototime
            for (int i = 0; i < resList.size(); i++) {

                if ("".equals(resList.get(i).getPhotoIconUrl())) {
                    continue;
                }

                String str = resList.get(i).getPhotoIconUrl();
                String[] strList = str.split("_");
                String noIconStr = strList[0] + ".jpg";
                resList.get(i).setPhotoUrl(PathUtil.getUrl(noIconStr));

                String[] tmp = strList[0].split("\\/");
                String strDate = tmp[tmp.length - 1];
                Date date = DateUtil.parse(strDate, DatePattern.PURE_DATETIME_MS_PATTERN);
                resList.get(i).setPhotoTime(date);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return resList;
    }

    // 私有方法  yyy
    private Map<String, UserInfo> getUserInfo(List<AdminStruct> adminsYun, List<TeacherStruct> teachersYun
            , List<StudentStruct> studentsYun, Map<String, ClassStruct> classMap) {

        Map<String, UserInfo> map = new HashMap<>();

        Date date = new Date();

        for (AdminStruct obj : adminsYun) {
            Integer sex = "男".equals(obj.getGender()) ? SexTypeEnum.MALE.getKey()
                    : "女".equals(obj.getGender()) ? SexTypeEnum.FEMALE.getKey() : SexTypeEnum.UNKNOWN.getKey();

            UserInfo userInfo = new UserInfo(null, obj.getUserID(), obj.getUserName(), UserTypeEnum.ADMIN.getKey()
                    , sex, "", "", "", "", "", "", obj.getPhotoPath()
                    , FaceFeatureTypeEnum.NONE.getKey(), "".getBytes(), "", 0, date, date);
            map.put(obj.getUserID(), userInfo);
        }

        for (TeacherStruct obj : teachersYun) {
            Integer sex = "男".equals(obj.getGender()) ? SexTypeEnum.MALE.getKey()
                    : "女".equals(obj.getGender()) ? SexTypeEnum.FEMALE.getKey() : SexTypeEnum.UNKNOWN.getKey();
            String classId = "";
            String className = "";

            if (classMap.containsKey(obj.getUserID())) {
                classId = classMap.get(obj.getUserID()).getClassID();
                className = classMap.get(obj.getUserID()).getClassName();
            }

            UserInfo userInfo = new UserInfo(null, obj.getUserID(), obj.getUserName(), UserTypeEnum.TEACHER.getKey()
                    , sex, "", "", classId, className, obj.getGroupID(), obj.getGroupName(), obj.getPhotoPath()
                    , FaceFeatureTypeEnum.NONE.getKey(), "".getBytes(), "", 0, date, date);
            map.put(obj.getUserID(), userInfo);
        }

        for (StudentStruct obj : studentsYun) {
            Integer sex = "男".equals(obj.getGender()) ? SexTypeEnum.MALE.getKey()
                    : "女".equals(obj.getGender()) ? SexTypeEnum.FEMALE.getKey() : SexTypeEnum.UNKNOWN.getKey();

            UserInfo userInfo = new UserInfo(null, obj.getUserID(), obj.getUserName(), UserTypeEnum.STUDENT.getKey()
                    , sex, obj.getGradeID(), obj.getGradeName(), obj.getClassID(), obj.getClassName(), "", "", obj.getPhotoPath()
                    , FaceFeatureTypeEnum.NONE.getKey(), "".getBytes(), "", 0, date, date);
            map.put(obj.getUserID(), userInfo);
        }

        return map;
    }

}
