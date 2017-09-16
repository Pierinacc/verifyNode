/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package verifyNode;

import dao.NodeDAO;
import entity.Node;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;
import org.apache.log4j.Logger;

/**
 *
 * @author DELL
 */
public class VerifyNode {
    
    static Logger logger = Logger.getLogger(VerifyNode.class);

    public static void main(String[] args) throws SQLException, UnsupportedEncodingException {
        logger.info("Inicia proceso de verificación ..............................");
        
        ArrayList<Node> nodes = NodeDAO.getInstancia().getActiveNodes();
        
        for (Node node : nodes) {
            String name = node.getName();
            enviarMensajeGET(name);
        }
        logger.info("Fin de  proceso de verificación ..............................");
    }
    
    private static String enviarMensajeGET(String node) throws UnsupportedEncodingException {
        String mensaje = node+" llega a movistar";
        String request= "http://54.208.99.218:8080/MES2App/sendSMS.jsp?mensaje="+mensaje
                + "&numero=942393266&usuario="+node;
        String protocolo="HTTP";
        String response = "";

        BufferedReader rd = null;
        try {
            URL url = new URL(request);

            if (protocolo.equals("HTTPS")) {
                HttpsURLConnection conn1 = (HttpsURLConnection) url.openConnection();
                rd = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
            } else {
                URLConnection conn2 = url.openConnection();
                rd = new BufferedReader(new InputStreamReader(conn2.getInputStream()));
            }

            String line;
            while ((line = rd.readLine()) != null) {
                //Process line...
                response += line;
            }

            logger.info(node+" envío estado para verificación"); 
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {

            if (rd != null) {
                try {
                    rd.close();
                } catch (IOException ex) {
                    System.out.println("PROBLEMA AL CERRA LA LECTURA");
                }
            }
        }

        return response;
    }
    
}
