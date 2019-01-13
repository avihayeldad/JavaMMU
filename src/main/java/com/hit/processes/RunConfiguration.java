package com.hit.processes;

public class RunConfiguration extends java.lang.Object
{
	private java.util.List<ProcessCycles> m_processesCycles;
	
	public RunConfiguration(java.util.List<ProcessCycles> i_processesCycles)
	{
		m_processesCycles = i_processesCycles;
	}
	
	public java.util.List<ProcessCycles> getProcessesCycles()
	{
		return m_processesCycles;
	}
	
	public void setProcessesCycles(java.util.List<ProcessCycles> i_processesCycles)
	{
		m_processesCycles = i_processesCycles;
	}
	
	@Override
	public java.lang.String toString()
	{
		String stringToReturn = "";
		for(ProcessCycles process : m_processesCycles)
		{
			stringToReturn += process.toString();
			stringToReturn += " ";
		}
		
		return stringToReturn;
	}
}
