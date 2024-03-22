/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author M S I
 */
public class JDBC {
    public static Connection getCon(){
        Connection c = null;
        try{
            DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
            String url = "jdbc:sqlserver://MSI\\SQLEXPRESS:1433;databaseName=pikachu;encrypt=true;trustServerCertificate=true";
            c = DriverManager.getConnection(url,"sa","sa1105");
            System.out.println("OK");
            return c;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return c;
    }
    
    public static void closeCon(Connection c){
        if(c!=null){
            try {
                c.close();
            } catch (SQLException ex) {
                Logger.getLogger(JDBC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    

    
}


