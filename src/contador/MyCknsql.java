package contador;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dpatik
 */
public class MyCknsql {
    
    Connection cn;
    String bdriver = "com.mysql.jdbc.Driver";
    //String url = "jdbc:mysql://localhost/chkncounter?autoReconnect=true";
    String url = "jdbc:mysql://192.168.7.219/chkncounter?autoReconnect=true";
    //String url = "jdbc:mysql://192.168.153.219/chkncounter?autoReconnect=true";   //con ?autoReconnect=true soluciono la perdida de conexion
    String usr = "usuario1";
    String pass = "";
    
    
    public MyCknsql() {
        try {

            Class.forName(bdriver);
            cn = DriverManager.getConnection(url, usr, pass);

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// fin del constructor

    int GetPollos(String fecha) {
        ResultSet rs;
        int qty=0;
        //fecha="2015-04-29";
        //System.out.println("select sum(qty) from faena_diaria where fecha="+fecha);
//        rs=ConsultaBD("SELECT sum(qty) FROM faena_diaria WHERE fecha=\'"+fecha+"\'");
        rs=ConsultaBD("SELECT qty FROM faena WHERE fecha=\'"+fecha+"\' order by granja desc limit 1");
        try {
            while (rs.next()) {
                qty = rs.getInt("qty");
                //System.out.println("TOTAL de POLLOS a la fecha" + fecha + " = " + qty);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
        return qty;
    }

    void InsertaTiempo(boolean compensa, long t, long t1, int cknxs) {
        //System.out.println("estoy en insertatiempo");
//                String query = "INSERT INTO tiempos (t20mm, t150mm, cknxh)"
//                        + " VALUES (?,?,?)";
        
        String query = "UPDATE tiempos set compensa=?, t20mm=?, t150mm=?, chknxseg=?";
                        
              try {
                //System.out.println("tabla tiempos "+ t+" "+t1+" "+cknxh+"\n\n");
                
                PreparedStatement pst = cn.prepareCall(query);
                pst.setBoolean(1, compensa);
                pst.setLong(2, t);
                pst.setLong(3, t1);
                pst.setDouble(4, cknxs);
                //pst.setLong(5, tpopreboot);
                
                pst.execute();
                //cn.close();

            } catch (SQLException ex) {
                Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void Compensa(boolean compensa, long tpopreboot) {

        String query = "UPDATE tiempos set compensa=?,tpopreboot=?";
                
        try {
            PreparedStatement pst = cn.prepareCall(query);
            pst.setBoolean(1, compensa);
            pst.setLong(2, tpopreboot);

            pst.execute();
            //cn.close();

        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public long GetTpreboot(){
        long tpopreboot=0;
               ResultSet rs;
        rs = ConsultaBD("SELECT tpopreboot FROM tiempos ");

        try {
            while (rs.next()) {
                tpopreboot= rs.getInt("tpopreboot");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tpopreboot;
    }
    
    public boolean GetCompensa() {
        boolean compensa = false;
        ResultSet rs;
        rs = ConsultaBD("SELECT compensa FROM tiempos ");
        try {
       
            while (rs.next()) {
                compensa = rs.getBoolean("compensa");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return compensa;
    }
    
    public int GetChknxS() {
        int chknxs = 0;
        ResultSet rs;
        rs = ConsultaBD("SELECT chknxseg FROM tiempos ");
        try {
        
            while (rs.next()) {
                chknxs = rs.getInt("chknxseg");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return chknxs;
    }
    
    public void SetChknxS(double chknxseg) {

        String query = "UPDATE tiempos set chknxseg=?";
                
        try {
            PreparedStatement pst = cn.prepareCall(query);
            pst.setDouble(1, chknxseg);
            pst.execute();
            //cn.close();

        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void SetTPreBoot(long tpreboot) {

        String query = "UPDATE tiempos set tpopreboot=?";
        try {
            PreparedStatement pst = cn.prepareCall(query);
            pst.setLong(1, tpreboot);
            pst.execute();
            //cn.close();

        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void SetTPostBoot(long tpostboot){
        
        String query = "UPDATE tiempos set tpopostboot=?";
               
        try {
            PreparedStatement pst = cn.prepareCall(query);
            pst.setLong(1, tpostboot);
            pst.execute();
            //cn.close();

        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void NewDia(String fecha, int granja){

        Statement st;
        ResultSet rs;
int qty = 0;
//       String query = "INSERT INTO faena_diaria (fecha, hora, qty)"
//                    + " VALUES (?,?,?)";
       String query = "INSERT INTO faena (fecha, qty, granja) VALUES (?,?,?)";
               
       
       PreparedStatement pst;
        try {
            pst = cn.prepareCall(query);
            pst.setString(1, fecha);
            pst.setInt(2, qty);
            pst.setInt(3, granja);
            
            //System.out.println("fecha="+fecha+"  frm="+granja);

            pst.execute();
            //cn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
    }

    public void MasUno(String fecha, int granja){
        //Connection cn = Conectar();
//        Statement st;
//        ResultSet rs;
int qty = 1;
//       String query = "INSERT INTO faena_diaria (fecha, hora, qty)"
//                    + " VALUES (?,?,?)";
       String query = "INSERT INTO faena (fecha, qty, granja) VALUES (?,?,?)"
               + " ON DUPLICATE KEY UPDATE qty=qty+1";
       
       PreparedStatement pst;
        try {
            pst = cn.prepareCall(query);
            pst.setString(1, fecha);
            pst.setInt(2, qty);
            pst.setInt(3, granja);
            
            //System.out.println("fecha="+fecha+"  frm="+granja);

            pst.execute();
            //cn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }

    private ResultSet ConsultaBD(String query) {
                //Connection cn = Conectar();
        Statement st;
        ResultSet rs = null;
        try {
            st = cn.createStatement();
            rs = st.executeQuery(query);
//cn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }


        return rs;
    }

        public int GetNewFrm(){
        int nombre=0;
        ResultSet rs;
        rs = ConsultaBD("SELECT nombre FROM granja_actual order by nombre desc limit 1");
        try {
        
            while (rs.next()) {
            nombre= rs.getInt("nombre");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return nombre;
    }
        
        
        public int GetFrm(String fecha){
            int nombre=0;
        ResultSet rs;
        rs = ConsultaBD("SELECT granja FROM faena WHERE fecha=\'"+fecha+"\' order by granja desc limit 1");
        try {
      
            while (rs.next()) {
                nombre= rs.getInt("granja");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        return nombre;
            
        }
        
    public String GetFechaDB() {
        ResultSet rs;
        String fechadb = null;
//        rs = ConsultaBD("SELECT fecha FROM faena_diaria order by fecha desc limit 1");
        rs = ConsultaBD("SELECT fecha FROM faena order by fecha desc limit 1");

        try {
            while (rs.next()) {
                fechadb = rs.getString("fecha");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fechadb;
    }
        
        public void SetFrm(int granja){
            
Statement st;
        ResultSet rs;

//       String query = "INSERT INTO faena_diaria (fecha, hora, qty)"
//                    + " VALUES (?,?,?)";
//       String query = "INSERT INTO granja_actual (nombre) VALUES (?)"
//               + " ON DUPLICATE KEY UPDATE nombre="+granja;
        
        String query = "UPDATE granja_actual set nombre=?";
                   
       PreparedStatement pst;
        try {
            pst = cn.prepareCall(query);
            pst.setInt(1, granja);
            
            pst.execute();
            //cn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }            
            
        }
        
    public void SetIP(String ip) {
        
        //System.out.println("en setmac la mac es: "+mac);

        String query = "UPDATE licencia set ip_maq=\'" + ip + "\'";
//        String query = "INSERT INTO identificacion set ip_maq=\'" + ip + "\' "
//                + "ON DUPLICATE KEY UPDATE ip_maq=\'" + ip + "\'";

        PreparedStatement pst;
        try {
            pst = cn.prepareCall(query);
            pst.execute();
            
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void SetMac(String mac){
        
        //System.out.println("en setmac la mac es: "+mac);

        String query = "UPDATE licencia set mac_maq=\'" + mac + "\'" ;
//        String query = "INSERT INTO identificacion set mac_maq=\'" + mac + "\' "
//                + "ON DUPLICATE KEY UPDATE mac_maq=\'" + mac + "\'";

        PreparedStatement pst;
        try {
            pst = cn.prepareCall(query);
            pst.execute();
            
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public String GetLic(){
        
        ResultSet rs;

        rs = ConsultaBD("SELECT lic_maq from licencia ");
        try {
            while (rs.next()) {
                String lic = rs.getString("lic_maq");
               return lic;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "SIN LICENCIA";
    }
    
    public void SetLic(String lic){
        
        //System.out.println("en setmac la mac es: "+mac);

        String query = "UPDATE licencia set lic_maq=\'" + lic + "\'";

        PreparedStatement pst;
        try {
            pst = cn.prepareCall(query);
            pst.execute();
            
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }     
    
    public void CierraCnx(){
        try {
            cn.close();
        } catch (SQLException ex) {
            System.out.println("Estoy en CierraCnx");
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int SumaDia(String fecha) {
        int total = 0;
        ResultSet rs;

        rs = ConsultaBD("SELECT sum(qty) FROM chkncounter.faena where fecha =\'" + fecha + "\'");
        try {
            while (rs.next()) {
                total = rs.getInt("sum(qty)");
                return total;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;

    }
    
    public void LOG(String descripcion) {
        //Statement st;
        //ResultSet rs;
//       String query = "INSERT INTO faena_diaria (fecha, hora, qty)"
//                    + " VALUES (?,?,?)";
        
        String query = "INSERT INTO pollos_log (descripcion) VALUES (?)";
                

        PreparedStatement pst;
        try {
            pst = cn.prepareCall(query);
            pst.setString(1, descripcion);
            
            pst.execute();
            //cn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void SetEstadistica(int Granja, int cknxh, int perchasxh) {
        String query = "INSERT INTO estadisticas (granja, vel_noria, pollos_prom) VALUES (?, ?, ?)";
                

        PreparedStatement pst;
        try {
            pst = cn.prepareCall(query);
            pst.setInt(1, Granja);
            pst.setInt(2, perchasxh);
            pst.setInt(3, cknxh);
            
            pst.execute();
            //cn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void SetCknxh(int Granja, int cknxh, int perchasxh) {
         String query = "INSERT INTO estadisticas (granja, vel_noria, pollos_prom) VALUES (?,?,?)"
               + " ON DUPLICATE KEY UPDATE vel_noria="+perchasxh+", pollos_prom="+cknxh;
       
       PreparedStatement pst;
        try {
            pst = cn.prepareCall(query);
            pst.setInt(1, Granja);
            pst.setInt(2, perchasxh);
            pst.setInt(3, cknxh);
            
            //System.out.println("fecha="+fecha+"  frm="+granja);

            pst.execute();
            //cn.close();
        } catch (SQLException ex) {
            Logger.getLogger(MyCknsql.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    
}
