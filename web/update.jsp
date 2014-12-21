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

<div class="container-fluid panel" style="background-image: url('image/background1.jpg'); position: fixed; top: 45px; box-shadow: 0 3px 6px black">
    <div class="container pull-left" style="margin-top: 10px">
        <div style="margin: 5px 10px 5px 0; width: 600px; float: left" class="input-group">
            <span class="input-group-addon"><span class="glyphicon glyphicon-user"></span> Username</span>
            <input type="text" class="form-control" id="username" placeholder="oracle user">
            <span class="input-group-addon">Password</span>
            <input type="password" class="form-control" id="password" placeholder="oracle password">
        </div>

        <div style="margin: 5px 10px 5px 0; width: 240px; float: left" class="input-group">
            <input type="button" id="uploadBtn" value="Select File..." class="btn btn-primary">
            &nbsp;
            <input type="button" id="update" value="UpdateCSO" class="btn btn-warning" onclick="login();">
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
        <div style="margin: 5px 10px 5px 0; width: 110px; float: left" class="input-group">
            <span class="input-group-addon">PN#</span>
            <input type="text" class="form-control" id="partnumber" name="partnumber" placeholder="pn">
        </div>
        <div style="margin: 5px 10px 5px 0; width: 240px; float: left" class="input-group">
            <span class="input-group-addon">Description</span>
            <input type="text" class="form-control" id="description" name="description"
                   placeholder="full/part of description">
        </div>
        <div style="margin: 5px 10px 5px 0; width: 110px; float: left" class="input-group">
            <span class="input-group-addon">MEP#</span>
            <input type="text" class="form-control" id="mep" name="mep" placeholder="mep">
        </div>
        <div style="margin: 5px 10px 5px 0; width: 110px; float: left" class="input-group">
            <span class="input-group-addon">RDO</span>
            <input type="text" class="form-control" id="rdo" name="rdo" placeholder="rdo">
        </div>
        <div style="margin: 5px 10px 5px 0; width: 240px; float: left" class="input-group">
            <input type="button" class="btn btn-primary" id="searchByPN" name="searchByPN" value="PN Search"
                   onclick="searchPN();">
            &nbsp;
            <input type="button" class="btn btn-primary" id="searchSubmit" name="searchSubmit" value="TreeList Search"
                   onclick="Click();"/>
        </div>
    </div>
</div>

<iframe src="empty.jsp" id="resultPage" name="resultPage" width="100%" height="78%" frameborder="0" style="background-color: darkgray; margin-top: 160px"></iframe>

<script type="text/javascript">
    function Click() {
        var selectObj = document.getElementById("searchSelect");
        var index = selectObj.selectedIndex;
        var tableName = selectObj.options[index].value;
        var partnumber = document.getElementById("partnumber").value;
        var description = document.getElementById("description").value;
        var mep = document.getElementById("mep").value;
        var rdo = document.getElementById("rdo").value;
        var check1 = document.getElementsByName("choice");
        var radioCheck;
        for (var i = 0; i < check1.length; i++) {
            if (check1[i].checked)
                radioCheck = check1[i].value;
        }

        var check2 = document.getElementById("info");
        var isAllInfo = false;
        if (check2.checked)
            isAllInfo = true;

        var check3 = document.getElementById("children");
        var isChildren = false;
        if (check3.checked)
            isChildren = true;

        window.frames["resultPage"].location.href = "/tree.jsp?tableName=" + tableName
                + "&partnumber=" + partnumber +
                "&description=" + description + "&mep=" + mep +
                "&rdo=" + rdo + "&isAllInfo=" + isAllInfo +
                "&isChildren=" + isChildren + "&radioCheck=" + radioCheck;
    }

    function searchPN() {
        var partnumber = document.getElementById("partnumber").value;
        var description = document.getElementById("description").value;
        var mep = document.getElementById("mep").value;
        var rdo = document.getElementById("rdo").value;

        window.frames["resultPage"].location.href = "/table.jsp?&partnumber=" + partnumber +
                "&description=" + description + "&mep=" + mep +
                "&rdo=" + rdo;
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
</script>
</body>
</html>
