package com.hit.exception;

public class HardDiskException extends java.io.IOException 
{
	public static enum ActionError
	{
		PAGE_FAULT,PAGE_REPLACEMENT;
	}

	private static final long serialVersionUID = 1L;
	
	public HardDiskException()
	{
		super();
	}
	
	public HardDiskException(java.lang.String i_msg)
	{
		super(i_msg);
	}
}
