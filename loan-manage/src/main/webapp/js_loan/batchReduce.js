var setToolBar_batchReduce = function(){
    $(".modal").on('hide.bs.modal', function () {
        tableUtils.clearSelection("batchReduceTable");
    });
    $(".modal").on('hidden.bs.modal', function () {
        tableUtils.clearSelection("batchReduceTable");
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
                    var dataGrid = $('#batchReduceTable').dxDataGrid('instance');
                    dataGrid.refresh();
                }else{
                    alert(result.message);
                }
                $("#btnReduce_loan").removeAttr("disabled");
                tableUtils.refresh("collectionListTable");
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
                    var dataGrid = $('#batchReduceTable').dxDataGrid('instance');
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
                    tableUtils.refresh("collectionListTable");
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
    $("#btnReduceBatch1").click(function(){
        var reduceData = $("#reduceData1").val();
        var reduceMoney = $("#reduceMoney1").val();
        var deductionsType ;
        var requestUrl = $("#requestUrl1").val();
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
        $("#btnReduceBatch1").attr("disabled",true);
        $.ajax({
            type:'POST',
            url: requestUrl,
            data: {reduceData:reduceData,reduceMoney:reduceMoney, deductionsType:deductionsType},
            success: function(result) {
                $("#btnReduceBatch1").removeAttr("disabled");
                alert(result.message);
                if(result.code == 1){
                    $("#collectionReduceBatch1").modal("hide");
                    tableUtils.refresh("batchReduceTable");
                }else{
                    return;
                }
            },
            error: function(data) {
                $("#collectionReduceBatch1").removeAttr("disabled");
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
                        tableUtils.refresh("batchReduceTable");
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
                var dataGrid = $('#batchReduceTable').dxDataGrid('instance');
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
var initBatchReduceTable = function () {

    tableUtils.initMuliTableToolBar(
        "batchReduceTable",
        'loanManagement/queryBatchReduce.action?userNo='+usernum,
        null,
        [
            {dataField : "bedueDays",fixed: true,caption : "逾期天数",alignment : "center",allowFiltering:true,filterOperations:["="],allowSorting:true},
            {dataField : "customerName",fixed: true,caption : "姓名",alignment : "center",allowFiltering:true,filterOperations:["="],allowSorting:false},
            {dataField : "surplusTotalAmount",caption : "剩余还款总额",alignment : "center",allowFiltering:false,filterOperations:["="],allowSorting:true},
            {dataField : "endDateString",caption : "到期日期",alignment : "center",allowFiltering:true,allowSorting:true
                ,filterOperations:["=","between"],dataType:"date",calculateCellValue: function (data) {
                    if(data.endDateString){
                        return data.endDateString.toString();
                    }else{
                        return "";
                    }
                }
            },
            //  {dataField : "stateString",caption : "借款状态",alignment : "center",allowFiltering:true,allowSorting:false,
            //     lookup:{
            //         dataSource:[
            //             { value: 'BS005', format: '逾期未还' },
            //             { value: 'BS004', format: '待还款' }
            //         ],displayExpr: 'format'
            //     }
            // },
            {dataField : "auditer",caption : "催收人",alignment : "center",allowFiltering:true,allowSorting:false,
                lookup: {
                    dataSource: collectors,
                    valueExpr: 'value',
                    displayExpr: 'format'
                }
            },
            {dataField : "bdStatus",caption : "上单状态",alignment : "center",allowFiltering:true,allowSorting:false,
                lookup:{
                    dataSource:[
                        { value: '1', format: '成功' },
                        { value: '2', format: '失败可扣' },
                        { value: '3', format: '失败' }
                    ],displayExpr: 'format'
                },
               calculateCellValue: function (data) {
                if(data.bdStatus == 1) {
                    return "成功";
                }else if(data.bdStatus == 2) {
                    return "失败可扣";
                }else if(data.bdStatus == 3) {
                    return "失败";
                }
            }
            },
            {dataField : "reason",caption : "失败原因",alignment : "center",allowFiltering:false,allowSorting:false},

            {dataField : "contractKey",caption : "合同编号ID",visible: false}
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
                        visible : !disableButton("ym-dh",0),
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
                        hint : "批量扣款(银生宝)",
                        text : "批量扣款(银生宝)",
                        visible : !disableButton("ym-dh",1),
                        onClick : function() {
                            $("#reduce_batch1").resetForm();
                            var selectData = tableUtils.loadTableSelectRows("batchReduceTable");
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
                            $("#requestUrl1").val("loanManagement/batchCollection.action?collectType=ysb&createUser="+usernum);
                            $("#reduceData1").val(data);
                            $("#reduceNames1").val(customerNames);
                            $("#payInterface").text("注意：本次批量扣款支付接口为银生宝！");
                            $("#collectionReduceBatch1").modal({show: true, backdrop: 'static', keyboard: false});
                        }
                    }
                },
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint : "批量扣款(海尔)",
                        text : "批量扣款(海尔)",
                        visible : !disableButton("ym-dh",3),
                        onClick : function() {
                            $("#reduce_batch1").resetForm();
                            var selectData = tableUtils.loadTableSelectRows("batchReduceTable");
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
                            $("#requestUrl1").val("loanManagement/batchCollection.action?collectType=haier&createUser="+usernum);
                            $("#reduceData1").val(data);
                            $("#reduceNames1").val(customerNames);
                            $("#payInterface").text("注意：本次批量扣款支付接口为海尔金融！");
                            $("#collectionReduceBatch1").modal({show: true, backdrop: 'static', keyboard: false});
                        }
                    }
                },
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint: "刷新",
                        text: "刷新",
                        visible : !disableButton("ym-dh",2),
                        icon: "refresh",
                        onClick: function () {
                            tableUtils.refresh("batchReduceTable");
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

var loanBatchReduce = function () {
    $('.modal-backdrop').hide();
    setToolBar_batchReduce();
    checkPageEnabled("ym-dh");
    initBatchReduceTable();
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