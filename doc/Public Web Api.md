> 人脸识别服务 公共接口

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



## 2 人脸库基本信息

### 2.1 获取学生信息

### 地址

	GET  /pub/getStudentFeature

### 参数

```
- hasFeature，boolean，true：只查询有特征的学生；false：全部学生
- photoTime，string，查询正面照在该时间点后的学生，格式：2018-12-02 15:02:00
```

### 输出

```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，List<object>，信息
	- userId，string，用户Id
	- userName，string，用户名称
	- userPhotoYun，string，基础平台中头像照片
	- sex，int，性别，0保密；1男；2女
	- gradeId，string，年级Id
	- gradeName，string，年级名称
	- classId，string，年级Id
	- className，string，年级名称
	- photoIconUrl，string，正面照缩略图（裁剪压缩过，300px*300px），无特征时为空
	- photoUrl，string，正面照原图，无特征是为空，无特征时为空
	- photoTime，DateTime，正面照上传时间
```

### 1.2 获取教师信息

### 地址

	GET  /pub/getTeacherFeature

### 参数

```
- hasFeature，boolean，true：只查询有特征的教师；false：全部教师
- photoTime，string，查询正面照在该时间点后的教师，格式：2018-12-02 15:02:00
```

### 输出

```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，List<object>，信息
	- userId，string，用户Id
	- userName，string，用户名称
	- userPhotoYun，string，基础平台中头像照片
	- sex，int，性别，0保密；1男；2女
	- groupId，string，分组Id
	- groupName，string，分组名称
	- photoIconUrl，string，正面照缩略图（裁剪压缩过，300px*300px），无特征时为空
	- photoUrl，string，正面照原图，无特征时为空
	- photoTime，DateTime，正面照上传时间
```

### 2.3 获取管理员信息

### 地址

	GET  /pub/getAdminFeature

### 参数

```
- hasFeature，boolean，true：只查询有特征的管理员；false：全部管理员
- photoTime，string，查询正面照在该时间点后的管理员，格式：2018-12-02 15:02:00
```

### 输出

```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，List<object>，信息
	- userId，string，用户Id
	- userName，string，用户名称
	- userPhotoYun，string，基础平台中头像照片
	- sex，int，性别，0保密；1男；2女
	- photoIconUrl，string，正面照缩略图（裁剪压缩过，300px*300px），无特征时为空
	- photoUrl，string，正面照原图，无特征时为空
	- photoTime，DateTime，正面照上传时间
```

### 2.4 获取其他人员信息

### 地址

	GET  /pub/getAdminFeature

### 参数

```
- hasFeature，boolean，true：只查询有特征的管理员；false：全部管理员
- photoTime，string，查询正面照在该时间点后的管理员，格式：2018-12-02 15:02:00
```

### 输出

```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，List<object>，信息
	- userId，string，用户Id
	- userName，string，用户名称
	- userPhotoYun，string，基础平台中头像照片
	- sex，int，性别，0保密；1男；2女
	- photoIconUrl，string，正面照缩略图（裁剪压缩过，300px*300px），无特征时为空
	- photoUrl，string，正面照原图，无特征时为空
	- photoTime，DateTime，正面照上传时间
```

### 2.5 根据用户id获取人脸图片

### 地址

	POST  /pub/getPhotoByUserIds

### 参数

```
- userIds，List<String>，userId集合
```

### 输出

```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，List<object>，信息
	- userId，string，用户Id
	- photoIconUrl，string，正面照缩略图（裁剪压缩过，300px*300px），无特征时为空
	- photoUrl，string，正面照原图，无特征时为空
	- photoTime，DateTime，正面照上传时间
```

## 3 人脸库特征管理

### 3.1 批量图片导入特征库（导入后同时更新人脸库）

### 地址

	POST  /face/importFeatures

### 参数

```
- userId，String
- type，Integer，图片类型，1 图片url， 2 Base64 格式
- photo，String，与type配合使用
```

### 输出

```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，List<object>，信息
    - userId，string
    - userName，string
    - type，int，导入方式
    - photo，照片地址
    - photoName，照片地址
    - state，bool，状态
    - msg，信息
```

### 3.2 连续单张或少量图片导入特征库

    完成导入后，需调用3.3接口，将新提取的特征加载到引擎中
    
### 地址

	POST  /face/importFeaturesNoUpdate

### 参数

```
- userId，String
- type，Integer，图片类型，1 图片url， 2 Base64 格式
- photo，String，与type配合使用
```

### 输出

```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，List<object>，信息
    - userId，string
    - userName，string
    - type，int，导入方式
    - photo，照片地址
    - photoName，照片地址
    - state，bool，状态
    - msg，信息
```

### 3.3 更新特征库

    3.2完成导入后，调用该接口，将新提取的特征加载到引擎中
    
### 地址

	POST  /face/updateFeatures
	or
	POST  /face/importFeaturesUpdateFeatures   （@Deprecated）

### 参数

```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，object，信息
    - userId，String
    - type，Integer，图片类型，1 图片url， 2 Base64 格式
    - photo，String，与type配合使用
```

### 输出

```
- state，bool，状态
- msg，信息
```

### 3.4 清空重置特征库
    
### 地址

	POST  /face/cleanFeature

### 参数

```
无
```

### 输出

```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，object，信息
    - state，bool，状态
    - msg，信息
```

### 3.5 根据userIds清空特征库
    
### 地址

	POST  /face/cleanFeatureByUser

### 参数

```
- userIds，list
```

### 输出

```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，object，信息
    - state，bool，状态
    - msg，信息
```

## 4 人脸识别

### 4.1 近距离人脸识别

### 地址

	POST  /face/recoImage

### 参数

```
- type, int, 图片类型（1 - 图片 URL 路径；2 - base64 格式）
- photo, string, 图片
```

### 输出

```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，List<object>，操作信息
	- userId, string, 用户 ID
	- similarityScore, int, 相似度, 0-100
	- headPhoto, string, 识别到的人脸照片
```

### 4.2 近距离人脸识别，指定用户

#### 地址

    POST  /face/recoImageWithUser

#### 输入
```
- type, int, 图片类型（1 - 图片 URL 路径；2 - base64 格式）
- photo, string, 图片
- userIds, List<string>, 指定识别的用户id
```

#### 输出
```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，List<object>，操作信息
	- userId, string, 用户 ID
	- similarityScore, int, 相似度, 0-100
	- headPhoto, string, 识别到的人脸照片
```

### 4.3 教室内远距离人脸识别（无感考勤）

### 地址

	POST  /face/recoImageRoom

### 参数

```
- type, int, 图片类型（1 - 图片 URL 路径；2 - base64 格式）
- photo, string, 图片
```

### 输出

```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，List<object>，操作信息
	- userId, string, 用户 ID
	- similarityScore, int, 相似度, 0-100
	- headPhoto, string, 识别到的人脸照片
```

### 4.4 教室内远距离人脸识别，指定用户（无感考勤）

#### 地址

    POST  /face/recoImageWithUserRoom

#### 输入
```
- type, int, 图片类型（1 - 图片 URL 路径；2 - base64 格式）
- photo, string, 图片
- userIds, List<string>, 指定识别的用户id
```

#### 输出
```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，List<object>，操作信息
	- userId, string, 用户 ID
	- similarityScore, int, 相似度, 0-100
	- headPhoto, string, 识别到的人脸照片
```

### 4.5 开启人脸检测视频流（无感考勤）

#### 地址

    POST  /face/startDetectionVideo

#### 输入
```
- url, string, 摄像头rtsp地址
```

#### 输出
```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，List<object>，操作信息
	- state, bool, 状态
	- msg, string, rtmp直播流地址
```

### 4.6 关闭人脸检测视频流（无感考勤）

#### 地址

    POST  /face/stopDetectionVideo

#### 输入
```
- url, string, 摄像头rtsp地址
```

#### 输出
```
- status，int，状态
- timestamp，dateTime，服务器时间
- msg，string，接口请求信息
- data，List<object>，操作信息
	- state, bool, 状态
	- msg, string, rtmp直播流地址
```