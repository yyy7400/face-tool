package com.yang.face.constant;

/**
 * @author Yang
 */
public class Constants {
    /**
     * 系统代号
     */
    public static final String SYS_ID = "E35";
    /**
     * 系统代号对应的锁控ID
     */
    public static final int SYS_LOCK_ID = 165;

    public static final String PYTHON_FEATURE_EXT = "npy";

    /**
     * 路径
     */
    public class Dir {

        public static final String CLASS = "class/";

        public static final String IMAGE_YUN = "image/Yun/";

        public static final String IMAGE_FACE = "image/Face/";

        public static final String UPLOAD = "upload/";

        public static final String TEMP = "temp/";//临时文件

        public static final String FACE_FEATRUE = "face_features/";
    }


    public class UploadType {
        public static final String IMAGE_FACE = "face_image";

        public static final String NORMAL_FILE = "normal_file";
    }
}
