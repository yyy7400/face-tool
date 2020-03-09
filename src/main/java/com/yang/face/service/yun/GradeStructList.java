package com.yang.face.service.yun;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.List;

/**
 * 从云平台获取年级信息列表
 * @author yangyuyang
 */
@Data
@XStreamAlias("ArrayOfAnyType")
public class GradeStructList {

	@XStreamImplicit(itemFieldName="anyType")
	//年级信息列表
	
	private List<GradeStruct> gradeList;

}
