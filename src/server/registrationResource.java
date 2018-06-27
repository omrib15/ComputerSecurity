package server;


import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.StringTokenizer;

//import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import com.google.common.hash.Hashing;


/*
 * This is the resource in charge of user registration on server 
 * */

@Path("/registration")
public class registrationResource {
	//private ServletContext context;
	private final URL resource = getClass().getResource("/");
	private final String path = resource.getPath().substring(1);
	private final String PASS_FILE_PATH = path + "UserAuth/users.txt";

	//this method is invoked when a POST request is made to the resource
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void registerUser(String s){
		//the parameter s is expected to be a formatted username and password (the derived one, not the master secret of course)
		StringTokenizer tokenizer = new StringTokenizer(s, ".");
		String username = tokenizer.nextToken();
		String password = tokenizer.nextToken();
		//the salt to be persisted with thhe user information
		byte[] salt = generateSalt();
		
		//use sha256 to hash the password+salt
		String hashedPassAndSalt = Hashing.sha256()
				.hashString(password+salt, StandardCharsets.UTF_8)
				.toString();

		String credentialsToPersist = username + "," + salt + "," + hashedPassAndSalt + ".";
		
		//try to write the credentials to the special password file
		try {
			Files.write(Paths.get(PASS_FILE_PATH), credentialsToPersist.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.out.println("caught error trying to write to pass file");
			e.printStackTrace();
		}



	}

	//This method returns a randomly generated salt
	private byte[] generateSalt() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[16];
		random.nextBytes(bytes);
		return bytes;
	}
}
