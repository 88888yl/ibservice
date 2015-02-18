<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/2/11
  Time: 20:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Dispatch Report</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
    <link rel="stylesheet" href="css/check-tree.css"/>
</head>
<body style="background-color: #a9a9a9; border: 0" id="myScreen">

<div id="div2" style="overflow-y: auto; overflow-x: auto; margin: 10px 28px 0 28px; background-color: #a9a9a9">
</div>
<link rel="stylesheet" type="text/css" href="ext_js/resources/css/ext-all-neptune.css"/>
<script type="text/javascript" src="ext_js/bootstrap.js"></script>
<script type="text/javascript" src="ext_js/locale/ext-lang-en.js"></script>
<script type="text/javascript">
    var winWidth;
    var winHeight;

    var searchKeys = parent.document.getElementById("tmpValue").innerText;

    if (window.innerWidth)
        winWidth = window.innerWidth;
    else if ((document.body) && (document.body.clientWidth))
        winWidth = document.body.clientWidth;

    if (window.innerHeight)
        winHeight = window.innerHeight;
    else if ((document.body) && (document.body.clientHeight))
        winHeight = document.body.clientHeight;

    function getParameter(paraName) {
        var sUrl = location.href + "?para1=invest&para2=dollar";
        var reg = "(?:\\?|&){1}" + paraName + "=([^&]*)";
        var regresult = new RegExp(reg, "gi");
        regresult.exec(sUrl);
        return RegExp.$1;
    }

    function getDispatchReport() {
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            data: {
                searchKeys: searchKeys
            },
            url: 'dispatchReport.action',
            success: function (result) {
                if (result == "fail") {
                    alert("Report is not found!");
                } else {
                    result = eval("(" + result + ")");
//                    initTable(result);
                    createChart(result);
//                    createChart2(result);
                }
            },
            error: function () {
                $("#result").html("Receive failed!");
            }
        });
    }

    function createChart(obj) {
        var result = obj[1];
        var psiCodeList = obj[0];

        console.log(result);
        console.log(psiCodeList);

//        var max = 0;
//        for (var i = 0; i < result.length; i++) {
//            for (var j = 1; j < result[i].length; j++) {
//                max = max < result[i][j] ? result[i][j] : max;
//            }
//        }
//        max = Math.ceil(max * 1.1);
//
//        var myStore = Ext.create('Ext.data.JsonStore', {
//            fields: ['0', '1', '2'],
//            data: result
//        });
//
//        var chart = Ext.create('Ext.chart.Chart', {
//            id: 'dispatch_chart',
//            style: 'background:#fff',
//            animate: true,
//            store: myStore,
//            shadow: true,
//            theme: 'Category1',
//            legend: {
//                position: 'right'
//            },
//            axes: [
//                {
//                    title: 'dispatch report',
//                    type: 'Numeric',
//                    position: 'left',
//                    minimum: 0,
//                    maximum: max,
//                    label: {
//                        renderer: Ext.util.Format.numberRenderer('0,0'),
//                        font: '10px Arial'
//                    }
//                },
//                {
//                    title: 'Products',
//                    type: 'Category',
//                    position: 'bottom',
//                    fields: ['0'],
//                    grid: true,
//                    label: {
//                        font: '11px Arial',
//                        renderer: function (name) {
//                            return name;
//                        },
//                        rotate: {
//                            degrees: -45
//                        }
//                    }
//                }
//            ],
//            series: [
//                {
//                    title: 'XRA',
//                    type: 'column',
//                    axis: 'left',
//                    highlight: true,
////                    smooth: true,
//                    xField: '0',
//                    yField: '2',
//                    style: {
//                        fill: '#A9413B',
//                        stroke: '#A9413B',
//                        'stroke-width': 2
//                    }
//                }
//            ]
//        });
//
//        Ext.create('Ext.Window', {
//            x: 0,
//            y: winHeight * 0.38,
//            width: '50%',
//            height: '62%',
//            hidden: false,
//            maximizable: true,
//            title: 'Trend',
//            constrain: true,
//            renderTo: Ext.getBody(),
//            layout: 'fit',
//            items: chart,
//            closable: false,
//            draggable: false
//        });
    }

    function createChart2(result) {
        var max = 0;
        for (var i = 0; i < result.length; i++) {
            max = max < result[i][2] ? result[i][2] : max;
        }
        max = Math.ceil(max * 1.1);

        var myStore = Ext.create('Ext.data.JsonStore', {
            fields: ['0', '1', '2'],
            data: result
        });

        var chart = Ext.create('Ext.chart.Chart', {
            id: 'dispatch_chart2',
            style: 'background:#fff',
            animate: true,
            store: myStore,
            shadow: true,
            theme: 'Category1',
            legend: {
                position: 'right'
            },
            axes: [
                {
                    title: 'dispatch report',
                    type: 'Numeric',
                    position: 'left',
                    minimum: 0,
                    maximum: max,
                    label: {
                        renderer: Ext.util.Format.numberRenderer('0,0'),
                        font: '10px Arial'
                    }
                },
                {
                    title: 'Years',
                    type: 'Category',
                    position: 'bottom',
                    fields: ['1'],
                    grid: true,
                    label: {
                        font: '11px Arial',
                        renderer: function (name) {
                            return name;
                        },
                        rotate: {
                            degrees: -45
                        }
                    }
                }
            ],
            series: [
                {
                    title: 'XRA',
                    type: 'column',
                    axis: 'left',
                    highlight: true,
//                    smooth: true,
                    xField: '1',
                    yField: '2',
                    style: {
                        fill: '#A9413B',
                        stroke: '#A9413B',
                        'stroke-width': 2
                    }
                }
            ]
        });

        Ext.create('Ext.Window', {
            x: winWidth * 0.5,
            y: winHeight * 0.38,
            width: '50%',
            height: '62%',
            hidden: false,
            maximizable: true,
            title: 'Trend',
            constrain: true,
            renderTo: Ext.getBody(),
            layout: 'fit',
            items: chart,
            closable: false,
            draggable: false
        });
    }

    function initTable(result) {
        var table = document.createElement("table");
        table.align = "left";
        table.style.border = "solid dashed #157fcc";
        table.style.height = "15%";
        table.setAttribute("id", "tblContent");

        var tr;
        var td;
        var yearsTmp = [];
        var nameTmp = [];

        for (var i = 0; i < result.length; i++) {
            yearsTmp[i] = result[i][0];
            nameTmp[i] = result[i][1];
        }

        var years = unique(yearsTmp);
        var names = unique(nameTmp);

        tr = document.createElement("tr");
        tr.style.backgroundColor = "#4792c8";
        td = document.createElement("td");
        td.innerHTML = "Dispatch";
        td.style.textAlign = "center";
        td.style.color = "#ffffff";
        tr.appendChild(td);
        for (i = 0; i < years.length; i++) {
            td = document.createElement("td");
            td.innerHTML = years[i];
            td.style.width = "100px";
            td.style.color = "#ffffff";
            td.style.textAlign = "center";
            tr.appendChild(td);
        }
        table.appendChild(tr);

        for (var n = 0; n < names.length; n++) {
            tr = document.createElement("tr");
            tr.style.backgroundColor = "#ffffff";
            td = document.createElement("td");
            td.innerHTML = names[n];
            td.style.textAlign = "center";
            td.style.color = "#4792c8";
            tr.appendChild(td);
            for (i = 0; i < years.length; i++) {
                td = document.createElement("td");
                for (var j = 0; j < result.length; j++) {
                    if (result[j][0] == years[i]) {
                        if (result[j][1] == names[n]) {
                            td.innerHTML = result[n][2];
                        }
                    }
                }
                td.style.width = "100px";
                td.style.textAlign = "center";
                tr.appendChild(td);
            }
            table.appendChild(tr);
        }
        document.getElementById("div2").appendChild(table);
    }

    function unique(arr) {
        var tmp = [];
        for (var i in arr) {
            if (tmp.indexOf(arr[i]) == -1) {
                tmp.push(arr[i]);
            }
        }
        return tmp;
    }

    Ext.require('Ext.chart.*');
    Ext.require(['Ext.Window', 'Ext.fx.target.Sprite', 'Ext.layout.container.Fit', 'Ext.window.MessageBox']);

    Ext.onReady(getDispatchReport);

</script>
</body>
</html>
