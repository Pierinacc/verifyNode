/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Node;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author DELL
 */
public class NodeDAO {
    
    static Logger logger = Logger.getLogger((Class)NodeDAO.class);
    
    private Connection cn = null;
    private ResultSet rs = null;
    private static NodeDAO instancia;
    
    public static NodeDAO getInstancia(){
        if(instancia == null){
            instancia = new NodeDAO();
        }
        return instancia;
    }
    
    public ArrayList<Node> getActiveNodes()throws SQLException{
        
        cn = Conexion.getInstancia().miConexion();
        Statement cs = null;
        ArrayList<Node> listNodes = new ArrayList<>() ;             
        String sqlString="select name from usrsms.nodes where status = 1 " +
                    "and name like 'MWMKT%' order by group_id,id";
        
        try{                                    
            cs = cn.createStatement();
            rs = cs.executeQuery(sqlString);
            while (rs.next()) {
                
                Node nodes= new Node(rs.getString("name"));
                listNodes.add(nodes);
            }
            
        }catch(SQLException ex) {
            logger.info((Object)("Error al listar incoming messages: " + ex.getMessage()));
        } finally {
            cn.close();
            if(cs != null){
                cs.close();
            }            
        }        
        return listNodes;
    }
    
}
