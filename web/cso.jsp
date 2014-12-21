<%--
  Created by IntelliJ IDEA.
  User: myl
  Date: 14-11-7
  Time: 11:05 am
--%>
<html>
<head>
    <title>CSO</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
    <script src="bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="ie/first-load-script.js"></script>
    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">
</head>

<body style="background-color: darkgray">

<div class="container-fluid panel" style="background-image: url('image/background1.jpg'); position: fixed; top: 45px; box-shadow: 0 3px 6px black">
    <div class="container pull-left" style="margin-top: 10px">

        <div style="margin: 5px 10px 5px 0; width: 600px; float: left" class="input-group" onclick="hidePopovers();">
            <span class="input-group-addon"><span class="glyphicon glyphicon-user"></span> Username</span>
            <input type="text" class="form-control" id="username" placeholder="user">
            <span class="input-group-addon">Password</span>
            <input type="password" class="form-control" id="password" placeholder="password">
        </div>

        <div style="margin: 5px 10px 5px 0; width: 220px; float: left" class="input-group">
            <input type="button" style="float: left; margin-right: 10px" id="uploadBtn" value="Select File..."
                   class="btn btn-primary" onclick="hidePopovers();">
            <input type="button" style="float: left" id="updateCSO" value="UpdateCSO" class="btn btn-warning"
                   onclick="$('#save').popover('hide');">

        </div>

        <div style="margin: 5px 10px 5px 0; width: 220px; float: left" class="input-group">
            <span class="label label-default" id="information" style=" width: 30px; margin-top: 10px"></span>
        </div>

    </div>

    <div class="container pull-left">

        <div style="margin: 5px 10px 5px 0; width: 200px; float: left" class="input-group" onclick="hidePopovers();">
            <span class="input-group-addon">Age</span>
            <input type="text" class="form-control" id="age_min" placeholder="min">
            <span class="input-group-addon">-</span>
            <input type="text" class="form-control" id="age_max" placeholder="max">
        </div>

        <div style="margin: 5px 10px 5px 0; width: 190px; float: left" class="input-group" onclick="hidePopovers();">
            <span class="input-group-addon">CSO Number</span>
            <input type="text" class="form-control" id="csonumber" placeholder="full/part">
        </div>

        <div style="margin: 5px 10px 5px 0; width: 190px;" class="input-group" onclick="hidePopovers();">
            <span class="input-group-addon">Owner Name</span>
            <input type="text" class="form-control" id="ownername" placeholder="full/part">
        </div>

        <div style="margin: 5px 10px 5px 0; width: 600px; float: left" class="input-group" onclick="hidePopovers();">
            <span class="input-group-addon">Problem Descrption</span>
            <input type="text" class="form-control" id="problem" placeholder="full/part of descrption">
        </div>

        <div style="margin: 5px 10px 5px 0; width: 240px; float: left" class="input-group" onclick="hidePopovers4()">
            <input type="button" value="more" class="btn btn-primary" id="more">
            &nbsp;
            <input type="button" value="submit" class="btn btn-warning" onclick="submitCSO()">
        </div>

    </div>

    <div class="container pull-left">

        <div style="margin: 5px 10px 5px 0; float: left" class="input-group" onclick="hidePopovers();">
            <button id="default" class="btn btn-primary" onclick="showDefaultCol()">show default</button>
        </div>

        <div style="margin: 5px 10px 5px 0; float: left" class="input-group" onclick="hidePopovers();">
            <button id="all" class="btn btn-primary" onclick="showAllCol()">show all</button>
        </div>

        <div style="margin: 5px 10px 5px 0; float: left" class="input-group" onclick="hidePopovers();">
            <button id="add" class="btn btn-primary" onclick="addColumn()">add column</button>
        </div>

        <div style="margin: 5px 10px 5px 0; float: left" class="input-group" onclick="hidePopovers2()">
            <button id="save" class="btn btn-warning">Save changes</button>
        </div>

        <div style="margin: 5px 10px 5px 0; float: left" class="input-group" onclick="hidePopovers3()">
            <button id="reset" class="btn btn-warning">Reset changes</button>
        </div>

        <div style="margin: 5px 10px 5px 0; float: left" class="input-group">
            <span class="label label-default" id="information2"></span>
        </div>

    </div>
</div>


<div id="progress" class="progress progress-striped active" style="width: 30%; margin: 200px auto;display:none">
    <div class="progress-bar progress-bar-success" role="progressbar"
         aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"
         style="width: 100%;">
    </div>
</div>

<div id="div1" style="margin-left: 30px;" onclick="hidePopovers();">
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
                        id="save_this_change" onclick="saveThisChange();">
                    Confirm
                </button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="myAddModal" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close"
                        data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h4 class="modal-title" id="myAddModalLabel">
                </h4>
            </div>
            <div class="modal-body" id="myAddModalContent">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary"
                        data-dismiss="modal">Close
                </button>
                <button type="button" class="btn btn-danger" data-dismiss="modal"
                        id="save_add_change" onclick="saveAddChange();">
                    Confirm
                </button>
            </div>
        </div>
    </div>
</div>

<script language=JavaScript>

    function hidePopovers() {
        $('#save').popover('hide');
        $('#updateCSO').popover('hide');
        $('#more').popover('hide');
        $('#reset').popover('hide');
    }

    function hidePopovers2() {
        $('#updateCSO').popover('hide');
        $('#more').popover('hide');
        $('#reset').popover('hide');
    }

    function hidePopovers3() {
        $('#updateCSO').popover('hide');
        $('#more').popover('hide');
        $('#save').popover('hide');
    }

    function hidePopovers4() {
        $('#updateCSO').popover('hide');
        $('#reset').popover('hide');
        $('#save').popover('hide');
    }

    $("#save").popover({
        html: true,
        show: true,
        trigger: 'click',
        placement: 'bottom',
        content: "<div class=\"container\" " +
        "style=\" width: 240px\">" +
        "<div style=\"margin: 10px; width: 200px;\" " +
        "class=\"input-group\"><span class=\"input-group-addon\">" +
        "Username</span><input type=\"text\" class=\"form-control\" " +
        "id=\"username1\" placeholder=\"username\"></div><div " +
        "style=\"margin: 0 10px 10px 10px; width: 200px;\" " +
        "class=\"input-group\"><span class=\"input-group-addon\">" +
        "Password</span><input type=\"password\" class=\"form-control\" " +
        "id=\"password1\" placeholder=\"password\"></div><div " +
        "style=\"margin: 0 10px 10px 10px; width: 200px\" " +
        "class=\"input-group\"><input style=\"width: 200px\" " +
        "type=\"button\" id=\"confirm_change\" value=\"Confirm to change\" " +
        "class=\"btn btn-danger\" onclick=\"saveChanges();\"></div></div>"
    });

    $("#reset").popover({
        html: true,
        show: true,
        trigger: 'click',
        placement: 'bottom',
        content: "<div class=\"container\" " +
        "style=\" width: 240px\">" +
        "<div style=\"margin: 10px; width: 200px;\" " +
        "class=\"input-group\"><span class=\"input-group-addon\">" +
        "Username</span><input type=\"text\" class=\"form-control\" " +
        "id=\"username2\" placeholder=\"username\"></div><div " +
        "style=\"margin: 0 10px 10px 10px; width: 200px;\" " +
        "class=\"input-group\"><span class=\"input-group-addon\">" +
        "Password</span><input type=\"password\" class=\"form-control\" " +
        "id=\"password2\" placeholder=\"password\"></div><div " +
        "style=\"margin: 0 10px 10px 10px; width: 200px\" " +
        "class=\"input-group\"><input style=\"width: 200px\" " +
        "type=\"button\" id=\"confirm_reset\" value=\"Confirm to reset\" " +
        "class=\"btn btn-danger\" onclick=\"resetChanges();\"></div></div>"
    });

    $("#updateCSO").popover({
        html: true,
        placement: 'right',
        trigger: 'focus',
        content: "<div class=\"container\" " +
        "style=\"\">" +
        "<div style=\"margin: 10px; width: 220px\" " +
        "class=\"input-group\"><input style=\"width: 220px\" " +
        "type=\"button\" id=\"fromOpenCSO\" value=\"Update CSO from OpenCSO\" " +
        "class=\"btn btn-danger\" onclick=\"updateCSO();\"></div>" +
        "<div style=\"margin: 0 10px 10px 10px; width: 220px\" " +
        "class=\"input-group\"><input style=\"width: 220px\" " +
        "type=\"button\" id=\"fromClosedCSO\" value=\"Update CSO from ClosedCSO\" " +
        "class=\"btn btn-danger\" onclick=\"updateCSO();\"></div>"
    });

    $("#more").popover({
        html: true,
        show: true,
        trigger: 'click',
        placement: 'bottom',
        content: "<div class=\"container\" " +
        "style=\" width: 240px\">" +
        "<div style=\"margin: 10px; width: 200px;\" " +
        "class=\"input-group\"><span class=\"input-group-addon\">" +
        "Modality</span><input type=\"text\" class=\"form-control\" " +
        "id=\"modality\"></div><div " +

        "style=\"margin: 0 10px 10px 10px; width: 200px;\" " +
        "class=\"input-group\"><span class=\"input-group-addon\">" +
        "Activity Description</span><input type=\"text\" class=\"form-control\" " +
        "id=\"activity-desc\"></div><div " +

        "style=\"margin: 0 10px 10px 10px; width: 200px;\" " +
        "class=\"input-group\"><span class=\"input-group-addon\">" +
        "Activity Owner</span><input type=\"text\" class=\"form-control\" " +
        "id=\"activity-owner\"></div><div " +

        "style=\"margin: 0 10px 10px 10px; width: 200px;\" " +
        "class=\"input-group\"><span class=\"input-group-addon\">" +
        "Activity Date</span><input type=\"text\" class=\"form-control\" " +
        "id=\"activity-date\"></div><div " +

        "style=\"margin: 0 10px 10px 10px; width: 200px;\" " +
        "class=\"input-group\"><span class=\"input-group-addon\">" +
        "Priority</span><input type=\"text\" class=\"form-control\" " +
        "id=\"priority\"></div><div " +

        "style=\"margin: 0 10px 10px 10px; width: 200px;\" " +
        "class=\"input-group\"><span class=\"input-group-addon\">" +
        "Milestone Status</span><input type=\"text\" class=\"form-control\" " +
        "id=\"milestone\"></div><div " +

        "style=\"margin: 0 10px 10px 10px; width: 200px\" " +
        "class=\"input-group\"><input style=\"width: 200px\" " +
        "type=\"button\" id=\"submitMoreCSO\" value=\"submit\" " +
        "class=\"btn btn-danger\" onclick=\"submitCSO()\"></div></div>"
    });


    function saveChanges() {
        var username1 = document.getElementById("username1").value;
        var password1 = document.getElementById("password1").value;
        var table = document.getElementById("tblContent");
        var table_list = new Array();

        for (var i = 0; i < table.rows.length; i++) {
            var cells_list = new Array();
            for (var j = 0; j < table.rows[i].cells.length; j++) {
                cells_list[j] = table.rows[i].cells[j].value;
            }
            table_list[i] = cells_list;
        }

        var table_string = JSON.stringify(table_list);
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'save.action',
            data: {
                username: username1,
                password: password1,
                changeResult: table_string
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

        $('#save').popover('hide');
    }

    function resetChanges() {
        var username2 = document.getElementById("username2").value;
        var password2 = document.getElementById("password2").value;
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'resetcso.action',
            data: {
                username: username2,
                password: password2
            },
            beforeSend: function () {
                $("#information2").html("Running...");
            },
            success: function (data) {
                $("#information2").html(data);
            },
            error: function () {
                $("#information2").html("Reset database failed!");
            }
        });
        $('#reset').popover('hide');
    }

    $(function () {
        var $uploadBtn = $("#uploadBtn");
        new AjaxUpload($uploadBtn, {
            action: 'uploadCSO.action',
            name: 'uploadfile',
            onSubmit: function (file) {
                $("#upload").html("Starting upload file...");
            },
            onComplete: function (file, response) {
                $("#upload").html(response);
            }
        });
    });

    function updateCSO() {
        var cur_btn = window.event.srcElement;
        var id = cur_btn.id;
        $("#update").attr("disabled", "disabled");
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'updateCSO.action',
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

    function submitCSO() {
        document.getElementById('div1').innerText = "";

        var age_min = document.getElementById("age_min").value;
        var age_max = document.getElementById("age_max").value;
        var csonumber = document.getElementById("csonumber").value;
        var ownername = document.getElementById("ownername").value;
        var problem = document.getElementById("problem").value;

        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'searchCSO.action',
            data: {
                age_min: age_min,
                age_max: age_max,
                csonumber: csonumber,
                ownername: ownername,
                problem: problem
            },
            beforeSend: function () {
                $("#progress").show();
            },
            success: function (result) {
                if (result == "fail") {
                    alert("Result is not found!");
                } else {
                    result = eval("(" + result + ")");
                    initTable(result);
                }
                $("#progress").hide();
            },
            error: function () {
                $("#result").html("Search failed!");
                $("#progress").hide();
            }
        });

        hidePopovers();
    }

    function initTable(result) {
        var table = document.createElement("table");

        table.border = "1px";
        table.padding = 0;
        table.style.marginTop = "250px";
        table.borderColor = "grey";
        table.setAttribute("id", "tblContent");
        table.style.whiteSpace = "nowrap";

        var tr = document.createElement("tr");
        tr.style.backgroundColor = "#99CCCC";
        tr.bold;

        var td;

        for (var n = 0; n < result[0].length; n++) {
            td = document.createElement("td");
            td.innerHTML = result[0][n];
            td.value = result[0][n];
            td.id = "td_0_" + n;
            tr.appendChild(td);
            tr.align = "center";
        }
        table.appendChild(tr);

        var k = 0;
        for (var i = 1; i < result.length; i++) {
            k++;
            var tr1 = document.createElement("tr");
            for (var j = 0; j < result[i].length; j++) {
                td = document.createElement("td");
                td.innerHTML = subStrings(result[i][j], 20);
                td.value = result[i][j];
                td.id = "td_" + i + "_" + j;
                tr1.appendChild(td);
            }
            table.appendChild(tr1);

            if (k % 2 == 0) tr1.style.backgroundColor = "#99CCCC";
            else tr1.style.backgroundColor = "#CCFFCC";
        }
        document.getElementById("div1").appendChild(table);

        showDefaultCol();

        $("#tblContent").find("tr").mouseover(function () {
            if ($(this).index() == 0)
                return;
            $(this).css({"background-color": "#FFFFCC"});
        }).mouseout(function () {
            var $index = $(this).index();
            if ($index % 2 == 0) {
                $(this).css({"background-color": "#99CCCC"});
            } else {
                $(this).css({"background-color": "#CCFFCC"});
            }
        });

        changeContent();
    }

    function showAllCol() {
        var tblContent = document.getElementById("tblContent");
        for (var i = 0; i < tblContent.rows.length; i++) {
            for (var j = 0; j < 88; j++) {
                tblContent.rows[i].cells[j].style.display = "block";
            }
        }
    }

    function showDefaultCol() {
        var tblContent = document.getElementById("tblContent");
        for (var i = 0; i < tblContent.rows.length; i++) {
            for (var j = 0; j < 88; j++) {
                tblContent.rows[i].cells[j].style.display = "none";
            }
        }
        for (var k = 0; k < tblContent.rows.length; k++) {
            tblContent.rows[k].cells[8].style.display = "block";
            tblContent.rows[k].cells[10].style.display = "block";
            tblContent.rows[k].cells[12].style.display = "block";
            tblContent.rows[k].cells[14].style.display = "block";
            tblContent.rows[k].cells[40].style.display = "block";
            tblContent.rows[k].cells[41].style.display = "block";
            tblContent.rows[k].cells[42].style.display = "block";
            tblContent.rows[k].cells[54].style.display = "block";
            tblContent.rows[k].cells[66].style.display = "block";
            tblContent.rows[k].cells[83].style.display = "block";
            tblContent.rows[k].cells[84].style.display = "block";
            tblContent.rows[k].cells[85].style.display = "block";
            tblContent.rows[k].cells[86].style.display = "block";
            tblContent.rows[k].cells[87].style.display = "block";
        }
        if (tblContent.rows[0].cells.length > 89) {
            for (var n = 88; n < tblContent.rows[0].cells.length; n++) {
                for (var m = 0; m < tblContent.rows.length; m++) {
                    tblContent.rows[m].cells[n].style.display = "block";
                }
            }
        }
    }

    function changeContent() {
        var tb = document.getElementById("tblContent");
        for (var i = 1; i < tb.rows.length; i++) {
            for (var j = 0; j < tb.rows[i].cells.length; j++) {
                tb.rows[i].cells[j].ondblclick = TdDoubleClick;
            }
        }
    }

    function TdDoubleClick() {
        $('#myModal').modal('show');
        var cur_td = window.event.srcElement;
        var id = cur_td.id;
        var tmp_id = id.substring(id.lastIndexOf('_') + 1);
        var parent_id = "td_0_" + tmp_id;
        var td_title = document.getElementById(parent_id).value;
        $('#myModalLabel').text(td_title);
        document.getElementById("myModalLabel").value = id;
        var td_content = cur_td.value;
        if (td_content) {
            $('#myModalContent').html('<textarea id="changeArea" cols="60" rows="5">' + td_content + '</textarea>');
        } else {
            $('#myModalContent').html('<textarea id="changeArea" cols="60" rows="5"></textarea>');
        }
    }

    function saveThisChange() {
        var content = document.getElementById("changeArea").value;
        var this_id = document.getElementById("myModalLabel").value;
        var this_td = document.getElementById(this_id);
        this_td.innerText = Trim(subStrings(content, 20));
        this_td.value = content;
        this_td.style.backgroundColor = "#FF5F67";
    }

    function subStrings(str, len) {
        if (str.length <= len) {
            return str;
        }
        var strlen = 0;
        var s = "";
        for (var i = 0; i < str.length; i++) {
            if (str.charCodeAt(i) > 128) {
                strlen = strlen + 2;
            }
            else {
                strlen = strlen + 1;
            }
            s = s + str.charAt(i);
            if (strlen >= len) {
                return s + "...";
            }
        }
        return s;
    }

    function Trim(str) {
        return str.replace(/(^\s*)|(\s*$)/g, "");
    }

    function addColumn() {
        $('#myAddModal').modal('show');
        $('#myAddModalLabel').text("Please input a name for the new added column");
        $('#myAddModalContent').html('<textarea id="addName" cols="30" rows="2"></textarea>');
    }

    function saveAddChange() {
        var content = document.getElementById("addName").value;
        if (content) {
            var tblContent = document.getElementById("tblContent");
            var newColumn = tblContent.rows[0].insertCell();
            newColumn.innerHTML = subStrings(content, 20);
            newColumn.value = content;
            newColumn.id = "td_" + 0 + "_" + tblContent.rows[0].cells.length;
            newColumn.value = content;
            for (var i = 1; i < tblContent.rows.length; i++) {
                newColumn = tblContent.rows[i].insertCell();
                newColumn.id = "td_" + i + "_" + tblContent.rows[i].cells.length;
            }
        }
        changeContent();
    }
</script>
</body>
</html>
