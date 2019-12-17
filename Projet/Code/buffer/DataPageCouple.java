package buffer;

public class DataPageCouple{

	private int pageIdx;
	
	private int freeSlots;
	
	public DataPageCouple(int pageIdx, int freeSlots)
	{
		this.pageIdx = pageIdx;
		this.freeSlots = freeSlots;
	}
	public int getPageIdx() {
		return pageIdx;
	}
	public void setPageIdx(int pageIdx) {
		this.pageIdx = pageIdx;
	}
	public int getFreeSlots() {
		return freeSlots;
	}
	public void setFreeSlots(int freeSlots) {
		this.freeSlots = freeSlots;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "DataPageCouple [pageIdx=" + pageIdx + ", freeSlots=" + freeSlots + "]";
	}
}