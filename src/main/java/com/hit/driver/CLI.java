package com.hit.driver;

import com.hit.view.*;
import java.io.IOException;
import java.util.Observable;
import java.util.Scanner;
import java.util.logging.Level;
import com.hit.util.MMULogger;

public class CLI extends Observable implements java.lang.Runnable, View
{
	private java.io.InputStream m_inputStream;
	private java.io.OutputStream m_outputStream;
	private Scanner m_scanner;
	public static final java.lang.String START = "start";
	public static final java.lang.String STOP = "stop";
	public static final java.lang.String LRU = "LRU";
	public static final java.lang.String MRU = "MRU";
	public static final java.lang.String RANDOM = "RANDOM";
	
	public CLI(java.io.InputStream i_inputStream, java.io.OutputStream i_outputStream) 
	{
		m_inputStream = i_inputStream;
		m_outputStream = i_outputStream;
		m_scanner = new Scanner(m_inputStream);
		m_scanner.useDelimiter(" ");
	}
	
	@Override
	public void run() 
	{	
		String operation = "";
		while (!operation.equals(STOP))
		{
			switch (m_scanner.nextLine())
			{
			case START:
				start();
				break;
			case STOP:
				try 
				{
					operation = STOP;
					setChanged();
					notifyObservers(new String[] {"stop"});
					write("Thank you.\n");
				} 
				catch (IOException e) 
				{
					MMULogger.GetInstance().write("IOException - invalid value in CLI stop method", Level.SEVERE);
				}	
				break;
			default:
				try 
				{
					write("Not a valid command.\n");
				} 
				catch (IOException e) 
				{
					MMULogger.GetInstance().write("IOException - invalid value in CLI run method", Level.SEVERE);
				}	
			}
		}
	}
	
	public void write(java.lang.String i_string) throws IOException
	{
		m_outputStream.write(i_string.getBytes());
		m_outputStream.flush();
	}

	private String getAlgoName (String i_UserAnswer)
	{
		String algoName = "";
		char[] answer = i_UserAnswer.toCharArray();
		for (int i =0; i < answer.length; ++i)
		{
			if (answer[i] == ' ')
			{
				algoName = i_UserAnswer.substring(0, i);
				break;
			}
		}
		
		return algoName;
	}
	
	private String getCapacity (String i_UserAnswer)
	{
		int i = 0;
		char[] answer = i_UserAnswer.toCharArray();
		
		while (answer[i] != ' ') ++i;
		++i;
		
		return i_UserAnswer.substring(i, i_UserAnswer.length());
	}
	
	@Override
	public void start()
	{
		String capacity;
		String cachAlgo;
		
		try {
			write("Please enter required algorithm and RAM capacity\n");
		} catch (IOException e) {
		}
		String Answer = "";
		while(Answer == "")
		{
			Answer = m_scanner.nextLine();
		}
		cachAlgo = getAlgoName(Answer);
		switch (cachAlgo)
		{
			case LRU:
				break;
			case MRU:
				break;
			case RANDOM:
				break;
			default:
			try {
				write("Not a valid command.\n");
			} catch (IOException e) {
			}
				return;					
		}

		capacity = getCapacity(Answer);
		setChanged();
       	notifyObservers(new String[]{"start", cachAlgo ,capacity});
	}
}

