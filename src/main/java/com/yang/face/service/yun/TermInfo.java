package com.yang.face.service.yun;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.List;

/*
 * author zhufeng 2018-12-14
 */

@Data
@XStreamAlias("ArrayOfString")
public class TermInfo {
	
	@XStreamImplicit(itemFieldName="string")
	//学期信息列表
	private List<String> List ;

	
	
}
