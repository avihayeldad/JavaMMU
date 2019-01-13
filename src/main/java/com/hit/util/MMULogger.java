package com.hit.util;

import java.io.IOException;
import java. util. logging. FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class MMULogger
{
	public final static String DEFAULT_FILE_NAME = "log.txt";
	private FileHandler m_handler;
	private static MMULogger m_Instance;
	
	private MMULogger() throws SecurityException, IOException
	{ 
		m_handler = new FileHandler("logs/" + DEFAULT_FILE_NAME);	
		m_handler.setFormatter(new OnlyMessageFormatter());
	}
	
	public static MMULogger GetInstance()
	{
		if(m_Instance == null)
		{
			try 
			{
				m_Instance = new MMULogger();		
			} 
			catch (SecurityException e) 
			{
				MMULogger.GetInstance().write("SecurityException - MMULogger GetInstance method", Level.SEVERE);
			} 
			catch (IOException e) 
			{
				MMULogger.GetInstance().write("IOException - MMULogger GetInstance method", Level.SEVERE);
			}
		}	
		
		return m_Instance;
	}

	public synchronized void write (String i_command, Level i_level) 
	{	
		m_handler.publish(new LogRecord(i_level, i_command +"\n\n"));	
		m_handler.flush();
	}
	
	public class OnlyMessageFormatter extends Formatter
	{
		public OnlyMessageFormatter () { super(); }
		
		public String format (final LogRecord record)
		{
			return record.getMessage();
		}
	}
}

