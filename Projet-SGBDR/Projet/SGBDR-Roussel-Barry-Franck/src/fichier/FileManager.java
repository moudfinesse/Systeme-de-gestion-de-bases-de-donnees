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
	private ArrayList<HeapFile>allHeapFiles = new ArrayList<HeapFile>();
	
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
		allHeapFiles.add(new HeapFile(relation));
		allHeapFiles.get(allHeapFiles.size()-1).createNewOnDisk();
	}
	
	public void insertRecordInRelation(String relDefName, Record record) throws IOException
	{
		for (HeapFile heapFile : allHeapFiles)
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
		for(HeapFile heapFile: allHeapFiles)
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
	
	public List<Record> selectFromRelation(String relationName, int idCol, String valeur ) throws IOException
	{
		List< Record> alRecord = selectAllFromRelation(relationName);
		List< Record> alNewRecords = new ArrayList<>();
		for(Record record: alRecord)
		{
			if(record.getValues().get(idCol-1).equals(valeur))
			{
				alNewRecords.add(record);
			}
		}
		
		return alNewRecords;
	}
	
	public void join(String[] tabCommande) throws IOException {
		int cpt=0;
		//
		List< PageId> allPageA = new ArrayList<PageId>();
		List< PageId> allPageB = new ArrayList<PageId>();
		HeapFile heapfileA = null;
		HeapFile heapfileB = null;
		// la relation la plus externe
		int indexA = Integer.parseInt(tabCommande[3])-1;
		int indexB = Integer.parseInt(tabCommande[4])-1;
		List< Record> allRecordA = new ArrayList<Record>();
		
		for(HeapFile heapFile: allHeapFiles)
		{
			
			if(heapFile.getRelation().getNomRelation().equalsIgnoreCase(tabCommande[1])) {
				allPageA = (heapFile.getDataPagesIds());
				heapfileA = heapFile;
			}
			if(heapFile.getRelation().getNomRelation().equalsIgnoreCase(tabCommande[2])) {
				allPageB = (heapFile.getDataPagesIds());
				heapfileB = heapFile;
			}
		}

		for(PageId pageA : allPageA) {
			for(Record recordA: heapfileA.getRecordsInDataPage(pageA)) {
				allRecordA.add(recordA);
				
			}
		}
		
		for(PageId pageB : allPageB) {
			for(Record recordB: heapfileB.getRecordsInDataPage(pageB)) {
				for (int i = 0 ; i < allRecordA.size() ; i++) {
					
					if(recordB.getValues().get(indexB).equals(allRecordA.get(i).getValues().get(indexA))) {
						cpt++;
					}
				}
			}
		}
		// juste l'affichage du nombre de tuples resultant du join
		System.out.println("resultat trouve : "+cpt+" tuples");
	}
	
	
	public void reset () {
	   	 this.allHeapFiles.clear();
	    }

}
