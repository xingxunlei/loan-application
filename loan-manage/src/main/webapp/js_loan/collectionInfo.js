var initCollectionInfoTable = function (collectors) {
    tableUtils.initMuliTableToolBar(
        "collectionInfoTable",
        "loanManagement/queryCollectorsInfo.action?userNo="+usernum,
        null,
        [
            {dataField : "bedueDays",fixed: true,caption : "逾期天数",alignment : "center",allowFiltering:true,filterOperations:["="],allowSorting:true},
            {dataField : "customerId",fixed: true,caption : "客户ID",alignment : "center",allowFiltering:false,visible: false,allowSorting:false},
            {dataField : "customerName",fixed: true,caption : "姓名",alignment : "center",allowFiltering:true,filterOperations:["="],allowSorting:false,width:100},
            {dataField : "customerIdValue",fixed: true,caption : "身份证号码",alignment : "center",allowFiltering:true,filterOperations:["="],allowSorting:false},
            {dataField : "customerMobile",fixed: true,caption : "手机号码",alignment : "center",allowFiltering:true,filterOperations:["="],allowSorting:false},
            {dataField : "productId",caption : "产品类型ID",alignment : "center",allowFiltering:false,visible: false,allowSorting:false},
            {dataField : "productName",caption : "产品类型",alignment : "center",allowSorting:false,allowFiltering:true,lookup:{
                dataSource:pruducts,displayExpr: 'format'
            },width:125},
            {dataField : "amount",caption : "贷款金额",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "totalInterest",caption : "应还利息",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "penalty",caption : "应还违约金",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "penaltyInterest",caption : "应还罚息",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "sumAmount",caption : "应还合计",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "surplusTotalAmount",caption : "剩余还款总额",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "endDate",caption : "到期日期",alignment : "center",dataType:"date",allowFiltering:true,
                filterOperations:["=","between"],allowSorting:true,
                calculateCellValue: function (data) {
                    if(data.endDate){
                        return data.endDate.toString().substring(0,10);
                    }
                }
            },
            {dataField : "settleDate",caption : "结清日",alignment : "center",dataType:"date",allowFiltering:true,filterOperations:["=","between"],
                allowSorting:true,
                calculateCellValue: function (data) {
                    if(data.settleDate){
                        return data.settleDate.toString().substring(0,10);
                    }
                }
            },
            {
                dataField: "stateString",
                caption: "借款状态",
                alignment: "center",
                allowFiltering: false,
                allowSorting: false,
                lookup: {
                    dataSource: [
                        {value: 'BS005', format: '逾期未还'},
                        {value: 'BS010', format: '逾期结清'}
                    ], displayExpr: 'format', valueExpr: 'value'
                },width:100
            },
            {dataField : "auditer",caption : "催收人",alignment : "center",allowFiltering:true,
                filterOperations:["="],allowSorting:false,width:100,
                lookup: {
                    dataSource: collectors,
                    valueExpr: 'value',
                    displayExpr: 'format'
                }
            },
            {dataField : "lastCallDate",caption : "最新催收时间",alignment : "center",allowFiltering:true,dataType:"date",
                filterOperations:["=","between"],allowSorting:true,
                calculateCellValue: function (data) {
                    if(data.lastCallDate){
                        return data.lastCallDate.toString().substring(0,10);
                    }
                }
            },
            {dataField : "contractId",caption : "合同编号",alignment : "center",allowFiltering:true,filterOperations:["="],allowSorting:false},
            {dataField : "contractKey",caption : "合同编号ID",alignment : "center",visible: false},
            {dataField : "loanAmount",caption : "放款金额",alignment : "center",allowFiltering:false,allowSorting:false},
            {dataField : "repayAmount",caption : "还款金额",alignment : "center",allowFiltering:false,allowSorting:false}
        ],
        "催收信息"+new Date(),
        function(e){
            var dataGrid = e.component;
            var toolbarOptions = e.toolbarOptions.items;
            toolbarOptions.push({
                location: "before",
                widget: "dxButton",
                options: {
                    hint : "查看",
                    text : "查看",
                    visible : !disableButton("ym-de",0),
                    icon : "find",
                    onClick : function() {
                        var selectData = dataGrid.getSelectedRowsData();
                        if(selectData.length == 0){
                            alert("请选择需要查看的催收信息");
                            return;
                        }
                        if(selectData.length > 1){
                            alert("一次只能操作一条数据");
                            return;
                        }
                        var customerId = selectData[0].customerId;
                        var contractKey = selectData[0].contractKey;
                        var contractId = selectData[0].contractId;
                        console.info(customerId+"=="+contractKey+"=="+contractId);
                        layer_alert(customerId,contractKey,contractId);
                    }
                }
            },
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint: "刷新",
                        text: "刷新",
                        visible : !disableButton("ym-de",1),
                        icon: "refresh",
                        onClick: function () {
                            tableUtils.refresh("collectionInfoTable");
                        }
                    }
                })
        }
    );
};
var collectionInfo = function () {
    $('.modal-backdrop').hide();
    checkPageEnabled("ym-de");
    initCollectionInfoTable(collectors);
};