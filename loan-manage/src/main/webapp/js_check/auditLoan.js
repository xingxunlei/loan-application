var auditLoan = function () {
    $('.modal-backdrop').hide();
    checkPageEnabled("ym-zf");
    initTable_zf();
    setToolbar_zf();

};

var setToolbar_zf = function () {

    $("#tool_find").dxButton({
        hint: "查询",
        text: "查询",
        visible : !disableButton("ym-zf",0),
        icon: "find",
        onClick: function () {
            initTable_zf();

        }
    });

    $("#tool_refresh").dxButton({
        hint: "刷新",
        text: "刷新",
        visible : !disableButton("ym-zf",1),
        icon: "refresh",
        onClick: function () {
            tableUtils.refresh("userTable");
        }
    });
};

//导出函数
var exportData = function (url, params) {
    var form = $("<form>");//定义一个form表单
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", url);
    $("body").append(form);//将表单放置在web中
    var input1 = $("<input>");
    if (params != null && params != '') {
        var par = params.data;
        for (var p in par) {
            var input1 = $("<input>");
            input1.attr("type", "hidden");
            input1.attr("value", par[p]);
            if (p.indexOf(".") > 0) {
                p = p.split(".")[1];
            }
            input1.attr("name", p);
            form.append(input1);
        }
    }
    form.submit();//表单提交
}

var initTable_zf = function () {
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
            args.take = 500;
            args.employId = $("#auditName").val();
            if ($("#datetimeStart").val() =='' || $("#datetimeStart").val() == null){
                var firstDate = new Date();
                firstDate.setDate(1); //第一天
                $("#datetimeStart").val(firstDate.getFullYear()+"-"+(firstDate.getMonth()+1)+"-"+firstDate.getDate());
            }
            args.beginDate = $("#datetimeStart").val();
            args.endDate = $("#datetimeEnd").val();
            $.ajax({
                url: "/zloan-manage/user/auditLoan.action",
                data: args,
                type:"post",
                success: function (result) {
                    result = JSON.parse(result);
                    if ($("#auditName")[0].options.length<2) {
                        $.each(result.list, function (key, val) {
                            $("#auditName").append('<option value="' + val.id + '">' + val.emplloyeeName + '</option>');
                        });
                        $("#auditName option:last").remove();
                    }
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

    $("#userTable").dxDataGrid({
        dataSource: {
            store: orders
        },
        dateSerializationFormat: "yyyy-MM-dd HH:mm:ss",
        remoteOperations: {
            sorting: true,
            paging: true,
            filtering: true
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
            pageSize: 500
        },
        pager: {
            showPageSizeSelector: true,
            allowedPageSizes: [500],
            showInfo: true,
            infoText: '第{0}页 . 共{1}页'
        },
        columns: [{
            dataField: "emplloyeeName",
            caption: "审核人员",
            alignment: "center",
            allowFiltering:false,
            allowSorting:false

        }, {
            dataField: "loanLimit",
            caption: "	放款笔数",
            alignment: "center",
            dataType: "number"
        }, {
            dataField: "borrAmount",
            caption: "	放款金额",
            alignment: "center",
            dataType: "number"
        }, {
            dataField: "monthQuota",
            caption: "	合同金额",
            alignment: "center",
            dataType: "number"
        }]
    });
};