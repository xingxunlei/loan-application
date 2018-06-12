$(function() {
    $('.modal-backdrop').hide();
	getpath();
	getTabs();
	loadUserId();
	
	$("#serchCoupon").click(function(){
		getCoupons(perCouponId, prodId)
	});
	
	$("#serchOrders").click(function(){
		getOrders(brroid_root);
	});
    $("#polyXinliCredit").click(function(){
        loadtable_credit(card_num, name, himid_root);
    });

	$("#modileContact").click(function(){
		getModileContact(himid_root);
	});
	$("#modileContactExport").click(function () {
        getModileContactExport(himid_root);
    })

    $("#polyXinliCreditExport").click(function () {
        getPolyXinliCreditExport(card_num, name, himid_root);
    })

});

var loadUserId = function () {
    loginId = $.cookie("userid");
};

var himid_root  = 0;
var brroid_root = 0;
var loginId = "";
var borrNum_root = "";
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
				brroid_root = info.split("_")[1];
				borrNum_root = info.split("_")[2];
				himid_root = himid;
				loadbox_data(himid);
				getCardPicById(himid);
				
			}
		});
	} catch (e) {
		// window.location = "/jhhoa/login.html";
	}

}

var getTabs = function() {

    $('#myTab a').click(function (e) {
	  	e.preventDefault();
	  	$(this).tab('show');
	  	
	  	if($(this).attr('id') == "tab_loan"){
	  		loadtable_loan(brroid_root);
	  	}else if($(this).attr('id') == "tab_bank"){
	  		loadtable_bank(himid_root);
	  	}else if($(this).attr('id') == "tab_memo"){
	  		loadtable_memo(brroid_root);
	  		initToolbar_memo();
	  	}else if($(this).attr('id') == "tab_credit") {
	  		loadtable_credit(card_num, name, himid_root);
	  	}else if($(this).attr('id') == "tab_black"){
            loadtable_black(himid_root)
		}
	});
    
	$('.newAdd_btn').click(function(){
		$('#myModal_add').modal('show');
	}); 
}

var name = "";
var card_num = "";
var qq_num = "";
var email = "";
var education = "";
var marry = "";
var getchild = "";

var usuallyaddress = "";
var source_value = "";
var phone = "";
var address = "";
var birthday = "";
var blacklist = "";
var create_date = "";

var profession = "";
var monthlypay = "";
var business = "";
var busi_city = "";
var busi_address = "";
var busi_phone = "";
var busi_province = "";

var relatives = "";
var relatives_name = "";
var rela_phone = "";
var society = "";
var society_name = "";
var soci_phone = "";

var perCouponId = "";
var prodId = "";
var phones = "";

var loadbox_data = function(himid) {
	$.ajax({
		type : "GET",
		url :  "/zloan-manage/userInfo.action",
		data : {
			'perId' : himid
		},
		success : function(msg) {
			var himinfo = msg;
			name = himinfo.name;
			card_num = himinfo.card_num;
			qq_num = himinfo.qq_num;
			email = himinfo.email;
			education = himinfo.education;
			marry = himinfo.marry;
			getchild = himinfo.getchild;

			usuallyaddress = himinfo.usuallyaddress;
			source_value = himinfo.source_value;
			phone = himinfo.phone;
			address = himinfo.address;
			birthday = himinfo.birthday;
			if(himinfo.blacklist == "Y"){
				blacklist = "是";
			}else{
				blacklist = "否";
			}
			create_date = himinfo.create_date;
			
			
			profession = himinfo.profession;
			monthlypay = himinfo.monthlypay;
			business = himinfo.business;
			busi_city = himinfo.busi_city;
			busi_address = himinfo.busi_address;
			busi_phone = himinfo.busi_phone;
			busi_province = himinfo.busi_province;

			relatives = himinfo.relatives_value;
			relatives_name = himinfo.relatives_name;
			rela_phone = himinfo.rela_phone;
			society = himinfo.society_value;
			society_name = himinfo.society_name;
			soci_phone = himinfo.soci_phone;

			loadbox_box();

			//征信报告按钮加载
			creditInvestigation(card_num, name);
		}
	});

}

var loadbox_box = function() {
	//基本信息
	$("#name").val(name);
	$("#cardNum").val(card_num);
	$("#phone").val(phone);
	
	//个人信息
	$("#infoName").val(name);
	$("#infoCardNum").val(card_num);
	$("#infoQQNum").val(qq_num);
	$("#infoEmail").val(email);
	$("#infoEducation").val(education);
	$("#infoMarry").val(marry);
	$("#infoGetchild").val(getchild);
	
	//职业信息
	$("#workProfession").val(profession);
	$("#workMonthlypay").val(monthlypay);
	$("#workBusiness").val(business);
	$("#workBusiCity").val(busi_city);
	$("#workBusiAddress").val(busi_address);
	$("#workBusiPhone").val(busi_phone);
	$("#workBusiProvince").val(busi_province);
	
	//社会关系
	$("#slRelatives").val(relatives);
	$("#slRelativesName").val(relatives_name);
	$("#slRelativesPhone").val(rela_phone);
	$("#slSociety").val(profession);
	$("#slSocietyPhone").val(soci_phone);
	$("#slSocietyName").val(society_name);
}

var getCardPicById = function(himid) {
	$.ajax({
		type : "GET",
		url : "/zloan-manage/cardInfo.action",
		data : {
			'perId' : himid
		},
		success : function(msg) {
			var cardinfo = msg;
			if(cardinfo != null){
                var imageZ = cardinfo.imageZ;
                var imageF = cardinfo.imageF;
                if (imageZ != null && imageZ != "") {
                    $("#imageZ").html(
                        "<img width='500' height='300' src='data:image/jpg;base64,"
                        + imageZ + "'/>");
                }
                if (imageF != null && imageF != "") {
                    $("#imageF").html(
                        "<img width='500' height='300' src='data:image/jpg;base64,"
                        + imageF + "'/>");
                }
			}
		}
	});
}

// load the table of loan
var loadtable_loan = function(borrId) {
	$.ajax({
		type : "GET",
		url : "/zloan-manage/borrowList.action",
		data : {
			"borrId" : borrId,
		},
		success : function(msg) {
			var jsonobj = eval("(" + msg + ")");
 			$("#productType").val(jsonobj.prodName);
			$("#monthMoney").val(jsonobj.monthMoney);

			$("#productTem").val(jsonobj.prodTem);
			$("#borrAmount").val(jsonobj.borrAmount);
			var borrStatuse = jsonobj.borrStatus;
			if(borrStatuse == "BS004"){
                borrStatuse = "待还款";
            }else if(borrStatuse == "BS005"){
                borrStatuse = "逾期未还";
            }else if(borrStatuse == "BS006"){
                borrStatuse = "正常结清";
            }else if(borrStatuse == "BS010"){
                borrStatuse = "逾期结清";
            }
			$("#borrStatus").val(borrStatuse);
			$("#actRepayAmount").val(jsonobj.monthQuota);
            var payDate = new Date(jsonobj.planrepayDate);
            payDate =  payDate.getFullYear()+"-"+(payDate.getMonth()+1)+"-"+payDate.getDate();
			$("#payDate").val(payDate);
			$("#rpBedueDays").val(jsonobj.bedueDays);
			$("#rpPenalty").val(jsonobj.surplusPenalty);
			$("#rpMonthInterest").val(jsonobj.surplusInterest);
			$("#rpMonthMoney").val(jsonobj.surplusMoney);
			$("#rpPenaltyInterest").val(jsonobj.surplusPenaltyInteres);
			$("#rpSurplusTotalAmount").val(jsonobj.surplusTotalAmount);
            $("#rpHasBeenPayment").val(jsonobj.hasBeenPayment);
            var isManual = "否";
            if(jsonobj.isManual != null){
                isManual = "是"
			}
            $("#isManual").val(isManual);
            $("#loanDscription").val(jsonobj.description);
			perCouponId = jsonobj.perCouponId;
			prodId = jsonobj.prodId;
			getCoupons(perCouponId, prodId);
			getOrders(brroid_root);
		}
	});
}


var getCoupons = function(couponId,prodId) {
	$.ajax({
		
		type : "GET",
		url : "/zloan-manage/coupon.action",
		data : {
			"couponId" : couponId,
			"prodId" : prodId,
		},
		success : function(msg) {
			loadtable_coupon(msg);
		}
	});
}

var loadtable_coupon = function(jsonobj) {
	$("#loan_table_coupon").dxDataGrid({
		dataSource : {
			store : {
				type : "array",
				data : jsonobj
			}
		},
		rowAlternationEnabled : true,
		showRowLines : true,
		allowColumnReordering : true,
		allowColumnResizing : true,
		columnAutoWidth : true,
		columnFixing : {
			enabled : true
		},
		paging : {
			pageSize : 20,
		},
		pager : {
			showPageSizeSelector : true,
			allowedPageSizes : [10, 15, 30, 45, 60 ],
			showInfo : true,
			infoText : '第{0}页 . 共{1}页'
		},
		columns : [ {
			dataField : "prodName",
			caption : "产品名称",
			alignment : "center",
            allowSorting:false,
		},{
			dataField : "couponName",
			caption : "优惠券名称",
			alignment : "center",
            allowSorting:false,
		},{
			dataField : "couponAmount",
			caption : "优惠金额",
			alignment : "center",
            allowSorting:false,
		},{
			dataField : "day",
			caption : "到期时间",
			alignment : "center",
		}]
	});
}

var getOrders = function(borrId){
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
            args.borrId = borrId;
            $.ajax({
                url: "/zloan-manage/order.action",
                data: args,
                success: function(result) {
                    if(result.code == 1){
                        var obj = result.object;
                        var list = obj.list;
                        var total = obj.total;
                        deferred.resolve(list, { totalCount: total });
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
	
	
	$("#orderTable").dxDataGrid({
				dataSource : {
					 store: orders
				},
				dateSerializationFormat:"yyyy-MM-dd HH:mm:ss",
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
					allowedPageSizes : [10, 15, 30, 45, 60 ],
					showInfo : true,
					infoText : '第{0}页 . 共{1}页'
				},
				columns : [ {
					dataField : "borrNum",
					caption : "合同编号",
					alignment : "center",
				},{
                    dataField : "type",
                    caption : "类型",
                    alignment : "center",
                    lookup: { dataSource: [
                        { value: '2', format: '还款' },
                        { value: '4', format: '还款(代收)(银生宝)' },
                        { value: '5', format: '主动还款(银生宝)' },
                        { value: '7', format: '线下还款' },
                        { value: '6', format: '减免'},
                        { value: '8', format: '批量代扣(银生宝)'},
                        { value: '13', format: '还款(代收)(海尔)' },
                        { value: '12', format: '主动还款(海尔)' },
                        { value: '14', format: '批量代扣(海尔)'}
                    ],  displayExpr: 'format' } ,
                    calculateCellValue: function (data) {
                        if(data.type){
                            if (data.type==2){
                                return "充值还款"
                            }else if (data.type==4){
                                return "还款(代收)(银生宝)"
                            }else if (data.type==5){
                                return "主动还款(银生宝)"
                            }else if (data.type==7){
                                return "线下还款"
                            }else if (data.type==6){
                                return "减免"
                            }else if (data.type==8){
                                return "批量代扣(银生宝)"
                            }else if(data.type==9){
                                return "拉卡拉代扣";
                            }else if(data.type==10){
                                return "拉卡拉批量代扣";
                            }else if (data.type==13){
                                return "还款(代收)(海尔)"
                            }else if (data.type==12){
                                return "主动还款(海尔)"
                            }else if (data.type==14){
                                return "批量代扣(海尔)"
                            }else{
                                return "";
                            }
                        }
                    }
                }, {
                    dataField : "rlState",
                    caption : "状态",
                    alignment : "center",
                    lookup: { dataSource: [
                        { value: 'p', format: '处理中' },
                        { value: 's', format: '成功' },
                        { value: 'f', format: '失败' },
                    ],  displayExpr: 'format' } ,
                    cellTemplate: function (container, options) {
                        var stateValue = options.value;
                        if(stateValue){
                            var stateValues = stateValue.split(",");
                            $("<div>").append(stateValues[0]).appendTo(container);
                        }
                    }
                },{
					dataField : "actAmount",
					caption : "金额",
					alignment : "center",
                    allowFiltering:false,
				}, {
					dataField : "createDate",
					caption : "时间",
					alignment : "center",
					dataType: "date" ,
                    allowFiltering:false,
                    format: function (date) {
                        var month = date.getMonth() + 1,
                            day = date.getDate(),
                            year = date.getFullYear(),
                            hours = date.getHours(),
                            minutes = date.getMinutes(),
                            seconds = date.getSeconds();
                        if(hours < 10){
                            hours = "0" + hours;
                        }
                        if(minutes < 10){
                            minutes = "0" + minutes;
                        }
                        if(seconds < 10){
                            seconds = "0" + seconds;
                        }

                        return year + "/" + month + "/" + day + " " + hours + ":" + minutes + ":" + seconds;
                    }
				}, {
					dataField : "reason",
					caption : "备注信息",
					alignment : "center",
                    allowFiltering:false,
				},
					{
						dataField : "serialNo",
						caption : "流水号",
						alignment : "center",
					}]
			});
};

var loadtable_bank = function(perId){
	DevExpress.config({
	    forceIsoDateParsing: true,
	});
	
	var banks = new DevExpress.data.CustomStore({
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
            args.perId = perId;
            $.ajax({
                url: "/zloan-manage/bank.action",
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
	
	
	$("#bankTable").dxDataGrid({
				dataSource : {
					 store: banks
				},
				dateSerializationFormat:"yyyy-MM-dd HH:mm:ss",
				remoteOperations: {
		            sorting: true,
		            paging: true,
		            filtering:true
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
					allowedPageSizes : [10, 15, 30, 45, 60 ],
					showInfo : true,
					infoText : '第{0}页 . 共{1}页'
				},
				columns : [ {
					dataField : "id",
					caption : "	主键",
					alignment : "center",
				}, {
					dataField : "bankName",
					caption : "银行名称",
					alignment : "center",
				}, {
					dataField : "bankNum",
					caption : "银行卡号",
					alignment : "center",
				},{
					dataField : "phone",
					caption : "预留手机号码",
					alignment : "center",
				}, {
					dataField : "createDate",
					caption : "添加时间",
					alignment : "center",
					dataType: "date" ,
				}, {
					dataField : "status",
					caption : "状态",
					alignment : "center",
					calculateCellValue: function (data) {
						 if(data.status){
								 if(data.status==0){
									 return "无效";
								 }else if (data.status==1){
									 return "主卡"
								 }else if (data.status==2){
									 return "副卡"
								 }else{
									 return "";
								 }
							 }
					}
				} ]
			});
};

var initToolbar_memo = function() {
		$("#memo_tool_add").dxButton({
			hint : "新增备注",
			text : "新增",
			icon : "add",
			onClick : function() {
				$("#contractId").val(borrNum_root);
				$("#contractKey").val(brroid_root);
				$("#createUser").val(loginId);
				$("#collectionMemo").modal({show:true,backdrop: 'static', keyboard: false});
			}
		});
	
		$("#btnRemark_layer").off("click").click(function () {
			var remark = $("#remark").val();
				if(remark == null || remark == "" || remark.length < 1){
					alert("请输入备注内容");
					return;
				}
			var formData = $("#remarkForm_layer").serialize();
			$("#btnRemark_layer").attr("disabled",true);
			$.ajax({
				url: "/zloan-manage/loanManagement/collectionRemark.action",
				data: formData,
				success: function(result) {
					if(result.code == 1){
						alert("操作成功");
						$("#remark").val("");
						$("#collectionMemo").modal("hide");
						tableUtils.refresh("memoTable");
						$("#btnRemark_layer").removeAttr("disabled");
					}else{
						return;
					}
				},
				error: function(data) {
					console.info(data);
					return;
				},
				timeout: 50000
			});
		});
}

var loadtable_memo = function(borrId){
	DevExpress.config({
	    forceIsoDateParsing: true,
	});
	
	var memos = new DevExpress.data.CustomStore({
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
            args.borrId = borrId;
            $.ajax({
                url: "/zloan-manage/memo.action",
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
	
	
	$("#memoTable").dxDataGrid({
				dataSource : {
					 store: memos
				},
				dateSerializationFormat:"yyyy-MM-dd HH:mm:ss",
				remoteOperations: {
		            sorting: true,
		            paging: true,
		            filtering:true
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
					allowedPageSizes : [10, 15, 30, 45, 60 ],
					showInfo : true,
					infoText : '第{0}页 . 共{1}页'
				},
				columns : [{
					dataField : "",
					caption : "",
					alignment : "",
					width: "100",
				}, {
					dataField : "type",
					caption : "	标题",
					alignment : "center",
                    allowSorting:false,
                    calculateCellValue: function (data) {
                        if(data.type){
                            if (data.type=='A'){
                                return "催收备注"
                            }else if (data.type=='B'){
                                return "提醒备注"
                            }else{
                                return data.type;
                            }
                        }
                    }
				}, {
					dataField : "remark",
					caption : "	内容",
					alignment : "center",
                    allowSorting:false,
				}, {
					dataField : "createUser",
					caption : "	催收人",
					alignment : "center",
                    allowSorting:false,
				}, {
					dataField : "createDate",
					caption : "	时间",
					alignment : "center",
                    dataType: 'date',
                    format: function (date) {
                        var month = date.getMonth() + 1,
                            day = date.getDate(),
                            year = date.getFullYear(),
                            hours = date.getHours(),
                            minutes = date.getMinutes(),
                            seconds = date.getSeconds();
                        if(hours < 10){
                            hours = "0" + hours;
                        }
                        if(minutes < 10){
                            minutes = "0" + minutes;
                        }
                        if(seconds < 10){
                            seconds = "0" + seconds;
                        }

                        return year + "/" + month + "/" + day + " " + hours + ":" + minutes + ":" + seconds;
                    }
				}]
			});
};


var loadtable_credit = function(idCard, name, perId){
	DevExpress.config({
	    forceIsoDateParsing: true,
	});
	
	var credits = new DevExpress.data.CustomStore({
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
            args.idCard = idCard;
            args.name = name;
            args.perId = perId;
            $.ajax({
                url: "/zloan-manage/polyXinli/credit.action",
                data: args,
                success: function(result) {
                	result = JSON.parse(result);
                    if(result.list != null){
                        deferred.resolve(result.list[0], { totalCount: result.total });
                        phones = "";
                        for(var i = 0; i < result.list[0].length; i++){
                            phones += result.list[0][i].phone_num + ",";
                        }
                    }else{
                        deferred.resolve(result.list, { totalCount: result.total });
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
	
	
	$("#creditTable").dxDataGrid({
				dataSource : {
					 store: credits
				},
				dateSerializationFormat:"yyyy-MM-dd HH:mm:ss",
				remoteOperations: {
		            sorting: true,
		            paging: true,
		            filtering:true
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
					allowedPageSizes : [10, 15, 30, 45, 60 ],
					showInfo : true,
					infoText : '第{0}页 . 共{1}页'
				},
				columns : [ {
					dataField : "phone_num",
					caption : "手机号码",
					alignment : "center",
				},{
					dataField : "contact_name",
					caption : "互联网标识",
					alignment : "center",
				}, {
					dataField : "phone_num_loc",
					caption : "归属地",
					alignment : "center",
				}, {
					dataField : "call_cnt",
					caption : "联系次数",
					alignment : "center",
				}, {
					dataField : "call_len",
					caption : "联系时间",
					alignment : "center",
				}, {
					dataField : "call_out_cnt",
					caption : "主叫次数",
					alignment : "center",
				}, {
					dataField : "call_in_cnt",
					caption : "被叫次数",
					alignment : "center",
				}, {
					dataField : "yMName",
					caption : "通讯录姓名",
					alignment : "center",
				}]
			});
};

function creditInvestigation(idCard, name){
	$("#creditInvestigation").click(function(){
		window.open("/zloan-manage/page_loan/creditInvestigation.html?idCard=" + idCard + "&name=" + escape(name));
	});
}

var getModileContact = function(perId){
	DevExpress.config({
	    forceIsoDateParsing: true,
	});
	
	var contacts = new DevExpress.data.CustomStore({
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
            args.perId = perId;
            args.phones = phones;
            $.ajax({
                url: "/zloan-manage/contact.action",
                data: args,
                success: function(result) {
                	result = JSON.parse(result);
                	if(result.list != null){
                        deferred.resolve(result.list[0], { totalCount: result.total });
					}else{
                        deferred.resolve(result.list, { totalCount: result.total });
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
	
	
	$("#modileContactTable").dxDataGrid({
				dataSource : {
					 store: contacts
				},
				dateSerializationFormat:"yyyy-MM-dd HH:mm:ss",
				remoteOperations: {
		            sorting: true,
		            paging: true,
		            filtering:true
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
					allowedPageSizes : [10, 15, 30, 45, 60 ],
					showInfo : true,
					infoText : '第{0}页 . 共{1}页'
				},
				columns : [ {
					dataField : "phone",
					caption : "手机号码",
					alignment : "center",
				},{
					dataField : "name",
					caption : "	通讯录姓名",
					alignment : "center",
				}]
			});
};

function getModileContactExport(perId) {
		var url = "/zloan-manage/export/phonebook.action?perId="+perId;
	    exportData(url,null);
}

function getPolyXinliCreditExport(idCard, name, perId) {
    var url = "/zloan-manage/export/polyXinliCredit.action?perId="+perId+"&idCard="+idCard+"&name="+name;
    exportData(url,null);
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

//load the table of banck
var loadtable_black = function(perId) {
    $.ajax({
        type : "POST",
        url : "/zloan-manage/reviewVoBlackList.action",
        data : {
            "perId" : perId,
        },
        success : function(msg) {
            loadtable2_black(msg);
        }
    });
}

// load the table
var loadtable2_black = function(jsonobj) {
    $("#black_table").dxDataGrid({
        dataSource : {
            store : {
                type : "array",
                // key : "id",
                data : jsonobj
            }
        },
        rowAlternationEnabled : true,
        showRowLines : true,
        allowColumnReordering : true,
        allowColumnResizing : true,
        columnAutoWidth : true,
        columnFixing : {
            enabled : true
        },
        paging : {
            pageSize : 20,
        },
        pager : {
            showPageSizeSelector : true,
            allowedPageSizes : [10, 15, 30, 45, 60 ],
            showInfo : true,
            infoText : '第{0}页 . 共{1}页'
        },
        onSelectionChanged : function(data) {
            $("#tool_edit").dxButton({
                disabled : data.selectedRowsData.length != 1,
            });
            $("#tool_del").dxButton({
                disabled : !data.selectedRowsData.length,
            });
            $("#tool_info").dxButton({
                disabled : data.selectedRowsData.length != 1,
            });
            // deleteButton.option("disabled", !data.selectedRowsData.length)
        },
        columns : [ {
            dataField : "id",
            caption : "ID",
            alignment : "center",

        }, {
            dataField : "meaning",
            caption : "操作",
            alignment : "center",
        }, {
            dataField : "reason",
            caption : "原因",
            alignment : "center",
        }, {
            dataField : "emplloyeeName",
            caption : "操作人",
            alignment : "center",
        }, {
            dataField : "createDate",
            caption : "操作时间",
            alignment : "center",
            dataType : "date",
            format: function (date) {
                var month = date.getMonth() + 1,
                    day = date.getDate(),
                    year = date.getFullYear(),
                    hours = date.getHours(),
                    minutes = date.getMinutes(),
                    seconds = date.getSeconds();
                if(hours < 10){
                    hours = "0" + hours;
                }
                if(minutes < 10){
                    minutes = "0" + minutes;
                }
                if(seconds < 10){
                    seconds = "0" + seconds;
                }

                return year + "/" + month + "/" + day + " " + hours + ":" + minutes + ":" + seconds;
            },
        }]
    });
}

Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}


function getContract(){
    if(borrNum_root != null){
        $.ajax({
            type : "GET",
            url : "/zloan-manage/contract/" + borrNum_root + ".action",
            success : function(msg) {
                if(msg.code == 200){
                    window.open(msg.data);
                }else {
                    alert(msg.data)
                }
            }
        });
    }else{
        alert("该订单号为空");
    }
};