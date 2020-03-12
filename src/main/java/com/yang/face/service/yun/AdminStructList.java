package com.yang.face.service.yun;
/*
 * @author zhufeng 2019-03-27
 */

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.List;

@Data
@XStreamAlias("ArrayOfAnyType")
public class AdminStructList {
	
	@XStreamImplicit(itemFieldName="anyType")	
	private List<AdminStruct> adminStructList;

}
