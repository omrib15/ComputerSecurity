package client.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;
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
			Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(cipherMode, secretKey);

			FileInputStream inputStream = new FileInputStream(inputFile);
			byte[] inputBytes = new byte[(int) inputFile.length()];
			inputStream.read(inputBytes);
			byte[] outputBytes = cipher.doFinal(inputBytes);

			FileOutputStream outputStream = new FileOutputStream(outputFile);
			outputStream.write(outputBytes);

			/*
			String encFilePath = inputFile.getAbsolutePath().replaceAll(inputFile.getName(), encryptString(key, inputFile.getName()));
			System.out.println("???::::: encfilepath : "+ encFilePath);
			outputFile.renameTo(new File(encFilePath));
			System.out.println("???::::: outputFile.getname() : "+ outputFile.getName());
			 */

			inputStream.close();
			outputStream.close();

		} catch (Exception ex) {
			throw new CryptoException("Error encrypting/decrypting file", ex);
		}
	}

	public static String encryptString(String key, String s){
		try{
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(s.getBytes());
			String replaced = new BASE64Encoder().encode(encrypted);
			replaced = replaced.replaceAll("\\+", "9999");
			replaced = replaced.replaceAll("/", "!!!!");
			
			System.out.println("encryptString encrypted = " + new BASE64Encoder().encode(encrypted));

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
				
				byte[] original = cipher.doFinal(new BASE64Decoder().decodeBuffer(replacedBack));
				//byte[] original = cipher.doFinal(new BASE64Decoder().decodeBuffer(encrypted.replaceAll("!!!!", "/")));

				return new String(original);
			} catch (Exception ex) {
				ex.printStackTrace();
			}


		}


		return null;


	}
}