package client.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import client.mac.authUtil;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
/**
 * A tester for the CryptoUtils class.
 *
 */
public class CryptoUtilsTest {
	public static void main(String[] args) {
		String key = "5a7127b8915a15287ed7a3da0e65378964e5babdfd0579ad399551ce49099849".substring(0, 32);

		File inputFile = new File("C:/omri/0b.txt");
		File encryptedFile = new File("C:/omri/0b/0b.txt");
		File decryptedFile = new File("C:/omri/0b.txt.decrypted");
		File emptyDir = new File("C:/omri");

				
		System.out.println(authUtil.getAuthTag(key, inputFile));
		System.out.println(authUtil.getAuthTag(key, encryptedFile));
		//System.out.println(authUtil.getAuthTag(key, inputFile));
		//System.out.println(encryptedFile + " tag = " + authUtil.getAuthTag(key, encryptedFile));
		//System.out.println(inputFile + " tag = " + authUtil.getAuthTag(key, inputFile));
		
		//System.out.println("deleting " + inputFile + " " + inputFile.delete());
		
		/*String algo = "HMACSHA256";

		try{
			Mac mac = Mac.getInstance(algo);
			Key secretKey = new SecretKeySpec(key.getBytes(), algo);
			mac.init(secretKey);

			FileInputStream in = new FileInputStream(inputFile);
			byte[] macb = processFile(mac, in);
			System.out.println(inputFile + ": " + new BASE64Encoder().encode(macb) + " macb :" + macb);
		}
		
		catch(Exception e){
			e.printStackTrace();
		}*/



		/*String plainText = "mato+++kiii";
        System.out.println("plainText = " + plainText);

        System.out.println("plainText.replacAll() = " + plainText.replaceAll("\\+", "9999"));
		 */	 
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

	static private final byte[] processFile(Mac mac,InputStream in)
			throws java.io.IOException
	{
		byte[] ibuf = new byte[1024];
		int len;
		while ((len = in.read(ibuf)) != -1) {
			mac.update(ibuf, 0, len);
		}
		return mac.doFinal();
	}
}