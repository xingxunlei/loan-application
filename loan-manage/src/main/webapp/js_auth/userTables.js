var allRoleInfo = [];
var selfRoleInfo = [];
function userTablesInit() {
    checkPageEnabled("ym-gb");
    loadUserRoleInfo();
    initBtnClick();
    initCompanys();
}

var loadUserRoleInfo = function () {
    var category = $.cookie("category");
    $.ajax({
        url: "/zloan-manage/user/userRoleInfo.action",
        data: {userCategory: category},
        type: 'POST',
        dataType: "json",
        async: false,
        success: function (data) {
            if (data.code == 0) {
                setMessage(data.message);
            } else {
                selfRoleInfo = data.object.selfRoles;
                allRoleInfo = data.object.allRoles;
                $("#levelType").empty();
                for(var i=0;i<selfRoleInfo.length;i++){
                    $("#levelType").append("<option value='"+selfRoleInfo[i].type+"'>"+selfRoleInfo[i].desc+"</option>");
                }

                $("#levelType").change(function(){
                    select();
                });
                select();
            }
        },
        error: function () {
            console.info("数据加载错误");
        },
        timeout: 50000
    });
};

function select(showAlert) {
    var index = $('option:selected', '#levelType').index();
    if(index ==-1) {
        if (showAlert) {
            alert("您没有权限修改该用户信息！");
        }
        return false;
    }
    var roles = selfRoleInfo[index].roles;
    $("#isManage").empty();
    for (var j = 0; j < roles.length; j++) {
        $("#isManage").append("<option value='" + roles[j].level + "'>" + roles[j].desc + "</option>");
    }
    return true;
}
function getManageLevel(type, level){
    for(var i=0;i<allRoleInfo.length;i++){
        if(allRoleInfo[i].type == type){
            var roles = allRoleInfo[i].roles;
            for(var j=0;j<roles.length;j++){
                var role = roles[j];
                if(role.level == level)
                    return role.desc;
            }
        }
    }
    return "";
}

function initCompanys() {

    $.ajax({
        url: "auth/loanCompanys.action",
        type: 'POST',
        dataType: "json",
        success: function (result) {
            if(result.code == 1){
                var data = result.object;
                var companys = [];
                for(var i=0;i<data.length;i++){
                    var m = data[i];
                    var obj = {value:m.id,format:m.name};
                    companys.push(obj);
                }
                initSystemUserTables(companys);
            }
        },
        error: function () {
            $("#btnEditUserInfo").removeAttr("disabled");
            alert("网络连接失败");
        },
        timeout: 50000
    });
}
function initBtnClick() {
    $("#btnEditUserInfo").click(function(){
        var userName = $("#userName").val();
        var phone = $("#phone").val();

        if(userName == null){
            alert("用户姓名必须为空");
            return;
        }
        if(phone == null){
            alert("用户电话必须为空");
            return;
        }
        if(phone.length != 11){
            alert("电话号码位数不正确");
            return;
        }
        $("#btnEditUserInfo").attr("disabled",true);
        var formData = $("#edituser-form").serialize();
        $.ajax({
            url: "auth/editCollectorsLevel.action",
            type: 'POST',
            data:formData,
            dataType: "json",
            success: function (result) {
                $("#editSystemUserInfo").modal("hide");
                $("#btnEditUserInfo").removeAttr("disabled");
                if (result.code == 1) {
                    tableUtils.refresh("systemUserTables");
                }else{
                    alert("用户操作失败");
                }
            },
            error: function () {
                $("#btnEditUserInfo").removeAttr("disabled");
                alert("网络连接失败");
            },
            timeout: 50000
        });
    });
}

function initSystemUserTables(companys) {
    tableUtils.initMuliTableToolBar(
        "systemUserTables",
        "auth/loadCollectorsLevels.action?userNo="+usernum,
        null,
        [
            {dataField: "userSysno", caption: "账号/登录名", alignment: "center", allowFiltering: true},
            {dataField: "userName", caption: "姓名", alignment: "center", allowFiltering: true},
            {dataField: "phone", caption: "电话号码", alignment: "center", allowFiltering: false},
            {dataField: "userGroupId", caption: "公司名称", alignment: "center", allowFiltering: true,
                lookup: {
                    dataSource: companys,
                    valueExpr: 'value',
                    displayExpr: 'format'
                }
            },
            {dataField: "status",caption: "状态",alignment: "center",allowFiltering: false,calculateCellValue: function (data) {
                    if (data.status == 'A') {
                        return "启用";
                    } else {
                        return "禁用";
                    }
                }
            },
            {dataField: "levelType",caption: "账户类型",alignment: "center",allowFiltering: true,
                lookup: {
                    dataSource: [
                        { value: '1', format: '公司催收' },
                        { value: '2', format: '外包催收' }
                    ],
                    valueExpr: 'value',
                    displayExpr: 'format'
                }
            },
            {dataField: "isManage",caption: "管理级别",alignment: "center",allowFiltering: false,calculateCellValue: function (data) {
                    return getManageLevel(data.levelType, data.isManage);
                }
            },
            {dataField: "createDate",caption: "创建时间",alignment: "center",allowFiltering: false,dataType: 'date',format: function (date) {
                    return tableUtils.formatDate(date);
                }
            }
        ],
        "系统用户",
        function (e) {
            var dataGrid = e.component;
            var toolbarOptions = e.toolbarOptions.items;
            toolbarOptions.push(
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint: "新增",
                        text: "新增",
                        visible : !disableButton("ym-gb",0),
                        icon: "check",
                        onClick: function () {
                            $("#edituser-form").resetForm();
                            $("#userId").val("");
                            $("#btnEditUserInfo").removeAttr("disabled");
                            $.ajax({
                                url: "auth/loanCompanys.action?userNo="+usernum,
                                type: 'POST',
                                dataType: "json",
                                success: function (result) {
                                    if (result.code == 1) {
                                        var obj = result.object;
                                        var html = "";
                                        for(var i=0;i<obj.length;i++){
                                            html += "<option value='"+obj[i].id+"'>"+obj[i].name+"</option>";
                                        }
                                        $("#userGroupId").html(html);
                                    }
                                },
                                error: function () {
                                    alert("数据加载失败");
                                },
                                timeout: 50000
                            });

                            $.ajax({
                                url: "auth/loadNewUsersysno.action",
                                type: 'POST',
                                dataType: "json",
                                success: function (result) {
                                    if (result.code == 1) {
                                        var obj = result.object;
                                        $("#userSysno").val(obj);
                                    }
                                },
                                error: function () {
                                    alert("数据加载失败");
                                },
                                timeout: 50000
                            });
                            select();
                            $("#editSystemUserInfoTitle").html("添加人员<span style='color: red'>(风控部门操作时人员类型选择公司催收)</span>");
                            $("#editSystemUserInfo").modal({show: true, backdrop: 'static', keyboard: false});
                        }
                    }
                },
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint: "修改",
                        text: "修改",
                        visible : !disableButton("ym-gb",1),
                        icon: "check",
                        onClick: function () {
                            var selectRows = dataGrid.getSelectedRowsData();
                            if(selectRows.length != 1){
                                alert("每次仅能操作一条数据");
                                return;
                            }
                            $("#edituser-form").resetForm();
                            $("#btnEditUserInfo").removeAttr("disabled");
                            $.ajax({
                                url: "auth/loanCompanys.action?userNo="+usernum,
                                type: 'POST',
                                dataType: "json",
                                success: function (result) {
                                    if (result.code == 1) {
                                        var obj = result.object;
                                        var html = "";
                                        for(var i=0;i<obj.length;i++){
                                            html += "<option value='"+obj[i].id+"'>"+obj[i].name+"</option>";
                                        }
                                        $("#userGroupId").html(html);
                                        $("#userGroupId").val(data.userGroupId);
                                    }
                                },
                                error: function () {
                                    alert("数据加载失败");
                                },
                                timeout: 50000
                            });
                            $("#editSystemUserInfoTitle").html("修改人员<span style='color: red'>(风控部门操作时人员类型选择公司催收)</span>");
                            var data = selectRows[0];
                            console.info(data.id);
                            $("#userId").val(data.id);
                            $("#userSysno").val(data.userSysno);
                            $("#userName").val(data.userName);
                            $("#phone").val(data.phone);
                            $("#status").val(data.status);

                            $("#levelType").val(data.levelType);
                            if(!select(true)){
                                return;
                            }
                            $("#isManage").val(data.isManage);

                            $("#editSystemUserInfo").modal({show: true, backdrop: 'static', keyboard: false});
                        }
                    }
                },
                {
                    location: "before",
                    widget: "dxButton",
                    options: {
                        hint: "同步权限信息",
                        text: "同步权限信息",
                        visible : !disableButton("ym-gb",2),
                        icon: "check",
                        onClick: function () {
                            $.ajax({
                                type : "POST",
                                url : "auth/doSyncOaUser.action",
                                success : function(data) {
                                    showMessage(data.message);
                                }
                            });
                        }
                    }
                }
            );
        }
    )
}