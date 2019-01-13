package com.hit.memoryunits;

import java.util.LinkedHashMap;
import java.util.logging.Level;
import com.hit.util.MMULogger;

public class RAM extends java.lang.Object
{
	private int m_capacity;
	private java.util.LinkedHashMap<java.lang.Long,Page<byte[]>> m_pages;
	
	public RAM(int i_initialCapacity) 
	{
		m_pages = new LinkedHashMap<java.lang.Long,Page<byte[]>>();
		m_capacity = i_initialCapacity;
		
		MMULogger.GetInstance().write("RC:" + m_capacity, Level.INFO);
	}
	
	public java.util.Map<java.lang.Long,Page<byte[]>> getPages()
	{
		return m_pages;
	}
	
	public void setPages(java.util.Map<java.lang.Long,Page<byte[]>> i_pages)
	{
		m_pages = (LinkedHashMap<Long, Page<byte[]>>) i_pages;
	}

	public Page<byte[]> getPage(java.lang.Long i_pageId)
	{
		return m_pages.get(i_pageId);
	}
	
	public void addPage(Page<byte[]> i_page)
	{
		m_pages.put(i_page.getPageId(), i_page);
	}
	
	public void removePage(Page<byte[]> i_page)
	{
		m_pages.remove(i_page.getPageId());
	}
	
	@SuppressWarnings("unchecked")
	public Page<byte[]>[] getPages(java.lang.Long[] i_pageIds)
	{
		Page<byte[]>[] pagesToReturn = new Page[i_pageIds.length - 1];
	
		for(int i = 0; i < i_pageIds.length; ++i)
		{
			pagesToReturn[i] = 	m_pages.get(i_pageIds[i]);
		}
		
		return pagesToReturn;
	}
	
	public void addPages(Page<byte[]>[] i_pages)
	{
		for(int i = 0; i < i_pages.length; ++i)
		{
			m_pages.put(i_pages[i].getPageId(), i_pages[i]);
		}
	}
	
	public void removePages(Page<byte[]>[] i_pages)
	{
		for(int i = 0; i < i_pages.length; ++i)
		{
			m_pages.remove(i_pages[i].getPageId());
		}
	}
	
	public int getInitialCapacity()
	{
		return m_capacity;
	}
	
	public void setInitialCapacity(int i_capacity)
	{
		m_capacity = i_capacity;
	}
}
