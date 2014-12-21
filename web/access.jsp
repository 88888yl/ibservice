<%--
  Created by IntelliJ IDEA.
  User: myl
  Date: 14-11-13
  Time: 上午8:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Access Statistics</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
</head>
<body onload="showStatistics()">
<br>
<table border=1; style="font-size:16px;font-family: Geneva;border-color: #696969; margin: 15px;"
       bgcolor="#FEFFE9" cellspacing="10px" cellpadding="10px">
    <tr style="font-size: 16px;">
        <td style="border: 1px; padding: 0">Statistics range:
            <select id="rangeSelect" onchange="showStatistics()">
                <option value="day">Day</option>
                <option value="week">Week</option>
                <option value="month">Month</option>
                <option value="year">Year</option>
            </select>
        </td>
    </tr>
</table>

<div id="div2" style="float:left; margin: 15px"></div>
<div id="div1" style=" margin: 15px"></div>

<script type="text/javascript">
    function showStatistics() {
        var selectObj = document.getElementById("rangeSelect");
        var index = selectObj.selectedIndex;
        var range = selectObj.options[index].value;

        document.getElementById('div1').innerText = "";
        document.getElementById('div2').innerText = "";

        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'access.action',
            data: { range: range },
            success: function (result) {
                if (result == "fail") {
                    alert("Result is not found!");
                } else {
                    result = eval("(" + result + ")");
                    initResult(result, range);
                    initTable(result);
                }
            },
            error: function () {
                $("#result").html("Search failed!");
            }
        })
    }

    function initResult(result, range) {
        var table = document.createElement("table");

        table.border = "1px";
        table.padding = 0;
        table.margin = 0;
        table.borderColor = "black";
        table.setAttribute("id", "tbInfo");
        table.style.whiteSpace = "nowrap";

        for (var i = 0; i < result[0].length; i++) {
            var tr1 = document.createElement("tr");
            var td = document.createElement("td");
            td.innerHTML = result[0][i];
            tr1.appendChild(td);
            tr1.style.backgroundColor = "#FFFFCC";
            table.appendChild(tr1);
        }
        document.getElementById("div2").appendChild(table);
    }

    function initTable(result) {
        var table = document.createElement("table");

        table.border = "1px";
        table.padding = 0;
        table.margin = 0;
        table.borderColor = "black";
        table.setAttribute("id", "tblContent");
        table.style.whiteSpace = "nowrap";

        var k = 0;
        for (var i = 0; i < result[1].length; i++) {
            k++;
            var tr1 = document.createElement("tr");
            var td = document.createElement("td");
            td.innerHTML = result[1][i];
            tr1.appendChild(td);
            table.appendChild(tr1);

            if (k % 2 == 0) tr1.style.backgroundColor = "#99CCCC";
            else tr1.style.backgroundColor = "#CCFFCC";
        }
        document.getElementById("div1").appendChild(table);

        $("#tblContent").find("tr").mouseover(function () {
            if ($(this).index() == 0)
                return;
            $(this).css({ "background-color": "#FFFFCC" });
        }).mouseout(function () {
            var $index = $(this).index();
            if ($index % 2 != 0) {
                $(this).css({ "background-color": "#99CCCC" });
            } else {
                $(this).css({ "background-color": "#CCFFCC" });
            }
        });
    }
</script>
</body>
</html>
