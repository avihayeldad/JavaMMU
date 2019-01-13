package com.hit.memoryunits;

import java.util.LinkedHashMap;
import java.util.logging.Level;
import com.hit.exception.HardDiskException;
import com.hit.util.MMULogger;

public class HardDisk extends java.lang.Object
{
	public static final int _SIZE = 8;
	public static final String DEFAULT_FILE_NAME = "";
	
	private java.util.LinkedHashMap<java.lang.Long,Page<byte[]>> m_pages;
	private static HardDisk m_HardDisk = null;
	
	private HardDisk()
	{
		m_pages = new LinkedHashMap<Long, Page<byte[]>>();
		fillHardDisk();
	}

	private void fillHardDisk() 
	{
		for (int i = 0; i < 100; ++i)
		{
			byte[] pageContent = {0};
			m_pages.put((long)i, new Page<byte[]>((long)i, pageContent));
		}
	}

	public static HardDisk getInstance()
	{
		if (m_HardDisk == null)
		{
			m_HardDisk = new HardDisk();
		}
		
		return 	m_HardDisk;
	}

	public Page<byte[]> pageFault(java.lang.Long i_pageId) throws HardDiskException
	{
		MMULogger.GetInstance().write("PF:" + i_pageId, Level.INFO);
		
		if (!m_pages.containsKey(i_pageId))
		{
			MMULogger.GetInstance().write("HardDiskException - The HD didn't contains the page ID " + i_pageId, Level.SEVERE);
			throw new HardDiskException();
		}
				
		return m_pages.get(i_pageId);		          
	}
	
	public Page<byte[]> pageReplacement(Page<byte[]> i_pageToHardDisk, java.lang.Long i_pageIdToRAM) throws HardDiskException
	{
		if (i_pageIdToRAM != null)
		{
			if (!m_pages.containsKey(i_pageIdToRAM))
			{
				MMULogger.GetInstance().write("HardDiskException - The HD didn't contains the page ID " + i_pageIdToRAM.longValue(), Level.SEVERE);
				throw new HardDiskException();
			}
		}
		
		m_pages.put(i_pageToHardDisk.getPageId(), i_pageToHardDisk);
		MMULogger.GetInstance().write("PR:MTH " + i_pageToHardDisk.getPageId() + " MTR "+ i_pageIdToRAM, Level.INFO);
			
		return m_pages.get(i_pageIdToRAM);		
	}	
}
