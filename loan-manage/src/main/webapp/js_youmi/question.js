
var question = function() {
	setToolbar_yb();
	loadtable_yb();
	
	window_add_yb();
	window_edit_yb();

}

var setToolbar_yb = function() {
    checkPageEnabled("ym-yb");

	$("#tool_add").dxButton({
		hint : "新增",
		text:"新增",
		icon : "add",
		disabled : disableButton("ym-yb",0),
		onClick : function() {
			$("#window_add").dxPopup({
				visible : true,
			});
			box_add_value_yb();
		}
	});
	
	$("#tool_edit").dxButton({
		hint : "编辑",
		text:"编辑",
		icon : "edit",
		disabled : true,
		onClick : function() {
			var dataGrid = $('#userTable').dxDataGrid('instance');
			var selectobj_param = dataGrid.getSelectedRowsData();
			$("#window_edit").dxPopup({
				visible : true,
			});
			box_edit_value_yb(selectobj_param[0]);
		}
	});

	

}

var loadtable_yb = function() {

	$.ajax({
		type : "post",
		url : "info/getAllQuestionList.action",
		success : function(msg) {
			//var jsonTrans = eval("(" + msg + ")").result;
			loadtable2_yb(msg);
		}
	});
}
var loadtable2_yb = function(jsonobj) {
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
		selection : {
			mode : "multiple"
		},
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
		onSelectionChanged : function(data) {
			$("#tool_edit").dxButton({
				disabled : (data.selectedRowsData.length != 1) || disableButton("ym-yb",1),
			});
		},
		columns : [ {
			dataField : "id",
			caption : "ID",
			alignment : "center",
		}, {
			dataField : "questionType",
			caption : "问题类型",
			alignment : "center",
		}, {
			dataField : "questionText",
			caption : "标题",
			alignment : "center",
		}, {
			dataField : "answer",
			caption : "内容",
			alignment : "left",
		}, {
			dataField : "status",
			caption : "启用状态",
			alignment : "center",
			calculateCellValue: function(rowData){
				if(rowData.status=="1"){
					return "启用";
				}else{
					return "禁用";
				}
				
			}
		}, {
			dataField : "employName",
			caption : "操作人",
			alignment : "center",
			
		}, {
			dataField : "updateDate",
			caption : "操作时间",
			alignment : "center",
			dataType: "date" ,
		}]
	});
}

//查看详情
var window_add_yb = function() {
	$("#window_add").dxPopup({
		showTitle : true,
		title : '新增问题',
		maxWidth : 650,
		maxHeight : 554,
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			setToolbar_yb();
			loadtable_yb();
		},
	});
}

var window_edit_yb = function() {
	$("#window_edit").dxPopup({
		showTitle : true,
		maxWidth : 650,
		maxHeight : 554,
		title : '编辑问题',
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			setToolbar_yb();
			loadtable_yb();
		},
	});
}


var box_add_value_yb = function() {
	var questionText = "";
	var answer = "";
	var status = "1";
	$("#status").dxCheckBox({
	    text: '启用',
	    value: true,
	    onValueChanged: function(data) {
	    	if(data.value){
	    		status="1";
	    	}else{
	    		status="0";
	    	}
		}
	});
	$("#questionText").dxTextArea({
		placeholder : "必填",
		height : 55,
		value:questionText,
		showClearButton : false,
		onValueChanged : function(data) {
			questionText = data.value;
		}
	});
	$("#answer").dxTextArea({
		placeholder : "必填",
		height : 250,
		//width:400,
		value:answer,
		showClearButton : false,
		onValueChanged : function(data) {
			answer = data.value;
		}
	});
	// 增加保存按钮
	$("#submit_add").dxButton({
		text : "确定",
		hint : "确认发送",
		icon : "todo",
		// height: 35,
		// width: 70,
		disabled : false,
		onClick : function() {
			if (questionText == '') {
				showMessage("必须填写标题！");
				return;
			}else if (answer==""){
				showMessage("必须填写内容！");
				return;
			}
			var conmitdata = {
					questionType:"常见问题",
					employNum:usernum,
					questionText :questionText,
					 answer:answer,
					 status :status,
			};
			$.ajax({
				type : "POST",
				url : "info/insertQuestion.action",
				data : conmitdata,
				success : function(msg) {
					if (msg.code >=0) {
						$("#window_add").dxPopup({
							visible : false,
						});
						
					} else {
						showMessage("操作失败！");
					}
				}
			});
		}
	});
}

var box_edit_value_yb = function(selectobj) {
	var id = selectobj.id;
	var questionText = selectobj.questionText;
	var answer = selectobj.answer;
	var status = selectobj.status;
	$("#status2").dxCheckBox({
	    text: '启用',
	    value: status=="1",
	    onValueChanged: function(data) {
	    	if(data.value){
	    		status="1";
	    	}else{
	    		status="0";
	    	}
		}
	});
	$("#questionText2").dxTextArea({
		placeholder : "必填",
		height : 55,
		value:questionText,
		showClearButton : false,
		onValueChanged : function(data) {
			questionText = data.value;
		}
	});
	$("#answer2").dxTextArea({
		placeholder : "必填",
		height : 250,
		//width:400,
		value:answer,
		showClearButton : false,
		onValueChanged : function(data) {
			answer = data.value;
		}
	});
	// 增加保存按钮
	$("#submit_edit").dxButton({
		text : "确定",
		hint : "确认发送",
		icon : "todo",
		// height: 35,
		// width: 70,
		disabled : false,
		onClick : function() {
			if (questionText == '') {
				showMessage("必须填写标题！");
				return;
			}else if (answer==""){
				showMessage("必须填写内容！");
				return;
			}
			var conmitdata = {
					id:id,
					questionType:"常见问题",
					employNum:usernum,
					questionText :questionText,
					 answer:answer,
					 status :status,
			};
			$.ajax({
				type : "POST",
				url : "info/UpdateQuestion.action",
				data : conmitdata,
				success : function(msg) {
					if (msg.code >=0) {
						$("#window_edit").dxPopup({
							visible : false,
						});
						
					} else {
						showMessage("操作失败！");
					}
				}
			});
		}
	});
}


