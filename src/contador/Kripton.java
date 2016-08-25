package contador;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Daniel
 */
public class Kripton {
    String clave;
    
    public Kripton(){
        clave = "dan127";
    }
    public Kripton(String clave){
        this.clave = clave;
    }
    
    public String md5(String input) {
         
        String md5 = null;
         
        if(null == input){
            return null;
        }else{
            input +=clave;
        }
         
        try {
             
        //Create MessageDigest object for MD5
        MessageDigest digest = MessageDigest.getInstance("MD5");
         
        //Update input string in message digest
        digest.update(input.getBytes(), 0, input.length());
 
        //Converts message digest value in base 16 (hex) 
        md5 = new BigInteger(1, digest.digest()).toString(16);
 
        } catch (NoSuchAlgorithmException e) {
 
            //e.printStackTrace();
        }
        return md5;
    }

    
}

