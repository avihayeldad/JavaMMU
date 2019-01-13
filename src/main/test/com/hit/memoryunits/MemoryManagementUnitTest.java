package com.hit.memoryunits;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hit.algorithm.LRUAlgoCacheImpl;
import com.hit.exception.HardDiskException;

class MemoryManagementUnitTest {
	
	private int m_capacity = 5;
	private MemoryManagementUnit m_MMU = new MemoryManagementUnit(m_capacity, new LRUAlgoCacheImpl<Long, Long>(m_capacity));
	private RAM m_RAM = m_MMU.getRam();
	
	private String byteArrayToString(byte[] i_byteArray)
	{
		return Arrays.toString(i_byteArray);
	}
	
	@SuppressWarnings("unused")
	private byte[] stringToByteArray(String i_string)
	{
		return i_string.getBytes();
	}

	@BeforeEach
	void setUp() throws Exception 
	{
		for (int i = 0; i < 8; ++i)
		{
			byte[] byteValue = {(byte)i,(byte)(i*i),(byte)(i+i)};
			Page<byte[]> pageToRAM = new Page<byte[]>((long)i, byteValue);
			HardDisk.getInstance().pageReplacement(pageToRAM, null);
		}
		
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() throws HardDiskException, IOException {
		String result = "";
		Long pageKeys[] = {(long)0, (long)1, (long)2, (long)3, (long)4, (long)5, (long)6, (long)7};
		m_MMU.getPages(pageKeys, 0);

		Iterator<Entry<Long, Page<byte[]>>> iterator = m_RAM.getPages().entrySet().iterator();
		while(iterator.hasNext())
		{
			result += (byteArrayToString(iterator.next().getValue().getContent()) + " ");
		}
		
		assertEquals("[3, 9, 6] [4, 16, 8] [5, 25, 10] [6, 36, 12] [7, 49, 14] ",result);
	}

}
