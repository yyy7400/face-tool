> 人脸识别服务 内部接口

服务器地址，从基础平台获取子系统E27的WsSvrAddr地址

## 0 约定

### 0.1 输出结果结构统一如下：
```
- timestamp String 服务器时间
- status Integer 状态
- msg String 接口请求信息
- data Object 数据体
```

其中输出结果中status字段说明：
```
- 0: 正常；
- 1：无数据；
- 2：非法操作；
- 3：非法参数；
- 4：操作失败；
```

## 1 系统配置

### 1.1 获取系统当前配置信息

### 地址

	GET  /systemSetting/get

### 参数

```
无
```

### 输出

```
- id，Integer，可忽略
- faceType，Integer，系统当前使用的人脸识别引擎，1虹软, 2OpenVINO
```

### 1.2 人脸识别引擎更改为虹软

### 地址

	GET  /systemSetting/changeArc

### 参数

```
无
```

### 输出

```
- state，bool，是否操作成功
- msg，String，操作信息
```

### 1.3 人脸识别引擎更改为OpenVINO

### 地址

	GET  /systemSetting/changeOpenVINO

### 参数

```
无
```

### 输出

```
- state，bool，是否操作成功
- msg，String，操作信息
```

### 1.4 判断电子锁是否正常

### 地址

	GET  /Locker/checkLocker

### 参数

```
无
```

### 输出

```
Integer ,大于等于0正常, 否则非正常
```

### 1.5 获取基础平台地址

### 地址

	GET  /mainServerAddr/getMainServerAddr

### 参数

```
无
```

### 输出

```
string , 基础平台地址
```

### 1.6 判断用户是否在线

### 地址

	GET  /mainServerAddr/WS_UserMgr_G_IsOnline

### 参数

```
- token，string
```

### 输出

```
string ,用户信息和登录状态， 具体如何解析可参考之前的前端代码
```

### 1.7 判断用户是否在线

### 地址

	GET  /mainServerAddr/WS_UserMgr_G_IsOnline

### 参数

```
- token，string
```

### 输出

```
string ,用户信息和登录状态， 具体如何解析可参考之前的前端代码
```

### 1.8 获取用户信息

### 地址

	GET  /mainServerAddr/WS_UserMgr_G_GetAdmin

### 参数

```
- token，string
```

### 输出

```
string ,用户信息和登录状态， 具体如何解析可参考之前的前端代码
```

```
string ,用户信息和登录状态， 具体如何解析可参考之前的前端代码
```

### 1.9 登出系统

### 地址

	GET  /mainServerAddr/loginOut

### 参数

```
- token，string
```

### 输出

```
string ,用户信息和登录状态，具体如何解析可参考之前的前端代码
```


## 2 人脸库接口

### 2.1 根据条件搜索学生  

### 地址

	GET  /userInfo/search

### 参数

```
- groupId，string，教师分组id，空为全部
- classId，string，班级id，空为全部
- gradeId，string，年级id,空为全部
- userType, Integer, 用户类型，-1为全部，0管理员，1教师，2学生，99其他
- userName, string, 用户名称，空为全部
- pageIndex，int，>=1
- pageSize，int
```

### 输出

```
- totalCount，int，总数量
- pageCount，int，总页数
- list, List<object>, 学生信息
	- id，int
	- userId，string，用户id
	- userName，string，名称
    - userType，string，类型
	- sex，int，string，性别，0保密，1男，2女
	- gradeId，string，年级id
	- gradeName，string，年级名称
	- classId，string，班级id，用户为班主任时，为他所带的班级id
	- className，string，班级名称
	- groupId，string，教师组id
	- groupName，string，教师组名称
	- photoUrl，string，图像url
    - faceFeatureType，特征类型
    - faceFeatureByte，Arc特征
    - faceFeatureFile，OpenVINO类型
	- score，int，分数，0-100
    - createTime，date，用户信息创建时间
    - updateTime，date，用户信息最近更新时间
```

### 2.2 获取个人详细信息

### 地址
	GET  /userInfo/getById

### 参数

```	
- id，int
```

### 输出

```
- id，int
- userId，string，用户id
- userName，string，名称
- userType，string，类型
- sex，int，string，性别，0保密，1男，2女
- gradeId，string，年级id
- gradeName，string，年级名称
- classId，string，班级id，用户为班主任时，为他所带的班级id
- className，string，班级名称
- groupId，string，教师组id
- groupName，string，教师组名称
- photoUrl，string，图像url
- faceFeatureType，特征类型
- faceFeatureByte，Arc特征
- faceFeatureFile，OpenVINO类型
- score，int，分数，0-100
- createTime，date，用户信息创建时间
- updateTime，date，用户信息最近更新时间
```

### 2.2 获取个人详细信息(userId)

### 地址
    GET  /userInfo/getByUserId

### 参数

```	
- userId，String
```

### 输出

```
- id，int
- userId，string，用户id
- userName，string，名称
- userType，string，类型
- sex，int，string，性别，0保密，1男，2女
- gradeId，string，年级id
- gradeName，string，年级名称
- classId，string，班级id，用户为班主任时，为他所带的班级id
- className，string，班级名称
- groupId，string，教师组id
- groupName，string，教师组名称
- photoUrl，string，图像url
- faceFeatureType，特征类型
- faceFeatureByte，Arc特征
- faceFeatureFile，OpenVINO类型
- score，int，分数，0-100
- createTime，date，用户信息创建时间
- updateTime，date，用户信息最近更新时间
```


### 2.3 更新个人图片（特征）2

### 地址
    POST  /userInfo/updatePhoto2

### 参数
```
- userId, string
- photo，String，照片路径，上传是返回的path路径，参考1.6
```

### 输出
```
- state，bool，是否操作成功
- msg，String，操作信息
```


### 2.4 删除个人图片

### 地址
	GET  /userInfo/delPhoto

### 参数
	- userId，string

### 输出
	- state，bool，是否操作成功
	- msg，String，操作信息
	
### 2.5 删除个人信息（两个入参不能同时为空）

### 地址
	GET  /userInfo/delUser

### 参数
	- userId，string， "" 为全部
	- userType, string， -1 为全部

### 输出
	- state，bool，是否操作成功
	- msg，String，操作信息


### 2.6 上传本地图片
前端使用plupload插件上传

### 地址
	POST  /upload/uploadFile

### 参数
```
- type，string，上传图片的类型，此处type=face_image
```

### 输出
```
- url，String,url地址
- path，String，相对地址
```

### 2.7 同步基础平台图片

### 地址
	GET  /userInfo/getPhotoFromYun

### 参数
```
- token，string
- userId, string
- userType，Integer
```

### 输出
```
- url，String,url地址
- path，String，相对地址
```

### 2.8 个人图片评分

###  地址
	GET  /userInfo/getPhotoScore

### 参数
```
- photoUrl，图片路劲
```

### 输出
```
- state，bool，是否操作成功
- msg，int，该图片分数, 0-100
```


### 2.9 重置人脸库
    删除系统中所有用户信息 -> 清空特征 -> 重新同步基础平台用户数据

### 地址
	GET  /userInfo/resetLibrary

### 参数
```
- token，string
```

### 输出
```
- state，bool，是否操作成功
- msg，String，信息
```


### 2.10 批量导入人脸

### 地址
	GET  /userInfo/importFeatures

### 参数
```
- zipPath，string， 结合1.6接口
```

### 输出（list）
```
- userId，string
- userName，string
- type，int，导入方式
- photo，照片地址
- photoName，照片地址
- state，bool，状态
- msg，信息
```


### 2.11 从基础云平台获取学校信息

### 地址
	GET  /userInfo/getSchoolInfo

### 参数
```
无
```

### 输出（list）
```
- SchoolID，string
- SchoolName，string
- SchoolCode，string
- SchoolLevel，string
- SchoolType，string
- SchoolState，string
- CreateTime，string
- SchoolLogo，string
```

### 2.12 从基础云平台获取年级信息

### 地址
	GET  /userInfo/getGradeInfo

### 参数
```
- token, string
```

### 输出（list）
```
- GradeID，string
- GradeName，string
- GlobalGrade，string
- OrderNo，string
- SchoolID，string
```

### 2.13 从基础云平台获取班级信息

### 地址
	GET  /userInfo/getClassInfo

### 参数
```
- token, string
```

### 输出（list）
```
- GlobalGrade，string
- ClassID，string
- ClassName，string
- ClassNameQM，string
- GradeID，string
- OrderNo，string
- UpdateTime，string
```

### 2.14 从基础云平台获取教师组信息

### 地址
	GET  /userInfo/getGroupInfo

### 参数
```
- token, string
```

### 输出（list）
```
- groupId，string
- groupName，string
- schoolId，string
```

