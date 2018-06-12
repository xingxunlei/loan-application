var setToolBar = function(){
    $("#tool_find").dxButton({
        hint : "查看",
        text : "查看",
        icon : "find",
        disabled : false,
        onClick : function() {
            //TODO
        }
    });
};
var initPaymentInfoTable = function () {
    DevExpress.config({
        forceIsoDateParsing: true
    });
    var paymentInfoStore = new DevExpress.data.CustomStore({
        load: function (loadOptions) {

            var deferred = $.Deferred(),
                args = {};
            args.filter = loadOptions.filter ? JSON.stringify(loadOptions.filter) : "";
            args.sort = loadOptions.sort ? JSON.stringify(loadOptions.sort) : "";
            args.requireTotalCount = loadOptions.requireTotalCount;
            if (loadOptions.sort) {
                args.orderby = loadOptions.sort[0].selector;
                if (loadOptions.sort[0].desc)
                    args.orderby += " desc";
            }
            args.skip = loadOptions.skip || 0;
            args.take = loadOptions.take || 15;
            $.ajax({
                url: 'repaymentPlan/queryDebitInfo.action',
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
    $("#paymentInfoTable").dxDataGrid({
        dataSource : {store: paymentInfoStore},
        dateSerializationFormat  : "yyyy-MM-dd",
        remoteOperations: {sorting: true,paging: true,filtering:true},
        filterRow : {visible : true,applyFilter : "auto"},
        rowAlternationEnabled : true,
        showRowLines : true,
        selection : {mode : "multiple"},
        allowColumnReordering : true,
        allowColumnResizing : true,
        columnAutoWidth : true,
        columnChooser : {title : "列选择器",enabled : true,emptyPanelText : '把你想隐藏的列拖拽到这里...'},
        columnFixing : {enabled : true},
        paging : {pageSize : 15},
        pager : {
            showPageSizeSelector : true,
            allowedPageSizes : [10, 15, 30, 45, 60 ],
            showInfo : true,
            infoText : '第{0}页 . 共{1}页'
        },
        onSelectionChanged : function(data) {
            $("#tool_find").dxButton({disabled : data.selectedRowsData.length != 1});
        },
        columns :[
            {dataField : "borrNum",caption : "合同编号",alignment : "center",allowFiltering:true,filterOperations:["="]},
            {dataField : "mobile",caption : "用户",alignment : "center",allowFiltering:false},
            {dataField : "username",caption : "客户姓名",alignment : "center",allowFiltering:true,filterOperations:["="]},
            {dataField : "idCard",caption : "身份证",alignment : "center",allowFiltering:true,filterOperations:["="]},
            {dataField : "bankNum",caption : "银行卡号",alignment : "center",allowFiltering:false},
            {dataField : "phone",caption : "手机号",alignment : "center",allowFiltering:true,filterOperations:["="]},
            {dataField : "amount",caption : "扣款金额",alignment : "center",allowFiltering:false},
            {dataField : "remark",caption : "备注信息",alignment : "center",allowFiltering:false},
            {dataField : "debitType",caption : "扣款方式",alignment : "center",
                calculateCellValue: function (data) {
                    if(data.debitType){
                        if(data.debitType == "1"){
                            return "放款";
                        }else if(data.debitType == "2"){
                            return "还款";
                        }else if(data.debitType == "3"){
                            return "手续费";
                        }else if(data.debitType == "4"){
                            return "代收还款";
                        }else if(data.debitType == "5"){
                            return "服务费";
                        }else if(data.debitType == "6"){
                            return "线下还款";
                        }
                    }
                }
            },
            {dataField : "debitState",caption : "扣款状态",alignment : "center"},
            {dataField : "reason",caption : "失败原因",alignment : "center",allowFiltering:false},
            {dataField : "createBy",caption : "操作人",alignment : "center",allowFiltering:false},
            {dataField : "createTime",caption : "操作时间",alignment : "center",allowFiltering:false}
        ]
    });
};


var paymentInfo = function(){
    $('.modal-backdrop').hide();
    setToolBar();
    initPaymentInfoTable();
};