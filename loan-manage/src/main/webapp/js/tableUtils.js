var tableUtils = {
    initSingle: function (tableId, url, onSelectionChanged, columns, pageSize) {
        DevExpress.config({
            forceIsoDateParsing: true
        });
        var store = new DevExpress.data.CustomStore({
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
                args.take = loadOptions.take || pageSize;
                $.ajax({
                    url: url,
                    data: args,
                    success: function (result) {
                        if (result.code == 1) {
                            var obj = result.object;
                            var list = obj.list;
                            var total = obj.total;
                            deferred.resolve(list, {totalCount: total});
                        }
                    },
                    error: function () {
                        deferred.reject("Data Loading Error");
                    },
                    timeout: 50000
                });

                return deferred.promise();
            }
        });
        $("#" + tableId).dxDataGrid({
            dataSource: {store: store},
            dateSerializationFormat: "yyyy-MM-dd",
            remoteOperations: {sorting: true, paging: true, filtering: true},
            filterRow: {visible: true, applyFilter: "auto"},
            rowAlternationEnabled: true,
            selection: {mode: "single"},
            allowColumnReordering: true,
            allowColumnResizing: true,
            columnAutoWidth: true,
            columnFixing: {enabled: true},
            paging: {pageSize: pageSize},
            pager: {
                showPageSizeSelector: true,
                allowedPageSizes: [10, 20, 30, 40],
                showInfo: true,
                infoText: '第{0}页 . 共{1}页'
            },
            export: {enabled: false},
            onSelectionChanged: onSelectionChanged,
            columns: columns,
        });
    },
    initMuliTable: function (tableId, url, onSelectionChanged, columns, fileName) {
        DevExpress.config({
            forceIsoDateParsing: true
        });
        var store = new DevExpress.data.CustomStore({
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
                    url: url,
                    data: args,
                    type: 'POST',
                    dataType: "json",
                    success: function (result) {
                        if (result.code == 1) {
                            var obj = result.object;
                            var list = obj.list;
                            var total = obj.total;
                            deferred.resolve(list, {totalCount: total});
                        }
                    },
                    error: function () {
                        deferred.reject("Data Loading Error");
                    },
                    timeout: 50000
                });

                return deferred.promise();
            }
        });
        $("#" + tableId).dxDataGrid({
            dataSource: {store: store},
            dateSerializationFormat: "yyyy-MM-dd",
            remoteOperations: {sorting: true, paging: true, filtering: true},
            filterRow: {visible: true, applyFilter: "auto"},
            rowAlternationEnabled: true,
            selection: {mode: "multiple", allowSelectAll: true, selectAllMode: 'page'},
            allowColumnReordering: true,
            allowColumnResizing: true,
            columnAutoWidth: true,
            columnFixing: {enabled: true},
            paging: {pageSize: 15},
            pager: {
                showPageSizeSelector: true,
                allowedPageSizes: [15, 30, 45, 60],
                showInfo: true,
                infoText: '第{0}页 . 共{1}页'
            },
            export: {enabled: true, allowExportSelectedData: true, excelFilterEnabled: true, fileName: fileName},
            onSelectionChanged: onSelectionChanged,
            columns: columns
        });
    },
    initMuliTableToolBar: function (tableId, url, onSelectionChanged, columns, fileName, toolBar) {
        DevExpress.config({
            forceIsoDateParsing: true
        });
        var store = new DevExpress.data.CustomStore({
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
                args.take = loadOptions.take || 10;
                $.ajax({
                    url: url,
                    data: args,
                    type: 'POST',
                    dataType: "json",
                    success: function (result) {
                        if (result.code == 1) {
                            var obj = result.object;
                            var list = obj.list;
                            var total = obj.total;
                            deferred.resolve(list, {totalCount: total});
                        }
                    },
                    error: function () {
                        deferred.reject("Data Loading Error");
                    },
                    timeout: 50000
                });
                return deferred.promise();
            }
        });
        $("#" + tableId).dxDataGrid({
            dataSource: {store: store},
            remoteOperations: {sorting: true, paging: true, filtering: true},
            filterRow: {visible: true, applyFilter: "auto"},
            rowAlternationEnabled: true,
            selection: {mode: "multiple", allowSelectAll: true, selectAllMode: 'page'},
            allowColumnReordering: true,
            allowColumnResizing: true,
            columnAutoWidth: true,
            columnFixing: {enabled: true},
            paging: {pageSize: 10},
            columnFixing: {enabled: true},
            cellHintEnabled: true,
            pager: {
                showPageSizeSelector: true,
                allowedPageSizes: [10, 20, 50, 100, 200, 500, 1000],
                showNavigationButtons: true,
                showInfo: true,
                infoText: '第{0}页 . 共{1}页.共 {2} 条'
            },
            onToolbarPreparing: function (e) {
                toolBar(e);
            },
            onContentReady: function (e) {
                var dataGrid = e.component;
                dataGrid.clearSelection();
            },
            onSelectionChanged: onSelectionChanged,
            columns: columns
        });
    },
    initSinglePages: function (tableId, url, onSelectionChanged, columns, pageSize) {
        DevExpress.config({
            forceIsoDateParsing: true
        });
        var store = new DevExpress.data.CustomStore({
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
                args.take = loadOptions.take || pageSize;
                $.ajax({
                    url: url,
                    data: args,
                    success: function (result) {
                        if (result.code == 1) {
                            var obj = result.object;
                            var list = obj.list;
                            var total = obj.total;
                            deferred.resolve(list, {totalCount: total});
                        }
                    },
                    error: function () {
                        deferred.reject("Data Loading Error");
                    },
                    timeout: 50000
                });

                return deferred.promise();
            }
        });
        $("#" + tableId).dxDataGrid({
            dataSource: {store: store},
            dateSerializationFormat: "yyyy-MM-dd",
            remoteOperations: {sorting: true, paging: true, filtering: true},
            filterRow: {visible: true, applyFilter: "auto"},
            rowAlternationEnabled: true,
            showRowLines : true,
            selection: {mode: "single"},
            allowColumnReordering: true,
            allowColumnResizing: true,
            columnAutoWidth: true,
            columnFixing: {enabled: true},
            paging: {pageSize: pageSize},
            pager: {
                showPageSizeSelector: true,
                allowedPageSizes: [15, 30, 45, 60],
                showInfo: true,
                infoText: '第{0}页 . 共{1}页'
            },
            export: {enabled: false},
            onSelectionChanged: onSelectionChanged,
            columns: columns
        });
    },
    loadTableSelectRows: function (tableId) {
        var dataGrid = $('#' + tableId).dxDataGrid('instance');
        var selectobj = dataGrid.getSelectedRowsData();
        return selectobj;
    },
    loadTableFilter: function (tableId) {
        var dataGrid = $('#' + tableId).dxDataGrid('instance');
        return dataGrid.getCombinedFilter();
    },
    refresh: function (tableId) {
        var dataGrid = $('#' + tableId).dxDataGrid('instance');
        dataGrid.refresh();
    },
    clearSelection: function (tableId) {
        var dataGrid = $('#' + tableId).dxDataGrid('instance');
        dataGrid.clearSelection();
    },
    formatDate:function(date){
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
    }
};