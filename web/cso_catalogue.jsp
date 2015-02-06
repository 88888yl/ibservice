<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/2/6
  Time: 18:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>CSO Catalogue</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="ext_js/bootstrap.js"></script>
    <script type="text/javascript" src="ext_js/locale/ext-lang-en.js"></script>


    <link rel="stylesheet" type="text/css" href="ext_js/resources/css/ext-all-neptune.css"/>
</head>
<body style="background-color: #a9a9a9; border: 0">
<style>
    tr.x-grid-record-grey .x-grid-td {
        background: #eaeaea;
    }
</style>

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

    function getCSOCatalogue() {
        $.ajax({
            type: 'post',
            dataType: 'text',
            async: true,
            url: 'csoCatalogue.action',
            success: function (result) {
                if (result == "fail") {
                    alert("Catalogue is not found!");
                } else {
                    result = eval("(" + result + ")");
                    initCatalogue(result);
                }
            }
        });
    }

    function changeRowClass(record, rowIndex, rowParams, store) {
        if (rowIndex % 2 == 0) {
            return 'x-grid-record-grey';
        }
    }

    function getSearchValues() {
        var data = Ext.getCmp('cso_catalogue').getStore().getSource();
        return JSON.stringify(data);
    }

    function initCatalogue(result) {
        Ext.create('Ext.grid.property.Grid', {
            id: 'cso_catalogue',
            title: 'Search Catalogue',
            width: '100%',
            height: winHeight - 95,
            x: 0,
            y: '95px',
            renderTo: Ext.getBody(),
            source: result[0],
            listeners: {
                render: function (grid) {
                    var gridColumns = grid.headerCt.getGridColumns();
                    gridColumns[0].width = 200;

                }
            },
            viewConfig: {
                getRowClass: changeRowClass,
                stripeRows: true,
                enableTextSelection: true
            }
        });
    }
    Ext.onReady(getCSOCatalogue);
</script>
</body>
</html>
