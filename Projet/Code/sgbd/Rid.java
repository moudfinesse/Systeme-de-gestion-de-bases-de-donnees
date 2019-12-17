
package sgbd;

public class Rid {
	private PageId pageIdx;
	private int slotIdx;
	public PageId getPageIdx() {
		return pageIdx;
	}
	public void setPageIdx(PageId pageIdx) {
		this.pageIdx = pageIdx;
	}
	public int getSlotIdx() {
		return slotIdx;
	}
	public void setSlotIdx(int slotIdx) {
		this.slotIdx = slotIdx;
	}
}