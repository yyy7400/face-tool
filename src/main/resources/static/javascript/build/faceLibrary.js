var _faceLibraryRefresh;
var FaceLibraryContent = React.createClass({displayName: "FaceLibraryContent",
	schoolId: "",
	classId: "",
	gradeId: "",
	groupId: "",
	searchName: "",
	pageSize: 50,
	schoolSeleIndex: 0,
	gradeSeleIndex: 0,
	classSeleIndex: 0,
	groupIndex: 0,
	studentNum: 0,
	userType: 2,
	getInitialState: function(){
		return {
			schoolData: [],
			gradeData: [],
			classData: [],
			groupData: [],
			studentFaceListData: [],
			schoolStyle: "",
			schoolName: ""
		}
	},
	componentDidMount: function(){
		var that = this;
		this.pageHandle = $(ReactDOM.findDOMNode(that.refs.page_container));
		this.pageHandle.handPage({total: 1, type: 0, handFunc: this.loadStudentFaceListData});
    	this.pageHandle.handPage("total", 0);
		this.getClassInfo();
		this.getTeacherGroup();
		_faceLibraryRefresh = this.refreshPage;
	},
	getTeacherGroup: function() {
		var that = this;
		ajaxGet("userInfo/getGroupInfo", {token: _token}, true, function(data) {
			var groupData = (data.status == 0) ? data.data : [];
			var groupData = [{"groupId":"","groupName":"全部分组"}].concat(groupData);
			that.setState({groupData: groupData});
		});
	},
	getSchoolInfo: function(layerIndex){
		var that = this;
		ajaxGet("userInfo/getSchoolInfo", {}, true, function(data) {
			layer.close(layerIndex);
    		var gradeList = [{"gradeID":"","gradeName":"全部年级"}];
			var classList = [{"classID":"","className":"全部班级"}];
            if (data.status == 0) {
            	if (data.data.length == 1) {
            		that.schoolId = data.data[0].schoolID;
            		that.setState({gradeData: gradeList.concat(that.gradeTotalData), classData: classList.concat(that.classTotalData), schoolStyle: "sele_bar_one_sc", schoolName: React.createElement("div", {className: "no_school", key: "no_school"}, data.data[0].schoolName)});
            	} else {
            		var schoolList = [{"schoolID":"","schoolName":"全部学校"}];
            		that.setState({schoolData: schoolList.concat(data.data), gradeData: gradeList.concat(that.gradeTotalData), classData: classList.concat(that.classTotalData)});
            	}
            } else {
            	that.setState({schoolStyle: "sele_bar_one_sc", schoolName: React.createElement("div", {className: "no_school", key: "no_school"}, "暂无学校信息")});
            }
			that.loadStudentFaceListData();
		});
	},
	getGradeInfo: function(layerIndex){
		var that = this;
		ajaxGet("userInfo/getGradeInfo", {token: _token}, true, function(data) {
            that.gradeTotalData = (data.status == 0) ? data.data : [];
			that.getSchoolInfo(layerIndex);
	    });
	},
	getClassInfo: function(){
		var layerIndex = layer.load(1);
		var that = this;
		ajaxGet("userInfo/getClassInfo", {token: _token}, true, function(data) {
            that.classTotalData = (data.status == 0) ? data.data : [];
			that.getGradeInfo(layerIndex);
	    });
	},
	handUserType: function(type) {
		if(type == 0){
			// 学生选项卡
			this.userType = 2;
		}else if(type == 1){
			// 教师选项卡
			this.userType = 1;
		}else{
			// 管理员选项卡
			this.userType = 0;
		}
		this.refs.search_input.clear();
		this.searchName = "";
		this.loadStudentFaceListData();
	},
	refreshPage: function() {
		var i = this.pageHandle.handPage("val");
		this.loadStudentFaceListData(i);
	},
	loadStudentFaceListData: function(index){//type,是否重置分页，false否true是
		pageIndex = index || 1;
		var layerIndex = layer.load(1);
		var that = this;
		var url="userInfo/search";
		if(that.userType=="2"){
			// 获取学生
			var prame={
				classId:that.classId,
				gradeId:that.gradeId,
				userType:that.userType,
				userName:that.searchName,
				pageIndex:pageIndex,
				pageSize:that.pageSize
			}
		}else if(that.userType=="1"){
			// 获取教师
			var prame={
				groupId:that.groupId,
				userType:that.userType,
				userName:that.searchName,
				pageIndex:pageIndex,
				pageSize:that.pageSize
			}
		}else{
			var prame={
				userType:that.userType,
				userName:that.searchName,
				pageIndex:pageIndex,
				pageSize:that.pageSize
			}
		}

		ajaxGet(url, prame, true, function(data) {
			layer.close(layerIndex);
            if (data.status == 0) {
            	that.studentNum = data.data.totalCount;
            	that.setState({studentFaceListData: data.data.list});
        		if (!index) {
        			that.pageHandle.handPage("total", data.data.pageCount);
        		} else {
        			that.pageHandle.handPage("val", pageIndex);
        		}
            } else {
            	that.pageHandle.handPage("total", 0);
            	that.studentNum = 0;
            	that.setState({studentFaceListData: []});
            }
		});
	},
	setSchoolName: function(index){
		if(index == 0){
			this.schoolId = "";
			this.gradeSeleIndex = 0;
			this.classSeleIndex = 0;
			var gradeList = [{"gradeID":"","gradeName":"全部年级"}];
			var classList = [{"classID":"","className":"全部班级"}];
			this.setState({gradeData: gradeList.concat(this.gradeTotalData), classData: classList.concat(this.classTotalData)});
		}else{
			this.schoolId = this.state.schoolData[index].schoolID;
			this.gradeSeleIndex = 0;
			this.classSeleIndex = 0;
			var gradeList = [{"gradeID":"","gradeName":"全部年级"}];
			for(var i in this.gradeTotalData){
				if(this.gradeTotalData[i].schoolID == this.schoolId){
					gradeList.push(this.gradeTotalData[i]);
				}
			}
			var classList = [{"classID":"","className":"全部班级"}];
			this.setState({gradeData: gradeList, classData: classList.concat(this.classTotalData)});
		}
		this.classId = "";
		this.gradeId = "";
		this.loadStudentFaceListData();
		this.refs.grade_sel.setVal(this.gradeSeleIndex);
		this.refs.class_sel.setVal(this.classSeleIndex);
	},
	setGradeName: function(index){
		this.classSeleIndex = 0;
		var classList = [{"classID":"","className":"全部班级"}];
		if(index == 0){
			this.gradeId = "";
			this.setState({classData: classList.concat(this.classTotalData)});
		}else{
			this.gradeId = this.state.gradeData[index].gradeID;
			for(var i in this.classTotalData){
				if(this.classTotalData[i].gradeID == this.gradeId){
					classList.push(this.classTotalData[i]);
				}
			}
			this.setState({classData: classList});
		}
		this.classId = "";
		this.loadStudentFaceListData();
		this.refs.class_sel.setVal(this.classSeleIndex);
	},
	setClassName: function(index){
		this.classId = (index == 0) ? "" : this.state.classData[index].classID;
		this.loadStudentFaceListData();
	},
	setGroup: function(index) {
		this.groupId = (index == 0) ? "" : this.state.groupData[index].groupId;
		this.loadStudentFaceListData();
	},
	setSearchName: function(data){
		this.searchName = data;
		this.loadStudentFaceListData();
	},
	openItemPage: function(id, userId) {
		_openFaceItam(id, userId, this.userType);
	},
	openUploadLayer: function(){
		_openUploadBatch(this.userType);
	},
	render: function(){
		var that = this;
		return (
			React.createElement("div", {className: "face_library_list"}, 
				React.createElement("div", {className: "face_library_search_bar_container clearfix"}, 
					React.createElement("div", {id: "selectBar", className: this.state.schoolStyle + " sele_bar" + this.userType}, 
						this.state.schoolName, 
						React.createElement(Selepicker, {width: "140px", class_name: "school_select", data: this.state.schoolData, attr: "schoolName", ref: "school_sel", handSele: this.setSchoolName, noSele: "全部学校"}), 
						React.createElement(Selepicker, {width: "140px", class_name: "grade_select stu_select", data: this.state.gradeData, attr: "gradeName", ref: "grade_sel", handSele: this.setGradeName, noSele: "全部年级"}), 
						React.createElement(Selepicker, {width: "140px", class_name: "class_select stu_select", data: this.state.classData, attr: "className", ref: "class_sel", handSele: this.setClassName, noSele: "全部班级"}), 
						React.createElement(Selepicker, {width: "140px", class_name: "group_select", data: this.state.groupData, attr: "groupName", ref: "group_sel", handSele: this.setGroup, noSele: "全部分组"})
					), 
					React.createElement(SearchInput, {ref: "search_input", handle: that.setSearchName})
				), 
            	React.createElement("div", {className: "clearfix", style: {"display":this.state.studentFaceListData.length>0?"block":"none"}}, 
					React.createElement("div", {className: "face_library_list_statistics"}, 
						React.createElement("span", {className: "word_1"}, "共"), 
						React.createElement("span", {className: "word_2"}, this.studentNum), 
						React.createElement("span", {className: "word_1"}, "人")
					), 
					React.createElement("div", {style: {"float":"right"}}, 
						React.createElement("button", {type: "button", className: "btn btn-xs btn-info", onClick: this.openUploadLayer}, React.createElement("span", {className: "glyphicon glyphicon-plus"}), " 批量导入")
					)
				), 
				React.createElement("div", {className: "no_data_container", style: {"display":this.state.studentFaceListData.length>0?"none":"block"}}, 
            		React.createElement("span", {className: "no_data_bg1"}), 
            		React.createElement("span", {className: "no_data_word"}, "暂无数据")
            	), 
				React.createElement("div", {className: "face_library_list_div clearfix", style: {"display":this.state.studentFaceListData.length>0?"block":"none"}}, 
					this.state.studentFaceListData.map(function(obj, i) {
						return React.createElement(FaceLibraryListItem, {key: "face_item_"+i, data: obj, index: i, handle: that.openItemPage})
					})
				), 
				React.createElement("div", {id: "page_container", ref: "page_container"})
			)
		)
	}
});
var SearchInput = React.createClass({displayName: "SearchInput",
	oriData: "",
	getInitialState: function() {
		return {
			btnStyle: {display: "none", top: 7}
		}
	},
	handleSearch: function(e) {
		if (e.keyCode == "13") {
			this.searchAppealData();
		}
	},
	searchAppealData: function() {
		var data = this.refs.appeal_name.value || "";
		if (data == this.oriData) return;
		this.props.handle(data);
		this.oriData = data;
	},
	clear: function() {
		this.oriData = "";
		this.refs.appeal_name.value = ""; 
		this.setState({btnStyle: {display: "none", top: 7}});
	},
	clear1: function() {
		this.clear();
		this.props.handle("");
	},
	setAppealName: function() {
		var btnStyle = (this.refs.appeal_name.value) ? {display: "block", top: 7} : {display: "none", top: 7};
		this.setState({btnStyle: btnStyle});
	},
	render: function() {
		return (
			React.createElement("div", {id: "searchInput"}, 
				React.createElement("input", {className: "form-control pull-right", id: "searchNameInput", ref: "appeal_name", placeholder: "请输入姓名进行搜索", maxLength: "16", onKeyDown: this.handleSearch, onChange: this.setAppealName}), 
				React.createElement("div", {className: "input_dele_small", style: this.state.btnStyle, onClick: this.clear1}), 
				React.createElement("a", {className: "searchButton", onClick: this.searchAppealData}, React.createElement("div", {className: "searchButtonIcon"}))
			)
		);
	}
});

var FaceLibraryListItem = React.createClass({displayName: "FaceLibraryListItem",
	clickFaceItem: function() {
		this.props.handle(this.props.data.id, this.props.data.userId);
	},
	errorImg: function(e) {
		this.setState({ face_img: "Images/default_face.png" });
	},
	render: function(){
		var o = this.props.data;
		return (
			React.createElement("div", {className: "face_library_block", onClick: this.clickFaceItem}, 
				React.createElement("div", {className: "face_library_pic"}, 
					React.createElement("img", {src: o.photoUrl || "Images/default_face.png", onError: this.errorImg})
				), 
				React.createElement("div", {className: "face_library_name"}, 
					o.userName
				)
			)
		)
	}
});

var _openFaceItam;
var FaceItemLayer = React.createClass({displayName: "FaceItemLayer",
	id: null,
	userId: "",
	photoSourceType: 0,//来源类型:1上传,2同步,3现场采集
	getInitialState: function(){
		return {
			studentFaceData: null,
			userPhotoUrl: "Images/default_face.png",
			userPhotoPath: "",
			uploadNewPhoto: false,
			score: 0,//人脸分数
			msg: ""
		}
	},
	componentDidMount: function() {
		_openFaceItam = this.loadFaceItemLayer;
	},
	loadFaceItemLayer: function(id, userId, userType) {
		this.id = id;
		this.userId = userId;
		this.userType = userType;
		var that = this;
  		this.loadStudentDataById();
  		if (!that.plupload) {
  			that.plupload = that.uploadImg('uploadImgBtn');
  		}
		that.layerIndex = layer.open({
			type: 1,
		  	title: false,
		  	content: $("#faceItemLayer"),
		  	area: ["830px", "545px"],
		  	resize: false,
		  	move: false,
		  	btn: false,
		  	closeBtn: false,
		  	success: function(){
		  	},
		  	end: function(){
		  		that.photoSourceType = 0;
		  		_faceLibraryRefresh();
		  	}
		});
	},
	loadStudentDataById: function(){
		var layerIndex = layer.load(1);
		var that = this;
		var url = "userInfo/getByUserId", d = {userId: this.userId};
		ajaxGet(url, d, true, function(data) {
			layer.close(layerIndex);
            if (data.status == 0) {
            	if(data.data.photoUrl){
            		that.setState({studentFaceData: data.data, userPhotoUrl: data.data.photoUrl, uploadNewPhoto: false, score: data.data.score});
            	}else{
            		that.setState({studentFaceData: data.data, uploadNewPhoto: false, score: 0});
            	}
            }else{
            	that.setState({studentFaceData: null});
            }
		});
	},
	uploadImg: function(id){
		var that = this;
		var upcover = new plupload.Uploader({
	    	browse_button: id, 
	        url: address + 'upload/uploadFile',    
	        runtimes: 'html5,flash,html4',
	        flash_swf_url: '../Script/library/plupload/Moxie.swf',
	        silverlight_xap_url: '../Script/library/plupload/Moxie.xap',
	        multi_selection: false,
	        max_file_size: '20mb',
	        filters: { mime_types: [
	            {title: '图片文件', extensions: 'jpg,jpeg,png,bmp,tiff' }
	        ]
	        //prevent_duplicates: true  //是否允许重复
	        },
	        max_retries: 0,           
	        chunk_size: '1mb',       
	        preinit: {
	            Init: function(up, info) {
	                if (up.runtime == "html4") {
	                    up.destroy();
	                    $("#upcover").on("click", function () {
	                        layer.msg('当前浏览器不支持Html5和Flash，请安装Flash插件或使用IE10+、Chrome、Firefox等浏览器', { time: 2000 });
	                    });
	                }
	            },
	            UploadFile: function(up, file) {
	                up.setOption('multipart_params', {type: "face_image"});
	            }
	        },
	
	        init: {
	            FilesAdded: function(up, files) {
	                for (var i in files) {
	                    if (!(files[i].origSize && files[i].name.length < 100)) {
	                        up.removeFile(files[i]);
	                        if (files[i].origSize)
	                            layer.msg('文件名不能超过100个字', {time: 2000});
	                        else
	                            layer.msg('不能上传空文件', {time: 1500});
	                        return;
	                    }
	                }
	                up.start();
	            },
	
	            FileUploaded: function(up, file, info) {
	                that.setUserPhoto(info);
	            },
	
	            Error: function(up, args) {
	                switch (args.code) {
	                    case -100: layer.msg('一般性错误', { time: 2000 }); break;
	                    case -200: layer.msg('网络错误', { time: 2000 }); break;
	                    case -300: layer.msg('文件不可读', { time: 2000 }); break;
	                    case -400: layer.msg('安全性太高，读取失败', { time: 2000 }); break;
	                    case -500: layer.msg('上传模块初始化出错', { time: 2000 }); break;
	                    case -600: layer.msg('文件太大', { time: 2000 }); break;
	                    case -601: layer.msg('文件类型不支持', { time: 2000 }); break;
	                    case -602: layer.msg('文件有重复', { time: 2000 }); break;
	                    case -700: layer.msg('图片格式错误', { time: 2000 }); break;
	                    case -701: layer.msg('内存发生错误', { time: 2000 }); break;
	                    case -702: layer.msg('错误:  文件太大，超过了限定', { time: 2000 }); break;
	                }
	            }
	        }  
	    });   
	    upcover.init();
	    return upcover;
	},
	//显示上传后的头像
	setUserPhoto: function(info) {
		var photoObj = JSON.parse(info.response);
		this.photoSourceType = 1;
		this.getPhotoScore(photoObj.data.url,photoObj.data.path,true);
	},
	//获取头像评分
	getPhotoScore: function(photoUrl,userPhotoPath,uploadNewPhoto){
		var layerIndex = layer.load(1);
		var that = this;
		ajaxGet("userInfo/getPhotoScore", {photoUrl: userPhotoPath}, true, function(data) {
            layer.close(layerIndex);
			var score = (data.status == 0 && data.data.state) ? data.data.msg : 0;
			var msg = data.data.msg;
			that.setState({userPhotoUrl: photoUrl, userPhotoPath: userPhotoPath, uploadNewPhoto: uploadNewPhoto, score: score, msg: msg});
		});
	},
	//同步基础平台图片
	getPhotoFromYun: function(){
		var layerIndex = layer.load(1);
		var that = this, curUserType = this.userType;
		ajaxGet("userInfo/getPhotoFromYun", {token: _token, userId: this.state.studentFaceData.userId, userType: curUserType}, true, function(data) {
			layer.close(layerIndex);
            if (data.status == 0) {
            	that.photoSourceType = 2;
            	that.getPhotoScore(data.data.url,data.data.path,true);
            }else{
            	console.log(data.msg);
            }
		});
	},
	//现场采集
	getPhotoFromScene: function(){
		this.photoSourceType = 3;
		layer.msg("现场采集");
	},
	saveUserPhoto: function(){
		if(this.state.score > 0){
			var layerIndex = layer.load(1);
			var that = this;
			var url = "userInfo/updatePhoto2";
			$.ajax({
		        type: "POST",
		        cache: false,
		        url: address + url,
		        dataType: "json",
		        contentType: "application/json",
		        data: JSON.stringify({id: this.id, userId: this.userId, photoUrl: this.state.userPhotoPath, photoSourceType: this.photoSourceType, score: this.state.score}),
		        async: true,
		        success: function (data) {
		            layer.close(layerIndex);
		            if (data.data.state) {
		            	layer.msg("保存成功");
		            	that.setState({uploadNewPhoto: false}, function(){
		            		that.closeLayer();
		            	});
		            }else{
						layer.msg("保存失败");
		            	console.log(data.msg);
		            }
		        },
		        error: function (responseData, textStatus, errorThrown) {
		        	layer.close(layerIndex);
		            console.log("请求数据出错");
		        }
		    });
		}else{
			layer.msg("当前头像分数过低，请重新选择");
		}
	},
	cancelOperate: function(){
		this.photoSourceType = 0;
		this.loadStudentDataById(this.id);
	},
	closeLayer: function(){
		layer.close(this.layerIndex);
	},
	deleMyPage: function() {
		var that = this;
		layer.confirm("确定要删除当前人脸图片吗？", function(index) {
			that.handDeleMy();
			layer.close(index);
		});
	},
	handDeleMy: function() {
		var url="userInfo/delPhoto";
		var that = this, layLoad = layer.load(1);
		ajaxGet(url, {userId: this.userId}, true, function(a) {
			layer.close(layLoad);
			if (a.status == 0 && a.data.state) {
				layer.msg("删除成功", {time: 2000});
				that.loadStudentDataById();
			} else {
				layer.msg("删除成功，请重新尝试", {time: 2000});
			}
		});
	},
	render: function(){
		var o = this.state.studentFaceData;
		if(o){
			var userName = o.userName;
			var sex = transUserSex(o.sex);
			var className = o.className;
			var userId = o.userId;
			var photoSourceType = o.photoSourceType;
		}
		var attr1 = "班级:", attr2 = "学号:", style = {display: "block"};
		if (this.userType == 1) {
			attr1 = "分组:", attr2 = "工号:", className = o.groupName;
		} else if (this.userType == 0) {
			attr2 = "工号:", style = {display: "none"};
		}
		return (
			React.createElement("div", {id: "faceItemLayer", style: {"display":"none"}}, 
				React.createElement("div", {className: "face_item_layer_title"}, 
					"人脸详情", React.createElement("span", {className: "close-btn", onClick: this.closeLayer})
				), 
				React.createElement("div", {className: "face_item_layer_content clearfix"}, 
					React.createElement("div", {className: "face_item_layer_content_left"}, 
						React.createElement("div", {className: "dele_img_btn", style: {display: (photoSourceType && photoSourceType == 1) ? "block" : "none"}, onClick: this.deleMyPage}, "删除人脸图片"), 
						React.createElement("img", {src: this.state.userPhotoUrl}), 
						React.createElement("div", {className: "my_face_operation_div"}, 
							React.createElement("a", {className: "btn_color_1", id: "uploadImgBtn"}, "上传照片"), 
							React.createElement("a", {className: "btn_color_1", style: {display: "none"}, onClick: this.getPhotoFromScene}, "现场采集"), 
							React.createElement("a", {className: "btn_no_color", onClick: this.getPhotoFromYun}, "同步基础平台")
						)
					), 
					React.createElement("div", {className: "face_item_layer_content_right"}, 
						React.createElement("p", null, React.createElement("span", {className: "title"}, "姓名: "), userName || ""), 
						React.createElement("p", null, React.createElement("span", {className: "title"}, "性别: "), sex || ""), 
						React.createElement("p", {style: style}, React.createElement("span", {className: "title"}, attr1, " "), className || ""), 
						React.createElement("p", null, React.createElement("span", {className: "title"}, attr2, " "), userId || ""), 
						React.createElement("p", null, React.createElement("span", {className: "title"}, "人脸: "), React.createElement("span", {style: {"color":this.state.score>0?"green":"red"}}, this.state.score), " 分"), 
						React.createElement("p", {style: {"fontSize":"14px","color":"red","display":this.state.score>0||this.photoSourceType==0?"none":"block"}}, this.state.msg)
					), 
					React.createElement("div", {className: "face_item_layer_content_bottom"}, 
						React.createElement("p", null, "温馨提示: 推荐照片像素最低为300*300，人脸部位像素不低于150*150，尽量选择人脸居中显示、光照亮度合适。")
					)
				), 
				React.createElement("div", {className: "face_item_layer_bottom"}, 
					React.createElement("span", {className: "sumit_btn", onClick: this.saveUserPhoto, style: {"display":this.state.uploadNewPhoto?"block":"none"}}, "保存"), 
					React.createElement("span", {className: "cancel_btn", onClick: this.cancelOperate, style: {"display":this.state.uploadNewPhoto?"block":"none"}}, "取消")
				)
			)
		)
	}
});

var _openUploadBatch;
var UploadBatchLayer = React.createClass({displayName: "UploadBatchLayer",
	getInitialState: function(){
		return {
			uploadResultList: []
		}
	},
	componentDidMount: function(){
		$(ReactDOM.findDOMNode(this.refs.recordListDiv)).mCustomScrollbar({
            theme: "minimal-dark",
            axis:"y",
            scrollbarPosition:"outside",
            autoDraggerLength:true,
            scrollInertia : 500,
            mouseWheel:{ preventDefault: true },
            advanced:{ 
                updateOnBrowserResize:true,
                updateOnContentResize:true,
                autoScrollOnFocus:true
            }
        });
		_openUploadBatch = this.openUploadBatchLayer;
	},
	openUploadBatchLayer: function(type){
		this.userType = type;
		var that = this;
		if (!that.plupload) {
  			that.plupload = that.uploadBatchImg();
  		}
		this.layerIndex = layer.open({
			type: 1,
		  	title: false,
		  	content: $("#uploadBatchLayerDiv"),
		  	area: ["830px", "572px"],
		  	resize: false,
		  	move: false,
		  	btn: false,
		  	closeBtn: false,
		  	success: function(){
		  		
		  	},
		  	end: function(){
		  		that.setState({uploadResultList: []});
		  		_faceLibraryRefresh();
		  	}
		});
	},
	//批量上传
	uploadBatchImg: function(){
		var that = this;
		var upcover = new plupload.Uploader({
	    	browse_button: 'upload_batch_btn',
	    	drop_element: 'uploadBatchDrop',
	        url: address + 'upload/uploadFile',    
	        runtimes: 'html5,flash,html4',
	        flash_swf_url: '../Script/library/plupload/Moxie.swf',
	        silverlight_xap_url: '../Script/library/plupload/Moxie.xap',
	        multi_selection: false,
	        max_file_size: '1024mb',
	        filters: { mime_types: [
	            {title: '压缩文件', extensions: 'zip' }
	        ],
	        //prevent_duplicates: true  //是否允许重复
	        },
	        max_retries: 0,           
	        chunk_size: '1mb',       
	        preinit: {
	            Init: function(up, info) {
	                if (up.runtime == "html4") {
	                    up.destroy();
	                    $("#upcover").on("click", function () {
	                        layer.msg('当前浏览器不支持Html5和Flash，请安装Flash插件或使用IE10+、Chrome、Firefox等浏览器', { time: 2000 });
	                    });
	                }
	            },
	            UploadFile: function(up, file) {
	                up.setOption('multipart_params', {type: "normal_file"});
	                that.uploadBatchLayerTips = layer.msg('<div class="uploadLoading"></div> 上传中', {
					  	time: 0,
					  	shade: 0.01
					});
	            }
	        },
	
	        init: {
	            FilesAdded: function(up, files) {
	                for (var i in files) {
	                    if (!(files[i].origSize && files[i].name.length < 100)) {
	                        up.removeFile(files[i]);
	                        if (files[i].origSize)
	                            layer.msg('文件名不能超过100个字', {time: 2000});
	                        else
	                            layer.msg('不能上传空文件', {time: 1500});
	                        return;
	                    }
	                }
	                up.start();
	            },
				UploadProgress: function(up, file){
					//console.log(up, file);
				},
	            FileUploaded: function(up, file, info) {
	            	layer.close(that.uploadBatchLayerTips);
	                that.importFeatures(info);
	                //console.log(up, file);
	            },
	            UploadComplete: function(up, file){
	            	//console.log(up, file);
	            },
	            Error: function(up, args) {
	                switch (args.code) {
	                    case -100: layer.msg('一般性错误', { time: 2000 }); break;
	                    case -200: layer.msg('网络错误', { time: 2000 }); break;
	                    case -300: layer.msg('文件不可读', { time: 2000 }); break;
	                    case -400: layer.msg('安全性太高，读取失败', { time: 2000 }); break;
	                    case -500: layer.msg('上传模块初始化出错', { time: 2000 }); break;
	                    case -600: layer.msg('文件太大', { time: 2000 }); break;
	                    case -601: layer.msg('文件类型不支持', { time: 2000 }); break;
	                    case -602: layer.msg('文件有重复', { time: 2000 }); break;
	                    case -700: layer.msg('图片格式错误', { time: 2000 }); break;
	                    case -701: layer.msg('内存发生错误', { time: 2000 }); break;
	                    case -702: layer.msg('错误:  文件太大，超过了限定', { time: 2000 }); break;
	                }
	            }
	        }  
	    });   
	    upcover.init();
	    return upcover;
	},
	//批量解压覆盖学生人脸
	importFeatures: function(info) {
		var zipObj = JSON.parse(info.response);
		var zipPath = zipObj.data.url;
		var layerIndex = layer.msg('<div class="uploadLoading"></div> 上传成功，正在导入中', {
		  	time: 0,
		  	shade: 0.01
		});
		var that = this;
		var url="userInfo/importFeatures";
		$.ajax({
			type: "get",
			cache: false,
			url: address + url,
			dataType: "json",
			data: {zipPath: zipPath},
			async: true,
			success: function(data){
				layer.close(layerIndex);
				if(data.status == 0){
					layer.msg("导入成功");
					that.setState({uploadResultList: data.data});
				}else{
					layer.msg("导入失败");
					that.setState({uploadResultList: []});
				}
			},
			error: function(responseData, textStatus, errorThrown){
				layer.close(layerIndex);
	            layer.msg("导入失败");
			}
		});
	},
	closeLayer: function(){
		layer.close(this.layerIndex);
	},
	render: function(){
		return (
			React.createElement("div", {id: "uploadBatchLayerDiv", style: {"display":"none"}}, 
				React.createElement("div", {className: "face_item_layer_title"}, 
					"批量导入人脸照片", React.createElement("span", {className: "close-btn", onClick: this.closeLayer})
				), 
				React.createElement("div", {className: "upload_batch_layer_content"}, 
					React.createElement("div", {className: "upload_batch_drop_div", id: "uploadBatchDrop", style: {"display": this.state.uploadResultList.length>0?"none":"block"}}, 
						React.createElement("div", {className: "upload_batch_drop_plus"}, React.createElement("span", {className: "glyphicon glyphicon-plus"}), " 请拖拽到方框内"), 
						React.createElement("div", {className: "upload_batch_drop_tips"}, "请选择压缩包文件（*.zip）包含以学生学号(教师/职工工号)命名的照片，如下示例图:"), 
						React.createElement("img", {className: "upload_batch_drop_demo", src: "Images/upload_batch_demo.jpg"})
					), 
					React.createElement("div", {style: {"padding":"5px 15px 0","display": this.state.uploadResultList.length>0?"block":"none"}}, 
						React.createElement("table", {className: "upload_batch_result_table_head table"}, 
							React.createElement("thead", null, 
								React.createElement("tr", null, 
									React.createElement("th", {width: "72"}, "人脸照片"), 
									React.createElement("th", {width: "150"}, "文件名"), 
									React.createElement("th", {width: "120"}, "学生学号"), 
									React.createElement("th", {width: "120"}, "学生姓名"), 
									React.createElement("th", {width: "80"}, "上传状态"), 
									React.createElement("th", {width: "258"}, "备注")
								)
							)
						)
					), 
					React.createElement("div", {className: "upload_batch_result_div", ref: "recordListDiv", style: {"display": this.state.uploadResultList.length>0?"block":"none"}}, 
						React.createElement("table", {className: "upload_batch_result_table table-hover"}, 
							React.createElement("tbody", null, 
							
								this.state.uploadResultList.map(function(o, i){
									return (
										React.createElement("tr", {key: "upload_batch_tr" + i}, 
											React.createElement("td", {width: "72"}, React.createElement("img", {src: o.photo})), 
											React.createElement("td", {width: "150"}, React.createElement("div", {className: "td_wrap", style: {"width":"140px"}}, o.photoName)), 
											React.createElement("td", {width: "120"}, React.createElement("div", {className: "td_wrap", style: {"width":"110px"}}, o.userId)), 
											React.createElement("td", {width: "120"}, React.createElement("div", {className: "td_wrap", style: {"width":"110px"}}, o.userName?o.userName:"-")), 
											React.createElement("td", {width: "80"}, React.createElement("span", {className: o.state?"glyphicon glyphicon-ok":"glyphicon glyphicon-remove"})), 
											React.createElement("td", {width: "258"}, React.createElement("div", {className: "td_wrap", style: {"width":"248px"}}, o.msg))
										)
									)
								})
							
							)
						)
					), 
					React.createElement("div", {className: "upload_batch_operation_div"}, 
						React.createElement("a", {id: "upload_batch_btn"}, this.state.uploadResultList.length>0?"重新上传":"上传压缩包")
					)
				)
			)
		)
	}
});

var FaceLibraryWarp = React.createClass({displayName: "FaceLibraryWarp",
	handTab: function(i) {
		this.refs.content.handUserType(i);
	},
	refreshPage: function() {
		this.refs.content.refreshPage();
	},
	render: function() {
		return (
			React.createElement("div", null, 
				React.createElement("div", {className: "face_library_search_bar"}, 
					React.createElement(FaceLibraryBar, {handTab: this.handTab, refresh: this.refreshPage})
				), 
				React.createElement(FaceLibraryContent, {ref: "content"}), 
				React.createElement(FaceItemLayer, {ref: "face_info_layer"}), 
				React.createElement(UploadBatchLayer, {ref: "upload_batch_layer"})
			)
		);
	}
});
var FaceLibraryBar = React.createClass({displayName: "FaceLibraryBar",
	getInitialState: function() {
		return {tabIndex: 0}
	},
	periodSelect: function(e) {
		var index = $(e.currentTarget).data("index");
		if (index == this.state.tabIndex) return;
		this.setState({tabIndex: index});
		this.props.handTab(index);
	},
	clearFace: function() {
		var that = this;
		layer.confirm("重置人脸库会清空管理员、教师、学生人脸图片，确定要重置吗?", function(index) {
			that.handClearFace();
			layer.close(index);
		});
	},
	handClearFace: function() {
		var layIndex = layer.load(1), that = this;
		ajaxGet("userInfo/resetLibrary", {token: _token}, true, function(a) {
			layer.close(layIndex);
			if (a.status == 0 && a.data.state) {
				layer.msg("共重置了" + a.data.msg + "位用户头像", {time: 2500});
				that.props.refresh();
			} else {
				layer.msg("重置人脸库失败", {time: 2000});
			}
		});
	},
	render: function() {
		var className = ["", "", ""];
		className[this.state.tabIndex] = "active";
		return (
			React.createElement("ul", {className: "period_select_ul clear"}, 
				React.createElement("li", {className: className[0], onClick: this.periodSelect, "data-index": 0}, "学生人脸库"), 
				React.createElement("li", {className: className[1], onClick: this.periodSelect, "data-index": 1}, "教师人脸库"), 
				React.createElement("li", {className: className[2], onClick: this.periodSelect, "data-index": 2}, "管理员人脸库"), 
				React.createElement("li", {style: {float: "right", fontSize: "16px", textDecoration: "underline"}, onClick: this.clearFace}, "重置人脸库")
			)
		);
	}
});

ReactDOM.render(
  	React.createElement(FaceLibraryWarp, null),
  	document.getElementById('faceLibraryContent')
)