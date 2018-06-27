package server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/login")
public class LoginResource {

	//No real login logic here, the SecurityFilter makes sure all requests are authorized.
	//including this one.
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String login(){
		return "login successful";
	}
}
