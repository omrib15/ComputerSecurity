package server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/Files")
public class FilesResource {
//C:/omri/study/sem8/security/codeJava/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/UploadServletApp/upload
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<FileNamesList> getFileNames(){
		String dir = "C:/omri/study/sem8/security/codeJava/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/UploadServletApp/upload";
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
		List<FileNamesList> fileNames = new ArrayList<FileNamesList>();
		for (int i = 0; i < listOfFiles.length; i++) {
			String fName = listOfFiles[i].getName();
			if (listOfFiles[i].isFile()) { 
				fileNames.add(new FileNamesList(fName));
				System.out.println( fName);

			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + fName);
			}
		}
		    
		    //fileList.Print();
		    //return fileList;
		return fileNames;
	}
}
