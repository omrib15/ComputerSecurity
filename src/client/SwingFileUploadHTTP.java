package client;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
 
import client.JFilePicker;
 
/**
 * A Swing application that uploads files to a HTTP server.
 *
 */
public class SwingFileUploadHTTP extends JFrame implements
        PropertyChangeListener {
    private JLabel labelURL = new JLabel("Upload URL: ");
    private JTextField fieldURL = new JTextField(30);
 
    private JFilePicker filePicker = new JFilePicker("Choose a file: ",
            "Browse");
 
    private JButton buttonUpload = new JButton("Upload");
    private JButton buttonDownload = new JButton("Download");
    private JLabel labelProgress = new JLabel("Progress:");
    private JProgressBar progressBar = new JProgressBar(0, 100);
    
    private String serverUrl = "http://localhost:8080/UploadServletApp/UploadServlet";
    
    public SwingFileUploadHTTP() {
        super("Swing File Upload to HTTP server");
 
        // set up layout
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
 
        // set up components
        filePicker.setMode(JFilePicker.MODE_OPEN);
 
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
 
        progressBar.setPreferredSize(new Dimension(200, 30));
        progressBar.setStringPainted(true);
 
        // add components to the frame
        constraints.gridx = 0;
        constraints.gridy = 0;
        add(labelURL, constraints);
 
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        add(fieldURL, constraints);
 
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.0;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.NONE;
        add(filePicker, constraints);
 
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        add(buttonUpload, constraints);
 
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.EAST;
        add(buttonDownload, constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        add(labelProgress, constraints);
 
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(progressBar, constraints);
        
        
        
  
 
        pack();
        setLocationRelativeTo(null);    // center on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
 
    /**
     * handle click event of the Upload button
     */
    private void buttonUploadActionPerformed(ActionEvent event) {
        String uploadURL = fieldURL.getText();
        String filePath = filePicker.getSelectedFilePath();
 
        // validate input first
        if (uploadURL.equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter upload URL!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            fieldURL.requestFocus();
            return;
        }
 
        if (filePath.equals("")) {
            JOptionPane.showMessageDialog(this,
                    "Please choose a file to upload!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        try {
            File uploadFile = new File(filePath);
            progressBar.setValue(0);
 
            UploadTask task = new UploadTask(uploadURL, uploadFile);
            task.addPropertyChangeListener(this);
            task.execute();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error executing upload task: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * handle click event of the Download button
     * @throws IOException 
     */
    private void buttonDownloadActionPerformed(ActionEvent event) throws IOException {
    	
    	String fileName = fieldURL.getText();
    	String url = serverUrl+"?fileName=" + fileName;
    	System.out.println("url for GET is : " + url);
    	HttpDownloadUtility.downloadFile(url, "C:/omri/study/sem8/security/codeJava/");
    	
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
    public static void main(String[] args) {
        try {
            // set look and feel to system dependent
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
 
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SwingFileUploadHTTP().setVisible(true);
            }
        });
    }
}