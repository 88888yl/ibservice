package cso.servlet;

import oracle.jdbc.OracleConnection;
import utils.GlobalVariables;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by myl on 14-11-10.
 */
public class DeleteCSOServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            Class.forName(GlobalVariables.oracleDriver);
            Connection conn= DriverManager.getConnection(GlobalVariables.oracleUrl, GlobalVariables.oracleUserName, GlobalVariables.oraclePassword);
            OracleConnection oconn=(OracleConnection)conn;
            oconn.setImplicitCachingEnabled(true);
            Statement stmt=conn.createStatement();
            stmt.executeUpdate("delete from cso");
            stmt.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
