<%@ page import="com.loan_entity.app.NoteResult" %>
<%@ page import="com.alibaba.fastjson.JSONObject" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
    NoteResult result = (NoteResult) request.getAttribute("data");
    JSONObject data = (JSONObject) result.getData();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
    <meta charset="utf-8">
    <title>签约</title>
    <meta name="viewport" content="initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no">
    <meta name="mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-title" content="vue">
    <!-- <link rel="stylesheet" type="text/css" href="style.css"/> -->
    <script>

        var oHtml = document.documentElement;
        getSize();
        function getSize(){
            // 获取屏幕的宽度
            var ascreen=oHtml.clientWidth;
            if (ascreen<=319) {
                oHtml.style.fontSize = '20px';
            } else if(ascreen>=750){
                oHtml.style.fontSize = '40px';
            }else{
                oHtml.style.fontSize=ascreen/18.75+"px";
            };
        }
        // 当窗口发生改变的时候调用
        window.onresize = function(){
            getSize();
        }
    </script>
    <script type="application/javascript" src="<%=path%>/register/jquery-1.8.0.min.js"></script>
    <base href="<%=basePath%>"></base>
    <style type="text/css">
        html {
            -webkit-text-size-adjust: 100%;
            -ms-text-size-adjust: 100%;
        }
        input[type="submit"],
        input[type="reset"],
        input[type="button"] {
            -webkit-appearance: none;
            resize: none;
        }
        /* 取消链接高亮  */
        body,
        div,
        ul,
        li,
        ol,
        h1,
        h2,
        h3,
        h4,
        h5,
        h6,
        input,
        textarea,
        select,
        p,
        dl,
        dt,
        dd,
        a,
        img,
        button,
        form,
        table,
        th,
        tr,
        td,
        tbody,
        article,
        aside,
        details,
        figcaption,
        figure,
        footer,
        header,
        hgroup,
        menu,
        nav,
        section {
            -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
        }
        /* 设置HTML5元素为块 */
        article,
        aside,
        details,
        figcaption,
        figure,
        footer,
        header,
        hgroup,
        menu,
        nav,
        section {
            display: block;
        }
        /* 图片自适应 */
        img {
            /* width: 100%; */
            height: auto;
            /* width: auto\9; ie8 */
            -ms-interpolation-mode: bicubic;
            /*为了照顾ie图片缩放失真*/
        }
        /* 初始化 */
        body,
        div,
        ul,
        li,
        ol,
        h1,
        h2,
        h3,
        h4,
        h5,
        h6,
        input,
        textarea,
        select,
        p,
        dl,
        dt,
        dd,
        a,
        img,
        button,
        form,
        article,
        aside,
        details,
        figcaption,
        figure,
        footer,
        header,
        hgroup,
        menu,
        nav,
        section {
            margin: 0;
            padding: 0;
            border: none;
        }
        body {
            font: 12px Helvetica, Microsoft YaHei, '宋体' Tahoma, Arial, sans-serif;
            color: #555;
            background-color: #F7F7F7;
        }
        em,
        i {
            font-style: normal;
        }
        strong {
            font-weight: normal;
        }
        .clearfix:after {
            content: "";
            display: block;
            visibility: hidden;
            height: 0;
            clear: both;
        }
        .clearfix {
            zoom: 1;
        }
        a {
            text-decoration: none;
            color: #969696;
            font-family: '宋体', Helvetica, Microsoft YaHei, Tahoma, Arial, sans-serif;
        }
        /* a:hover {

            color: #FED503;

            text-decoration: none;

        } */
        ul,
        ol {
            list-style: none;
        }
        h1,
        h2,
        h3,
        h4,
        h5,
        h6 {
            font-size: 100%;
            font-family: Microsoft YaHei;
        }
        img {
            border: none;
        }
        html {
            font-size: 40px;
            min-width: 319px;
            max-width: 750px;
            margin: 0 auto;
        }
        .main {
            width: 100%;
            height: 33.35rem;
            background: url(<%=path%>/register/images/register_bg.png) no-repeat;
            -webkit-background-size: cover;
            -o-background-size: cover;
            -moz-background-size: cover;
            -ms-background-size: cover;
            background-size: cover;
            padding-top: 1.6rem;
            box-sizing: border-box;
            overflow: hidden;
        }
        .logoImg {
            width: 6.5rem;
            height: 8rem;
            margin: 0 auto;
            background: url(<%=path%>/register/images/logo.png) no-repeat;
            -webkit-background-size: cover;
            -o-background-size: cover;
            -moz-background-size: cover;
            -ms-background-size: cover;
            background-size: cover;
        }
        .regInfo {
            font-size: 0.75rem;
            color: #fffc00;
            text-align: center;
            margin-top: 1.65rem;
        }
        .warp {
            margin-top: 1.7rem;
        }
        .inputBox {
            margin-bottom: 0.3rem;
        }
        .inputBox label {
            width: 6rem;
            text-align: right;
            float: left;
            line-height: 1.75rem;
            font-size: 0.7rem;
            color: #fff;
        }
        .inputBox .shortInput,
        .inputBox .longInput {
            width: 7rem;
            height: 1.75rem;
            background: #403b89;
            border-radius: 4px;
            outline: none;
            color: #fff;
            font-size: 0.7rem;
            margin-left: 0.2rem;
        }
        .inputBox .inputBtn {
            width: 3.75rem;
            height: 1.75rem;
            border-radius: 4px;
            font-size: 0.65rem;
            background: #ffce54;
            color: #403a88;
            cursor: pointer;
            outline: none;
        }
        .inputBox .longInput {
            width: 10.4rem;
            padding-left: 0.5rem;
        }
        .warpcheck {
            padding-left: 6.15rem;
            margin-top: 0.55rem;
        }
        .warpcheck input {
            width: 0.9rem;
            height: 0.9rem;
        }
        .warpcheck a {
            font-size: 0.6rem;
            color: #fff;
            vertical-align: top;
        }
        .goBtn {
            display: block;
            width: 8.4rem;
            height: 1.86rem;
            background: url(<%=path%>/register/images/gobtn_bg.png) no-repeat;
            -webkit-background-size: cover;
            -o-background-size: cover;
            -moz-background-size: cover;
            -ms-background-size: cover;
            background-size: cover;
            margin-top: 1.4rem;
            position: absolute;
            left: 50%;
            -webkit-transform: translateX(-50%);
            -moz-transform: translateX(-50%);
            -ms-transform: translateX(-50%);
            -o-transform: translateX(-50%);
            transform: translateX(-50%);
            text-indent: -9999px;
        }
        .layerBox {
            position: fixed;
            width: 100%;
            height: 100%;
            top: 0;
            left: 0;
            background: rgba(0, 0, 0, 0.65);
            display: none;
        }
        .layerMain {
            width: 12.5rem;
            height: 7.65rem;
            border-radius: 4px;
            background: #fff;
            position: absolute;
            left: 50%;
            top: 50%;
            -webkit-transform: translate(-50%, -50%);
            -moz-transform: translate(-50%, -50%);
            -ms-transform: translate(-50%, -50%);
            -o-transform: translate(-50%, -50%);
            transform: translate(-50%, -50%);
        }
        .layerHd {
            height: 1.5rem;
            border-bottom: 1px solid #ccc;
        }
        .layerHd p {
            color: #1e82d2;
            text-align: center;
            font-size: 0.9rem;
            line-height: 1.5rem;
        }
        .layerHd i {
            width: 1.25rem;
            height: 1.25rem;
            display: block;
            background: url(<%=path%>/register/images/closeBtn.png) no-repeat;
            -webkit-background-size: cover;
            -o-background-size: cover;
            -moz-background-size: cover;
            -ms-background-size: cover;
            background-size: cover;
            position: absolute;
            top: 0;
            right: 0.5rem;
            cursor: pointer;
        }
        .layerInfo {
            padding: 1.2rem 0;
        }
        .layerInfo p {
            color: #1e82d2;
            font-size: 0.75rem;
            text-align: center;
        }
        .larerBtn {
            display: block;
            width: 5.75rem;
            height: 2rem;
            background: url(<%=path%>/register/images/layerBtn.png) no-repeat;
            -webkit-background-size: cover;
            -o-background-size: cover;
            -moz-background-size: cover;
            -ms-background-size: cover;
            background-size: cover;
            text-indent: -9999px;
            position: absolute;
            left: 50%;
            -webkit-transform: translateX(-50%);
            -moz-transform: translateX(-50%);
            -ms-transform: translateX(-50%);
            -o-transform: translateX(-50%);
            transform: translateX(-50%);
        }
        .regSuc {
            width: 100%;
            height: 33.35rem;
            background: url(<%=path%>/register/images/regSuc_bg.png) no-repeat;
            -webkit-background-size: cover;
            -o-background-size: cover;
            -moz-background-size: cover;
            -ms-background-size: cover;
            background-size: cover;
            padding-top: 4rem;
            box-sizing: border-box;
        }
        .sucInfo {
            width: 9.2rem;
            height: 3.05rem;
            background: url(<%=path%>/register/images/text.png) no-repeat;
            -webkit-background-size: cover;
            -o-background-size: cover;
            -moz-background-size: cover;
            -ms-background-size: cover;
            background-size: cover;
            margin: 2.5rem auto 2rem;
        }
        .suceBtn {
            display: block;
            width: 8.25rem;
            height: 1.85rem;
            background: url(<%=path%>/register/images/sussessBtn.png) no-repeat;
            -webkit-background-size: cover;
            -o-background-size: cover;
            -moz-background-size: cover;
            -ms-background-size: cover;
            background-size: cover;
            text-indent: -999px;
            margin: 0 auto;
        }
        .imgBox {
            margin-top: 1.5rem;
        }
        .imgEr {
            width: 8rem;
            height: 8rem;
            margin: 0 auto;
        }
        .imgEr img {
            width: 100%;
            height: 100%;
        }
        .imgBox p {
            text-align: center;
            color: #fff;
            font-size: 0.6rem;
            line-height: 0.9rem;
        }
        .android,
        .iphone {
            display: block;
            margin: 0.8rem auto;
            width: 8.2rem;
            height: 1.85rem;
        }
        .android {
            background: url(<%=path%>/register/images/androidPng.png) no-repeat;
            -webkit-background-size: cover;
            -o-background-size: cover;
            -moz-background-size: cover;
            -ms-background-size: cover;
            background-size: cover;
        }
        .iphone {
            background: url("images/iphonePng.png") no-repeat;
            -webkit-background-size: cover;
            -o-background-size: cover;
            -moz-background-size: cover;
            -ms-background-size: cover;
            background-size: cover;
        }
        .intervalBox {
            width: 100%;
            height: 100%;
            position: fixed;
            top: 0;
            left: 0;
            background: rgba(0, 0, 0, 0.8);
            display: none;
        }
        .loader {
            width: 5rem;
            height: 5rem;
            position: absolute;
            left: 50%;
            top: 50%;
            transform: translate(-50%, -50%);
            -webkit-transform: translate(-50%, -50%);
        }
        .tipDiv {
            width: 6rem;
            height: 3.5rem;
            background: #fff;
            border-radius: 3px;
            position: absolute;
            left: 50%;
            top: 50%;
            transform: translate(-50%, -50%);
            -webkit-transform: translate(-50%, -50%);
            padding-top: 0.5rem;
            display: none;
        }
        .tipsInfo {
            font-size: 0.75rem;
            text-align: center;
            line-height: 1.5rem;
            color: #1E82D2;
        }
        .sure_btn {
            display: block;
            width: 2rem;
            height: 0.8rem;
            background: #1E82D2;
            border-radius: 50px;
            line-height: 0.8rem;
            text-align: center;
            margin: 0.5rem auto 0;
            color: #fff;
            font-size: 0.5rem;
        }
        @-webkit-keyframes ball-spin-fade-loader {
            50% {
                opacity: 0.3;
                -webkit-transform: scale(0.4);
                transform: scale(0.4);
            }
            100% {
                opacity: 1;
                -webkit-transform: scale(1);
                transform: scale(1);
            }
        }
        @keyframes ball-spin-fade-loader {
            50% {
                opacity: 0.3;
                -webkit-transform: scale(0.4);
                transform: scale(0.4);
            }
            100% {
                opacity: 1;
                -webkit-transform: scale(1);
                transform: scale(1);
            }
        }
        .ball-spin-fade-loader {
            position: relative;
            left: 2rem;
            top: 50%;
            transform: translateY(-50%);
            -webkit-transform: translateY(-50%);
        }
        .ball-spin-fade-loader > div:nth-child(1) {
            top: 25px;
            left: 0;
            -webkit-animation: ball-spin-fade-loader 1s 0s infinite linear;
            animation: ball-spin-fade-loader 1s 0s infinite linear;
        }
        .ball-spin-fade-loader > div:nth-child(2) {
            top: 17.04545px;
            left: 17.04545px;
            -webkit-animation: ball-spin-fade-loader 1s 0.12s infinite linear;
            animation: ball-spin-fade-loader 1s 0.12s infinite linear;
        }
        .ball-spin-fade-loader > div:nth-child(3) {
            top: 0;
            left: 25px;
            -webkit-animation: ball-spin-fade-loader 1s 0.24s infinite linear;
            animation: ball-spin-fade-loader 1s 0.24s infinite linear;
        }
        .ball-spin-fade-loader > div:nth-child(4) {
            top: -17.04545px;
            left: 17.04545px;
            -webkit-animation: ball-spin-fade-loader 1s 0.36s infinite linear;
            animation: ball-spin-fade-loader 1s 0.36s infinite linear;
        }
        .ball-spin-fade-loader > div:nth-child(5) {
            top: -25px;
            left: 0;
            -webkit-animation: ball-spin-fade-loader 1s 0.48s infinite linear;
            animation: ball-spin-fade-loader 1s 0.48s infinite linear;
        }
        .ball-spin-fade-loader > div:nth-child(6) {
            top: -17.04545px;
            left: -17.04545px;
            -webkit-animation: ball-spin-fade-loader 1s 0.6s infinite linear;
            animation: ball-spin-fade-loader 1s 0.6s infinite linear;
        }
        .ball-spin-fade-loader > div:nth-child(7) {
            top: 0;
            left: -25px;
            -webkit-animation: ball-spin-fade-loader 1s 0.72s infinite linear;
            animation: ball-spin-fade-loader 1s 0.72s infinite linear;
        }
        .ball-spin-fade-loader > div:nth-child(8) {
            top: 17.04545px;
            left: -17.04545px;
            -webkit-animation: ball-spin-fade-loader 1s 0.84s infinite linear;
            animation: ball-spin-fade-loader 1s 0.84s infinite linear;
        }
        .ball-spin-fade-loader > div {
            background-color: #fff;
            width: 15px;
            height: 15px;
            border-radius: 100%;
            margin: 2px;
            -webkit-animation-fill-mode: both;
            animation-fill-mode: both;
            position: absolute;
        }
        .signContract {
            background: #fff;
            height: 33.3rem;
            padding-top: 1.5rem;
            -webkit-box-sizing: border-box;
            box-sizing: border-box;
        }
        .contractDiv {
            padding: 0 1.875rem;
        }
        .contractDiv h5 {
            color: #1e82d2;
            font-size: 0.8rem;
            line-height: 2.75rem;
            border-bottom: 1px solid #dedede;
        }
        .signContent {
            padding-top: 1rem;
        }
        .signContent p {
            font-size: 0.8rem;
            color: #666;
            margin-bottom: 0.5rem;
        }
        .signInfo p {
            font-size: 0.8rem;
            line-height: 1.1rem;
            color: #1e82d2;
        }
        .signInput {
            margin-top: 1rem;
        }
        .signInput input {
            width: 0.9rem;
            height: 0.9rem;
            vertical-align: middle;
        }
        .signInput span {
            font-size: 0.8rem;
            color: #666;
            line-height: 1rem;
            vertical-align: middle;
        }
        .signInput span a {
            color: #1e82d2;
            display: inline-block;
            font-size: 0.78rem;
            line-height: 1rem;
            border-bottom: 1px solid #1e82d2;
        }
        .signBtn {
            display: block;
            width: 15rem;
            height: 2.2rem;
            border-radius: 0.2rem;
            background: #1e82d2;
            margin: 1.5rem auto 0;
            color: #fff;
            line-height: 2.2rem;
            text-align: center;
            font-size: 0.8rem;
        }
        .sureLayerBox {
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.6);
            position: fixed;
            top: 0;
            left: 0;
            display: none;
        }
        .sureMain {
            width: 13rem;
            height: 9rem;
            background: #fff;
            border-radius: 0.25rem;
            position: absolute;
            top: 50%;
            left: 50%;
            -webkit-transform: translate(-50%, -50%);
            transform: translate(-50%, -50%);
            padding: 1.5rem 0.75rem 0;
            -webkit-box-sizing: border-box;
            box-sizing: border-box;
        }
        .sureMain:before {
            content: '';
            position: absolute;
            top: -1.4rem;
            left: 50%;
            width: 10rem;
            height: 3.3rem;
            background: url(<%=path%>/register/images/layerTitle.png) no-repeat;
            background-size: cover;
            -webkit-background-size: cover;
            transform: translateX(-50%);
            -webkit-transform: translateX(-50%);
        }
        .sureMain p {
            font-size: 0.7rem;
            color: #666;
            line-height: 1.25rem;
        }
        .sureMain p span {
            color: #f92d2d;
        }
        .signSure {
            display: block;
            width: 7rem;
            height: 1.9rem;
            background: #ffdbdb;
            border-radius: 0.125rem;
            margin: 0.25rem auto 0;
            font-size: 1rem;
            color: #fa2e2f;
            line-height: 1.9rem;
            text-align: center;
        }
        .sureMain i {
            display: block;
            width: 1.55rem;
            height: 1.55rem;
            background: url(<%=path%>/register/images/signColsed.png) no-repeat;
            -webkit-background-size: cover;
            background-size: cover;
            position: absolute;
            right: 0;
            top: -2rem;
        }
        .repayment {
            width: 100%;
            height: 100%;
            background: #f3f2f7;
            padding: 1.15rem 0 2.5rem;
        }
        .repayment .re_logo {
            width: 8.7rem;
            height: 2.65rem;
            background: url(<%=path%>/register/images/re_logo.png) no-repeat;
            background-size: cover;
            -webkit-background-size: cover;
            margin: 0 auto 1.15rem;
        }
        .repayment .re_img {
            width: 15.05rem;
            height: 9rem;
            background: url(<%=path%>/register/images/re_img.png) no-repeat;
            background-size: cover;
            -webkit-background-size: cover;
            margin: 0 auto;
        }
        .repayment .re_tips {
            width: 15rem;
            height: 0.75rem;
            background: url(<%=path%>/register/images/re_tips.png) no-repeat;
            background-size: cover;
            -webkit-background-size: cover;
            margin: 0.25rem auto 0.75rem;
        }
        .repayment .re_slogan {
            width: 15rem;
            height: 5.6rem;
            background: url(<%=path%>/register/images/re_slogan.png) no-repeat;
            background-size: cover;
            -webkit-background-size: cover;
            margin: 0 auto 1.15rem;
        }
        .repayment .re_btn a {
            display: block;
            width: 15.05rem;
            height: 2.25rem;
            margin: 0 auto;
            text-indent: -99999px;
        }
        .repayment .re_btn a.re_download {
            background: url(<%=path%>/register/images/re_downLoad.png) no-repeat;
            background-size: cover;
            -webkit-background-size: cover;
        }
        .repayment .re_btn a.re_open {
            background: url(<%=path%>/register/images/re_open.png) no-repeat;
            background-size: cover;
            -webkit-background-size: cover;
            margin-top: 1rem;
            margin-bottom: 0.5rem;
        }
        .repayment .re_p {
            text-align: center;
            line-height: 0.9rem;
            color: #1e82d2;
            font-size: 0.65rem;
        }
        /*悠米闪的手机官网页面开始*/
        .Ym_main {
            width: 18.75rem;
        }
        .ym_banner {
            width: 18.75rem;
            height: 10.75rem;
            background: url(<%=path%>/register/images/appImg/banner.jpg) no-repeat;
            background-size: cover;
            -webkit-background-size: cover;
        }
        .ym_erwei {
            width: 4.75rem;
            height: 5.75rem;
            float: right;
            margin-right: 1.5rem;
            margin-top: 1rem;
        }
        .ym_erImg {
            width: 4.75rem;
            height: 4.75rem;
        }
        .ym_erImg img {
            width: 100%;
            height: 100%;
        }
        .ym_erwei p {
            font-size: 0.35rem;
            color: #fff;
            padding-top: 0.3rem;
            text-align: center;
        }
        .ym_listMain {
            width: 100%;
            height: 7.25rem;
            background: #fff;
        }
        .ym_listMain ul li {
            float: left;
            width: 4.25rem;
            padding-top: 0.5rem;
            font-size: 0;
        }
        .ym_listMain ul li .ym_icon {
            width: 2.05rem;
            height: 2.05rem;
            margin: 0 auto;
        }
        .ym_listMain ul li .ym_icon img {
            width: 100%;
            height: 100%;
        }
        .ym_listMain ul li:nth-child(2) {
            margin-right: 0.5rem;
        }
        .ym_listMain ul li:nth-child(3) {
            margin-right: 0.5rem;
        }
        .ym_listMain ul li p {
            font-size: 0.7rem;
            color: #5f5f5f;
            font-weight: bold;
            text-align: center;
            line-height: 28px;
        }
        .ym_listMain ul li span {
            font-size: 0.5rem;
            color: #666;
            line-height: 0.7rem;
            text-align: left;
        }
        .ym_listMain ul li:first-child {
            text-align: center;
        }
        .ym_borrow {
            width: 100%;
        }
        .ym_borrow_1 {
            width: 100%;
            height: 17.5rem;
            background: url(<%=path%>/register/images/appImg/borrow_1.jpg) no-repeat;
            background-size: cover;
            -webkit-background-size: cover;
        }
        .ym_borrow_2 {
            width: 100%;
            height: 17.5rem;
            background: url(<%=path%>/register/images/appImg/borrow_2.jpg) no-repeat;
            background-size: cover;
            -webkit-background-size: cover;
        }
        .ym_borrow_3 {
            width: 100%;
            height: 4rem;
            padding-top: 0.75rem;
            background: url(<%=path%>/register/images/appImg/ym_borrow3.png) no-repeat;
            background-size: cover;
            -webkit-background-size: cover;
        }
        .ym_borrow_3 p {
            font-size: 0.5rem;
            color: #fff;
            line-height: 0.85rem;
            text-align: center;
        }
        .ym_borrow_3 p a {
            color: #fff;
        }
        /*悠米闪的手机官网页面结束*/

    </style>
  </head>
  <body>
     <div class="signContract">
     	<div class="contractDiv">
     		<h5>请您仔细阅读以下信息及电子合同：</h5> 
     		<div class="signContent">
     			<p>姓名：<span><%=data == null ? "" : data.get("name") %></span></p>
     			<p>借款金额：<span><%=data == null ? "" : data.get("money") %></span>元</p>
     			<p>借款期限：<span><%=data == null ? "" : data.get("day") %></span>天</p>
     			<p>银行卡号：<span><%=data == null ? "" : data.get("bankNum") %></span></p>
     			<p>预计借款日期：<span><%=data == null ? "" : data.get("borrDay") %></span></p>
     			<p>快速信审费：<span><span><%=data == null ? "" : data.get("review") %></span>元</p>
     			<p>平台管理费：<span><span><%=data == null ? "" : data.get("plat") %></span>元</p>
     			<p>利息：<span><span><%=data == null ? "" : data.get("interest") %></span>元</p>
     			<p>优惠券：<span><span><%=data == null ? "" : data.get("couponMoney") %></span>元</p>
     			<p>到期应还：<span><span><%=data == null ? "" : data.get("planRepay") %></span>元</p>
     			<p>预计还款日期：<span><span><%=data == null ? "" : data.get("repayDay") %></span></p>
     		</div>
     		<div class="signInfo">
     			<p>温馨提示： <br>1、若发生逾期，收取逾期利息及违约金。逾期利息总额 = 借款本金×对应逾期利息利率(1%)×逾期天数；
                                    违约金=借款本金×10%（逾期后一次性收取）。
                            <br>2.基于借款本金，从实际支付日期开始计息</p>
     		</div>
			<div class="signInput">
				<input type="checkbox" checked="checked" class="ckeckBox"> <span>我已阅读
                <a class="openNew" href="javascript:;">《消费贷电子合同》</a>
                <a href="javascript:;" class="certificate">《数字证书服务协议（e签宝）》</a>
                <a href="javascript:;" class="userAuth">《用户授权协议》</a>
            </span>
			</div>	
			<a href="javascript:;" class="signBtn">确 认</a>
     	</div>
     </div>
    <!-- 签约成功的弹窗 -->
     <div class="sureLayerBox">
         <div class="sureMain">
            <i></i>
           <p>根据信用评分不同，将于1到7个工作日内放款，请注意接听抽样审核电话。如有疑问请关注悠米官方公众号悠米小助手（ID：umishanjie）查看攻略</p>
           <a href="javascript:;" class="signSure">确 定</a>
         </div>
     </div>
  </body>
</html>
<script type="text/javascript">
    var code = '<%=result.getCode()%>';
    var perId = '<%=data != null ? data.get("per_id") : ""%>';
    var borrId = '<%=data != null ? data.get("borr_id") : ""%>';
    var name = '<%=data == null ? "" : data.get("name") %>';
    var money = '<%=data == null ? "" : data.get("money") %>';
    var day = '<%=data == null ? "" : data.get("day") %>';
    var token = '<%=request.getAttribute("token") == null ? "" : request.getAttribute("token")%>';

    var path = '<%=path%>';
    var call = function (code,b) {
        if (navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)) {
            //苹果
            window.webkit.messageHandlers.callApp.postMessage(code);
        }else if (navigator.userAgent.match(/android/i)) {
            //安卓
            window.youmi.callApp(code);
        }
    };
    var callOpen = function (code) {
        if (navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)) {
            //苹果
            window.webkit.messageHandlers.callOpen.postMessage(code);
        }else if (navigator.userAgent.match(/android/i)) {
            //安卓
            window.youmi.callOpen(code);
        }
    };
    var certificateOpen = function (code) {
        if (navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)) {
            //苹果
            window.webkit.messageHandlers.certificateOpen.postMessage(code);
        }else if (navigator.userAgent.match(/android/i)) {
            //安卓
            window.youmi.certificateOpen(code);
        }
    };

    var userAuthOpen = function (code) {
        if (navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)) {
            //苹果
            window.webkit.messageHandlers.userAuthOpen.postMessage(code);
        }else if (navigator.userAgent.match(/android/i)) {
            //安卓
            window.youmi.userAuthOpen(code);
        }
    };

    var callSign = function (perId,borrId,name,money,day,token) {
        if (navigator.userAgent.match(/(iPhone|iPod|iPad);?/i)) {
            //苹果
            window.webkit.messageHandlers.callSign.postMessage(perId+","+borrId+","+name+","+money+","+day+","+token);
        }else if (navigator.userAgent.match(/android/i)) {
            //安卓
            window.youmi.callSign(perId,borrId,name,money,day,token);
        }
    };
    var open = function(){
        var sureLayerBox = document.querySelector('.sureLayerBox');
        sureLayerBox.style.display = 'none';
    };
    function signSuccess() {
        var sureLayerBox = document.querySelector('.sureLayerBox');
        sureLayerBox.style.display = 'block';
    };
    window.onload = function(){
        var signBtn = document.querySelector('.signBtn');
        var ckeckBox = document.querySelector('.ckeckBox');
        var sureLayerBox = document.querySelector('.sureLayerBox');
        var closed = document.querySelector('.sureMain i');
        var signSure = document.querySelector('.signSure');
        var openNew = document.querySelector('.openNew');
        var userAuth = document.querySelector('.userAuth');
        var certificate = document.querySelector('.certificate');
        signBtn.onclick = function(){
            if(code == "201"){//通知app
                call("201");
            }else{
                if(ckeckBox.checked){
                    //sureLayerBox.style.display = 'block';
                    callSign(perId,borrId,name,money,day,token);
                }else{
                    alert('请阅读消费贷电子合同！')
                }
            }
        };
        openNew.onclick = function(){
            if(code == "201"){//通知app
                call("20");
            }else{
                callOpen("202,"+borrId);
            }
        };
        certificate.onclick = function(){
            if(code == "201"){//通知app
                call("201");
            }else{
                certificateOpen("numberCertificate.html");
            }
        };
        userAuth.onclick = function(){
            if(code == "201"){//通知app
                call("201");
            }else{
                userAuthOpen("userAccredit.html");
            }
        };
        closed.onclick = signSure.onclick = function(){
            sureLayerBox.style.display = 'none';
            call("200",borrId);
        };
    }
</script>