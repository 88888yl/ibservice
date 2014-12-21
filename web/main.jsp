<%--
  Created by IntelliJ IDEA.
  User: myl
  Date: 14-11-7
  Time: 11:05 am
--%>
<%--<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">--%>
<html>
<head>
    <title>IB Service</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="ie/first-load-script.js"></script>
    <script type="text/javascript">
        function getUserIP() {
            var elem = document.getElementById("tab3");
            changeTab3(elem);
            $.ajax({
                type: 'post',
                async: true,
                url: 'get_ip.action'
            })
        }
    </script>
</head>

<body onload="getUserIP();" style="margin: 0; padding:0; background-color: darkgray; border: 0">

<nav class="navbar navbar-fixed-top navbar-inverse" role="navigation" style="box-shadow: 0 3px 6px black">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                    aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" style="font-size: large; color: papayawhip">IB Service</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li id="tab3" onclick="changeTab3(this);"><a href="#">PartFinder</a></li>
                <li id="tab1" onclick="changeTab1(this);"><a href="#">CSO</a></li>
                <li id="tab5" onclick="changeTab5(this);"><a href="#">CSO Report</a></li>
                <li id="tab2" onclick="changeTab2(this);"><a href="#">SCR</a></li>
                <%--<li id="tab4" onclick="changeTab4(this);"><a>Chart</a></li>--%>
                <li id="tab6" onclick="changeTab6(this);"><a href="#">SCR Report</a></li>
                <%--<li id="tab7" onclick="changeTab7(this);"><a>Access Statistics</a></li>--%>
            </ul>
        </div>
    </div>
</nav>

<div id="tab" style="margin-top: 48px"></div>

<script type="text/javascript">
    if (window.ActiveXObject) {
        $("#tab").css({'height': '750px', 'background-color': 'darkgray'});
    }

    function changeTab1(elem) {
        $("#tab").load("cso.jsp");
        changeTabColor(elem);
    }
    function changeTab2(elem) {
        $("#tab").load("scr.jsp");
        changeTabColor(elem);
    }
    function changeTab3(elem) {
        $("#tab").load("update.jsp");
        changeTabColor(elem);
    }
    function changeTab4(elem) {
        $("#tab").load("chart.jsp");
        changeTabColor(elem);
    }
    function changeTab5(elem) {
        document.getElementById("tab").innerHTML =
                '<iframe name="file_frame" width="100%" height="95%" src="report1.jsp" ' +
                'style="margin: 0; padding:0; background-color: darkgray; border: 0"></iframe>';
        changeTabColor(elem);
    }
    function changeTab6(elem) {
        document.getElementById("tab").innerHTML =
                '<iframe name="file_frame" width="100%" height="95%" src="report2.jsp" ' +
                'style="margin: 0; padding:0; background-color: darkgray; border: 0"></iframe>';
        changeTabColor(elem);
    }
    function changeTab7(elem) {
        $("#tab").load("access.jsp");
        changeTabColor(elem);
    }
    function changeTabColor(elem) {
        var className = 'active';
        var tab1 = document.getElementById('tab1');
        //var tab2 = document.getElementById('tab2');
        var tab3 = document.getElementById('tab3');
        //var tab4 = document.getElementById('tab4');
        var tab5 = document.getElementById('tab5');
        //var tab6 = document.getElementById('tab6');
        //var tab7 = document.getElementById('tab7');

        tab1.removeAttribute('class');
        tab2.removeAttribute('class');
        tab3.removeAttribute('class');
        //tab4.removeAttribute('class');
        tab5.removeAttribute('class');
        tab6.removeAttribute('class');
        //tab7.removeAttribute('class');

        elem.setAttribute('class', className);
    }
</script>
</body>
</html>