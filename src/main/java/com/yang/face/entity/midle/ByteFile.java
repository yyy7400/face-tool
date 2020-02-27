package com.yang.face.entity.midle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ByteFile {
    private byte[] bytes;

    private String ext;

    private String fileName;
}
