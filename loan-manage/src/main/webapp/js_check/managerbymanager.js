
var managerbymanager = function() {
	setToolbar_zc();
	loadtable_zc();
}

var setToolbar_zc = function() {
    checkPageEnabled("ym-zc");

	$("#tool_ok").dxButton({
		hint : "启用",
		text:"启用",
		icon : "todo",
		disabled : true,
		onClick : function() {
			var dataGrid = $('#userTable').dxDataGrid('instance');
			var selectobj_param = dataGrid.getSelectedRowsData();
			var brroid_list ="";
			for(var i = 0;i<selectobj_param.length;i++ ){
//				if(i==selectobj_param.length -1){
//					brroid_list += selectobj_param[i].id;
//				}else{
					brroid_list += selectobj_param[i].id + ",";
				//}
			}
			box_ok_value_zc(brroid_list);
		}
	});
	
	$("#tool_no").dxButton({
		hint : "禁用",
		text:"禁用",
		icon : "close",
		disabled : true,
		onClick : function() {
			var dataGrid = $('#userTable').dxDataGrid('instance');
			var selectobj_param = dataGrid.getSelectedRowsData();
			var brroid_list ="";
			for(var i = 0;i<selectobj_param.length;i++ ){
//				if(i==selectobj_param.length -1){
//					brroid_list += selectobj_param[i].id;
//				}else{
					brroid_list += selectobj_param[i].id + ",";
				//}
			}
			box_no_value_zc(brroid_list);
		}
	});
	

	

}
var loadtable_zc = function(){
	$.ajax({
		type : "POST",
		url :  "manager/selectRiewerListAll.action",
		success : function(msg) {
			loadtable2_zc(msg);
		}
	});
};

var loadtable2_zc = function(jsonobj) {
	$("#userTable").dxDataGrid({
		dataSource : {
			store : {
				type : "array",
				 data: jsonobj,
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
			var flag = false;
			var flag2= false;
			for(var i = 0; i<data.selectedRowsData.length; i++){
				if(data.selectedRowsData[i].status != "y"){
					flag = true;
					break;
				}
				
			}
			for(var i = 0; i<data.selectedRowsData.length; i++){
				if(data.selectedRowsData[i].status == "y"){
					flag2 = true;
					break;
				}
			}
			$("#tool_ok").dxButton({
				disabled : !data.selectedRowsData.length || flag2 || disableButton("ym-zc",0) ,
			});
			$("#tool_no").dxButton({
				disabled : !data.selectedRowsData.length || flag ||  disableButton("ym-zc",1) ,
			});
		},
		columns : [ {
			dataField : "employNum",
			caption : "员工编号",
			alignment : "center",
		}, {
			dataField : "emplloyeeName",
			caption : "员工姓名",
			alignment : "center",
		}, {
			dataField : "status",
			caption : "启用状态",
			alignment : "center",
			calculateCellValue: function(rowData){
				if(rowData.status=='y'){
					return "启用";
				}else{
					return "禁用";
				}
			}
		}, {
			dataField : "creationDate",
			caption : "创建时间",
			alignment : "center",
			dataType: "date" ,
		}, {
			dataField : "updateDate",
			caption : "更新时间",
			alignment : "center",
			dataType: "date" ,
		}]
	});
}





var box_ok_value_zc = function(brroid_list) {
	var status = "y";
	var conmitdata = {
			brroid_list:brroid_list,
			status:status,
	};
	$.ajax({
		type : "POST",
		url : "manager/managerbymanager.action",
		data : conmitdata,
		success : function(msg) {
			if (msg.code >=0) {
				showMessage("操作成功！");
				setToolbar_zc();
				loadtable_zc();
				
			} else {
				showMessage("操作失败！");
			}
	
		}
	});
}

var box_no_value_zc = function(brroid_list) {
	var status = "n";
	var conmitdata = {
			brroid_list:brroid_list,
			status:status,
	};
	$.ajax({
		type : "POST",
		url : "manager/managerbymanager.action",
		data : conmitdata,
		success : function(msg) {
			if (msg.code >=0) {
				showMessage("操作成功！");
				setToolbar_zc();
				loadtable_zc();
				
			} else {
				showMessage("操作失败！");
			}
		}
	});
}


