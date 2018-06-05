package server;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FileNamesList {
	private String name;
	//private ArrayList<String> fileList;
	
	
	public FileNamesList(String name){
		this.name = name;
		//this.fileList = new ArrayList<String>();
		
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	/*public List getFileList() {
		return fileList;
	}

	public void Add(String fileName){
		fileList.add(fileName);
	}
	
	public void Print(){
		for(int i = 0; i < fileList.size() ; i++){
			System.out.println(fileList.get(i));
		}
	}*/

}
