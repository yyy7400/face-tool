package com.yang.face.service.yun;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.List;

@Data
@XStreamAlias("ArrayOfAnyType")
public class TeacherGroupStructList {
	
	@XStreamImplicit(itemFieldName="anyType")	
	List<TeacherGroupStruct> teaGroupStructList;
    //教师组详细信息列表

}
