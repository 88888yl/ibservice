<%--
  Created by IntelliJ IDEA.
  User: myl
  Date: 14-10-19
  Time: 下午2:51
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<html>
<head>
    <title>PartFinder</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="ie/first-load-script.js"></script>
    <script type="text/javascript">
        $.getScript("ie/table-init.js");
    </script>
</head>
<body id="body" style="margin: 0 0 0 30px; padding:0; background-color: darkgray; border: 0">
<div style="position:absolute; height:15px;">
    <div style="margin: 15px 15px 15px 0; width: 180px; box-shadow: 0 3px 6px grey" class="input-group">
        <span class="input-group-addon" style="font-size: 22px; font: bold;">
            <span class="glyphicon glyphicon-bookmark"></span>
            Where Use Info:
        </span>
        <span class="input-group-addon" id="table1"></span>
    </div>
</div>

<div id="div1" style="position:absolute; height:89%; top: 80px; overflow-y: auto">
</div>
</body>
</html>
