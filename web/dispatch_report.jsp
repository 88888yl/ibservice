<%@ page import="cso.database.EmailToDB" %>
<%@ page import="utils.GlobalVariables" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/2/11
  Time: 20:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Dispatch Report</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
    <link rel=StyleSheet href="bootstrap/css/bootstrap.min.css" type="text/css">
</head>
<body style="margin: 0; padding:0; background-color: darkgray; border: 0">
<style>
    #content-wrapper {
        margin: 0;
        width: 100%;
        height: 87.5%;
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

    .dropdown-submenu {
        position: relative;
    }

    .dropdown-submenu > .dropdown-menu {
        top: 0;
        left: 100%;
        margin-top: -6px;
        margin-left: -1px;
        -webkit-border-radius: 0 6px 6px 6px;
        -moz-border-radius: 0 6px 6px;
        border-radius: 0 6px 6px 6px;
    }

    .dropdown-submenu:hover > .dropdown-menu {
        display: block;
    }

    .dropdown-submenu > a:after {
        display: block;
        content: " ";
        float: right;
        width: 0;
        height: 0;
        border-color: transparent;
        border-style: solid;
        border-width: 5px 0 5px 5px;
        border-left-color: #ccc;
        margin-top: 5px;
        margin-right: -10px;
    }

    .dropdown-submenu:hover > a:after {
        border-left-color: #fff;
    }

    .dropdown-submenu.pull-left {
        float: none;
    }

    .dropdown-submenu.pull-left > .dropdown-menu {
        left: -100%;
        margin-left: 10px;
        -webkit-border-radius: 6px 0 6px 6px;
        -moz-border-radius: 6px 0 6px 6px;
        border-radius: 6px 0 6px 6px;
    }
</style>
<div id="div1" class="container-fluid panel" style="background-image: url('image/background1.jpg'); margin-bottom: 6px; box-shadow: 0 3px 6px black">
    <div class="container pull-left" style="margin-top: 10px">
        <div class="btn-group" style="margin: 5px 0 5px 0; float: left">
            <button type="button" class="btn btn-primary" id="addNew">
                <span class="glyphicon glyphicon-plus"></span> new
            </button>
        </div>
        <div class="btn-group" style="margin: 5px 10px 5px 0; float: left" onclick="hidePopovers()">
            <button type="button" class="btn btn-primary dropdown-toggle"
                    data-toggle="dropdown">
                Frequent Contact<span class="caret"></span>
            </button>
            <ul class="dropdown-menu" role="menu" id="menu2">
                <%
                    EmailToDB emailToDB = new EmailToDB(
                            GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
                    List<String> emails = emailToDB.getAddr();
                    for (int i = 0; i < emails.size(); i++) {
                %>
                <li><a href="#"><%=emails.get(i)%></a></li>
                <%}%>
            </ul>
        </div>
        <div style="margin: 5px 10px 5px 0; width: 300px; float: left" class="input-group" onclick="hidePopovers()">
            <span class="input-group-addon"><span class="glyphicon glyphicon-send"></span> Email</span>
            <input type="text" class="form-control" id="email">
        </div>
        <div style="margin: 5px 10px 5px 0; width: 120px; float: left" class="input-group" onclick="hidePopovers()">
            <input type="button" id="send" value="Send" class="btn btn-warning">
                   <%--onclick="{window.frames['resultPage'].getSVG();this.disabled = true;var me = this;setTimeout(function() { me.disabled = false; }, 5000);}">--%>
            <span class="label label-default" id="information" style="width: 30px; margin-left: 5px"></span>
        </div>
    </div>
    <div class="container pull-left">
        <div style="margin: 5px 10px 5px 0; float: left" class="input-group" onclick="hidePopovers()">
            <input type="button" id="confirm" value="Refresh Chart" class="btn btn-warning" onclick="refreshChart()">
        </div>
    </div>
</div>

<div id='content-wrapper'>
    <div id='content-left'>
        <iframe src="empty.jsp" id="treePage" name="treePage" width="100%" height="100%" frameborder="0"
                onclick="hidePopovers()"
                style="background-color: darkgray; overflow-y: hidden;">
        </iframe>
    </div>
    <div id='content-right'>
        <iframe src="empty.jsp" id="resultPage" name="resultPage" width="100%" height="100%" frameborder="0"
                onclick="hidePopovers()"
                style="background-color: darkgray; overflow-y: hidden;">
        </iframe>
    </div>
</div>
<div id="tmpValue" style="display: none"></div>
<script type="text/javascript">
    window.frames["treePage"].location.href = "/dispatch_report_catalogue.jsp";

    function hidePopovers() {
        $('#addNew').popover('hide');
    }

    function refreshChart() {
        document.getElementById("tmpValue").innerText = window.frames["treePage"].getCheckedInfo();

        var btn = document.getElementById("confirm");
        btn.disabled = true;
        var me = btn;
        setTimeout(function () {
            me.disabled = false;
        }, 3000);

        window.frames["resultPage"].location.href = "/dispatch_report_table.jsp";
    }
</script>
</body>
</html>
