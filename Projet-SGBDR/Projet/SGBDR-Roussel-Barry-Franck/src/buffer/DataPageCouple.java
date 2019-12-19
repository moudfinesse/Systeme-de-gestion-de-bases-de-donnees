package buffer;

public class DataPageCouple{

	private int pageIdx;
	
	private int nbrSlots;
	
	public DataPageCouple(int pageIdx, int freeSlots)
	{
		this.pageIdx = pageIdx;
		this.nbrSlots = freeSlots;
	}
	public int getPageIdx() {
		return pageIdx;
	}
	public void setPageIdx(int pageIdx) {
		this.pageIdx = pageIdx;
	}
	public int getNbrSlots() {
		return nbrSlots;
	}
	public void setNbrSlots(int nbrSlots) {
		this.nbrSlots = nbrSlots;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "DataPageCouple [pageIdx=" + pageIdx + ", freeSlots=" + nbrSlots + "]";
	}
}