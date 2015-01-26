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
</head>
<body style="background-color: #a9a9a9; border: 0">
<div id="div2" style="overflow-y: auto; overflow-x: auto; margin: 10px 28px 0 28px; background-color: #a9a9a9">
</div>
<script type="text/javascript">
  var winWidth;
  var winHeight;
  var Id = getParameter("id").split("?")[0];

  if (window.innerWidth)
    winWidth = window.innerWidth;
  else if ((document.body) && (document.body.clientWidth))
    winWidth = document.body.clientWidth;

  if (window.innerHeight)
    winHeight = window.innerHeight;
  else if ((document.body) && (document.body.clientHeight))
    winHeight = document.body.clientHeight;

  getComplaintsTable();

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
        id: Id
      },
      success: function (result) {
        if (result == "fail") {
          alert("ID is not found!");
        } else {
          result = eval("(" + result + ")");
          console.log(result);
        }
      }
    });
  }
</script>
</body>
</html>
