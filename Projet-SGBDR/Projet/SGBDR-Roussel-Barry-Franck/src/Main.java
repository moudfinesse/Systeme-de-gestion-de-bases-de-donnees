import java.io.IOException;
import java.util.Scanner;

import sgbd.DBManager;

public class Main {

	public static void main(String[] args)throws IOException, ClassNotFoundException {
	
			
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

	}

}
