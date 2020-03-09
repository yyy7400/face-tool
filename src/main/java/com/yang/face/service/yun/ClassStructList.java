package com.yang.face.service.yun;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.List;

/**
 * 从云平台获取班级信息列表
 */
@Data
@XStreamAlias("ArrayOfAnyType")
public class ClassStructList {

	@XStreamImplicit(itemFieldName="anyType")
	//班级信息列表
	
	private List<ClassStruct> classList;

}
