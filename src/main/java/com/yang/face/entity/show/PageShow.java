package com.yang.face.entity.show;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 分页类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageShow {

    private Long totalCount;

    private Integer pageCount;

    private List<?> list;

}
