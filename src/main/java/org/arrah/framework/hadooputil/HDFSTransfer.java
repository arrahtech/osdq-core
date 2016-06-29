package org.arrah.framework.hadooputil;

/***********************************************
 *     Copyright to Arrah Technology 2013      *
 *     http://www.arrahtec.org                 *
 *                                             *
 * Any part of code or file can be changed,    *
 * redistributed, modified with the copyright  *
 * information intact                          *
 *                                             *
 * Author$ : Vivek Singh                       *
 * Author$ : Dheeraj Chugh                     *
 *                                             *
 ***********************************************/

/* This file is used for transferring data 
 * between localfile system and hdfs
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


public class HDFSTransfer {
	
	private Configuration conf = null; 
	private FileSystem fs = null;
	int progressctr = 0;
	private boolean success = false;
	
	private HDFSTransferProgressListener hdfsTransferProgressListener;
	/**
	 * Constructor accepting the configuration parameter
	 * <p>
	 * It accepts the default HadoopDFS URL of the format 
	 * hdfs://hostAddress:port
	 * @param  fsDefaultName  HadoopDFS URL
	 */
	public HDFSTransfer(HDFSTransferProgressListener hdfsTransferProgressListener, String fsDefaultName){
		this.hdfsTransferProgressListener = hdfsTransferProgressListener;
	  conf = new Configuration();
		conf.set("fs.default.name", fsDefaultName);
	}
	
	public HDFSTransfer(HDFSTransferProgressListener hdfsTransferProgressListener){
		// default constructor
	  this.hdfsTransferProgressListener = hdfsTransferProgressListener;
	}
	
	/**
	 * Copies a particular file from user's local machine to HDFS 
	 * <p>
	 * This method will first check, if the file with the same name as source file 
	 * already exists in HDFS. If it exists it will overwrite the existing file in HDFS
	 * @param  sourcePath  path of the source object; like a particular file (residing on the local machine) that needs to be moved to HDFS
	 * @param  destPath destination path (of HDFS of the format) where the files need to be copied
	 * @return true if file is successfully copied else false
	 */
	public boolean moveToHDFS(String sourcePath, String destPath) throws IOException{
		
		success = false;
		fs = FileSystem.get(conf);
	
		final File file = new File(sourcePath);
		final FileInputStream readFile = new FileInputStream(file);
		
		// check if director or file ending with "/" means directory
		if (destPath.endsWith("/") == true)
			destPath = destPath + file.getName();
		
		System.out.println("Going to write file: "+destPath);
		Path outFile = new Path(destPath);
		checkOutFile(outFile);
		
		final FSDataOutputStream out = fs.create(outFile);
		final long filesize = file.length();
		
				progressctr=0;
				int progressfactor = 0;
				
				byte buffer[];
				try {
					
					if(filesize>25600){
						buffer = new byte[256];
						progressfactor= (int)(filesize/25600); 
					}
					else if(filesize>100)
					{
						buffer = new byte[1];
						progressfactor= (int)(filesize/100); 
					}
					else{
						buffer = new byte[1];
				
					}
					int bytesRead = 0;
				    int bytectr=0;
				    
					while ((bytesRead = readFile.read(buffer)) > 0) {
						out.write(buffer, 0, bytesRead);
						bytectr++;
						if(filesize>100){
							if(bytectr%progressfactor == 0 && progressctr<=100 )
							{
								progressctr++;
								hdfsTransferProgressListener.progressUpdate(progressctr);
							}
						} else if(filesize<100){
							if(!(progressctr ==  ((filesize)*((int)(100/filesize)))))
								progressctr = progressctr + (int)(100/filesize);
				        	if(progressctr == ((filesize)*((int)(100/filesize))))
				        		progressctr=100;
				        	hdfsTransferProgressListener.progressUpdate(progressctr);
						}
				    }
					success = true;
				
				} catch (IOException e) {
					System.out.println( e.getMessage());
					System.out.println( "Error while copying file");
				} finally{
					
					try {
						readFile.close();
						out.close();
					} catch (IOException e) {
						System.out.println(e.getMessage());
						System.out.println( "Error while closing files");
					}
				}
				if(progressctr>=100){
					System.out.println( "Copy Complete");
				}					

		return success;
	}
	 
    
	/**
	 * Copies a particular file from HDFS to the user's local machine
	 * <p>
	 * This method will first check, if the file exists in HDFS. 
	 * If the file does not exists in HDFS throws FileNotFounException.
	 * Overwrites the file if the file with same name exists in user's local machine
	 * @param  sourcePath  path of the source object; like a particular file (residing in HDFS) that needs to be moved to user's local machine
	 * @param  destPath destination path (of user's local machine) where the files need to be copied
	 * @return true if file is successfully copied else false
	 */
	public boolean moveFromHDFS(String sourcePath, String destPath) throws IOException{
		
		success = false;
		fs = FileSystem.get(conf);
		
		final Path inFile = new Path(sourcePath);
		final FSDataInputStream readFile = fs.open(inFile);
		
		File file = new File(destPath);
		final FileOutputStream out = new FileOutputStream(file);
		final long filesize = fs.getFileStatus(inFile).getLen();
		
				progressctr=0;
				int progressfactor= (int)(filesize/100); 
				
			    byte buffer[];
				
			    try { 
					if(filesize>25600){
						buffer = new byte[256];
						progressfactor= (int)(filesize/25600); 
					}
					else if(filesize>100)
					{
						buffer = new byte[1];
						progressfactor= (int)(filesize/100); 
					}
					else{
						buffer = new byte[1];
					}
					
					int bytesRead = 0;
				    int bytectr=0;
					
					while ((bytesRead = readFile.read(buffer)) > 0) {
				        out.write(buffer, 0, bytesRead);
				        bytectr++;
				        if(filesize>100){
							if(bytectr%progressfactor == 0 && progressctr<=100)
							{
								progressctr++;					
								hdfsTransferProgressListener.progressUpdate(progressctr);
							}
				        } else if(filesize<100){
				        	if(!(progressctr ==  ((filesize)*((int)(100/filesize)))))
								progressctr = progressctr + (int)(100/filesize);
				        	if(progressctr == ((filesize)*((int)(100/filesize))))
				        		progressctr=100;					
				        	hdfsTransferProgressListener.progressUpdate(progressctr);
				        }
				    }   
					
					success = true;
					
				} catch (IOException e) {
					System.out.println(e.getMessage());
					System.out.println( "Error while copying file");
				} finally {
					try {
						out.close();
						readFile.close();    
					} catch (IOException e) {
						System.out.println(e.getMessage());
						System.out.println( "Error while closing files");
					}
				}
			
			    if(progressctr>=100){
			    	System.out.println("Copy Complete");
				}


		return success;
	}

	/* This function is used for reading some bytes from file to show into preview section */
	public String readFile(String filepath, String filesystem, String readtype, int limit) throws IOException{
		
		String filetext = "";
		int bytesRead = 0,linesenctd = 0,bytectr=0; //initialize
		
		if(filesystem.equalsIgnoreCase("hdfs")){
			fs = FileSystem.get(conf);
			Path inFile = new Path(filepath);
			FSDataInputStream readFile = fs.open(inFile);

			try { 
				if(readtype.equalsIgnoreCase("bybytes")){
					
					while ((bytesRead = readFile.read()) > 0 && limit > 0) {
				        filetext = filetext + (char)bytesRead;
				        limit = limit -1;
				    }   
				}
				else if(readtype.equalsIgnoreCase("byline")){
					
					while ((bytesRead = readFile.read()) > 0 && linesenctd<limit) {
				        filetext = filetext + (char)bytesRead;
				        
				        if((char)bytesRead == '\n')
				        	linesenctd = linesenctd +1;
				    }   
				}
				else if(readtype.equalsIgnoreCase("bydelim")){
					
					while ((bytesRead = readFile.read()) > 0 && bytectr < 65536) {
				        filetext = filetext + (char)bytesRead;
				        
				        bytectr++;
				        
				        if(bytesRead==limit && bytesRead!=10){
				        	filetext = filetext + '\n';
				        }
				    }   
				}	
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.out.println( "Error while reading HDFS file");
			} finally {
				readFile.close();    
			}
		}
		else if(filesystem.equalsIgnoreCase("local")){
			
			File file = new File(filepath);
			FileInputStream readFile = new FileInputStream(file);
			
			try { 
				if(readtype.equalsIgnoreCase("bybytes")){
					while ((bytesRead = readFile.read()) > 0 && limit>0) {
				        filetext = filetext + (char)bytesRead;
				        limit = limit -1;
				    }   
				}
				else if(readtype.equalsIgnoreCase("byline")){
					
					while ((bytesRead = readFile.read()) > 0 && linesenctd<limit) {
				        filetext = filetext + (char)bytesRead;
				        
				        if((char)bytesRead == '\n')
				        	linesenctd = linesenctd +1;
				    }   
				}
				else if(readtype.equalsIgnoreCase("bydelim")){
					
					while ((bytesRead = readFile.read()) > 0 && bytectr < 65536) { // 1024 *64
				        filetext = filetext + (char)bytesRead;
				        
				        bytectr++;
				        
				        if(bytesRead==limit && bytesRead !=10){
				        	filetext = filetext + '\n';
				        }
				    }   
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
				System.out.println( "Error while reading Local Source file");
			} finally {
				readFile.close();    
			}
		}
		return filetext;
	}
	
	private void checkOutFile(Path outFile) throws IOException{
		
		if(fs.exists(outFile)){		
			System.out.println("File with same name already exists.. Will overwrite file");
			fs.delete(outFile, true);
		}
	}
	
	
	/* This function will check whether the path is valid
	 * 
	 */
	public static boolean testhdpath(String hdpath){
		if (hdpath == null || "".equals(hdpath))
			return false;

		Configuration conf = new Configuration();
		conf.set("fs.default.name", hdpath);
		
		try {
			FileSystem fs = FileSystem.get(conf);
			fs.listStatus(new Path("/"));  
			return true;
		} catch (IOException e) {
			System.out.println("File Validation error:"+e.getLocalizedMessage());
			return false;
		}
		
	}

}
