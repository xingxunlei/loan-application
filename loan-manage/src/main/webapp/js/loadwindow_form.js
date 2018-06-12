$(function() {
	var system = {

		win : false,

		mac : false,

		xll : false

	};

	var p = navigator.platform;

	system.win = p.indexOf("Win") == 0;

	system.mac = p.indexOf("Mac") == 0;

	system.x11 = (p == "X11") || (p.indexOf("Linux") == 0);

	if (system.win || system.mac || system.xll) {// 转向后台登陆页面
		window_type = "pho";

	} else {
		window_type = "pho";

	}

});

var window_type = "";

var loadwindow_form = function(formtype, himid, formclass, formid, taskid) {
	var iWidth = 850;
	var iHeight = window.screen.availHeight - 34;
	var iTop = 0;
	var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
	var var_text = "hid=" + himid + "&fc=" + formclass + "&fi=" + formid
			+ "&ti=" + taskid + "&win="+window_type;
	var new_window = window.open("/zloan-manage/js/loadwindow_bef.html", "申请单", "height=" + iHeight
			+ ", width=" + iWidth + ", top=" + iTop
			+ ", left=" + iLeft
			+ ",location=no,resizable=no,scrollbars=1");
	
	$
			.ajax({
				type : "post",
				url : "url_lock.action",
				data : {
					var_text : var_text
				},
				success : function(msg) {
					url_locked = msg;
					if (formtype == "payment" && window_type == "win") {
						window.open("/zloan-manage/page_bpm/f_finance_askpay.html?"
								+ url_locked + "", "付款申请单", "height=" + iHeight
								+ ", width=" + iWidth + ", top=" + iTop
								+ ", left=" + iLeft
								+ ",location=no,resizable=no,scrollbars=1");

					} else if (formtype == "payment" && window_type == "pho") {
						new_window.location.href = "/zloan-manage/page_bpm/f_finance_askpay.html?"
								+ url_locked + "";
					} else if (formtype == "prepayment" && window_type == "win") {
						window
								.open(
										"/zloan-manage/page_bpm/f_finance_askadvance.html?"
												+ url_locked + "",
										"预支申请单",
										"height="
												+ iHeight
												+ ", width="
												+ iWidth
												+ ", top="
												+ iTop
												+ ", left="
												+ iLeft
												+ ",location=no,resizable=no,scrollbars=1");
					} else if (formtype == "prepayment" && window_type == "pho") {
						new_window.location.href = "/zloan-manage/page_bpm/f_finance_askadvance.html?"
								+ url_locked + "";
					} else if (formtype == "expense" && window_type == "win") {
						window
								.open(
										"/zloan-manage/page_bpm/f_finance_compensationcharges.html?"
												+ url_locked + "",
										"费用报销单",
										"height="
												+ iHeight
												+ ", width="
												+ iWidth
												+ ", top="
												+ iTop
												+ ", left="
												+ iLeft
												+ ",location=no,resizable=no,scrollbars=1");
					} else if (formtype == "expense" && window_type == "pho") {
						new_window.location.href = "/zloan-manage/page_bpm/f_finance_compensationcharges.html?"
								+ url_locked + "";
					} else if (formtype == "business" && window_type == "win") {
						window
								.open(
										"/zloan-manage/page_bpm/f_finance_compensationevection.html?"
												+ url_locked + "",
										"差旅报销单",
										"height="
												+ iHeight
												+ ", width="
												+ iWidth
												+ ", top="
												+ iTop
												+ ", left="
												+ iLeft
												+ ",location=no,resizable=no,scrollbars=1");
					} else if (formtype == "business" && window_type == "pho") {
						new_window.location.href = "/zloan-manage/page_bpm/f_finance_compensationevection.html?"
								+ url_locked + "";
					} else if (formtype == "c09" && window_type == "win") {
						window
								.open(
										"/zloan-manage/page_bpm/f_per_longbeawayforb.html?"
												+ url_locked + "",
										"出差单",
										"height="
												+ iHeight
												+ ", width="
												+ iWidth
												+ ", top="
												+ iTop
												+ ", left="
												+ iLeft
												+ ",location=no,resizable=no,scrollbars=1");
					} else if (formtype == "c09" && window_type == "pho") {
						new_window.location.href = "/zloan-manage/page_bpm/f_per_longbeawayforb.html?"
								+ url_locked + "";
					} else if (formtype == "attend" && window_type == "win") {
						window.open("/zloan-manage/page_bpm/f_per_attend.html?"
								+ url_locked + "", "漏打卡/地铁延误单", "height="
								+ iHeight + ", width=" + iWidth + ", top="
								+ iTop + ", left=" + iLeft
								+ ",location=no,resizable=no,scrollbars=1");
					} else if (formtype == "attend" && window_type == "pho") {
						new_window.location.href = "/zloan-manage/page_bpm/f_per_attend.html?"
								+ url_locked + "";
					} else if (formtype == "c08" && window_type == "win") {
						window.open("/zloan-manage/page_bpm/f_per_business.html?"
								+ url_locked + "", "公出单", "height=" + iHeight
								+ ", width=" + iWidth + ", top=" + iTop
								+ ", left=" + iLeft
								+ ",location=no,resizable=no,scrollbars=1");
					} else if (formtype == "c08" && window_type == "pho") {
						new_window.location.href = "/zloan-manage/page_bpm/f_per_business.html?"
								+ url_locked + "";
					} else if (formtype == "c06" && window_type == "win") {
						window.open("/zloan-manage/page_bpm/f_per_overtime.html?"
								+ url_locked + "", "加班单", "height=" + iHeight
								+ ", width=" + iWidth + ", top=" + iTop
								+ ", left=" + iLeft
								+ ",location=no,resizable=no,scrollbars=1");
					} else if (formtype == "c06" && window_type == "pho") {
						new_window.location.href = "/zloan-manage/page_bpm/f_per_overtime.html?"
								+ url_locked + "";
					} else if (formtype == "c07" && window_type == "win") {
						window.open("/zloan-manage/page_bpm/f_per_askleave.html?"
								+ url_locked + "", "请假单", "height=" + iHeight
								+ ", width=" + iWidth + ", top=" + iTop
								+ ", left=" + iLeft
								+ ",location=no,resizable=no,scrollbars=1");
					} else if (formtype == "c07" && window_type == "pho") {
						new_window.location.href = "/zloan-manage/page_bpm/f_per_askleave.html?"
								+ url_locked + "";
					} else if (formtype == "c10" && window_type == "win") {
						window.open("/zloan-manage/page_bpm/f_per_procurement.html?"
								+ url_locked + "", "采购单", "height=" + iHeight
								+ ", width=" + iWidth + ", top=" + iTop
								+ ", left=" + iLeft
								+ ",location=no,resizable=no,scrollbars=1");
					} else if (formtype == "c10" && window_type == "pho") {
						new_window.location.href = "/zloan-manage/page_bpm/f_per_procurement.html?"
								+ url_locked + "";
					} else if (formtype == "c11" && window_type == "win") {
						window.open("/zloan-manage/page_bpm/f_finance_payment.html?"
								+ url_locked + "", "客户收益兑付申请单", "height=" + iHeight
								+ ", width=" + iWidth + ", top=" + iTop
								+ ", left=" + iLeft
								+ ",location=no,resizable=no,scrollbars=1");
					} else if (formtype == "c11" && window_type == "pho") {
						new_window.location.href = "/zloan-manage/page_bpm/f_finance_payment.html?"
								+ url_locked + "";
					}
				}
			});
}

var loadwindow_userinfo = function(himid,brroid) {
	var iWidth = 1350;
	if (window.screen.availWidth < 1350) {
		iWidth = window.screen.availWidth;
	}
	var iHeight = window.screen.availHeight - 34;
	var iTop = 0;
	var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
	var var_text = "hi=" + himid + "_"+brroid;
	$.ajax({
		type : "post",
		url : "/zloan-manage/url/url_lock.action",
		data : {
			var_text : var_text
		},
		success : function(msg) {
			url_locked = msg;
			if(window_type == "pho"){
				window.open("/zloan-manage/page_check/loadwindow_userinfo.html?"
						+ url_locked + "", "员工信息查看", "height=" + iHeight
						+ ", width=" + iWidth + ", top=" + iTop + ", left=" + iLeft
						+ ",location=no,resizable=no");
			}else{
				window.location.href = "/zloan-manage/page_check/loadwindow_userinfo.html?"
					+ url_locked + "";
			}
			
		}
	});
}

var loadwindow_userinfo_kf = function(himid,brroid) {
	var iWidth = 1350;
	if (window.screen.availWidth < 1350) {
		iWidth = window.screen.availWidth;
	}
	var iHeight = window.screen.availHeight - 34;
	var iTop = 0;
	var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
	var var_text = "hi=" + himid + "_"+brroid;
	$.ajax({
		type : "post",
		url : "/zloan-manage/url/url_lock.action",
		data : {
			var_text : var_text
		},
		success : function(msg) {
			url_locked = msg;
			if(window_type == "pho"){
				window.open("/zloan-manage/page_check/loadwindow_userinfo_kf.html?"
						+ url_locked + "", "员工信息查看", "height=" + iHeight
						+ ", width=" + iWidth + ", top=" + iTop + ", left=" + iLeft
						+ ",location=no,resizable=no");
			}else{
				window.location.href = "/zloan-manage/page_check/loadwindow_userinfo_kf.html?"
					+ url_locked + "";
			}
			
		}
	});
}

var layer_alert = function(himid, brroid, borrNum) {
	var iWidth = 1350;
	if (window.screen.availWidth < 1350) {
		iWidth = window.screen.availWidth;
	}
	var iHeight = window.screen.availHeight - 80;
	var iTop = 0;
	var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
	var var_text = "hi=" + himid + "_"+brroid + "_" + borrNum;
	$.ajax({
		type : "post",
		url : "/zloan-manage/url/url_lock.action",
		data : {
			var_text : var_text
		},
		success : function(msg) {
			url_locked = msg;
			if(window_type == "pho"){
				window.open("/zloan-manage/page_loan/layer_alert.html?"
						+ url_locked + "", "员工信息查看", "height=" + iHeight
						+ ", width=" + iWidth + ", top=" + iTop + ", left=" + iLeft
						+ ",location=no,resizable=no");
			}else{
				window.location.href = "/zloan-manage/page_loan/layer_alert.html?"
					+ url_locked + "";
			}
			
		}
	});
}

var baikelu_alert = function(borrNum) {
    var iWidth = 1350;
    if (window.screen.availWidth < 1350) {
        iWidth = window.screen.availWidth;
    }
    var iHeight = window.screen.availHeight - 80;
    var iTop = 0;
    var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
    var var_text = "hi=" + "_" + borrNum;
    $.ajax({
        type : "post",
        url : "/zloan-manage/url/url_lock.action",
        data : {
            var_text : var_text
        },
        success : function(msg) {
            url_locked = msg;
            if(window_type == "pho"){
                window.open("/zloan-manage/page_check/baikelu_alert.html?"
                    + url_locked + "", "", "height=" + iHeight
                    + ", width=" + iWidth + ", top=" + iTop + ", left=" + iLeft
                    + ",location=no,resizable=no");
            }else{
                window.location.href = "/zloan-manage/page_check/baikelu_alert.html?"
                    + url_locked + "";
            }

        }
    });
}

var loanRemark_alert = function(borrNum) {
    var iWidth = 1350;
    if (window.screen.availWidth < 1350) {
        iWidth = window.screen.availWidth;
    }
    var iHeight = window.screen.availHeight - 80;
    var iTop = 0;
    var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
    var var_text = "hi=" + "_" + borrNum;
    $.ajax({
        type : "post",
        url : "/zloan-manage/url/url_lock.action",
        data : {
            var_text : var_text
        },
        success : function(msg) {
            url_locked = msg;
            if(window_type == "pho"){
                window.open("/zloan-manage/page_check/loanRemark_alert.html?"
                    + url_locked + "", "", "height=" + iHeight
                    + ", width=" + iWidth + ", top=" + iTop + ", left=" + iLeft
                    + ",location=no,resizable=no");
            }else{
                window.location.href = "/zloan-manage/page_check/loanRemark_alert.html?"
                    + url_locked + "";
            }

        }
    });
}

var loadwindow_userinfo_log = function(himid) {
	var iWidth = 1350;
	if (window.screen.availWidth < 1350) {
		iWidth = window.screen.availWidth;
	}
	var iHeight = window.screen.availHeight - 34;
	var iTop = 0;
	var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
	var var_text = "hi=" + himid + "";
	$.ajax({
		type : "post",
		url : "url_lock.action",
		data : {
			var_text : var_text
		},
		success : function(msg) {
			url_locked = msg;
			if(window_type == "pho"){
				window.open("/zloan-manage/page_per/loadwindow_userinfo_log.html?"
						+ url_locked + "", "员工信息日志查看", "height=" + iHeight
						+ ", width=" + iWidth + ", top=" + iTop + ", left=" + iLeft
						+ ",location=no,resizable=no");
			}else{
				window.location.href = "/zloan-manage/page_per/loadwindow_userinfo_log.html?"
					+ url_locked + "";
			}
			
		}
	});
}
var loadwindow_CompanyNews = function(himid, param) {
	var iWidth = 850;
	if (window.screen.availWidth < 850) {
		iWidth = window.screen.availWidth;
	}
	var iHeight = window.screen.availHeight - 34;
	var iTop = 0;
	var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
	var var_text = "hi=" + himid + "&param=" + param;
	$.ajax({
		type : "post",
		url : "url_lock.action",
		data : {
			var_text : var_text
		},
		success : function(msg) {
			url_locked = msg;
			if(window_type == "pho"){
			window.open("/zloan-manage/views/loadwindow_companynews.html?"
					+ url_locked + "", "信息查看", "height=" + iHeight + ", width="
					+ iWidth + ", top=" + iTop + ", left=" + iLeft
					+ ",location=no,resizable=no");
			}else{
				window.location.href ="/zloan-manage/views/loadwindow_companynews.html?"
					+ url_locked + "";
			}
		}
	});
}
var loadwindow_photoupload = function(himid, flags) {
	var iWidth = 900;
	if (window.screen.availWidth < 900) {
		iWidth = window.screen.availWidth;
	}
	var iHeight = window.screen.availHeight - 300;
	var iTop = 70;
	var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
	var var_text = "hi=" + himid + "&f=" + flags;
	$.ajax({
		type : "post",
		url : "url_lock.action",
		data : {
			var_text : var_text
		},
		success : function(msg) {
			url_locked = msg;
			if(window_type == "pho"){
			window.open("/zloan-manage/page_per/loadwindow_photo.html?" + url_locked
					+ "", "头像上传", "height=" + iHeight + ", width=" + iWidth
					+ ", top=" + iTop + ", left=" + iLeft
					+ ",location=no,resizable=no");
			}else{
				window.location.href ="/zloan-manage/page_per/loadwindow_photo.html?" + url_locked
				+ "";
			}
		}
	});
}

function showMessage(msg) {
	message_alert("提示", msg);
}

// the popup for the message
var message_alert = function(titles, texts) {
	$("#message_alert").dxPopup({
		title : titles,
		visible : true,
		height : 200,
		width : 400,
		showCloseButton : false,
		onHiding : function() {
			$("#message_text").empty();
		},
	});
	$("#message_text").html("<span style='color:black'>" + texts + "</span>");
	$("#message_ok").dxButton({
		text : "确定",
		hint : "我知道了",
		// height: 35,
		// width: 70,
		disabled : false,
		onClick : function() {
			$("#message_alert").dxPopup({
				visible : false,
			});
		}
	});

}
