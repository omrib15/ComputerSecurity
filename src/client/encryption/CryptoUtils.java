package client.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
//import sun.misc.BASE64Encoder;
//import sun.security.ec.ECDHKeyAgreement;
//import sun.misc.BASE64Decoder;
/**
 * A utility class that encrypts or decrypts a file.
 *
 */
public class CryptoUtils {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";
	private static final String initVector = "encryptionIntVec";


	public static void encrypt(String key, File inputFile, File outputFile)
			throws CryptoException {
		doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
	}

	public static void decrypt(String key, File inputFile, File outputFile)
			throws CryptoException {
		doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
	}

	private static void doCrypto(int cipherMode, String key, File inputFile,
			File outputFile) throws CryptoException {
		try {
			System.out.println("doCrypto 1");
			Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
			System.out.println("doCrypto 2");
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			System.out.println("doCrypto 3");
			cipher.init(cipherMode, secretKey);
			System.out.println("doCrypto 4");
			FileInputStream inputStream = new FileInputStream(inputFile);
			System.out.println("doCrypto 5");
			byte[] inputBytes = new byte[(int) inputFile.length()];
			inputStream.read(inputBytes);
			byte[] outputBytes = cipher.doFinal(inputBytes);
			System.out.println("doCrypto 6");
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			System.out.println("doCrypto 7");
			outputStream.write(outputBytes);
			System.out.println("doCrypto 8");

			inputStream.close();
			outputStream.close();
			System.out.println("doCrypto 9");
		} catch (Exception ex) {
			throw new CryptoException(ex.getMessage(), ex);
		}
	}

	public static String encryptString(String key, String s){
		try{
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			
			byte[] encrypted = cipher.doFinal(s.getBytes());
			//String replaced = new BASE64Encoder().encode(encrypted);
			String replaced = Base64.getEncoder().encodeToString(encrypted);
			replaced = replaced.replaceAll("\\+", "9999");
			replaced = replaced.replaceAll("/", "!!!!");

			return replaced;
			//return new BASE64Encoder().encode(encrypted).replaceAll("/", "!!!!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;


	}

	public static String decryptString(String key, String encrypted){
		if(encrypted != null && encrypted.length() > 0 ){
			try {
				IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
				SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
				cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
				
				String replacedBack = encrypted.replaceAll("9999", "\\+");
				replacedBack = replacedBack.replaceAll("!!!!", "/");
				
				byte[] original = cipher.doFinal(Base64.getDecoder().decode(replacedBack.getBytes()));
				//byte[] original = cipher.doFinal(new BASE64Decoder().decodeBuffer(replacedBack));
				

				return new String(original);
			} catch (Exception ex) {
				ex.printStackTrace();
			}


		}


		return null;


	}
}