var verifyNetwork = function () {
    $.ajax({
        type: "POST",
        url: "/zloan-manage/auth/loadRemoteAddr.action",

        success: function (data) {
            console.info(data);
            if(data.code == 1) {
                if (typeof(data.object)=="undefined" || data.object == null || data.object.length < 8 ||  ("116.231." != data.object.substr(0, 8) && "172.16." != data.object.substr(0, 7) && "192.168." != data.object.substr(0, 8) && "180.169.146.185" != data.object && "127.0.0.1" != data.object)) {
                    if ("0:0:0:0:0:0:0:1" == data.object) {
                        $("#source").val(0);
                        $("#loginSubmitButton").css("display", "block");
                    } else {
                        $("#verifyCodePanel").css("display", "block");
                        $("#source").val(1);
                        $("#source").val(1);
                    }
                } else {
                    $("#source").val(0);
                    $("#loginSubmitButton").css("display", "block");
                }
            }
        }
    });
};
var doLogin = function(){
    var name = $("#loginName").val();
    var password = $("#loginPwd").val();
    var loginVerifyCode = $("#loginVerifyCode").val();
    var source = $("#source").val();
    if(name == null || name == ""){
        setMessage("必须输入登录账号");
        return;
    }
    if(password == null || password == ""){
        setMessage("必须输入登录密码");
        return;
    }
    if(source == 1){
        if(loginVerifyCode == null || loginVerifyCode == ""){
            setMessage("必须输入验证码");
            return;
        }
    }
    $.ajax({
        type: "POST",
        url: "user/userLogin.action",
        data: {
            loginname: name,
            loginpassword: password,
            source:source,
            loginVerifyCode:loginVerifyCode
        },
        success: function (data) {
            if(data.code == 0){
                setMessage(data.message);
            }else {
                var resultData = data.object;
                var auth = resultData.auth;
                var category = resultData.category;
                var userid = resultData.userid;
                var username = resultData.username;
                $.cookie("userid",userid);
                $.cookie("username",username);
                $.removeCookie("auth");
                $.removeCookie("role");
                $.cookie("auth",auth);
                $.cookie("category",category);

                window.location = "/zloan-manage/index.html?e=" + 1;
            }
        }
    });
};
var setMessage = function(msg){
    $("#loginPwd_message").html(msg);
};
var clearMessage = function(){
    $("#loginPwd_message").html("");
};
var initLoginButtonClick = function(){
    $("#loginName").blur(function () {
        var name = $("#loginName").val();
        if (name != "") {
            $.ajax({
                type: "POST",
                url: "user/checkLoginName.action",
                data: {
                    'loginname': name
                },
                success: function (data) {
                    if(data.code != 1){
                        setMessage(data.message);
                    }else{
                        setMessage("");
                    }
                }
            });
        } else {
            setMessage("");
        }
    });
    $('.login-form input').keypress(function (e) {
        if (e.which == 13) {
            if ($('.login-form').validate().form()) {
                doLogin();
            }
            return false;
        }
    });
    $("#loadVerifyCode").click(function(){
        var name = $("#loginName").val();
        var password = $("#loginPwd").val();
        if(name == null || name == ""){
            setMessage("必须输入登录账号");
            return;
        }
        if(password == null || password == ""){
            setMessage("必须输入登录密码");
            return;
        }
        $("#loadVerifyCode").attr("disabled",true);
        $.ajax({
            type: "POST",
            url: "auth/sendVerifyCode.action",
            data: {userName:name,password:password},
            success: function (data) {
                if(data.code == 1){
                    setMessage(data.message);
                    $("#loginSubmitButton").css("display","block");
                    settime();
                }else{
                    setMessage("");
                }
            }
        });
    });
    $("#loginSubmitButton").click(function(){
        doLogin();
    });


    //倒计时代码
    var countdown = 180;
    function settime() {
        if (countdown == 0) {
            $("#loadVerifyCode").removeAttr("disabled");
            $("#loadVerifyCode").html("获取验证码");
            countdown = 180;
        } else {
            $("#loadVerifyCode").attr("disabled", true);
            $("#loadVerifyCode").html("重新发送(" + countdown + ")");
            countdown--;
            setTimeout(function() {
                settime()
            }, 1000)
        }
    }

    $('.inputBtn').click(function(){
        settime(this);
    })





};
var initLoginInfo = function(){
    verifyNetwork();
    initLoginButtonClick();
};

/*var Login = function () {
    $("#loginName").blur(function () {
        var name1 = $("#loginName").val();
        if (name1 != "") {
            $.ajax({
                type: "POST",
                url: "user/checkLoginName.action",
                data: {
                    'loginname': name1,

                },
                success: function (msg) {
                    var jsonobj = eval("(" + msg + ")");
                    if (jsonobj.code != 0) {

                        $("#loginName_message").html(jsonobj.message);
                    } else {
                        $("#loginName_message").html("");
                    }

                }
            });
        } else {
            $("#loginName_message").html("");

        }
    });
    $("#loginVerifyCode").bind("input propertychange",function () {
        if($("#loginVerifyCode").val().length == 6){
            $("#loginBtn").css("display","block");
        }
    });
    $("#loginName_pwd").blur(function () {
        var name1 = $("#loginName_pwd").val();
        if (name1 != "") {
            $.ajax({
                type: "POST",
                url: "user/checkLoginName.action",
                data: {
                    'loginname': name1
                },
                success: function (data) {
                    alert(data);
                    if(data.code != 1){
                        $("#loginName_message_pwd").html(data.message);
                    }else{
                        $("#loginName_message_pwd").html("");
                    }
                }
            });
        } else {
            $("#loginName_message_pwd").html("");
        }
    });

    var handleLogin = function () {
        $('.login-form').validate({
            errorElement: 'span', // default input error message container
            errorClass: 'help-block', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
                username: {
                    required: true
                },
                password: {
                    required: true
                },
                remember: {
                    required: false
                }
            },

            messages: {
                username: {
                    required: "必须输入登录账号."
                },
                password: {
                    required: "必须输入登录密码."
                }
            },

            invalidHandler: function (event, validator) { // display error
                // alert on form
                // submit
                $('.alert-danger', $('.login-form')).show();
            },

            highlight: function (element) { // hightlight error inputs
                $(element).closest('.form-group').addClass('has-error'); // set
                // error
                // class
                // to
                // the
                // control
                // group
            },

            success: function (label) {
                label.closest('.form-group').removeClass('has-error');
                label.remove();
            },

            errorPlacement: function (error, element) {
                error.insertAfter(element.closest('.input-icon'));
            },

            submitHandler: function (form) {
                dologin();
            }
        });

        $('.login-form input').keypress(function (e) {
            if (e.which == 13) {
                if ($('.login-form').validate().form()) {
                    dologin();

                }
                return false;
            }
        });
    };

    var handleForgetPassword = function () {
        $('.forget-form').validate({
            errorElement: 'span', // default input error message container
            errorClass: 'help-block', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
            rules: {
                username: {
                    required: true
                },
                password: {
                    required: true
                },
            },
            messages: {
                username: {
                    required: "必须输入登录账号."
                },
                password: {
                    required: "必须输入用户姓名."
                }
            },
            invalidHandler: function (event, validator) { // display error
                // alert on form
                // submit

            },

            highlight: function (element) { // hightlight error inputs
                $(element).closest('.form-group').addClass('has-error'); // set
                // error
                // class
                // to
                // the
                // control
                // group
            },

            success: function (label) {
                label.closest('.form-group').removeClass('has-error');
                label.remove();
            },

            errorPlacement: function (error, element) {
                error.insertAfter(element.closest('.input-icon'));
            },

            submitHandler: function (form) {
                clicklogin_pwd();
            }
        });

        $('.forget-form input').keypress(function (e) {
            if (e.which == 13) {
                if ($('.forget-form').validate().form()) {
                    clicklogin_pwd();
                }
                return false;
            }
        });

        jQuery('#forget-password').click(function () {
            jQuery('.login-form').hide();
            jQuery('.forget-form').show();
        });

        jQuery('#back-btn').click(function () {
            jQuery('.login-form').show();
            jQuery('.forget-form').hide();
        });

    };

    var handleRegister = function () {

        function format(state) {
            if (!state.id)
                return state.text; // optgroup
            return "<img class='flag' src='../../assets/global/img/flags/"
                + state.id.toLowerCase() + ".png'/>&nbsp;&nbsp;"
                + state.text;
        }

        $("#select2_sample4")
            .select2(
                {
                    placeholder: '<i class="fa fa-map-marker"></i>&nbsp;Select a Country',
                    allowClear: true,
                    formatResult: format,
                    formatSelection: format,
                    escapeMarkup: function (m) {
                        return m;
                    }
                });

        $('#select2_sample4').change(function () {
            $('.register-form').validate().element($(this)); // revalidate
            // the chosen
            // dropdown
            // value and
            // show error or
            // success
            // message for
            // the input
        });

        jQuery('#register-btn').click(function () {
            jQuery('.login-form').hide();
            jQuery('.register-form').show();
        });

        jQuery('#register-back-btn').click(function () {
            jQuery('.login-form').show();
            jQuery('.register-form').hide();
        });
    };


    return {
        // main function to initiate the module
        init: function () {
            verifyNetwork();
            handleLogin();
            handleForgetPassword();
            handleRegister();
        }

    };

}();
var dologin = function () {
    var name = $("#loginName").val();
    var password = $("#loginPwd").val();
    $.ajax({
        type: "POST",
        url: "user/userLogin.action",
        data: {
            'loginname': name,
            'loginpassword': password
        },
        success: function (data) {
            if(data.code != 1){
                $("#loginPwd_message").html(data.message);
            }else {
                var resultData = data.object;
                var result = resultData.result;
                var userid = resultData.userid;
                var username = resultData.username;
                $.cookie("userid",userid);
                $.cookie("username",username);
                $.cookie("result",result);

                window.location = "/zloan-manage/index.html?e=" + 1;
            }
        }
    });

}
//click button to login_pwd
var clicklogin_pwd = function () {
    var name = $("#loginName_pwd").val();
    var username = $("#loginPwd_pwd").val();
    $.ajax({
        type: "POST",
        url: "user/losspassword.action",
        data: {
            'loginname': name,
            'username': username
        },
        success: function (msg) {
            var jsonobj = eval("(" + msg + ")");
            if (jsonobj.code != 0) {

                $("#loginPwd_message_pwd").html(jsonobj.message);

            } else {
                $("#loginPwd_message_pwd").html("");
                alert("密码已重置，请到预留邮箱查看后使用新密码登录！");
                window.location = "/zloan-manage/login.html";

            }
        }
    });


}*/
