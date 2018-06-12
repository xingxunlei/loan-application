$(function() {
	getpath();
	getTabs();

});
var himid_root  = 0;
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
				var brroid = info.split("_")[1];
				himid_root = himid;
				loadbox_data(himid);
				getCardPicById(himid);
				
			}
		});
	} catch (e) {
	}

}
var getTabs = function() {
	$("#myTabs").dxTabs({
		dataSource : [ {
			text : "客户信息",
			icon : "user",
			content : "1"
		}, {
			text : "贷款信息",
			icon : "money",
			content : "2"
		}, {
			text : "银行卡信息",
			icon : "card",
			content : "3"
		} ,{
			text : "资金流水",
			icon : "bookmark",
			content : "4"
		} ,{
            text : "手机通讯录",
            icon : "card",
            content : "6"
        },{
			text:"认证流程",
			icon: "bookmark",
			content: "7"
		}
		],
		selectedIndex : 0,
		onItemClick : function(e) {
			$("#1").css("display", "none");
			$("#2").css("display", "none");
			$("#3").css("display", "none");
			$("#4").css("display", "none");
            $("#6").css("display", "none");
            $("#7").css("display", "none");
			$("#" + e.itemData.content + "").css("display", "block");
			if(e.itemData.content =="2"){
				loadtable_loan(himid_root);
			}else if (e.itemData.content =="3"){
				loadtable_banck(himid_root);
			}else if (e.itemData.content =="4"){
				getOrders(himid_root);
			}else if (e.itemData.content =="6"){
                getPhones_kf(himid_root);
            }else if (e.itemData.content == "7"){
				getNode(himid_root);
			}
		}
	});

};

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

var relatives = "";
var relatives_name = "";
var rela_phone = "";
var society = "";
var society_name = "";
var soci_phone = "";

var loadbox_data = function(himid) {
	$.ajax({
		type : "POST",
		url : "/zloan-manage/manager/selectUserPrivateVo.action",
		data : {
			'himid' : himid
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

			relatives = himinfo.relatives_value;
			relatives_name = himinfo.relatives_name;
			rela_phone = himinfo.rela_phone;
			society = himinfo.society_value;
			society_name = himinfo.society_name;
			soci_phone = himinfo.soci_phone;

			loadbox_box();

		}
	});

};

var loadbox_box = function() {
	$("#name").dxTextBox({
		value : name,
		showClearButton : false,
	});
	$("#card_num").dxTextBox({
		value : card_num,
		showClearButton : false,
	});
	$("#qq_num").dxTextBox({
		value : qq_num,
		showClearButton : false,
	});
	$("#email").dxTextBox({
		value : email,
		showClearButton : false,
	});
	$("#education").dxTextBox({
		value : education,
		showClearButton : false,
	});
	$("#marry").dxTextBox({
		value : marry,
		showClearButton : false,
	});
	$("#getchild").dxTextBox({
		value : getchild,
		showClearButton : false,
	});

	$("#usuallyaddress").dxTextArea({
		value : usuallyaddress,
		showClearButton : false,
	});
	$("#source_value").dxTextBox({
		value : source_value,
		showClearButton : false,
	});
	$("#phone").dxTextBox({
		value : phone,
		showClearButton : false,
	});
	$("#address").dxTextArea({
		value : address,
		showClearButton : false,
	});
	$("#birthday").dxDateBox({
		value : birthday,
		showClearButton : false,
	});
	$("#blacklist").dxTextBox({
		value : blacklist,
		showClearButton : false,
	});
	$("#create_date").dxDateBox({
		value : create_date,
		showClearButton : false,
	});
	
	
	

	$("#profession").dxTextBox({
		value : profession,
		showClearButton : false,
	});
	$("#monthlypay").dxTextBox({
		value : monthlypay,
		showClearButton : false,
	});
	$("#business").dxTextBox({
		value : business,
		showClearButton : false,
	});
	$("#busi_city").dxTextBox({
		value : busi_city,
		showClearButton : false,
	});
	$("#busi_address").dxTextBox({
		value : busi_address,
		showClearButton : false,
	});
	$("#busi_phone").dxTextBox({
		value : busi_phone,
		showClearButton : false,
	});

	$("#relatives").dxTextBox({
		value : relatives,
		showClearButton : false,
	});
	$("#relatives_name").dxTextBox({
		value : relatives_name,
		showClearButton : false,
	});
	$("#rela_phone").dxTextBox({
		value : rela_phone,
		showClearButton : false,
	});
	$("#society").dxTextBox({
		value : society,
		showClearButton : false,
	});
	$("#society_name").dxTextBox({
		value : society_name,
		showClearButton : false,
	});
	$("#soci_phone").dxTextBox({
		value : soci_phone,
		showClearButton : false,
	});

};
var getCardPicById = function(himid) {
	$.ajax({
		type : "POST",
		url : "/zloan-manage/manager/getCardPicById.action",
		data : {
			'himid' : himid
		},
		success : function(msg) {
			var cardinfo = msg;
            if(cardinfo != null) {
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
};

var getOrders = function(himid) {
	$.ajax({
		type : "POST",
		url :  "/zloan-manage/manager/getOrders.action",
		data : {
			"himid" : himid,
		},
		success : function(msg) {
			loadtable2_loan_kf(msg);
		}
	});
};

var loadtable2_loan_kf = function(jsonobj) {
	
	$("#loan_table_kf").dxDataGrid({
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
		},
		columns : [ {
			dataField : "borrNum",
			caption : "借款编号",
			alignment : "center",
		},{
            dataField : "serialNo",
            caption : "流水号",
            alignment : "center",
        },{
			dataField : "type",
			caption : "类型",
			alignment : "center",
			 calculateCellValue: function (data) {
				 if(data.type){
					 if(data.type=='1'){
						 return "放款";
					 }else if (data.type==2){
                         return "充值还款"
                     }else if (data.type==4){
                         return "还款(代收)"
                     }else if (data.type==5){
                         return "主动还款"
                     }else if (data.type==7){
                         return "线下还款"
                     }else if (data.type==6){
                         return "减免"
                     }else if (data.type==8){
                         return "批量代扣"
                     }else{
                         return "";
                     }
				 }
	            },

		}, {
			dataField : "optAmount",
			caption : "金额",
			alignment : "center"
		}, {
			dataField : "rlState",
			caption : "状态",
			alignment : "center",
			 calculateCellValue: function (data) {
				 if(data.rlState){
					 //交易状态：p处理中，s成功，f失败,q清结算处理中,c清结算失败
					 if(data.rlState=='p'){
						 return "处理中";
					 }else if (data.rlState=='s'){
						 return "成功"
					 }else if (data.rlState=='f'){
						 return "失败"
					 }else if (data.rlState=='q'){
						 return "清结算处理中"
					 }else if (data.rlState=='c'){
						 return "清结算失败"
					 }else{
						 return "";
					 }
				 }
	            },
		}, {
			dataField : "reason",
			caption : "原因",
			alignment : "center",
		} , {
			dataField : "updateDate",
			caption : "时间",
			alignment : "center",
			calculateCellValue: function (data) {
				var d = new Date(parseInt(data.updateDate)).toLocaleString().replace(/:\d{1,2}$/,' '); 
				return d;
	            }
		}
		
		]
	});
};
var loadtable_loan = function(himid) {
	$.ajax({
		type : "POST",
		url :  "/zloan-manage/manager/selectLoanInfoPrivateVoForOperator.action",
		data : {
			"himid" : himid,
		},
		success : function(msg) {
			loadtable2_loan(msg);
		}
	});
};

var loadtable2_loan = function(jsonobj) {
	$("#loan_table").dxDataGrid({
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
		columns : [  {
			dataField : "borrNum",
			caption : "借款编号",
			alignment : "center",

		},{
			dataField : "productName",
			caption : "产品类型",
			alignment : "center",

		}, {
			dataField : "borrStatusStr",
			caption : "合同状态",
			alignment : "center",
		}, {
			dataField : "askborrDate",
			caption : "申请时间",
			alignment : "center",
            dataType : "date",
		}, {
			dataField : "makeborrDate",
			caption : "合同签约日",
			alignment : "center",
			dataType : "date",
		}, {
            dataField : "payDate",
            caption : "放款成功日",
            alignment : "center",
            dataType : "date",
        },{
            dataField : "maximumAmount",
            caption : "放款金额",
            alignment : "center",
        },{
			dataField : "planrepayDate",
			caption : "约定还款日",
			alignment : "center",
            dataType : "date"
		},{
			dataField : "planRepay",
			caption : "应还款总额",
			alignment : "center"
		},{
			dataField : "actRepayDate",
			caption : "实际结清日",
			alignment : "center",
            dataType : "date"
		},{
			dataField : "actRepayAmount",
			caption : "实际还款总额",
			alignment : "center"
		}]
	});
};
var loadtable_banck = function(himid) {
	$.ajax({
		type : "POST",
		url :  "/zloan-manage/manager/selectBankInfoVo.action",
		data : {
			"himid" : himid,
		},
		success : function(msg) {
			loadtable2_banck(msg);
		}
	});
};

// load the table
var loadtable2_banck = function(jsonobj) {
	$("#bank_table").dxDataGrid({
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
		onSelectionChanged : function(data) {
			$("#tool_edit").dxButton({
				disabled : data.selectedRowsData.length != 1
			});
			$("#tool_del").dxButton({
				disabled : !data.selectedRowsData.length
			});
			$("#tool_info").dxButton({
				disabled : data.selectedRowsData.length != 1
			});
		},
		columns : [ {
			dataField : "bank_name",
			caption : "银行名称",
			alignment : "center"

		}, {
			dataField : "bankNum",
			caption : "银行卡号",
			alignment : "center"
		}, {
			dataField : "phone",
			caption : "预留手机号",
			alignment : "center"
		}, {
			dataField : "type",
			caption : "卡片类型",
			alignment : "center"
		}, {
			dataField : "creationDate",
			caption : "添加时间",
			alignment : "center",
			dataType : "date"
		}, {
			dataField : "status_name",
			caption : "状态",
			alignment : "center"

		} ]
	});
};
var loadtable_black = function(himid) {
	$.ajax({
		type : "POST",
		url : "/zloan-manage/manager/getReviewVoBlackList.action",
		data : {
			"himid" : himid,
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
			pageSize : 10,
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
			
		}]
	});
}

var getPhones_kf = function(himid) {
    var args = {};
    args.skip = 0;
    args.take = 5000;
    args.perId = himid;
    args.phones = "";
    $.ajax({
        url: "/zloan-manage/contact.action",
        data: args,
        type : "GET",
        success: function(result) {
            result = JSON.parse(result);
            if(result.list != null) {
                loadPhoneTable_kf(result.list[0]);
            }else {
                loadPhoneTable_kf({});
			}
        }
    });
}

function loadPhoneTable_kf(contacts){
    $("#phone_table_kf").dxDataGrid({
        dataSource : {
            type : "array",
            store: contacts
        },
        export: {enabled: true, allowExportSelectedData: true, excelFilterEnabled: true, fileName: "phoneList"},
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
            caption : "通讯录姓名",
            alignment : "center",
        }]
    });
};

function getNode(himid){
	$.post(
		"/zloan-manage/manager/nodeinfo.action",
		{himid:himid},
		function (jsonobj) {
            $("#node_info").dxDataGrid({
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
                },
                columns : [{
                    dataField : "nodeName",
                    caption : "认证节点",
                    alignment : "center",
                }, {
                    dataField : "nodeStatusStr",
                    caption : "节点状态",
                    alignment : "center",
                }, {
                    dataField : "description",
                    caption : "说明",
                    alignment : "center",
                }, {
                    dataField : "nodeDate",
                    caption : "认证日期",
                    alignment : "center",
                    dataType : "date",

                },{
                    dataField : "updateDate",
                    caption : "更新日期",
                    alignment : "center",
                    dataType : "date",

                }]
            });
        },'json'
		);

	//查询当前应认证节点
	$.ajax({
		url : "/zloan-manage/drainage/query/borrow/"+himid+".action",
		success : function(msg){

			if(msg.code == 1 && msg.object){
				if(msg.object.borr_status == "BS001" && msg.object.id){
					//表示人工审核节点
                    $("#note").dxTextBox({
                        value : "人工审核",
						width : 160,
                        showClearButton : false,
                    });
                    $("#note input").css("color","#FF0000");
                    $("#label").css("display","inline");

				}else if(msg.object.borr_status == "BS001"){
					switch (msg.object.code){
						case "207" :
						{
							switch (msg.object.data){
								case "1" :
                                    $("#note").dxTextBox({
                                        value : "身份证正面认证",
                                        width : 160,
                                        showClearButton : false,
                                    });
                                    $("#note input").css("color","#FF0000");
                                    $("#label").css("display","inline");
                                    break;
                                case "2" :
                                    $("#note").dxTextBox({
                                        value : "身份证反面认证",
                                        width : 160,
                                        showClearButton : false,
                                    });
                                    $("#note input").css("color","#FF0000");
                                    $("#label").css("display","inline");
                                    break;
                                case "3" :
                                    $("#note").dxTextBox({
                                        value : "个人认证",
                                        width : 160,
                                        showClearButton : false,
                                    });
                                    $("#note input").css("color","#FF0000");
                                    $("#label").css("display","inline");
                                    break;
                                case "4" :
                                    $("#note").dxTextBox({
                                        value : "人脸识别",
                                        width : 160,
                                        showClearButton : false,
                                    });
                                    $("#note input").css("color","#FF0000");
                                    $("#label").css("display","inline");
                                    break;
                                case "5" :
                                    $("#note").dxTextBox({
                                        value : "手机认证",
                                        width : 160,
                                        showClearButton : false,
                                    });
                                    $("#note input").css("color","#FF0000");
                                    $("#label").css("display","inline");
                                    break;
                                case "6" :
                                    $("#note").dxTextBox({
                                        value : "银行卡认证",
                                        width : 160,
                                        showClearButton : false,
                                    });
                                    $("#note input").css("color","#FF0000");
                                    $("#label").css("display","inline");
                                    break;
								default:
                                    $("#label").css("display","none");
							}
						}
							break;
						case "209" :
                            $("#note").dxTextBox({
                                value : "通讯录认证",
                                width : 160,
                                showClearButton : false,
                            });
                            $("#note input").css("color","#FF0000");
                            $("#label").css("display","inline");
							break;
						case "220" :
                            $("#note").dxTextBox({
                                value : "芝麻认证",
                                width : 160,
                                showClearButton : false,
                            });
                            $("#note input").css("color","#FF0000");
                            $("#label").css("display","inline");
							break;
						default :
                            $("#label").css("display","none");
					}
				}else{
                    $("#label").css("display","none");
				}
			}else{
				$("#label").css("display","none");
			}
		}
	})
}