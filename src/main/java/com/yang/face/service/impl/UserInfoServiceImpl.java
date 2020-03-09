package com.yang.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.yang.face.constant.enums.FaceFeatureTypeEnum;
import com.yang.face.constant.enums.SexTypeEnum;
import com.yang.face.constant.enums.UserTypeEnum;
import com.yang.face.entity.db.UserInfo;
import com.yang.face.mapper.UserInfoMapper;
import com.yang.face.service.UserInfoService;
import com.yang.face.service.yun.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableCaching
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    UserInfoMapper userInfoMapper;

    @Cacheable(cacheNames = "userInfos")
    @Override
    public List<UserInfo> selectAll() {
        //System.out.println("调用了db all");
        return userInfoMapper.selectAll();
    }

    @CacheEvict(value = "userInfos", allEntries = true)
    @Override
    public void clearSelectAllCache() {
    }

    @Override
    public List<UserInfo> search(String schoolId, String groupId, String userName, Integer pageIndex, Integer pageSize) {

        PageHelper.startPage(pageIndex, pageSize);

        return null;
    }


    @Cacheable(value = "userInfos", key = "#userId")
    @Override
    public UserInfo selectByUserId(String userId) {

        //System.out.println("调用了db userId");
        return userInfoMapper.selectOne(new UserInfo(userId));
    }

    @Override
    public UserInfo selectById(Integer id) {
        return null;
    }

    @Override
    public boolean updatePhtoto(String userId, String photo) {
        return false;
    }

    @Override
    public boolean deletePhoto(String userId) {
        return false;
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
    @Transactional
    public Boolean updateUserInfo() {

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
