<%--
  Created by IntelliJ IDEA.
  User: myl
  Date: 14-12-14
  Time: 10:25 pm
--%>
<html>
<head>
    <title>SCR</title>
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

<div class="container-fluid panel" style="background-image: url('image/background1.jpg'); position: fixed; top: 45px; box-shadow: 0 3px 6px black">
    <div class="container pull-left" style="margin-top: 10px">
        <div style="margin: 5px 10px 5px 0; width: 600px; float: left" class="input-group">
            <span class="input-group-addon"><span class="glyphicon glyphicon-user"></span> Username</span>
            <input type="text" class="form-control" id="username">
            <span class="input-group-addon">Password</span>
            <input type="password" class="form-control" id="password">
        </div>
        <div style="margin: 5px 10px 5px 0; width: 220px; float: left" class="input-group">
            <input type="button" style="float: left; margin-right: 10px" id="uploadBtn" value="Select File..."
                   class="btn btn-primary">
            <input type="button" style="float: left" id="updateSCR" value="UpdateSCR" class="btn btn-warning" onclick="updateSCR()">
        </div>
        <div style="margin: 5px 10px 5px 0; width: 220px; float: left" class="input-group">
            <span class="label label-default" id="information" style=" width: 30px; margin-top: 10px"></span>
        </div>
    </div>

    <div class="container pull-left">
        <div style="margin: 5px 10px 5px 0; float: left" class="input-group">
            <input type="button" id="search" value="Search" class="btn btn-primary" onclick="showResult()">
        </div>
        <div style="margin: 5px 10px 5px 0; float: left" class="input-group">
            <input type="button" id="export" value="Export" class="btn btn-primary" onclick="getExport()">
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
<script language=JavaScript>
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

    window.frames["treePage"].location.href = "/scr_catalogue.jsp";

    function showResult() {
        document.getElementById("tmpValue").innerText = window.frames["treePage"].getSearchValues();

        var btn = document.getElementById("search");
        btn.disabled = true;
        var me = btn;
        setTimeout(function () {
            me.disabled = false;
        }, 3000);

        window.frames["resultPage"].location.href = "/scr_table.jsp";
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
        form.action = "exportSCR.action";

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
            action: 'uploadSCR.action',
            name: 'uploadfile',
            onSubmit: function (file) {
                $("#upload").html("Starting upload file...");
            },
            onComplete: function (file, response) {
                $("#upload").html(response);
            }
        });
    });

    function updateSCR() {
        var cur_btn = window.event.srcElement;
        var id = cur_btn.id;
        $("#update").attr("disabled", "disabled");
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'updateSCR.action',
            data: {username: $("#username").val(), password: $("#password").val(), id: id},
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