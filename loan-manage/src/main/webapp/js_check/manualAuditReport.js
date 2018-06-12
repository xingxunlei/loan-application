var manualAuditReport = function () {
    $('.modal-backdrop').hide();
    checkPageEnabled("ym-ze");
    loadRiewer();
    initTable_mar();
    initToolbar_mar();

};

var initToolbar_mar = function () {
    $("#tool_find").dxButton({
        hint: "查询",
        text: "查询",
        visible : !disableButton("ym-ze",0),
        icon: "find",
        onClick: function () {
            $("#userTable tbody").html("")
            initTable_mar();
        }
    });
};

var initTable_mar = function () {
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
            args.employName = $("#employName").val();
            args.beginDate = $("#datetimeStart").val();
            args.endDate = $("#datetimeEnd").val();
            $.ajax({
                url: "/zloan-manage/user/manualAuditReport.action",
                data: args,
                type:"post",
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
            dataField: "employName",
            caption: "审核人员",
            alignment: "center"
        }, {
            dataField: "cancelNum",
            caption: "已取消",
            alignment: "center"
        }, {
            dataField: "passNum",
            caption: "通过",
            alignment: "center"
        }, {
            dataField: "rejectNum",
            caption: "拒绝",
            alignment: "center"
        }, {
            dataField: "sumNum",
            caption: "合计",
            alignment: "center"
        },{
            dataField: "passRate",
            caption: "通过率",
            alignment: "center",
            calculateCellValue: function (data) {
                return data.passRate + "%";
            }
        }]
    });
};

function loadRiewer(){
    $.ajax({
        url: "/zloan-manage/user/riewer.action",
        type:"get",
        success: function (result) {
            if(result){
                result = JSON.parse(result);
                var option = "<option value='' default>全部</option>"
                for (var i = 0 ; i< result.length; i++){
                     option += "<option value='" + result[i].employNum + "'>" + result[i].emplloyeeName + "</option>"
                }
                $("#employName").html(option);
            }
        },
        error: function () {
            deferred.reject("Data Loading Error");
        },
        timeout: 50000
    });
}