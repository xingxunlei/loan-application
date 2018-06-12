
var feedback = function() {
	setToolbar_ya();
	loadtable_ya();
}

var setToolbar_ya = function() {
    checkPageEnabled("ym-ya");
}
var loadtable_ya = function() {

	$.ajax({
		type : "post",
		url : "info/getFeedbackList.action",
		success : function(msg) {
			//var jsonTrans = eval("(" + msg + ")").result;
			loadtable2_ya(msg);
		}
	});
}

var loadtable2_ya = function(jsonobj) {
	$("#userTable").dxDataGrid({
		dataSource : {
			store : {
				type : "array",
				data : jsonobj
			}
		},
		searchPanel : {
			visible : true,
			width : 240,
			placeholder : "搜索..."
		},
		"export" : {
			enabled : true,
			fileName : "Employees",
			allowExportSelectedData : true
		},
		headerFilter : {
			visible : true
		},
		filterRow : {
			visible : true,
			applyFilter : "auto"
		},
		rowAlternationEnabled : true,
		showRowLines : true,
//		selection : {
//			mode : "multiple"
//		},
		allowColumnReordering : true,
		allowColumnResizing : true,
		columnAutoWidth : true,
		columnChooser : {
			title : "列选择器",
			enabled : true,
			emptyPanelText : '把你想隐藏的列拖拽到这里...'
		},
		columnFixing : {
			enabled : true
		},
		paging : {
			pageSize : 15,
		},
		pager : {
			showPageSizeSelector : true,
			allowedPageSizes : [10, 15, 30, 45, 60 ],
			showInfo : true,
			infoText : '第{0}页 . 共{1}页'
		},
		columns : [ {
			dataField : "id",
			caption : "ID",
			alignment : "center",
		}, {
			dataField : "createTime",
			caption : "反馈时间",
			alignment : "center",
			dataType: "date" ,
            format: function (date) {
                var month = date.getMonth() + 1,
                    day = date.getDate(),
                    year = date.getFullYear(),
                    hours = date.getHours(),
                    minutes = date.getMinutes(),
                    seconds = date.getSeconds();

                if(month < 10){
                    month = "0" + month;
                }
                if(day < 10){
                    day = "0" + day;
                }
                if(hours < 10){
                    hours = "0" + hours;
                }
                if(minutes < 10){
                    minutes = "0" + minutes;
                }
                if(seconds < 10){
                    seconds = "0" + seconds;
                }

                return year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
            },
		}, {
			dataField : "phone",
			caption : "反馈用户",
			alignment : "center",
		}, {
			dataField : "content",
			caption : "反馈内容",
			alignment : "left",
		}]
	});
}

