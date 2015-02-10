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
        <div class="btn-group" style="margin: 5px 10px 5px 0; float: left">
            <button id="serviceType" type="button" class="btn btn-primary dropdown-toggle"
                    data-toggle="dropdown">
                Choose a service<span class="caret"></span>
            </button>
            <ul class="dropdown-menu" role="menu" id="type_menu">
                <li><a href="#">CSO Report</a></li>
                <li><a href="#">SCR Report</a></li>
            </ul>
        </div>
        <div style="margin: 0 10px 5px 0; float: left" class="input-group">
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
                        <ul class="dropdown-menu" id="start_li_2">
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
            <div class="btn-group" style="margin: 5px 0 5px 0; float: left">
                <button id="dayofweek" type="button" class="btn btn-primary dropdown-toggle"
                        data-toggle="dropdown">
                    Accept subscription time<span class="caret"></span>
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
    </div>
    <div class="container pull-left">
        <div style="margin: 0 10px 5px 0; float: left" class="input-group">
            <input type="button" id="confirm" value="Refresh Chart" class="btn btn-warning" onclick="chooseItems()">
        </div>
        <div style="margin: 0 10px 5px 0; width: 300px; float: left" class="input-group">
            <span class="input-group-addon"><span class="glyphicon glyphicon-send"></span> Email</span>
            <input type="text" class="form-control" id="email">
        </div>
        <div style="margin: 0 10px 5px 0; float: left" class="input-group">
            <input type="button" id="send" value="Subscribe" class="btn btn-warning" onclick="getSubscribeInfo()">
        </div>
        <div style="margin: 0 10px 5px 0; float: left" class="input-group">
            <input type="button" id="setUnSub" value="Unsubscribe" class="btn btn-danger" onclick="setUnSubscribe()">
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
<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close"
                        data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h4 class="modal-title" id="myModalLabel">
                </h4>
            </div>
            <div class="modal-body" id="myModalContent">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary"
                        data-dismiss="modal">Close
                </button>
                <button type="button" class="btn btn-danger" data-dismiss="modal"
                        id="save_subscribe" onclick="saveSubscribe();">
                    Confirm
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="myModal2" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close"
                        data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h4 class="modal-title" id="myModalLabel2">
                </h4>
            </div>
            <div class="modal-body" id="myModalContent2">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary"
                        data-dismiss="modal">Close
                </button>
                <button type="button" class="btn btn-danger" data-dismiss="modal"
                        id="unsubscribe" onclick="unSubscribe();">
                    Unsubscribe
                </button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    bind_info();

    function chooseItems() {
        var items = window.frames["treePage"].getCheckedInfo();
        var itemStr = '@';
        for (var i = 0; i < items.length; i++) {
            itemStr += items[i] + '@';
        }
        var itemString = itemStr.substring(1, itemStr.length - 1);
        var type = document.getElementById("serviceType");
        var id;
        if (type.value == "CSO Report") id = "cso_report";
        if (type.value == "SCR Report") id = "scr_report";
        var start_time = document.getElementById("startTime").value;
        var end_time = "now";
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
                if (id == "cso_report") {
                    window.frames["resultPage"].location.href =
                            "/cso_report_table.jsp?&id=" + id + "&start_time=" + start_time + "&end_time=" + end_time;
                }
                if (id == "scr_report") {
                    window.frames["resultPage"].location.href =
                            "/scr_report_table.jsp?&id=" + id + "&start_time=" + start_time + "&end_time=" + end_time;
                }
                $("#refresh").removeAttr("disabled");
            },
            error: function () {
                $("#refresh").removeAttr("disabled");
            }
        });
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
                var time_result = this.getElementsByTagName("a")[0].innerHTML;
                var time = document.getElementById("dayofweek");
                time.innerText = time_result;
                time.value = time_result;
            };
        }

        for (var j = 0; j < type_lis.length; j++) {
            type_lis[j].onclick = function () {
                var type_result = this.getElementsByTagName("a")[0].innerHTML;
                var type = document.getElementById("serviceType");
                type.innerText = type_result;
                type.value = type_result;
                if (type.value == "CSO Report") getCSOReport();
                if (type.value == "SCR Report") getSCRReport();
            };
        }

        bind_start_time();
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

    function getSubscribeInfo() {
        $('#myModal').modal('show');
        $('#myModalLabel').text("Subscribe Info");

        var type = document.getElementById("serviceType").value;
        var start = document.getElementById("startTime").value;
        var time = document.getElementById("dayofweek").value;
        var email = document.getElementById("email").value;


        if (type == "") {
            $('#myModalContent').text('Please choose a service first!');
            $('#save_subscribe').attr("disabled", "disabled");
        } else {
            if (start == "") {
                $('#myModalContent').text('Please input a start time!');
                $('#save_subscribe').attr("disabled", "disabled");
            } else if (time == "") {
                $('#myModalContent').text('Please choose a report time!');
                $('#save_subscribe').attr("disabled", "disabled");
            } else if (email == "") {
                $('#myModalContent').text('Please input a subscribe author(email address)!');
                $('#save_subscribe').attr("disabled", "disabled");
            } else {
                $("#save_subscribe").removeAttr("disabled");
                $('#myModalContent').html(
                        '<p><b>Author</b>: ' + email + '</p>' +
                        '<p><b>Type</b>: ' + type + '</p>' +
                        '<p><b>Start FW</b>: ' + start + '</p>' +
                        '<p><b>Time</b>: Every ' + time + ' of week</p>'
                );
            }
        }
    }

    function saveSubscribe() {
        var items = window.frames["treePage"].getCheckedInfo();
        var itemStr = '@';
        for (var i = 0; i < items.length; i++) {
            itemStr += items[i] + '@';
        }
        var itemString = itemStr.substring(1, itemStr.length - 1);

        var type = document.getElementById("serviceType").value;
        var start = document.getElementById("startTime").value;
        var time = document.getElementById("dayofweek").value;
        var email = document.getElementById("email").value;

        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'subscribe.action',
            data: {
                items: itemString,
                serviceType: type,
                startTime: start,
                dayOfWeek: time,
                email: email
            }
        });
    }

    function setUnSubscribe() {
        $('#myModal2').modal('show');
        $('#myModalLabel2').text("UnSubscribe Info");

        var type = document.getElementById("serviceType").value;
        var start = document.getElementById("startTime").value;
        var time = document.getElementById("dayofweek").value;
        var email = document.getElementById("email").value;

        if (type == "") {
            $('#myModalContent2').text('Please choose your unsubscribe service first!');
            $('#unsubscribe').attr("disabled", "disabled");
        } else {
            if (start == "") {
                $('#myModalContent2').text('Please choose unsubscribe start time!');
                $('#unsubscribe').attr("disabled", "disabled");
            } else if (time == "") {
                $('#myModalContent2').text('Please choose your unsubscribe report time!');
                $('#unsubscribe').attr("disabled", "disabled");
            } else if (email == "") {
                $('#myModalContent2').text('Please input your unsubscribe author(email address)!');
                $('#unsubscribe').attr("disabled", "disabled");
            } else {
                $("#unsubscribe").removeAttr("disabled");
                $('#myModalContent2').html(
                        '<p><b>Author</b>: ' + email + '</p>' +
                        '<p><b>Type</b>: ' + type + '</p>' +
                        '<p><b>Start FW</b>: ' + start + '</p>' +
                        '<p><b>Time</b>: Every ' + time + ' of week</p>'
                );
            }
        }
    }

    function unSubscribe() {
        var type = document.getElementById("serviceType").value;
        var start = document.getElementById("startTime").value;
        var time = document.getElementById("dayofweek").value;
        var email = document.getElementById("email").value;

        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'unSubscribe.action',
            data: {
                serviceType: type,
                startTime: start,
                dayOfWeek: time,
                email: email
            }
        });
    }
</script>
</body>
</html>
