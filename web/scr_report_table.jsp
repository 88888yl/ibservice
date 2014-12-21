<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2014/12/14
  Time: 21:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>scr report</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
</head>
<body style="background-color: #a9a9a9; border: 0" id="myScreen">
<div id="div2" style="overflow-y: auto; overflow-x: auto; margin: 25px 25px 0 25px; background-color: #a9a9a9">
</div>
<link rel="stylesheet" type="text/css" href="ext_js/resources/css/ext-all-neptune.css"/>
<script type="text/javascript" src="ext_js/bootstrap.js"></script>
<script type="text/javascript" src="ext_js/locale/ext-lang-en.js"></script>
<script type="text/javascript">
var winWidth;
var winHeight;

if (window.innerWidth)
    winWidth = window.innerWidth;
else if ((document.body) && (document.body.clientWidth))
    winWidth = document.body.clientWidth;

if (window.innerHeight)
    winHeight = window.innerHeight;
else if ((document.body) && (document.body.clientHeight))
    winHeight = document.body.clientHeight;

getSCRReport();

function getSCRReport() {
    $.ajax({
        type: 'post',
        dataType: 'text',
        async: true,
        url: 'scrreport.action',
        success: function (result) {
            if (result == "fail") {
                alert("Report is not found!");
            } else {
                result = eval("(" + result + ")");
                initTable(result);
            }
        },
        error: function () {
            $("#result").html("Receive failed!");
        }
    });
}

function initTable(result) {
    var table = document.createElement("table");
    table.align = "center";
    table.style.border = "solid dashed #157fcc";
    table.style.width = "100%";
    table.style.height = "15%";
    table.setAttribute("id", "tblContent");
    var tr;
    var td;
    var len = result.length;
    var i;

    tr = document.createElement("tr");
    tr.style.backgroundColor = "#4792c8";
    td = document.createElement("td");
    td.innerHTML = "Matrix";
    td.style.textAlign = "center";
    td.style.color = "#ffffff";
    tr.appendChild(td);
    td = document.createElement("td");
    td.innerHTML = "GOAL";
    td.style.width = "80px";
    td.style.textAlign = "center";
    td.style.color = "#ffffff";
    tr.appendChild(td);
    for (i = len - 12; i < len; i++) {
        td = document.createElement("td");
        td.innerHTML = "FW" + result[i][1];
        td.style.width = "100px";
        td.style.color = "#ffffff";
        td.style.textAlign = "center";
        tr.appendChild(td);
    }
    table.appendChild(tr);

    tr = document.createElement("tr");
    tr.style.backgroundColor = "#ffffff";
    td = document.createElement("td");
    td.innerHTML = "# All Open SCR";
    td.noWrap = true;
    td.style.textAlign = "right";
    tr.appendChild(td);
    td = document.createElement("td");
    td.innerHTML = "0";
    td.style.width = "100px";
    td.style.textAlign = "right";
    tr.appendChild(td);
    for (i = len - 12; i < len; i++) {
        td = document.createElement("td");
        td.innerHTML = result[i][2];
        td.style.width = "80px";
        td.style.textAlign = "right";
        tr.appendChild(td);
    }
    table.appendChild(tr);

    tr = document.createElement("tr");
    tr.style.backgroundColor = "#ffffff";
    td = document.createElement("td");
    td.innerHTML = "# SCR's >60 Days";
    td.noWrap = true;
    td.style.textAlign = "right";
    tr.appendChild(td);
    td = document.createElement("td");
    td.innerHTML = "0";
    td.style.width = "100px";
    td.style.textAlign = "right";
    tr.appendChild(td);
    for (i = len - 12; i < len; i++) {
        td = document.createElement("td");
        td.innerHTML = result[i][3];
        td.style.width = "80px";
        td.style.textAlign = "right";
        tr.appendChild(td);
    }
    table.appendChild(tr);

    tr = document.createElement("tr");
    tr.style.backgroundColor = "#ffffff";
    td = document.createElement("td");
    td.innerHTML = "New Open This Week";
    td.noWrap = true;
    td.style.textAlign = "right";
    tr.appendChild(td);
    td = document.createElement("td");
    td.innerHTML = "0";
    td.style.width = "100px";
    td.style.textAlign = "right";
    tr.appendChild(td);
    for (i = len - 12; i < len; i++) {
        td = document.createElement("td");
        td.innerHTML = result[i][4];
        td.style.width = "80px";
        td.style.textAlign = "right";
        tr.appendChild(td);
    }
    table.appendChild(tr);

    tr = document.createElement("tr");
    tr.style.backgroundColor = "#ffffff";
    td = document.createElement("td");
    td.innerHTML = "New Closed This Week";
    td.noWrap = true;
    td.style.textAlign = "right";
    tr.appendChild(td);
    td = document.createElement("td");
    td.innerHTML = "0";
    td.style.width = "80px";
    td.style.textAlign = "right";
    tr.appendChild(td);
    for (i = len - 12; i < len; i++) {
        td = document.createElement("td");
        td.innerHTML = result[i][5];
        td.style.width = "100px";
        td.style.textAlign = "right";
        tr.appendChild(td);
    }
    table.appendChild(tr);

    tr = document.createElement("tr");
    tr.style.backgroundColor = "#ffffff";
    td = document.createElement("td");
    td.innerHTML = "P95 All Open CSO";
    td.noWrap = true;
    td.style.textAlign = "right";
    tr.appendChild(td);
    td = document.createElement("td");
    td.innerHTML = "0";
    td.style.width = "80px";
    td.style.textAlign = "right";
    tr.appendChild(td);
    for (i = len - 12; i < len; i++) {
        td = document.createElement("td");
        td.innerHTML = result[i][6];
        td.style.width = "100px";
        td.style.textAlign = "right";
        tr.appendChild(td);
    }
    table.appendChild(tr);

    tr = document.createElement("tr");
    tr.style.backgroundColor = "#ffffff";
    td = document.createElement("td");
    td.innerHTML = "P50 SCR";
    td.noWrap = true;
    td.style.textAlign = "right";
    tr.appendChild(td);
    td = document.createElement("td");
    td.innerHTML = "0";
    td.style.width = "80px";
    td.style.textAlign = "right";
    tr.appendChild(td);
    for (i = len - 12; i < len; i++) {
        td = document.createElement("td");
        td.innerHTML = result[i][7];
        td.style.width = "100px";
        td.style.textAlign = "right";
        tr.appendChild(td);
    }
    table.appendChild(tr);

    document.getElementById("div2").appendChild(table);
}

Ext.require('Ext.chart.*');
Ext.require(['Ext.Window', 'Ext.fx.target.Sprite', 'Ext.layout.container.Fit', 'Ext.window.MessageBox']);

function createChart() {
    $.ajax({
        type: 'post',
        dataType: 'text',
        async: true,
        url: 'scrreport.action',
        success: function (result) {
            if (result == "fail") {
                alert("Report is not found!");
            } else {
                result = eval("(" + result + ")");
                myChart(result);
            }
        },
        error: function () {
            $("#result").html("Receive failed!");
        }
    });
}

function myChart(result) {
    var list_2009 = [];
    var list_2010 = [];
    var list_2011 = [];
    var list_2012 = [];
    var list_2013 = [];
    var list_2014 = [];
    var list_2015 = [];
    var n09 = 0;
    var n10 = 0;
    var n11 = 0;
    var n12 = 0;
    var n13 = 0;
    var n14 = 0;
    var n15 = 0;
    for (var i = 0; i < result.length; i++) {
        var fw_list = [];
        for (var j = 0; j < result[i].length; j++) {
            fw_list[j] = result[i][j];
        }
        if (fw_list[0] == "2009") {
            list_2009[n09] = fw_list;
            n09++;
        }
        if (fw_list[0] == "2010") {
            list_2010[n10] = fw_list;
            n10++;
        }
        if (fw_list[0] == "2011") {
            list_2011[n11] = fw_list;
            n11++;
        }
        if (fw_list[0] == "2012") {
            list_2012[n12] = fw_list;
            n12++;
        }
        if (fw_list[0] == "2013") {
            list_2013[n13] = fw_list;
            n13++;
        }
        if (fw_list[0] == "2014") {
            list_2014[n14] = fw_list;
            n14++;
        }
        if (fw_list[0] == "2015") {
            list_2015[n15] = fw_list;
            n15++;
        }
    }
    chart20xx(list_2013);
}

function chart20xx(list) {
    var myStore = Ext.create('Ext.data.JsonStore', {
        fields: ['0', '1', '2', '3', '4', '5', '6', '7'],
        data: list
    });

    var chart = Ext.create('Ext.chart.Chart', {
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
                title: 'scr report',
                type: 'Numeric',
                position: 'left',
                fields: ['2'],
                minimum: 0,
                label: {
                    renderer: Ext.util.Format.numberRenderer('0,0'),
                    font: '10px Arial'
                }
            },
            {
                title: 'FW of 2013',
                type: 'Category',
                position: 'bottom',
                fields: ['1'],
                grid: true,
                label: {
                    font: '11px Arial',
                    renderer: function (name) {
                        return "FW" + name;
                    },
                    rotate: {
                        degrees: -45
                    }
                }
            }
        ],
        series: [
            {
                title: 'Total Open SCR',
                type: 'line',
                xField: '1',
                yField: '2',
                tips: {
                    trackMouse: true,
                    width: 150,
                    height: 40,
                    renderer: function (storeItem) {
                        this.setTitle("FW" + storeItem.get('1'));
                        this.update("Total Open CSO: " + storeItem.get('2'));
                    }
                },
                style: {
                    fill: '#D69927',
                    stroke: '#D69927',
                    'stroke-width': 2
                },
                markerConfig: {
                    type: 'circle',
                    size: 3,
                    radius: 3,
                    'stroke-width': 0,
                    fill: '#D69927',
                    stroke: '#D69927'
                }
            },
            {
                title: '# SCR\'s >60 Days',
                type: 'column',
                axis: 'left',
                highlight: true,
                xField: '1',
                yField: '3',
                renderer: function (sprite, record, attr, index, store) {
                    return Ext.apply(attr, {
                        fill: '#A95B5B'
                    });
                },
                tips: {
                    trackMouse: true,
                    width: 170,
                    height: 40,
                    renderer: function (storeItem) {
                        this.setTitle("FW" + storeItem.get('1'));
                        this.update("# SCR\'s >60 Days: " + storeItem.get('3'));
                    }
                }
            },
            {
                title: 'New Open This Week',
                type: 'line',
                xField: '1',
                yField: '4',
                tips: {
                    trackMouse: true,
                    width: 180,
                    height: 40,
                    renderer: function (storeItem) {
                        this.setTitle("FW" + storeItem.get('1'));
                        this.update("New Open This Week: " + storeItem.get('4'));
                    }
                },
                style: {
                    fill: '#D69927',
                    stroke: '#D69927',
                    'stroke-width': 2
                },
                markerConfig: {
                    type: 'circle',
                    size: 3,
                    radius: 3,
                    'stroke-width': 0,
                    fill: '#D69927',
                    stroke: '#D69927'
                }
            },
            {
                title: 'New Closed This Week',
                type: 'line',
                xField: '1',
                yField: '5',
                tips: {
                    trackMouse: true,
                    width: 180,
                    height: 40,
                    renderer: function (storeItem) {
                        this.setTitle("FW" + storeItem.get('1'));
                        this.update("New Closed This Week: " + storeItem.get('5'));
                    }
                },
                style: {
                    fill: '#574D48',
                    stroke: '#574D48',
                    'stroke-width': 2
                },
                markerConfig: {
                    type: 'circle',
                    size: 3,
                    radius: 3,
                    'stroke-width': 0,
                    fill: '#574D48',
                    stroke: '#574D48'
                }
            }
        ]
    });

    Ext.create('Ext.Window', {
        x: winWidth * 0.02,
        y: winHeight * 0.35,
        width: '96%',
        height: '65%',
        hidden: false,
        maximizable: true,
        title: 'Trend',
        constrain: true,
        renderTo: Ext.getBody(),
        layout: 'fit',
        items: chart,
        closable: false,
        draggable: false,
        tbar: [
            {
                text: 'Save Chart',
                handler: function () {
                    chart.save({
                        type: 'image/png'
                    });
                }
            }
        ]
    });
}

Ext.onReady(createChart);

</script>
</body>
</html>
