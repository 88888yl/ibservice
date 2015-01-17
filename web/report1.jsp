<%@ page import="cso.database.EmailToDB" %>
<%@ page import="utils.GlobalVariables" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: myl
  Date: 14-11-17
  Time: 下午3:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>CSO Report</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
    <link rel=StyleSheet href="bootstrap/css/bootstrap.min.css" type="text/css">
</head>

<body style="margin: 0; padding:0; background-color: darkgray; border: 0" onload="getCSOReport()">

<style>
    #content-wrapper {
        margin: 0;
        width: 100%;
        height: 88%;
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

<div id="div1" class="container-fluid panel"
     style="background-image: url('image/background1.jpg'); margin-bottom: 6px; box-shadow: 0 3px 6px black">
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
            <ul class="dropdown-menu" role="menu" id="menu">
                <%
                    EmailToDB emailToDB = new EmailToDB(
                            GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
                    List<String> emails = emailToDB.getAddr();
                    for (int i = 0; i < emails.size(); i++) {
                %>
                <li><a href="#"><%=emails.get(i)%>
                </a></li>
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
    <div class="container pull-left">
        <div class="dropdown" style="margin: 5px 10px 5px 0; float: left">
            <a id="startTime" role="button" data-toggle="dropdown" class="btn btn-primary" data-target="#">
                Choose start time... <span class="caret"></span>
            </a>
            <ul id="start_ul" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                <li class="dropdown-submenu">
                    <a tabindex="-1" href="#">2011</a>
                    <ul class="dropdown-menu" id="start_li_1">
                        <li><a href="#">2011-FW1</a></li>
                        <li><a href="#">2011-FW5</a></li>
                        <li><a href="#">2011-FW10</a></li>
                        <li><a href="#">2011-FW15</a></li>
                        <li><a href="#">2011-FW20</a></li>
                        <li><a href="#">2011-FW25</a></li>
                        <li><a href="#">2011-FW30</a></li>
                        <li><a href="#">2011-FW35</a></li>
                        <li><a href="#">2011-FW40</a></li>
                        <li><a href="#">2011-FW45</a></li>
                        <li><a href="#">2011-FW50</a></li>
                        <li><a href="#">2011-FW52</a></li>
                    </ul>
                </li>
                <li class="dropdown-submenu">
                    <a tabindex="-1" href="#">2012</a>
                    <ul class="dropdown-menu"  id="start_li_2">
                        <li><a href="#">2012-FW1</a></li>
                        <li><a href="#">2012-FW5</a></li>
                        <li><a href="#">2012-FW10</a></li>
                        <li><a href="#">2012-FW15</a></li>
                        <li><a href="#">2012-FW20</a></li>
                        <li><a href="#">2012-FW25</a></li>
                        <li><a href="#">2012-FW30</a></li>
                        <li><a href="#">2012-FW35</a></li>
                        <li><a href="#">2012-FW40</a></li>
                        <li><a href="#">2012-FW45</a></li>
                        <li><a href="#">2012-FW50</a></li>
                        <li><a href="#">2012-FW52</a></li>
                    </ul>
                </li>
                <li class="dropdown-submenu">
                    <a tabindex="-1" href="#">2013</a>
                    <ul class="dropdown-menu" id="start_li_3">
                        <li><a href="#">2013-FW1</a></li>
                        <li><a href="#">2013-FW5</a></li>
                        <li><a href="#">2013-FW10</a></li>
                        <li><a href="#">2013-FW15</a></li>
                        <li><a href="#">2013-FW20</a></li>
                        <li><a href="#">2013-FW25</a></li>
                        <li><a href="#">2013-FW30</a></li>
                        <li><a href="#">2013-FW35</a></li>
                        <li><a href="#">2013-FW40</a></li>
                        <li><a href="#">2013-FW45</a></li>
                        <li><a href="#">2013-FW50</a></li>
                        <li><a href="#">2013-FW52</a></li>
                    </ul>
                </li>
                <li class="dropdown-submenu">
                    <a tabindex="-1" href="#">2014</a>
                    <ul class="dropdown-menu" id="start_li_4">
                        <li><a href="#">2014-FW1</a></li>
                        <li><a href="#">2014-FW5</a></li>
                        <li><a href="#">2014-FW10</a></li>
                        <li><a href="#">2014-FW15</a></li>
                        <li><a href="#">2014-FW20</a></li>
                        <li><a href="#">2014-FW25</a></li>
                        <li><a href="#">2014-FW30</a></li>
                        <li><a href="#">2014-FW35</a></li>
                        <li><a href="#">2014-FW40</a></li>
                        <li><a href="#">2014-FW45</a></li>
                        <li><a href="#">2014-FW50</a></li>
                        <li><a href="#">2014-FW52</a></li>
                    </ul>
                </li>
                <li class="dropdown-submenu">
                    <a tabindex="-1" href="#">2015</a>
                    <ul class="dropdown-menu" id="start_li_5">
                        <li><a href="#">2015-FW1</a></li>
                        <li><a href="#">2015-FW5</a></li>
                        <li><a href="#">2015-FW10</a></li>
                        <li><a href="#">2015-FW15</a></li>
                        <li><a href="#">2015-FW20</a></li>
                        <li><a href="#">2015-FW25</a></li>
                        <li><a href="#">2015-FW30</a></li>
                        <li><a href="#">2015-FW35</a></li>
                        <li><a href="#">2015-FW40</a></li>
                        <li><a href="#">2015-FW45</a></li>
                        <li><a href="#">2015-FW50</a></li>
                        <li><a href="#">2015-FW52</a></li>
                    </ul>
                </li>
            </ul>
        </div>
        <div class="dropdown" style="margin: 5px 10px 5px 0; float: left">
            <a id="endTime" role="button" data-toggle="dropdown" class="btn btn-primary" data-target="#">
                Choose end time... <span class="caret"></span>
            </a>
            <ul id="end_ul" class="dropdown-menu multi-level" role="menu" aria-labelledby="dropdownMenu">
                <li class="dropdown-submenu">
                    <a tabindex="-1" href="#">2011</a>
                    <ul class="dropdown-menu" id="end_li_1">
                        <li><a href="#">2011-FW1</a></li>
                        <li><a href="#">2011-FW5</a></li>
                        <li><a href="#">2011-FW10</a></li>
                        <li><a href="#">2011-FW15</a></li>
                        <li><a href="#">2011-FW20</a></li>
                        <li><a href="#">2011-FW25</a></li>
                        <li><a href="#">2011-FW30</a></li>
                        <li><a href="#">2011-FW35</a></li>
                        <li><a href="#">2011-FW40</a></li>
                        <li><a href="#">2011-FW45</a></li>
                        <li><a href="#">2011-FW50</a></li>
                        <li><a href="#">2011-FW52</a></li>
                    </ul>
                </li>
                <li class="dropdown-submenu">
                    <a tabindex="-1" href="#">2012</a>
                    <ul class="dropdown-menu" id="end_li_2">
                        <li><a href="#">2012-FW1</a></li>
                        <li><a href="#">2012-FW5</a></li>
                        <li><a href="#">2012-FW10</a></li>
                        <li><a href="#">2012-FW15</a></li>
                        <li><a href="#">2012-FW20</a></li>
                        <li><a href="#">2012-FW25</a></li>
                        <li><a href="#">2012-FW30</a></li>
                        <li><a href="#">2012-FW35</a></li>
                        <li><a href="#">2012-FW40</a></li>
                        <li><a href="#">2012-FW45</a></li>
                        <li><a href="#">2012-FW50</a></li>
                        <li><a href="#">2012-FW52</a></li>
                    </ul>
                </li>
                <li class="dropdown-submenu">
                    <a tabindex="-1" href="#">2013</a>
                    <ul class="dropdown-menu" id="end_li_3">
                        <li><a href="#">2013-FW1</a></li>
                        <li><a href="#">2013-FW5</a></li>
                        <li><a href="#">2013-FW10</a></li>
                        <li><a href="#">2013-FW15</a></li>
                        <li><a href="#">2013-FW20</a></li>
                        <li><a href="#">2013-FW25</a></li>
                        <li><a href="#">2013-FW30</a></li>
                        <li><a href="#">2013-FW35</a></li>
                        <li><a href="#">2013-FW40</a></li>
                        <li><a href="#">2013-FW45</a></li>
                        <li><a href="#">2013-FW50</a></li>
                        <li><a href="#">2013-FW52</a></li>
                    </ul>
                </li>
                <li class="dropdown-submenu">
                    <a tabindex="-1" href="#">2014</a>
                    <ul class="dropdown-menu" id="end_li_4">
                        <li><a href="#">2014-FW1</a></li>
                        <li><a href="#">2014-FW5</a></li>
                        <li><a href="#">2014-FW10</a></li>
                        <li><a href="#">2014-FW15</a></li>
                        <li><a href="#">2014-FW20</a></li>
                        <li><a href="#">2014-FW25</a></li>
                        <li><a href="#">2014-FW30</a></li>
                        <li><a href="#">2014-FW35</a></li>
                        <li><a href="#">2014-FW40</a></li>
                        <li><a href="#">2014-FW45</a></li>
                        <li><a href="#">2014-FW50</a></li>
                        <li><a href="#">2014-FW52</a></li>
                    </ul>
                </li>
                <li class="dropdown-submenu">
                    <a tabindex="-1" href="#">2015</a>
                    <ul class="dropdown-menu" id="end_li_5">
                        <li><a href="#">2015-FW1</a></li>
                        <li><a href="#">2015-FW5</a></li>
                        <li><a href="#">2015-FW10</a></li>
                        <li><a href="#">2015-FW15</a></li>
                        <li><a href="#">2015-FW20</a></li>
                        <li><a href="#">2015-FW25</a></li>
                        <li><a href="#">2015-FW30</a></li>
                        <li><a href="#">2015-FW35</a></li>
                        <li><a href="#">2015-FW40</a></li>
                        <li><a href="#">2015-FW45</a></li>
                        <li><a href="#">2015-FW50</a></li>
                        <li><a href="#">2015-FW52</a></li>
                    </ul>
                </li>
                <li><a href="#">Now</a></li>
            </ul>
        </div>
        <div style="margin: 5px 10px 5px 0; float: left" class="input-group" onclick="hidePopovers()">
            <input type="button" id="refresh" value="Refresh Chart" class="btn btn-warning" onclick="chooseItems()">
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
    function getCSOReport() {
        bind_menu();
        bind_start_time();
        bind_end_time();
        window.frames["resultPage"].location.href = "/cso_report_table.jsp";
        window.frames["treePage"].location.href = "/cso_tree.jsp";
    }

    function chooseItems() {
        var items = window.frames["treePage"].getCheckedInfo();
//        console.log(items);
        var itemStr = '@';
        for (var i = 0; i < items.length; i++) {
            itemStr += items[i] + '@';
        }
        var itemString = itemStr.substring(1, itemStr.length - 1);
        var id = "cso_report";
        var start_time = document.getElementById("startTime").value;
        var end_time = document.getElementById("endTime").value;
        $("#refresh").attr("disabled", "disabled");
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'refresh.action',
            data: {
                items: itemString,
                id: id
            },
            success: function () {
                window.frames["resultPage"].location.href =
                        "/cso_report_table.jsp?&id=" + id + "&start_time=" + start_time + "&end_time=" + end_time;
                $("#refresh").removeAttr("disabled");
            },
            error: function () {
                $("#refresh").removeAttr("disabled");
            }
        });
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
        var lis = document.getElementById("menu").getElementsByTagName("li");
        for (var i = 0; i < lis.length; i++) {
            lis[i].onclick = function () {
                var email = document.getElementById("email");
                email.value = this.getElementsByTagName("a")[0].innerHTML;
                console.log(this.getElementsByTagName("a")[0].innerHTML)
            };
        }
    }

    function bind_start_time() {
        var start_result;
        for (var i = 0; i < 5; i++) {
            var tmpId = "start_li_" + (i + 1).toString();
            var subLis = document.getElementById(tmpId).getElementsByTagName("li");
            for (var j = 0; j < subLis.length; j++) {
                subLis[j].onclick = function () {
                    start_result = this.getElementsByTagName("a")[0].innerHTML;
                    var start_text = document.getElementById("startTime");
                    start_text.innerHTML = start_result;
                    start_text.value = start_result;
                }
            }
        }
    }

    function bind_end_time() {
        var end_result;
        for (var i = 0; i < 5; i++) {
            var tmpId = "end_li_" + (i + 1).toString();
            var subLis = document.getElementById(tmpId).getElementsByTagName("li");
            for (var j = 0; j < subLis.length; j++) {
                subLis[j].onclick = function () {
                    end_result = this.getElementsByTagName("a")[0].innerHTML;
                    var end_text = document.getElementById("endTime");
                    end_text.value = end_result;
                    var start_text = document.getElementById("startTime").value;
                    if (start_text == null) {
                        alert("Please select start time before this.");
                    } else if (start_text.substring(0, 4) > end_text.value.substring(0, 4)) {
                        alert("End year must older than start year, please reselect the time");
                    } else if (start_text.substring(0, 4) == end_text.value.substring(0, 4)
                    && parseInt(start_text.substring(7)) >= parseInt(end_text.value.substring(7))) {
                        alert("End fw must older than start fw, please reselect the time");
                    } else {
                        end_text.innerHTML = end_result;
                    }
                }
            }
        }
        var lis = document.getElementById("end_ul").getElementsByTagName("li");
        lis[65].onclick = function () {
            var end_text = document.getElementById("endTime");
            end_text.innerHTML = "now";
            end_text.value = "now";
        }
    }

    function sendReport() {
        $("#send").attr("disabled", "disabled");
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'email.action',
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
