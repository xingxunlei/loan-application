var format_date_value = function(date){
	var new_date = "";
	if(date==null){
		var new_date = "";
	}else if(date.length==10){
		var date2 = date.replace("/", "-");
		new_date = date2.replace("/", "-");
	}else{
		var year = date.getFullYear(); 
		var month = date.getMonth() + 1; 
		var day = date.getDate();
		new_date=year+"-"+month+"-"+day;
	}
	return new_date;
}
var aCity = { 11: "北京", 12: "天津", 13: "河北", 14: "山西", 15: "内蒙古", 21: "辽宁", 22: "吉林", 23: "黑龙江 ", 31: "上海", 32: "江苏", 33: "浙江", 34: "安徽", 35: "福建", 36: "江西", 37: "山东", 41: "河南", 42: "湖北 ", 43: "湖南", 44: "广东", 45: "广西", 46: "海南", 50: "重庆", 51: "四川", 52: "贵州", 53: "云南", 54: "西藏 ", 61: "陕西", 62: "甘肃", 63: "青海", 64: "宁夏", 65: "新疆", 71: "台湾", 81: "香港", 82: "澳门", 91: "国外 " };

var regIdCard1 = new RegExp("[1-9]{1}\\d{16}|\\d{13}([0-9]|[xX])");
/*检查是否为合法的身份证号*/
function verifyIsIdCard(idCard) {
    //    var val = false;
    //    if (regIdCard.test(idCard)) {
    //        val = true;
    //    }
    //    return val;
    //验证基本位数准确性
    if (regIdCard1.test(idCard)) {
        if (idCard.length == 15) {
            //验证中间六位生日日期(根18位的规则不同，18位的生日号位8位)
            var sBirthday = "19" + idCard.substr(6, 2) + "-" + Number(idCard.substr(8, 2)) + "-" + Number(idCard.substr(10, 2));
            var d = new Date(sBirthday.replace(/-/g, "/"))
            if (sBirthday != (d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate()))
                return false; // "Error:非法生日";

            //验证头两位地区
            if (aCity[parseInt(idCard.substr(0, 2))] == null)
                return false; // "Error:非法地区";
            return true;
        }

        if (idCard.length == 18) {
            //验证最后一位校验位
            var idCardWi = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2); //将前17位加权因子保存在数组里
            var idCardY = new Array(1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2); //这是除以11后，可能产生的11位余数、验证码，也保存成数组
            var idCardWiSum = 0; //用来保存前17位各自乖以加权因子后的总和
            for (var i = 0; i < 17; i++) {
                idCardWiSum += idCard.substring(i, i + 1) * idCardWi[i];
            }

            var idCardMod = idCardWiSum % 11; //计算出校验码所在数组的位置
            var idCardLast = idCard.substring(17); //得到最后一位身份证号码

            //如果等于2，则说明校验码是10，身份证号码最后一位应该是X
            if (idCardMod == 2) {
                if (idCardLast == "X" || idCardLast == "x") {
                    //alert("恭喜通过验证啦！");
                } else {
                    return false; // alert("身份证号码错误！");
                }
            } else {
                //用计算出的验证码与最后一位身份证号码匹配，如果一致，说明通过，否则是无效的身份证号码
                if (idCardLast == idCardY[idCardMod]) {
                    // alert("恭喜通过验证啦！");
                } else {
                    return false; // alert("身份证号码错误！");
                }
            }

            //验证中间八位生日日期
            var sBirthday = idCard.substr(6, 4) + "-" + Number(idCard.substr(10, 2)) + "-" + Number(idCard.substr(12, 2));
            var d = new Date(sBirthday.replace(/-/g, "/"))
            if (sBirthday != (d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate()))
                return false; // "Error:非法生日";

            //验证头两位地区
            if (aCity[parseInt(idCard.substr(0, 2))] == null)
                return false; // "Error:非法地区";
            return true;
        }
    } else {
        return false;
    }

}