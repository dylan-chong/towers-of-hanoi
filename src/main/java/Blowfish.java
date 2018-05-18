import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Arrays;
import java.util.*;
import javax.xml.bind.DatatypeConverter;

/**
 * This class is a simple wrapper that simplifies the use of
 * the Blowfish algorithm and defines a range of useful utilities.
 */

public class Blowfish {

    // our secret key, created once -- used many times
    private static SecretKeySpec skeySpec;
    private static Cipher cipher;

    static {
	   try {
           cipher =  Cipher.getInstance("Blowfish");
	   } catch (NoSuchAlgorithmException e) {
           System.err.println("Blowfish implementation cannot be found. Exiting ...");
           System.exit(-1);
	   } catch (NoSuchPaddingException e) {
	       System.err.println("Padding exception. Exiting ...");
           System.exit(-1);
       }
    }

    /**
     * Initialises the key
     *
     * @param raw Key for using with the cipher
     */
    public static void setKey(byte[] raw) {
        try {
            skeySpec = new SecretKeySpec(raw, "Blowfish");
        } catch (Exception e) {
            // do nothing
        }                  
    }

    /**
     * Encrypts an array of bytes using the current cipher key.
     *
     * Suppresses exceptions related to invalid keys.
     *
     * @param plaintext   Array of bytes to convert to ciphertext
     * @return	          Ciphertext as array of bytes
     */
    public static byte[] encrypt(byte[] plaintext) {
        byte[] encrypted;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            encrypted = cipher.doFinal(plaintext);
        } catch (InvalidKeyException e) {
            encrypted = new byte[] {0};
        } catch (IllegalBlockSizeException e) {
            encrypted = new byte[] {0};
        } catch (BadPaddingException e) {
            encrypted = new byte[] {0};
        }
        return encrypted;
    }

    /**
     * Decrypts a ciphertext using the current cipher key.
     *
     * Assumes that ISO-8859-1 encoding was used.
     *
     * Suppress errors related to unsupported coding exceptions
     * (all plaintexts should be okay, otherwise we assume an 
     * incorrect decryption)
     *
     * @param ciphertext   Array of bytes to convert to ciphertext
     * @return Plaintext as a string
     */
    public static String decryptToString(byte[] ciphertext) {
        String plaintext = null;
        try {
            plaintext = new String(decrypt(ciphertext),"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            // do nothing, null will be returned
        }
        return plaintext;
    }
    
    /**
     * Decrypts an array of bytes using the current cipher key.
     *
     * Suppresses exceptions related to invalid keys.
     *
     * @param ciphertext   Array of bytes to convert to plaintext
     * @return Plaintext as array of bytes
     */
    public static byte[] decrypt(byte[] ciphertext) {
        byte[] original;
        try {
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            original = cipher.doFinal(ciphertext);
        } catch (InvalidKeyException e) {
            original = new byte[] {0};
        } catch (IllegalBlockSizeException e) {
            original = new byte[] {0};
        } catch (BadPaddingException e) {
            original = new byte[] {0};
        }
        return original;
    }
    
    /**
     * Converts an unsigned byte array into a positive BigInteger
     *
     * @param buf   Array of bytes to convert to plaintext
     * @return	    BigInteger
     */
    public static BigInteger asBigInteger (byte buf[]) {
        BigInteger bi = new BigInteger(1, buf);
        return bi;
    }   
    
    /**
     * Converts a BigInteger into a byte array (assumes unsigned).
     * 
     * @param BigInteger Convert a BigInteger into a byte array
     * @return byte[]
     */
    public static byte[] asByteArray(BigInteger bi) {    
        byte[] byteArray = bi.toByteArray();
        return byteArray;
    }
    
    /**
     * Converts a BigInteger into a byte array (assumes unsigned).
     * 
     * Creates a left padded output, this is useful when printing.
     *
     * @param BigInteger Convert a BigInteger into a byte array
     * @param len Expected length of the output
     * @return byte[]
     */
    public static byte[] asByteArray(BigInteger bi, int len) {    
        byte[] byteArray = bi.toByteArray();
        // ignore any extra leftmost byte (two's complement)
        int frmLen = byteArray.length;
        int frm=0;

        if (frmLen > len) {
            frmLen=len;
            frm = 1; // ignore the leftmost padding
        } else {
            frm = 0;
        }
        // create the padding
        byte[] padding = new byte[len];
        for (int i = len-frmLen; i < len; i++) {
            padding[i] = byteArray[frm++];
        }
        return padding;
    }   
    
    /**
     * Although you can use BigInteger(16) to convert to hex it doesn't
     * provide padding etc. unlike this method.
     *
     * @param byte[] signed bytes
     * @return a string representing unsigned versions of signed bytes
     */
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    public static String toHex(byte[] data) {
        char[] chars = new char[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            chars[i * 2] = HEX_DIGITS[(data[i] >> 4) & 0xf];
            chars[i * 2 + 1] = HEX_DIGITS[data[i] & 0xf];
        }
        return new String(chars);
    }
    
    /**
     * Take a base64 encoded string and convert to an array of bytes.
     *
     * @param encodedString base64 encoded string
     * @return an array of bytes
     */
    public static byte[] fromBase64(String encodedString) throws IllegalArgumentException {
        return DatatypeConverter.parseBase64Binary(encodedString);
    }

    /**
     * Take an array of bytes and convert into Base64 encoded string.
     *
     * @param arrayBytes array of bytes to encode
     * @return base64 encoded string
     */
    public static String toBase64(byte[] arrayBytes) throws IllegalArgumentException {
        return DatatypeConverter.printBase64Binary(arrayBytes);
    }
    
}
