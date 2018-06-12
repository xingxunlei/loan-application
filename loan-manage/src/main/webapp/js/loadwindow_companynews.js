$(function() {
	geturl_info();
});
var userid = "";
var param = 0;
var geturl_info = function(){
	
	var url = location.href;
	var data_url = url.split("?")[1];
	$.ajax({
		type : "post",
		url : "url_open.action",
		data : {
			var_text_locked : data_url
		},
		success : function(msg) {
			data_url = msg;
			userid = data_url.split("&")[0].split("=")[1];
			param = data_url.split("&")[1].split("=")[1];
			getCompanyNews_info(userid,param);
			}
		});
}

var getCompanyNews_info= function(userid,param){
	$.ajax({
		type : "POST",
		url : "selectOneArticle.action",
		data:{
			userid:userid,
			param :param,
		},
		success : function(msg) {
			var jsonobj = eval("("+msg+")");
			var jsonresult = jsonobj.result;
			$("#title").html(jsonresult.title);
			$("#author").html(jsonresult.userName);
			var dates =jsonresult.creation_date ;
			dates = dates.split(" ")[0];
			$("#dates").html(dates);
			$("#text").html(jsonresult.comment);
			
		}
	});
	
}