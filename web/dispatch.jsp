<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/1/26
  Time: 12:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Dispatch</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
    <link rel=StyleSheet href="bootstrap/css/bootstrap.min.css" type="text/css">
</head>

<body>

<style>
    #content-wrapper {
        margin: 0;
        width: 100%;
        overflow: hidden;
    }

    #content-left {
        width: 20%;
        height: 100%;
        float: left;
        border-right: solid 1px #A9D0D6;
        overflow-y: hidden;
        margin: 0;
    }

    #content-right {
        width: 80%;
        height: 100%;
        float: right;
        overflow-y: hidden;
        margin: 0;
    }

    #myLoadingModal
    {
        top: 35%;
    }
    #myLoadingModal2
    {
        top: 35%;
    }
</style>

<div class="container-fluid panel"
     style="background-image: url('image/background1.jpg');  position:fixed; top: 45px; box-shadow: 0 3px 6px black">
    <div class="container pull-left" style="margin-top: 10px">

        <div style="margin: 5px 10px 5px 0; width: 600px; float: left" class="input-group">
            <span class="input-group-addon"><span class="glyphicon glyphicon-user"></span> Username</span>
            <input type="text" class="form-control" id="username">
            <span class="input-group-addon">Password</span>
            <input type="password" class="form-control" id="password">
        </div>

        <div style="margin: 5px 10px 5px 0; width: 400px; float: left" class="input-group">
            <input type="button" style="float: left; margin-right: 10px" id="uploadBtn" value="Select File..."
                   class="btn btn-primary">
            <input type="button" style="float: left; margin-right: 10px" id="updateDispatch" value="UpdateDispatch"
                   class="btn btn-warning" onclick="updateDispatch()">
            <input type="button" style="float: left; margin-right: 10px" id="deleteDispatch" value="deleteDispatch"
                   class="btn btn-danger" onclick="deleteDispatch()">
        </div>
    </div>
    <div class="container pull-left">
        <div style="margin: 5px 10px 5px 0; float: left" class="input-group">
            <input type="button" id="search" value="Search" class="btn btn-primary" onclick="showResult()">
        </div>
        <div style="margin: 5px 10px 5px 0; float: left" class="input-group">
            <input type="button" id="export" value="Export" class="btn btn-primary" onclick="getExport()">
        </div>
        <div style="margin: 5px 10px 5px 0; width: 500px; float: left" class="input-group">
            <span class="input-group-addon">Products Install Search</span>
            <input type="text" class="form-control" id="sys_id" placeholder="Sited System Local Identifier">
        </div>
        <div style="margin: 5px 10px 5px 0; float: left" class="input-group">
            <input type="button" id="sys_search" value="Search" class="btn btn-primary" onclick="searchSYS()">
        </div>
    </div>

</div>

<div class="modal fade" id="myLoadingModal" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true" >
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">
                    Update database
                </h4>
            </div>
            <div id="loadingText" class="modal-body">
                loading...
            </div>
            <div class="modal-footer">
                <button id="loadingButton" type="button" class="btn btn-default"
                        data-dismiss="modal" disabled>ok
                </button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="myLoadingModal2" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true" >
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel2">
                    Delete database
                </h4>
            </div>
            <div id="loadingText2" class="modal-body">
                loading...
            </div>
            <div class="modal-footer">
                <button id="loadingButton2" type="button" class="btn btn-default"
                        data-dismiss="modal" disabled>ok
                </button>
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

    window.frames["treePage"].location.href = "/dispatch_catalogue.jsp";

    function searchSYS() {
        var sysId = document.getElementById("sys_id").value;
        window.frames["resultPage"].location.href = "/dispatch_sys_table.jsp?sysId=" + sysId;
    }

    function showResult() {
        document.getElementById("tmpValue").innerText = window.frames["treePage"].getSearchValues();

        var btn = document.getElementById("search");
        btn.disabled = true;
        var me = btn;
        setTimeout(function () {
            me.disabled = false;
        }, 3000);

        window.frames["resultPage"].location.href = "/dispatch_table.jsp";
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
        form.action = "exportDispatch.action";

        element1.value = JSON.stringify(store);
        element1.name = "store";
        element1.type = 'hidden';
        form.appendChild(element1);

        document.body.appendChild(form);

        form.submit();
    }

    $(function () {
        var $uploadBtn = $("#uploadBtn");
        new AjaxUpload($uploadBtn, {
            action: 'uploadDispatch.action',
            name: 'uploadfile',
            onSubmit: function (file) {
            },
            onComplete: function (file, response) {
                var btn = document.getElementById("uploadBtn");
                btn.html = "Running...";
                btn.disabled = true;
                var me = btn;
                setTimeout(function () {
                    me.disabled = false;
                    me.value = "Select File...";
                }, 2000);
            }
        });
    });
    function updateDispatch() {
        $("#updateDispatch").attr("disabled", "disabled");
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'updateDispatch.action',
            data: {username: $("#username").val(), password: $("#password").val()},
            beforeSend: function () {
                $("#myLoadingModal").modal('show');
                $("#loadingButton").attr("disabled", "disabled");
                $("#loadingText").text("loading...");
            },
            success: function (data) {
                $("#updateDispatch").removeAttr("disabled");
                $("#loadingButton").removeAttr("disabled");
                $("#loadingText").text(data);
            },
            error: function () {
                $("#updateDispatch").removeAttr("disabled");
                $("#loadingButton2").removeAttr("disabled");
                $("#loadingText2").text("update failed");
            }
        })
    }
    function deleteDispatch() {
        var cur_btn = window.event.srcElement;
        var id = cur_btn.id;
        $("#deleteDispatch").attr("disabled", "disabled");
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'deleteDispatch.action',
            data: {username: $("#username").val(), password: $("#password").val(), id: id},
            beforeSend: function () {
                $("#myLoadingModal2").modal('show');
                $("#loadingButton2").attr("disabled", "disabled");
                $("#loadingText2").text("loading...");
            },
            success: function (data) {
                $("#deleteDispatch").removeAttr("disabled");
                $("#loadingButton2").removeAttr("disabled");
                $("#loadingText2").text(data);
            },
            error: function () {
                $("#deleteDispatch").removeAttr("disabled");
                $("#loadingButton2").removeAttr("disabled");
                $("#loadingText2").text("delete failed");
            }
        })
    }
    $('#myLoadingModal').modal({backdrop: 'static', keyboard: false, show: false});
    $('#myLoadingModal2').modal({backdrop: 'static', keyboard: false, show: false});
</script>
</body>
</html>
