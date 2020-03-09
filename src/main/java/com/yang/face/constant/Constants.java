package com.yang.face.constant;

public class Constants {
    /**
     * 系统代号
     */
    public static final String SYS_ID = "E35";
    /**
     * 系统代号对应的锁控ID
     */
    public static final int SYS_LOCK_ID = 165;

    /**
     * 路径
     */
    public class Dir {

        public static final String CLASS = "Class/";

        public static final String IMAGE_YUN = "Image/Yun/";

        public static final String IMAGE_FACE = "Image/Face/";

        public static final String UPLOAD = "Upload/";

        public static final String TEMP = "Temp/";//临时文件

        public static final String CAMERA = "Camera/";

        public static final String FACE_FEATRUE = "face_features/";
    }


    public class UploadType {
        public static final String IMAGE_FACE = "face_image";

        public static final String NORMAL_FILE = "normal_file";
    }
}
