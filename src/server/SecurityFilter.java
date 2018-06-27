package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.StringTokenizer;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.internal.util.Base64;

import com.google.common.hash.Hashing;

@Provider
public class SecurityFilter implements ContainerRequestFilter{

	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
	private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";
	private static final String REGISTRATION = "registration";
	private static final int AUTHENTICATION_SUCCESS = 1;
	private static final int AUTHENTICATION_FAIL = -1;
	private static final int AUTHENTICATION_USER_FILE_NOT_FOUND = 0;

	//Process incoming requests before passing the on to the resources
	public void filter(ContainerRequestContext requestContext) throws IOException {
		//do'nt filter if the request is for registering a new user
		if(!requestContext.getUriInfo().getPath().contains(REGISTRATION)){
			List<String> authHeader = requestContext.getHeaders().get(AUTHORIZATION_HEADER_KEY);

			if(authHeader != null && authHeader.size() > 0 ){
				
				String authToken = authHeader.get(0);
				//get the authHeader value, replace the Authorization prefix with empty string
				authToken = authToken.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
				
				String decodedString = Base64.decodeAsString(authToken);
	
				StringTokenizer tokenizer = new StringTokenizer(decodedString, ":");
				
				String username = tokenizer.nextToken();

				String password = tokenizer.nextToken();

				//perform basic auth
				int authRes = authenticate(username,password);

				if(authRes == AUTHENTICATION_SUCCESS){
					return;
				}
				
				else if(authRes == AUTHENTICATION_USER_FILE_NOT_FOUND){
					Response unauthorizedStatus = Response
							.status(Response.Status.UNAUTHORIZED)
							.entity("Special Users file not found")
							.build();

					requestContext.abortWith(unauthorizedStatus);
				}
			}

			Response unauthorizedStatus = Response
					.status(Response.Status.UNAUTHORIZED)
					.entity("User cannot acces the resource")
					.build();

			requestContext.abortWith(unauthorizedStatus);

		}

	}


	//Checks the user is registered
	private int authenticate(String username , String password) {
		//get the correct path to the special file holding user names and passwords(the derived ones of course)
		URL resource = getClass().getResource("/");
		String path = resource.getPath().substring(1);
		String userFilePath = path + "UserAuth/users.txt";
		
		File usersFile = new File(userFilePath);
		
		//create the special file if it does not exist
		if (!usersFile.exists()) {
			System.out.println(userFilePath + " does not exist.");
			return AUTHENTICATION_USER_FILE_NOT_FOUND;
		}

		if (!(usersFile.isFile() && usersFile.canRead())) {
			System.out.println(usersFile.getName() + " cannot be read from.");
			return AUTHENTICATION_USER_FILE_NOT_FOUND;
		}

		try {
			FileInputStream fis = new FileInputStream(usersFile);
			char c;
			String currentCredentials = "";

			//go over user credentials file and check if the user is registered
			while (fis.available() > 0) {
				c = (char) fis.read();
				if(c != '.'){
					currentCredentials += c;
				}
				else{
					if(compareToCredentials(currentCredentials, username, password)){
						fis.close();
						return AUTHENTICATION_SUCCESS;
					}
					currentCredentials = "";
				}
			}
			
			fis.close();
			
			//not registered
			return AUTHENTICATION_FAIL;

		} catch (IOException e) {
			e.printStackTrace();
			return AUTHENTICATION_USER_FILE_NOT_FOUND;
		}

	}

	
	private boolean compareToCredentials(String credentials, String username, String password){
		StringTokenizer tokenizer = new StringTokenizer(credentials, ",");
		
		String credUserName = tokenizer.nextToken();
		
		String salt = tokenizer.nextToken();
		
		String credPass = tokenizer.nextToken();
		
		String hashedPassAndSalt = Hashing.sha256()
				.hashString(password+salt, StandardCharsets.UTF_8)
				.toString();
		
		return (username.equals(credUserName) && credPass.equals(hashedPassAndSalt));
	}

}
