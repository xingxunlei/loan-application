var checkbyoned = function () {
    setToolbar_zd();
    loadtable2_zd();

    window_find_zd();
};

var setToolbar_zd = function () {
    checkPageEnabled("ym-zd");

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
};

var loadtable2_zd = function () {
    $("#userTable").dxDataGrid(
    {
        dataSource: {
            store: {
                type: "odata",
                url: odataurl
                + "/odata/YM_view_odata_checklist?$filter=employ_num%20eq%20%27"
                + usernum + "%27%20and%20(borr_status%20ne%20%27BS003%27%20and%20borr_status%20ne%20%27BS012%27)",
            }
        },
        searchPanel: {
            visible: true,
            width: 240,
            placeholder: "搜索..."
        },
        "export": {
            enabled: true,
            fileName: "Employees",
            allowExportSelectedData: true
        },
        headerFilter: {
            visible: true
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
            $("#tool_find").dxButton({disabled: (data.selectedRowsData.length != 1) || disableButton("ym-zd",0)});
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
        }, {
            dataField: "product_name",
            caption: "申请类型",
            alignment: "center",
            calculateCellValue: function (rowData) {
                return "消费贷";
            }
        }, {
            dataField: "product_name",
            caption: "产品类型",
            alignment: "center",
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
        }, {
            dataField: "reason",
            caption: "拒绝理由",
            alignment: "center",
        }, {
            dataField: "emplloyee_name",
            caption: "审核人",
            alignment: "center",
        }, {
            dataField: "makeborr_date",
            caption: "签约时间",
            alignment: "center",
            dataType: "date"
        }]
    });
};

// 查看详情
var window_find_zd = function () {
    $("#window_find").dxPopup({
        showTitle: true,
        title: '详情',
        width: "95%",
        height: "88%",
        visible: false,
        WindowScroll: true,
        resizeEnabled: true,
        onHiding: function () {
            loadtable2_zd();
        }
    });
};
