var clientType = getQueryVariable("clientType");//判断客户端类型
var address = "http://192.168.129.142:8089/face/";
var _token = "";
var _schoolID = "";
var _userId = "";
var _userType = "";
var _userName = "";
var _lockPoint = -1;
var _base_addr="";
var _sys_id="";
var _userPhoto="";
// 登录相关方法和控件
function pageToken() {
	_setSysInfo();
	if (location.search != "" && location.search.indexOf("lg_tk=") >= 0) {
		var token = location.search.substring(location.search.indexOf('lg_tk=') + 6);
		_setCookie(_base_addr,token,_sys_id);
	}
	if(!_base_addr){
		var baseA = ajaxGet("mainServerAddr/getMainServerAddr", {}, false);
		if (!baseA) window.location.href = "Error.html";
		_base_addr = (baseA && baseA.data && baseA.status == 0) ? baseA.data : "http://192.168.129.229:3331/";
		_setCookie(_base_addr,_token,_sys_id);
	}
	if (_token && _getUserInfo(true)) {
		return;
	}
	if (!_token) {
		window.location.href = _getLoginUrl()
	}
	_getUserInfo();
}
function _getUserInfo(type) {
	var a = ajaxGet("mainServerAddr/WS_UserMgr_G_IsOnline", { token: _token }, false);
	if (a && a.status == 0 && a.data) {
		setInterval(testOnline, 70000);
		var returnData = ajaxGet("mainServerAddr/WS_UserMgr_G_GetAdmin", { token: _token }, false);
		if(returnData && returnData.status=="0" && returnData.data){
			var _userData = JSON.parse(returnData.data).data;
			_userType = _userData.UserType;
			_userId = _userData.UserID;
			_schoolID = _userData.SchoolID;
			_userName=_userData.UserName;
			_lockPoint=_userData.LockerState;
			_userPhoto=decodeURIComponent(_userData.PhotoPath);
			if (_userData) return true;
		}
	}
	if (type) return false;
	window.location.href = _getLoginUrl();
}
pageToken();
function testOnline() {
	ajaxGet("mainServerAddr/WS_UserMgr_G_IsOnline", { token: _token }, true, function (a) {
		if (a && a.status == 0 && a.data) {
			return;
		}
		window.location.href = _getLoginUrl();
	});
}
function _getLoginUrl(){
	var retUrl,_href=window.location.href;
	if (_href.indexOf('lg_tk=') > 1) {
		retUrl = encodeURIComponent(_href.substring(0, _href.indexOf('lg_tk=') - 1));
	} else {
		retUrl = encodeURIComponent(_href);
	}
	if (_base_addr != "") {
		var loginUrl = _base_addr + "/UserMgr/Login/Login.aspx?lg_sysid=" + _sys_id + "&lg_preurl=" + retUrl;
		return loginUrl;
	}
	return "Error.html";
}
function _setCookie(base_adr,token,sys_id) {
	var cookieStr = encodeURIComponent(base_adr + ","+token+"," + sys_id);
	document.cookie = "_login_OnlineCheck=" + cookieStr;
	_setSysInfo();
}
function _delCookie() {
	var cookieStr = encodeURIComponent(_base_addr + ",," + _sys_id);
	document.cookie = "_login_OnlineCheck=" + cookieStr;
}
// 从cookie中获取基础平台地址,token和sysid信息
function _setSysInfo(){
	var name = "_login_OnlineCheck";
	var arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
	if (arr != null) {
		var a = unescape(arr[2]);
		try {
			if (a != null && a != "") {
				var c = a.split(",");
				if (c != null && c.length == 3) {
					_base_addr = c[0];
					_token = c[1];
					_sys_id = c[2];
				}
				return null;
			} else { return null; }
		} catch (e) { return null; }
	}
	return null;
}
var LoginPlugin=React.createClass({
	goUserDetail:function(){
		var link=_base_addr+"UserMgr/PersonalMgr/Default.aspx";
		window.open(link,"_blank")
	},
	goHelp:function(e){
		var link=address+"userhelp/e26-m.html";
		window.open(link,"_blank")
	},
	quitPage:function(e){
		layer.confirm("确定要退出登录吗?", function () {
			var loadIndex = layer.load(1);
			ajaxGet("mainServerAddr/loginOut", {token: _token}, true, function (a) {
				if (a && a.status == 0 && a.data) {
					window.location.href = _getLoginUrl()
				} else {
					layer.msg("退出登录失败，请重新尝试", {time: 2000});
				}
			});
			layer.close(loadIndex);
		});
	},
    render:function(){
        return (
			<div className="clear">
				<div className="left help_photo">
					<div className="left help" onClick={this.goHelp}>
						<span className="help_icon"></span>
						<span>帮助</span>
					</div>
					<div className="user_photo" style={{backgroundImage:"url("+_userPhoto+")"}} onClick={this.goUserDetail}>
					</div>
				</div>
				<div className="left name_out">
					<span className="user_name" onClick={this.goUserDetail}>{decodeURIComponent(_userName)}</span>
					<span className="log_out" onClick={this.quitPage}></span>
				</div>
			</div>
        );
    }
})
ReactDOM.render(
  	<LoginPlugin />,
  	document.getElementById('loginBlock')
)
// 登录相关方法和控件end
// 获取参数
function _getUrlParams() {
    var urlParamsStr = location.search.substr(1);

    if(urlParamsStr){
        var urlParamsArr = urlParamsStr.split('&');
        var urlParamsObj = {};

        for(var i = 0; i < urlParamsArr.length; i++){
            var index = urlParamsArr[i].indexOf('=');
            var key = urlParamsArr[i].substr(0, index);
            var value = urlParamsArr[i].substr(index + 1);

            urlParamsObj[key] = value;
        }

        return urlParamsObj;
    }else{
        return null;
    }
}
//格式化时间，传入date可以为 时间戳(number)、时间字符串(string)、时间对象(object)
function formateDate(date,type){
	if( typeof(date) != "object" && typeof(date) != "number"){
		var date = date.replace(/-/g, '/');
	}
	var newDate = new Date(date);
	var yyyy = newDate.getFullYear();
	var MM = newDate.getMonth() + 1;
	if(MM<=9){
		MM = "0" + MM;
	}
	var dd = newDate.getDate();
	if(dd<=9){
		dd = "0" + dd;
	}
	var HH = newDate.getHours();
	if(HH<=9){
		HH = "0" + HH;
	}
	var mm = newDate.getMinutes();
	if(mm<=9){
		mm = "0" + mm;
	}
	var ss = newDate.getSeconds();
	if(ss<=9){
		ss = "0" + ss;
	}
	if(type=="yyyy-MM-dd"){
		return yyyy + "-" + MM + "-" + dd;
	}else if(type=="yyyy-MM-dd HH:mm:ss"){
		return yyyy + "-" + MM + "-" + dd + " " + HH + ":" + mm + ":" + ss;
	}else if(type=="yyyy-MM-dd HH:mm"){
		return yyyy + "-" + MM + "-" + dd + " " + HH + ":" + mm;
	}else if(type=="HH:mm:ss"){
		return HH + ":" + mm + ":" + ss;
	}else if(type=="HH:mm"){
		return HH + ":" + mm;
	}
}

//获取url地址后面拼接的参数
function getQueryVariable(variable){
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i=0;i<vars.length;i++) {
		var pair = vars[i].split("=");
		if(pair[0] == variable){
			return pair[1];
		}
	}
	return undefined;
}

//转换用户性别
function transUserSex(sex){
	switch(sex){
		case 1 : return "男";
		case 2 : return "女";
		default : return "保密";
	}
}
//校验ip地址是否有效
function isValidIP(ip) {
    var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/
    return reg.test(ip);
}

//判断是否安装flash player及当前版本
function flashChecker() {
    var hasFlash = 0;　　　　 //是否安装了flash
    var flashVersion = 0;　　 //flash版本
    if (document.all) {//目前只有ie10及以下版本支持,返回为true,其他浏览器为false
        var swf = new ActiveXObject('ShockwaveFlash.ShockwaveFlash');
        if (swf) {
            hasFlash = 1;
            VSwf = swf.GetVariable("$version");
            flashVersion = parseInt(VSwf.split(" ")[1].split(",")[0]);
        }
    } else {
        if (navigator.plugins && navigator.plugins.length > 0) {
            var swf = navigator.plugins["Shockwave Flash"];
            if (swf) {
                hasFlash = 1;
                var words = swf.description.split(" ");
                for (var i = 0; i < words.length; ++i) {
                    if (isNaN(parseInt(words[i]))) continue;
                    flashVersion = parseInt(words[i]);
                }
            }
        }
    }
    return {
        f: hasFlash,
        v: flashVersion
    };
}

//判断浏览器名称,给出相对应的安装flash播放器和启动方法,目前只测试了ie10~11、firefox、chrome、QQ浏览器、猎豹、360
function browserChecker() {
	var msg = navigator.userAgent;
	if ( msg.indexOf("Firefox") != -1 ){//判断为火狐浏览器
		var content = '<div>';
		content += '<div class="student_confirm_layer_Title">温馨提示<span class="close-btn" onClick="layer.closeAll()"></span></div>';
		content += '<div class="confirm_layer_content">';
		content += '<p>请安装并开启浏览器的flash功能，以获得更好的体验!</p>'
		content += '<p>请点击官网链接<a href="https://www.flash.cn" target="_blank">www.flash.cn</a></p>';
		content += '<p>可参考<a href="https://jingyan.baidu.com/article/1612d5009b3d43e20e1eee04.html" target="_blank">教程</a></p>';
		content += '<p>安装flash player并重启浏览器</p>';
		content += '</div>';
		content += '<div class="confirm_layer_bottom">';
		content += '<button type="button" class="confirm_layer_btn" onClick="layer.closeAll()">我知道了</button>';
		content += '</div>';
		content += '</div>';
		
		var layerIndex = layer.open({
			type: 1,
		  	title: false,
		  	content: content,
		  	area: ['480px', '300px'],
		  	resize: false,
		  	move: false,
		  	btn: false,
		  	closeBtn: false,
		  	shadeClose: true
		});
	}else if( msg.indexOf("Edge") != -1 ){//判断Edge浏览器
		var content = '<div>';
		content += '<div class="student_confirm_layer_Title">温馨提示<span class="close-btn" onClick="layer.closeAll()"></span></div>';
		content += '<div class="confirm_layer_content">';
		content += '<p>请开启浏览器的flash功能，以获得更好的体验!</p>';
		content += '<p>点击浏览器地址栏前的感叹号，开启Adobe Flash功能</p>';
		content += '<p>如仍未解决，可参考<a href="https://jingyan.baidu.com/article/4ae03de3b8b8373eff9e6b1e.html" target="_blank">教程</a>，设置后请重启浏览器</p>';
		content += '<p>注:win10家庭版系统启用组策略gpedit.msc可参考<a href="https://blog.csdn.net/u013642500/article/details/80138799" target="_blank">教程</a></p>';
		content += '</div>';
		content += '<div class="confirm_layer_bottom">';
		content += '<button type="button" class="confirm_layer_btn" onClick="layer.closeAll()">我知道了</button>';
		content += '</div>';
		content += '</div>';
		
		var layerIndex = layer.open({
			type: 1,
		  	title: false,
		  	content: content,
		  	area: ['480px', '300px'],
		  	resize: false,
		  	move: false,
		  	btn: false,
		  	closeBtn: false,
		  	shadeClose: true
		});
	}else {//其他浏览器
		var content = '<div>';
		content += '<div class="student_confirm_layer_Title">温馨提示<span class="close-btn" onClick="layer.closeAll()"></span></div>';
		content += '<div class="confirm_layer_content">';
		content += '<p>请开启浏览器的flash功能，以获得更好的体验!</p>';
		content += '<p>点击浏览器地址栏前的感叹号，开启Adobe Flash功能</p>';
		content += '<p>可参考<a href="https://jingyan.baidu.com/article/e75aca8568cd6d142fdac678.html" target="_blank">教程</a></p>';
		content += '</div>';
		content += '<div class="confirm_layer_bottom">';
		content += '<button type="button" class="confirm_layer_btn" onClick="layer.closeAll()">我知道了</button>';
		content += '</div>';
		content += '</div>';
		
		var layerIndex = layer.open({
			type: 1,
		  	title: false,
		  	content: content,
		  	area: ['480px', '300px'],
		  	resize: false,
		  	move: false,
		  	btn: false,
		  	closeBtn: false,
		  	shadeClose: true
		});
	}
}

//下拉选择公共组件
var Selepicker = React.createClass({
    componentDidMount: function() {
        this.ele = $(ReactDOM.findDOMNode(this.refs.selectpicker));
        this.ele.selectpicker({});
        var that = this;
        this.ele.on('changed.bs.select', function (e, index, newVal, oldVal) {
            that.props.handSele(index);
        }.bind(this));

        this.scrollBar = $(ReactDOM.findDOMNode(this.refs.sele_warp)).find('.inner');
        this.initScrollBar();
    },
    componentDidUpdate: function() {
        if (this.ele) {
            if (this.hasScrollBar) {
                this.scrollBar.mCustomScrollbar("destroy");
                this.hasScrollBar = false;
            }
            this.ele.selectpicker("refresh");
            if (this.props.data.length > 0) {
                this.ele.selectpicker("val", 0);
                this.initScrollBar();
            }
        }
    },
    shouldComponentUpdate: function(nProps, nState) {
        return (this.props.data !== nProps.data);
    },
    initScrollBar: function() {
        if (this.props.data.length < 7) return;
        this.hasScrollBar = true;
        this.scrollBar.mCustomScrollbar({
            theme: "minimal-dark",
            axis:"y",
            scrollbarPosition:"inside",
            autoDraggerLength:true,
            scrollInertia : 500,
            mouseWheel:{ preventDefault: true },
            advanced:{ 
                updateOnBrowserResize:true,
                updateOnContentResize:true,
                autoScrollOnFocus:true
            }
        });
    },
    setVal: function(i) {
        this.ele.selectpicker("val", i);
    },
    render: function() {
        var attr = this.props.attr, disAttr = this.props.disAttr;
        var optList = this.props.data.map(function(o, i) {
            var text = (attr) ? o[attr] : o;
            var disabled = (disAttr && o[disAttr]) ? true : false;
            return <option value={i} key={"sele_opt" + i} disabled={disabled}>{text}</option>
        });
        var width = this.props.width || "auto";
        var size = this.props.size || "6";
        var search = (this.props.search) ? true : false;
        var no_sele = this.props.noSele || "无可选项";
        var class_name = this.props.class_name || "";
        return (
            <div className={class_name} style={{"display": this.props.data.length > 0 ? "" : "none"}}>
            	<div className={class_name + "_title"} style={{"display":this.props.title?"":"none"}}>{this.props.title}</div>
                <div className={class_name + "_picker"} ref="sele_warp">
                    <select className="selectpicker" ref="selectpicker" data-width={width} data-dropup-auto="false" data-live-search={search} data-size={size} data-style="sele_style" data-none-selected-text={no_sele}>
                        {optList}
                    </select>
                </div>
            </div>
        );
    }
});

(function($){
    myBrowser();
	if(clientType == "classBoard"){
		$(".kq_header").hide();
		$(".kq_header_1").hide();
	}
	var urlpath = window.location.pathname;
	console.log(urlpath);
	urlpath = urlpath.toLowerCase();
	if(_userType!="1" && _userType!="2"){
		// 管理员
		if(urlpath.indexOf("facelibrary.html")<0){
			window.location.href = "FaceLibrary.html";
		}
	}
	if(_userType=="1"){
		// 教师
		window.location.href = "Error.html";
	}
	if(_userType=="2"){
		// 学生
		if(urlpath.indexOf("myface.html")<0){
			window.location.href = "MyFace.html";
		}
	}
	var _hasHide = false;
    $(window).on("load",function(){
        $("#mCustomScrollbar_div").mCustomScrollbar({
        	theme: "minimal-dark",
            axis:"y",
            scrollbarPosition:"inside",
            autoDraggerLength:true,
            scrollInertia : 500,
            mouseWheel:{ preventDefault: true },
            advanced:{ 
                updateOnBrowserResize:true,
                updateOnContentResize:true,
                autoScrollOnFocus:true
            },
            callbacks:{
			    onCreate: function(){
			    	//console.log("Plugin markup generated");
			    },
			    onInit:function(){
			      	//console.log("Scrollbars initialized");
			    },
			    onScrollStart:function(){
			      	//console.log("Scrolling started...");
			    },
			    onScroll:function(){
			      	//console.log("Content scrolled...");
			    },
			    whileScrolling:function(){
			    	//console.log(this.mcs.top,this.mcs.draggerTop,this.mcs.topPct);
			    	if (_hasHide && window.screen.height < 910) {
			    		_hasHide = false;
			    		$("#particles-js").css("height", "65px");
			    	}
			    	if(this.mcs.draggerTop>0){
			    		$(".kq_header").css("background-color","#000715");
			    	}else{
			    		$(".kq_header").css("background-color","rgba(0,0,0,0.6)");
			    	}
			    },
			    onTotalScroll:function(){
			      	//console.log("Scrolled to end of content.");
			    },
			    onTotalScrollBack:function(){
			    	//console.log("Scrolled back to the beginning of content.");
			    },
			    alwaysTriggerOffsets: false
			}
        });
    });
})(jQuery);
function myBrowser() {
	// var aData = ajaxGet("system/getValTime", {time: formateDate(Date.parse(new Date()),"yyyy-MM-dd HH:mm:ss")}, false);
	// if (!aData || aData.status != 0) {
	// 	window.location.href = "/Error.html";
	// 	return;
	// }
    var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
    var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1; //判断是否IE浏览器
    if (isIE) {
        var reIE = new RegExp("MSIE (\\d+\\.\\d+);");
        reIE.test(userAgent);
        var fIEVersion = parseFloat(RegExp["$1"]);
        if (fIEVersion < 10)
            window.location.href = "/Browser.html";
    }
}

//请求封装
function ajaxGet(url, data, async, fun) {
    var aData = null;
    $.ajax({
        type: "GET",
        url: address + url,
        cache: false,
        dataType: "json",
        contentType: "application/json",
        data: data,
        async: async,
        success: function (data) {
            data = data || {status: 6};
            if (async)
                fun(data);
            else
                aData = data;
        },
        error: function (responseData, textStatus, errorThrown) {
            layer.closeAll("loading");
        }
    });
    return aData;
}

function ajaxPost(url, d, async, fun) {
    var aData = null;
    $.ajax({
        type: "POST",
        cache: false,
        url: address + url,
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(d),
        async: async, 
        success: function (data) {
            data = data || {status: 6};
            if (async)
                fun(data);
            else
                aData = data;
        },
        error: function (responseData, textStatus, errorThrown) {
            layer.closeAll("loading");
        }
    });
    return aData;
}