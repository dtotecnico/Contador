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
    static boolean ckn = true;
    static long T = 80;    //T=t1-t0   (tck) (t20mm)
    static long T1 = 600;  //T1=t0-t01  (t150mm)
    static long t0 = 600;
    static long t1 = 680;
    static long t01;
    static long T_lectura=40;  //  T_lectura=T/2    tiempo en milisegundos
    static String granja = "1";
    static int Granja = 1;
    static int NewGranja;
    static int contador, cknxh, anterior;
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
        
        long Tpreboot, Tpostboot, Toffline;
        final MyCknsql msq = new MyCknsql();
        //Eth0 eth = new Eth0();
        if (args.length != 1) {
//    System.out.println("Usage: bla bla bla");
//    return;
        } else {
            String licenciar = args[0];   //recibo la clave para licenciar
            //llamo a Kripton(licenciar) y actualizo la bd
            //Kripton krp = new Kripton(licenciar);

            //System.out.println("Licencia " + krp.md5(eth.GetMAC()));
            //msq.SetLic(krp.md5(eth.GetMAC()));
            System.exit(0);
        }
        String Version = "CONTADOR 20160822-1"; 
        
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
        extcont = msq.SumaDia(fecha_hoy) + 32000;         //para que nuca cuente mas de 32k
        msq.LOG("CONTADOR EXTENDIDO= " + String.valueOf(extcont));
        Granja = msq.GetFrm(fecha_hoy);
        msq.LOG("CONTADOR: "+String.valueOf(contador)+" GRANJA: "+String.valueOf(Granja)); 
        Tpostboot = System.currentTimeMillis();
        msq.LOG("POSTBOOT= " + String.valueOf(Tpostboot));
        
//////        //create and register gpio pin listener
//////        gpio.addListener(new GpioPinListenerDigital() {
//////
//////            @Override
//////            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
//////               // System.out.println(event.getPin().getName() + " "+event.getState());
//////                if(event.getPin().getName().equalsIgnoreCase("clk")){
//////                    
//////                    if(event.getState() == PinState.HIGH){ //entro a la percha
//////                                                                                    // -----T1---
//////                        t0 = System.currentTimeMillis();                            // --         --
//////                        T1 = t0 - t01; //////////////////////T1 = t0 - t01 + T      // | |        | |
//////                        if(T1 == 0){                                                // | |________| |
//////                            T1=600;                                                 //t0 t01      t0
//////                        }
//////                        cknxh = (int) (3600 * 1000 / (T1+T));   //velocidad de la noria T1+T=tiempo entre pollos
//////                        msq.InsertaTiempo(false, T, T1, cknxh);
//////                        
//////                    }else{ //salgo de la percha
//////                        
//////                        t1 = System.currentTimeMillis();
//////                        t01 = t1;
//////                        T = t1 - t0;
//////                        
//////                        if (myButtons[1].getState() == PinState.HIGH) {
//////                            Led.pulse(50);
//////                            NOCkn = 0;
//////                            contador++;
//////                            msq.MasUno(ahora, Granja);
//////
//////                        }
//////                        
//////                    } //FIN isHIGH
//////                    
//////                }
//////                
//////            } //FIN de handlegpio
//////        }, myButtons);        //FIN de LISTENER
        
        // create and register gpio pin listener
        clk.addListener(new GpioPinListenerDigital() {
            
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                //System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
                
                    if(event.getState() == PinState.HIGH){ //entro a la percha
                                                                                    // -----T1---
                        t0 = System.currentTimeMillis();                            // --         --
                        T1 = t0 - t01; //////////////////////T1 = t0 - t01 + T      // | |        | |
                        if(T1 == 0){                                                // | |________| |
                            T1=600;                                                 //t0 t01      t0
                        }
                        cknxh = (int) (3600 * 1000 / (T1+T));   //velocidad de la noria T1+T=tiempo entre pollos
                        msq.InsertaTiempo(false, T, T1, cknxh);
                        
                    }else{ //salgo de la percha
                        
                        t1 = System.currentTimeMillis();
                        t01 = t1;
                        T = t1 - t0;
                        
                        if (ckn.getState() == PinState.HIGH) {
                            Led.pulse(50);
                            NOCkn = 0;
                            contador++;
                            msq.MasUno(ahora, Granja);
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
                extcont = 32000;
                msq.SetFrm(Granja);
                msq.NewDia(fecha_hoy, Granja);
                msq.LOG("CAMBIO DE FECHA");

                System.out.println("REINICIO EL SO");
                Reiniciar boot = new Reiniciar();
                msq.LOG("REINICIO DEL SISTEMA");
                Tpreboot = System.currentTimeMillis();
                msq.LOG("PREBOOT = " + String.valueOf(Tpreboot));
                boot.Reinicio();
            }
            
            System.out.println("POLLOS X MIN: "+(contador - anterior));
            System.out.println("CONTADOR = "+contador);
            anterior = contador;
            
            try {
                    Thread.sleep(60000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Contador.class.getName()).log(Level.SEVERE, null, ex);
                }
            
        }// FIN del WHILE
        
    }//FIN del main
}
