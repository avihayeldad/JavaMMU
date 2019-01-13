package com.hit.memoryunits;

public class Page<T> extends java.lang.Object implements java.io.Serializable 
{
	private static final long serialVersionUID = 1L;
	private java.lang.Long m_ID;
	private T m_content;
	
	public Page(java.lang.Long i_ID, T i_content) 
	{
		m_content = i_content;
		m_ID = i_ID;
	}
	
	public java.lang.Long getPageId()
	{
		return m_ID;
	}
	
	public void setPageId(java.lang.Long i_ID)
	{
		m_ID = i_ID;
	}
	
	public T getContent()
	{
		return m_content;
	}

	public void setContent(T i_content)
	{
		m_content = i_content;
	}
	
	@Override
	public int hashCode()
	{
		return m_ID.hashCode();
	}

	@Override
	public boolean equals(java.lang.Object i_object)
	{
		return (hashCode() == i_object.hashCode());
	}
	
	@Override
	public java.lang.String toString()
	{
		return m_content.toString();
	}
}
