$(function() {

	getcookie_all();
	
});


var cookiearray_all = new Array();

var treeindex_p = 0;
var treearray_p = new Array("r","f","p","a","k","b", "o", "s", "x");

var treearray_for_p = new Array("ra","rb","fk","pm","pu", "pc", "ps", "pe", "pd","pmy", "pmn", "pl", "as", "ad","kr","kw","ka","bw","ba","bm","bb","od",
		"oj", "op", "og", "ol", "os", "odm", "sp", "se", "xf", "xp", "xg",
		"xs", "xsm","xj","xa","xb");



// ////////////////////////////////
// 设置cookie
function setCookie(cname, cvalue, exdays) {
	var d = new Date();
	d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
	var expires = "expires=" + d.toUTCString();
	document.cookie = cname + "=" + cvalue + "; " + expires;
}
// 获取cookie
function getCookie(cname) {
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for ( var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ')
			c = c.substring(1);
		if (c.indexOf(name) != -1)
			return c.substring(name.length, c.length);
	}
	return "";
}
// 清除cookie
function clearCookie(name) {
	setCookie(name, "", -1);
}

// ///////////////////////////////
var getcookie_all = function() {
	try {
		cookiearray_all = $.cookie("result").split(",");
		userid = cookiearray_all[0].split("_")[1];
		username = cookiearray_all[1].split("_")[1];
	} catch (e) {
		window.location = "/jhhoa/login.html";
	}
	$('#trees').accordion({
		animate : true
	});
};

var getTree = function(){
	for ( var i = 0; i < treearray_p.length; i++) {
		var flag = false;
		for ( var j = 2; j < cookiearray_all.length; j++) {
			if (treearray_p[i] == cookiearray_all[j].split("_")[0]) {
				flag = true;
				treeindex_p = treeindex_p + 1;
				break;
			}
		}
		if (flag) {
			$("#"+treearray_p[i]+"").css('display', 'block');
			flag = false;
		}

	}
	for ( var i = 0; i < treearray_for_p.length; i++) {
		for ( var j = 2; j < cookiearray_all.length; j++) {
			if (treearray_for_p[i] == cookiearray_all[j].split("_")[0]) {
				$("#" + treearray_for_p[i] + "").css('display', 'block');
				break;
			}
		}
	}

}