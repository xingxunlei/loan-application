
var initTable_rm = function(){
    tableUtils.initMuliTableToolBar(
    	"userTable",
        "order/queryOrders.action?userNo="+usernum,
        null,
        [
        {dataField : "blackList",caption : "黑名单",alignment : "center",allowFiltering:false,allowSorting:false,
            // lookup: {
            //     dataSource: [
            //         { value: 'Y', format: '是' },
            //         { value: 'N', format: '否' }
            //     ],
            //     valueExpr: 'value',
            //     displayExpr: 'format'
            // },width:80,fixed: true
            calculateCellValue: function (data) {
                if(data.blackList) {
                    if (data.blackList == "Y") {
                        return "是"
                    } else if (data.blackList == "N") {
                        return "否"
                    }
                }
            }
        },
        {
            dataField : "borrNum",
            caption : "合同编号",
            alignment : "center",
            filterOperations:["="],
            allowSorting:false
        },
        {
            dataField : "name",
            caption : "姓名",
            alignment : "center",
            filterOperations:["="],
            allowSorting:false,
            width:110
        },
        {
            dataField : "idCard",
            caption : "身份证号",
            alignment : "center",
            filterOperations:["="],
            allowSorting:false,
            width:190
        },
        {
            dataField : "phone",
            caption : "手机号码",
            alignment : "center",
            filterOperations:["="],
            allowSorting:false,
            width:140
        },
        {
            dataField : "bankName",
            caption : "开户行",
            alignment : "center",
            lookup: { dataSource: [
            { value: '1', format: '中国银行' },
            { value: '2', format: '农业银行' },
            { value: '3', format: '工商银行' },
            { value: '4', format: '建设银行' },
            { value: '5', format: '交通银行'},
            { value: '6', format: '平安银行'},
            { value: '7', format: '浦发银行'},
            { value: '8', format: '中信银行'},
            { value: '9', format: '兴业银行'},
            { value: '10', format: '光大银行'},
            { value: '11', format: '邮储银行'},
            { value: '12', format: '上海银行'}
        ],  displayExpr: 'format' }
        },
        {
            dataField : "bankNum",
            caption : "银行卡号",
            alignment : "center",
            filterOperations:["="],
            allowSorting:false
        },
        {
            dataField : "type",

            caption : "还款类型",
            alignment : "center",
            width:140,
            lookup: { dataSource: [
                { value: '2', format: '还款' },
                { value: '4', format: '还款(代收)(银生宝)' },
                { value: '5', format: '主动还款(银生宝)' },
                { value: '7', format: '线下还款' },
                { value: '6', format: '减免'},
                { value: '8', format: '批量代扣(银生宝)'},
                { value: '13', format: '还款(代收)(海尔)' },
                { value: '12', format: '主动还款(海尔)' },
                { value: '14', format: '批量代扣(海尔)'}/*,
                { value: '9', format: '拉卡拉代扣'},
                { value: '10', format: '拉卡拉批量代扣'}*/
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
        },
        {
            dataField : "rlState",
            caption : "还款状态",
            alignment : "left",
            lookup: { dataSource: [
                { value: 'p', format: '处理中' },
                { value: 's', format: '成功' },
                { value: 'f', format: '失败' }
            ],  displayExpr:'format'} ,
            cellTemplate: function (container, options) {
                var stateValue = options.value;
                if(stateValue){
                    if(stateValue.indexOf(",") > 0){
                        var stateValues = stateValue.split(",");
                        var state = stateValues[0];
                        if(state.length != 2){
                            $("<div>").append(state+"&nbsp;&nbsp;<button type='button' onclick='queryState(\""+stateValues[1]+"\")' class='btn btn-primary btn-xs' style='font-size: 12px;line-height: 15px;'>查询</button>").appendTo(container);
                        }else{
                            $("<div>").append(state).appendTo(container);
                        }
                    }else{
                        $("<div>").append(stateValue).appendTo(container);
                    }
                }
            }
        },
        {
            dataField : "actAmount",
            caption : "还款金额",
            alignment : "center",
            allowFiltering:false,
            allowSorting:false
        },
        {
            dataField : "createDate",
            caption : "还款时间",
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
        },
        {
            dataField : "createUser",
            caption : "操作人",
            alignment : "center",
            filterOperations:["="],
            allowSorting:false
        },
        {
            dataField : "collectionUser",
            caption : "催收人",
            alignment : "center",
            filterOperations:["="],
            allowSorting:false,
            lookup: {
                dataSource: collectors,
                valueExpr: 'value',
                displayExpr: 'format'
            }
        },
        {
            dataField : "reason",
            caption : "备注信息",
            alignment : "center",
            allowFiltering:false,
            allowSorting:false
        },
        {
            dataField : "serialNo",
            caption : "还款流水号",
            alignment : "center",
            filterOperations:["="],
            allowSorting:false
        }],
        "还款流水"+new Date(),
        function(e){
            var dataGrid = e.component;
            var toolbarOptions = e.toolbarOptions.items;
            toolbarOptions.push(
            {
                location: "before",
                widget: "dxButton",
                options: {
                    hint: "查看",
                    text: "查看",
                    visible : !disableButton("ym-da",0),
                    icon: "find",
                    onClick: function () {
                        var selectData = dataGrid.getSelectedRowsData();
                        if (selectData.length == 0) {
                            alert("请选择需要查看的还款流水");
                            return;
                        }
                        if (selectData.length > 1) {
                            alert("一次只能操作一条数据");
                            return;
                        }
                        var customerId = selectData[0].perId;
                        var contractKey = selectData[0].borrId;
                        var contractId = selectData[0].borrNum;
                        layer_alert(customerId, contractKey, contractId);
                    }
                }
            }
            ,
            {
                location: "before",
                widget: "dxButton",
                options: {
                    hint: "导出",
                    text: "导出",
                    visible : !disableButton("ym-da",1),
                    icon: "download",
                    onClick: function () {
                        var filter = dataGrid.getCombinedFilter();
                        filter = JSON.stringify(filter) == undefined ? '' : JSON.stringify(filter);
                        var url = "downloadOrder.action?filter=" + encodeURI(filter) + "&userNo=" +usernum+"&count="+dataGrid.totalCount();
                        exportData(url,null);
                    }
                }
            }
            ,
            {
                location: "before",
                widget: "dxButton",
                options: {
                    hint: "拉黑",
                    text: "拉黑",
                    visible : !disableButton("ym-da",2),
                    onClick: function () {
                        var selectobj = dataGrid.getSelectedRowsData();
                        if (selectobj.length > 0 && selectobj.length == 1) {
                            if(selectobj[0].blackList == 'Y'){
                                alert("该用户已经被拉黑！");
                                dataGrid.clearSelection();
                                return;
                            }
                            if (confirm("确定要将该用户拉入黑名单吗?")) {
                                $("#form-whiteBlackList").resetForm();
                                $("#whiteBlackListUserId").val(usernum);
                                $("#whiteBlackListContractId").val(selectobj[0].perId);
                                $("#whiteBlackListType").val(1);
                                $("#whiteBlackListTitle").html("拉入黑名单原因");
                                $("#whiteBlackList").modal({show: true, backdrop: 'static', keyboard: false});
                            }
                        } else if(selectobj.length > 1){
                            alert("一次只能操作拉黑一个用户");
                            dataGrid.clearSelection();
                        } else {
                            alert("请选择需要拉入黑名单的记录");
                            dataGrid.clearSelection();
                        }
                    }
                }
            }
            ,
            {
                location: "before",
                widget: "dxButton",
                options: {
                    hint: "刷新",
                    text: "刷新",
                    visible : !disableButton("ym-da",3),
                    icon: "refresh",
                    onClick: function () {
                        tableUtils.refresh("userTable");
                    }
                }
            })
        });
};
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

var queryState = function(serialNo){
    if(serialNo != null){
        $("#showMessage").html("正在从银生宝查询流水号为:&nbsp;&nbsp;"+serialNo+"&nbsp;&nbsp;的扣款情况");
        $("#queryDialog").modal({show: true, backdrop: 'static', keyboard: false});
        $.ajax({
            type: 'POST',
            url: "queryCostState.action?serialNo="+serialNo,
            success: function (result) {
                setTimeout("callBack("+result.code+",'"+result.message+"')",1000);
            },
            error: function (data) {
                $("#queryDialog").modal('hide');
                return;
            },
            timeout: 50000
        });
    }else{
        alert("该单流水号为空");
    }
};
var callBack = function(code,message){
    alert(message);
    $("#queryDialog").modal('hide');
    if (code != 30) {//回调银生宝成功
        tableUtils.refresh("userTable");
    }
};
var inintRepaymentBtn = function() {
    $("#btnWhiteBlackList").click(function(){
        var reason = $("#reason").val();
        if(reason == null || reason == ""){
            alert("请输入原因");
        }else{
            $("#btnWhiteBlackList").attr("disabled",true);
            $.ajax({
                type: 'POST',
                url: "loanManagement/whiteBlackList.action",
                data: $("#form-whiteBlackList").serialize(),
                success: function (result) {
                    $("#btnWhiteBlackList").removeAttr("disabled");
                    if (result.code == 1) {
                        alert("操作成功");
                        $("#whiteBlackList").modal("hide");
                        tableUtils.refresh("userTable");
                    } else {
                        alert(result.message);
                        return;
                    }
                },
                error: function (data) {
                    console.info(data);
                    return;
                },
                timeout: 50000
            });
        }
    });
}
var repayment = function() {
    $('.modal-backdrop').hide();
    checkPageEnabled("ym-da");
    initTable_rm();
    inintRepaymentBtn();
};