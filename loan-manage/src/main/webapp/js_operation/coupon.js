var coupon = function() {
	setToolbar_va();
	loadtable_va();

	window_add_va();
	window_edit_va();

}

var setToolbar_va = function() {

    checkPageEnabled("ym-va");

	$("#tool_add").dxButton({
		hint : "新增",
		text : "新增",
		icon : "add",
		disabled : disableButton("ym-va",0),
		onClick : function() {
			$("#window_add").dxPopup({
				visible : true,
			});
			box_add_value_va();
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
			box_edit_value_va(selectobj_param[0]);
		}
	});

}

var loadtable_va = function() {

	$.ajax({
		type : "post",
		url : "info/getAllCouponList.action",
		success : function(msg) {
			// var jsonTrans = eval("(" + msg + ")").result;
			loadtable2_va(msg);
		}
	});
}
var loadtable2_va = function(jsonobj) {
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
			couponName : "列选择器",
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
				disabled :(data.selectedRowsData.length != 1) || disableButton("ym-va",1),
			});
		},
		columns : [ {
			dataField : "couponName",
			caption : "优惠券名称",
			alignment : "center",
		}, {
			dataField : "amount",
			caption : "金额",
			alignment : "center",
			calculateCellValue : function(rowData) {
				return rowData.amount + " 元";
			}
		}, {
			dataField : "duation",
			caption : "使用期限",
			alignment : "center",
			calculateCellValue : function(rowData) {
				return rowData.duation + " 天";
			}
		}, {
			dataField : "productId",
			caption : "可用产品",
			alignment : "center",
			calculateCellValue : function(rowData) {
				if (rowData.productId == "0") {
					return "全部产品";
				} else {
					return rowData.productName;
				}

			}
		}, {
			dataField : "status",
			caption : "启用状态",
			alignment : "center",
			calculateCellValue : function(rowData) {
				if (rowData.status == "1") {
					return "启用";
				} else {
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
			dataType : "date",
		} ]
	});
}

// 查看详情
var window_add_va = function() {
	$("#window_add").dxPopup({
		showTitle : true,
		title : '新增优惠券',
		maxWidth : 650,
		maxHeight : 470,
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			setToolbar_va();
			loadtable_va();
		},
	});
}

var window_edit_va = function() {
	$("#window_edit").dxPopup({
		showTitle : true,
		maxWidth : 650,
		maxHeight : 470,
		title : '编辑优惠券',
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			setToolbar_va();
			loadtable_va();
		},
	});
}

var box_add_value_va = function() {
	var couponName = "";
	var amount = "";
	var duation = "";
	var status = "1";
	var productId = 0;
	$("#status").dxCheckBox({
		text : '启用',
		value : true,
		onValueChanged : function(data) {
			if (data.value) {
				status = "1";
			} else {
				status = "0";
			}
		}
	});
	$("#couponName").dxTextBox({
		placeholder : "必填",
		value : couponName,
		showClearButton : false,
		onValueChanged : function(data) {
			couponName = data.value;
		}
	});
	$("#amount").dxNumberBox({
		placeholder : "必填",
		max : 9999,
		min : 0,
		value :amount,
		showSpinButtons : true,
		onValueChanged : function(data) {
			amount = data.value;
		}
	});
	$("#duation").dxNumberBox(
			{
				placeholder : "必填",
				max : 365,
				min : 0,
				value : duation,
				showSpinButtons : true,
				onKeyPress : function(e) {
					var event = e.jQueryEvent, str = event.key
							|| String.fromCharCode(event.which);
					if (/^[\.\,e]$/.test(str))
						event.preventDefault();
				},

				onValueChanged : function(data) {
					duation = data.value;
				}
			});
	
	$.ajax({
		type : "POST",
		url : "manager/getAllProduct.action",
		success : function(msg) {
			$("#productId").dxSelectBox({
				placeholder : "必填",
				dataSource :msg,
				displayExpr : "productName",
				valueExpr : "id",
				value :productId,
				onValueChanged : function(data) {
					productId = data.value;
				}
			});
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
			if (couponName == '') {
				showMessage("必须填写名称！");
				return;
			} else if (amount == "") {
				showMessage("必须填写金额！");
				return;
			}else if (duation == "") {
				showMessage("必须填写期限！");
				return;
			}
			var conmitdata = {
				updateUser : usernum,
				creationUser : usernum,
				couponName : couponName,
				amount : amount,
				status : status,
				duation:duation,
				productId:productId,
			};
			$.ajax({
				type : "POST",
				url : "info/insertCoupon.action",
				data : conmitdata,
				success : function(msg) {
					if (msg.code >= 0) {
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

var box_edit_value_va = function(selectobj) {
	var id = selectobj.id;
	var couponName = selectobj.couponName;
	var amount = selectobj.amount;
	var duation = selectobj.duation;
	var status = selectobj.status;
	var productId = selectobj.productId;
	$("#status2").dxCheckBox({
		text : '启用',
		value : status=="1",
		onValueChanged : function(data) {
			if (data.value) {
				status = "1";
			} else {
				status = "0";
			}
		}
	});
	$("#couponName2").dxTextBox({
		placeholder : "必填",
		value : couponName,
		showClearButton : false,
		onValueChanged : function(data) {
			couponName = data.value;
		}
	});
	$("#amount2").dxNumberBox({
		placeholder : "必填",
		max : 9999,
		min : 0,
		value :amount,
		showSpinButtons : true,
		onValueChanged : function(data) {
			amount = data.value;
		}
	});
	$("#duation2").dxNumberBox(
			{
				placeholder : "必填",
				max : 365,
				min : 0,
				value : duation,
				showSpinButtons : true,
				onKeyPress : function(e) {
					var event = e.jQueryEvent, str = event.key
							|| String.fromCharCode(event.which);
					if (/^[\.\,e]$/.test(str))
						event.preventDefault();
				},

				onValueChanged : function(data) {
					duation = data.value;
				}
			});
	
	$.ajax({
		type : "POST",
		url : "manager/getAllProduct.action",
		success : function(msg) {
			$("#productId2").dxSelectBox({
				placeholder : "必填",
				dataSource :msg,
				displayExpr : "productName",
				valueExpr : "id",
				value :productId,
				onValueChanged : function(data) {
					productId = data.value;
				}
			});
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
			if (couponName == '') {
				showMessage("必须填写名称！");
				return;
			} else if (amount == "") {
				showMessage("必须填写金额！");
				return;
			}else if (duation == "") {
				showMessage("必须填写期限！");
				return;
			}
			var conmitdata = {
					id:id,
				updateUser : usernum,
				creationUser : usernum,
				couponName : couponName,
				amount : amount,
				status : status,
				duation:duation,
				productId:productId,
			};
			$.ajax({
				type : "POST",
				url : "info/updateCoupon.action",
				data : conmitdata,
				success : function(msg) {
					if (msg.code >= 0) {
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
