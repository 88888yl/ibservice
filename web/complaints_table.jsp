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
        'Ext.ux.exporter.Exporter'
    ]);

    var winWidth;
    var winHeight;
    var searchKeys = parent.document.getElementById("tmpValue").innerText;
    var totalResult = [];

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

    function getComplaintsTable() {
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'complaintsSearch.action',
            data: {
                searchKeys: searchKeys
            },
            success: function (result) {
                if (result == "fail") {
                    Ext.MessageBox.alert("Error Info", "SearchKeys is empty or not found!");
                } else {
                    result = eval("(" + result + ")");
                    totalResult = result[2];
                    initTable(result);
                }
            }
        });
    }

    function initTable(result) {
        if (result[2].length > 200) {
            Ext.MessageBox.alert("Warning", "Result table is too large, please export to excel!");
        } else {
            Ext.define('Complaints', {
                extend: 'Ext.data.Model',
                fields: result[0]
            });

            Ext.grid.myData = result[2];

            var getLocalStore = function () {
                return Ext.create('Ext.data.ArrayStore', {
                    model: 'Complaints',
                    data: Ext.grid.myData
                });
            };

            var myGrid = Ext.create('Ext.grid.Panel', {
                id: 'myComplaintsGrid',
                store: getLocalStore(),
                columns: result[1],
                columnLines: true,
                enableLocking: true,
                x: 0,
                y: '95px',
                width: '100%',
                height: winHeight - 95,
                collapsible: false,
                animCollapse: false,
                title: 'Complaints grid info',
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
    }

    function changeRowClass(record, rowIndex, rowParams, store) {
        if (rowIndex % 2 == 0) {
            return 'x-grid-record-grey';
        }
    }

    function getGridStore() {
        return totalResult;
    }

    Ext.onReady(getComplaintsTable);
</script>
</body>
</html>
