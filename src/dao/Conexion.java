/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author DELL
 */
public class Conexion {
    static Logger logger = Logger.getLogger((Class)Conexion.class);
    
    Properties prop;
    
    public Driver objDriver;
    public DriverManager objDriverManager;
    
    String url; 
    String user; 
    String password;
    
    private static Conexion instancia;
    
    private Conexion(){
        
    }
    
    public static Conexion getInstancia(){
        if(instancia==null){
          instancia=new Conexion();  
        }            
        return instancia;
    }
    
    public void getCadenaConex(){
        try{
            FileReader reader = new FileReader("/home/ubuntu/task/mes/taskmes.properties");
            prop = new Properties();
            prop.load(reader);
            url = prop.getProperty("mowa.url");
            user = prop.getProperty("mowa.user");
            password = prop.getProperty("mowa.password");
        }catch(IOException ex){
            logger.info((Object)("Error " + ex.getMessage()));
            System.exit(0);    
        }
    }
    
    public Connection miConexion(){
        Connection cn=null;
        getCadenaConex();
        try{
           objDriver=(Driver)Class.forName("org.postgresql.Driver").newInstance();
           DriverManager.registerDriver(objDriver);
           cn=DriverManager.getConnection(url,user,password);
        }catch(Exception ex){
            logger.info((Object)("Error de Conexion Remota:  " + ex.getMessage()));
        }
        
        return cn;
    }
}
