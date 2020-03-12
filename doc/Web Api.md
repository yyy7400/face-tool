# Web Api 接口

## 1.1 python端获取特征文件

### 地址

GET /face/pythonClietn/features

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

 