package com.yang.face.service.yun;
/*
 * author zhufeng 2018-12-14
 */

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.List;

/**
 * @author Yang
 */
@Data
@XStreamAlias("ArrayOfAnyType")
public class SchoolInfoStructList {
	
	@XStreamImplicit(itemFieldName="anyType")
	//学校信息列表
	List<SchoolInfoStruct> SchoolInfoStructList;

}
