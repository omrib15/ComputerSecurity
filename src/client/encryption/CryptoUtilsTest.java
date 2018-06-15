package client.encryption;

import java.io.File;

/**
 * A tester for the CryptoUtils class.
 *
 */
public class CryptoUtilsTest {
    public static void main(String[] args) {
        String key = "Mary has one cat";
        String path =  "C:/omri/study/sem8/security/codeJava/UploadServletApp/src/client/encryption";
        File inputFile = new File(path + "/omri.txt");
        File encryptedFile = new File(path +"/enc.txt");
        File decryptedFile = new File(path + "/dec.txt");
         
        try {
            CryptoUtils.encrypt(key, inputFile, encryptedFile);
            CryptoUtils.decrypt(key, encryptedFile, decryptedFile);
        } catch (CryptoException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}