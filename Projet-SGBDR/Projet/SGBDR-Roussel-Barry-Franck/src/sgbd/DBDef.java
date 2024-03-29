package sgbd;

import java.io.*;
import java.util.ArrayList;


/**
 *contient les informations de sch�ma pour l�ensemble de la base
 *de donn�es
 */
public class DBDef implements Serializable{

	private static DBDef INSTANCE = null;
	private ArrayList<RelDef> relations;
	private int nbRelations;
	
	private DBDef() {
		nbRelations = 0;
		relations = new ArrayList<RelDef>();
	}
	
	public static DBDef getInstance() {
		if ( INSTANCE == null) {
			INSTANCE = new DBDef();
		}
		return INSTANCE;
	}

	/**
	 * ajuste le contenu des variables membres
	 * de la DBDef pour le rajout d�une relation
	 * @param relation
	 */
	public void addRelation(RelDef relation) {
		nbRelations++;
		relations.add(relation);
	}

	public ArrayList<RelDef> getRelations() {
		return relations;
	}

	public void setRelations(ArrayList<RelDef> relations) {
		this.relations = relations;
	}

	public int getNbRelations() {
		return nbRelations;
	}

	public void setNbRelations(int nbRelations) {
		this.nbRelations = nbRelations;
	}
	
	public void reset()
	{
		 this.relations.clear();
	   	 this.nbRelations = 0;

	}
	
	
	public void init()throws FileNotFoundException,IOException,ClassNotFoundException{
		deserialiserDBDef() ;
	}
	/*elle enregistre dans le fichier catalog.def
	/**@throws FileNotFoundException
	*@throws IoException
	*/
	public void finish() throws IOException
	{
		serializeDBDef();		
	}
	public static void serializeDBDef() throws IOException
	{
		System.out.println("serialization DBDef");
		String fileName = "./DB/"+"Catalog.def";
		FileOutputStream fileOut = null;
		
		try
		{
			fileOut = new FileOutputStream(fileName);
		} catch (FileNotFoundException e)
		{
			System.out.println("Fichier Introuvable + " + fileName);
			e.printStackTrace();
		}
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(INSTANCE);
		out.close();
		fileOut.close();
		System.out.println("Fin serialization DBDef");
	}
	
	public void deserialiserDBDef() throws ClassNotFoundException, IOException 
	{
		System.out.println("Verifier si Catalog.def existe");
		String fileName =  "./DB/"+"Catalog.def";
		File file = new File(fileName);
		
		FileInputStream fis;
		if(file.exists())
		{
			System.out.println("fichier Catalog.def trouve");
			fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			INSTANCE = (DBDef) ois.readObject();
			ois.close();
		}
		else
		{
			System.out.println("fichier Catalog.def introuvable");
		
			return;
		}
		
		
		 System.out.println("Fin Deserialiser DBDef");
		// System.out.println("all relation : " + this.relations);		
	}
	
}
