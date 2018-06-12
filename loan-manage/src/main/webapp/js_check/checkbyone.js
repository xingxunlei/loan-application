var checkbyone = function () {
    setToolbar_za();
    loadtable2_za();

    window_find_za();
    window_ok_za();
    window_no_za();

}

var setToolbar_za = function () {
    checkPageEnabled("ym-za");

    $("#tool_find").dxButton({
        hint: "查看详情",
        text: "详情",
        icon: "find",
        disabled: true,
        onClick: function () {
            var dataGrid = $('#userTable').dxDataGrid('instance');
            var selectobj = dataGrid.getSelectedRowsData();
            var himid = selectobj[0].per_id;
            var brroid = selectobj[0].id;
            loadwindow_userinfo(himid, brroid);
        }
    });
    $("#tool_ok").dxButton({
        hint: "放款",
        text: "放款",
        icon: "todo",
        disabled: true,
        onClick: function () {
            var dataGrid = $('#userTable').dxDataGrid('instance');
            var selectobj_param = dataGrid.getSelectedRowsData();
            $("#window_ok").dxPopup({
                visible: true,
            });
            box_ok_value_za(selectobj_param[0]);
        }
    });

    $("#tool_no").dxButton({
        hint: "拒绝",
        text: "拒绝",
        icon: "close",
        disabled: true,
        onClick: function () {

            var dataGrid = $('#userTable').dxDataGrid('instance');
            var selectobj_param = dataGrid.getSelectedRowsData();
            if (selectobj_param.length > 1) {
                showMessage("只能选择一条数据！");
            } else {
                $("#window_no").dxPopup({
                    visible: true,
                });
                box_no_value_za(selectobj_param[0]);
            }
        }
    });

};

var loadtable2_za = function () {
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
            $.ajax({
                url: 'user/getauditsforUser.action?employ_num=' + usernum,
                data: args,
                success: function (result) {
                    result = JSON.parse(result);
                    deferred.resolve(result.list, {totalCount: result.total});
                },
                error: function () {
                    deferred.reject("Data Loading Error");
                },
                timeout: 50000
            });

            return deferred.promise();
        }
    });

    $("#userTable")
        .dxDataGrid(
            {
                dataSource: {
                    store: orders
                },
                dateSerializationFormat: "yyyy-MM-dd HH:mm:ss",
                remoteOperations: {
                    sorting: true,
                    paging: true,
                    filtering: true
                },
                filterRow: {
                    visible: true,
                    applyFilter: "auto"
                },
                rowAlternationEnabled: true,
                showRowLines: true,
                selection: {
                    mode: "multiple"
                },
                allowColumnReordering: true,
                allowColumnResizing: true,
                columnAutoWidth: true,
                columnChooser: {
                    title: "列选择器",
                    enabled: true,
                    emptyPanelText: '把你想隐藏的列拖拽到这里...'
                },
                columnFixing: {
                    enabled: true
                },
                paging: {
                    pageSize: 15,
                },
                pager: {
                    showPageSizeSelector: true,
                    allowedPageSizes: [10, 15, 30, 45, 60],
                    showInfo: true,
                    infoText: '第{0}页 . 共{1}页'
                },
                onSelectionChanged: function (data) {
                    var flag = false;
                    for (var i = 0; i < data.selectedRowsData.length; i++) {
                        if (data.selectedRowsData[i].borr_status != "BS003"
                            && data.selectedRowsData[i].borr_status != "BS012") {
                            flag = true;
                            break;
                        }
                    }

                    $("#tool_find")
                        .dxButton(
                            {
                                disabled: (data.selectedRowsData.length != 1)
                                || disableButton("ym-za",0),
                            });
                    $("#tool_ok")
                        .dxButton(
                            {
                                disabled: data.selectedRowsData.length != 1
                                || flag
                                || disableButton("ym-za",1),
                            });
                    $("#tool_no")
                        .dxButton(
                            {
                                disabled: data.selectedRowsData.length != 1
                                || flag
                                || disableButton("ym-za",2),
                            });
                },
                columns: [{
                    dataField: "borr_num",
                    caption: "合同编号",
                    alignment: "center",
                }, {
                    dataField: "name",
                    caption: "姓名",
                    alignment: "center",
                }, {
                    dataField: "card_num",
                    caption: "身份证号",
                    alignment: "center",
                }, {
                    dataField: "phone",
                    caption: "手机号码",
                    alignment: "center",
                },
                {
                    dataField: "product_name",
                    caption: "产品类型",
                    alignment: "center",
                    lookup: {
                        dataSource: [
                            {value: '500元-7天', format: '500元-7天'},
                            {value: '500元-14天', format: '500元-14天'},
                            {value: '1000元-7天', format: '1000元-7天'},
                            {value: '1000元-14天', format: '1000元-14天'},
                            {value: '1500元-7天', format: '1500元-7天'},
                            {value: '1500元-14天', format: '1500元-14天'},
                            {value: '2000元-7天', format: '2000元-7天'},
                            {value: '2000元-14天', format: '2000元-14天'},
                        ], displayExpr: 'format'
                    }
                }, {
                    dataField: "maximum_amount",
                    caption: "贷款金额",
                    alignment: "center",
                    dataType: "number"
                }, {
                    dataField: "bank_name",
                    caption: "银行名称",
                    alignment: "center",
                }, {
                    dataField: "bank_num",
                    caption: "银行卡号",
                    alignment: "center",
                }, {
                    dataField: "meaning",
                    caption: "贷款状态",
                    alignment: "center",
                    lookup: {
                        dataSource: [
                            {value: 'BS003', format: '已签约'},
                            {value: 'BS004', format: '待还款'},
                            {value: 'BS009', format: '电审未通过'},
                            {value: 'BS011', format: '放款中'},
                            {value: 'BS012', format: '放款失败'},
                            { value: 'BS007', format: '已取消' }
                        ], displayExpr: 'format'
                    }
                },
                {
                    dataField: "up_status",
                    caption: "上单状态",
                    alignment: "center",
                    calculateCellValue: function (data) {
                        if (data.up_status) {
                            if (data.up_status == 'BS001') {
                                return "申请中";
                            } else if (data.up_status == 'BS002') {
                                return "待签约"
                            } else if (data.up_status == 'BS003') {
                                return "已签约"
                            } else if (data.up_status == 'BS004') {
                                return "待还款"
                            } else if (data.up_status == 'BS005') {
                                return "逾期未还"
                            } else if (data.up_status == 'BS006') {
                                return "正常结清"
                            } else if (data.up_status == 'BS007') {
                                return "已取消"
                            } else if (data.up_status == 'BS008') {
                                return "审核未通过"
                            } else if (data.up_status == 'BS009') {
                                return "电审未通过"
                            } else if (data.up_status == 'BS010') {
                                return "逾期结清"
                            } else if (data.up_status == 'BS011') {
                                return "放款中"
                            } else if (data.up_status == 'BS012') {
                                return "放款失败"
                            }
                            else {
                                return "";
                            }
                        }
                    },
                    lookup: {
                        dataSource: [
                            {value: 'BS001', format: '申请中'},
                            {value: 'BS002', format: '待签约'},
                            {value: 'BS003', format: '已签约'},
                            {value: 'BS004', format: '待还款'},
                            {value: 'BS005', format: '逾期未还'},
                            {value: 'BS006', format: '正常结清'},
                            {value: 'BS008', format: '审核未通过'},
                            {value: 'BS009', format: '电审未通过'},
                            {value: 'BS010', format: '逾期结清'},
                            {value: 'BS011', format: '放款中'},
                            {value: 'BS012', format: '放款失败'},
                            {value: 'BS007', format: '已取消'}

                        ], displayExpr: 'format'
                    }
                }
                , {
                        dataField : "state",
                        caption : "自动电呼",
                        alignment : "center",
                        calculateCellValue: function (data) {
                            if(data.state == null){
                                return "未拨打";
                            }else{
                                if(data.state == 1){
                                    return "拨打中";
                                }else if (data.state == 2){
                                    return "通过";
                                }else if (data.state == 3){
                                    return "拒绝";
                                }else if (data.state == 4){
                                    return "未接通";
                                }else if (data.state == 5){
                                    return "非本人";
                                }
                            }
                        },
                        lookup: { dataSource: [
                            { value: '8888', format: '未拨打' },
                            { value: '1', format: '拨打中' },
                            { value: '2', format: '通过' },
                            { value: '3', format: '拒绝' },
                            { value: '4', format: '未接通' },
                            { value: '5', format: '非本人' },
                            { value: '9999', format: '未知' },
                        ],  displayExpr: 'format' }
                    },{
                    dataField: "reason",
                    caption: "拒绝理由",
                    alignment: "center",
                },
                {
                    dataField: "isManual",
                    caption: "是否人工审核",
                    alignment: "center",
                    calculateCellValue: function (data) {
                        if (data.isManual != null) {
                            return "是";
                        } else {
                            return "否";
                        }
                    },
                    lookup: {
                        dataSource: [
                            {value: '1', format: '是'},
                            {value: '2', format: '否'},
                        ], displayExpr: 'format'
                    }
                }, {
                    dataField: "description",
                    caption: "认证说明",
                    alignment: "center",
                },
                {
                    dataField: "makeborr_date",
                    caption: "签约时间",
                    alignment: "center",
                    dataType: "date",
                    filterOperations:["=","between"]
                }]
            });
}

// 查看详情
var window_find_za = function () {
    $("#window_find").dxPopup({
        showTitle: true,
        title: '详情',
        width: "95%",
        height: "88%",
        visible: false,
        WindowScroll: true,
        resizeEnabled: true,
        onHiding: function () {
            loadtable2_za();
        },
    });
}
var window_ok_za = function () {
    $("#window_ok").dxPopup({
        showTitle: true,
        maxWidth: 400,
        maxHeight: 220,
        title: '放款',
        visible: false,
        WindowScroll: true,
        resizeEnabled: true,
        onHiding: function () {
            setToolbar_za();
            loadtable2_za();
        },
    });
}

var window_no_za = function () {
    $("#window_no").dxPopup({
        showTitle: true,
        maxWidth: 500,
        maxHeight: 300,
        title: '拒绝',
        visible: false,
        WindowScroll: true,
        resizeEnabled: true,
        onHiding: function () {
            setToolbar_za();
            loadtable2_za();
        },
    });
}

var box_ok_value_za = function (selectobj) {
    $("#pro_check_name").html(selectobj.product_name);
    $("#per_check_name").html(selectobj.name);
    // 增加保存按钮
    $("#submit_ok").dxButton(
        {
            text: "确定",
            hint: "确认发送",
            icon: "todo",
            // height: 35,
            // width: 70,
            disabled: false,
            onClick: function () {
                var reason = "";
                var status = "y";
                var conmitdata = {
                    brroid: selectobj.id,
                    status: status,
                    employ_num: usernum,
                    reason: reason,
                };
                $.ajax({
                    type: "POST",
                    url: "manager/personCheckMessage.action",
                    data: conmitdata,
                    success: function (msg) {
                        if (msg.code >= 0) {
                            var arrSimple = new Array(selectobj.per_id,
                                selectobj.id);
                            arrSimple.sort();
                            var md5sign = arrSimple[0] + "" + arrSimple[1]
                                + "" + signkey;
                            md5sign = hex_md5(md5sign);
                            $.ajax({
                                type: "get",
                                url: "callback/payCont.action",
                                data: {
                                    per_id: selectobj.per_id,
                                    borrId: selectobj.id,
                                    md5sign: md5sign,
                                },
                                success: function (msg) {
                                    // alert(msg);
                                    var jsonobj = eval("(" + msg + ")");
                                    if (jsonobj.code == 200) {
                                        $("#window_ok").dxPopup({
                                            visible: false,
                                        });
                                        message_alert("提示:", "通过电审，已放款！");
                                        setToolbar_za();
                                        loadtable2_za();
                                    } else {
                                        showMessage(jsonobj.info + "！");
                                    }
                                }
                            });
                        } else {
                            showMessage("操作失败！");
                        }
                    }
                });
            }
        });

};

var box_no_value_za = function (selectobj) {
    var reason = "";
    var status = "n";

    $("#reason").dxTextArea({
        placeholder: "必填",
        height: 100,
        value: reason,
        showClearButton: false,
        onValueChanged: function (data) {
            reason = data.value;
        }
    });

    // 增加保存按钮
    $("#submit_no")
        .dxButton(
            {
                text: "确定",
                hint: "确认发送",
                icon: "todo",
                // height: 35,
                // width: 70,
                disabled: false,
                onClick: function () {
                    if (reason == '') {
                        showMessage("必须填写拒绝理由！");
                        return;
                    }
                    var conmitdata = {
                        brroid: selectobj.id,
                        status: status,
                        employ_num: usernum,
                        reason: reason,
                    };
                    $
                        .ajax({
                            type: "POST",
                            url: "manager/personCheckMessage.action",
                            data: conmitdata,
                            success: function (msg) {
                                if (msg.code >= 0) {
                                    $
                                        .ajax({
                                            type: "POST",
                                            url: "manager/UpdateBorrowList.action",
                                            data: {
                                                ispay: 0,
                                                status: "BS009",
                                                borrid: selectobj.id,
                                            },
                                            success: function (msg) {
                                                if (msg.code >= 0) {
                                                    message_alert(
                                                        "提示:",
                                                        "操作成功！");
                                                } else {
                                                    showMessage("操作失败！");
                                                }
                                            }
                                        });
                                    $("#window_no").dxPopup({
                                        visible: false,
                                    });

                                } else {
                                    showMessage("操作失败！");
                                }
                            }
                        });
                }
            });
}
