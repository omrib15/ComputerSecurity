package client.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import com.google.common.hash.Hashing;

import client.UserFrame;
import client.UserInfo;


public class LoginFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	private JLabel userLabel;
	private JTextField userText;
	private JLabel passwordLabel;
	private JPasswordField passwordText;
	private JButton loginButton;
	private JButton registerButton;


	public LoginFrame(String name){
		super(name);

		userLabel = new JLabel("User");
		userText = new JTextField(20);
		passwordLabel = new JLabel("Password");
		passwordText = new JPasswordField(20);
		loginButton = new JButton("login");
		registerButton = new JButton("register");
		this.setSize(300, 180);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocation(750, 350);
		JPanel panel = new JPanel();
		this.add(panel);
		this.placeComponents(panel);
		setActionListeners();

	}

	public static void main(String[] args) {
		LoginFrame loginFrame = new LoginFrame("File manager");
		
		loginFrame.setVisible(true);
		
	}

	public void placeComponents(JPanel panel) {

		panel.setLayout(null);

		userLabel.setBounds(10, 10, 80, 25);
		panel.add(userLabel);

		userText.setBounds(100, 10, 160, 25);
		panel.add(userText);

		passwordLabel.setBounds(10, 40, 80, 25);
		panel.add(passwordLabel);

		passwordText.setBounds(100, 40, 160, 25);
		panel.add(passwordText);

		loginButton.setBounds(10, 80, 80, 25);
		panel.add(loginButton);

		registerButton.setBounds(180, 80, 80, 25);
		panel.add(registerButton);
	}

	private void setActionListeners(){
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				buttonLoginActionPerformed(event);
			}
		});

		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				buttonRegisterActionPerformed(event);
			}
		});

	}

	/*
	 * handles login button action
	 * */
	private void buttonLoginActionPerformed(ActionEvent event){
		String username = userText.getText();
		String pass = extractPass(passwordText);
		UserInfo user = new UserInfo(username,pass);

		//make sure the username and password are legitimate
		if(checkLength(username,pass)){ 
			//create the Jersey client instance to send http requests
			Client client = ClientBuilder.newClient();

			Response response = client.target("http://localhost:9999/UploadServletApp/webapi/login")
					.request().header("Authorization", user.getAuthHeaderVal()).get();

			if(response.readEntity(String.class).equals("login successful")){
				JOptionPane.showMessageDialog(this, "login successful",
						"Success", JOptionPane.INFORMATION_MESSAGE);

				openUserFrame(user);
				this.setVisible(false);
				this.dispose();
			} 

			else {
				JOptionPane.showMessageDialog(this, "username or password incorrect",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	private void buttonRegisterActionPerformed(ActionEvent event){

		String username = userText.getText();
		String pass = extractPass(passwordText) ;
		String hashedPass = Hashing.sha256()
				.hashString(pass+UserInfo.PASSWORD_SECRET_NUM, StandardCharsets.UTF_8)
				.toString();


		UserCredentials credentials = new UserCredentials(username, hashedPass);

		if(checkLength(username,pass)){

			sendRegistrationRequest(credentials);	
		}
	}

	private void sendRegistrationRequest(UserCredentials credentials){

		Client client = ClientBuilder.newClient();
		//send a get request and get the response
		String body = credentials.getUsername()+"."+credentials.getPassword();
		Response response = client.target("http://localhost:9999/UploadServletApp/webapi/registration")
				.request().post(Entity.json(body));
		
		System.out.println("registration request returned with status: " + response.getStatus());

		if(response.getStatus() >=  200 && response.getStatus() < 300){
			JOptionPane.showMessageDialog(this, "Registration was successful",
					"Success", JOptionPane.INFORMATION_MESSAGE);

		}

		else{
			JOptionPane.showMessageDialog(this, "Could not register",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private String extractPass(JPasswordField passObj){
		char[] passC = passObj.getPassword();
		String retVal="";

		for(int i = 0; i < passC.length ; i++){
			retVal += passC[i]; 
		}

		return retVal;
	}

	private boolean checkLength(String username,String pass){
		boolean ans = true;

		if(username.length() < 6){
			ans = false;
			JOptionPane.showMessageDialog(this,
					"user name must be at least 6 characters long " , "",
					JOptionPane.INFORMATION_MESSAGE);
		}

		else if(pass.length() < 6){
			ans = false;
			JOptionPane.showMessageDialog(this,
					"password must be at least 6 characters long " , "",
					JOptionPane.INFORMATION_MESSAGE);
		}

		return ans;
	}

	private void openUserFrame(final UserInfo user){
		try {
			// set look and feel to system dependent
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new UserFrame(user).setVisible(true);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}



}