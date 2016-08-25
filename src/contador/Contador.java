/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contador;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.system.SystemInfo;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dpatik
 */
public class Contador {
    static boolean ckn_flg = true;
    static long T = 80;    //T=t1-t0   (tck) (t20mm)
    static long T1 = 600;  //T1=t0-t01  (t150mm)
    static long t0 = 600, t0_ant;
    static long t1 = 680;
    static long t01;
    static long T_lectura=40;  //  T_lectura=T/2    tiempo en milisegundos
    static String granja = "1";
    static int Granja = 1;
    static int NewGranja;
    static int contador, cknxm, cknxh, perchasxm, anterior, cknpromxm, n_min, n_perchas_ant, perchasxh, n_perchas;
    static int NOCkn = 0; 
//    static Display lcd = new Display();
    static boolean newfrm = false;
    static String mac, ip;
    static int extcont = 0;
    static String ahora = "";

    /**
     * 20160816-1   nueva version del contador de pollos
     * 
     */
    
        //static Scanner in = new Scanner(System.in);
    
    final static MyCknsql msq = new MyCknsql();

    
    public static void main(String[] args) {
        // TODO code application logic here
        
    
    GpioController gpio = GpioFactory.getInstance();
    final GpioPinDigitalOutput Led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, // PIN NUMBER #7
            "LED", // PIN FRIENDLY NAME (optional)
            PinState.LOW);      // PIN STARTUP STATE (optional)
//////    final GpioPinDigitalInput myButtons[] = {
//////        gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, "clk", PinPullResistance.PULL_UP), //#16
//////        //gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, "B2", PinPullResistance.PULL_UP),
//////        gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, "ckn", PinPullResistance.PULL_UP), //#18
//////    //gpio.provisionDigitalInputPin(RaspiPin.GPIO_12, "B4", PinPullResistance.PULL_UP) 
//////    };
    
    final GpioPinDigitalInput clk = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_UP);
    final GpioPinDigitalInput ckn = gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, PinPullResistance.PULL_UP);
        
        //long Tpreboot, Tpostboot, Toffline;
                
        //Eth0 eth = new Eth0();
        if (args.length != 1) {
//    System.out.println("Usage: bla bla bla");
//    return;
        } else {
            String lic_key = args[0];   //recibo la clave para licenciar
            //llamo a Kripton(licenciar) y actualizo la bd
            //Kripton krp = new Kripton(lic_key);

            //System.out.println("Licencia " + krp.md5(eth.GetMAC()));
            //msq.SetLic(krp.md5(eth.GetMAC()));
            System.exit(0);
        }
        String Version = "CONTADOR 20160824-5"; 
        
        msq.LOG("INICIO DE LA APLICACION // VERSION "+Version);
        try {
            System.out.println("Nro de SERIE ------>"+SystemInfo.getSerial());
        } catch (IOException ex) {
            Logger.getLogger(Contador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Contador.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        System.out.println(" ================> SISTEMA LICENCIADO <================\n\n");

        String fecha_db=null;
        Date fechadb=null;
        Date fechahoy=null;
        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
        String fecha_hoy=ymd.format(new Date());
        ahora = fecha_hoy;
        
        contador = msq.GetPollos(fecha_hoy);
        extcont = msq.SumaDia(fecha_hoy) + 10000;         //para que nuca cuente mas de 32k
        Granja = msq.GetFrm(fecha_hoy);
        msq.LOG("CONTADOR: "+String.valueOf(contador)+" GRANJA: "+String.valueOf(Granja)); 
//        Tpostboot = System.currentTimeMillis();
//        msq.LOG("POSTBOOT= " + String.valueOf(Tpostboot));
        

        
        // create and register gpio pin listener
        clk.addListener(new GpioPinListenerDigital() {
            
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                //System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
                
                if (event.getState() == PinState.HIGH) { //entro a la percha

                    if (ckn.getState() == PinState.HIGH) {      //miro si hay pollo al entrar a la percha
                        ckn_flg = true;
                    } else {
                        ckn_flg = false;
                    }
                                                                               /////// // -----T1---
                    t0 = System.currentTimeMillis();                           /////// // --         --
                    //////T1 = t0 - t01; //////////////////////T1 = t0 - t01 + T      // | |        | |
//////                    if (T1 == 0) {                                              // | |________| |
//////                        T1 = 600;                                               //t0 t01      t0
//////                    }
//                    if (t0_ant != 0) {
//                        T1 = t0 - t0_ant;
//                        t0_ant = t0;
//
//
//                        //perchasxh = (int) (3600 * 1000 / (T1 + T));   //velocidad de la noria T1+T=tiempo entre pollos


                } else { //salgo de la percha
                        n_perchas++;
//////                    t1 = System.currentTimeMillis();
//////                    t01 = t1;
//////                    T = t1 - t0;

                    if (ckn.getState() == PinState.HIGH && ckn_flg) {   //si hay pollo al entrar y al  salir de la percha  ===> HAY POLLO
                        Led.pulse(50);
                        NOCkn = 0;

                        if (newfrm) {
                            newfrm = false;
                            NOCkn = 0;
                            NewGranja = msq.GetNewFrm();
                            if (Granja != NewGranja) {
                                contador = 0;
                                Granja = NewGranja;                             //--------->>>>ESTE ES EL QUE VALE
                                msq.LOG("CAMBIO de GRANJA: " + String.valueOf(Granja));
                                msq.SetEstadistica(Granja, cknxh, perchasxh);
                            }
                        }
                        contador++;
                        msq.MasUno(ahora, Granja);
                        
                    } else {   //NO HAY POLLO
                        if (NOCkn > 15) {
                            newfrm = true;
                        } else {
                            NOCkn++;
                        }
                    }
                } //FIN isHIGH
 
            }

        });
        
        while(true) {
            fecha_hoy = ymd.format(new Date());
            try {            
                fecha_db = msq.GetFechaDB();
                fechadb = ymd.parse(fecha_db);
                //fechadb = ymd.parse(ahora);
                fechahoy=ymd.parse(fecha_hoy);
            } catch (ParseException ex) {
                Logger.getLogger(Contador.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (fechahoy.after(fechadb)) {
                contador = 0;
                Granja = 1;
                extcont = 10000;
                msq.SetFrm(Granja);
                msq.NewDia(fecha_hoy, Granja);
                msq.LOG("CAMBIO DE FECHA");

                System.out.println("REINICIO EL SO");
                Reiniciar boot = new Reiniciar();
                msq.LOG("REINICIO DEL SISTEMA");
//                Tpreboot = System.currentTimeMillis();
//                msq.LOG("PREBOOT = " + String.valueOf(Tpreboot));
                boot.Reinicio();
            }
            if (anterior == 0) {
                cknxm = 0;
            } else {
                cknxm = contador - anterior;
                cknpromxm = (n_min * cknpromxm + cknxm) / (n_min + 1);
                n_min++;
            }
            
            if(n_perchas_ant == 0){
                perchasxm = 0;
            } else {
                perchasxm = (n_perchas - n_perchas_ant);
                perchasxh = perchasxm * 60;
            }
            System.out.println("POLLOS X MIN: "+(contador - anterior));
            System.out.println("PERCHAS POR MIN= "+perchasxm);
            System.out.println("POLLOS PROM X MIN: "+cknpromxm);
            System.out.println("n_min= "+n_min);
            System.out.println("CONTADOR = "+contador);
            cknxh = cknpromxm * 60;
            System.out.println("POLLOS PROM X H= "+cknxh);
            
            System.out.println("PERCHAS POR HORA= "+perchasxh+"\n");
            
            msq.SetCknxh(Granja, cknxh, perchasxh);
            anterior = contador;
            n_perchas_ant = n_perchas;
            
            if(contador >= extcont){
                extcont += 10000;
                //GrbCol.GetGarbageColector(); //solo me muestra la memoria
                GetGarbageColector();
            }
            
            try {
                    Thread.sleep(60000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Contador.class.getName()).log(Level.SEVERE, null, ex);
                }
            
        }// FIN del WHILE
        

        
    }//FIN del main
    
        public static void GetGarbageColector() {

        try {
            //System.out.println("********** INICIO: 'LIMPIEZA GARBAGE COLECTOR' **********");
            Runtime basurero = Runtime.getRuntime();
            //System.out.println("***********************************************************************");
            //System.out.println("MEMORIA TOTAL 'JVM': " + basurero.totalMemory());
            //System.out.println("MEMORIA [FREE] 'JVM' [ANTES]: " + basurero.freeMemory());
            //System.out.println("***********************************************************************\n");
            msq.LOG("MEMORIA TOTAL 'JVM': " + basurero.totalMemory());
            msq.LOG("MEMORIA [FREE] 'JVM' [ANTES]: " + basurero.freeMemory());
            basurero.gc(); //Solicitando ... 
//////            System.out.println("MEMORIA [FREE] 'JVM' [DESPUES]: " + basurero.freeMemory());
//////            System.out.println("********** FIN: 'LIMPIEZA GARBAGE COLECTOR' **********");
            msq.LOG("MEMORIA [FREE] 'JVM' [DESPUES]: " + basurero.freeMemory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//
}
