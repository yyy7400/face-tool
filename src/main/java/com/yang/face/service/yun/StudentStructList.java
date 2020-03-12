package com.yang.face.service.yun;
/*
 * author zhufeng 2018-12-17
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
public class StudentStructList {
	
	@XStreamImplicit(itemFieldName="anyType")	
	List<StudentStruct> studentStructList;
    //学生详细信息列表

}
