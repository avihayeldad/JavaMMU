package com.hit.processes;

import java.util.List;
import com.hit.memoryunits.MemoryManagementUnit;
import com.hit.memoryunits.Page;

public class Process extends java.lang.Object implements java.util.concurrent.Callable<java.lang.Boolean> 
{
	private MemoryManagementUnit m_mmu;
	private int m_processId;
	private ProcessCycles m_processCycles;
	
	@Override
	public Boolean call() throws Exception 
	{
		List<ProcessCycle> processCycles = m_processCycles.getProcessCycles();
		
		while (true)
		{
			for (ProcessCycle processCycle : processCycles)
			{	
				synchronized (m_mmu)
				{		
					Page<byte[]>[] systemPages = m_mmu.getPages(listToLongArray(processCycle.getPages()), m_processId);

					List<byte[]> processPages = processCycle.getData();
					for(int i = 0; i < systemPages.length; ++i)
					{
						systemPages[i].setContent(processPages.get(i));		
					}
				}
				
				Thread.sleep(processCycle.getSleepMs());
			}
		}
	}

	private Long[] listToLongArray(List<Long> i_list)
	{
		int j = 0;
		
		Long[] array = new Long[i_list.size()];
		
		for (Long page : i_list)
		{
			array[j] = page;
			++j;
		}
		
		return array;
	}
	
	public Process(int i_processId, MemoryManagementUnit i_mmu, ProcessCycles i_processCycles)
	{
		m_mmu = i_mmu;
		m_processId = i_processId;
		m_processCycles = i_processCycles;
	}
	
	public int getId()
	{
		return m_processId;
	}
	
	public void setId(int i_processId)
	{
		m_processId = i_processId;
	}
}
