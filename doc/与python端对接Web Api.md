# Web Api 接口

## 1.0 python心跳

每5秒发调用一次

### 地址

GET /face/pythonClient/heartBeat

### 输入

- addr, string, 格式：http://192.168.129.134:5009/

### 输出
```
    - state, bool
    - msg, string，信息
```

## 1.1 python端获取所有特征文件

系统启动时调用，与本地文件对比，根据文件md5至判断文件是否已更新，如更新则下载替换

### 地址

GET /face/pythonClient/features

### 输入
无

### 输出
```
List
    - userId, string
    - fileUrl, string，特征文件路径
    - updateTime, string，特征生成时间
    - md5, string，用于验证特征是否已更新
```


 