package com.yang.face.service.yun;
/*
 * author zhufeng 2019-03-27
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
public class TeacherStructList {
	
	@XStreamImplicit(itemFieldName="anyType")	
	List<TeacherStruct> teaStructList;
    //教师详细信息列表


}
