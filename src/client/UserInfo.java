package client;

import java.nio.charset.StandardCharsets;

import org.glassfish.jersey.internal.util.Base64;

import com.google.common.hash.Hashing;

public class UserInfo {
	private static final int PASSWORD_SECRET_NUM = 1;
	private static final int ENCRYPTION_SECRET_NUM = 2;
	private static final int AUTHENTICCATION_SECRET_NUM = 3;
	
	private String username;
	private String masterSecret;
	private String authHeaderVal;
	private String derivedPass;
	private String encKey;
	private String authKey;
	
	public UserInfo(String username, String masterSecret){
		this.username = username;
		this.masterSecret = masterSecret;
		derivedPass = deriveSecret(PASSWORD_SECRET_NUM);
		authHeaderVal = "Basic " + Base64.encodeAsString(username + ":"+derivedPass);
		encKey = deriveSecret(ENCRYPTION_SECRET_NUM);
		authKey = deriveSecret(AUTHENTICCATION_SECRET_NUM);
	}
	
	private String deriveSecret(int secretType){
		return Hashing.sha256()
				.hashString(masterSecret+secretType, StandardCharsets.UTF_8)
				.toString();
		
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMasterSecret() {
		return masterSecret;
	}

	public void setMasterSecret(String masterSecret) {
		this.masterSecret = masterSecret;
	}

	public String getDerivedPass() {
		return derivedPass;
	}

	public void setDerivedPass(String derivedPass) {
		this.derivedPass = derivedPass;
	}

	public String getEncKey() {
		return encKey;
	}

	public void setEncKey(String encKey) {
		this.encKey = encKey;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	
}
