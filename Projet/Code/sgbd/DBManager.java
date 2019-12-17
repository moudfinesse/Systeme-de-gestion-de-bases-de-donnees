package sgbd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import buffer.BufferManager;
import fichier.FileManager;

public final class DBManager {
	
	private static DBManager INSTANCE = null;
	
	private DBManager()
	{
	}
	
	public static DBManager getInstance()
	{
		if (INSTANCE == null) {
			INSTANCE = new DBManager();
		}
		return INSTANCE;
	}

	
	public void processCommand(String commande) throws IOException {
		String[] tabCommande = commande.split(" ");
		switch (tabCommande[0].toUpperCase()) {
		case "CREATE": create(tabCommande); break;
		case "DELETE": delete(tabCommande); break;
		case "SELECT": select(tabCommande); break;
		case "INSERT": insert(tabCommande[1],2,tabCommande); break;
		case "INSERTALL"  : insertAll(tabCommande); break;
		case "CLEAN" : clean(); break;
		case "CREATEINDEX": createIndex(tabCommande); break;
		case "SELECTALL": selectAll(tabCommande); break;
		case "EXIT": finish(); break;
		case "JOIN": join(tabCommande); break;
		

		default:
			break;
		}
	}
	
	private void join(String[] tabCommande) throws IOException {

		FileManager.getInstance().join(tabCommande);
		
	}

	private void create(String[] tabCommande) throws IOException
	{
		if(Constants.Validate.verifyInsert(tabCommande))
		{
			RelDef relation = new RelDef();
			relation.setNomRelation(tabCommande[1]);
			relation.setNbColonnes(Integer.parseInt(tabCommande[2])); 
			ArrayList <String> typesColonnes = new ArrayList<String>();
			
			for (int i = 3 ; i < tabCommande.length ; i++)
			{
				typesColonnes.add(tabCommande[i]);
			}
			
			relation.setTypeColonnes(typesColonnes);
			createRelation(tabCommande[1], relation.getNbColonnes(), relation.getTypeColonnes());
			
			System.out.println(DBDef.getInstance().getRelations().toString());
		}
	}
	
	private void delete(String[] tabCommande)
	{
		
	}
	
	private void select(String[] tabCommande) throws IOException {
		//select nomRelation indice_colonne v
		List<Record> records = FileManager.getInstance()
				.selectFromRelation(tabCommande[1], Integer.parseInt(tabCommande[2]), tabCommande[3]);
		toString(tabCommande[1], records);
//		System.out.println(toString(tabCommande[1], tabCommande[2], tabCommande[3]));
	}
	
	/**Cette commande doit afficher tous les records de la relation (1 par ligne), suivis par la phrase
 	 *(sur une nouvelle ligne) �Total records: x�, ou x est le nombre de records affiches.
	 * @param tabCommande
	 * @throws IOException 
	 */
	private void selectAll(String[] tabCommande) throws IOException {
		//selectall nomRelation
		List<Record> records = FileManager.getInstance().selectAllFromRelation(tabCommande[1]);
		toString(tabCommande[1], records);
//		System.out.println(toString(tabCommande[1]));	
	}
	/** creer un B+Tree d ordre val_ordre, residant uniquement en memoire
 	 *(pas besoin donc de pages, buffers et autres).
	 *Ce B+Tree pointera par leurs rids les records de la relation nomRelation
	 *et aura comme cle de recherche la colonne indice_colonne de la relation nomRelation. 
	 * @param tabCommande
	 * @throws IOException 
	 */
	private void createIndex(String[] tabCommande) throws IOException{
		
		
	}
	
	private void insert(String nomRelation,int indexDebut, String[] tabCommande) throws IOException
	{ 
		System.out.println("DEBUT insertWithCommand");
		System.out.println("Taille  relation : " + DBDef.getInstance().getRelations());
		for (RelDef relation :DBDef.getInstance().getRelations())
		{
			System.out.println("Relation : " + relation.getNomRelation());
			if(relation.getNomRelation().equals(nomRelation))
			{
				Record record = new Record();
				ArrayList<String> list = new ArrayList<>();
				
				for (int j = indexDebut; j < tabCommande.length; j++)
				{
					list.add(tabCommande[j]);
				}
				record.setValues(list);
				FileManager.getInstance().insertRecordInRelation(nomRelation, record);
				break;
			}
		}
		System.out.println("FIN insert");
	}
		
	
	/**Methode qui permet d'ajouter a une chaine de caracteres un certain nombre de fois
	 *une autre chaine de caracteres
	 * @param texte est la chaine de caracteres de depart
	 * @param nbFois est le nombre de fois pour lequel on souhaite ajouter stringAjout a texte
	 * @param stringAjout est la chaine de caracteres a ajouter nbFois
	 * @return la chaine de caracteres obtenue 
	 */
	private String ajoutStrings (String texte, int nbFois, String stringAjout) {
		StringBuilder sb = new StringBuilder(texte);
		for (int i = 0 ; i < nbFois ; i++) {
			sb.append(stringAjout);
		}
		return sb.toString();
	}
	
	/**Methode permettant de centrer une chaine de caracteres avec une taille precise a atteindre
	 * @param texte est le texte a centrer
	 * @param tailleCase est la taille que doit avoir le texte centre
	 * @return le texte centre
	 */
	private String centrerString (String texte, int tailleCase) {
		if (texte.length() == tailleCase) return texte;
		String texteCentre = texte;
		while (texteCentre.length() != tailleCase) {
			texteCentre = " "+texteCentre;
			if (texteCentre.length() != tailleCase)
			texteCentre = texteCentre+" ";
		}
		
		return texteCentre;
	}
	
	
//	/**Affichage d une table dans un tableau
//	 * @param nomRelation est le nom de la table de relation a afficher
//	 * @return chaine de caracteres a afficher
//	 */
//	private String toString (String nomRelation) {
//		return toString(nomRelation, null, null);
//	} 
//	
	/**Affichage d'une table dans un tableau avec indiceColonne = valeur
	 * @param nomRelation est le nom de la table de relation a afficher
	 * @param indiceColonne
	 * @param valeur
	 * @return chaine de caracteres a afficher
	 */
	private void toString (String nomRelation, List<Record> records) {
		StringBuilder sb = new StringBuilder();
	
		List<RelDef> relations = DBDef.getInstance().getRelations();
		int nbColonnes = 0;
		ArrayList<String> typeColonnes = new ArrayList<>();
		int tailleColonne = 15;
		
		//determination de nbColonnes
		for (RelDef relation : relations) {
			if  (relation.getNomRelation().equals(nomRelation)) {
				System.out.println("nom trouve");
				nbColonnes = relation.getNbColonnes();
				typeColonnes = relation.getTypeColonnes();
			}
		}
		
		//determination de tailleColonne
		for (Record record : records) {
			ArrayList<String> values = record.getValues();
			for (int i = 0 ; i < nbColonnes ; i++) {
				if (values.get(i).length() > tailleColonne) {
					tailleColonne = values.get(i).length();
				}
			}
		}
		
		//titre
		sb.append("\n"
				+centrerString(nomRelation, (tailleColonne+1)*nbColonnes+1)
				+ "\n");
		
		//ligne
		String ligne = ";";
		for (int i = 0 ; i < nbColonnes ; i++) {
			ligne = ajoutStrings(ligne, tailleColonne, "-")+";";
		}
		sb.append("\n"
				+ligne);
		
		//premiere ligne du tableau
		String colonnes = "";
		String colonne = "";		
		for (int i = 0 ; i < nbColonnes ; i++) {
			colonne = "Col"+(i+1)+"("+ typeColonnes.get(i) + ")";
			colonne = centrerString(colonne, tailleColonne);
			colonnes += "|"+ colonne;
		}
		
		sb.append("\n"
			 	+ colonnes + "|"
				+ "\n"
			 	+ ligne);
		
		//records
		for (Record record : records) {
			ArrayList<String> values = record.getValues();
			String affichageValeurs = "";
			for (int i = 0 ; i < nbColonnes ; i++) {
				String valueToString = centrerString(values.get(i), tailleColonne);
				affichageValeurs += ";" + valueToString;
			}
			sb.append("\n"
					+ affichageValeurs+";"
					+ "\n"
					+ ligne);
		}
	
		sb.append("\n"
				+ "Total records : " + records.size()
				+ "\n");

		System.out.println(sb.toString());
	}
	

	public void clean() throws IOException
	{
		System.out.println("cleanning");
	//	String fileName =  "..DB";
		Path directory = Files.createDirectories(Paths.get("DB"));
		for(File file: directory.toFile().listFiles()) 
		    if (!file.isDirectory()) 
		        file.delete();
		System.out.println("all files deleted");
		System.out.println("resetting...");
	   	BufferManager.getInstance().reset();
	   	DBDef.getInstance().reset();
	   	FileManager.getInstance().reset();

	}
	public void insertAll(String[] tabCommande)
	{  // commande : insertall  nomRelation nomfichier.csv
		
		String fileName = "./DB/scenario/"+tabCommande[2];			
		FileReader fr=null;
		String text;
		System.out.println("Chargement du fichier  "+fileName);
		try
		{
			// ouverture du fichier en mode lecture
			fr = new FileReader(fileName);
			BufferedReader bufferReader = new BufferedReader(fr);
			// écriture de la ligne de texte
			int ccc = 0;
			while ((text=bufferReader.readLine())!=null){
				traiterLigne(tabCommande[1], text);
				System.out.println(ccc++);
			}
				
			
			// fermeture du fichier
			fr.close();
		} catch (IOException e)
		{
			System.out.println("Probleme lors de la lecture dans le fichier "+fileName);
		}
	}
	
	/**
	 * cette fonction permet de traiter chaque ligne sachant que les enregistrements 
	 * sont delimités par des virgules
	 * @param text la ligne de texte à  traiter.
	 * @throws IOException 
	 */
	private void traiterLigne(String nomRelation, String ligne) throws IOException
	{
		//List<String> alValues = new ArrayList<>();
		System.out.println("ligne  :  "+ligne);
		String [] tab = ligne.split(",");
		
		insert(nomRelation, 0,tab);
		
	}
	
	
	/**
	 * crée une RelDef suivant les arguments et qui la rajoute au DBDef.
	 * @param nomRelation
	 * @param nbColonnes
	 * @param typesDesColonnes
	 * @throws IOException 
	 */
	public void createRelation(String nomRelation, int nbColonnes, ArrayList<String> typesDesColonnes) throws IOException {
		RelDef relation = new RelDef();
		relation.setNomRelation(nomRelation);
		relation.setNbColonnes(nbColonnes);
		relation.setTypeColonnes(typesDesColonnes);
		int recordSize = calculerRecordSize(typesDesColonnes);
		int slotCount = (Constants.PAGE_SIZE-(Constants.PAGE_SIZE / recordSize))/recordSize; //pas très sur
		relation.setRecordSize(recordSize);
		relation.setSlotCount(slotCount);
		relation.setFileId(DBDef.getInstance().getNbRelations());
		DBDef.getInstance().addRelation(relation);
		FileManager.getInstance().createNewHeapFile(relation);
	}
	
	private int calculerRecordSize(ArrayList<String> typesDesColonnes)
	{
		int recordSize=0;
		for(String type : typesDesColonnes) {
				if (type.length() > 6 && type.substring(0,6).equals("string"))
				recordSize+= Integer.parseInt(type.substring(6,type.length()))*2;
			else
				switch (type) {
				case "int" : case "float":
					recordSize+=4;
					break;
				}
		}
		return recordSize;	
	}
	
	
	public void reset()
	{
		DBDef.getInstance().reset();
	}
	/** initialuse le DBDef et le FileMananger
	 * 
	 * */
	public void init() throws FileNotFoundException, ClassNotFoundException, IOException
	{
		DBDef.getInstance().init();
		FileManager.getInstance().init();
	}

	public void finish() throws FileNotFoundException, IOException
	{
		System.out.println("Patience exiting");
		DBDef.getInstance().finish();
		BufferManager.getInstance().flushAll();
	}
	
	
}
