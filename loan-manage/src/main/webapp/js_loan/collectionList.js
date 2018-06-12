var setButtons_ctl = function () {
    $("#tool_reduce").dxButton({

    });
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
                        tableUtils.refresh("collectionListTable");
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
    $("#btnRemark").click(function () {
        var remark = $("#remark").val();
        if(remark == null || remark == ""){
            alert("请输入备注内容");
            return;
        }
        var formData = $("#remarkForm").serialize();
        $("#btnRemark").attr("disabled",true);
        $.ajax({
            type:'POST',
            url: "loanManagement/collectionRemark.action",
            data: formData,
            success: function(result) {
                $("#btnRemark").removeAttr("disabled");
                alert(result.message);
                if(result.code == 1){
                    $("#remarkForm").resetForm();
                    $("#collectionRemark").modal("hide");
                    tableUtils.refresh("collectionListTable");
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
    $("#btnReduce").click(function () {
        var optAmount = $("#optAmount").val();
        var surplusTotalAmount = $("#surplusTotalAmount").val();

        if(parseFloat(optAmount) <= 0){
            alert("还款金额必须大于0");
            return false;
        }

        if(!Number(optAmount) ){
            alert("金额只能输入数字");
            return false;
        }

        if(optAmount.toString().indexOf(".") != -1){
            if(optAmount.toString().split(".")[1].length > 2){//小数点后大于两位
                alert("小数点仅能保留两位");
                return false;
            }
        }

        if(parseFloat(optAmount) > parseFloat(surplusTotalAmount)){
            alert("还款金额不能大于最大还款金额");
            return false;
        }
        var askUrl = $("#askUrl").val();
        $("#btnReduce").attr("disabled", true);
        var formData = $("#reduce").serialize();
        $.ajax({
            type: 'POST',
            url: askUrl,
            data: formData,
            success: function (result) {
                $("#btnReduce").removeAttr("disabled");
                alert(result.message);
                if (result.code == 1) {
                    $("#reduce").resetForm();
                    $("#collectionReduce").modal("hide");
                    tableUtils.refresh("collectionListTable");
                } else {
                    return;
                }
            },
            error: function (data) {
                $("#btnReduce").removeAttr("disabled");
                console.info(data);
                return;
            },
            timeout: 50000
        });
    });
};

var initCollectionListTable = function () {
    tableUtils.initMuliTableToolBar(
        "collectionListTable",
        "loanManagement/queryCollectors.action?userId="+usernum,
        null,
        [
            {dataField : "blackList",caption : "黑名单",alignment : "center",allowFiltering:true,allowSorting:false,
                lookup: {
                    dataSource: [
                        { value: 'Yes', format: '是' },
                        { value: 'No', format: '否' }
                    ],
                    valueExpr: 'value',
                    displayExpr: 'format'
                },width:80,fixed: true
            },
            {dataField : "bedueDays",fixed: true,caption : "逾期天数",alignment : "center",allowFiltering:true,filterOperations:["="]},
            {dataField : "customerId",fixed: true,caption : "ID",alignment : "center",allowFiltering:false,visible: false},
            {dataField : "customerName",fixed: true,caption : "姓名",alignment : "center",width:110,allowFiltering:true,filterOperations:["="],allowSorting:false},
            {dataField : "customerIdValue",fixed: true,caption : "身份证号码",alignment : "center",width:190,allowFiltering:true,filterOperations:["="],allowSorting:false},
            {dataField : "customerMobile",fixed: true,caption : "手机号码",alignment : "center",width:140,allowFiltering:true,filterOperations:["="],allowSorting:false},
            {dataField : "productId",caption : "产品类型ID",alignment : "center",allowFiltering:false,visible: false,allowSorting:false},
            {dataField : "productName",caption : "产品类型",alignment : "center",allowSorting:false,allowFiltering:true,width:125,lookup:{
                dataSource:pruducts,displayExpr: 'format'
            }},
            {dataField : "amount",caption : "贷款金额",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "totalInterest",caption : "应还利息",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "penalty",caption : "应还违约金",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "penaltyInterest",caption : "应还罚息",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "sumAmount",caption : "应还合计",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "surplusTotalAmount",caption : "剩余还款总额",alignment : "center",allowFiltering:false,filterOperations:["="],allowSorting:true},
            {
                dataField : "endDate",
                caption : "到期日期",
                alignment : "center",
                allowFiltering:true,
                dataType: 'date',
                filterOperations:["=","between"],
                allowSorting:true,
                calculateCellValue: function (data) {
                    if(data.endDate){
                        return data.endDate.toString();
                    }
                }
            },
            {dataField : "lastCallDate",caption : "最新催收时间",alignment : "center",dataType: 'date',allowFiltering:true,filterOperations:["=","between"],format: function (date) {
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
            },allowSorting:true},
            {dataField : "stateString",caption : "最新扣款时间",alignment : "center",dataType: 'date',allowFiltering:true,filterOperations:["=","between"],format: function (date) {
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
            },allowSorting:true},
            {dataField : "contractId",caption : "合同编号",alignment : "center",allowFiltering:true,filterOperations:["="],allowSorting:false},
            {dataField : "loanAmount",caption : "放款金额",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "repayAmount",caption : "还款金额",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "contractKey",caption : "合同主键",alignment : "center",allowFiltering:false,visible: false}
    ],
        "催收队列"+new Date(),
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
                    visible : !disableButton("ym-dd",0),
                    icon: "find",
                    onClick: function () {
                        var selectData = dataGrid.getSelectedRowsData();
                        if (selectData.length == 0) {
                            alert("请选择需要查看的催收信息");
                            return;
                        }
                        if (selectData.length > 1) {
                            alert("一次只能操作一条数据");
                            return;
                        }
                        var customerId = selectData[0].customerId;
                        var contractId = selectData[0].contractKey;
                        var contractNum = selectData[0].contractId;
                        layer_alert(customerId, contractId, contractNum);
                    }
                }
            },
            {
                location: "before",
                widget: "dxButton",
                options: {
                    hint : "新增备注",
                    text : "催收备注",
                    visible : !disableButton("ym-dd",1),
                    icon : "comment",
                    onClick : function() {
                        var selectData = dataGrid.getSelectedRowsData();
                        if (selectData.length == 0) {
                            alert("请选择需要备注的催收信息");
                            return;
                        }
                        if (selectData.length > 1) {
                            alert("一次只能操作一条数据");
                            return;
                        }
                        $("#remark").val("");
                        var contractId = selectData[0].contractId;
                        var contractKey = selectData[0].contractKey;
                        $("#contractId").val(contractId);
                        $("#contractKey").val(contractKey);
                        $("#createUser").val(usernum);
                        $("#collectionRemark").modal({show: true, backdrop: 'static', keyboard: false});
                    }
                }
            },
            {
                location: "before",
                widget: "dxButton",
                options: {
                    hint : "扣款",
                    text : "提交扣款",
                    visible : !disableButton("ym-dd",2),
                    icon : "chevrondown",
                    onClick : function() {
                        var selectData = dataGrid.getSelectedRowsData();
                        if (selectData.length == 0) {
                            alert("请选择需要备注的催收信息");
                            return;
                        }
                        if (selectData.length > 1) {
                            alert("一次只能操作一条数据");
                            return;
                        }
                        $("#reduce").resetForm();
                        $("#tool_reduce").attr("disabled", true);
                        $.ajax({
                            url: "loanManagement/queryMainBankInfo.action",
                            data: {userId: selectData[0].customerId},
                            success: function (result) {
                                if (result.code == 1) {
                                    var data = result.object;
                                    var bankInfo = data.bankInfo;
                                    var banks = data.banks;
                                    var html = "";
                                    for (var i = 0; i < banks.length; i++) {
                                        if (banks[i].support == 1) html += "<option value='" + banks[i].id + "'>" + banks[i].bankName + "</option>";
                                    }
                                    $("#bankId").append(html);
                                    $("#bankId").val(bankInfo.bankId);
                                    $("#bankNum").val(bankInfo.bankNum);
                                    $("#phone").val(bankInfo.phone);
                                    $("#name").val(selectData[0].customerName);
                                    $("#idCardNo").val(selectData[0].customerIdValue);
                                    $("#borrNum").val(selectData[0].contractId);
                                    $("#borrowId").val(selectData[0].contractKey);
                                    $("#surplusTotalAmount").val(selectData[0].surplusTotalAmount);
                                    $("#optAmount").val(selectData[0].surplusTotalAmount);
                                    $("#createUser_ask").val(usernum);
                                    $("#askUrl").val("loanManagement/askCollection.action");
                                    $("#collectionReduce").modal({show: true, backdrop: 'static', keyboard: false});
                                } else {

                                    return;
                                }
                                $("#tool_reduce").removeAttr("disabled");
                            },
                            error: function (data) {
                                $("#tool_reduce").removeAttr("disabled");
                                console.info(data);
                                return;
                            },
                            timeout: 50000
                        });
                    }
                }
            },
            {
                location: "before",
                widget: "dxButton",
                options: {
                    hint : "拉黑",
                    text : "拉黑",
                    icon : "clear",
                    visible : !disableButton("ym-dd",3),
                    onClick : function() {
                        var selectobj = dataGrid.getSelectedRowsData();
                        if (selectobj.length > 0 && selectobj.length == 1) {
                            if(selectobj[0].blackList == "Yes"){
                                alert("该用户已被拉黑");
                            }else{
                                if (confirm("确定要将该用户拉入黑名单吗?")) {
                                    $("#form-whiteBlackList").resetForm();
                                    $("#whiteBlackListUserId").val(usernum);
                                    $("#whiteBlackListContractId").val(selectobj[0].customerId);
                                    $("#whiteBlackListType").val(1);
                                    $("#whiteBlackListTitle").html("拉入黑名单原因");
                                    $("#whiteBlackList").modal({show: true, backdrop: 'static', keyboard: false});
                                }
                            }
                        } else if(selectobj.length > 1){
                            alert("一次只能操作拉黑一个用户");
                        } else {
                            alert("请选择需要拉入黑名单的记录");
                        }
                    }
                }
            },
                /*{
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint : "拉卡拉扣款",
                        text : "拉卡拉扣款",
                        icon : "chevrondown",
                        onClick : function() {
                            var selectData = dataGrid.getSelectedRowsData();
                            if (selectData.length == 0) {
                                alert("请选择需要备注的催收信息");
                                return;
                            }
                            if (selectData.length > 1) {
                                alert("一次只能操作一条数据");
                                return;
                            }
                            $("#reduce").resetForm();
                            $("#tool_reduce").attr("disabled", true);
                            $.ajax({
                                url: "loanManagement/queryMainBankInfo.action",
                                data: {userId: selectData[0].customerId},
                                success: function (result) {
                                    if (result.code == 1) {
                                        var data = result.object;
                                        var bankInfo = data.bankInfo;
                                        var banks = data.banks;
                                        var html = "";
                                        for (var i = 0; i < banks.length; i++) {
                                            if (banks[i].support == 1) html += "<option value='" + banks[i].id + "'>" + banks[i].bankName + "</option>";
                                        }
                                        $("#bankId").append(html);
                                        $("#bankId").val(bankInfo.bankId);
                                        $("#bankNum").val(bankInfo.bankNum);
                                        $("#phone").val(bankInfo.phone);
                                        $("#perId").val(selectData[0].customerId);
                                        $("#bankInfoId").val(bankInfo.id);
                                        $("#name").val(selectData[0].customerName);
                                        $("#idCardNo").val(selectData[0].customerIdValue);
                                        $("#borrNum").val(selectData[0].contractId);
                                        $("#borrowId").val(selectData[0].contractKey);
                                        $("#surplusTotalAmount").val(selectData[0].surplusTotalAmount);
                                        $("#optAmount").val(selectData[0].surplusTotalAmount);
                                        $("#createUser_ask").val(usernum);
                                        $("#askUrl").val("loanManagement/lakalaAskCollection.action");
                                        $("#collectionReduce").modal({show: true, backdrop: 'static', keyboard: false});
                                    } else {

                                        return;
                                    }
                                    $("#tool_reduce").removeAttr("disabled");
                                },
                                error: function (data) {
                                    $("#tool_reduce").removeAttr("disabled");
                                    console.info(data);
                                    return;
                                },
                                timeout: 50000
                            });
                        }
                    }
                },
            {
                location: "before",
                widget: "dxButton",
                options: {
                    hint : "洗白",
                    text : "洗白",
                    icon : "favorites",
                    onClick : function() {
                        var selectobj = dataGrid.getSelectedRowsData();
                        if (selectobj.length > 0 && selectobj.length == 1) {
                            console.info(selectobj[0].blackList);
                            if(selectobj[0].blackList == "Yes"){
                                if (confirm("确定要将该用户洗白吗?")) {
                                    $("#form-whiteBlackList").resetForm();
                                    $("#whiteBlackListUserId").val(usernum);
                                    $("#whiteBlackListContractId").val(selectobj[0].customerId);
                                    $("#whiteBlackListType").val(0);
                                    $("#whiteBlackListTitle").html("洗白原因");
                                    $("#whiteBlackList").modal({show: true, backdrop: 'static', keyboard: false});
                                }
                            }else{
                                alert("该用户不在黑名单中");
                            }
                        } else if(selectobj.length > 1){
                            alert("一次只能操作洗白一个用户");
                        } else {
                            alert("请选择需要洗白的记录");
                        }
                    }
                }
            },*/

                {
                    location: "before",
                    widget: "dxButton",
                    visible : !disableButton("ym-dd",4),
                    options: {
                        hint: "刷新",
                        text: "刷新",
                        icon: "refresh",
                        onClick: function () {
                            tableUtils.refresh("collectionListTable");
                        }
                    }
                })
        }
    )
};
var collectionList = function () {
    $('.modal-backdrop').hide();
    setButtons_ctl();
    checkPageEnabled("ym-dd");
    initCollectionListTable();
};