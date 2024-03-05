package com.snakindia.cmd.exec.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.log4j.Logger;


public class Utilities {

	static Logger logger = Logger.getLogger(Utilities.class);
	
	public static void printStackTraceToLogs(String className, String methodName, Exception e)
	{
		try
		{
			Writer writer = new StringWriter();
			PrintWriter print = new PrintWriter(writer);
			e.printStackTrace(print);
			logger.info(className+"::"+methodName+":: Error :: > " + e.getMessage());
			logger.info(className+"::"+methodName+":: Error :: > " + writer.toString());
			print = null;
			writer= null;
		}
		catch(Exception f)
		{
			printStackTraceToLogs(Utilities.class.getName(), "printStackTraceToLogs()", e);
		}
	}	
}
