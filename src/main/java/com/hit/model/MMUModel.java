package com.hit.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import com.hit.algorithm.IAlgoCache;
import com.hit.algorithm.LRUAlgoCacheImpl;
import com.hit.algorithm.MRUAlgoCacheImpl;
import com.hit.algorithm.Random;
import com.hit.memoryunits.MemoryManagementUnit;
import com.hit.processes.ProcessCycle;
import com.hit.processes.ProcessCycles;
import com.hit.processes.RunConfiguration;
import com.hit.util.MMULogger;
import LogDataLinkedList.BaseLink;
import LogDataLinkedList.ErrorLink;
import LogDataLinkedList.GetPagesLink;
import LogDataLinkedList.PageFaultLink;
import LogDataLinkedList.PageReplacementLink;
import LogDataLinkedList.ProcessNumbersLink;
import LogDataLinkedList.RamCapacityLink;
import gvjava.org.json.JSONArray;
import gvjava.org.json.JSONException;
import gvjava.org.json.JSONObject;

public class MMUModel extends Observable implements Model 
{
	private static AtomicInteger m_processIdCreator = new AtomicInteger(0);
	public final static ExecutorService Pool = Executors.newCachedThreadPool();
	String m_configuration;
	
	public MMUModel(String i_configuration)
	{
		m_configuration = i_configuration;
	}
	
	@Override
	public void start(java.lang.String[] i_command)
	{
		int capacity = Integer.parseInt(i_command[1]);
		IAlgoCache<Long, Long> algo = getAlgoByName(i_command[0], capacity);
		MemoryManagementUnit mmu = new MemoryManagementUnit(capacity, algo);
		RunConfiguration runConfig = null;
		try {
			runConfig = readConfigurationFile();
		} catch (FileNotFoundException e) {
		} catch (JSONException e) {
		}
		List<ProcessCycles> processCycles = runConfig.getProcessesCycles();
		List<com.hit.processes.Process> processes = createProcesses(processCycles, mmu);
		runProcesses(processes);
		mmu.shutDown();
		setChanged();
       	notifyObservers();
	}
	
	private IAlgoCache<Long, Long> getAlgoByName(String i_algoName, int i_capacity) 
	{
		IAlgoCache<Long, Long> algoToReturn = null;
		switch(i_algoName)
		{
		case "LRU":
			algoToReturn = new LRUAlgoCacheImpl<Long, Long>(i_capacity);
			break;
		case "MRU":
			algoToReturn = new MRUAlgoCacheImpl<Long, Long>(i_capacity);
			break;
		case "RANDOM":
			algoToReturn = new Random<Long, Long>(i_capacity);
			break;			
		}
		
		return algoToReturn;
	}

	public void runProcesses(java.util.List<com.hit.processes.Process> applications)
	{
		List<Future<Boolean>> tasksIsComplete = new ArrayList<Future<Boolean>>();
		
		for (com.hit.processes.Process process : applications)
		{
			tasksIsComplete.add(Pool.submit(process));
		}
		
		MMULogger.GetInstance().write("PN:" + applications.size(), Level.INFO);
		
		for (Future<Boolean> task : tasksIsComplete)
		{
			while (!task.isDone());
		}
	}
	
	public java.util.List<com.hit.processes.Process> createProcesses(java.util.List<ProcessCycles> i_appliocationsScenarios,
            com.hit.memoryunits.MemoryManagementUnit i_mmu)
	{
		java.util.List<com.hit.processes.Process> processListToReturn = new java.util.ArrayList<com.hit.processes.Process>();
		
		for(ProcessCycles processCycles : i_appliocationsScenarios)
		{			
			processListToReturn.add(new com.hit.processes.Process(m_processIdCreator.getAndIncrement(), i_mmu, processCycles));
		}
		
		return processListToReturn;		
	}
	
	public RunConfiguration readConfigurationFile() throws FileNotFoundException, JSONException
	{
		JSONObject jsonFile = new JSONObject(getFileByAddress(m_configuration)); //set by lecturer
		java.util.List<ProcessCycles> ProcessesCycles = new java.util.ArrayList<ProcessCycles>(); //list for RunConfiguration constructor
	
		JSONArray processesCyclesJsonArray = jsonFile.getJSONArray("processesCycles");
		for (int i = 0; i < processesCyclesJsonArray.length(); ++i) //for any ProcessCycles in the JSON object
		{
			JSONArray processCyclesJsonArray =  processesCyclesJsonArray.getJSONObject(i).getJSONArray("processCycles");
			java.util.List<ProcessCycle> processCycles = new java.util.ArrayList<ProcessCycle>(); //list for current ProcessCycle constructor
			for (int j = 0; j < processCyclesJsonArray.length(); ++j) //for any ProcessCycle in current ProcessCycles
			{
				ProcessCycle currentProcessCycle = jsonObjectToProcessCycle(processCyclesJsonArray.getJSONObject(j));
				processCycles.add(currentProcessCycle);
			}
			ProcessesCycles.add(new ProcessCycles(processCycles));
		}
		
		return new RunConfiguration(ProcessesCycles);	
	}
	
	private String getFileByAddress(String i_fileAddress) throws FileNotFoundException 
	{
	    File file = new File(i_fileAddress);
	    @SuppressWarnings("resource")
		Scanner scanner = new Scanner(file);
	    String fileToReturn = "";
		 
	    while (scanner.hasNextLine())
	    {
	    	fileToReturn += scanner.nextLine();
	    }
	    
	    return fileToReturn;
	}

	private ProcessCycle jsonObjectToProcessCycle(JSONObject i_jsonObject) throws JSONException
	{
		java.util.List<java.lang.Long> pages = new java.util.ArrayList<java.lang.Long>();
		java.util.List<byte[]> data = new java.util.ArrayList<byte[]>();
		Integer sleepMs = i_jsonObject.getInt("sleepMs");

		JSONArray pageJsonArray = i_jsonObject.getJSONArray("pages");
		JSONArray dataJsonArray = i_jsonObject.getJSONArray("data");			
		for (int i = 0; i < pageJsonArray.length(); ++i)
		{
			pages.add(new Long(pageJsonArray.getInt(i)));
		}
	
		for (int i = 0; i < dataJsonArray.length(); ++i)
		{
			data.add(dataJsonArray.getString(i).getBytes());
		}

		return new ProcessCycle(pages, sleepMs, data);
	}

	
	public BaseLink createLinkedListByLogFile()
	{
		BaseLink ListToReturn = new BaseLink();
		Scanner scanner = null;
		String currentLine;
		BaseLink listPointer = ListToReturn;
		
		try {
			scanner = new Scanner(new FileReader("logs/"+MMULogger.DEFAULT_FILE_NAME));
		} catch (FileNotFoundException e) {
		}
		
		while(scanner.hasNextLine())
		{
			currentLine = scanner.nextLine();
			if (!currentLine.isEmpty())
			{
				if(currentLine.startsWith("RC"))
				{
					int capacity = Integer.valueOf(currentLine.substring(3, currentLine.length()));
					listPointer.Next = new RamCapacityLink("RC", capacity);
				}
				else if(currentLine.startsWith("PN"))
				{
					int processNumbers = Integer.valueOf(currentLine.substring(3, currentLine.length()));
					listPointer.Next = new ProcessNumbersLink("PN", processNumbers);
				}
				else if(currentLine.startsWith("PF"))
				{
					int pageNumber = Integer.valueOf(currentLine.substring(3, currentLine.length()));
					listPointer.Next = new PageFaultLink("PF", pageNumber);
				}
				else if(currentLine.startsWith("PR"))
				{
					int MTH = Integer.valueOf(currentLine.substring(7, currentLine.indexOf("MTR") - 1));
					int MTR = -1;
					
					if (currentLine.indexOf("null") == -1)
					{
						MTR = Integer.valueOf(currentLine.substring(currentLine.indexOf("MTR") + 4, currentLine.length()));	
					}
					listPointer.Next = new PageReplacementLink("PR", MTH, MTR);
				}
				else if(currentLine.startsWith("GP"))
				{
					int process = Integer.valueOf(currentLine.substring(4, currentLine.indexOf(" ")));
					int pageID = Integer.valueOf(currentLine.substring(currentLine.indexOf(" ") + 1, currentLine.indexOf("[") - 1));
					int[] data = {0};
					
					if (currentLine.indexOf("[") + 2 != currentLine.indexOf("]"))
					{
						data = getIntArrayFromString(currentLine.substring(currentLine.indexOf("[") + 1, currentLine.indexOf("]")));		
					}
					listPointer.Next = new GetPagesLink("GP", process, pageID, data);
				}
				else //Error message
				{
					listPointer.Next = new ErrorLink("Error", currentLine);
				}
				
				listPointer.Next.Prev = listPointer;
				listPointer = listPointer.Next;
			}
		}

		return ListToReturn;		
	}

	private int[] getIntArrayFromString(String i_ArrayString) 
	{
		String[] separateStrings = i_ArrayString.split(", ");
		int[] intArray = new int[separateStrings.length];
		
		for (int i = 0; i < separateStrings.length; ++i)
		{
			intArray[i] = Integer.valueOf(separateStrings[i]);
		}
		
		return intArray;
	}
}

