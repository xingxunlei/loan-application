var checkbymanual_result = function() {
	setToolbar_zmr();
	loadtable2_zmr();

	window_find_zmr();

}
var setToolbar_zmr = function() {
    checkPageEnabled("ym-zmr");

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
    $("#tool_export").dxButton({
        hint : "导出",
        text : "导出",
        icon : "export",
        disabled : true,
        onClick : function(loadOptions) {
            var dataGrid = $('#userTable').dxDataGrid('instance');
            var filter = dataGrid.getCombinedFilter();
            filter = JSON.stringify(filter) == undefined ? '' : JSON.stringify(filter);
            var url = "user/exportManuallyReview.action?employ_num=" + usernum + "&filter=" + encodeURI(filter);
            exportData(url,null);
        }
    });
}

var loadtable2_zmr = function() {
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
	                url: 'user/getManuallyReview.action?employ_num=' + usernum ,
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
						}
//						{
//							store : {
//								type : "odata",
//								url : odataurl
//										+ "/odata/YM_view_odata_checklist?$filter=employ_num%20eq%20%27"
//										+ usernum + "%27%20and%20(borr_status%20eq%20%27BS003%27%20or%20borr_status%20eq%20%27BS012%27)",
//							}
//						}
						,
//						searchPanel : {
//							visible : true,
//							width : 240,
//							placeholder : "搜索..."
//						},
//						"export" : {
//							enabled : true,
//							fileName : "Employees",
//							allowExportSelectedData : true
//						},
//						headerFilter : {
//							visible : true
//						},
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
							allowedPageSizes : [10, 15, 30, 45, 60 ],
							showInfo : true,
							infoText : '第{0}页 . 共{1}页'
						},
						onSelectionChanged : function(data) {
							var flag = false;
							for (var i = 0; i < data.selectedRowsData.length; i++) {
								if (data.selectedRowsData[i].borr_status == "BS001") {
									flag = true;
									break;
								}

							}

							$("#tool_find")
									.dxButton(
											{
                                                disabled : data.selectedRowsData.length != 1
                                                || flag
												|| disableButton("ym-zmr",0),
											});

                            $("#tool_export")
                                .dxButton(
                                    {
                                        disabled :
                                        	disableButton("ym-zmr",1),
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
							lookup: { dataSource: [
								{ value: 'BS003', format: '已签约' },
								{ value: 'BS004', format: '待还款' },
								{ value: 'BS005', format: '逾期未还' },
                                { value: 'BS006', format: '正常结清' },
                                { value: 'BS007', format: '已取消' },
								{ value: 'BS008', format: '审核未通过' },
                                { value: 'BS010', format: '逾期结清' },
							    { value: 'BS011', format: '放款中' },
								{ value: 'BS012', format: '放款失败' }
							   ],  displayExpr: 'format' }
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
						}
						,{
							dataField : "reason",
							caption : "理由",
							alignment : "center",
						},
						{
							dataField : "description",
							caption : "认证说明",
							alignment : "center",
						},
						{
							dataField : "emplloyee_name",
							caption : "审核人",
							alignment : "center",
						},{
							dataField : "auditTime",
							caption : "审核时间",
                            alignment : "center",
							dataType:"date",
							filterOperations:["=","between"],
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
						},{
							dataField : "makeborr_date",
							caption : "签约时间",
							alignment : "center",
							dataType:"date",
							filterOperations:["=","between"]
						} ]
					});
}

// 查看详情
var window_find_zmr = function() {
	$("#window_find").dxPopup({
		showTitle : true,
		title : '详情',
		width : "95%",
		height : "88%",
		visible : false,
		WindowScroll : true,
		resizeEnabled : true,
		onHiding : function() {
			loadtable2_zmr();
		},
	});
}


//导出函数
var exportData = function(url,params){
    var form=$("<form>");//定义一个form表单
    form.attr("style","display:none");
    form.attr("target","");
    form.attr("method","post");
    form.attr("action",url);
    $("body").append(form);//将表单放置在web中
    form.submit();//表单提交
};

