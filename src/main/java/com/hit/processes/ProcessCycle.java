package com.hit.processes;

public class ProcessCycle extends java.lang.Object
{
	private java.util.List<java.lang.Long> m_pages;
	private Integer m_sleepMs;
	private java.util.List<byte[]> m_data;
	
	public ProcessCycle(java.util.List<java.lang.Long> i_pages, Integer i_sleepMs, java.util.List<byte[]> i_data)
	{
		m_pages = i_pages;
		m_sleepMs = i_sleepMs;
		m_data = i_data;
	}

	public java.util.List<java.lang.Long> getPages()
	{
		return m_pages;
	}
	
	public void setPages(java.util.List<java.lang.Long> i_pages)
	{
		m_pages = i_pages;
	}
	
	public Integer getSleepMs()
	{
		return m_sleepMs;
	}
	
	public void setSleepMs(Integer i_sleepMs)
	{
		m_sleepMs = i_sleepMs;
	}

	public java.util.List<byte[]> getData()
	{
		return m_data;
	}

	public void setData(java.util.List<byte[]> i_data)
	{
		m_data = i_data;
	}

	@Override
	public java.lang.String toString()
	{
		return m_data.toString();
	}
}
