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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.apache.log4j.Logger;

/**
 *
 * @author DELL
 */
public class VerifyNode {
    
    static Logger logger = Logger.getLogger(VerifyNode.class);

    public static void main(String[] args) throws SQLException, UnsupportedEncodingException, IOException {
        logger.info("Inicia proceso de verificación ..............................");
        
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        
        switch(hour){
            case 9: case 11:
                new VerifyNode().enviarMensajesANodoMovistar();
                break;
            case 10: case 14:
                new VerifyNode().revisarRespuestasEnNodoMovistar();
                break;
            default: break;
        }
        
        logger.info("Fin de  proceso de verificación ..............................");
    }
    
    private void enviarMensajesANodoMovistar() throws UnsupportedEncodingException, SQLException{
        ArrayList<Node> nodes = NodeDAO.getInstancia().getActiveNodes();

        for (Node node : nodes) {
            this.enviarMensajeGET(node.getName());
        }
        
    }
    
    private void revisarRespuestasEnNodoMovistar() throws SQLException, IOException{
        String mensaje = "";
        ArrayList<Node> nodes = NodeDAO.getInstancia().getActiveNodes();
        
        for(Node node : nodes) {
            int codigoRespuesta = NodeDAO.getInstancia().obtenerCodigoDeRespuesta(node.getName());
            if(codigoRespuesta > 0){
                NodeDAO.getInstancia().setearRespuestaComoLeida(codigoRespuesta);
            } else{
                mensaje = mensaje + node.getName() + "; ";
            }
        }
        
        if(mensaje.length() > 0){
            this.enviarNotificacionPorSlack(mensaje);
        }
        
    }
    
    private void enviarNotificacionPorSlack(String mensaje) throws SQLException, IOException{
        String mensajeSlack = "ADVERTENCIA LOS PITCH NO ENVIAN A MOVISTAR: " + mensaje;
        
        List usuarios = NodeDAO.getInstancia().obtenerUsuariosSlack();

        for (int i = 0; i < usuarios.size(); i++) {
            String username = (String) usuarios.get(i);
            this.enviarSlack(username, mensajeSlack);
        }
        
    }
    
    private String enviarMensajeGET(String node) throws UnsupportedEncodingException {
        String mensaje = node+" llega a movistar";
        mensaje = mensaje.replaceAll(" ","%20");
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
                    System.out.println("PROBLEMA AL CERRAR LA LECTURA");
                }
            }
        }

        return response;
    }
    
    public void enviarSlack(String usuario,String message) throws MalformedURLException, IOException
    {
        URL url;

        try {
            String cadenaUrl = "http://dev.mowa.com.pe/Notifier/public/index.php/send_slack/"
                                +usuario+"/"+URLEncoder.encode(message, "UTF-8");
            
            url = new URL(cadenaUrl);
            URLConnection conn = url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            br.close();

            logger.info( (Object) ("Enviando mensaje a: " + usuario));

        } catch (MalformedURLException e) {
            logger.info( (Object) ("Error: " + e.getMessage()));
        } catch (IOException e) {
            logger.info( (Object) ("Error: " + e.getMessage()));
        }
    
    }
    
}
