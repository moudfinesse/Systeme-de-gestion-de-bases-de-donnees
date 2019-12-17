package buffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

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
	{ //buffer correspondant � un headpage
		//System.out.println("debut readFromBuffer" + this.getClass());
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
		//System.out.println("readFromBuffer : buffer Taille : " + buffer.length + " " + getClass());
		this.dataPageCount = byteBuffer.getInt();
		//System.out.println("DataCount : " + dataPageCount);
		for (int i = 0 ; i < dataPageCount; i++)
		{
			DataPageCouple  dataPageCouple = new DataPageCouple(byteBuffer.getInt(),byteBuffer.getInt());
			//System.out.println(dataPageCouple);
			this.liste.add(dataPageCouple);
		}
		
		////System.out.println(Arrays.toString(byteBuffer));
		buffer = byteBuffer.array();
		//System.out.println("bufer for Alex : " + Arrays.toString(buffer));
		//System.out.println("fin readFromBuffer" + this.getClass());
	}
	
	public void writeToBuffer(byte[] buffer)
	{ 
		//System.out.println("debut writeToBuffer" + this.getClass());
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
		byteBuffer.putInt(this.dataPageCount);
		
		for (int i = 0 ; i < dataPageCount; i++)
		{
			byteBuffer.putInt(liste.get(i).getPageIdx());
			byteBuffer.putInt(liste.get(i).getFreeSlots());
		}
		buffer = byteBuffer.array();
		//System.out.println("bufer for Alex : " + Arrays.toString(buffer));
		//System.out.println("fin writeToBuffer" + this.getClass());
	}
}