package buffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fichier.DiskManager;
import sgbd.PageId;
import sgbd.Constants;

import java.util.Set;

public class BufferManager
{
	private static BufferManager INSTANCE = null;
	private static List<Frame> bufferPool = new ArrayList<Frame>();
	//private Map<Integer,Integer> coupleIndexFrame = new HashMap<Integer,Frame>();
	
	public  static BufferManager getInstance() throws IOException{
		if (INSTANCE == null) {
			
			INSTANCE = new BufferManager();
			
			for (int i = 0; i < Constants.FRAME_COUNT; i++)
			{
				bufferPool.add(new Frame());
			}
		}
		return INSTANCE;
	}
	
	private   BufferManager () {}
	
	public Frame getFrame(int index)
	{
		return bufferPool.get(index);
	}
	
	public byte[] getPage(PageId pageId) throws IOException
	{

	    for (int i = 0; i < bufferPool.size(); i++)
		{
	    	if(bufferPool.get(i).getPageId() == null) // cas HeaderPage
			{			
				DiskManager.getInstance().readPage(pageId, bufferPool.get(i).getBuffer()); // chercher la page dans le disk
				bufferPool.get(i).setPageId(pageId);
				bufferPool.get(i).incrementPinCount();
	        	return bufferPool.get(i).getBuffer(); // s'elle existe on la lit
			}
	    	
	    	if(bufferPool.get(i).getPageId().getFileIdx() == pageId.getFileIdx() && bufferPool.get(i).getPageId().getPageIdx() == pageId.getPageIdx()) // cas HeaderPage
			{
							
				bufferPool.get(i).incrementPinCount();
		
	        	return bufferPool.get(i).getBuffer(); // s'elle existe on la lit
			}
	    	
		}
	   
	    int frameIndexToReplace = remplacementClock() ;
	  
	    
	    if(bufferPool.get(frameIndexToReplace).isDirty())
	    {
	   
			DiskManager.getInstance().writePage(bufferPool.get(frameIndexToReplace).getPageId(), bufferPool.get(frameIndexToReplace).getBuffer());
	
		}
	    
	    bufferPool.get(frameIndexToReplace).setPageId(pageId);
	   
		bufferPool.get(frameIndexToReplace).incrementPinCount();
		DiskManager.getInstance().readPage(bufferPool.get(frameIndexToReplace).getPageId(), bufferPool.get(frameIndexToReplace).getBuffer());
 		
		return bufferPool.get(frameIndexToReplace).getBuffer();
	}
	private int  remplacementClock()
	{	
		// prendre le pincount minimum de toutes le frames
		for(int j = 0; j < bufferPool.size(); j++)
		{ 
			if( bufferPool.get(j).getPinCount() == 0);
				return j;
		}
			
		return 0;
	}
	
	public void freePage(PageId iPageId, boolean iIsDirty)
	{
		for(Frame  frame : bufferPool)
		{
			if(iPageId.equals(frame.getPageId()))
			{
				frame.decrementPinCount();
			}
			
			if(iIsDirty)
			{
				frame.setDirty(iIsDirty);
			}
		}
	}
	/**
	 * Cette meÌ�thode sâ€™occupe de lâ€™eÌ�criture de toutes les pages dirty sur disque 
	 * et la remise aÌ€ 0 de tous les flags et buffers ! 
	 * Rajoutez un appel aÌ€ cette meÌ�thode dans la meÌ�thode Finish du DBManager 
	 * (car le contenu disque doit eÌ‚tre actualiseÌ� avant de fermer lâ€™application)!
	 * @throws IOException
	 */
	public void flushAll() throws IOException
	{	
		for(Frame frame : bufferPool)
		{
			if(frame.isDirty())
		    {
	        	DiskManager.getInstance().writePage(frame.getPageId(), frame.getBuffer());
	        }
	        
			frame.setDirty(false);
			frame.resetPinCount();
			frame.resetBuffer();
		}
	}
	
	/**
	 * @return the bufferPool
	 */
	public static List<Frame> getBufferPool()
	{
		return bufferPool;
	}

	/**
	 * @param bufferPool the bufferPool to set
	 */
	public static void setBufferPool(List<Frame> bufferPool)
	{
		BufferManager.bufferPool = bufferPool;
	}
	
	public void reset() throws IOException
	{
		
	}
}