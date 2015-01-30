<%@ page import="cso.database.EmailToDB" %>
<%@ page import="utils.GlobalVariables" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/1/6
  Time: 15:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Report Service</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel=StyleSheet href="bootstrap/css/bootstrap.min.css" type="text/css">

    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>

</head>
<body style="margin: 0; padding:0; background-color: darkgray; border: 0">
<style>
    #content-wrapper {
        margin: 0;
        width: 100%;
        height: 82.5%;
        overflow: hidden;
    }

    #content-left {
        width: 15%;
        height: 100%;
        float: left;
        border-right: solid 1px #A9D0D6;
        overflow-y: hidden;
    }

    #content-right {
        width: 85%;
        height: 100%;
        float: right;
        overflow-y: hidden;
    }
</style>
<div id="div1" class="container-fluid panel"
     style="background-image: url('image/background1.jpg'); margin-bottom: 6px; box-shadow: 0 3px 6px black">
    <div class="container pull-left" style="margin-top: 10px">
        <div class="btn-group" style="margin: 5px 10px 5px 0; float: left">
            <button type="button" class="btn btn-primary dropdown-toggle"
                    data-toggle="dropdown">
                Choose a service<span class="caret"></span>
            </button>
            <ul class="dropdown-menu" role="menu" id="type_menu">
                <li><a href="#">CSO Report</a></li>
                <li><a href="#">SCR Report</a></li>
            </ul>
        </div>
        <div style="margin: 0 10px 5px 0; float: left" class="input-group">
            <div class="btn-group" style="margin: 5px 0 5px 0; float: left">
                <button type="button" class="btn btn-primary dropdown-toggle"
                        data-toggle="dropdown">
                    Day of week<span class="caret"></span>
                </button>
                <ul class="dropdown-menu" role="menu" id="week_menu">
                    <li><a href="#">Monday</a></li>
                    <li><a href="#">Tuesday</a></li>
                    <li><a href="#">Wednesday</a></li>
                    <li><a href="#">Thursday</a></li>
                    <li><a href="#">Friday</a></li>
                    <li><a href="#">Saturday</a></li>
                    <li><a href="#">Sunday</a></li>
                </ul>
            </div>
        </div>
        <div style="margin: 5px 0 5px 0; width: 200px; float: left" class="input-group">
            <span class="input-group-addon"><span class="glyphicon glyphicon-info-sign"></span> Info</span>
            <input type="text" class="form-control" id="service_type" placeholder="Subscribe type" readonly>
        </div>
        <div style="margin: 5px 10px 5px 0; width: 160px; float: left" class="input-group">
            <input type="text" class="form-control" id="service_date" placeholder="Subscribe date" readonly>
        </div>
    </div>
    <div class="container pull-left">
        <div style="margin: 0 10px 5px 0; float: left" class="input-group">
            <input type="button" id="confirm" value="Refresh Chart" class="btn btn-warning" onclick="chooseItems()">
        </div>
        <div style="margin: 0 10px 5px 0; width: 300px; float: left" class="input-group">
            <span class="input-group-addon"><span class="glyphicon glyphicon-send"></span> Email</span>
            <input type="text" class="form-control" id="email">
        </div>
        <div style="margin: 0 10px 5px 0; width: 120px; float: left" class="input-group">
            <input type="button" id="send" value="Subscribe" class="btn btn-warning">
            <span class="label label-default" id="information" style="width: 30px; margin-left: 5px"></span>
        </div>
    </div>
</div>
<div id='content-wrapper'>
    <div id='content-left'>
        <iframe src="empty.jsp" id="treePage" name="treePage" width="100%" height="100%" frameborder="0"
                style="background-color: darkgray; overflow-y: hidden;">
        </iframe>
    </div>
    <div id='content-right'>
        <iframe src="empty.jsp" id="resultPage" name="resultPage" width="100%" height="100%" frameborder="0"
                style="background-color: darkgray; overflow-y: hidden;">
        </iframe>
    </div>
</div>

<script type="text/javascript">
    bind_info();

    function chooseItems() {
        var items = window.frames["treePage"].getCheckedInfo();
        console.log(items);
    }

    function getCSOReport() {
        window.frames["resultPage"].location.href = "/cso_report_table.jsp";
        window.frames["treePage"].location.href = "/cso_tree.jsp";
    }
    function getSCRReport() {
        window.frames["treePage"].location.href = "/scr_tree.jsp";
        window.frames["resultPage"].location.href = "/scr_report_table.jsp";
    }
    function bind_info() {
        var week_lis = document.getElementById("week_menu").getElementsByTagName("li");
        var type_lis = document.getElementById("type_menu").getElementsByTagName("li");

        for (var i = 0; i < week_lis.length; i++) {
            week_lis[i].onclick = function () {
                var info = document.getElementById("service_date");
                info.value = this.getElementsByTagName("a")[0].innerHTML;
            };
        }

        for (var j = 0; j < type_lis.length; j++) {
            type_lis[j].onclick = function () {
                var info2 = document.getElementById("service_type");
                info2.value = this.getElementsByTagName("a")[0].innerHTML;
                if (info2.value == "CSO Report") getCSOReport();
                if (info2.value == "SCR Report") getSCRReport();
            };
        }
    }
</script>
</body>
</html>
