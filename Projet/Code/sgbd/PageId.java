package sgbd;

import java.io.Serializable;

public class PageId implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int FileIdx;
	private int PageIdx;
	
	/**
	 * @param fileIdx
	 * @param pageIdx
	 */
	public PageId(int fileIdx)
	{
		super();
		FileIdx = fileIdx;
	}
	/**
	 * @param fileIdx
	 * @param pageIdx
	 */
	public PageId(int fileIdx, int pageIdx)
	{
		super();
		FileIdx = fileIdx;
		PageIdx = pageIdx;
	}
	public int getFileIdx() {
		return FileIdx;
	}
	public void setFileIdx(int fileIdx) {
		FileIdx = fileIdx;
	}
	public int getPageIdx()
	{
		return PageIdx;
	}
	
	public void setPageIdx(int pageIdx) {
		PageIdx = pageIdx;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "PageId [FileIdx=" + FileIdx + ", PageIdx=" + PageIdx + "]";
	}
	
	public boolean comparer(PageId autre)
	{
		return this.FileIdx == autre.FileIdx && this.PageIdx == autre.getPageIdx();
	}
}

