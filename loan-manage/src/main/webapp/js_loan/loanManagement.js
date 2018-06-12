var setToolBar_lmg = function(){
    $(".modal").on('hide.bs.modal', function () {
        tableUtils.clearSelection("loanManagementTable");
    });
    $(".modal").on('hidden.bs.modal', function () {
        tableUtils.clearSelection("loanManagementTable");
    });

    $("#btnReduce_loan").click(function () {
        var maxReduce = $("#maxReduce").val();
        var reduceAmount = $("#reduceAmount").val();
        var remark = $("#message-text").val();
        var contractId = $("#contractID").val();
        //操作金额判断
        if(!optAument(reduceAmount, maxReduce, "减免")){
            return;
        }
        //进行减免
        $("#btnReduce_loan").attr("disabled",true);
        $.ajax({
            type:'POST',
            url: "loanManagement/reduceLoan.action",
            data: {contractId:contractId,reduce:reduceAmount,remark:remark,type:6,userName:usernum},
            success: function(result) {
                if(result.code == 1){
                    alert("申请减免成功");
                    $("#reduceApprove").modal("hide");
                    var dataGrid = $('#loanManagementTable').dxDataGrid('instance');
                    dataGrid.refresh();
                }else{
                    alert(result.message);
                }
                $("#btnReduce_loan").removeAttr("disabled");
                tableUtils.refresh("loanManagementTable");
            },
            error: function(data) {
                console.info(data);
                $("#btnReduce").removeAttr("disabled");
                return;
            },
            timeout: 50000
        });


        $("#btnReduce").removeAttr("disabled");
    });
    $("#btnOffline").click(function () {
        var contractId = $("#contractIDOffline").val();
        var surplusTotalAmount = $("#surplusTotalAmountOffline").val();
        var offlineAmount = $("#offlineAmount").val();
        var remark = $("#remarkOffline").val();
        //操作金额判断
        if(!optAument(offlineAmount, surplusTotalAmount, "还款")){
            return;
        }

        $("#btnOffline").attr("disabled",true);
        $.ajax({
            type:'POST',
            url: "loanManagement/reduceLoan.action",
            data: {contractId:contractId,reduce:offlineAmount,remark:remark,type:7,userName:usernum},
            success: function(result) {
                $("#btnOffline").removeAttr("disabled");
                if(result.code == 1){;
                    alert("线下还款成功");
                    $("#offlineApprove").modal("hide");
                    var dataGrid = $('#loanManagementTable').dxDataGrid('instance');
                    dataGrid.refresh();
                }else{
                    alert(result.message);
                    return;
                }
            },
            error: function(data) {
                $("#btnOffline").removeAttr("disabled");
                console.info(data);
            },
            timeout: 50000
        });


    });
    $("#btnReduceT").click(function () {
        var optAmount = $("#optAmount").val();
        var surplusTotalAmount = $("#totalAmount").val();
        //操作金额判断
        if(!optAument(optAmount, surplusTotalAmount, "扣款")){
            return;
        }

        $("#btnReduceT").attr("disabled",true);
        var askUrl = $("#askUrl").val();
        var formData = $("#reduce").serialize();
        $.ajax({
            type:'POST',
            url: askUrl,
            data: formData,
            success: function(result) {
                $("#btnReduceT").removeAttr("disabled");
                alert(result.message);
                if(result.code == 1){
                    $("#collectionReduce").modal("hide");
                    tableUtils.refresh("loanManagementTable");
                }else{
                    return;
                }
            },
            error: function(data) {
                $("#btnReduceT").removeAttr("disabled");
                console.info(data);
                return;
            },
            timeout: 50000
        });
    });
    $("#btnReduceBatch").click(function(){
        var reduceData = $("#reduceData").val();
        var reduceMoney = $("#reduceMoney").val();
        var deductionsType ;
        var requestUrl = $("#requestUrl").val();
        if(!requestUrl){
            alert("错误");
            return;
        }
        //操作金额判断
        if(!optAument(reduceMoney, "", "扣款", "batchDeductions")){
            return;
        }

        if(reduceMoney == 0){
            deductionsType = 1;
        }else {
            deductionsType = 2;
        }
        $("#btnReduceBatch").attr("disabled",true);
        $.ajax({
            type:'POST',
            url: requestUrl,
            data: {reduceData:reduceData,reduceMoney:reduceMoney, deductionsType:deductionsType},
            success: function(result) {
                $("#btnReduceBatch").removeAttr("disabled");
                alert(result.message);
                if(result.code == 1){
                    $("#collectionReduceBatch").modal("hide");
                    tableUtils.refresh("loanManagementTable");
                }else{
                    return;
                }
            },
            error: function(data) {
                $("#collectionReduceBatch").removeAttr("disabled");
                console.info(data);
                return;
            },
            timeout: 50000
        });
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
                        tableUtils.refresh("loanManagementTable");
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
};
var doTransfer = function (userIdd) {
    console.info(userIdd);
    var itemValue = $("#itemValue").val();
    $("#tmsg").html("正在转件,请稍后......");
    $("#transferApprove").modal("hide");
    $("#trP").modal({show: true, backdrop: 'static', keyboard: false});
    $.ajax({
        url: "loanManagement/transferLoan.action?opUserId="+usernum,
        type:'POST',
        data: {contractIds:itemValue,userId:userIdd},
        success: function(result) {
            $("#trP").modal("hide");
            if(result.code == 1){
                alert(result.message);
                $("#transferApprove").modal("hide");
                var dataGrid = $('#loanManagementTable').dxDataGrid('instance');
                dataGrid.refresh();
            }else{
                alert(result.message);
                $("#transferApprove").modal({show: true, backdrop: 'static', keyboard: false});
                return;
            }
        },
        error: function(data) {
            console.info(data);
            return;
        },
        timeout: 50000
    });
};



var initReceiptUserTable = function(){
    tableUtils.initSinglePages("transferUserTable","loanManagement/queryReceiptUsersByUser.action?type=2&userNo="+usernum,null,[
        {dataField : "userSysno",caption : "账号",alignment : "center",allowFiltering:false},
        {dataField : "userName",caption : "姓名",alignment : "center",allowFiltering:true,filterOperations:["="]},
        {dataField : "productSysno",caption : "催收产品",alignment : "center",allowFiltering:false,calculateCellValue: function (data) {
            if(data.productSysno == "1"){
                return "悠米闪借";//.substring(0,10);
            }}},
        {dataField : "status",caption : "是否启用",alignment : "center",allowFiltering:false,calculateCellValue: function (data) {
            if(data.status == "A"){
                return "启用";
            }}},
        {
            dataField: "userSysno",
            caption : "操作",
            allowFiltering: false,
            allowSorting: false,
            cellTemplate: function (container, options) {
                $("<div>").append("<button type='button' onclick='doTransfer(\""+options.value+"\")' class='btn btn-primary btn-sm'>&nbsp;&nbsp;选择&nbsp;&nbsp;</button>").appendTo(container);
            }
        }
    ],10)
};
var initLoanManagementTable = function () {

    tableUtils.initMuliTableToolBar(
        "loanManagementTable",
        'loanManagement/queryLoans.action?userNo='+usernum,
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
            {dataField : "bedueDays",width:70,fixed: true,caption : "逾期天数",alignment : "center",allowFiltering:true,filterOperations:["="],allowSorting:true},
            {dataField : "customerId",fixed: true,caption : "用户ID",alignment : "center",visible: false},
            {dataField : "customerName",fixed: true,caption : "姓名",alignment : "center",width:110,allowFiltering:true,filterOperations:["="],allowSorting:false},
            {dataField : "customerIdValue",fixed: true,caption : "身份证",alignment : "center",width:190,allowFiltering:true,filterOperations:["="],allowSorting:false},
            {dataField : "customerMobile",fixed: true,caption : "手机号码",alignment : "center",width:140,allowFiltering:true,filterOperations:["="],allowSorting:false},
            {dataField : "productName",caption : "产品类型",alignment : "center",allowFiltering:true,allowSorting:false,width:125,
                lookup:{
                    dataSource:pruducts,displayExpr: 'format'
                }
            },
            {dataField : "amount",caption : "贷款金额",alignment : "center",allowFiltering:false,filterOperations:["="],allowSorting:false},
            {dataField : "totalInterest",caption : "应还利息",alignment : "center",allowFiltering:false,allowFiltering:false,allowSorting:false},
            {dataField : "penalty",caption : "应还违约金",alignment : "center",allowFiltering:false,allowFiltering:false,allowSorting:false},
            {dataField : "penaltyInterest",caption : "应还罚息",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "sumAmount",caption : "应还合计",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "surplusTotalAmount",caption : "剩余还款总额",alignment : "center",allowFiltering:false,filterOperations:["="],allowSorting:true},
            {dataField : "endDateString",caption : "到期日期",alignment : "center",allowFiltering:true,allowSorting:true
                ,filterOperations:["=","between"],dataType:"date",calculateCellValue: function (data) {
                if(data.endDateString){
                    return data.endDateString.toString();
                }else{
                    return "";
                }
            },
                width:120
            },
            {dataField : "settleDateString",caption : "结清日",alignment : "center",allowFiltering:true,filterOperations:["=","between"],dataType:"date",allowSorting:true,
                calculateCellValue: function (data) {
                    if(data.settleDateString){
                        return data.settleDateString.toString().substring(0,10);
                    }else{
                        return "";
                    }
                },
                width:120
            },

            {dataField : "stateString",caption : "借款状态",alignment : "center",allowFiltering:true,allowSorting:false,
                lookup:{
                    dataSource:[
                        { value: 'BS005', format: '逾期未还' },
                        { value: 'BS010', format: '逾期结清' },
                        { value: 'BS004', format: '待还款' },
                        { value: 'BS006', format: '正常结清' }
                    ],displayExpr: 'format'
                },width:100
            },
            {dataField : "auditer",caption : "催收人",alignment : "center",allowFiltering:true,allowSorting:false,width:100,
                lookup: {
                    dataSource: collectors,
                    valueExpr: 'value',
                    displayExpr: 'format'
                }
            },
            {dataField : "lastCallDateString",caption : "最新催收时间",alignment : "center",
                allowFiltering:true,filterOperations:["=","between"],dataType:"date",allowSorting:true,
                calculateCellValue: function (data) {
                    if(data.lastCallDateString){
                        console.info(data.lastCallDateString);
                        return data.lastCallDateString.toString().substring(0,19);
                    }else{
                        return "";
                    }
                }
            },
            {dataField : "orderString",caption : "最新扣款时间",alignment : "center",allowFiltering:true,filterOperations:["=","between"],dataType:"date",allowSorting:true,
                calculateCellValue: function (data) {
                    if(data.orderString){
                        return data.orderString.toString().substring(0,19);
                    }
                }
            },
            {dataField : "contractID",caption : "合同编号",alignment : "center",allowFiltering:true,allowSorting:false,filterOperations:["="]},
            {dataField : "contractKey",caption : "合同编号ID",visible: false},
            {dataField : "loanAmount",caption : "放款金额",alignment : "center",allowFiltering:false,allowSorting:false}
        ],
        "贷后管理导出"+new Date(),
        function(e){
            var dataGrid = e.component;
            var toolbarOptions = e.toolbarOptions.items;
            toolbarOptions.push(
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint : "查看",
                        text: "查看",
                        visible : !disableButton("ym-dc",0),
                        onClick: function() {
                            var selectData = dataGrid.getSelectedRowsData();
                            if (selectData.length == 0) {
                                alert("请选择需要查看信息");
                                return;
                            }
                            if (selectData.length > 1) {
                                alert("一次只能操作一条数据");
                                return;
                            }
                            var customerId = selectData[0].customerId;
                            var contractId = selectData[0].contractKey;
                            var contract = selectData[0].contractID;
                            console.info(customerId+"="+contractId+"="+contract);
                            layer_alert(customerId, contractId,contract);
                            dataGrid.clearSelection();
                        }
                    }
                },
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint : "申请减免",
                        text : "申请减免",
                        visible : !disableButton("ym-dc",1),
                        onClick : function() {
                            $("#reduce-form").resetForm();
                            var selectobj = dataGrid.getSelectedRowsData();
                            if (selectobj.length == 1) {
                                if (selectobj[0].stateString == "逾期结清" || selectobj[0].stateString == "正常结清") {
                                    alert("该单已结清,无需申请减免");
                                } else {

                                    var reduceAmount = (parseFloat(selectobj[0].penalty) + parseFloat(selectobj[0].penaltyInterest)).toFixed(2);
                                    $("#customerName").val(selectobj[0].customerName);
                                    $("#customerIdValue").val(selectobj[0].customerIdValue);
                                    $("#contractID").val(selectobj[0].contractID);
                                    $("#surplusTotalAmount").val(selectobj[0].surplusTotalAmount);
                                    $("#maxReduce").val(reduceAmount);
                                    if(parseFloat($("#surplusTotalAmount").val()) < parseFloat(reduceAmount)){
                                        $("#reduceAmount").val($("#surplusTotalAmount").val());
                                    }else {
                                        $("#reduceAmount").val(reduceAmount);
                                    }
                                    $("#penalty").val(selectobj[0].penalty);
                                    $("#penaltyInterest").val(selectobj[0].penaltyInterest);
                                    $("#reduceApprove").modal({show: true, backdrop: 'static', keyboard: false});
                                }
                            } else if (selectobj.length > 1) {
                                alert("申请减免每次仅能操作一条");
                            } else {
                                alert("请选择记录");
                            }
                        }
                    }
                },
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint : "线下还款",
                        text : "线下还款",
                        visible : !disableButton("ym-dc",2),
                        onClick : function() {
                            $("#form-horizontal").resetForm();
                            var selectobj = dataGrid.getSelectedRowsData();
                            if (selectobj.length == 1) {
                                if (selectobj[0].stateString == "逾期结清" || selectobj[0].stateString == "正常结清") {
                                    alert("该单已结清,无需线下还款");
                                    return;
                                }
                                $("#customerNameOffline").val(selectobj[0].customerName);
                                $("#customerIdValueOffline").val(selectobj[0].customerIdValue);
                                $("#contractIDOffline").val(selectobj[0].contractID);
                                $("#surplusTotalAmountOffline").val(selectobj[0].surplusTotalAmount);
                                $("#offlineAmount").val(selectobj[0].surplusTotalAmount);
                                $("#offlineApprove").modal({show: true, backdrop: 'static', keyboard: false});
                            } else if (selectobj.length > 1) {
                                alert("线下还款每次仅能操作一条数据");
                            } else {
                                alert("请选择需要线下还款的记录");
                            }
                        }
                    }
                },
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint : "提交扣款",
                        text : "提交扣款",
                        visible : !disableButton("ym-dc",3),
                        onClick : function() {
                            $("#reduce").resetForm();
                            var selectData = dataGrid.getSelectedRowsData();
                            if (selectData.length == 0) {
                                alert("请选择需要扣款的信息");
                                return;
                            }
                            if (selectData.length > 1) {
                                alert("一次只能操作一条数据");
                                return;
                            }
                            if (selectData[0].stateString == "逾期结清" || selectData[0].stateString == "正常结清") {
                                alert("该单已结清");
                                return;
                            }
                            $("#tool_reduce_money").attr("disabled", true);
                            $.ajax({
                                type: 'POST',
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
                                        /////////////////////////////
                                        $("#name").val(selectData[0].customerName);
                                        $("#idCardNo").val(selectData[0].customerIdValue);
                                        $("#borrNum").val(selectData[0].contractID);
                                        $("#borrowId").val(selectData[0].contractKey);
                                        $("#totalAmount").val(selectData[0].surplusTotalAmount);
                                        $("#optAmount").val(selectData[0].surplusTotalAmount);
                                        $("#createUser_ask").val(usernum);
                                        $("#askUrl").val("loanManagement/askCollection.action");
                                        $("#collectionReduce").modal({show: true, backdrop: 'static', keyboard: false});
                                    } else {

                                        return;
                                    }
                                    $("#tool_reduce_money").removeAttr("disabled");
                                },
                                error: function (data) {
                                    $("#tool_reduce_money").removeAttr("disabled");
                                    console.info(data);
                                    return;
                                },
                                timeout: 50000
                            });
                        }
                    }
                },
                /*{
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint : "拉卡拉扣款",
                        text : "拉卡拉扣款",
                        visible : false,
                        onClick : function() {
                            $("#reduce").resetForm();
                            var selectData = dataGrid.getSelectedRowsData();
                            if (selectData.length == 0) {
                                alert("请选择需要扣款的信息");
                                return;
                            }
                            if (selectData.length > 1) {
                                alert("一次只能操作一条数据");
                                return;
                            }
                            if (selectData[0].stateString == "逾期结清" || selectData[0].stateString == "正常结清") {
                                alert("该单已结清");
                                return;
                            }
                            $("#tool_reduce_money").attr("disabled", true);
                            $.ajax({
                                type: 'POST',
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
                                        /////////////////////////////
                                        $("#perId").val(selectData[0].customerId);
                                        $("#bankInfoId").val(bankInfo.id);
                                        $("#name").val(selectData[0].customerName);
                                        $("#idCardNo").val(selectData[0].customerIdValue);
                                        $("#borrNum").val(selectData[0].contractID);
                                        $("#borrowId").val(selectData[0].contractKey);
                                        $("#totalAmount").val(selectData[0].surplusTotalAmount);
                                        $("#optAmount").val(selectData[0].surplusTotalAmount);
                                        $("#createUser_ask").val(usernum);
                                        $("#askUrl").val("loanManagement/lakalaAskCollection.action");
                                        $("#collectionReduce").modal({show: true, backdrop: 'static', keyboard: false});
                                    } else {

                                        return;
                                    }
                                    $("#tool_reduce_money").removeAttr("disabled");
                                },
                                error: function (data) {
                                    $("#tool_reduce_money").removeAttr("disabled");
                                    console.info(data);
                                    return;
                                },
                                timeout: 50000
                            });
                        }
                    }
                },*/
                /*
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint : "批量扣款",
                        text : "批量扣款",
                        visible : !disableButton("ym-dc",4),
                        onClick : function() {
                            $("#reduce_batch").resetForm();
                            var selectData = tableUtils.loadTableSelectRows("loanManagementTable");
                            if (selectData.length == 0) {
                                alert("请选择需要需要扣款的信息,至少一条。");
                                return;
                            }
                            var customerNames = "";
                            for (var i = 0; i < selectData.length; i++) {
                                var customerName = selectData[i].customerName;
                                if (selectData[i].stateString == "逾期未还") {
                                    if (i == selectData.length - 1) {
                                        customerNames += customerName;
                                    } else {
                                        customerNames += customerName + ",";
                                    }
                                }
                            }
                            if(customerNames == ""){
                                alert("只能代扣状态逾期未还的单子");
                                return
                            }
                            var data = JSON.stringify(selectData);
                            $("#requestUrl").val("loanManagement/batchCollection.action?createUser="+usernum);
                            $("#reduceData").val(data);
                            $("#reduceNames").val(customerNames);
                            $("#collectionReduceBatch").modal({show: true, backdrop: 'static', keyboard: false});
                        }
                    }
                },
                */
                /*,
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint : "拉卡拉批量扣款",
                        text : "拉卡拉批量扣款",
                        visible : false,
                        onClick : function() {
                            $("#reduce_batch").resetForm();
                            var selectData = tableUtils.loadTableSelectRows("loanManagementTable");
                            if (selectData.length == 0) {
                                alert("请选择需要需要扣款的信息,至少一条。");
                                return;
                            }
                            var customerNames = "";
                            for (var i = 0; i < selectData.length; i++) {
                                var customerName = selectData[i].customerName;
                                if (selectData[i].stateString == "逾期未还") {
                                    if (i == selectData.length - 1) {
                                        customerNames += customerName;
                                    } else {
                                        customerNames += customerName + ",";
                                    }
                                }
                            }
                            if(customerNames == ""){
                                alert("只能代扣状态逾期未还的单子");
                                return
                            }
                            var data = JSON.stringify(selectData);
                            $("#requestUrl").val("loanManagement/lakalaBatchCollection.action?createUser="+usernum);
                            $("#reduceData").val(data);
                            $("#reduceNames").val(customerNames);
                            $("#collectionReduceBatch").modal({show: true, backdrop: 'static', keyboard: false});
                        }
                    }
                }*/
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint : "转件",
                        text : "转件",
                        visible : !disableButton("ym-dc",5),
                        onClick : function() {
                            var selectobj = dataGrid.getSelectedRowsData();
                            if (selectobj.length > 0) {
                                var contractIds = "";
                                for (var i = 0; i < selectobj.length; i++) {
                                    if (selectobj[i].stateString == "逾期未还" || selectobj[i].stateString == "待还款") {
                                        if (i == selectobj.length - 1) {
                                            contractIds += selectobj[i].contractID+ "-" +selectobj[i].contractKey;
                                        } else {
                                            contractIds += selectobj[i].contractID + "-" +selectobj[i].contractKey + ",";
                                        }
                                    }
                                }
                                if(contractIds == ""){
                                    alert("请选择逾期未还或者待还款的记录进行转件");
                                    return;
                                }
                                console.info("转件合同号记录:" + contractIds);
                                $("#itemValue").val(contractIds);
                                initReceiptUserTable();
                                $("#transferApprove").modal({show: true, backdrop: 'static', keyboard: false});
                            } else {
                                alert("请选择需要转件的记录");
                            }
                        }
                    }
                },
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint : "拉黑",
                        text : "拉黑",
                        visible : !disableButton("ym-dc",6),
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
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint : "洗白",
                        text : "洗白",
                        visible : !disableButton("ym-dc",7),
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
                },
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint : "导出",
                        text : "导出",
                        visible : !disableButton("ym-dc",8),
                        onClick : function() {
                            var filter = dataGrid.getCombinedFilter();
                            filter = JSON.stringify(filter) == undefined ? '' : JSON.stringify(filter);
                            var url = "loanManagement/exportLoans.action?count=" +dataGrid.totalCount()+ "&filter=" + encodeURI(filter)+"&userNo="+usernum;
                            exportData(url,null);
                        }
                    }
                },
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint : "导出",
                        text : "导出催收备注",
                        visible : !disableButton("ym-dc",9),
                        onClick : function() {
                            var filter = tableUtils.loadTableFilter("loanManagementTable");
                            filter = JSON.stringify(filter) == undefined ? '' : JSON.stringify(filter);
                            var url = "loanManagement/exportLoansRemark.action?filter=" + encodeURI(filter)+"&userNo=" + usernum +"&count="+dataGrid.totalCount();
                            exportData(url,null);
                        }
                    }
                },
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint: "刷新",
                        text: "刷新",
                        visible : !disableButton("ym-dc",10),
                        icon: "refresh",
                        onClick: function () {
                            tableUtils.refresh("loanManagementTable");
                        }
                    }
                });
        }
    );
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

var loanManagement = function () {
    $('.modal-backdrop').hide();
    setToolBar_lmg();
    checkPageEnabled("ym-dc");
    initLoanManagementTable();
};
function optAument(inMoney, maxMoney, desc, type) {
    //批量扣款不能可以输入0
    if(type != "batchDeductions"){
        if(parseFloat(inMoney) <= 0){
            alert(desc + "金额必须大于0");
            return false;
        }

        if(!Number(inMoney) ){
            alert("金额只能输入数字");
            return false;
        }

        if(inMoney.toString().indexOf(".") != -1){
            if(inMoney.toString().split(".")[1].length > 2){//小数点后大于两位
                alert("小数点仅能保留两位");
                return false;
            }
        }

        if(parseFloat(inMoney) > parseFloat(maxMoney)){
            alert(desc + "金额不能大于最大" + desc + "金额");
            return false;
        }
    }else {
        if(parseFloat(inMoney) < 0){
            alert(desc + "金额不能为负数");
            return false;
        }
        if(!(inMoney === "0")){
            if(!Number(inMoney) ){
                alert("金额只能输入数字");
                return false;
            }
        }

        if(inMoney.toString().indexOf(".") != -1){
            if(inMoney.toString().split(".")[1].length > 2){//小数点后大于两位
                alert("小数点仅能保留两位");
                return false;
            }
        }

    }
    return true;
}