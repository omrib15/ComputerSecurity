package client.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import com.google.common.hash.Hashing;

public class LoginFrame extends JFrame{

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
		setActionListeners();

	}

	public static void main(String[] args) {
		LoginFrame loginFrame = new LoginFrame("File manager");
		loginFrame.setSize(300, 150);
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginFrame.setLocation(750, 350);

		JPanel panel = new JPanel();
		loginFrame.add(panel);
		loginFrame.placeComponents(panel);

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

	private void buttonLoginActionPerformed(ActionEvent event){

	}

	private void buttonRegisterActionPerformed(ActionEvent event){

		String stringedPass = extractPass(passwordText);
		String hashedPass = "";

		if(checkLength()){
			//use SHA-256 to hash the password before sending it to the server
			hashedPass = Hashing.sha256()
					.hashString(stringedPass, StandardCharsets.UTF_8)
					.toString();
			
			//send user name and hashedpass to the registration resource
			
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

	private boolean checkLength(){
		boolean ans = true;

		if(userText.getText().length() < 6){
			ans = false;
			JOptionPane.showMessageDialog(this,
					"user name must be at least 6 characters long " , "",
					JOptionPane.INFORMATION_MESSAGE);
		}

		else if(passwordText.getPassword().length < 6){
			ans = false;
			JOptionPane.showMessageDialog(this,
					"password must be at least 6 characters long " , "",
					JOptionPane.INFORMATION_MESSAGE);
		}

		return ans;
	}

	

}