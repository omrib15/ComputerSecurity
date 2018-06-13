package user;

import java.io.Serializable;
import java.util.StringTokenizer;

import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.servlet.ServletContainerInitializer;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement

public class UserCredentials implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
    private String password;

    public UserCredentials(String username , String password){
    	this.username = username;
    	this.password = password;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}