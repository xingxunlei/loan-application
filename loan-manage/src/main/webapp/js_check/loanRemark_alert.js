var borrId_root;
$(function() {
    getpath();
    loadtable_lr();

});

var getpath = function() {
    try {
        var url = location.href;
        var data_url = url.split("?")[1];
        $.ajax({
            type : "post",
            url : "/zloan-manage/url/url_open.action",
            data : {
                var_text_locked : data_url
            },
            success : function(msg) {
                data_url = msg;
                var info = data_url.split("=")[1];
                var himid = info.split("_")[0];
                borrId_root = info.split("_")[1];

            }
        });
    } catch (e) {
        // window.location = "/jhhoa/login.html";
    }
}

var loadtable_lr = function() {
    DevExpress.config({
        forceIsoDateParsing: true,
    });

    var memos = new DevExpress.data.CustomStore({
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
            args.borrId = borrId_root;
            $.ajax({
                url: "/zloan-manage/memo.action",
                data: args,
                success: function(result) {
                    result = JSON.parse(result);
                    deferred.resolve(result.list, { totalCount: result.total });
                },
                error: function() {
                    deferred.reject("Data Loading Error");
                },
                timeout: 50000
            });

            return deferred.promise();
        }
    });


    $("#userTable").dxDataGrid({
        dataSource : {
            store: memos
        },
        dateSerializationFormat:"yyyy-MM-dd HH:mm:ss",
        remoteOperations: {
            sorting: true,
            paging: true,
            filtering:true
        },
        rowAlternationEnabled : true,
        showRowLines : true,
        selection : {
            mode : "multiple"
        },
        allowColumnReordering : true,
        allowColumnResizing : true,
        columnAutoWidth : true,
        columnFixing : {
            enabled : true
        },
        paging : {
            pageSize : 15,
        },
        pager : {
            showPageSizeSelector : true,
            allowedPageSizes : [10, 15, 30, 45, 60 ],
            showInfo : true,
            infoText : '第{0}页 . 共{1}页'
        },
        columns : [{
            dataField : "type",
            caption : "	标题",
            alignment : "center",
            allowSorting:false,
            calculateCellValue: function (data) {
                if(data.type){
                    if (data.type=='A'){
                        return "催收备注"
                    }else if (data.type=='B'){
                        return "提醒备注"
                    }else{
                        return data.type;
                    }
                }
            }
        }, {
            dataField : "remark",
            caption : "	内容",
            alignment : "center",
            allowSorting:false,
        }, {
            dataField : "createUser",
            caption : "	催收人",
            alignment : "center",
            allowSorting:false,
        }, {
            dataField : "createDate",
            caption : "	时间",
            alignment : "center",
            dataType: 'date',
            format: function (date) {
                var month = date.getMonth() + 1,
                    day = date.getDate(),
                    year = date.getFullYear(),
                    hours = date.getHours(),
                    minutes = date.getMinutes(),
                    seconds = date.getSeconds();
                if(hours < 10){
                    hours = "0" + hours;
                }
                if(minutes < 10){
                    minutes = "0" + minutes;
                }
                if(seconds < 10){
                    seconds = "0" + seconds;
                }

                return year + "/" + month + "/" + day + " " + hours + ":" + minutes + ":" + seconds;
            }
        }]
    });
}





