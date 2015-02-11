<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/1/26
  Time: 15:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Complaints Table</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="ext_js/bootstrap.js"></script>
    <script type="text/javascript" src="ext_js/locale/ext-lang-en.js"></script>
    <script type="text/javascript" src="ext_js/myExporter/export-all.js"></script>

    <link rel="stylesheet" type="text/css" href="ext_js/resources/css/ext-all-neptune.css"/>
</head>
<body style="background-color: #a9a9a9; border: 0">
<style>
    tr.x-grid-record-grey .x-grid-td {
        background: #eaeaea;
    }
</style>

<script type="text/javascript">
    Ext.Loader.setConfig({
        enabled: true
    });

    Ext.Loader.setPath('Ext.ux.exporter', '/ext_js/myExporter/exporter');

    Ext.require([
        'Ext.toolbar.*',
        'Ext.grid.*',
        'Ext.data.*',
        'Ext.selection.CheckboxModel',
        'Ext.ux.exporter.Exporter',
        'Ext.chart.*',
        'Ext.Window',
        'Ext.fx.target.Sprite',
        'Ext.layout.container.Fit',
        'Ext.window.MessageBox'
    ]);

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

    function getParameter(paraName) {
        var sUrl = location.href + "?para1=invest&para2=dollar";
        var reg = "(?:\\?|&){1}" + paraName + "=([^&]*)";
        var regresult = new RegExp(reg, "gi");
        regresult.exec(sUrl);
        return RegExp.$1;
    }

    function getIPTable() {
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'ipAddressInfo.action',
            success: function (result) {
                if (result == "fail") {
                    Ext.MessageBox.alert("Error Info", "No IP address found!");
                } else {
                    result = eval("(" + result + ")");
                    initTable(result);
                }
            }
        });
    }

    function initTable(result) {

        Ext.define('IP', {
            extend: 'Ext.data.Model',
            fields: result[0]
        });

        Ext.grid.myData = result[2];

        var getLocalStore = function () {
            return Ext.create('Ext.data.ArrayStore', {
                model: 'IP',
                data: Ext.grid.myData
            });
        };

        var myGrid = Ext.create('Ext.grid.Panel', {
            id: 'myIP',
            store: getLocalStore(),
            forceFit: true,
            columns: result[1],
            columnLines: true,
            enableLocking: true,
            x: 0,
            y: 0,
            width: '20%',
            height: winHeight,
            collapsible: false,
            animCollapse: false,
            title: 'Access IP grid info',
            iconCls: 'icon-grid',
            margin: '0 0 0 0',
            viewConfig: {
                getRowClass: changeRowClass,
                stripeRows: true,
                enableTextSelection: true
            },
            listeners: {
                celldblclick: function (view, cell, cellIndex, record, row, rowIndex, e) {
                    var clickedDataIndex = view.panel.headerCt.getHeaderAtIndex(cellIndex).dataIndex;
                    var clickedColumnName = view.panel.headerCt.getHeaderAtIndex(cellIndex).text;
                    var clickedCellValue = record.get(clickedDataIndex);
                    Ext.MessageBox.alert('Detailed', clickedCellValue);
                }
            },
            selModel: Ext.create('Ext.selection.Model', {listeners: {}}),
            renderTo: Ext.getBody()
        });
    }

    function changeRowClass(record, rowIndex, rowParams, store) {
        if (rowIndex % 2 == 0) {
            return 'x-grid-record-grey';
        }
    }

    function getGridStore() {
        return Ext.getCmp('myIPGrid').getStore().getProxy().getReader().rawData;
    }

    function createDayChart() {
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'dayAccess.action',
            success: function (result) {
                if (result == "fail") {
                    alert("Report is not found!");
                } else {
                    result = eval("(" + result + ")");
                    myDayChart(result);
                }
            },
            error: function () {
                $("#result").html("Receive failed!");
            }
        });
    }

    function createTotalChart() {
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'totalAccess.action',
            success: function (result) {
                if (result == "fail") {
                    alert("Report is not found!");
                } else {
                    result = eval("(" + result + ")");
                    myTotalChart(result);
                }
            },
            error: function () {
                $("#result").html("Receive failed!");
            }
        });
    }

    function myDayChart(result) {
        var max = 0;
        for (var i = 0; i < result.length; i++) {
            result[i][1] = parseInt(result[i][1]);
            max = max < result[i][1] ? result[i][1] : max;
        }
        max = Math.ceil(max * 1.1);

        var myStore = Ext.create('Ext.data.JsonStore', {
            fields: ['0', '1'],
            data: result
        });
        var title = "During the last 30 days statistic";

        var chart = Ext.create('Ext.chart.Chart', {
            id: 'day_chart',
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
                    title: 'statistic report',
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
                    title: title,
                    type: 'Category',
                    position: 'bottom',
                    fields: ['0'],
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
                    title: 'last 30 days access',
                    type: 'line',
                    xField: '0',
                    yField: '1',
                    style: {
                        fill: '#A9413B',
                        stroke: '#A9413B',
                        'stroke-width': 2
                    },
                    markerConfig: {
                        type: 'circle',
                        size: 3,
                        radius: 3,
                        'stroke-width': 0,
                        fill: '#A9413B',
                        stroke: '#A9413B'
                    }
                }]
        });

        Ext.create('Ext.Window', {
            x: winWidth * 0.2,
            y: 0,
            width: '80%',
            height: winHeight * 0.5,
            hidden: false,
            maximizable: true,
            title: 'Trend',
            constrain: true,
            renderTo: Ext.getBody(),
            layout: 'fit',
            items: chart,
            closable: false,
            draggable: false,
            resizable: false
        });
    }

    function myTotalChart(result) {
        var max = 0;
        for (var i = 0; i < result.length; i++) {
            result[i][1] = parseInt(result[i][1]);
            max = max < result[i][1] ? result[i][1] : max;
        }
        max = Math.ceil(max * 1.1);

        var myStore = Ext.create('Ext.data.JsonStore', {
            fields: ['0', '1'],
            data: result
        });
        var title = "Total statistic";

        var colors = ['#357F51'];
        Ext.define('Ext.chart.theme.MyFancy', {
            extend: 'Ext.chart.theme.Base',
            constructor: function (config) {
                this.callParent([Ext.apply({
                    colors: colors
                }, config)]);
            }
        });

        var chart = Ext.create('Ext.chart.Chart', {
            id: 'total_chart',
            style: 'background:#fff',
            animate: true,
            store: myStore,
            shadow: true,
            theme: 'MyFancy',
            legend: {
                position: 'right'
            },
            axes: [
                {
                    title: 'statistic report',
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
                    title: title,
                    type: 'Category',
                    position: 'bottom',
                    fields: ['0'],
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
                    title: 'all access',
                    type: 'line',
                    xField: '0',
                    yField: '1',
                    style: {
                        fill: '#A28339',
                        stroke: '#A28339',
                        'stroke-width': 2
                    },
                    markerConfig: {
                        type: 'circle',
                        size: 3,
                        radius: 3,
                        'stroke-width': 0,
                        fill: '#A28339',
                        stroke: '#A28339'
                    }
                }]
        });

        Ext.create('Ext.Window', {
            x: winWidth * 0.2,
            y: winHeight * 0.5,
            width: '80%',
            height: winHeight * 0.5,
            hidden: false,
            maximizable: true,
            title: 'Trend',
            constrain: true,
            renderTo: Ext.getBody(),
            layout: 'fit',
            items: chart,
            closable: false,
            draggable: false,
            resizable: false
        });
    }

    Ext.onReady(getIPTable);
    Ext.onReady(createDayChart);
    Ext.onReady(createTotalChart);
</script>
</body>
</html>
