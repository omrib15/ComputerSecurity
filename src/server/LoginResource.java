package server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/login")
public class LoginResource {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String login(){
		return "login successful";
	}
}
