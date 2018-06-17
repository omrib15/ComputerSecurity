package client;

import java.awt.Dimension;
import java.awt.Font;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.internal.util.Base64;

import client.JFilePicker;
import client.encryption.CryptoException;
import client.encryption.CryptoUtils;
import client.login.LoginFrame;
import sun.misc.BASE64Decoder;


/**
 * A Swing application that uploads files to a HTTP server.
 *
 */
public class UserFrame extends JFrame implements
PropertyChangeListener {
	//private String authHeaderVal;
	//private String username;
	private UserInfo user;

	private JFilePicker filePicker = new JFilePicker("Choose a file: ", "Browse");
	private JFileChooser dirChooser = new JFileChooser();

	private JButton buttonUpload = new JButton("Upload");
	private JButton buttonDownload = new JButton("Download");
	private JButton buttonDelete = new JButton("Delete");
	private JButton buttonRefresh = new JButton("Refresh");
	private JButton buttonLogOut = new JButton("Logout");

	private JProgressBar progressBar = new JProgressBar(0, 100);

	private JLabel labelUpload = new JLabel("Upload:");
	private JLabel labelDownload = new JLabel("Manage your files: ");
	private JLabel labelProgress = new JLabel("Upload progress:");
	private JLabel labelFiles = new JLabel("Your Files:");

	private String uploadUrl = "http://localhost:8080/UploadServletApp/UploadServlet";

	private DefaultListModel fileListModel = new DefaultListModel();
	private JList fileList = new JList(fileListModel);
	private JScrollPane fileListScroller = new JScrollPane(fileList);



	public UserFrame(UserInfo user) throws IOException {
		super("Swing File Upload to HTTP server");

		//this.authHeaderVal = authHeaderVal;
		//this.username = username;

		this.user = user;

		// set up layout
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);

		// set up components
		filePicker.setMode(JFilePicker.MODE_OPEN);
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		//adding action listeners to buttons
		buttonUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				buttonUploadActionPerformed(event);
			}
		});

		buttonDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					buttonDownloadActionPerformed(event);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		buttonDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				buttonDeleteActionPerformed(event);
			}
		});

		buttonRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				buttonRefreshActionPerformed(event);
			}
		});

		buttonLogOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				buttonLogOutActionPerformed(event);
			}
		});

		//set some labels bold
		Font upFont = labelUpload.getFont();
		labelUpload.setFont(new Font(upFont.getFontName(), Font.BOLD, upFont.getSize()+2));
		labelDownload.setFont(new Font(upFont.getFontName(), Font.BOLD, upFont.getSize()+2));

		fileListScroller.setPreferredSize(new Dimension(100, 150));
		progressBar.setPreferredSize(new Dimension(215, 30));
		progressBar.setStringPainted(true);
		//fill the list with the user's file names 
		updateFileList();


		//Adding components to the frame

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		add(labelUpload, constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0.0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.NONE;
		add(filePicker, constraints);

		//Buttons
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		add(buttonUpload, constraints);

		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.anchor = GridBagConstraints.NORTH;
		add(buttonRefresh, constraints);

		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.anchor = GridBagConstraints.CENTER;
		add(buttonDownload, constraints);

		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.anchor = GridBagConstraints.SOUTH;
		add(buttonDelete, constraints);

		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.anchor = GridBagConstraints.WEST;
		add(buttonLogOut, constraints);



		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		add(labelProgress, constraints);

		constraints.gridx = 1;
		constraints.weightx = 1.0;
		//constraints.fill = GridBagConstraints.HORIZONTAL;
		add(progressBar, constraints);

		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		add(labelDownload, constraints);

		/*constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        add(labelFiles,constraints);
		 */

		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.weightx = 0.0;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(fileListScroller,constraints);



		pack();
		setLocationRelativeTo(null);    // center on screen
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * handle click event of the Upload button
	 */
	private void buttonUploadActionPerformed(ActionEvent event) {

		String filePath = filePicker.getSelectedFilePath();

		//create a temporary encrypted file in the same directory 
		String fileName = filePath.substring(filePath.lastIndexOf("\\")+1 , filePath.length());

		//the encrypted file name
		String encFileName = CryptoUtils.encryptString(user.getEncKey(), fileName);
		String decFileName = CryptoUtils.decryptString(user.getEncKey(), encFileName);
		System.out.println("<><><>< Upload: fileName: " +fileName + " encFileName : " +encFileName+ " decFileName " + decFileName);

		//String encFilePath = filePath.substring(0 , filePath.lastIndexOf("\\")) + "/" +encFileName ;
		String encFilePath = filePath+".encrypted";

		//validate server url
		if(uploadUrl.equals("")){
			JOptionPane.showMessageDialog(this, "Please enter upload URL!",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (filePath.equals("")) {
			JOptionPane.showMessageDialog(this,
					"Please choose a file to upload!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		File selectedFile = new File(filePath);
		final File encryptedFile = new File(encFilePath);
		final File encryptedFilePath = new File(encryptedFile.getAbsolutePath().substring(0 , encryptedFile.getAbsolutePath().indexOf(fileName)) + encFileName);

		try {
			//encrypt the file
			CryptoUtils.encrypt(user.getEncKey(), selectedFile, encryptedFile);
			System.out.println(encryptedFile.renameTo(encryptedFilePath));

			progressBar.setValue(0);

			UploadTask upTask = new UploadTask(uploadUrl, encryptedFilePath, user){
				@Override
				public void done() {
					if (!isCancelled()) {
						//update the file list when upload task is done
						updateFileList();

						//show success message
						JOptionPane.showMessageDialog(null,
								"File has been uploaded successfully!", "Message",
								JOptionPane.INFORMATION_MESSAGE);


					}
					//dont forget to delete the temporary encrypted file
					//System.out.println("trying to delete "+encryptedFile +encryptedFile.delete());
					System.out.println("trying to delete "+encryptedFilePath.getAbsolutePath()+" " +encryptedFilePath.delete());


				}
			};

			upTask.addPropertyChangeListener(this);
			//perform the upload task
			upTask.execute();

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
					"Error executing upload task: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			//dont forget to delete the temporary encrypted file
			//encryptedFile.delete();
			encryptedFilePath.delete();
		}



	}

	/**
	 * handle click event of the Download button
	 * @throws IOException 
	 */
	private void buttonDownloadActionPerformed(ActionEvent event) throws IOException {
		String dest = "";
		if( dirChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
			//get destination path
			dest = dirChooser.getSelectedFile().getAbsolutePath();

			//download the file to the selected destination
			String fileName = (String) fileList.getSelectedValue();
			//fileName = fileName.replaceAll(" ", ";");
			String encFileName = CryptoUtils.encryptString(user.getEncKey(), fileName);

			String url = uploadUrl+"?fileName=" + encFileName + "&username="+user.getUsername();
			System.out.println("get url : " +url);
			HttpDownloadUtility.downloadFile(url, dest);

			File downloadedFile = new File(dest+"/"+encFileName);

			//using randomAccessFile to strip last two bytes of the file (the line feed an boundary added by the multipartUploadUtility)
			RandomAccessFile file = new RandomAccessFile(downloadedFile,"rwd");

			file.setLength(downloadedFile.length()-2);

			System.out.println("downloaded "+ downloadedFile.getName()+ " from server with length = " + downloadedFile.length());

			File decryptedFile = new File(dest+"/"+fileName);
			//.replaceAll(".encrypted", "") );


			try {
				CryptoUtils.decrypt(user.getEncKey(), downloadedFile, decryptedFile);
			} catch (CryptoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			file.close();
			downloadedFile.delete();
			System.out.println("user.getEncKey() = " +user.getEncKey());
			System.out.println("user.getEncKey().length() = " +user.getEncKey().length());


		}

	}

	/**
	 * handle click event of the Delete button
	 * @throws IOException 
	 */
	private void buttonDeleteActionPerformed(ActionEvent event) {

		String fileName = (String) fileList.getSelectedValue();
		String encFileName = CryptoUtils.encryptString(user.getEncKey(), fileName);

		System.out.println("!@#!@#!@# fileName = " + fileName );
		if(fileName != null){
			//prompt a user confirmation
			int userRes = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete file: " + fileName, "Confirm",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (userRes == JOptionPane.NO_OPTION) {
				System.out.println("No button clicked");
			} 

			//user confirmation for deleting file
			else if (userRes == JOptionPane.YES_OPTION) {
				sendDeleteRequest(encFileName);
				updateFileList();
			} 

			else if (userRes == JOptionPane.CLOSED_OPTION) {
				System.out.println("JOptionPane closed");
			}
		}
	}

	private void sendDeleteRequest(String fileName){
		Client client = ClientBuilder.newClient();

		String deleteUrl = "http://localhost:8080/UploadServletApp/webapi/Files/"+user.getUsername()+"/"+fileName;

		Response response = client.target(deleteUrl)
				.request().header("Authorization", user.getAuthHeaderVal()).delete();

		int status = response.getStatus();

		if(status < 200 || status >= 300){
			JOptionPane.showMessageDialog(this,
					"executing delete task: server responded with " + status +" status code", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * handle click event of the Refresh button
	 */
	private void buttonRefreshActionPerformed(ActionEvent event){
		updateFileList();

	}

	private void buttonLogOutActionPerformed(ActionEvent event){
		this.setVisible(false);
		new LoginFrame("File manager").setVisible(true);
		this.dispose();
	}

	/*
	 * updates the list of file names with file names from server
	 */
	private void updateFileList(){

		Client client = ClientBuilder.newClient();

		//send a get request and get the response
		Response response = client.target("http://localhost:8080/UploadServletApp/webapi/Files/" + user.getUsername())
				.request().header("Authorization", user.getAuthHeaderVal()).get();

		System.out.println("MMMMMMMMMMM  authHeaderVal = " + user.getAuthHeaderVal());

		ArrayList list = response.readEntity(ArrayList.class);

		//clear the list
		fileListModel.clear();
		
		//update the list
		for(int i = 0; i < list.size() ; i++){
			String fileName = (String) list.get(i);
			
			if(fileName.equals("Warning: file size changed on server")){
				JOptionPane.showMessageDialog(this, fileName,
						"Error", JOptionPane.ERROR_MESSAGE);
				
			}

			else if(fileName.equals("Warning: authentication file deleted from server")){
				JOptionPane.showMessageDialog(this, fileName,
						"Error", JOptionPane.ERROR_MESSAGE);
			}

			else if(fileName.equals("Warning: file name has changed on server")){
				JOptionPane.showMessageDialog(this, fileName,
						"Error", JOptionPane.ERROR_MESSAGE);
			}
			
			else{

				System.out.println(">>> UpdateFileList fileName = " + fileName);
				//decrypt file names
				String decFileName = CryptoUtils.decryptString(user.getEncKey(), fileName);
				System.out.println(">>> UpdateFileList decFileName = " + decFileName);
				//prevent duplicates
				if(!fileListModel.contains(decFileName)){
					fileListModel.addElement(decFileName);
				}
			}
		}

	}


	/**
	 * Update the progress bar's state whenever the progress of upload changes.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}



	/**
	 * Launch the application
	 */
	}