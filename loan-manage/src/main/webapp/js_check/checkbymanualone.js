var checkbymanualone = function() {
	setToolbar_zma();
	loadtable2_zma();

	window_find_zma();
	window_zma();
    window_transfer_zma();

}

var setToolbar_zma = function() {
    checkPageEnabled("ym-zma");

	$("#tool_find").dxButton({
		hint : "查看详情",
		text : "详情",
		icon : "find",
		disabled : true,
		onClick : function() {
			var dataGrid = $('#userTable').dxDataGrid('instance');
			var selectobj = dataGrid.getSelectedRowsData();
			var himid = selectobj[0].per_id;
			var brroid = selectobj[0].id;
			loadwindow_userinfo(himid, brroid);
		}
	});
	$("#tool_ok").dxButton({
		hint : "通过",
		text : "通过",
		icon : "todo",
		disabled : true,
		onClick : function() {
			var dataGrid = $('#userTable').dxDataGrid('instance');
			var selectobj_param = dataGrid.getSelectedRowsData();
			$("#window").dxPopup({
				visible : true,
			});
			box_ok_value_zma(selectobj_param[0]);
		}
	});

	$("#tool_no").dxButton({
		hint : "拒绝",
		text : "拒绝",
		icon : "close",
		disabled : true,
		onClick : function() {
			var dataGrid = $('#userTable').dxDataGrid('instance');
			var selectobj_param = dataGrid.getSelectedRowsData();
			if (selectobj_param.length > 1) {
				showMessage("只能选择一条数据！");
			} else {
                $("#window").dxPopup({
                    visible : true,
                });
				box_no_value_zma(selectobj_param[0]);
			}
		}
	});

    $("#tool_black").dxButton({
        hint : "拉黑",
        text : "拉黑",
        icon : "close",
        disabled : true,
        onClick : function() {
            var dataGrid = $('#userTable').dxDataGrid('instance');
            var selectobj_param = dataGrid.getSelectedRowsData();
            if (selectobj_param.length > 1) {
                showMessage("只能选择一条数据！");
            } else {
                $("#window").dxPopup({
                    visible : true,
                });
                box_black_value_zma(selectobj_param[0]);
            }
        }
    });

    $("#tool_transfer").dxButton({
        hint : "转件",
        text : "转件",
        icon : "revert",
        disabled : true,
        onClick : function() {

            var dataGrid = $('#userTable').dxDataGrid('instance');
            var selectobj_param = dataGrid.getSelectedRowsData();
            var brroid_list = "";
            for (var i = 0; i < selectobj_param.length; i++) {
                // if(i==selectobj_param.length -1){
                // brroid_list += selectobj_param[i].id;
                // }else{
                brroid_list += selectobj_param[i].id + ",";
                // }
            }
            box_transfer_value_zma(brroid_list);

        }
    });

}

var loadtable2_zma = function() {
	DevExpress.config({
	    forceIsoDateParsing: true,

	});
	 var orders = new DevExpress.data.CustomStore({
	        load: function (loadOptions) {
	            var deferred = $.Deferred(),
	                args = {};
	            args.filter = loadOptions.filter ? JSON.stringify(loadOptions.filter) : "";   // Getting filter settings
	            args.sort = loadOptions.sort ? JSON.stringify(loadOptions.sort) : "";  // Getting sort settings
	            args.requireTotalCount = loadOptions.requireTotalCount; // You can check this parameter on the server side  
	            if (loadOptions.sort) {
	                args.orderby = loadOptions.sort[0].selector;
	                if (loadOptions.sort[0].desc)
	                    args.orderby += " desc";
	            }
	    
	            args.skip = loadOptions.skip || 0;
	            args.take = loadOptions.take || 15;

	            $.ajax({
	                url: 'user/getManuallyReview.action?employ_num=' + usernum + "&meaning=BS001" ,
	                data: args,
	                success: function(result) {
	                	result = JSON.parse(result);
	                    deferred.resolve(result.list, { totalCount: result.total });
	                },
	                error: function() {
	                    deferred.reject("Data Loading Error");
	                },
	                timeout: 50000
	            });
	    
	            return deferred.promise();
	        }
	    });
	
	$("#userTable")
			.dxDataGrid(
					{
						dataSource : {
							 store: orders
						},
						dateSerializationFormat  : "yyyy-MM-dd HH:mm:ss",
						remoteOperations: {
				            sorting: true,
				            paging: true,
				            filtering:true
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
							allowedPageSizes : [ 15, 30, 45, 60 ],
							showInfo : true,
							infoText : '第{0}页 . 共{1}页'
						},
						onSelectionChanged : function(data) {
							var flag = false;
							for (var i = 0; i < data.selectedRowsData.length; i++) {
								if (data.selectedRowsData[i].borr_status != "BS001") {
									flag = true;
									break;
								}

							}

							$("#tool_find")
									.dxButton(
											{
                                                disabled : data.selectedRowsData.length != 1
                                                || flag
                                                || disableButton("ym-zma",0),
											});
							$("#tool_ok")
									.dxButton(
											{
												disabled : data.selectedRowsData.length != 1
														|| flag
                                                		|| disableButton("ym-zma",1),
											});
							$("#tool_no")
									.dxButton(
											{
												disabled : data.selectedRowsData.length != 1
														|| flag
                                                		|| disableButton("ym-zma",2),
											});
                            $("#tool_black")
                                .dxButton(
                                    {
                                        disabled : data.selectedRowsData.length != 1
                                        || flag
                                        || disableButton("ym-zma",3),
                                    });
                            $("#tool_transfer")
                                .dxButton(
                                    {
                                        disabled : (!data.selectedRowsData.length > 0)
                                        			|| disableButton("ym-zma",4)
                                    });
						},
						columns : [ {
							dataField : "borr_num",
							caption : "合同编号",
							alignment : "center",
						}, {
							dataField : "name",
							caption : "姓名",
							alignment : "center",
						}, {
							dataField : "card_num",
							caption : "身份证号",
							alignment : "center",
						}, {
							dataField : "phone",
							caption : "手机号码",
							alignment : "center",
						}, 
						{
							dataField : "contactNum",
							caption : "通讯录个数",
							alignment : "center",
						},
						{
							dataField : "product_name",
							caption : "产品类型",
							alignment : "center",
							lookup: { dataSource: [
                                {value: '500元-7天', format: '500元-7天'},
                                {value: '500元-14天', format: '500元-14天'},
                                {value: '1000元-7天', format: '1000元-7天'},
                                {value: '1000元-14天', format: '1000元-14天'},
                                {value: '1500元-7天', format: '1500元-7天'},
                                {value: '1500元-14天', format: '1500元-14天'},
                                {value: '2000元-7天', format: '2000元-7天'},
                                {value: '2000元-14天', format: '2000元-14天'},
					                               ],  displayExpr: 'format' } 
						}, {
							dataField : "maximum_amount",
							caption : "贷款金额",
							alignment : "center",
							dataType:"number"
						}, {
							dataField : "bank_name",
							caption : "银行名称",
							alignment : "center",
						}, {
							dataField : "bank_num",
							caption : "银行卡号",
							alignment : "center",
						}, {
							dataField : "meaning",
							caption : "贷款状态",
							alignment : "center",
                            allowFiltering:false
						}, 
						{
							dataField : "up_status",
							caption : "上单状态",
							alignment : "center",
							 calculateCellValue: function (data) {
								 if(data.up_status){
								 	if (data.up_status=='BS002'){
										 return "待签约"
									 }else if (data.up_status=='BS003'){
										 return "已签约"
									 }else if (data.up_status=='BS004'){
										 return "待还款"
									 }else if (data.up_status=='BS005'){
										 return "逾期未还"
									 }else if (data.up_status=='BS006'){
										 return "正常结清"
									 }else if (data.up_status=='BS007'){
										 return "已取消"
									 }else if (data.up_status=='BS008'){
										 return "审核未通过"
									 }else if (data.up_status=='BS009'){
										 return "电审未通过"
									 }else if (data.up_status=='BS010'){
										 return "逾期结清"
									 }else if (data.up_status=='BS011'){
										 return "放款中"
									 }else if (data.up_status=='BS012'){
										 return "放款失败"
									 }
									 else{
										 return "";
									 }
								 }
					            },
					            lookup: { dataSource: [
					                                   { value: 'BS001', format: '申请中' },
					                                   { value: 'BS002', format: '待签约' },
					                                   { value: 'BS003', format: '已签约' },
					                                   { value: 'BS004', format: '待还款' },
					                                   { value: 'BS005', format: '逾期未还' },
					                                   { value: 'BS006', format: '正常结清' },
					                                   { value: 'BS008', format: '审核未通过' },
					                                   { value: 'BS009', format: '电审未通过' },
					                                   { value: 'BS010', format: '逾期结清' },
					                                   { value: 'BS011', format: '放款中' },
					                                   { value: 'BS012', format: '放款失败' },
					                                   { value: 'BS007', format: '已取消' }
					                                   
					                               ],  displayExpr: 'format' } 
						}/*,{
                                dataField : "state",
                                caption : "自动电呼",
                                alignment : "center",
                                calculateCellValue: function (data) {
                                    if(data.state == null){
                                        return "未拨打";
                                    }else{
                                        if(data.state == 1){
                                            return "拨打中";
                                        }else if (data.state == 2){
                                            return "通过";
                                        }else if (data.state == 3){
                                            return "拒绝";
                                        }else if (data.state == 4){
                                            return "未接通";
                                        }

                                    }
                                },
                                lookup: { dataSource: [
                                    { value: '5', format: '未拨打' },
                                    { value: '1', format: '拨打中' },
                                    { value: '2', format: '通过' },
                                    { value: '3', format: '拒绝' },
                                    { value: '4', format: '未接通' },
                                ],  displayExpr: 'format' }
							}*/,
						{
							dataField : "description",
							caption : "认证说明",
							alignment : "center",
						},
						{
							dataField : "emplloyee_name",
							caption : "审核人",
							alignment : "center",
						},
						{
							dataField : "createDate",
							caption : "注册时间",
							alignment : "center",
							dataType:"date",
                            filterOperations:["=","between"]
						} ]
					});
}

// 查看详情
var window_find_zma = function() {
	$("#window_find").dxPopup({
		showTitle : true,
		title : '详情',
		width : "95%",
		height : "88%",
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			loadtable2_zma();
		},
	});
}
var window_zma = function() {
	$("#window").dxPopup({
		showTitle : true,
        maxWidth : 500,
        maxHeight : 300,
		title : '确认',
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			setToolbar_zma();
			loadtable2_zma();
		},
	});
}

var window_transfer_zma = function() {
    $("#window_transfer").dxPopup({
        showTitle : true,
        maxWidth : 500,
        maxHeight : 300,
        title : '转件',
        visible : false,
        WindowScroll : true,
        resizeEnabled : true,
        onHiding : function() {
            setToolbar_zma();
            loadtable2_zma();
        },
    });
}

var box_ok_value_zma = function(selectobj) {

    $("#reasonText").html("请说明通过理由");
    var reason = "";

    $("#reason").dxTextArea({
        placeholder : "必填",
        height : 100,
        value : reason,
        showClearButton : false,
        onValueChanged : function(data) {
            reason = data.value;
        }
    });

    // 增加保存按钮
	$("#submit_affirm").dxButton(
			{
				text : "确定",
				hint : "确认发送",
				icon : "todo",
				disabled : false,
				onClick : function() {
                    if (reason == '') {
                        showMessage("必须填写通过理由！");
                        return;
                    }
					var status = "y";
					var conmitdata = {
						brroId : selectobj.id,
						status : status,
                        userNum : usernum,
						reason : reason,
					};
					$.ajax({
						type : "POST",
						url : "/zloan-manage/Riewer/pass.action",
						data : conmitdata,
						success : function(msg) {
							if (msg.code == 1) {
                                showMessage("操作成功！");
                                $("#window").dxPopup({
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

var box_no_value_zma = function(selectobj) {
    $("#reasonText").html("请说明拒绝理由");
    var reason = "";

    $("#reason").dxTextArea({
        placeholder : "必填",
        height : 100,
        value : reason,
        showClearButton : false,
        onValueChanged : function(data) {
            reason = data.value;
        }
    });
    // 增加保存按钮
    $("#submit_affirm")
        .dxButton(
            {
                text : "确定",
                hint : "确认发送",
                icon : "todo",
                // height: 35,
                // width: 70,
                disabled : false,
                onClick : function() {
                    if (reason == '') {
                        showMessage("必须填写拒绝理由！");
                        return;
                    }
                    var conmitdata = {
                        brroId : selectobj.id,
                        userNum : usernum,
                        reason : reason,
                    };
                    $.ajax({
                        type : "POST",
                        url : "/zloan-manage/Riewer/reject.action",
                        data : conmitdata,
                        success : function(msg) {
                            if (msg.code == 1) {
                                showMessage("操作成功！");
                                $("#window").dxPopup({
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


var box_black_value_zma = function(selectobj) {
	var reason = "";
	$("#reasonText").html("请说明拉黑理由");
    $("#reason").dxTextArea({
        placeholder : "必填",
        height : 100,
        value : reason,
        showClearButton : false,
        onValueChanged : function(data) {
            reason = data.value;
        }
    });
    // 增加保存按钮
    $("#submit_affirm")
        .dxButton(
            {
                text : "确定",
                hint : "确认发送",
                icon : "todo",
                // height: 35,
                // width: 70,
                disabled : false,
                onClick : function() {
                    if (reason == '') {
                        showMessage("必须填写拉黑理由！");
                        return;
                    }
                    var conmitdata = {
                        brroId : selectobj.id,
                        userNum : usernum,
                        reason : reason,
                    };
                    $.ajax({
                        type : "POST",
                        url : "/zloan-manage/Riewer/black.action",
                        data : conmitdata,
                        success : function(msg) {
                            if (msg.code == 1) {
                                showMessage("操作成功！");
                                $("#window").dxPopup({
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

var box_transfer_value_zma = function(brroid_list) {
    $("#window_transfer").dxPopup({
        visible : true,
    });
    $.ajax({
        type : "POST",
        url : "manager/selectBorrowLists.action",
        data : {
            borrIds : brroid_list,
            borrStatus : "BS001",
        },
        dataType:'text',
        success : function(msg) {
            if(msg == "sucess"){
                box_transfer_value_zma_sucess(brroid_list);
            }else {
                alert("订单" + msg + "状态已发生改变无法转件");
            }
        },
        error: function(data) {
            alert("Data Loading Error");
        }
    });



}

var box_transfer_value_zma_sucess = function(brroid_list) {
    // 增加保存按钮
    $("#submit_transfer").dxButton({
        text : "确定",
        hint : "确认发送",
        icon : "todo",
        // height: 35,
        // width: 70,
        disabled : false,
        onClick : function() {

            if (transfer == '') {
                showMessage("必须选择转件人！");
                return;
            }
            var conmitdata = {
                brroid_list : brroid_list,
                transfer : transfer,
            };
            $.ajax({
                type : "POST",
                url : "/zloan-manage/manager/transferPersonCheck.action",
                data : conmitdata,
                success : function(msg) {
                    $("#transfer").dxSelectBox("instance").reset();
                    if (msg.code >= 0) {
                        showMessage("操作成功！");
                        $("#window_transfer").dxPopup({
                            visible : false,
                        });
                    } else {
                        showMessage("操作失败！");
                    }
                }
            });
        }
    });

    $("#window_transfer").dxPopup({
        visible : true,
    });
    var transfer = "";
    $.ajax({
        type : "POST",
        url : "/zloan-manage/manager/selectRiewerList.action",
        data : {
            status : "y",
        },
        success : function(msg) {
            $('#transfer').dxSelectBox({
                dataSource : msg,
                placeholder : "必填",
                valueExpr : 'userSysno',
                displayExpr : 'userName',
                showClearButton : true,
                onValueChanged : function(e) {
                    transfer = e.value;
                }
            });
        }
    });
}
