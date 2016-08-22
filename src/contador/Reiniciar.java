package contador;


import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dpatik
 */
public class Reiniciar {
    
    public Reiniciar(){
        
    }
        
    public void Reinicio(){
        
    try {
            //String [] cmd = {"shutdown","-s","-t", "10"}; //Comando de apagado en windows
            String cmd = "shutdown -r now";
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
        
    }
    
    
    
}
