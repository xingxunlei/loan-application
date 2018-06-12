

var initTable_bcl = function(){
    tableUtils.initMuliTableToolBar(
        "userTable",
        "order.action?type=8",
        null,
        [ {
            dataField : "borrNum",
            caption : "合同编号",
            alignment : "center",
            filterOperations:["="]
        },{
            dataField : "serialNo",
            caption : "还款流水号",
            alignment : "center",
            filterOperations:["="]
        }, {
            dataField : "name",
            caption : "姓名",
            alignment : "center",
            filterOperations:["="],
            allowSorting:false
        }, {
            dataField : "idCard",
            caption : "身份证号",
            alignment : "center",
            filterOperations:["="]
        }, {
            dataField : "phone",
            caption : "手机号码",
            alignment : "center",
            filterOperations:["="]
        },{
            dataField : "prodName",
            caption : "产品类型",
            alignment : "center",
            width:125,
            lookup: { dataSource: pruducts,  displayExpr: 'format' }
            /*lookup: { dataSource: [
                { value: '1', format: '500元-7天' },
                { value: '2', format: '500元-14天' },
                { value: '3', format: '1000元-7天' },
                { value: '4', format: '1000元-14天' }
            ],  displayExpr: 'format' }*/
        },{
            dataField : "bedueDays",
            caption : "逾期天数",
            alignment : "center",
            allowFiltering:true,
            filterOperations:["="]
        },{
            dataField : "deductionsType",
            caption : "类型",
            alignment : "center",
            lookup: { dataSource: [
                { value: '1', format: '全额' },
                { value: '2', format: '定额' },
            ],  displayExpr: 'format' } ,
            calculateCellValue: function (data) {
                if(data.deductionsType){
                    if (data.deductionsType=='1'){
                        return "全额"
                    }else if (data.deductionsType=='2'){
                        return "定额"
                    }else{
                        return data.deductionsType;
                    }
                }
            }
        },{
            dataField : "actAmount",
            caption : "金额",
            alignment : "center",
            allowFiltering:false,
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
        }, {
            dataField : "createUser",
            caption : "操作人",
            alignment : "center",
            allowFiltering:true,
            filterOperations:["="],
        }, {
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
        }],
        "还款流水"+new Date(),
        function(e){
            var dataGrid = e.component;
            var toolbarOptions = e.toolbarOptions.items;
            toolbarOptions.push({
                location: "before",
                widget: "dxButton",
                options: {
                    hint: "查看",
                    text: "查看",
                    visible : !disableButton("ym-dg",0),
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
                        var customerId = selectData[0].perId;
                        var contractKey = selectData[0].borrId;
                        var contractId = selectData[0].borrNum;
                        layer_alert(customerId, contractKey, contractId);
                    }
                }
            },
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint: "刷新",
                        text: "刷新",
                        visible : !disableButton("ym-dg",1),
                        icon: "refresh",
                        onClick: function () {
                            tableUtils.refresh("userTable");
                        }
                    }
                })
        });
};

var beatchConllectionList = function(){
    $('.modal-backdrop').hide();
    checkPageEnabled("ym-dg");
    initTable_bcl();
};
