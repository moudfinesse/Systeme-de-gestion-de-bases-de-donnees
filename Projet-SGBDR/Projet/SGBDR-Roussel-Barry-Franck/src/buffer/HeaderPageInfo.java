package buffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class HeaderPageInfo {

	private int dataPageCount;
	private ArrayList <DataPageCouple> liste;
	
	public HeaderPageInfo(int i){ liste = new ArrayList<>();}
	public HeaderPageInfo(){ this(0);}	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "HeaderPageInfo [dataPageCount=" + dataPageCount + ", liste=" + liste + "]";
	}

	public int getDataPageCount() {
		return dataPageCount;
	}

	public void setDataPageCount(int dataPageCount)
	{
		//System.out.println("Entree : datpageCount : " + dataPageCount);
		this.dataPageCount = dataPageCount;
	}

	public ArrayList<DataPageCouple> getListe() {
		return liste;
	}

	public void setListe(ArrayList<DataPageCouple> liste) {
		this.liste = liste;
	}
	
	public void readFromBuffer(byte [] buffer)
	{ 
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
		
		this.dataPageCount = byteBuffer.getInt();
	
		for (int i = 0 ; i < dataPageCount; i++)
		{
			DataPageCouple  dataPageCouple = new DataPageCouple(byteBuffer.getInt(),byteBuffer.getInt());
			
			this.liste.add(dataPageCouple);
		}
		
		
		buffer = byteBuffer.array();
		
	}
	
	public void writeToBuffer(byte[] buffer)
	{ 
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
		byteBuffer.putInt(this.dataPageCount);
		
		for (int i = 0 ; i < dataPageCount; i++)
		{
			byteBuffer.putInt(liste.get(i).getPageIdx());
			byteBuffer.putInt(liste.get(i).getNbrSlots());
		}
		buffer = byteBuffer.array();
		
	}
}
