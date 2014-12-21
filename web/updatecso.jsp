<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.ResultSetMetaData"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   	<%
   		Class.forName("oracle.jdbc.driver.OracleDriver");
   		Connection conn=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl","ta4_1","ta4_1");
   		Statement stmt=conn.createStatement();
   		String sql="update CSO set csostartdate='"
   			+request.getParameter("csostartdate")
   			+"',milestonestatus='"
   			+request.getParameter("milestonestatus")
   			+"',nextaction='"
   			+request.getParameter("nextaction")
   			+"',closedate=to_date('"+request.getParameter("closedate")
   			+"','yyyy-mm-dd'),productline='"+request.getParameter("productline")
   			+"'  where csonumber='"+request.getParameter("csonumber")+"'";
   		System.out.println(sql);
   		stmt.executeUpdate(sql);
   		stmt.close();
   		conn.close();
   	%>
   	<script>
   		parent.location.reload();
   	</script>
</html>
