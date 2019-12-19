package fichier;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import buffer.BufferManager;
import buffer.DataPageCouple;
import buffer.HeaderPageInfo;
import sgbd.PageId;
import sgbd.Record;
import sgbd.RelDef;
import sgbd.Rid;

public class HeapFile {

//	à travers  la RelDef , le Heap File
//	aura accès au fileIdx du fichier disque qui lui correspond !
	private RelDef relation;

	public HeapFile(RelDef relation) {
		this.relation = relation;
	}

	public RelDef getRelation() {
		return relation;
	}

	public void setRelation(RelDef relation) {
		this.relation = relation;
	}

	/**
	 *
	 * cette methode créera le nouveau heapFile sur le disque
	 * 
	 * @throws IOException
	 * 
	 */
	public void createNewOnDisk() throws IOException {
		int iFileIdx = this.relation.getFileIdx();// DBDef.getInstance().getNbRelations();
		DiskManager.getInstance().createFile(iFileIdx);// trouver le fileidx qui va bien
		PageId headerPage = DiskManager.getInstance().addPage(iFileIdx);

		byte[] buffer = BufferManager.getInstance().getPage(headerPage);

		HeaderPageInfo headerPageInfo = new HeaderPageInfo();
		headerPageInfo.writeToBuffer(buffer);
		BufferManager.getInstance().freePage(headerPage, true);

	}

	/**
	 * cette methode remplira l'argument pageId avec l'id d'une page disposant de
	 * cases libres. et le cas contraire rajouter une page et actualiser les infos
	 * de la headerpage
	 * 
	 * @param oPageId
	 * @throws IOException
	 */
	public PageId getFreePageId(PageId pageId) throws IOException {

		PageId headerPage = new PageId(this.relation.getFileIdx(), 0);
		byte[] buffer = BufferManager.getInstance().getPage(headerPage);
		HeaderPageInfo headerPageInfo = new HeaderPageInfo();
		headerPageInfo.readFromBuffer(buffer);

		BufferManager.getInstance().freePage(headerPage, false);

		for (DataPageCouple dataPageCouple : headerPageInfo.getListe()) {

			if (dataPageCouple.getNbrSlots() > 0) {
				BufferManager.getInstance().freePage(headerPage, false);

				return new PageId(relation.getFileIdx(), dataPageCouple.getPageIdx());
			} else {

			}
		}

		PageId newPage = DiskManager.getInstance().addPage(relation.getFileIdx());
		DataPageCouple dataCouple = new DataPageCouple(newPage.getPageIdx(), relation.getNbrSlots());
		headerPageInfo.getListe().add(dataCouple);
		headerPageInfo.setDataPageCount(headerPageInfo.getDataPageCount() + 1);
		headerPageInfo.writeToBuffer(buffer);
		BufferManager.getInstance().freePage(headerPage, true);

		byte[] newBuffer = BufferManager.getInstance().getPage(newPage);
		ByteBuffer byteBuffer = ByteBuffer.wrap(newBuffer);
		for (int i = 0; i < relation.getNbrSlots(); i++) {
			byteBuffer.put((byte) 0);
		}
		newBuffer = byteBuffer.array();
		BufferManager.getInstance().freePage(newPage, true);

		return newPage;
	}

	/**
	 * Cette methode devra actualiser les informations dans la Header Page suite a l
	 * occupation d une des cases disponible sur une page.
	 * 
	 * @param pageId
	 * @throws IOException
	 */
	public void updateHeaderUsedSlot(PageId pageId) throws IOException {
		PageId headerPage = new PageId(this.relation.getFileIdx(), 0);
		byte[] buffer = BufferManager.getInstance().getPage(headerPage);
		HeaderPageInfo headerPageInfo = new HeaderPageInfo();
		headerPageInfo.readFromBuffer(buffer);

		for (DataPageCouple d : headerPageInfo.getListe()) {
			if (d.getPageIdx() == pageId.getPageIdx())
				d.setNbrSlots(d.getNbrSlots() - 1);
		}
		headerPageInfo.writeToBuffer(buffer);
		BufferManager.getInstance().freePage(headerPage, true);
	}

	public Record readFromBuffer(byte[] iBuffer, int slotIdx) {
		Record record = new Record();
		ByteBuffer byteBuffer = ByteBuffer.wrap(iBuffer);
		int position = relation.getNbrSlots() + relation.getRecordSize() * slotIdx;
		byteBuffer.position(position);
		for (int i = 0; i < relation.getTypeColonnes().size(); i++) {
			if (relation.getTypeColonnes().get(i).contains("string")) {
				String[] tabTyPe = relation.getTypeColonnes().get(i).split("string");
				final int TAILLE = Integer.parseInt(tabTyPe[1]);
				String text = "";
				for (int j = 0; j < TAILLE; j++) {
					text += byteBuffer.getChar();

				}

				record.getValues().add(text);
			} else {
				switch (relation.getTypeColonnes().get(i)) {
				case "int":
					record.getValues().add("" + byteBuffer.getInt());
					break;
				case "float":
					record.getValues().add("" + byteBuffer.getFloat());
					break;
				}
			}
		}
	return record;
	}

	public void writeToBuffer(Record record, byte[] ioBuffer, int slotIdx) {

		int position = relation.getNbrSlots() + relation.getRecordSize() * slotIdx;

		// String type;
		ByteBuffer byteBuffer = ByteBuffer.wrap(ioBuffer);
		byteBuffer.position(position);
		for (int i = 0; i < relation.getTypeColonnes().size(); i++) {
			if (relation.getTypeColonnes().get(i).contains("string")) {
				final int TAILLE = Integer.parseInt(relation.getTypeColonnes().get(i).split("string")[1]);
				String stringVal = record.getValues().get(i);
				for (int j = 0; j < stringVal.length(); j++) {

					byteBuffer.putChar(stringVal.charAt(j));
				}

				for (int j = 1; j < (TAILLE - stringVal.length()) * 2 + 1; j++)
					byteBuffer.put((byte) 0);
			}

			else {
				switch (relation.getTypeColonnes().get(i)) {
				case "int":
					byteBuffer.putInt(Integer.parseInt(record.getValues().get(i)));
					break;
				case "float":
					byteBuffer.putFloat(Float.parseFloat(record.getValues().get(i)));
					break;
				}
			}
		}

		ioBuffer = byteBuffer.array();
	}

	/**
	 * Cette méthode prend en argument un Record et un PageId et écrit le record
	 * dans la page
	 * 
	 * @param record
	 * @param pageId
	 * @return
	 * @throws IOException
	 */
	public Rid writeRecordToDataPage(Record record, PageId pageId) throws IOException {
		Rid rid = new Rid();
		byte[] buffer = BufferManager.getInstance().getPage(pageId);
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
		byteBuffer.position(0);
		int slotIdx = 0;
		for (int i = 0; i < relation.getNbrSlots(); i++) {
			if (byteBuffer.get() == 0) {
				slotIdx = i;
				break;
			}
		}
		buffer = byteBuffer.array();
		writeToBuffer(record, buffer, slotIdx);
		ByteBuffer.wrap(buffer);
		byteBuffer.position(slotIdx);
		byteBuffer.put((byte) 1);
		buffer = byteBuffer.array();
		rid.setPageIdx(pageId);
		rid.setSlotIdx(slotIdx);
		BufferManager.getInstance().freePage(pageId, true);

		updateHeaderUsedSlot(pageId);
		return rid;
	}

	public Rid insertRecord(Record record) throws IOException {
		PageId pageId = new PageId(this.relation.getFileIdx());
		PageId page = getFreePageId(pageId);
		return writeRecordToDataPage(record, page);
	}

	public ArrayList<Record> getRecordsInDataPage(PageId pageId) throws IOException {
		ArrayList<Record> allRecords = new ArrayList<>();
		byte[] buffer = BufferManager.getInstance().getPage(pageId);
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
		byteBuffer.position(0);

		for (int i = 0; i < buffer.length; i++) {
			if (byteBuffer.get() == 1 && i < relation.getNbrSlots()) {
				buffer = byteBuffer.array();
				allRecords.add(readFromBuffer(buffer, i));
			}
		}

		return allRecords;
	}

	public List<PageId> getDataPagesIds() throws IOException {
		ArrayList<PageId> allPageId = new ArrayList<>();
		PageId headerPage = new PageId(this.relation.getFileIdx(), 0);
		byte[] buffer = BufferManager.getInstance().getPage(headerPage);
		HeaderPageInfo headerPageInfo = new HeaderPageInfo();
		headerPageInfo.readFromBuffer(buffer);
		for (DataPageCouple dataCouple : headerPageInfo.getListe())
			allPageId.add(new PageId(relation.getFileIdx(), dataCouple.getPageIdx()));
		BufferManager.getInstance().freePage(headerPage, false);
		return allPageId;
	}
}