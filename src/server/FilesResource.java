package server;

import java.io.File;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/Files")
public class FilesResource {

	private static final String USERS_DIR_PATH = "C:/omri/study/sem8/security/codeJava/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/UploadServletApp/users";
	
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
				fileNames.add(fName);
			} 
			else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + fName);
			}
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
			}else{
				System.out.println("Delete operation is failed.");
			}

		}catch(Exception e){

			e.printStackTrace();

		}
	}
	
	
}
