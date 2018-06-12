var SWITCH_ON = "1";
var SWITCH_OFF = "0";

var autoLoanInit = function () {
    checkPageEnabled("ym-gd");

    $('#start_time').datetimepicker({
        format: 'yyyy-mm-dd hh:ii',
        minView: 0,
        minuteStep: 5,
        language: 'zh-CN',
        todayBtn: 1,
        autoclose: 1,
        initialDate: new Date(),
        startDate: new Date()
    });

    $('#end_time').datetimepicker({
        format: 'yyyy-mm-dd hh:ii',
        minView: 0,
        minuteStep: 5,
        language: 'zh-CN',
        todayBtn: 1,
        autoclose: 1
    });

    $('#start_time')
        .datetimepicker()
        .on('changeDate', function (e) {
            $('#end_time').datetimepicker('setStartDate', e.date < new Date() ? new Date() : e.date);
        });

    $('#start_time').change(function () {
        updateTimeSwitchSettings(false);
    })


    $('#end_time')
        .datetimepicker()
        .on('changeDate', function (e) {
            $('#start_time').datetimepicker('setEndDate', e.date);
        });

    $('#end_time').change(function () {
        updateTimeSwitchSettings(false);
    })

    $('#normal_order_switch').change(function () {
        if (!updateLastOrderSwitchSettings(isSwitched($('#last_order_switch')), false)) {
            $('#normal_order_switch').get(0).checked = !$('#normal_order_switch').get(0).checked;
        }
    });

    $('#overdue_order_switch').change(function () {
        if (!updateLastOrderSwitchSettings(isSwitched($('#last_order_switch')), false)) {
            $('#overdue_order_switch').get(0).checked = !$('#overdue_order_switch').get(0).checked;
        }
    });

    $('#none_loan_switch').change(function () {
        if (!updateLastOrderSwitchSettings(isSwitched($('#last_order_switch')), false)) {
            $('#none_loan_switch').get(0).checked = !$('#none_loan_switch').get(0).checked;
        }
    });

    function checkTimeSwitch() {
        var startTime = $('#start_time').val();
        var endTime = $('#end_time').val();
        var startDate = null;
        var endDate = null;
        if (startTime.trim() != '') {
            startDate = Date.parse(startTime.trim().replace(/-/g, "/"));
            if (isNaN(startDate)) {
                alert('非法的起始时间！');
                return false;
            }
        }
        if (endTime.trim() != '') {
            endDate = Date.parse(endTime.trim().replace(/-/g, "/"));
            if (isNaN(endDate)) {
                alert('非法的结束时间！');
                return false;
            }

            if (endDate <= new Date()) {
                alert('结束时间不能小于等于当前时间！');
                return false;
            }
        }
        if (startDate != null && endDate != null && startDate >= endDate) {
            alert('起始时间不能大于等于结束时间！');
            return false;
        }

        if (startDate == null && endDate == null) {
            alert('请设置起始时间和结束时间！');
            return false;
        }

        return true;
    }

    function checkLastOrderSwitch(lastOrderSwitch) {
        var checkNormal = $('#normal_order_switch').get(0).checked;
        var checkOverdue = $('#overdue_order_switch').get(0).checked;
        var checkNoneLoan = $('#none_loan_switch').get(0).checked;

        if (lastOrderSwitch && !checkNormal && !checkOverdue && !checkNoneLoan) {
            alert("请选择上单状态！");
            return false;
        }

        var overdue_order_day = $('#overdue_order_day').text();
        if (checkOverdue) {
            var regex = /^(\d+)$/;
            if (!regex.exec(overdue_order_day)) {
                alert("逾期天数非法!");
                return false;
            }

            if (parseFloat(overdue_order_day) <= 0) {
                alert("逾期天数小于等于0，请设置合理的逾期天数!");
                return false;
            }

            if (parseFloat(overdue_order_day) > 9) {
                alert("逾期天数过大，请设置合理的逾期天数!");
                return false;
            }
        }

        return true;
    }

    function checkConcurrencySwitch() {
        var concurrency_limit = $('#concurrency_limit').text();
        var regex = /^(\d+)$/;
        if (!regex.exec(concurrency_limit)) {
            alert("请设置合法的并发限制次数!");
            return false;
        }

        if (parseFloat(concurrency_limit) <= 0) {
            alert("并发限制次数小于等于0，请设置合理的并发限制次数!");
            return false;
        }

        if (parseFloat(concurrency_limit) > 999) {
            alert("并发限制次数过大，请设置合理的并发限制次数!");
            return false;
        }

        return true;
    }

    function checkAmountSwitch() {
        var amount_limit = $('#amount_limit').text();
        var regex = /^[0-9]\d*(\.\d+)?$/;
        if (!regex.exec(amount_limit)) {
            alert("请设置合法的放款金额!");
            return false;
        }

        if (parseFloat(amount_limit) <= 0) {
            alert("放款金额小于等于0元，请设置合理的放款金额!");
            return false;
        }
        if (parseFloat(amount_limit) > 10000) {
            alert("放款金额过大，请设置合理的放款金额!");
            return false;
        }
        return true;
    }

    function isSwitched(button) {
        return 'true' == button.attr('switch');
    }

    function isChecked(button) {
        return button.get(0).checked;
    }

    function setSwitched(button, isSwitched) {
        if (isSwitched) {
            button.attr('switch', 'true');
            button.children('span').animate({left: '38px'}, 500, function () {
                button.addClass('currentSwitch').attr('data', 0);
            });
        } else {
            button.attr('switch', 'false');
            button.children('span').animate({left: '0px'}, 500, function () {
                button.removeClass('currentSwitch').attr('data', 1);

            });
        }
    }

    function commitAutoLoanStatus() {
        var rulers = {};

        rulers["last_order_switch"] = isSwitched($('#last_order_switch')) ? "1" : "0";
        rulers["normal_order_switch"] = isChecked($('#normal_order_switch')) ? "1" : "0";
        rulers["overdue_order_switch"] = isChecked($('#overdue_order_switch')) ? "1" : "0";
        rulers["overdue_order_day"] = getValue($('#overdue_order_day'));
        rulers["none_loan_switch"] = isChecked($('#none_loan_switch')) ? "1" : "0";

        rulers["time_switch"] = isSwitched($('#time_switch')) ? "1" : "0";
        rulers["start_time"] = $('#start_time').val();
        rulers["end_time"] = $('#end_time').val();

        rulers["concurrency_switch"] = isSwitched($('#concurrency_switch')) ? "1" : "0";
        rulers["concurrency_limit"] = getValue($('#concurrency_limit'));

        rulers["amount_switch"] = isSwitched($('#amount_switch')) ? "1" : "0";
        rulers["amount_limit"] = getValue($('#amount_limit'));

        if (!isSwitched($('#last_order_switch'))
            && !isSwitched($('#time_switch'))
            && !isSwitched($('#concurrency_switch'))
            && !isSwitched($('#amount_switch'))) {
            setSwitched($('#status'), 0);
        }

        var authInfo = $.cookie("auth");
        $.ajax({
            url: "/zloan-manage/autoLoan/updateAutoLoanStatus.action",
            data: {
                userNo: usernum,
                auth: authInfo,
                autoLoanRulers: JSON.stringify(rulers),
                autoLoanStatus: isSwitched($('#status')) ? "1" : "0"
            },
            type: 'POST',
            dataType: "json",
            success: function (result) {
                if (result.code != 1) {
                    alert(result.message);
                } else {
                    loadAutoLoanStatus();
                }
            },
            error: function () {
                console.info("更新自动放款状态错误");
            },
            timeout: 50000
        });
    }

    function updateTimeSwitchSettings(updateSwitch) {
        if (checkTimeSwitch()) {
            if (updateSwitch) {
                setSwitched($('#time_switch'), true);
            }
            commitAutoLoanStatus();
        }
    }

    $('#time_switch').click(function () {
        if (!isSwitched($('#time_switch'))) {
            updateTimeSwitchSettings(true);
        } else {
            setSwitched($('#time_switch'), false);
            commitAutoLoanStatus();
        }
    });

    function updateLastOrderSwitchSettings(switchStatus, updateSwitch) {
        if (checkLastOrderSwitch(switchStatus)) {
            if (updateSwitch) {
                setSwitched($('#last_order_switch'), true);
            }
            commitAutoLoanStatus();
            return true;
        }
        else {
            return false;
        }
    }

    $('#last_order_switch').click(function () {
        if (!isSwitched($('#last_order_switch'))) {
            updateLastOrderSwitchSettings(true, true);
        } else {
            setSwitched($('#last_order_switch'), false);
            commitAutoLoanStatus();
        }
    });


    function updateConcurrencySwitchSettings(updateSwitch) {
        if (checkConcurrencySwitch()) {
            if (updateSwitch) {
                setSwitched($('#concurrency_switch'), true);
            }
            commitAutoLoanStatus();
        }
    }

    $('#concurrency_switch').click(function () {
        if (!isSwitched($('#concurrency_switch'))) {
            updateConcurrencySwitchSettings(true);
        } else {
            setSwitched($('#concurrency_switch'), false);
            commitAutoLoanStatus();
        }
    });

    function updateAmountSwitchSettings(updateSwitch) {
        if (checkAmountSwitch()) {
            if (updateSwitch) {
                setSwitched($('#amount_switch'), true);
            }
            commitAutoLoanStatus();
        }
    }

    $('#amount_switch').click(function () {
        if (!isSwitched($('#amount_switch'))) {
            updateAmountSwitchSettings(true);
        } else {
            setSwitched($('#amount_switch'), false);
            commitAutoLoanStatus();
        }
    });

    $('#status').click(function () {
        if (!isSwitched($('#status'))) {

            if (!isSwitched($('#last_order_switch'))
                && !isSwitched($('#time_switch'))
                && !isSwitched($('#concurrency_switch'))
                && !isSwitched($('#amount_switch'))) {
                alert("请设置自动放款规则!");
                return;
            }

            if (isSwitched($('#last_order_switch')) && !checkLastOrderSwitch()) {
                return;
            }

            if (isSwitched($('#time_switch')) && !checkTimeSwitch()) {
                return;
            }

            if (isSwitched($('#concurrency_switch')) && !checkConcurrencySwitch()) {
                return;
            }

            if (isSwitched($('#amount_switch')) && !checkAmountSwitch()) {
                return;
            }
            setSwitched($('#status'), true);
            commitAutoLoanStatus();
        } else {
            setSwitched($('#status'), false);
            commitAutoLoanStatus();
            //$('#amountDiv').hide();
        }
    });


    //修改弹窗
    //第一个按钮
    //点击的时候获取文本文本框的值，将文本框改变为input,然后输入到弹框中
    $('.clickBtn').click(function (event) {

        event.stopPropagation();

        _that = $(this);

        var reviseInfo = $(this).parent().find('.pText');
        var clonerevise = reviseInfo.clone();

        var textInfo = clonerevise.find('b').text();
        clonerevise.find('b').addClass('displayNone');

        clonerevise.find('input').addClass('displayBlock').attr('value', '' + textInfo + '');

        //alert(reviseInfo.html())
        $('.reviseText').html(clonerevise.html())
        $('#reviseLog').modal('show');

    })
    //获取弹窗里的输入

    $('.clickSure').click(function (event) {

        var id = $(this).parents('.modal-content').find('span').attr("id");
        var value = $(this).parents('.modal-content').find('input').val();
        var reviseInfo = _that.parent().find('.pText');

        if (id = "overdue_order_day") {
            var regex = /^(\d+)$/;
            if (!regex.exec(value)) {
                alert("请输入合法的逾期天数!");
                return false;
            }

            if (parseFloat(value) <= 0) {
                alert("逾期天数小于等于0，请输入合理的逾期天数!");
                return false;
            }

            if (parseFloat(value) > 9) {
                alert("逾期天数过大，请输入合理的逾期天数!");
                return false;
            }

            reviseInfo.find('b').find('span').text(value);
            updateLastOrderSwitchSettings(isSwitched($('#last_order_switch')), false);
        }

        $('#reviseLog').modal('hide');
    })

    //点击的时候获取文本文本框的值，将文本框改变为input,然后输入到弹框中
    //后面两个的按钮点击事件
    $('.reviseBtn').click(function (event) {

        event.stopPropagation();

        _that = $(this);

        var reviseInfo = $(this).parents(".row").find('.contText');
        var clonerevise = reviseInfo.clone();

        var textInfo = clonerevise.find('b').text();
        clonerevise.find('b').addClass('displayNone');

        clonerevise.find('input').addClass('displayBlock').attr('value', '' + textInfo + '');

        //alert(reviseInfo.html())
        $('.reviseText').html(clonerevise.html())
        $('#reviseInfo').modal('show');

    })

    //获取弹窗里的输入框的值赋值给td

    $('.btnSure').click(function (event) {

        var id = $('.reviseText').find('span').attr("id");
        var value = $('.reviseText').find('input').val();
        var reviseInfo = _that.parents(".row").find('.contText');

        if (id == "amount_limit") {
            var regex = /^[0-9]\d*(\.\d+)?$/;
            if (!regex.exec(value)) {
                alert("请输入合法的金额数字!");
                return false;
            }

            if (parseFloat(value) <= 0) {
                alert("放款金额小于等于0元，请输入合理的放款金额!");
                return false;
            }
            if (parseFloat(value) > 10000) {
                alert("放款金额过大，请输入合理的放款金额!");
                return false;
            }
            reviseInfo.find('span').text(value);
            updateAmountSwitchSettings(false);
        } else if (id = "concurrency_limit") {
            var regex = /^(\d+)$/;
            if (!regex.exec(value)) {
                alert("请输入合法的并发限制次数!");
                return false;
            }

            if (parseFloat(value) <= 0) {
                alert("并发限制次数小于等于0，请输入合理的并发限制次数!");
                return false;
            }

            if (parseFloat(value) > 999) {
                alert("并发限制次数过大，请输入合理的并发限制次数!");
                return false;
            }
            reviseInfo.find('span').text(value);
            updateConcurrencySwitchSettings(false);
        }

        $('#reviseInfo').modal('hide');

    })

    function updateAutoLoanStatus(autoLoanStatus) {
        if (autoLoanStatus != null && SWITCH_ON == autoLoanStatus.status) {
            setSwitched($('#status'), true);
            //$('#amountDiv').show();
        } else {
            setSwitched($('#status'), false);
            //$('#amountDiv').hide();
        }
        setValue($('#loan_amount'), autoLoanStatus.loan_amount);
    }

    function setChecked(button, isChecked) {
        button.get(0).checked = isChecked;
    }

    function setValue(label, value) {
        var id = label.attr('id');
        if (typeof(value) == 'undefined' || value == null) {
            if (id == 'overdue_order_day') {
                value = 3;
            }
            else if (id == 'concurrency_limit') {
                value = 20;
            }
            else if (id == 'amount_limit') {
                value = 500;
            }
        } else {
            if (id == 'amount_limit') {
                value = value / 10000;
            } else if (id == 'loan_amount') {
                value = (value / 10000).toFixed(2);
            }
        }
        label.text(value);
    }

    function getValue(label) {
        var id = label.attr('id');
        var value = label.text();
        if (id == 'amount_limit' || id == 'loan_amount') {
            value = "" + value * 10000;
        }
        return value;
    }

    function updateAutoLoanRulers(autoLoanRulers) {
        for (var i = 0; i < autoLoanRulers.length; i++) {
            var ruler = autoLoanRulers[i];
            if (ruler.property == 'last_order_switch'
                || ruler.property == 'time_switch'
                || ruler.property == 'concurrency_switch'
                || ruler.property == 'amount_switch') {
                setSwitched($('#' + ruler.property), SWITCH_ON == ruler.value);
            }

            if (ruler.property == 'normal_order_switch'
                || ruler.property == 'overdue_order_switch'
                || ruler.property == 'none_loan_switch') {
                setChecked($('#' + ruler.property), SWITCH_ON == ruler.value);
            }

            if (ruler.property == 'overdue_order_day' || ruler.property == 'concurrency_limit' || ruler.property == 'amount_limit') {
                setValue($('#' + ruler.property), ruler.value);
            }

            if (ruler.property == 'start_time') {
                $('#end_time').datetimepicker('setStartDate', ruler.value);
                $('#' + ruler.property).val(ruler.value);
            }
            if (ruler.property == 'end_time') {
                $('#start_time').datetimepicker('setEndDate', ruler.value);
                $('#' + ruler.property).val(ruler.value);
            }
        }
    }

    function loadAutoLoanStatus() {
        var authInfo = $.cookie("auth");
        $.ajax({
            url: "/zloan-manage/autoLoan/autoLoanStatus.action",
            data: {userNo: usernum, auth: authInfo},
            type: 'POST',
            dataType: "json",
            success: function (result) {
                if (result.code == 1) {
                    updateAutoLoanStatus(result.object);
                } else {
                    console.info(result.message);
                }
            },
            error: function () {
                console.info("自动放款状态数据加载错误");
            },
            timeout: 50000
        });
    }

    function loadAutoLoanRulers() {
        var authInfo = $.cookie("auth");
        $.ajax({
            url: "/zloan-manage/autoLoan/autoLoanRulers.action",
            data: {userNo: usernum, auth: authInfo},
            type: 'POST',
            dataType: "json",
            success: function (result) {
                if (result.code == 1) {
                    updateAutoLoanRulers(result.object);
                } else {
                    console.info(result.message);
                }
            },
            error: function () {
                console.info("数据加载错误");
            },
            timeout: 50000
        });
    }

    loadAutoLoanRulers();
    loadAutoLoanStatus();

    window.setInterval(loadAutoLoanStatus, 3000);
};
