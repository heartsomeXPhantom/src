/*
 * Created on 10.09.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.util;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * This class implements the OpenTMS encryption and decryption functions.
 * 
 * @author klemens
 *
 * 
 */
public class OpenTMSCrypter
{

    public static void main(String[] args)
    {
        OpenTMSCrypter crypt = new OpenTMSCrypter();
        System.out.println(crypt.encryptString(args[0]));
    }

    private Cipher dcipher;

    private Cipher ecipher;

    // Iteration count
    private int iterationCount = 19;
    
    // 8-byte Salt
    private byte[] salt =
        {
                (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
        };
    /**
     * returns an OpenTMSCrypter with a standard secret
     */
    public OpenTMSCrypter()
    {
        OpenTMSCrypter myCrypt = new OpenTMSCrypter("My19OcpenT4MSPass8word");
        ecipher = myCrypt.ecipher;
        dcipher = myCrypt.dcipher;
    }

    /**
     * OpenTMSCrypter initializes the OpenTMSCrypter with a secret (string)
     * 
     * @param passPhrase - the string used to generate the encrypted secret
     */
    public OpenTMSCrypter(String passPhrase)
    {
        try
        {
            // Create the key
            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            ecipher = Cipher.getInstance(key.getAlgorithm());
            dcipher = Cipher.getInstance(key.getAlgorithm());

            // Prepare the parameter to the ciphers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

            // Create the ciphers
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        }
        catch (java.security.InvalidAlgorithmParameterException ex)
        {
            ex.printStackTrace();
        }
        catch (java.security.spec.InvalidKeySpecException ex)
        {
            ex.printStackTrace();
        }
        catch (javax.crypto.NoSuchPaddingException ex)
        {
            ex.printStackTrace();
        }
        catch (java.security.NoSuchAlgorithmException ex)
        {
            ex.printStackTrace();
        }
        catch (java.security.InvalidKeyException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * decryptString decrypts a string and returns the decrypted string
     * 
     * @param str the encrypted string to be decrypted into its original value
     * @return the decrypted string
     */
    public String decryptString(String str)
    {
        try
        {
            // Decode base64 to get bytes
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);
            // Decode using utf-8
            return new String(utf8, "UTF8");
        }
        catch (javax.crypto.BadPaddingException ex)
        {
            // ex.printStackTrace();
            return str;
        }
        catch (IllegalBlockSizeException ex)
        {
            // ex.printStackTrace();
            return str;
        }
        catch (java.io.IOException ex)
        {
            // ex.printStackTrace();
            return str;
        }
        catch (Exception ex)
        {
            // ex.printStackTrace();
            return str;
        }
    }
    
    /**
     * encryptString encrypt a given string and returns the encrypted string
     * 
     * @param str - the string to be encrypted
     * @return the encrypted string
     */
    public String encryptString(String str)
    {
        try
        {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            return new sun.misc.BASE64Encoder().encode(enc);
        }
        catch (javax.crypto.BadPaddingException ex)
        {
            ex.printStackTrace();
        }
        catch (IllegalBlockSizeException ex)
        {
            ex.printStackTrace();
        }
        catch (java.io.IOException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
                        
    
}
