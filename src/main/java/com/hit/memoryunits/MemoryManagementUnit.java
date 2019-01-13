package com.hit.memoryunits;

import java.util.Map.Entry;
import java.util.logging.Level;
import com.hit.algorithm.IAlgoCache;
import com.hit.exception.HardDiskException;
import com.hit.util.MMULogger;

public class MemoryManagementUnit extends java.lang.Object 
{
	private com.hit.algorithm.IAlgoCache<java.lang.Long,java.lang.Long> m_paggingAlgorithm;
	private RAM m_RAM;
	
	public MemoryManagementUnit(int i_ramCapacity, IAlgoCache<java.lang.Long,java.lang.Long> i_paggingAlgorithm)
	{
		m_paggingAlgorithm = i_paggingAlgorithm;
		m_RAM = new RAM(i_ramCapacity);
	}
	
	@SuppressWarnings("unchecked")
	public Page<byte[]>[] getPages(Long[] i_pageIds, int i_processID) throws java.io.IOException, HardDiskException
	{	

		Page<byte[]>[] pagesToReturn = new Page[i_pageIds.length];
		for(int i = 0; i < i_pageIds.length; ++i) 
		{			
			Page<byte[]> pageContent = m_RAM.getPage(i_pageIds[i]);//search page in algoCache
			if (pageContent != null) //if page in RAM
			{
				pagesToReturn[i] = pageContent;
			}
			else//if page not in RAM ask HD
			{
				pageContent= HardDisk.getInstance().pageFault(i_pageIds[i]); //get page from HD		
				if (pageContent != null)
				{
					Long pageToRemove = m_paggingAlgorithm.putElement(pageContent.getPageId(),pageContent.getPageId()); //get witch page to delete
					if (pageToRemove != null)
					{					
						pageContent = HardDisk.getInstance().pageReplacement(m_RAM.getPage(pageToRemove), i_pageIds[i]);
						m_paggingAlgorithm.removeElement(pageToRemove);
						m_RAM.removePage(m_RAM.getPage(pageToRemove));
						//remove from RAM
					}
				}

				pagesToReturn[i] = pageContent;
				m_RAM.addPage(pageContent);
			}
					
			MMULogger.GetInstance().write("GP:P" + i_processID + " " + i_pageIds[i] + " " + byteArrayToString(pagesToReturn[i].getContent()), Level.INFO);
		}	
		return pagesToReturn;
	}
	
	private String byteArrayToString(byte[] i_byteArray)
	{
		String stringToReturn = "[";
		
		for (int i = 0; i < i_byteArray.length; ++i)
		{
			stringToReturn += i_byteArray[i];
			if (i != i_byteArray.length - 1)
			{
				stringToReturn += ", ";
			}				
		}
		
		stringToReturn += "]";
		
		return stringToReturn;
	}
	
	public void shutDown()
	{
		update();
	}
	
	public void update()
	{//update page in HD by RAM
		for(Entry<Long, Page<byte[]>> page : m_RAM.getPages().entrySet())
		{
			try 
			{
				HardDisk.getInstance().pageReplacement(page.getValue(), null);
			} 
			catch (HardDiskException e) 
			{
				MMULogger.GetInstance().write("The MMU update method didn't work currectly", Level.SEVERE);
				e.printStackTrace();
			}
		}
	}

	public com.hit.algorithm.IAlgoCache<java.lang.Long,java.lang.Long> getAlgo()
	{
		return m_paggingAlgorithm;
	}
	
	public void setAlgo(com.hit.algorithm.IAlgoCache<java.lang.Long,java.lang.Long> i_algo)
	{
		m_paggingAlgorithm = i_algo;
	}
	
	public RAM getRam()
	{
		return m_RAM;
	}
	
	public void setRam(RAM i_ram)
	{
		m_RAM = i_ram;
	}
	
}
