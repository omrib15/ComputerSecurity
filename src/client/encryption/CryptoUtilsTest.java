package client.encryption;

import java.io.File;

/**
 * A tester for the CryptoUtils class.
 *
 */
public class CryptoUtilsTest {
    public static void main(String[] args) {
        String key = "5a7127b8915a15287ed7a3da0e65378964e5babdfd0579ad399551ce49099849".substring(0, 16);
        
        File inputFile = new File("C:/omri/before.txt");
        File encryptedFile = new File("C:/omri/0b.txt.encrypted");
        File decryptedFile = new File("C:/omri/0b.txt.decrypted");
         
        
        
        
        String plainText = "mato+++kiii";
        System.out.println("plainText = " + plainText);
        
        System.out.println("plainText.replacAll() = " + plainText.replaceAll("\\+", "9999"));
        
        /*
        String cipherText = CryptoUtils.encryptString(key, plainText);
        System.out.println("chiperText = " + cipherText + " length = " + cipherText.length() + " 16 % length = " +( 16 % cipherText.length() ) );
        
        String decryptedText = CryptoUtils.decryptString(key, cipherText);
        System.out.println("decryptedText = " + decryptedText+ " length = " + decryptedText.length());
        
        
        inputFile.renameTo(new File(inputFile.getAbsolutePath().substring(0 , inputFile.getAbsolutePath().lastIndexOf('\\') +1) + "metoki"));
        
        */
        /*try {
            CryptoUtils.encrypt(key, inputFile, encryptedFile);
            CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
            System.out.println("ecrypted 0b length = " + encryptedFile.length() );
            System.out.println("decrypted 0b length = " +decryptedFile.length() );
            
        } catch (CryptoException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }*/
    }
}