package buffer;

import java.io.IOException;
import java.util.Arrays;

import sgbd.PageId;
import sgbd.Constants;

public class Frame
{
	private PageId pageId;
	private int pinCount;
	private boolean isDirty;
	private byte [] buffer;
		
	public Frame() throws IOException
	{
		this.buffer = new byte [Constants.PAGE_SIZE];
	}
	

	
	
	public boolean isDirty()
	{
		return isDirty;
	}
	public void setDirty(boolean isDirty)
	{
		this.isDirty = isDirty; 
	}
	public void incrementPinCount()
	{
		this.pinCount++;
	}
	
	public void decrementPinCount()
	{
		if(pinCount >= 1)
		this.pinCount--;
	}
	
	public byte[] getBuffer()
	{
		return this.buffer;
	}

	public void setBuffer(byte[] buffer)
	{
		this.buffer = buffer;
	}
	
	public int getPinCount()
	{
		return pinCount;
	}
	
	public void resetPinCount()
	{
		this.pinCount = 0;
	}
	
	public void modifyContent()
	{
		this.isDirty = true;
	}
	
	public PageId getPageId()
	{
		return this.pageId;
	}
	
	public void setPageId(PageId pageId)
	{
		this.pageId = pageId;
	}
	
	public void resetBuffer()
	{
		for (int i = 0; i < buffer.length; i++)
		{
			buffer[0] = 0;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String s = "";
		 s+="Frame [pageId=" + pageId + ", pinCount=" + pinCount + ", isDirty=" + isDirty + ", buffer=[";
		for (int i = 0; i < buffer.length; i++)
		{
			s+="("+i+ ":"+buffer[i] + ")";
		}
				s+= "]";
		 return s ;
	}	
}
