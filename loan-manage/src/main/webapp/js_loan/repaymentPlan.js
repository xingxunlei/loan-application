var initRepaymentPlanTable = function(){
    tableUtils.initMuliTableToolBar(
        "repaymentPlanTable",
        "repaymentPlan/queryRepaymentPlan.action?userNo="+usernum,
        null,
        [
            {dataField : "borrNum",caption : "合同编号",alignment : "center",allowFiltering:true,filterOperations:["="],allowSorting:false},
            {dataField : "days",caption : "逾期天数",alignment : "center",allowFiltering:true,filterOperations:["="]},
            {dataField : "borrId",caption : "borrId",alignment : "center",visible: false},
            {dataField : "customerId",caption : "customerId",alignment : "center",visible: false,allowSorting:false},
            {dataField : "customerName",caption : "姓名",alignment : "center",allowFiltering:true,width:110,filterOperations:["="],allowSorting:false},
            {dataField : "customerIdValue",caption : "身份证号码",alignment : "center",allowFiltering:true,width:190,filterOperations:["="],allowSorting:false},
            {dataField : "customerMobile",caption : "手机号码",alignment : "center",allowFiltering:true,width:140,filterOperations:["="],allowSorting:false},
            {dataField : "productName",caption : "产品类型",alignment : "center",allowFiltering:true,allowSorting:false,filterOperations:["="],
                lookup:{
                    dataSource:pruducts,displayExpr: 'format'
                },width:125
            },
            {dataField : "amount",caption : "本金",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "monthInterest",caption : "利息",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "penalty",caption : "违约金",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "penaltyInterest",caption : "罚息",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "sumAmount",caption : "合计",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "surplusTotalAmount",caption : "剩余还款总额",alignment : "center",allowFiltering:false,allowSorting:false},
            {
                dataField : "repayDate",
                caption : "到期日",
                alignment : "center",
                allowFiltering:true,
                dataType:"date",
                filterOperations:["=","between"],
                allowSorting:true,calculateCellValue: function (data) {
                if(data.repayDate){
                    return data.repayDate.toString();
                }
            }
            },
            {dataField : "state",caption : "借款状态",alignment : "center",allowFiltering:true,allowSorting:false,width:100,filterOperations:["="],
                lookup:{
                    dataSource:[
                        { value: 'BS005', format: '逾期未还' },
                        { value: 'BS004', format: '待还款' },
                    ],displayExpr: 'format'
                },
                calculateCellValue: function (data) {
                    if(data.state=='BS001'){
                        return "申请中";
                    }else if (data.state=='BS002'){
                        return "待签约"
                    }else if (data.state=='BS003'){
                        return "已签约"
                    }else if (data.state=='BS004'){
                        return "待还款"
                    }else if (data.state=='BS005'){
                        return "逾期未还"
                    }else if (data.state=='BS006'){
                        return "正常结清"
                    }else if (data.state=='BS007'){
                        return "已取消"
                    }else if (data.state=='BS008'){
                        return "审核未通过"
                    }else if (data.state=='BS009'){
                        return "电审未通过"
                    }else if (data.state=='BS010'){
                        return "逾期结清"
                    }else if (data.state=='BS011'){
                        return "放款中"
                    }else if (data.state=='BS012'){
                        return "放款失败"
                    }
                }}
        ],
        "还款计划"+new Date(),
        function(e){
            var dataGrid = e.component;
            var toolbarOptions = e.toolbarOptions.items;
            toolbarOptions.push({
                location: "before",
                widget: "dxButton",
                options: {
                    hint: "查看",
                    text: "查看",
                    visible : !disableButton("ym-db",0),
                    icon: "find",
                    onClick: function () {
                        var selectData = dataGrid.getSelectedRowsData();
                        if (selectData.length == 0) {
                            alert("请选择需要查看的还款计划");
                            return;
                        }
                        if (selectData.length > 1) {
                            alert("一次只能操作一条数据");
                            return;
                        }
                        var customerId = selectData[0].customerId;
                        var contractKey = selectData[0].borrId;
                        var contractId = selectData[0].borrNum;
                        console.info(customerId + "==" + contractKey + "==" + contractId);
                        layer_alert(customerId, contractKey, contractId);
                    }
                }
            },
                {
                    location: "before",
                    widget: "dxButton",
                    visible : !disableButton("ym-db",1),
                    options: {
                        hint: "刷新",
                        text: "刷新",
                        icon: "refresh",
                        onClick: function () {
                            tableUtils.refresh("repaymentPlanTable");
                        }
                    }
                })
        }
    );
};

var repaymentPlan = function () {
    $('.modal-backdrop').hide();
    checkPageEnabled("ym-db");
    initRepaymentPlanTable();
};

