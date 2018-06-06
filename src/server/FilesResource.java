package server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/Files")
public class FilesResource {
//C:/omri/study/sem8/security/codeJava/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/UploadServletApp/upload
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getFileNames(){
		String dir = "C:/omri/study/sem8/security/codeJava/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/UploadServletApp/upload";
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
		List<String> fileNames = new ArrayList<String>();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			String fName = listOfFiles[i].getName();
			if (listOfFiles[i].isFile()) { 
				fileNames.add(fName);
				System.out.println( fName);

			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + fName);
			}
		}
		
		//GenericEntity<List<String>> entity = new GenericEntity<List<String>>(fileNames) {};
		//Response response = Response.ok(entity).build();
		//return new JSONArray(fileNames);
		
		    //fileList.Print();
		    //return fileList;
		return fileNames;
	}
}
