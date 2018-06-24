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



@Path("/registration")
public class registrationResource {
	//private ServletContext context;
	private final URL resource = getClass().getResource("/");
	private final String path = resource.getPath().substring(1);
	private final String PASS_FILE_PATH = path + "UserAuth/users.txt";
	//private static final String PASS_FILE_PATH = "C:/omri/study/sem8/security/codeJava/Auth/users/users.txt";

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void registerUser(String s){
		
		System.out.println("!!!!!!!!!!!!!!!!! path = " + path);
		System.out.println("!!!!!!!!!!!!!!!!! PASS_FILE_PATH = " + PASS_FILE_PATH);
		
		StringTokenizer tokenizer = new StringTokenizer(s, ".");
		String username = tokenizer.nextToken();
		String password = tokenizer.nextToken();
		byte[] salt = generateSalt();
		
		String hashedPassAndSalt = Hashing.sha256()
				.hashString(password+salt, StandardCharsets.UTF_8)
				.toString();

		String credentialsToPersist = username + "," + salt + "," + hashedPassAndSalt + ".";
		
		try {
			System.out.println("bilhaaaa");
			Files.write(Paths.get(PASS_FILE_PATH), credentialsToPersist.getBytes(), StandardOpenOption.APPEND);
			System.out.println("tamar");
		} catch (IOException e) {
			System.out.println("caught error trying to write to pass file");
			e.printStackTrace();
		}



	}

	private byte[] generateSalt() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[16];
		random.nextBytes(bytes);
		return bytes;
	}
}
