package com.snakindia.cmd.exec.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.snakindia.cmd.exec.utils.ApplicationProperties;
import com.snakindia.cmd.exec.utils.SendMailUsingAuthentication;
import com.snakindia.cmd.exec.utils.Utilities;


public class StartCommandsExecutionImpl {
	
	final static Logger log = Logger.getLogger(StartCommandsExecutionImpl.class);

	public static void main(String[] args) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss a");
		Date systemDate = new Date();
		System.setProperty("current.date.time", dateFormat.format(systemDate));
		// initialize loggers
		File f = new File(StartCommandsExecutionImpl.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		PropertyConfigurator.configure(f.getParentFile().getAbsolutePath()+"/log.properties");
		f=  null;
		try
		{
			String homeDirPath = ApplicationProperties.getProperty("home.dir.path");
			String commands="git config --global --add safe.directory "+homeDirPath+"oe-core/.repo/repo,"
					+ "repo init -u git://git.toradex.com/toradex-manifest.git -b kirkstone-6.x.y -m tdxref/default.xml,"
					+ "repo sync,. export,bitbake core-image-minimal";
			
			
//			String commands="cd /home/ubuntu/oe-core,repo init -u git://git.toradex.com/toradex-manifest.git -b kirkstone-6.x.y -m tdxref/default.xml," + 
//					"repo sync," + 
//					"bitbake core-image-minimal";
//			
//			String commands="sudo apt install gawk wget git diffstat unzip texinfo gcc build-essential chrpath socat cpio python3 python3-pip python3-pexpect xz-utils debianutils iputils-ping python3-git python3-jinja2 libegl1-mesa libsdl1.2-dev python3-subunit mesa-common-dev zstd liblz4-tool file locales libacl1";
//			commands+=",";
//			commands+="sudo locale-gen en_US.UTF-8,export LC_ALL=\"en_US.UTF-8\",export LC_CTYPE=\"en_US.UTF-8\",sudo locale-gen \"en_US.UTF-8\",sudo update-locale LANG=en_US.UTF-8,";
//			commands+="mkdir ~/bin,export PATH=~/bin:$PATH,curl https://commondatastorage.googleapis.com/git-repo-downloads/repo > ~/bin/repo,chmod a+x ~/bin/repo,";
//			commands+="sudo apt install git,mkdir ${HOME}/oe-core,cd ${HOME}/oe-core,repo init -u git://git.toradex.com/toradex-manifest.git -b kirkstone-6.x.y -m tdxref/default.xml,repo sync";
			
			
			String[] tok = commands.split(",");
			if(null!=tok && tok.length>0)
			{
				for(int a=0;a<tok.length;a++)
				{
					log.info("########################## START ############################");
					log.info("## EXECUTE COMMAND :: >" + tok[a]);
					System.out.println("## EXECUTE COMMAND :: >" + tok[a]);
					String s;
					String error;
			        Process p = null;
			        StringBuilder str = new StringBuilder();
			        BufferedReader br = null;
			        BufferedReader errorBr = null;
			        try 
			        {
			        	if(a==0)
			        	{
			        		String[] cmd = { "/bin/sh", "-c", "cd "+tok[a]+"; ls -l" };
			        		p = Runtime.getRuntime().exec(cmd);
			        		cmd = null;
			        	}
			        	else
			        	{
			        		p = Runtime.getRuntime().exec(tok[a]);
			        	}
			        	br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			        	while ((s = br.readLine()) != null)
			        	{
			        		System.out.println("line: " + s);
			        		str.append("line: " + s);
			        		log.info("line: " + s);
			        		str.append(System.getProperty("line.separator"));
			        	}
			        	p.waitFor();
			        
//			        	System.out.println ("exit: " + p.exitValue());
			        	log.info("------------ Exit :: >"+ p.exitValue());
			        	if(p.exitValue()!= 0)
			        	{
			        		errorBr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				        	while ((error = errorBr.readLine()) != null)
				        	{
				        		System.err.println("error: "+ error);
				        		str.append("error: "+ error);
				        		log.error("error: "+ error);
				        		str.append(System.getProperty("line.separator"));
				        	}
			        	}
			        	p.destroy();
			        	
			        	if(null!=str && null!=str.toString())
			        	{
			        		String path = ApplicationProperties.getProperty("command.logs.file.path");
			        		if(!path.endsWith("/"))
			        		{
			        			path = path+"/";
			        		}
			        		File dir = new File(path);
			        		if(!dir.exists() && !dir.isDirectory())
			        		{
			        			dir.mkdir();
			        		}
			        		dir = null;
			        		
			        		// add dateFormat.format(systemDate) as folder name
			        		path = path +dateFormat.format(systemDate)+"/";
			        		dir = new File(path);
			        		if(!dir.exists() && !dir.isDirectory())
			        		{
			        			dir.mkdir();
			        		}
			        		dir = null;
			        		
			        		String fName = "Command_"+(a+1)+"_"+dateFormat.format(systemDate)+".log";
			        		File commandLogFile = new File(path+fName);
			        		FileOutputStream fos = new FileOutputStream(commandLogFile);
			        		fos.write(str.toString().getBytes());
			        		fos.flush();
			        		fos.close();
			        		fos = null;
			        		commandLogFile = null;
			        		
			        		log.info("---- Command Execution Log File Written successfully at Path :: >"+ (path+fName));
			        		path = null;
			        		fName=null;
			        	}
			        	str = null;
			        } 
			        catch (Exception e) 
			        {
			        	Utilities.printStackTraceToLogs(StartCommandsExecutionImpl.class.getName(), "main()", e);
			        	if(null!=p)
			        	{
			        		p.destroy();
			        	}
			        }
			        finally
			        {
			        	if(null!=br)
			        	{
			        		br.close();
			        	}
			        	if(null!=errorBr)
			        	{
			        		errorBr.close();
			        	}
			        	br = null;
			        	errorBr = null;
			        }
					p= null;
					s = null;
					br = null;
					str = null;
					error = null;
					
					log.info("########################## END ############################");
					log.info("\n");
					log.info("\n");
				}
			}
			commands = null;
			tok = null;
			
			/*
			 * GENERATE ZIP FILE AND SEND EMAIL NOTIFICATION
			 */
			
			/*
			 * check if filesPathList is not null create zip directory and add all the files of the list to it
			 */
			String zipFilePath = ApplicationProperties.getProperty("command.logs.file.path");
			if(!zipFilePath.endsWith("/"))
			{
				zipFilePath = zipFilePath+"/";
			}
			File dir = new File(zipFilePath+dateFormat.format(systemDate));
			List<String> filesPathList = new ArrayList<String>();
			if(null!=dir && dir.exists() && null!=dir.listFiles() && dir.listFiles().length>0)
			{
				for(int a=0;a<dir.listFiles().length;a++) 
				{
					filesPathList.add(dir.listFiles()[a].getAbsolutePath());
				}
			}
			
			if(null!=filesPathList && filesPathList.size()>0)
			{
				// add zip extension
				zipFilePath=zipFilePath+dateFormat.format(systemDate)+".zip";
				
				//create ZipOutputStream to write to the zip file
		        FileOutputStream fos = new FileOutputStream(zipFilePath);
		        ZipOutputStream zos = new ZipOutputStream(fos);
		        for(String filePath : filesPathList){
//		            System.out.println("Zipping "+filePath);
		            log.info("---------zipping :: > " + filePath);
		            //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
		            ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length()+1, filePath.length()));
		            zos.putNextEntry(ze);
		            //read the file and write to ZipOutputStream
		            FileInputStream fis = new FileInputStream(filePath);
		            byte[] buffer = new byte[1024];
		            int len;
		            while ((len = fis.read(buffer)) > 0) {
		                zos.write(buffer, 0, len);
		            }
		            zos.closeEntry();
		            fis.close();
		        }
		        zos.close();
		        fos.close();
				dir = null;
				zos=  null;
				fos = null;
				
				
				log.info("-------- zip file path :: >"+ zipFilePath);
//				System.out.println("-------- zip file path :: >"+ zipFilePath);
				List<String> toList = new ArrayList<String>();
				String toEmailIds = ApplicationProperties.getProperty("EMAILS_TO_LIST");
				String[] emailTok = toEmailIds.split(",");
				// send notification with all logs attached
				if(null!=emailTok && emailTok.length>0)
				{
					for(int r=0;r<emailTok.length;r++)
					{
						toList.add(emailTok[r]);
					}
				}
				emailTok = null;
				toEmailIds = null;
				
				
				String subject="Commands Exection Job Completion :"+ dateFormat.format(systemDate);
				String message="<p>Hello,</p>";
				message+="<p>Commands Exuection Job scheduled at "+ dateFormat.format(systemDate)+" has been completed. Please find the attached logs zip for reference.</p>";
				message+="<p style=\"font-size:9px !important;\"><b>Note: </b> This is an autogenerated email. Please do not reply.</p>";
				message+="<p>Regards,<br>IT Support Team.</p>";
				
				SendMailUsingAuthentication.newPostHTMLMailWithAttachment(toList, subject, message, zipFilePath);
				
				toList = null;
				subject = null;
				message =null;
				zipFilePath = null;
			}
			else
			{
				List<String> toList = new ArrayList<String>();
				String toEmailIds = ApplicationProperties.getProperty("EMAILS_TO_LIST");
				String[] emailTok = toEmailIds.split(",");
				// send notification with all logs attached
				if(null!=emailTok && emailTok.length>0)
				{
					for(int r=0;r<emailTok.length;r++)
					{
						toList.add(emailTok[r]);
					}
				}
				emailTok = null;
				toEmailIds = null;
				
				// send notification - no logs generated for the run
				String subject="Commands Exection Job Completion :"+ dateFormat.format(systemDate);
				String message="<p>Hello,</p>";
				message+="<p>Commands Exuection Job scheduled at "+ dateFormat.format(systemDate)+" has been completed. No Logs were generated, please reach out to IT Support Team for further support.</p>";
				message+="<p style=\"font-size:9px !important;\"><b>Note: </b> This is an autogenerated email. Please do not reply.</p>";
				message+="<p>Regards,<br>IT Support Team.</p>";
				
				SendMailUsingAuthentication.newPostHTMLMail(toList, subject, message);
				
				toList = null;
				subject = null;
				message =null;
			}
			
			zipFilePath = null;
			filesPathList = null;
			dir = null;
		}
		catch(Exception e)
		{
			Utilities.printStackTraceToLogs(StartCommandsExecutionImpl.class.getName(), "main()", e);
			/*
			 * send notification = to IT Team
			 */
			
			List<String> toList = new ArrayList<String>();
			String toEmailIds = ApplicationProperties.getProperty("IT_SUPPORT_EMAIL_ID");
			String[] emailTok = toEmailIds.split(",");
			// send notification with all logs attached
			if(null!=emailTok && emailTok.length>0)
			{
				for(int r=0;r<emailTok.length;r++)
				{
					toList.add(emailTok[r]);
				}
			}
			emailTok = null;
			toEmailIds = null;
			
			// send notification - no logs generated for the run
			String subject="Exception - Commands Exection Job :"+ dateFormat.format(systemDate);
			String message="<p>Hello,</p>";
			message+="<p>Commands Exuection Job scheduled at "+ dateFormat.format(systemDate)+" has been failed during its execution, check server logs.</p>";
			message+="<p style=\"font-size:9px !important;\"><b>Note: </b> This is an autogenerated email. Please do not reply.</p>";
			message+="<p>Regards,<br>IT Support Team.</p>";
			
			SendMailUsingAuthentication.newPostHTMLMail(toList, subject, message);
			
			toList = null;
			subject = null;
			message =null;
		}
		dateFormat = null;
		systemDate  =null;
	}

}
