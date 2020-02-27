package com.yang.face.util;

import cn.hutool.core.util.StrUtil;
import com.yang.face.constant.Properties;

/**
 * 路径转换处理,只支持本系统内路劲
 */
public class PathUtil {

    /**
     * 获取相对路径
     * @param srcPath
     * @return
     */
    public static String getRelPath(String srcPath) {

        srcPath = srcPath.replace('\\', '/');

        if (srcPath.startsWith(Properties.SERVER_ADRR)) {
            return srcPath.substring(Properties.SERVER_ADRR.length());

        } else if (srcPath.startsWith(Properties.SERVER_RESOURCE)) {
            return srcPath.substring(Properties.SERVER_RESOURCE.length());
        }

        return srcPath;
    }

    /**
     * 获取绝对路径
     * @param srcPath
     * @return
     */
    public static String getAbsPath(String srcPath) {

        srcPath = srcPath.replace('\\', '/');

        if (srcPath.startsWith(Properties.SERVER_ADRR)) {
            return srcPath.replace(Properties.SERVER_ADRR, Properties.SERVER_RESOURCE);

        } else if (srcPath.startsWith(Properties.SERVER_RESOURCE)) {
            return srcPath;
        }

        return Properties.SERVER_RESOURCE + StrUtil.removePrefix(srcPath, "/");
    }

    /**
     * 获取url路径
     * @param srcPath
     * @return
     */
    public static String getUrl(String srcPath){

        srcPath = srcPath.replace('\\', '/');

        if (srcPath.startsWith(Properties.SERVER_ADRR)) {
            return srcPath;

        } else if (srcPath.startsWith(Properties.SERVER_RESOURCE)) {
            return srcPath.replace(Properties.SERVER_RESOURCE, Properties.SERVER_ADRR);
        }

        return Properties.SERVER_ADRR + StrUtil.removePrefix(srcPath, "/");
    }


}
