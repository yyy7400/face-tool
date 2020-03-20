package com.yang.face.util;

import java.io.*;

/**
 * 深拷贝，拷贝对象需要实现Serializable接口
 * @author yangyuyang
 * @date 2020/3/19 21:37
 */
public class DeepCopyUtil {

    // 实现深拷贝
    public static <T extends Serializable> T clone(T obj) throws IOException, ClassNotFoundException {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bout);
        oos.writeObject(obj);
        bout.flush();
        bout.close();

        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bin);

        T cloneObj = (T) ois.readObject();
        ois.close();

        return cloneObj;
    }
}
