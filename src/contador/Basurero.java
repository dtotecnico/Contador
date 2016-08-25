/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contador;

/**
 *
 * @author dpatik
 */
public class Basurero {
    
    public void GetGarbageColector() {

        try {
            //System.out.println("********** INICIO: 'LIMPIEZA GARBAGE COLECTOR' **********");
            Runtime basurero = Runtime.getRuntime();
            System.out.println("***********************************************************************");
            System.out.println("MEMORIA TOTAL 'JVM': " + basurero.totalMemory());
            System.out.println("MEMORIA [FREE] 'JVM' [ANTES]: " + basurero.freeMemory());
            System.out.println("***********************************************************************\n");
//////            basurero.gc(); //Solicitando ... 
//////            System.out.println("MEMORIA [FREE] 'JVM' [DESPUES]: " + basurero.freeMemory());
//////            System.out.println("********** FIN: 'LIMPIEZA GARBAGE COLECTOR' **********");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//
    
}