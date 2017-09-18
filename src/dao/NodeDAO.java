/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.Node;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
    
    public int obtenerCodigoDeRespuesta(String nodo)throws SQLException{
        
        cn = Conexion.getInstancia().miConexion();
        Statement cs = null;
        int codigo = 0;
        
        String sqlString="select id from usrsms.incoming_message" +
                            " where upper(node) = 'MOVISTAR'" +
                            " and date_trunc('day', received_date) = CURRENT_DATE" +
                            " and process_status  = 1" +
                            " and txt_msg = '" + nodo.toUpperCase() + " llega a movistar'";
        
        try{                                    
            cs = cn.createStatement();
            rs = cs.executeQuery(sqlString);
            while (rs.next()) {                
                codigo = rs.getInt("id");
            }
            
        }catch(SQLException ex) {
            logger.info((Object)("Error al obtener codigo de incoming messages: " + ex.getMessage()));
        } finally {
            cn.close();
            if(cs != null){
                cs.close();
            }            
        }        
        return codigo;
    }
    
    public boolean setearRespuestaComoLeida(int id) throws SQLException{
        cn = Conexion.getInstancia().miConexion();
        PreparedStatement ps=null;
        int response=0;
        try {
            
            String query = "update usrsms.incoming_message" +
                            " set process_status = 2" +
                            " where id = ?";
            
            ps = cn.prepareStatement(query);
            ps.setInt(1, id);
            response=ps.executeUpdate();

        } catch (SQLException ex) {
            logger.info((Object)("Error al actualizar a leido incoming messages: " + ex.getMessage()));
        } finally {
            cn.close();
            if(ps != null){
                ps.close();
            }            
        }
        
        if(response == 0){
            return false;
        }else{
            return true;
        }
    }
    
    public List obtenerUsuariosSlack()throws SQLException{
        
        cn = Conexion.getInstancia().miConexion();
        Statement cs = null;
        List userSlack = new ArrayList<>();
        
        String sqlString="select username from usrsms.alert_users where status = 1 order by username;";
        
        try{
            cs = cn.createStatement();
            rs = cs.executeQuery(sqlString);
            while (rs.next()) {
                if(rs.getString(1).trim().length() > 0){
                    userSlack.add(rs.getString(1));
                }
            }
        }catch(SQLException ex) {
            logger.info((Object)("Error al listar usuarios de alertas: " + ex.getMessage()));
        } finally {
            cn.close();
            if(cs != null){
                cs.close();
            }            
        }        
        return userSlack;
    }
    
}
