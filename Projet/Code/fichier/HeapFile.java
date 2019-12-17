
package fichier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



import buffer.BufferManager;
import buffer.DataPageCouple;
import buffer.HeaderPageInfo;
import sgbd.PageId;
import sgbd.Record;
import sgbd.RelDef;
import sgbd.Rid;

public class HeapFile {

//	Cï¿½est via la RelDef que le Heap File
//	aura accï¿½s au fileIdx du fichier disque qui lui correspond !
	private RelDef relation;
	
	public HeapFile(RelDef relation)
	{
		this.relation = relation;
	}
	
	public RelDef getRelation() {
		return relation;
	}

	public void setRelation(RelDef relation) {
		this.relation = relation;
	}
	
	/**Cette mï¿½thode devra gï¿½rer la crï¿½ation du fichier disque 
	 * correspondant et le rajout dï¿½une Header Page ï¿½ vide ï¿½ ï¿½ ce fichier. 
	 * @throws IOException 
	 * 
	 */
	public void createNewOnDisk () throws IOException
	{
		int iFileIdx = this.relation.getFileIdx() ;//DBDef.getInstance().getNbRelations();
		DiskManager.getInstance().createFile(iFileIdx);//trouver le fileidx qui va bien
		PageId headerPage = DiskManager.getInstance().addPage(iFileIdx);
		
	//	//System.out.println("PageId : " + pageId);
		byte[] buffer   = BufferManager.getInstance().getPage(headerPage);


		HeaderPageInfo headerPageInfo = new HeaderPageInfo();
		headerPageInfo.writeToBuffer(buffer);
		BufferManager.getInstance().freePage(headerPage, true);
		
	}
	
	/**Cette mï¿½thode est censï¿½e remplir lï¿½argument oPageId avec
	 * lï¿½identifiant dï¿½une page de donnï¿½es sur laquelle il reste
	 * des cases disponibles. Si cela nï¿½est pas le cas, la mï¿½thode
	 * devra en plus gï¿½rer le rajout dï¿½une page (libre donc) et
	 * lï¿½actualisation des informations de la Header Page.
	 * @param oPageId
	 * @throws IOException 
	 */
	public PageId getFreePageId(PageId pageId) throws IOException
	{

		PageId headerPage = new PageId(this.relation.getFileIdx(),0);
		byte[] buffer     = BufferManager.getInstance().getPage(headerPage);
		HeaderPageInfo headerPageInfo = new HeaderPageInfo();
		headerPageInfo.readFromBuffer(buffer);
		
		BufferManager.getInstance().freePage(headerPage, false);
	
		for (DataPageCouple dataPageCouple : headerPageInfo.getListe())
		{

			if (dataPageCouple.getFreeSlots() > 0 /*&& dataPageCouple.getPageIdx() ==*/ )
			{
				//System.out.println("Il y a encore de l'espace sur la page : " + dataPageCouple );
				BufferManager.getInstance().freePage(headerPage, false);
				//System.out.println("FIN 1 getFreePageId");
				return new PageId(relation.getFileIdx(),dataPageCouple.getPageIdx());
			}
			else
			{
				//System.err.println();
				//System.err.println("Toutes les slots remplis");
				//System.err.println("ajout de page ...");
			}
		}
		
		PageId newPage = DiskManager.getInstance().addPage(relation.getFileIdx());
		//System.out.println("newPage : " + newPage );
		DataPageCouple dataCouple = new DataPageCouple(newPage.getPageIdx(), relation.getSlotCount());
		headerPageInfo.getListe().add(dataCouple);
		headerPageInfo.setDataPageCount(headerPageInfo.getDataPageCount()+1);
	//	//System.out.println("headerPageInfo.getDataPageCount: " + headerPageInfo.getDataPageCount() );
	//	//System.out.println("apres");
	//	//System.out.println("Taille headerPageInfo_Liste: " +headerPageInfo.getListe().size());
	//	//System.out.println("headerPageInfo_Liste: " +headerPageInfo.getListe());
		headerPageInfo.writeToBuffer(buffer);
		BufferManager.getInstance().freePage(headerPage, true);
		
		//  reÌ�cupeÌ�rer le buffer de la nouvelle page, eÌ�crire une seÌ�quence de 0 aÌ€ son deÌ�but (la bytemap) 
		//pour dire que toutes les cases sont vides, puis le libeÌ�rer (avec, aussi, dirty = 1)!
		byte[] newBuffer = BufferManager.getInstance().getPage(newPage);
		ByteBuffer  byteBuffer = ByteBuffer.wrap(newBuffer);
		for (int i = 0; i < relation.getSlotCount(); i++)
		{
			byteBuffer.put((byte)0);
		}
		newBuffer = byteBuffer.array();
		BufferManager.getInstance().freePage(newPage, true);
	//	//System.out.println("newBuffer : " + Arrays.toString(newBuffer));
		//System.out.println("FIN 2 getFreePageId");
		return newPage;
	}
	
	/**Cette methode devra actualiser les informations dans la Header Page
	 * suite a l occupation d une des cases disponible sur une page. 
	 * @param iPageId
	 * @throws IOException 
	 */
	public void updateHeaderWithTakenSlot(PageId iPageId) throws IOException {
		//System.out.println("DEBUT updateHeaderWithTakenSlot");
			PageId headerPage = new PageId(this.relation.getFileIdx(),0);
			byte[] buffer     = BufferManager.getInstance().getPage(headerPage);
			HeaderPageInfo headerPageInfo = new HeaderPageInfo();
			headerPageInfo.readFromBuffer(buffer);
			//System.out.println("Taille Liste dataCouple" + headerPageInfo.getListe() + " getHeaderPage()");
			
		//byte[] bufferHeaderPage = BufferManager.getInstance().getPage(headerPage);
		// a remplir a partir de bufferHeaderPage
		//	headerPage = bufferToHeaderPage(bufferHeaderPage);
		// fin remplissage
			for(DataPageCouple d: headerPageInfo.getListe()) {
				if(d.getPageIdx()==iPageId.getPageIdx())
					d.setFreeSlots(d.getFreeSlots()-1);
			}
		//	byte[] bufferHeaderPage = BufferManager.getInstance().getPage(headerPage);
			headerPageInfo.writeToBuffer(buffer);
			BufferManager.getInstance().freePage(headerPage, true);
			/*
			DataPageCouple d = headerPage.getListe().get(iPageId.getPageIdx());//????????? pourquoi?
			d.setFreeSlots(d.getFreeSlots()-1);
			headerPage.writeToBuffer(bufferHeaderPage); //bufferHeaderPage actualise
			BufferManager.getInstance().freePage(iPageId, true); //true : 1
			*/
			//System.out.println("FIN updateHeaderWithTakenSlot");
	}
	
	public Record readRecordFromBuffer(byte[] iBuffer, int iSlotIdx)
	{
		//System.out.println("readRecordFromBuffer DEBUT");
		//System.out.print("iBuffer LECTURE : iSlotIdx : " + iSlotIdx);
		
		Record record = new Record();
		ByteBuffer byteBuffer =ByteBuffer.wrap(iBuffer);
		int position = relation.getSlotCount() + relation.getRecordSize()*iSlotIdx;
		byteBuffer.position(position);
		//System.out.println("Position : " + byteBuffer.position());
		for (int i = 0 ; i < relation.getTypeColonnes().size() ; i++)
		{
			if (relation.getTypeColonnes().get(i).contains("string"))
			{
				String[] tabTyPe = relation.getTypeColonnes().get(i).split("string");
				final int TAILLE = Integer.parseInt(tabTyPe[1]);
				String text="";
				for(int j=0;j < TAILLE;j++)
				{
					text+=byteBuffer.getChar();
					//System.out.println("----pos char"+byteBuffer.position());
				}
			//byteBuffer.position(byteBuffer.position()+(TAILLE-text.length()*2));
				/*
				int cpt = 0 ;
				while(cpt < TAILLE)
				{
					text+= byteBuffer.getChar();
					//System.out.println(position + ":" + byteBuffer.position());
					cpt++;
				}
				*/
				record.getValues().add(text);
			}
			else
			{
				switch (relation.getTypeColonnes().get(i))
				{
					case "int" : record.getValues().add(""+byteBuffer.getInt());   break;
					case "float":record.getValues().add(""+byteBuffer.getFloat()); break;
				}
			}
		}
		//System.out.println(record);
		//System.out.println("readRecordFromBuffer FIN");

		return record;
	}

	public  void writeRecordInBuffer(Record iRecord, byte [] ioBuffer,int iSlotIdx)
	{
		//System.err.println("Debut writeRecordInBuffer : ");
		//System.out.println("ioBuffer" + Arrays.toString(ioBuffer));
		//System.out.println("slotCount : " +  relation.getSlotCount() + " iSlotIdx : " + iSlotIdx + " Taille iOBuffer : " + ioBuffer.length );
		int position = relation.getSlotCount() + relation.getRecordSize()*iSlotIdx;
		//System.out.println("Position : " + position);
		String type;
		ByteBuffer byteBuffer =ByteBuffer.wrap(ioBuffer);
		byteBuffer.position(position);
		for (int i = 0 ; i < relation.getTypeColonnes().size() ; i++)
		{
			if (relation.getTypeColonnes().get(i).contains("string"))
			{
				final int TAILLE = Integer.parseInt(relation.getTypeColonnes().get(i).split("string")[1]);
				String stringVal =iRecord.getValues().get(i);
				for (int j = 0; j < stringVal.length(); j++)
				{
					//System.out.println(byteBuffer.position() + ":" + stringVal.charAt(j));
					byteBuffer.putChar(stringVal.charAt(j));
				}
				//System.out.println("taille des char a ajouter "+(TAILLE-stringVal.length()*2));
				//System.out.println(TAILLE);
				//System.out.println(stringVal.length()*2);
				
				for(int j = 1;j<(TAILLE-stringVal.length())*2+1;j++)
					byteBuffer.put((byte)0);
			}
			
			else
			{
				switch (relation.getTypeColonnes().get(i))
				{
					case "int"  : byteBuffer.putInt( Integer.parseInt(iRecord.getValues().get(i))); break;//System.out.println(byteBuffer.position() + ":" + iRecord.getValues().get(i));
					case "float": byteBuffer.putFloat(Float.parseFloat(iRecord.getValues().get(i)));break;//System.out.println(byteBuffer.position() + ":" + iRecord.getValues().get(i)); break;
				}
			}
		}
		
		//System.out.print("Buffer Apres ecriture : ");
		//System.out.println(Arrays.toString(byteBuffer.array()));
		ioBuffer = byteBuffer.array();
	}
	
	/**Cette mÃ©thode prend en argument un Record 
	 * et un PageId et Ã©crit le record dans la page
	 * @param iRecord
	 * @param iPageId
	 * @return
	 * @throws IOException 
	 */
	public Rid writeRecordToDataPage(Record record,PageId pageId) throws IOException
	{
		//System.out.println("DEBUT insertRecordInPage");
		//System.out.println("PageID : " + iPageId);
		Rid rid = new Rid();
		
		
			byte [] buffer = BufferManager.getInstance().getPage(pageId);
			ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
			byteBuffer.position(0);
			int iSlotIdx = 0;
			for(int i=0;i < relation.getSlotCount();i++)
			{
				if(byteBuffer.get() == 0)
				{
					iSlotIdx=i;	
					break;
				}
			}
			buffer=byteBuffer.array();
			writeRecordInBuffer(record,buffer,iSlotIdx);
			ByteBuffer.wrap(buffer);
			byteBuffer.position(iSlotIdx);
			byteBuffer.put((byte)1);
			buffer = byteBuffer.array();
		//	//System.out.println(Arrays.toString(buffer));
			rid.setPageIdx(pageId);
			rid.setSlotIdx(iSlotIdx);
			BufferManager.getInstance().freePage(pageId, true);
	
			updateHeaderWithTakenSlot(pageId);
		return rid;
	}
	public Rid insertRecord (Record record) throws IOException
	{	//System.out.println("\nDEBUT insertRecord");
		PageId pageId = new PageId(this.relation.getFileIdx());
		PageId page = getFreePageId(pageId);
		//System.out.println("getFreePageId() : " + page);
		//System.out.println("FIN insertRecord");
		return writeRecordToDataPage(record,page);
	}
	
	
	
	public ArrayList<Record> getRecordsInDataPage(PageId iPageId) throws IOException {
		//System.out.println("DEBUT getRecordsOnPage " + getClass());
		ArrayList<Record> alRecord = new ArrayList<>();
		byte[] buffer = BufferManager.getInstance().getPage(iPageId);
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
		byteBuffer.position(0);
		//System.out.println("Debut select : " + iPageId + " : " + Arrays.toString(buffer));
		
		for (int i = 0; i < buffer.length; i++)
		{
			if(byteBuffer.get() == 1 && i < relation.getSlotCount())
			//if(buffer[i] == 1 && i < relation.getSlotCount())
			{
				//System.out.println("Record trouve sur index : " + i);
				buffer=byteBuffer.array();
				alRecord.add(readRecordFromBuffer(buffer, i));				
			}
		}
		
		//System.out.println("FIN getRecordsOnPage " + getClass());
		
		return alRecord;
	}
	
	public List<PageId> getDataPagesIds() throws IOException
	{
		//System.out.println("DEBUT getDataPagesIds " + getClass());
		ArrayList<PageId> alPageId = new ArrayList<>(); 

		PageId headerPage = new PageId(this.relation.getFileIdx(),0);
		byte[] buffer     = BufferManager.getInstance().getPage(headerPage);
		HeaderPageInfo headerPageInfo = new HeaderPageInfo();
		headerPageInfo.readFromBuffer(buffer);
		//System.out.println("Taille Liste dataCouple" + headerPageInfo.getListe() + " getHeaderPage()");
		for(DataPageCouple dataCouple : headerPageInfo.getListe())
			alPageId.add(new PageId(relation.getFileIdx(), dataCouple.getPageIdx()));
		BufferManager.getInstance().freePage(headerPage,false);
		//System.out.println("FIN getDataPagesIds " + getClass());
		return alPageId;
	}	
}