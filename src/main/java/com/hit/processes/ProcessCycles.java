package com.hit.processes;

public class ProcessCycles extends java.lang.Object
{
	private java.util.List<ProcessCycle> m_processCycles;
	
	public ProcessCycles(java.util.List<ProcessCycle> i_processCycles) 
	{
		m_processCycles = i_processCycles;
	}

	public java.util.List<ProcessCycle> getProcessCycles()
	{
		return m_processCycles;
	}

	public void setProcessCycles(java.util.List<ProcessCycle> i_processCycles)
	{
		m_processCycles = i_processCycles;
	}

	@Override
	public java.lang.String toString()
	{
		String stringToReturn = "";
		for(ProcessCycle process : m_processCycles)
		{
			stringToReturn += process.toString();
			stringToReturn += " ";
		}
		
		return stringToReturn;
	}
}
