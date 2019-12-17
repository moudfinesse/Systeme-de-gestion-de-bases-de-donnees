package fichier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



import sgbd.DBDef;
import sgbd.PageId;
import sgbd.Record;
import sgbd.RelDef;

public class FileManager
{
	private static FileManager INSTANCE = null;
	private ArrayList<HeapFile>alHeapFiles = new ArrayList<HeapFile>();
	
	private   FileManager() {}
	public  static FileManager getInstance() {
		if (INSTANCE == null) {
			
			INSTANCE = new FileManager();
		}
		return INSTANCE;
	}
	
	public void init() throws IOException
	{		
		for(RelDef relDef : DBDef.getInstance().getRelations())
		{
			createNewHeapFile(relDef);
		}
	}
	
	public void createNewHeapFile(RelDef relation) throws IOException
	{
		alHeapFiles.add(new HeapFile(relation));
		alHeapFiles.get(alHeapFiles.size()-1).createNewOnDisk();
	}
	
	public void insertRecordInRelation(String relDefName, Record record) throws IOException
	{
		for (HeapFile heapFile : alHeapFiles)
		{
			if(heapFile.getRelation().getNomRelation().equals(relDefName))
			{
				heapFile.insertRecord(record);
			}
		}
	}
	
	
	public List<Record> selectAllFromRelation(String relationName) throws IOException
	{
		List< Record> alRecord = new ArrayList<>();
		for(HeapFile heapFile: alHeapFiles)
		{
			if(heapFile.getRelation().getNomRelation().equalsIgnoreCase(relationName))
			{
				for(PageId iPageId : heapFile.getDataPagesIds())
				{
					alRecord.addAll(heapFile.getRecordsInDataPage(iPageId));
				}
			}
		}
		return alRecord;
	}
	
	public List<Record> selectFromRelation(String relationName, int iIdxCol, String iValeur ) throws IOException
	{
		List< Record> alRecord = selectAllFromRelation(relationName);
		List< Record> alNewRecordWithFilter = new ArrayList<>();
		for(Record record: alRecord)
		{
			if(record.getValues().get(iIdxCol-1).equals(iValeur))
			{
				alNewRecordWithFilter.add(record);
			}
		}
		//System.out.println("listeAAA : " + alNewRecordWithFilter);
		return alNewRecordWithFilter;
	}
	
	public void join(String[] tabCommande) throws IOException {
		int cpt=0;
		List< PageId> alPageA = new ArrayList<PageId>();
		List< PageId> alPageB = new ArrayList<PageId>();
		HeapFile heapfileA = null;
		HeapFile heapfileB = null;
		int indexA = Integer.parseInt(tabCommande[3])-1;
		int indexB = Integer.parseInt(tabCommande[4])-1;
		List< Record> alRecordA = new ArrayList<Record>();
		
		for(HeapFile heapFile: alHeapFiles)
		{
			
			if(heapFile.getRelation().getNomRelation().equalsIgnoreCase(tabCommande[1])) {
				alPageA = (heapFile.getDataPagesIds());
				heapfileA = heapFile;
			}
			if(heapFile.getRelation().getNomRelation().equalsIgnoreCase(tabCommande[2])) {
				alPageB = (heapFile.getDataPagesIds());
				heapfileB = heapFile;
			}
		}

		for(PageId pageA : alPageA) {
			for(Record recordA: heapfileA.getRecordsInDataPage(pageA)) {
				alRecordA.add(recordA);
				
			}
		}
		
		for(PageId pageB : alPageB) {
			for(Record recordB: heapfileB.getRecordsInDataPage(pageB)) {
				for (int i = 0 ; i < alRecordA.size() ; i++) {
					
					if(recordB.getValues().get(indexB).equals(alRecordA.get(i).getValues().get(indexA))) {
						cpt++;
					}
				}
			}
		}
		System.out.println("resultat trouve : "+cpt+" tuples");
	}
	
	
	public void reset () {
	   	 this.alHeapFiles.clear();
	    }

}
