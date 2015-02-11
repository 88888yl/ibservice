<%@ page import="java.util.*" language="java" contentType="text/html; charset=utf-8" %>
<%--<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">--%>
<html>
<head>
    <title>PartFinder</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="js/ajaxupload.js"></script>
    <script type="text/javascript" src="ie/first-load-script.js"></script>
</head>

<body id="body" style="margin: 0; padding:0; background-color: darkgray; border: 0">

<style>
    #content-wrapper {
        margin: 0;
        width: 100%;
        height: 93.2%;
        overflow: hidden;
    }

    #content-left {
        width: 20%;
        height: 100%;
        float: left;
        border-right: solid 1px #A9D0D6;
        overflow-y: hidden;
    }

    #content-right {
        width: 80%;
        height: 100%;
        float: right;
        overflow-y: hidden;
    }
</style>

<div class="container-fluid panel"
     style="background-image: url('image/background1.jpg'); position: fixed; top: 45px; box-shadow: 0 3px 6px black">
    <div class="container pull-left" style="margin-top: 10px">
        <div style="margin: 5px 10px 5px 0; width: 600px; float: left" class="input-group">
            <span class="input-group-addon"><span class="glyphicon glyphicon-user"></span> Username</span>
            <input type="text" class="form-control" id="username">
            <span class="input-group-addon">Password</span>
            <input type="password" class="form-control" id="password">
        </div>

        <div style="margin: 5px 10px 5px 0; width: 240px; float: left" class="input-group">
            <input type="button" id="uploadBtn" value="Select File..." class="btn btn-primary">
            &nbsp;
            <input type="button" id="update" value="UpdateBOM" class="btn btn-warning" onclick="login();">
            <span class="label label-default" id="information" style="width: 30px; margin-left: 5px"></span>
        </div>
    </div>

    <div class="container pull-left">
        <div style="margin: 5px 10px 5px 0; width: 600px; float: left" class="input-group">
            <span class="input-group-addon">
                Product
            </span>
            <span class="input-group-addon">
            <select name="searchSelect" id="searchSelect" class="form-control" style="width: 220px">
                <option value="all">All</option>
                <%
                    partfinder.utils.GetTableInfo getTableInfo = new partfinder.utils.GetTableInfo();
                    getTableInfo.getConnect();
                    List<String> tableNames = getTableInfo.getTableNames();
                    List<String> tableDescs = getTableInfo.getTableDesc();
                    getTableInfo.closeAll();
                    for (int i = 0; i < tableDescs.size() && i < tableNames.size(); i++) {
                %>
                <option value=<%=tableNames.get(i)%>><%=tableDescs.get(i)%>
                </option>
                <%}%>
            </select>
                </span>
            <span class="input-group-addon">
                <input class="checkbox-inline" type="checkbox" id="children" name="children" value="children">&nbsp;Show Children
            </span>
            <span class="input-group-addon">
                <input class="checkbox-inline" type="checkbox" id="info" name="info" value="info">&nbsp;Show all Info
            </span>
        </div>
    </div>

    <div class="container pull-left">
        <div style="margin: 5px 10px 5px 0; float: left" class="input-group">
            <div style="margin: 5px 10px 5px 0; float: left" class="input-group">
                <input type="button" class="btn btn-primary" id="search_table" name="search_table" value="Grid Search"
                       onclick="showGridResult();">
            </div>
            <div style="margin: 5px 10px 5px 0; float: left" class="input-group">
                <input type="button" class="btn btn-primary" id="searchSubmit" name="searchSubmit" value="Tree Search"
                       onclick="Click();"/>
            </div>
            <div style="margin: 5px 10px 5px 0; float: left" class="input-group">
                <input type="button" id="export" value="Export" class="btn btn-primary" onclick="getExport()">
            </div>
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
<div id="tmpValue" style="display: none"></div>

<script type="text/javascript">
    var winHeight;
    var winWidth;

    if (window.innerWidth)
        winWidth = window.innerWidth;
    else if ((document.body) && (document.body.clientWidth))
        winWidth = document.body.clientWidth;

    if (window.innerHeight)
        winHeight = window.innerHeight;
    else if ((document.body) && (document.body.clientHeight))
        winHeight = document.body.clientHeight;

    var frameBody = document.getElementById("content-wrapper");
    frameBody.style.height = winHeight - 48;

    window.frames["treePage"].location.href = "/partFinder_catalogue.jsp";

    function showGridResult() {
        document.getElementById("tmpValue").innerText = window.frames["treePage"].getSearchValues();

        var btn = document.getElementById("search_table");
        btn.disabled = true;
        var me = btn;
        setTimeout(function () {
            me.disabled = false;
        }, 3000);

        window.frames["resultPage"].location.href = "/partFinder_table.jsp";
    }

    function Click() {
        var searchValues = window.frames["treePage"].getSearchValues();

        var selectObj = document.getElementById("searchSelect");
        var index = selectObj.selectedIndex;
        var tableName = selectObj.options[index].value;

        var check2 = document.getElementById("info");
        var isAllInfo = false;
        if (check2.checked)
            isAllInfo = true;

        var check3 = document.getElementById("children");
        var isChildren = false;
        if (check3.checked)
            isChildren = true;

        window.frames["resultPage"].location.href = "/tree.jsp?tableName=" + tableName
         + "&isAllInfo=" + isAllInfo +
        "&isChildren=" + isChildren + "&searchValues=" + searchValues;
    }

    $(function () {
        var $uploadBtn = $("#uploadBtn");
        new AjaxUpload($uploadBtn, {
            action: 'upload.action',
            name: 'uploadfile',
            onSubmit: function (file) {
                $("#upload").html("Starting upload file...");
            },
            onComplete: function (file, response) {
                $("#upload").html(response);
            }
        });
    });

    function login() {
        $("#update").attr("disabled", "disabled");
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'login.action',
            data: {username: $("#username").val(), password: $("#password").val()},
            beforeSend: function () {
                $("#information").html("Running...");
            },
            success: function (data) {
                $("#information").html(data);
                $("#update").removeAttr("disabled");
            },
            error: function () {
                $("#information").html("Update database failed!");
                $("#update").removeAttr("disabled");
            }
        })
    }

    function getExport() {
        var store = window.frames["resultPage"].getGridStore();

        var btn = document.getElementById("export");
        btn.disabled = true;
        var me = btn;
        setTimeout(function () {
            me.disabled = false;
        }, 2000);

        var form = document.createElement("form");
        var element1 = document.createElement("input");
        form.method = "POST";
        form.action = "exportPartFinder.action";

        element1.value = JSON.stringify(store);
        element1.name = "store";
        element1.type = 'hidden';
        form.appendChild(element1);

        document.body.appendChild(form);

        form.submit();
    }
</script>
</body>
</html>
