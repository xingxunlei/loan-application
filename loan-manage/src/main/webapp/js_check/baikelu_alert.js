var borrNum_root;
$(function() {
    getpath();
    loadtable2_bkl();

});

var getpath = function() {
    try {
        var url = location.href;
        var data_url = url.split("?")[1];
        $.ajax({
            type : "post",
            url : "/zloan-manage/url/url_open.action",
            data : {
                var_text_locked : data_url
            },
            success : function(msg) {
                data_url = msg;
                var info = data_url.split("=")[1];
                var himid = info.split("_")[0];
                borrNum_root = info.split("_")[1];

            }
        });
    } catch (e) {
        // window.location = "/jhhoa/login.html";
    }
}

var loadtable2_bkl = function() {
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
	                url: '/zloan-manage/robotOrder/' + borrNum_root + '.action',
	                data: args,

	                success: function(result) {
                        if(result.code == 2000){
                            result = result.data;
                            deferred.resolve(result.list, {totalCount: result.total});
                            // result = result.data;
                            // deferred.resolve(result, { totalCount: result.length });
                        }else {
                            deferred.reject("Data Loading Error");
                        }
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
						columns : [ {
							dataField : "state",
							caption : "结果",
							alignment : "center",
                            calculateCellValue: function (data) {
                                if(data.state == 1){
                                    return "拨打中";
                                }else if (data.state == 2){
                                    return "通过";
                                }else if (data.state == 3){
                                    return "拒绝";
                                }else if (data.state == 4){
                                    return "未接通";
                                }else if (data.state == 5){
                                    return "非本人";
                                }
                            },
                            lookup: {
                                dataSource: [
                                    {value: '1', format: '拨打中'},
                                    {value: '2', format: '通过'},
                                    {value: '3', format: '拒绝'},
                                    {value: '4', format: '未接通'},
                                    {value: '5', format: '非本人'},
                                    { value: '9999', format: '未知' },
                                ], displayExpr: 'format'
                            }
						}, {
                            dataField : "duration",
                            caption : "时长",
                            alignment : "center",
                            allowFiltering:false
                        },{
                            dataField : "score",
                            caption : "分数",
                            alignment : "center",
                            filterOperations:["="]
                        },{
                            dataField : "gender",
                            caption : "性别",
                            alignment : "center",
                            calculateCellValue: function (data) {
                                if(data.gender == 1){
                                    return "男";
                                }else if (data.gender == 2){
                                    return "女";
                                }
                            },
                            lookup: {
                                dataSource: [
                                    {value: '1', format: '男'},
                                    {value: '2', format: '女'},
                                ], displayExpr: 'format'
                            }
                        },{
							dataField : "phone",
							caption : "手机号",
							alignment : "center",
                            filterOperations:["="]
						}, {
							dataField : "quest1",
							caption : "问题描述1",
							alignment : "center",
                            allowFiltering:false
						}, {
							dataField : "answer1",
							caption : "答案1",
							alignment : "center",
                            calculateCellValue: function (data) {
                                if(data.answer1 == "/"){
                                    return "未回答";
                                }else {
                                    return data.answer1;
                                }
                            },
                            allowFiltering:false
						},
						{
							dataField : "quest2",
							caption : "问题描述2",
							alignment : "center",
                            allowFiltering:false
						},{
							dataField : "answer2",
							caption : "答案2",
							alignment : "center",
                                calculateCellValue: function (data) {
                                    if(data.answer2 == "/"){
                                        return "未回答";
                                    }else {
                                        return data.answer2;
                                    }
                                },
                                allowFiltering:false
						}, {
							dataField : "quest3",
							caption : "问题描述3",
							alignment : "center",
							dataType:"number",
                            allowFiltering:false
						}, {
							dataField : "answer3",
							caption : "答案3",
							alignment : "center",
                                calculateCellValue: function (data) {
                                    if(data.answer3 == "/"){
                                        return "未回答";
                                    }else {
                                        return data.answer3;
                                    }
                                },
                                allowFiltering:false
						}, {
							dataField : "quest4",
							caption : "问题描述4",
							alignment : "center",
                            allowFiltering:false
						}, {
							dataField : "answer4",
							caption : "答案4",
							alignment : "center",
                                calculateCellValue: function (data) {
                                    if(data.answer4 == "/"){
                                        return "未回答";
                                    }else {
                                        return data.answer4;
                                    }
                                },
                                allowFiltering:false
						}, {
							dataField : "quest5",
							caption : "问题描述5",
							alignment : "center",
							allowFiltering:false
						},{
							dataField : "answer5",
							caption : "答案5",
							alignment : "center",
                                calculateCellValue: function (data) {
                                    if(data.answer5 == "/"){
                                        return "未回答";
                                    }else {
                                        return data.answer5;
									}
                                },
                                allowFiltering:false
						},{
							dataField : "creatDate",
							caption : "呼叫时间",
							alignment : "center",
								dataType: 'date',
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
                                filterOperations:["=","between"]
						} ]
					});
}





