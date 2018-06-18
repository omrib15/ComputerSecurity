package server;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;


@Path("/Files")
public class FilesResource {
	
	public static final String UNAUTHORIZED_CHANGES_MADE = "Warning: unauthorized changes may have been made to your files on the server";

	public static final String USERS_DIR_PATH = "C:/omri/study/sem8/security/codeJava/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/UploadServletApp/users";

	@Path("/{userName}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getFileNames(@PathParam("userName") String userName){
		//will change to user specific directory

		File userFolder = new File(USERS_DIR_PATH + File.separator + userName);
		if (!userFolder.exists()) {
			userFolder.mkdir();
		}

		File[] listOfFiles = userFolder.listFiles();
		List<String> fileNames = new ArrayList<String>();

		//this loop fills the list of file names
		for (int i = 0; i < listOfFiles.length; i++) {
			String fName = listOfFiles[i].getName();

			if (listOfFiles[i].isFile()) {
				fileNames.add(checkFile(listOfFiles[i], userName));
			} 

			else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + fName);
			}
		}
		
		//go over the auth file and make sure all files on it exist
		String checkAuthResult = checkAuthFile(fileNames, userName);
		if(checkAuthResult == UNAUTHORIZED_CHANGES_MADE){
			fileNames.add(checkAuthResult);
		}

		return fileNames;
	}

	@DELETE
	@Path("/{userName}/{fileName}")
	public void deleteFile(@PathParam("userName") String userName, @PathParam("fileName") String fileName){

		String dir = USERS_DIR_PATH + "/" + userName;

		try{
			File file = new File(dir + "/" + fileName);

			System.out.println("path to file: "+ dir + "/" + fileName);
			if(file.delete()){
				System.out.println(file.getName() + " is deleted!");

				String authDirPath = dir + "/auth";
				String authFilePath = authDirPath+"/auth.txt";

				//delete the files triplet from auth file
				deleteFromAuthFile(authFilePath, fileName);

			}else{
				System.out.println("Delete operation has failed.");
			}

		}catch(Exception e){

			e.printStackTrace();

		}
	}

	private void deleteFromAuthFile(String authFilePath, String fileName) throws IOException{

		File authFile = new File(authFilePath);

		String fileContext = FileUtils.readFileToString(authFile);

		int tripletIndex = fileContext.indexOf("<"+fileName);

		String fileTriplet = fileContext.substring( tripletIndex , fileContext.indexOf('>', tripletIndex)+1);

		fileContext = fileContext.replace(fileTriplet, "");

		FileUtils.write(authFile, fileContext);
	}


	private String checkAuthFile(List<String> fileNames, String userName){

		String authDirPath = USERS_DIR_PATH + File.separator + userName + File.separator + "auth";
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
				return UNAUTHORIZED_CHANGES_MADE;
			}


			StringTokenizer tokenizer = new StringTokenizer(fileContext, "<");
			String triplet,fileNameAuthFIle;

			while(tokenizer.hasMoreTokens()){
				triplet = tokenizer.nextToken();
				StringTokenizer tripletTokenizer = new StringTokenizer(triplet, ",");
				fileNameAuthFIle =  tripletTokenizer.nextToken();
				System.out.println("=================== fileNameAuthFIle  "+ fileNameAuthFIle);
				if(!fileNames.contains(fileNameAuthFIle)){
					return UNAUTHORIZED_CHANGES_MADE;
				}

			}
			
			return "all files listed on auth file exist";

		}
		
		else{
			try {
				authFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return "";
	}





	private String checkFile(File file, String userName) {
		String retVal = file.getName();

		String authDirPath = USERS_DIR_PATH + File.separator + userName + File.separator + "auth";
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
				return UNAUTHORIZED_CHANGES_MADE;

			}

			int tripletIndex = fileContext.indexOf("<"+file.getName());

			if(tripletIndex == -1){
				return UNAUTHORIZED_CHANGES_MADE;
			}

			String fileTriplet = fileContext.substring( tripletIndex + 1 , fileContext.indexOf('>', tripletIndex));
			StringTokenizer tokenizer = new StringTokenizer(fileTriplet, ",");

			if(!file.getName().equals(tokenizer.nextToken())){
				retVal = UNAUTHORIZED_CHANGES_MADE;
			}

			String tag = tokenizer.nextToken();

			if(file.length() != Long.parseLong(tokenizer.nextToken())){
				retVal = UNAUTHORIZED_CHANGES_MADE;
			}

		}

		else{
			try {
				authFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println(")(*&^$#%@ retVal " + retVal);
		return retVal;
	}


}
