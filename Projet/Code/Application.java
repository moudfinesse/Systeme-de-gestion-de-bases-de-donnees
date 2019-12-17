


import java.io.IOException;
import java.util.Scanner;

import sgbd.DBManager;

public class Application {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		DBManager.getInstance().init();
		Scanner sc = new Scanner(System.in);
		String commande;
		
	
		//gestion des commandes
		do
		{
			System.out.println("Veuillez saisir une commande ");
			commande = sc.nextLine();
			DBManager.getInstance().processCommand(commande);
		}
		while(!commande.toLowerCase().equals("exit"));
		
		
		/**
		DiskManager diskManager = DiskManager.getInstance();
		diskManager.createFile(3);
		**/
		
		/**
		PageId pageId = new PageId(1, 1);
		
		byte[] buffer = pageIdToBuffer(pageId);
	
		System.out.println(buffer);
		
		PageId pageIDcopy = bufferToPageId(buffer);
		
		System.out.println(pageId.compareTo(pageIDcopy));
		*/
		/**
		ArrayList<String> list = new ArrayList<>();
		list.add("Kissema");
		list.add("Eduardo");
		list.add("Rafael");
		list.add("Tandu");
		list.add("Lufundisu");
		
		System.out.println(list);
		
		list.remove(2);
		System.out.println(list);
		list.add(2, "Rafael");
		System.out.println(list);
		**/
		
		/**
		String s = "a";
		while(s.length()<10)
			s+="\0";
		s+="b";
		System.out.println("s : " + s );
		System.out.println("taille : " + s.length());
		
		String nom = "abcdef";
		String age = "10";
		String poids = "60.50";	
		
		byte[] byteArray = new byte[14];
	//	ByteBuffer buf = ByteBuffer.wrap(byteArray);
		
		int value = 1389745347;
		System.out.printf("%1$d => 0x%1$X", value);
		
		ByteBuffer buf = ByteBuffer.allocate(14);
		//buf.put(nom.getBytes());
		buf.put(nom.getBytes());
		buf.putInt(Integer.parseInt(age));
		buf.putFloat(Float.parseFloat(poids));
		buf.flip();
		byte[] arr = buf.array();
		//for (int i = 0 ; i < buf.limit() ; i++)
		    System.out.println(buf.limit());
		    write(buf);
		//read
				
		
		//write
		**/
		
		//System.out.println("AAAAAAAAAAAAAAAA");
	//	write();
		//read();
		
		
		
	//	DBManager.getInstance().processCommand("fill S1.csv");

	}
}
//crete Test git
//create Personne 3 string15 string15 int floate
//create Personne 4 string15 
//create Personne 4 string15 string15b int floate
//create Personne 4 string15 string15 int float
// create FileTest 8 string3 int string4 float string4 int int int
//SELECTALL FileTest