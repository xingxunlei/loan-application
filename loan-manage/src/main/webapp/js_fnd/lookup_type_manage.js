
var lookup_type_manage = function() {
	setToolbar_ga();
	loadtable_ga();

	window_add_ga();
	window_edit_ga();
	window_del_ga();
	window_find_ga();
	window_del_value();
	window_add_value();
	window_edit_value();
}

var setToolbar_ga = function() {

    checkPageEnabled("ym-ga");
	
	$("#tool_add").dxButton({
		hint : "添加",
		icon : "add",
		disabled : disableButton("ym-ga",0),
		onClick : function() {
			$("#window_add").dxPopup({
				visible : true,
			});
			box_add_ga();
		}
	});
	$("#tool_sync").dxButton({
		hint : "同步用户",
		text:"同步",
		icon : "repeat",
		disabled : disableButton("ym-ga",4),
		onClick : function() {
			 $.ajax({
	                url: 'user/sync.action',
	                success: function(result) {
	                	alert(result);
	                },
	                error: function() {
	                    deferred.reject("Data Loading Error");
	                },
	                timeout: 50000
	            });
		}
	});
	$("#tool_edit").dxButton({
		hint : "修改",
		icon : "edit",
		disabled : true,
		onClick : function() {

			var dataGrid = $('#userTable').dxDataGrid('instance');
			var selectobj_param = dataGrid.getSelectedRowsData();
			if(selectobj_param.length > 1){
				showMessage("只能选择一条数据修改！");
			}else{				
				$("#window_edit").dxPopup({
					visible : true,
				});
				box_edit_ga(selectobj_param[0]);
			}
		}
	});
	
	$("#tool_del").dxButton({
		hint : "删除",
		icon : "close",
		disabled : true,
		onClick : function() {

			var dataGrid = $('#userTable').dxDataGrid('instance');
			var selectobj_param = dataGrid.getSelectedRowsData();
			var idfordel ="";
			var meanings = "";
			for(var i = 0;i<selectobj_param.length;i++ ){
				if(i==selectobj_param.length -1){
					idfordel += selectobj_param[i].id + ",";
					meanings += selectobj_param[i].meaning;
				}else{
					idfordel += selectobj_param[i].id + ",";
					meanings += selectobj_param[i].meaning + ",";
				}
			}
			
			$("#window_del").dxPopup({
				visible : true,
			});
			box_del_ga(selectobj_param,idfordel,meanings);
			
		}
	});
	
	$("#tool_find").dxButton({
		hint : "查看详情",
		icon : "find",
		disabled : true,
		onClick : function() {
			var dataGrid = $('#userTable').dxDataGrid('instance');
			var selectobj_param = dataGrid.getSelectedRowsData();
			$("#window_find").dxPopup({
				visible : true,
				title : "编码详细----"+selectobj_param[0].meaning,
			});
			box_find_ga(selectobj_param[0]);
		}
	});
};


//加载界面
var loadtable_ga = function() {

	$.ajax({
		type : "post",
		url : "info/getCodeTypeList.action",
		success : function(msg) {
			loadtable2_ga(msg);
		}
	});
}

var loadtable2_ga = function(jsonobj) {
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
				disabled : data.selectedRowsData.length != 1 || disableButton("ym-ga",1) ,
			});
			
			$("#tool_del").dxButton({
				disabled : !data.selectedRowsData.length || disableButton("ym-ga",2) ,
			});
			
			$("#tool_find").dxButton({
				disabled : data.selectedRowsData.length != 1 || disableButton("ym-ga",3) ,
			});
		},
		columns : [ {
			dataField : "codeType",
			caption : "类型",
			alignment : "center",
		}, {
			dataField : "meaning",
			caption : "含义",
			alignment : "center",
		}, {
			dataField : "description",
			caption : "说明",
			alignment : "center",
		}]
	});
}

//加载新增界面
var window_add_ga = function() {
	$("#window_add").dxPopup({
		showTitle : true,
		maxWidth : 600,
		maxHeight : 500,
		title : '新增',
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			loadtable_ga();
		},
	});
}

//加载增加界面
var box_add_ga = function() {
	
	var w_lookuptype = "";
	var w_lookuptypemeaning = "";
	var w_description = "";
	
	$("#w_lookuptype").dxTextBox({
		mode : "text",
		placeholder : "必填",
		showClearButton : false,
		value :"",
		onValueChanged : function(data) {
			w_lookuptype=data.value;
	    }
	});
	
	$("#w_lookuptypemeaning").dxTextBox({
		mode : "text",
		placeholder : "必填",
		showClearButton : false,
		value :"",
		onValueChanged : function(data) {
			w_lookuptypemeaning=data.value;
	    }
	});
	
	$("#w_description").dxTextBox({
		mode : "text",
		placeholder : "",
		showClearButton : true,
		value :"",
		onValueChanged : function(data) {
			w_description=data.value;
	    }
	});

	// 增加保存按钮
	$("#submitadd_ga").dxButton({
		text : "保存",
		hint : "保存",
		icon : "todo",
		// height: 35,
		// width: 70,
		disabled : false,
		onClick : function() {
			
			if (w_lookuptype == '') {
				showMessage("代码不能为空");
				return;
			}
		
			if (w_lookuptypemeaning == '') {
				showMessage("含义不能为空");
				return;
			}
			var conmitdata = {
					codeType:w_lookuptype,
					meaning:w_lookuptypemeaning,
					description:w_description,
			};
			$.ajax({
				type : "POST",
				url : "info/insertCodeType.action",
				data : conmitdata,
				success : function(msg) {
					if (msg.code > 0) {
						showMessage("添加成功");
						$("#window_add").dxPopup({
							visible : false,
						});
					} else {
						showMessage("添加失败");
					}
				}
			});
		}
	});
}

var window_edit_ga = function() {
	$("#window_edit").dxPopup({
		showTitle : true,
		maxWidth : 500,
		title : '修改',
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			loadtable_ga();
		},
	});
}

//加载修改界面
var box_edit_ga = function(selectobj) {
	var id =selectobj.id; 
	var u_w_lookuptype = selectobj.codeType;
	var u_w_lookuptypemeaning = selectobj.meaning;
	var u_w_description = selectobj.description;

	$("#u_w_lookuptype").dxTextBox({
		mode : "text",
		placeholder : "必填",
		showClearButton : false,
		value :u_w_lookuptype,
		readOnly : true,
		onValueChanged : function(data) {
			u_w_lookuptype=data.value;
	    }
	});
	
	$("#u_w_lookuptypemeaning").dxTextBox({
		mode : "text",
		placeholder : "必填",
		showClearButton : false,
		value :u_w_lookuptypemeaning,
		onValueChanged : function(data) {
			u_w_lookuptypemeaning=data.value;
	    }
	});
	
	$("#u_w_description").dxTextBox({
		mode : "text",
		placeholder : "",
		showClearButton : true,
		value :u_w_description,
		onValueChanged : function(data) {
			u_w_description=data.value;
	    }
	});

	// 修改保存按钮
	$("#submitedit_ga").dxButton({
		text : "保存",
		hint : "保存",
		icon : "todo",
		// height: 35,
		// width: 70,
		disabled : false,
		onClick : function() {
			
			if (u_w_lookuptype == '') {
				showMessage("代码不能为空");
				return;
			}
		
			if (u_w_lookuptypemeaning == '') {
				showMessage("含义不能为空");
				return;
			}
			
			var conmitdata = {
					id:id,
					codeType:u_w_lookuptype,
					meaning:u_w_lookuptypemeaning,
					description:u_w_description,
			};
			$.ajax({
				type : "POST",
				url : "info/UpdateCodeType.action",
				data : conmitdata,
				success : function(msg) {
					if (msg.code > 0) {
						showMessage("修改成功");
						$("#window_edit").dxPopup({
							visible : false,
						});
					} else {
						showMessage("修改失败");
					}
				}
			});
		}
	});
}

//删除界面
var window_del_ga = function() {
	$("#window_del").dxPopup({
		showTitle : true,
		maxWidth : 500,
		maxHeight:300,
//		title : '修改',
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			loadtable_ga();
		},
	});
}

//加载删除界面
var box_del_ga = function(selectobj,idfordel,meanings) {
	
	$("#showdelnames_ga").dxTextBox({
		mode : "text",
		placeholder : "必填",
		showClearButton : false,
		value :meanings,
		readOnly :true,
		onValueChanged : function(data) {
			
	    }
	});
	
	$("#submit_del_ga").dxButton({
		text : "删除",
		hint : "删除",
		icon : "todo",
		// height: 35,
		// width: 70,
		disabled : false,
		onClick : function() {

			$.ajax({
				type : "POST",
				url : "info/deleteCodeType.action",
				data : {
					'idfordel' : idfordel
				},
				success : function(msg) {
					if (msg.code > 0) {
						showMessage("删除成功！");
						$("#window_del").dxPopup({
							visible : false,
						});
					} else {
						showMessage("删除失败！");
					}
				}
			});
		}
	});
}

//查看详情
var window_find_ga = function() {
	$("#window_find").dxPopup({
		showTitle : true,
		title : '编码详细',
		width : "95%",
		height: "88%" ,
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			loadtable_ga();
		},
	});
}

//查看详情
var box_find_ga = function(selectobj) {
	setToolbar_value(selectobj);
	loadtable_value(selectobj);
	
}

var setToolbar_value = function(selectobj) {
	$("#tool_add_value").dxButton({
		hint : "添加",
		icon : "add",
		disabled : disableButton("ym-ga",0),
		onClick : function() {
			$("#window_add_value").dxPopup({
				visible : true,
			});
			box_add_value(selectobj);
		}
	});
	
	$("#tool_edit_value").dxButton({
		hint : "修改",
		icon : "edit",
		disabled : true,
		onClick : function() {

			var dataGrid = $('#userTable_value').dxDataGrid('instance');
			var selectobj_param = dataGrid.getSelectedRowsData();
			if(selectobj_param.length > 1){
				showMessage("只能选择一条数据修改！");
			}else{				
				$("#window_edit_value").dxPopup({
					visible : true,
				});
				box_edit_value(selectobj_param[0],selectobj);
			}
		}
	});
	
	$("#tool_del_value").dxButton({
		hint : "删除",
		icon : "close",
		disabled : true,
		onClick : function() {

			var dataGrid = $('#userTable_value').dxDataGrid('instance');
			var selectobj_param = dataGrid.getSelectedRowsData();
			var idfordel ="";
			var meanings = "";
			for(var i = 0;i<selectobj_param.length;i++ ){
				if(i==selectobj_param.length -1){
					idfordel += selectobj_param[i].id + ",";
					meanings += selectobj_param[i].meaning;
				}else{
					idfordel += selectobj_param[i].id + ",";
					meanings += selectobj_param[i].meaning + ",";
				}
			}
			
			$("#window_del_value").dxPopup({
				visible : true,
			});
			box_del_value(selectobj,selectobj_param,idfordel,meanings);
			
		}
	});
	
}

//加载界面
var loadtable_value = function(selectobj) {

	$.ajax({
		type : "post",
		url : "info/getCodeValueListByCode.action",
		data : {
			"code_type":selectobj.codeType,
		},
		success : function(msg) {
			loadtable2_value(msg);
		}
	});
}

var loadtable2_value = function(jsonobj) {
	$("#userTable_value").dxDataGrid({
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

			$("#tool_edit_value").dxButton({
				disabled : data.selectedRowsData.length != 1  || disableButton("ym-ga",1),
			});
			
			$("#tool_del_value").dxButton({
				disabled : !data.selectedRowsData.length  || disableButton("ym-ga",2),
			});
		},
		columns : [ {
			dataField : "codeCode",
			caption : "类型",
			alignment : "center",
		}, {
			dataField : "meaning",
			caption : "含义",
			alignment : "center",
		}, {
			dataField : "description",
			caption : "说明",
			alignment : "center",
		}, {
			dataField : "enabledFlag",
			caption : "状态",
			alignment : "center",
			calculateDisplayValue : function(rowData) { 
				if(rowData.enabledFlag == "y"){
					return "启用";
				}else if(rowData.enabledFlag == "n"){
					return "禁用";
				}else{
					return rowData.enabledFlag;
				}
			}
		}]
	});
}

//加载新增界面
var window_add_value = function() {
	$("#window_add_value").dxPopup({
		showTitle : true,
		maxWidth : 600,
		maxHeight : 500,
		title : '新增',
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			
		},
	});
}

//加载修改界面
var window_edit_value = function() {
	$("#window_edit_value").dxPopup({
		showTitle : true,
		maxWidth : 600,
		maxHeight : 500,
		title : '修改',
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			
		},
	});
}

//加载删除界面
var window_del_value = function() {
	$("#window_del_value").dxPopup({
		showTitle : true,
		maxWidth : 600,
		maxHeight : 300,
		title : '删除',
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			
		},
	});
}

var box_add_value = function(selectobj) {
	
	var w_lookupcode_value = "";
	var w_lookupcodemeaning_value = "";
	var w_description_value = "";
	var w_checks_value = "y";
	
	$("#w_lookupcode_value").dxTextBox({
		mode : "text",
		placeholder : "必填",
		showClearButton : false,
		value :"",
		onValueChanged : function(data) {
			w_lookupcode_value=data.value;
	    }
	});
	
	$("#w_lookupcodemeaning_value").dxTextBox({
		mode : "text",
		placeholder : "必填",
		showClearButton : false,
		value :"",
		onValueChanged : function(data) {
			w_lookupcodemeaning_value=data.value;
	    }
	});
	
	$("#w_description_value").dxTextBox({
		mode : "text",
		placeholder : "",
		showClearButton : true,
		value :"",
		onValueChanged : function(data) {
			w_description_value=data.value;
	    }
	});
	var selectBoxData = [{ "name": "启用", "id": "y" },
	                     { "name": "禁用", "id": "n" }];
	$("#w_checks_value").dxSelectBox({
	    dataSource: selectBoxData,
	    placeholder : "必填",
	    value : "y",
	    valueExpr: 'id',
	    displayExpr: 'name',
	    showClearButton : false,
		onValueChanged : function(e) {
			w_checks_value = e.value;
		}
	});

	// 增加保存按钮
	$("#submitadd_ga_value").dxButton({
		text : "保存",
		hint : "保存",
		icon : "todo",
		// height: 35,
		// width: 70,
		disabled : false,
		onClick : function() {
			
			if (w_lookupcode_value == '') {
				showMessage("代码不能为空");
				return;
			}
		
			if (w_lookupcodemeaning_value == '') {
				showMessage("含义不能为空");
				return;
			}
			
			if (w_checks_value == '') {
				showMessage("状态不能为空");
				return;
			}
			
			var conmitdata = {
					codeType:selectobj.codeType,
					codeCode:w_lookupcode_value,
					meaning:w_lookupcodemeaning_value,
					description:w_description_value,
					enabledFlag:w_checks_value,
			};
			$.ajax({
				type : "POST",
				url : "info/insertCodeValue.action",
				data : conmitdata,
				success : function(msg) {
					if (msg.code > 0) {
						showMessage("添加成功");
						$("#window_add_value").dxPopup({
							visible : false,
						});
						loadtable_value(selectobj);
					} else {
						showMessage("添加失败");
					}
				}
			});
		}
	});
}

//修改的
var box_edit_value = function(selectobj,selectobjold) {
	
	var u_w_lookupcode_value = selectobj.codeCode;
	var u_w_lookupcodemeaning_value = selectobj.meaning;
	var u_w_description_value = selectobj.description;
	var u_w_checks_value = selectobj.enabledFlag;
	
	$("#u_w_lookupcode_value").dxTextBox({
		mode : "text",
		placeholder : "必填",
		showClearButton : false,
		value :u_w_lookupcode_value,
		readOnly : true,
		onValueChanged : function(data) {
			u_w_lookupcode_value=data.value;
	    }
	});
	
	$("#u_w_lookupcodemeaning_value").dxTextBox({
		mode : "text",
		placeholder : "必填",
		showClearButton : false,
		value :u_w_lookupcodemeaning_value,
		onValueChanged : function(data) {
			u_w_lookupcodemeaning_value=data.value;
	    }
	});
	
	$("#u_w_description_value").dxTextBox({
		mode : "text",
		placeholder : "",
		showClearButton : true,
		value :u_w_description_value,
		onValueChanged : function(data) {
			u_w_description_value=data.value;
	    }
	});
	var selectBoxData = [{ "name": "启用", "id": "y" },
	                     { "name": "禁用", "id": "n" }];
	$("#u_w_checks_value").dxSelectBox({
	    dataSource: selectBoxData,
	    placeholder : "必填",
	    value : u_w_checks_value,
	    valueExpr: 'id',
	    displayExpr: 'name',
	    showClearButton : false,
		onValueChanged : function(e) {
			u_w_checks_value = e.value;
		}
	});

	// 增加保存按钮
	$("#submitedit_ga_value").dxButton({
		text : "保存",
		hint : "保存",
		icon : "todo",
		// height: 35,
		// width: 70,
		disabled : false,
		onClick : function() {
		
			if (u_w_lookupcodemeaning_value == '') {
				showMessage("含义不能为空");
				return;
			}
			
			if (u_w_checks_value == '') {
				showMessage("状态不能为空");
				return;
			}
			
			var conmitdata = {
					//codeType:selectobjold.codeType,
					id:selectobj.id,
					codeCode:u_w_lookupcode_value,
					meaning:u_w_lookupcodemeaning_value,
					description:u_w_description_value,
					enabledFlag:u_w_checks_value,
			};
			$.ajax({
				type : "POST",
				url : "info/UpdateCodeValue.action",
				data : conmitdata,
				success : function(msg) {
					
					if (msg.code > 0) {
						showMessage("修改成功！");
						$("#window_edit_value").dxPopup({
							visible : false,
						});
						loadtable_value(selectobjold);
					} else {
						showMessage("修改失败！");
					}	
				}
			});
		}
	});
}

var box_del_value = function(selectobjold,selectobj,idfordel,meanings) {
	
	$("#showdelnames_ga_value").dxTextBox({
		mode : "text",
		placeholder : "必填",
		showClearButton : false,
		value :meanings,
		readOnly :true,
		onValueChanged : function(data) {
			
	    }
	});
	
	$("#submit_del_ga_value").dxButton({
		text : "删除",
		hint : "删除",
		icon : "todo",
		// height: 35,
		// width: 70,
		disabled : false,
		onClick : function() {
			
			$.ajax({
				type : "POST",
				url :"info/deleteCodeValue.action",
				data : {
					'idfordel' : idfordel
				},
				success : function(msg) {
					if (msg.code >  0) {
						showMessage("删除成功！");
						$("#window_del_value").dxPopup({
							visible : false,
						});
						loadtable_value(selectobjold);
					} else {
						showMessage("删除失败！");
					}
				}
			});
		}
	});
}
