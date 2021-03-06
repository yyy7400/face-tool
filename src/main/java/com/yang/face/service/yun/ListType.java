package com.yang.face.service.yun;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.Data;

import java.util.List;

/**
 * @author Yang
 */
@Data
@XStreamAlias("ArrayOfString")
public class ListType {
	@XStreamImplicit(itemFieldName="string")
	//信息系统列表
	private List<String> List ;

}
