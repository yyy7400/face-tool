package com.yang.face.entity.middle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ByteFile {
    private byte[] bytes;

    private String ext;

    private String fileName;
}
