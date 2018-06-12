var drainageInit = function() {
	setToolbar_yl();
	loadtable_yl();

	window_add_yl();
	window_edit_yl();

}

var setToolbar_yl = function() {

    checkPageEnabled("ym-vb");

	$("#tool_add").dxButton({
		hint : "新增",
		text : "新增",
		icon : "add",
		disabled : disableButton("ym-vb",0),
		onClick : function() {
			$("#window_add").dxPopup({
				visible : true,
			});
            box_add_value_ly();
		}
	});

	$("#tool_edit").dxButton({
		hint : "编辑",
		text : "编辑",
		icon : "edit",
		disabled : true,
		onClick : function() {
			var dataGrid = $('#userTable').dxDataGrid('instance');
			var selectobj_param = dataGrid.getSelectedRowsData();
			$("#window_edit").dxPopup({
				visible : true,
			});
			box_edit_value_yl(selectobj_param[0]);
		}
	});
    $("#tool_delete").dxButton({
        hint : "删除",
        text : "删除",
        icon : "close",
        disabled : true,
        onClick : function() {
            var dataGrid = $('#userTable').dxDataGrid('instance');
            var selectobj_param = dataGrid.getSelectedRowsData();
            box_delete_value_yl(selectobj_param);
        }
    });

}

var loadtable_yl = function() {

	$.ajax({
		type : "post",
		url : "drainage/query.action",
		success : function(msg) {
			// var jsonTrans = eval("(" + msg + ")").result;
			loadtable2_ly(msg.object);
		}
	});
}
var loadtable2_ly = function(jsonobj) {
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
            drainage_name : "列选择器",
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
				disabled :(data.selectedRowsData.length != 1) || disableButton("ym-vb",1),
			});
            $("#tool_delete").dxButton({
                disabled :(data.selectedRowsData.length < 1) || disableButton("ym-vb",1),
            });
		},
		columns : [ {
            dataField : "id",
            caption : "产品ID",
            alignment : "center",
            visible : false,
        },{
			dataField : "drainage_name",
			caption : "产品名字",
			alignment : "center",
		}, {
			dataField : "drainage_title",
			caption : "产品标题",
			alignment : "center",
		}, {
			dataField : "drainage_des",
			caption : "产品说明",
			alignment : "center",
		}, {
			dataField : "drainage_type",
			caption : "产品类型",
			alignment : "center",
		}, {
			dataField : "drainage_status",
			caption : "启用状态",
			alignment : "center",
			calculateCellValue : function(rowData) {
				if (rowData.drainage_status == "1") {
					return "启用";
				} else {
					return "禁用";
				}

			}
		}, {
			dataField : "drainage_url",
			caption : "产品地址",
			alignment : "center",

		}, {
			dataField : "drainage_backurl",
			caption : "产品回调地址",
			alignment : "center",
		}, {
            dataField : "drainage_img",
            caption : "产品图片地址",
            alignment : "center",
        }
		]
	});
}

// 查看详情
var window_add_yl = function() {
	$("#window_add").dxPopup({
		showTitle : true,
		title : '新增产品',
		maxWidth : 650,
		maxHeight : 1000,
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			setToolbar_yl();
			loadtable_yl();
		},
	});
}

var window_edit_yl = function() {
	$("#window_edit").dxPopup({
		showTitle : true,
		maxWidth : 650,
		maxHeight : 850,
		title : '编辑产品',
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			setToolbar_yl();
			loadtable_yl();
		},
	});
}

var box_add_value_ly = function() {
    var drainage_name = "";
    var drainage_title = "";
    var drainage_des = "";
    var drainage_type = "";
    var drainage_url = "";
    var drainage_backurl = "";
    var drainage_img = "";
    var drainage_status = "";
    $("#status").dxCheckBox({
        text : '启用',
        value : '1',
        name : "drainage_status",
        onValueChanged : function(data) {
            if (data.value) {
                drainage_status = "1";
            } else {
                drainage_status = "2";
            }
        }
    });
    $("#drainage_name").dxTextBox({
        placeholder : "必填",
        showClearButton : false,
        name : "drainage_name",
		value : drainage_name,
        onValueChanged : function(data) {
            drainage_name = data.value;
        }
    });
    $("#drainage_type").dxTextBox({
        placeholder : "必填",
        showClearButton : false,
        name : "drainage_type",
		value : drainage_type,
        onValueChanged : function(data) {
            drainage_type = data.value;
        }
    });
    $("#drainage_title").dxTextBox({
        placeholder : "必填",
        showClearButton : false,
        name : "drainage_title",
		value : drainage_title,
        onValueChanged : function(data) {
            drainage_title = data.value;
        }
    });
    $("#drainage_des").dxTextBox({
        placeholder : "必填",
        showClearButton : false,
        name : "drainage_des",
		value : drainage_des,
        onValueChanged : function(data) {
            drainage_des = data.value;
        }
    });
    $("#drainage_url").dxTextBox({
        placeholder : "必填",
        showClearButton : false,
        name : "drainage_url",
		value : drainage_url,
        onValueChanged : function(data) {
            drainage_url = data.value;
        }
    });
    $("#drainage_backurl").dxTextBox({
        placeholder : "必填",
        name : "drainage_backurl",
		value : drainage_backurl,
        showClearButton : false,
        onValueChanged : function(data) {
            drainage_backurl = data.value;
        }
    });
    $("#drainage_img").dxFileUploader({
        selectButtonText : "选择文件",
        name : "file",
        labelText: "",
		accept : "image/*",
        uploadMode: "useForm",
        showClearButton : true
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
            if (drainage_name == '') {
                showMessage("必须填写产品名称！");
                return;
            } else if (drainage_title == "") {
                showMessage("必须填写产品标题！");
                return;
            } else if (drainage_type == "") {
                showMessage("必须填写产品类型！");
                return;
            }

            $("#adddrainage-form").ajaxSubmit({
                type: "post",
                url: "drainage/add.action",
                success: function (msg) {
                    if (msg.code >= 0) {
                        $("#window_add").dxPopup({
                            visible: false,
                        });
                    } else {
                        showMessage("操作失败！");
                    }
                }
            });
        }
	});
}

var box_edit_value_yl = function(selectobj) {
	var id = selectobj.id;
	var drainage_name = selectobj.drainage_name;
	var drainage_title = selectobj.drainage_title;
	var drainage_des = selectobj.drainage_des;
	var drainage_type = selectobj.drainage_type;
	var drainage_url = selectobj.drainage_url;
    var drainage_backurl = selectobj.drainage_backurl;
    var drainage_img = selectobj.drainage_img;
    var drainage_status = selectobj.drainage_status;
	$("#status2").dxCheckBox({
		text : '启用',
		value : drainage_status=="1",
		name : "drainage_status",
		onValueChanged : function(data) {
			if (data.value) {
                drainage_status = "1";
			} else {
                drainage_status = "2";
			}
		}
	});
    $("#id2").dxTextBox({
        value : id,
        showClearButton : false,
        name : "id",
    });
	$("#drainage_name2").dxTextBox({
		placeholder : "必填",
		value : drainage_name,
		showClearButton : false,
		name : "drainage_name",
		onValueChanged : function(data) {
            drainage_name = data.value;
		}
	});
    $("#drainage_type2").dxTextBox({
        placeholder : "必填",
        value : drainage_type,
        showClearButton : false,
		name : "drainage_type",
        onValueChanged : function(data) {
            drainage_type = data.value;
        }
    });
    $("#drainage_title2").dxTextBox({
        placeholder : "必填",
        value : drainage_title,
        showClearButton : false,
		name : "drainage_title",
        onValueChanged : function(data) {
            drainage_title = data.value;
        }
    });
    $("#drainage_des2").dxTextBox({
        placeholder : "必填",
        value : drainage_des,
        showClearButton : false,
		name : "drainage_des",
        onValueChanged : function(data) {
            drainage_des = data.value;
        }
    });
    $("#drainage_url2").dxTextBox({
        placeholder : "必填",
        value : drainage_url,
        showClearButton : false,
		name : "drainage_url",
        onValueChanged : function(data) {
            drainage_url = data.value;
        }
    });
    $("#drainage_backurl2").dxTextBox({
        placeholder : "必填",
        value : drainage_backurl,
		name : "drainage_backurl",
        showClearButton : false,
        onValueChanged : function(data) {
            drainage_backurl = data.value;
        }
    });
    $("#drainage_img2").dxFileUploader({
        selectButtonText : "选择文件",
		name : "file",
		labelText: "",
        accept : "image/*",
        uploadMode: "useForm",
        showClearButton : true,
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
            if (drainage_name == '') {
                showMessage("必须填写产品名称！");
                return;
            } else if (drainage_type == "") {
                showMessage("必须填写产品标题！");
                return;
            }else if (drainage_type == "") {
                showMessage("必须填写产品类型！");
                return;
            }
            $("#editdrainage-form").ajaxSubmit({
				type : "post",
                url : "drainage/update.action",
				success : function(msg){
                    if (msg.code > 0) {
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
var box_delete_value_yl = function(selectobj) {
    var ids = "";
	for(var i=0 ;i<selectobj.length ;i++){
		var s = selectobj[i];
        if(i == selectobj.length-1){
            ids+=s.id;
        }else{
            ids+=s.id+",";
        }
	}

    var data = {ids : ids};
	$.ajax({
		url : "drainage/delete.action",
		type : "post",
		data : data,
		async : false,
		success : function (msg) {
			if(msg.code == 1){
                //showMessage("删除成功！");
			}else{
                showMessage("删除失败！");
			}
        }
	})
    loadtable_yl();

}
