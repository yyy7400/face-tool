(function($){
	$.fn.handPage = function(opts, value) {
		if (typeof opts === "string") {
			var rVal = -1;
			this.each(function() {
				var that = this;
				switch (opts) {
					case "refresh":
						refresh.call(that);
						break;
					case "val":
						rVal = getOrSetVal.call(that, value);
						break;
					case "total":
						rVal = getOrSetTotal.call(that, value);
						break;
				}
			});
			if (!value) return rVal;
		} else if (typeof opts === "object") {
			opts = $.extend({}, $.fn.handPage.opts, opts);
			this.each(function() {
				var html = "";
				var that = this;
				init.call(that, opts);
			});
		}
	}
	$.fn.handPage.opts = {
		total: 20 //总页数
		, type: 0 //样式类型
		, setIndex: -1//设定当前所在页
		, handFunc: $.noop//触发按钮的方法
	};
	function init(opts) {//初始化
		if (opts.total < 1) return;
		var preHtml = "";
		var nextHtml = ""; 
		var type = "type" + opts.type;
		var neBtn = (opts.total > 1) ? "" : " hp_ban_btn";
		if (opts.type != 2) {
			preHtml += "<span class='hp_pre hp_ban_btn'>上一页</span>";
			preHtml += "<span class='hp_page_n hp_page_first hp_hide'>1</span>";
	    	preHtml += "<span class='hp_pre_node hp_hide'>···</span>";
	    	var nNode = (opts.total > 6) ? "" : " hp_hide";
    		nextHtml += "<span class='hp_next_node"+ nNode +"'>···</span>";
    		var toNum = (opts.total > 5) ? "" : " hp_hide";
	    	nextHtml += "<span class='hp_page_n hp_page_last"+ toNum +"'>"+ opts.total +"</span>";
	    	nextHtml += "<span class='hp_next"+ neBtn +"'>下一页</span>";
    		nextHtml += "<span class='hp_text'>&nbsp;&nbsp;到</span>";
		    nextHtml += "<input type='text' class='hp_input' placeholder='"+ opts.total +"'/>";
		    nextHtml += "<span class='hp_text'>页&nbsp;&nbsp;</span>";
		} else {
			preHtml += "<span class='hp_pre hp_ban_btn'><</span>";
			nextHtml += "<span class='hp_next"+ neBtn +"'>></span>";
    		nextHtml += "<span class='hp_text'>&nbsp;跳至</span>";
		    nextHtml += "<input type='text' class='hp_input' placeholder='"+ opts.total +"'/>";
		}
		
		var html = "<div class='hp_page_warp " + type +"'>" + preHtml;
		html += "<span class='hp_page_n hp_cur_page hp_sh_page'>1</span>";
		var curTo = (opts.total > 5) ? 5 : opts.total;
		for (var i = 2; i <= curTo; i++) {
			html += "<span class='hp_page_n hp_sh_page'>"+ i +"</span>";
		}
     	var goBtn = "Go";
     	if (opts.type == 1) goBtn = "确定";
     	else if (opts.type == 2) goBtn = "GO";
	    html += nextHtml + "<span class='hp_page_go"+ neBtn +"' >"+ goBtn +"</span><div style='clear: both;'></div></div>";
		$(this).html(html);
		$(this).show();
		bindAction.call(this, opts);
	}
	function bindAction(opts) {
		opts.curIndex = 1;
		$(this).data("opts", opts);
		var that = this;
		$(this).find(".hp_pre").on("click", function () {
			var CurIndex = $(that).data("opts").curIndex;
			if (CurIndex == 1) return;
			var page = -- CurIndex;
			changeNum.call(that, page);
		});
		$(this).find(".hp_next").on("click", function () {
			var opts = $(that).data("opts");
			var CurIndex = opts.curIndex;
			var total = opts.total;
			if (CurIndex == total) return;
			var page = ++ CurIndex;
			changeNum.call(that, page);
		});
		$(this).find(".hp_page_first").on("click", function () {
			changeNum.call(that, 1);
		});
		$(this).find(".hp_page_last").on("click", function () {
			var page = $(that).data("opts").total;
			changeNum.call(that, page);
		});
		$(this).find(".hp_sh_page").on("click", function () {
			if ($(this).hasClass("hp_cur_page")) return;
			 
			var page = $(this).text();
			page = parseInt(page);
			changeNum.call(that, page);
		});
		$(this).find(".hp_page_go").on("click", function () {
			var page = $(that).find('.hp_input').val();
			var opts = $(that).data("opts");
			var CurIndex = opts.curIndex;
			var total = opts.total;
			page = parseInt(page); 
			if (page < 1 || !page) { 
				$(that).find('.hp_input').val("");
				return;
			}else if(page > total){
				$(that).find('.hp_input').val(total);
				changeNum.call(that, total);
			}else{
				changeNum.call(that, page);
			}
		});
		$(this).find(".hp_input").bind('keypress',function(event){  
            if(event.keyCode == "13") {  
  				var page = $(this).val();
  				var opts = $(that).data("opts");
				var CurIndex = opts.curIndex;
				var total = opts.total;
				page = parseInt(page);
				if (page < 1 || page > total || !page || page == CurIndex) { 
					$(this).val(""); 
					return;
				}
				changeNum.call(that, page);
            }
        });
	}
	function changeNum(page) {
		var opts = $(this).data("opts");
		opts.curIndex = page;
		$(this).data("opts", opts);
		opts.handFunc(page);
		var total = opts.total;
		var pages = $(this).find(".hp_sh_page");
		var preBtn = $(this).find(".hp_pre");
		var nextBtn = $(this).find(".hp_next");
		if (page == 1) {
			preBtn.addClass("hp_ban_btn");
		} else if (preBtn.hasClass("hp_ban_btn")) {
			preBtn.removeClass("hp_ban_btn");
		}
		if (page == total) {
			nextBtn.addClass("hp_ban_btn");
		} else if (nextBtn.hasClass("hp_ban_btn")) {
			nextBtn.removeClass("hp_ban_btn");
		}
		
		if (total < 6) {
			var index = page - 1;
			pages.eq(index).addClass("hp_cur_page").siblings().removeClass("hp_cur_page");
		} else {
			var fistP, curIndex;
			if (page == 1) {
				fistP = 1;
				curIndex = 1;
			} else if (page == 2) {
				fistP = 1;
				curIndex = 2;
			} else if (page == total) {
				fistP = page - 4;
				curIndex = 5;
			} else if (page == total - 1) {
				fistP = page - 3;
				curIndex = 4;
			} else {
				fistP = page - 2;
				curIndex = 3;
			}
			pages.eq(curIndex - 1).addClass("hp_cur_page").siblings().removeClass("hp_cur_page");
			for (var i = 0; i < 5; i++) {
				var pageList = i + fistP;
				pages.eq(i).html(pageList);
			}
			if (opts.type != 2) {
				if (page < 4) {
					$(this).find('.hp_page_first').addClass("hp_hide");
					$(this).find('.hp_pre_node').addClass("hp_hide");
				} else if (page == 4) {
					$(this).find('.hp_page_first').removeClass("hp_hide");
					$(this).find('.hp_pre_node').addClass("hp_hide");
				} else {
					$(this).find('.hp_page_first').removeClass("hp_hide");
					if (total > 6)
						$(this).find('.hp_pre_node').removeClass("hp_hide");
				}  
				if (page > total - 3) {
					$(this).find('.hp_page_last').addClass("hp_hide");
					$(this).find('.hp_next_node').addClass("hp_hide");
				} else if (page == total - 3) {
					$(this).find('.hp_page_last').removeClass("hp_hide");
					$(this).find('.hp_next_node').addClass("hp_hide");
				} else {
					$(this).find('.hp_page_last').removeClass("hp_hide");
					if (total > 6)
						$(this).find('.hp_next_node').removeClass("hp_hide");
				}
			} 
		}
	}
	function refresh() {//刷新
		var opts = $(this).data("opts");
		init.call(this, opts);
	}
	function getOrSetVal(val) {//获取或修改当前页
		var opts = $(this).data("opts");
		var page = $(this).find(".hp_cur_page").text();
		if (val) {
			if ( val > 0 && val < opts.total && page != val) {
				changeNum.call(this, val);
				return true;
			} else {
				return false;
			}
		} else {
			return page;
		}
	}
	function getOrSetTotal(val) {//获取或修改总页数
		var opts = $(this).data("opts");
		var total = opts.total;
		if (val || val == 0) {
			opts.total = val;
		    if (val == 0 || val == 1) {
		        $(this).hide();
		        return false;
		    }
			if (total != val) {
				opts.total = val;
				init.call(this, opts);
				return true;
			} else {
				changeNum.call(this, 1);
				return false;
			}
		} else {
			return total;
		}
	}
})(jQuery);