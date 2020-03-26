var MyFaceContent = React.createClass({
	id: null,
	photoSourceType: 0,//来源类型:1上传,2同步,3现场采集
	getInitialState: function(){
		return {
			studentFaceData: null,
			userPhotoUrl: "Images/default_face.png",
			userPhotoPath: "",
			uploadNewPhoto: false,
			score: 0,//人脸分数
		}
	},
	componentDidMount: function(){
		var plupload = this.uploadImg('uploadImgBtn');
		this.loadStudentFaceData();
	},
	loadStudentFaceData: function(){
		var layerIndex = layer.load(1);
		var that = this;
	    $.ajax({
	        type: "GET",
	        cache: false,
	        url: address + "userInfo/getByUserId",
	        dataType: "json",
	        data: {userId: _userId},
	        async: true,
	        success: function (data) {
	            layer.close(layerIndex);
	            if (data.status == 0) {
	            	that.id = data.data.id;
	            	that.setState({studentFaceData: data.data, userPhotoUrl: data.data.photoUrl, uploadNewPhoto: false, score: data.data.score});
	            }else{
	            	that.setState({studentFaceData: [], score: 0});
	                console.log(data.msg);
	            }
	        },
	        error: function (responseData, textStatus, errorThrown) {
	        	layer.close(layerIndex);
	            console.log("请求数据出错");
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
		this.setState({userPhotoUrl: photoObj.data.url, userPhotoPath: photoObj.data.path, uploadNewPhoto: true})
		this.getPhotoScore(photoObj.data.path);
	},
	//获取头像评分
	getPhotoScore: function(photoUrl){
		var layerIndex = layer.load(1);
		var that = this;
		$.ajax({
	        type: "GET",
	        cache: false,
	        url: address + "userInfo/getPhotoScore",
	        dataType: "json",
	        data: {photoUrl: photoUrl},
	        async: true,
	        success: function (data) {
	            layer.close(layerIndex);
	            if (data.data.state) {
	            	that.setState({score: data.data.msg});
	            }else{
	            	that.setState({score: 0});
	            }
	        },
	        error: function (responseData, textStatus, errorThrown) {
	        	layer.close(layerIndex);
	            console.log("请求数据出错");
	        }
	    });
	},
	//同步基础平台图片
	getPhotoFromYun: function(){
		var layerIndex = layer.load(1);
		var that = this;
		$.ajax({
	        type: "GET",
	        cache: false,
	        url: address + "userInfo/getPhotoFromYun",
	        dataType: "json",
	        data: {token: _token, userId: this.state.studentFaceData.userId},
	        async: true,
	        success: function (data) {
	            layer.close(layerIndex);
	            if (data.status == 0) {
	            	that.photoSourceType = 2;
	            	that.setState({userPhotoUrl: data.data.url, userPhotoPath: data.data.path, uploadNewPhoto: true});
	            	that.getPhotoScore(data.data.url);
	            }else{
	            	console.log(data.msg);
	            }
	        },
	        error: function (responseData, textStatus, errorThrown) {
	        	layer.close(layerIndex);
	            console.log("请求数据出错");
	        }
	    });
	},
	//现场采集
//	getPhotoFromScene: function(){
//		this.photoSourceType = 3;
//		layer.msg("现场采集");
//	},
	saveUserPhoto: function(){
		if(this.state.score > 0){
			var layerIndex = layer.load(1);
			var that = this;
			$.ajax({
		        type: "POST",
		        cache: false,
		        url: address + "userInfo/updatePhoto2",
		        dataType: "json",
		        contentType: "application/json",
		        data: JSON.stringify({id: this.id, userId: _userId, photoUrl: this.state.userPhotoPath, photoSourceType: this.photoSourceType, score: this.state.score}),
		        async: true,
		        success: function (data) {
		            layer.close(layerIndex);
		            if (data.data.state) {
		            	layer.msg("保存成功");
		            	that.setState({uploadNewPhoto: false});
		            }else{
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
		this.loadStudentFaceData();
	},
	render: function(){
		var o = this.state.studentFaceData;
		if(o){
			var userName = o.userName;
			var sex = transUserSex(o.sex);
			var className = o.className;
			var userId = o.userId;
		}
		return (
			<div>
				<div className="my_face_bg" id="particles-js">
					<div className="my_face_container">
						<div className="my_face_photo_info">
							<p>姓名: {userName}</p>
							<p>性别: {sex}</p>
							<p>班级: {className}</p>
							<p>学号: {userId}</p>
							<p>人脸: <span style={{"color":this.state.score>0?"green":"red"}}>{this.state.score}</span> 分</p>
							<p style={{"fontSize":"14px","color":"red","display":this.state.score>0||this.photoSourceType==0?"none":"block"}}>当前头像分数过低，请重新选择！</p>
						</div>
						<div className="my_face_photo_div">
							<img className="my_face_photo_block1" src={this.state.userPhotoUrl} />
							<div className="my_face_operation_div">
								<a className="btn_color_1" id="uploadImgBtn">上传照片</a>
								{/*<a className="btn_color_1" onClick={this.getPhotoFromScene}>现场采集</a>*/}
								<a className="btn_no_color" onClick={this.getPhotoFromYun}>同步基础平台</a>
							</div>
						</div>
					</div>
				</div>
				<div style={{"paddingTop": "40px"}}>
					<div className="my_face_operation_container">
						<div className="my_face_operation_notice">
							<p>温馨提示 :</p>
							<p>1. 本系统支持上传本地照片，进行自主认证，或同步基础平台个人用户头像；</p>
							<p>2. 推荐照片像素最低为300*300，人脸部位像素不低于100*100，尽量选择人脸居中显示、光照亮度合适。</p>
						</div>
						<div className="my_face_photo_btn_group clearfix">
							<span className="sumit_btn" onClick={this.saveUserPhoto} style={{"display":this.state.uploadNewPhoto?"block":"none"}}>保存</span>
							<span className="cancel_btn" onClick={this.cancelOperate} style={{"display":this.state.uploadNewPhoto?"block":"none"}}>取消</span>
						</div>
					</div>
				</div>
			</div>
		)
	}
})

ReactDOM.render(
  	<MyFaceContent />,
  	document.getElementById('myFaceContent')
)