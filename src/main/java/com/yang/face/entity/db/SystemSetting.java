package com.yang.face.entity.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.Area;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author yangyuyang
 * @date 2020/3/13 10:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "system_setting")
public class SystemSetting implements Serializable, Cloneable {

    private static final long serialVersionUID = 1955405019200931676L;

    private Integer id;

    private Integer faceType;

    @Override
    public SystemSetting clone() {
        try {
            return (SystemSetting) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
