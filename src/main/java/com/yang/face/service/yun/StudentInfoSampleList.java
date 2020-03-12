package com.yang.face.service.yun;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.List;

/**
 * @author zhufeng 2018-12-7
 */

@Data
@XStreamAlias("ArrayOfAnyType")
public class StudentInfoSampleList {
	
	@XStreamImplicit(itemFieldName="anyType")
	//学生信息列表
	private List<StudentInfoSample> stuInfoList;

	
}
