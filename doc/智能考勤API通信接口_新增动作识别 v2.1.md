#智能考勤API通信接口_新增动作识别

通过webapi的方式设定人脸识别接口

code安全码为特定格式（yyyy-MM-dd）的当天日期的BASE64编码。如：2019-01-02，base64编码后：MjAxOS0wMS0wMg==

响应结果中status字段说明：
```
- 0：正常

- 1：无数据；

- 2：安全码异常；

- 3：非法参数；

- 4：操作失败
```


## 1 人脸特征提取

### 1.1 人脸特征提取

名称：人脸特征提取

请求类型：post

请求url：/face_feature

接口描述：完成输入图片中的人脸特征提取工作，保存至本地，并返回url链接可远程访问与下载。

请求参数
```
- id, string, 图片编号，确保id唯一

- photo_type, int, 图片类型，1:图片URL路径；2：base64格式

- photo, string, 图片链接或图片转成的base64字符串。
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- data, List<object>

	- id, string, 图片编号

	- featureFile, string, 特征文件的url链接。
```


### 2 人脸评分

### 2.1 单张图片人脸评分

名称：单张图片人脸评分

请求类型：post

请求url：/face_score_image

接口描述：输入图片中仅有一张人脸是才能正确评分，调用方需自行判断；单张

请求参数
```
- photo_type, int, 图片类型，1:图片URL路径；2：base64格式

- photo，string，图片链接或图片转成的base64字符串。
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- data, object

	- score, int, 分数(0 - 100)

	- state,bool, 是否人脸
```


### 2.1.1 单张图片人脸评分-修改版

名称：单张图片人脸评分-修改版

请求类型：post

请求url：/face_score_image_mod

接口描述：输入图片中仅有一张人脸是才能正确评分，调用方需自行判断；多角度对图片进行评分。

请求参数
```
- photo_type, int, 图片类型，1:图片URL路径；2：base64格式

- photo，string, 图片链接或图片转成的base64字符串。
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- data, object

	- score, int, 分数(0 - 100)；综合计算人脸得分

	- state, bool, 是否人脸

	- retio_score, int, 人脸占比评价得分

	- retio_state, bool, 是否人脸占比过滤

	- face_score, int, 预测人脸得分

	- face_state, bool, 是否人脸得分过滤

	- loc_score, int, 人脸位置评分得分

	- loc_state, bool, 是否人脸位置过滤

	- clear_score, int, 照片清晰度评分得分

	- clear_state, bool, 是否照片清晰度过滤

	- px_score, int, 照片分辨率评分得分

	- px_state, bool, 是否照片分辨率过滤
```


### 2.2 *视频流人脸评分（待完成）*

名称：视频流人脸评分

请求类型：post

请求url：/face_score_video

接口描述：调用后,一段时间内输出一张分数最高的图片

请求参数
```
- videoUrl, string, 摄像头地址
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- date, object

	- photo, string, 图片地址（相对地址）

	- score, int, 分数，score=0时，photo为空
```


## 3 人脸检测

### 3.1 人脸检测-图片

名称：人脸检测-图片

请求类型：post

请求url：/face_detection_image

接口描述：图片中检测到人脸，保存到本地；并提供图片的url链接。

请求参数
```
- photo_type, int, 图片类型，1:图片URL路径；2：base64格式

- photo, string, 图片链接或图片转成的base64字符串。
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- data, object

	- photo, string, 带框选头像的图片，url链接。

	- faces, List<object>

		- x1, int, 起点x坐标

		- y1, int, 起点y坐标

		- x2, int, 终点x坐标

		- y2, int, 终点y坐标
```


### 3.2 人脸检测-视频（启动）

名称：人脸检测-视频（启动）

请求类型：post

请求url：/face_detection_video_start

接口描述：检测视频中的人脸；启动。

请求参数
```
- videoUrl, string, 摄像头地址
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- date, object

	- state, bool, 人脸检测启动状态；true，false

	- videoUrl, string, 带框头像视频地址（推流地址）
```


### 3.3 人脸检测-视频（关闭）

名称：人脸检测-视频（关闭）

请求类型：post

请求url：/face_detection_video_close

接口描述：关闭检测视频中的人脸服务。

请求参数
```
- videoUrl, string, 摄像头地址
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- date, object

	- state, bool, 人脸检测是否关闭，true，false
```


### 3.3.1 人脸检测-视频（关闭所有）

名称：人脸检测-视频（关闭所有）

请求类型：post

请求url：/face_detection_video_close_all

接口描述：关闭所有检测视频中的人脸服务。

请求参数
```

```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- date, object

	- state, bool, 人脸检测是否关闭，true，false
```


### 3.4 获取当前人脸检测视频列表

名称：获取当前人脸检测视频列表

请求类型：post

请求url：/face_detection_video_get_list

接口描述：获取当前运行的人脸检测的视频列表。

请求参数
```

```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- date, object

    - videoUrl, List<string>, 摄像头地址

    - rtmpvideoUrl, List<string>, 推流地址
```


### 3.5 人脸检测-图片（电子班牌）

名称：人脸检测-图片（电子班牌）

请求类型：post

请求url：/face_detection_image_ec

接口描述：对电子班牌照片呢个的图片进行检测出人脸；图片分辨率在800*800左右；并提供url链接。

请求参数
```
- photo_type, int, 图片类型，1:图片URL路径；2：base64格式

- photo, string, 图片链接或图片转成的base64字符串。
```


响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- data, object

	- photo, string, 带框选头像的图片，url链接。

	- faces, List<object>

		- x1, int, 起点x坐标

		- y1, int, 起点y坐标

		- x2, int, 终点x坐标

		- y2, int, 终点y坐标
```


### 3.6 *人脸检测-视频（启动）(电子班牌)(未测试)*

名称：人脸检测-视频（启动）(电子班牌)

请求类型：post

请求url：/face_detection_video_start_ec

接口描述：

请求参数
```
- videoUrl, string, 摄像头地址

- faceServerIp, String, 人脸识别服务器IP
```


响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- date, object

	- state, bool, 人脸检测启动状态，true，false

	- videoUrl, string, 带框头像视频地址
```


## 4 人脸识别

### 4.1 人脸识别-图片

名称：人脸识别-图片

请求类型：post

请求url：/face_recognition_image

接口描述：对所发送过来的照片进行人脸识别操作。

请求参数
```
- photo_type, int, 图片类型，1:图片URL路径；2：base64格式

- photo, string, 图片链接或图片转成的base64字符串。

- userInfos，List<object>，userInfos.size() == 0时，匹配全部；不能为空

    - userId, string, 用户Id

    - featureFiles, string, 特征路径
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- data, List<object>

	- userId，string，用户Id

	- similarityScore，double, 相识度（0-1）

	- x1, int, 起点x坐标

	- y1, int, 起点y坐标

	- x2, int, 终点x坐标

	- y2, int, 终点y坐标

	- headPhoto, 识别到的人脸照片(地址)

	- state, bool, 是否识别成功
```


### 4.2 *人脸识别-视频(待完善)*

名称：人脸识别-视频

请求类型：post

请求url：/face_recognition_video

接口描述：

请求参数
```
- videoUrl, string, 摄像头地址

- faceServerIp, String, 人脸识别服务器IP

- userInfos，List<object>，userInfos.size() == 0时，匹配全部；不能为空

    - userId, string, 用户Id

    - featureFiles, string, 特征路径
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- data, List<object>

	- videoUrl, string, 带框头像视频地址

	- faces, List<object>

		- userId, string, 用户Id

		- similarityScore, 相识度

		- x1, int, 起点x坐标

		- y1, int, 起点y坐标

		- x2, int, 终点x坐标

		- y2, int, 终点y坐标

		- headPhoto, 识别到的人脸照片(地址)
```


### 4.3 人脸识别-图片（电子班牌）

名称：人脸识别-图片（电子班牌）

请求类型：post

请求url：/face_recognition_image_ec

接口描述：对电子班牌的图片进行人脸识别。

请求参数
```
- type, int, 图片类型，1:图片URL路径；2：base64格式

- photo, string, 图片链接或图片转成的base64字符串。

- userInfos，List<object>，userInfos.size() == 0时，匹配全部；不能为空

    - userId, string, 用户Id

    - featureFiles, string, 特征路径
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- data, List<object>

	- userId，string，用户Id

	- similarityScore，double, 相识度（0-1）

	- x1, int, 起点x坐标

	- y1, int, 起点y坐标

	- x2, int, 终点x坐标

	- y2, int, 终点y坐标

	- headPhoto, 识别到的人脸照片(地址)

	- state, bool, 是否识别成功
```


### 4.4 人脸识别-证人校验

名称：人脸识别-证人校验

请求类型：post

请求url：/id_card_face_cmp

接口描述：人脸识别的1:1校验

请求参数
```
- type1, int, 图片类型，1:图片URL路径；2：base64格式

- photo1, string, 图片

- type2, int, 图片类型，1:图片URL路径；2：base64格式

- photo2, string, 图片
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- data, List<object>

	- similarityScore，double, 相识度（0-1）

	- state, bool, 是否识别成功
```


## 5 数据清理

### 5.1 人脸特征数据清理

名称：人脸特征数据清理

请求类型：post

请求url：/face_feature_clean

接口描述：清除本地的所有人脸特征

请求参数
```

```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- data, object

	- state, bool, 表示是否已清理人脸特征（True表示已清理）
```


### 5.2 人脸特征数据更新

名称：人脸特征数据更新

请求类型：post

请求url：/face_feature_update

接口描述：先获取主节点特征，进行比对操作，更新到本地，再重新加载本地的人脸特征库至内存中。

主接点返回参数格式：
```
-list
    -useId,string

    -fileUrl,string

    -updateTime,string

    -md5,string
```

请求参数
```

```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- data, object

	- state, bool, 表示是否重新加载人脸特征至内存（True表示已加载）
```


### 5.3 人脸特征数据清理（根据ID进行清理）

名称：人脸特征数据清理（根据ID进行清理）

请求类型：post

请求url：/face_feature_id_clean

接口描述：删除特定的ID，更新人脸特征库。

请求参数
```
- ids, list<string>, 表示将要清理的特征IDs
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- data, list

	- id, string, 表示将要清理的特征IDs

	- state, bool, 表示是否已清理人脸特征（True表示已清理）
```


## 6 截图

### 6.1 摄像头实时截图

名称：摄像头实时截图

请求类型：post

请求url：/get_cam_picture

接口描述：完成输入图片中的人脸特征提取工作，保存至本地，并返回保存目录。

请求参数
```
- videoUrl, string, 摄像头地址
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- data, object

	- state, bool, 摄像头状态

	- picUrl, string, 截图的地址
```



## 7 人体动作识别

### 7.1 人体动作识别-视频（启动）

名称：人体动作识别-视频（启动）

请求类型：post

请求url：/action_recognition_video_start

接口描述：根据所指定的摄像头地址，检测视频中的人体动作，启动人体动作识别视频服务。

请求参数
```
- videoUrl, string, 摄像头地址，或者本地视频路径
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- date, object

	- state, bool, 人脸检测启动状态，true，false

	- videoUrl, string, 带框选人体视频地址
```


### 7.2 人体动作识别-视频（关闭）

名称：人体动作识别-视频（关闭）

请求类型：post

请求url：/action_recognition_video_close

接口描述：关闭人体动作识别视频的服务。

请求参数
```
- videoUrl, string, 摄像头地址
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- date, object

	- state, bool,  人体动作识别服务是否关闭，true，false
```


### 7.3 获取人体动作识别视频列表

名称：获取人体动作识别视频列表

请求类型：post

请求url：/action_recognition_video_get_list

接口描述：获取当前运行的人体动作识别的视频列表

请求参数
```

```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- date, object

    - videoUrl, List<string>, 摄像头地址

    - rtmpvideoUrl, List<string>, 推流地址
```


### 7.4 人体动作识别与人脸识别的数据融合-图片

名称：人体动作识别与人脸识别的数据融合-图片

请求类型：post

请求url：/action_face_recognition_image

接口描述：识别照片中的人体动作，融合人脸ID，最终给出返回结果。

请求参数
```

- photo_type, int, 图片类型，1:图片URL路径；2：base64格式

- photo, string, 图片

- scheduleId, string, 课堂ID

- userInfos，List<object>，userInfos.size() == 0时，匹配全部

    - userId, string, 用户Id

    - featureFiles, string, 特征路径
```

响应参数
```
- status, int, 状态

- timestamp, dateTime, 服务器时间

- data, List<object>

	- scheduleId, string, 课堂ID

    - photo_id, string, 照片Id

	- userId, List<string>, 用户Id

	- action_label, List<string>, 动作

	- photo, 照片(地址)
```



