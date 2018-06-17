package client.mac;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Encoder;

public class authUtil {



	public static String getAuthTag(String authKey, File file){
		String algo = "HMACSHA256";

		try{
			Mac mac = Mac.getInstance(algo);
			Key secretKey = new SecretKeySpec(authKey.getBytes(), algo);
			mac.init(secretKey);

			FileInputStream in = new FileInputStream(file);
			byte[] macb = processFile(mac, in);
			String encoded = new BASE64Encoder().encode(macb);
			in.close();
			System.out.println(file + ": " + encoded + " macb :" + macb);
			return encoded;
		}

		catch(Exception e){
			e.printStackTrace();
			return "could not get authTag";
		}

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
