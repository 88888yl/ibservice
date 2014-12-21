<%@ page import="java.util.*" language="java" contentType="text/html; charset=gb2312" %>
<%@ page import="partfinder.build.Entry" %>
<jsp:useBean id="tree" class="partfinder.build.TreeBuilder" scope="page"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<html>
<head>
    <title>PartFinder</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <script type="text/javascript" src="js/jquery-1.11.1.min.js"></script>
    <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="ie/first-load-script.js"></script>
    <script type="text/javascript" src="js/tree/tree.js"></script>
    <link rel="stylesheet" href="js/tree/tree.css">
</head>

<body id="body" style="margin: 10px 0 0 30px; padding:0; background-color: darkgray; border: 0">

<div style="position: fixed; right: 60px; top: 30px;" class="input-group">
    <input type="button" style="box-shadow: 0 3px 6px grey" class="btn btn-danger" value="ExpandAll"
           onclick="expand();">
    &nbsp;&nbsp;&nbsp;
    <input type="button" style="box-shadow: 0 3px 6px grey" class="btn btn-danger" value="CollapseAll"
           onclick="TreeView.backExpandAll()">
</div>

<div id="loading" style="width:100%;height:100%;position:absolute;display:none">
    <table align="center">
        <tr>
            <td><img src="./js/tree/loading.gif"></td>
        </tr>
    </table>
</div>

<script language=JavaScript>
    buildTree();

    function expand() {
        document.getElementById("loading").style.display = "";
        TreeView.expandAll();
        document.getElementById("loading").style.display = "none";
    }

    function buildTree() {
        <%
        String tableName = request.getParameter("tableName");
        String partNumber = request.getParameter("partnumber");
        String description = request.getParameter("description");
        String mep = request.getParameter("mep");
        String rdo = request.getParameter("rdo");
        String isChildren = request.getParameter("isChildren");
        String isAllInfo = request.getParameter("isAllInfo");
        %>

        window.TreeView = new MzTreeView("TreeView");
        TreeView.setIconPath("./js/tree/");
        TreeView.nodes["root_root0"] = "text@ ROOT"; //PT800+ BOM


        <%
        if (tableName != null) {
        tree.setTableName(tableName);
        tree.setPartNumber(partNumber);
        tree.setDescription(description);
        tree.setMep(mep);
        tree.setRdo(rdo);
        if (isChildren != null && isChildren.equals("true"))
            tree.setChildren(true);
        if (isAllInfo != null && isAllInfo.equals("true"))
            tree.setAllInfo(true);

        List<List<Entry>> treeLists = tree.getTreeLists();
            for (int n = 0; n < treeLists.size(); n++) {
                List<Map<String,String>> nodeList = tree.getTreeNodeList(treeLists, n);
                for(int i=0;i<nodeList.size();i++){
                %>
        TreeView.nodes["<%=nodeList.get(i).get("parentid") %>_<%=nodeList.get(i).get("id") %>"]
                = 'text@<%=nodeList.get(i).get("text") %>';
        <%}%>
        <%}%>
        document.write(TreeView.toString());
        <%
        if (tableName != null || partNumber != null || description != null) {
            %>expand();
        <%
                }
        if (partNumber != null) {
            if (!partNumber.equals("")) System.out.println(partNumber);
            %>document.getElementById("partnumber").value = "<%=partNumber%>";

        <%}
        if (description != null) {
            if (!description.equals("")) System.out.println(description);
           %>document.getElementById("description").value = "<%=description%>";
        <%}
        }
        if (mep != null) {
            if (!mep.equals("")) System.out.println(mep);
             %>document.getElementById("mep").value = "<%=mep%>";
        <%}
        if (rdo != null) {
            if (!rdo.equals("")) System.out.println(rdo);
            %>document.getElementById("rdo").value = "<%=rdo%>";
        <%}
        %>
    }
</script>
</body>
</html>