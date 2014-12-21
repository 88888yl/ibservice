<%@ page import="cso.database.EmailToDB" %>
<%@ page import="utils.GlobalVariables" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: myl
  Date: 14-12-15
  Time: 下午3:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>SCR Report</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
    <%--<script type="text/javascript" src="ie/first-load-script.js"></script>--%>
    <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
    <link rel=StyleSheet href="bootstrap/css/bootstrap.min.css" type="text/css">

</head>
<body style="margin: 0; padding:0; background-color: darkgray; border: 0" onload="getSCRReport()">

<style>
    #content-wrapper {
        margin: 0;
        width: 100%;
        height: 93.2%;
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
            <input type="text" class="form-control" id="email" placeholder="example@example.com">
        </div>
        <div style="margin: 5px 10px 5px 0; width: 120px; float: left" class="input-group" onclick="hidePopovers()">
            <input type="button" id="send" value="Send" class="btn btn-warning" onclick="sendReport()">
            <span class="label label-default" id="information" style="width: 30px; margin-left: 5px"></span>
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

<script type="text/javascript">
    function getSCRReport() {
        bind_menu();
        window.frames["treePage"].location.href = "/scr_tree.jsp";
        window.frames["resultPage"].location.href = "/scr_report_table.jsp";
    }

    $("#addNew").popover({
        html: true,
        show: true,
        trigger: 'click',
        placement: 'bottom',
        content: "<div class=\"container\" " +
        "style=\" width: 240px\">" +
        "<div style=\"margin: 10px; width: 200px;\" " +
        "class=\"input-group\"><span class=\"input-group-addon\">" +
        "Email</span><input type=\"text\" class=\"form-control\" " +
        "id=\"emailAddr\" placeholder=\"example@example.com\"></div><div " +
        "style=\"margin: 0 10px 10px 10px; width: 200px\" " +
        "class=\"input-group\"><input style=\"width: 200px\" " +
        "type=\"button\" id=\"confirm_add\" value=\"Confirm to add\" " +
        "class=\"btn btn-danger\" onclick=\"saveAddr();\"></div></div>"
    });

    function hidePopovers() {
        $('#addNew').popover('hide');
    }

    function saveAddr() {
        var email = document.getElementById("emailAddr").value;
        console.log(email);
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'addr.action',
            data: {
                email: email
            },
            beforeSend: function () {
                $("#information2").html("Running...");
            },
            success: function (data) {
                $("#information2").html(data);
            },
            error: function () {
                $("#information2").html("Update database failed!");
            }
        });
        $('#addNew').popover('hide');
    }

    function bind_menu() {
        var lis = document.getElementById("menu2").getElementsByTagName("li");
        for (var i = 0; i < lis.length; i++) {
            lis[i].onclick = function () {
                var email = document.getElementById("email");
                email.value = this.getElementsByTagName("a")[0].innerHTML;
                console.log(this.getElementsByTagName("a")[0].innerHTML)
            };
        }
    }

    function sendReport() {
        $("#send").attr("disabled", "disabled");
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'scremail.action',
            data: {email: $("#email").val()},
            beforeSend: function () {
                $("#information").html("Running...");
            },
            success: function (data) {
                $("#information").html(data);
                $("#send").removeAttr("disabled");
            },
            error: function () {
                $("#information").html("Update database failed!");
                $("#send").removeAttr("disabled");
            }
        })
    }
</script>
</body>
</html>