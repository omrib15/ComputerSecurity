package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.internal.util.Base64;

import sun.misc.BASE64Decoder;

/**
 * A Java servlet that handles file upload from client.
 * 
 */
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String UPLOAD_DIRECTORY = "users";
	private static final int THRESHOLD_SIZE 	= 1024 * 1024 * 3; 	// 3MB
	private static final int MAX_FILE_SIZE 		= 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE 	= 1024 * 1024 * 50; // 50MB
	private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";
	/**
	 * handles file upload via HTTP POST method
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String tag = request.getHeader("MAC");
		String requestUsername;

		// checks if the request actually contains upload file
		if (!ServletFileUpload.isMultipartContent(request)) {
			PrintWriter writer = response.getWriter();
			writer.println("Request does not contain upload data");
			//Calling flush() on the PrintWriter commits the response
			writer.flush();
			return;
		}

		requestUsername = getRequester(request.getHeader("Authorization"));

		// configures upload settings
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(THRESHOLD_SIZE);
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setFileSizeMax(MAX_FILE_SIZE);
		upload.setSizeMax(MAX_REQUEST_SIZE);

		// constructs the directory path to store upload file
		String uploadPath = getServletContext().getRealPath("")
				+ File.separator + UPLOAD_DIRECTORY;

		// creates the directory if it does not exist
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}

		uploadPath += "/" + requestUsername; 
		uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}

		try {
			// parses the request's content to extract file data
			List formItems = upload.parseRequest(request);
			Iterator iter = formItems.iterator();

			// iterates over form's fields
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				// processes only fields that are not form fields
				if (!item.isFormField()) {
					String fileName = (new File(item.getName())).getName();
					System.out.println("fileName = " + fileName);
					String filePath = uploadPath + "/" + fileName;
					File storeFile = new File(filePath);

					// saves the file on disk
					item.write(storeFile);

					//persist file authentication information
					String authDirPath = uploadPath + "/auth";
					File authDir = new File (authDirPath);
					if(!authDir.exists()){
						authDir.mkdir();
					}

					String authFilePath = authDirPath+"/auth.txt";
					String fileAuthInfo = "<" + fileName +","+tag+"," + storeFile.length()+">";

					File authFile = new File(authFilePath);

					if(!authFile.exists()){
						authFile.createNewFile();
					}

					String fileContext = FileUtils.readFileToString(authFile);

					if(!fileContext.contains(fileAuthInfo)){
						//write the file info to auth file
						try {
							Files.write(Paths.get(authFilePath), fileAuthInfo.getBytes(), StandardOpenOption.APPEND);
						} catch (IOException e) {
							System.out.println("caught error trying to write to pass file");
							e.printStackTrace();
						}
					}
				}
			}


			request.setAttribute("message", "Upload has been done successfully!");
			System.out.println(" - - - - -response status = = = = " +response.getStatus());
		} catch (Exception ex) {
			request.setAttribute("message", "There was an error: " + ex.getMessage());
		}

	}

	/*
	 * handles file downloads
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileName = request.getParameter("fileName");
		String username = request.getParameter("username");

		System.out.println("trying to get file : "+ fileName);

		if(fileName == null || fileName.equals("")){
			throw new ServletException("File Name can't be null or empty");
		}

		String downloadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY 
				+ File.separator + username + File.separator +fileName  ;

		File file = new File(downloadPath);
		if(!file.exists()){
			throw new ServletException("File doesn't exists on server.");
		}

		System.out.println("File location on server::"+file.getAbsolutePath());
		ServletContext ctx = getServletContext();
		InputStream fis = new FileInputStream(file);
		String mimeType = ctx.getMimeType(file.getAbsolutePath());
		response.setContentType(mimeType != null? mimeType:"application/octet-stream");
		response.setContentLength((int) file.length());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		String tag = getFileTag(fileName, username);
		System.out.println("2222 sending back MAC with tag " + tag);
		response.setHeader("MAC", tag);

		ServletOutputStream os = response.getOutputStream();
		byte[] bufferData = new byte[1024];
		int read=0;
		while((read = fis.read(bufferData))!= -1){
			os.write(bufferData, 0, read);
		}
		os.flush();
		os.close();
		fis.close();
		System.out.println("File downloaded at client successfully");
	}

	private ArrayList<String> getFileNames(String dir){
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> fileNames = new ArrayList<String>();

		for (int i = 0; i < listOfFiles.length; i++) {
			String fName = listOfFiles[i].getName();

			if (listOfFiles[i].isFile()) { 
				fileNames.add(fName);
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + fName);
			}
		}

		return fileNames;
	}

	private String getRequester(String authHeader){
		String retVal;
		StringTokenizer tokenizer;

		retVal = authHeader.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
		retVal = Base64.decodeAsString(retVal);

		tokenizer = new StringTokenizer(retVal, ":");

		retVal = tokenizer.nextToken();

		return retVal;
	}

	private String getFileTag(String fileName, String userName){
		String tag = "";
		String authDirPath = FilesResource.USERS_DIR_PATH + File.separator + userName + File.separator + "auth";
		File authDir = new File(authDirPath);
		//create auth directory if doesnt exist
		if(!authDir.exists()){
			authDir.mkdir();
		}

		File authFile = new File(authDirPath + File.separator+"auth.txt");
		if(authFile.exists()){
			String fileContext;

			try {
				fileContext = FileUtils.readFileToString(authFile);
			} catch (IOException e) {
				e.printStackTrace();
				fileContext= "";
			}
			
			int tripletIndex = fileContext.indexOf("<"+fileName);

			if(tripletIndex == -1){
				return FilesResource.UNAUTHORIZED_CHANGES_MADE;
			}

			String fileTriplet = fileContext.substring( tripletIndex + 1 , fileContext.indexOf('>', tripletIndex));
			StringTokenizer tokenizer = new StringTokenizer(fileTriplet, ",");
			tokenizer.nextToken();
			tag = tokenizer.nextToken();

		}
		System.out.println("?????????? tag sent back : " + tag);
		return tag;

	}


}
