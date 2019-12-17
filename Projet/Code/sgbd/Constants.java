package sgbd;

public class Constants
{
	public static final int FRAME_COUNT = 2;
	public static final int PAGE_SIZE = 4096;
	
	final static class Validate
	{
		public static boolean verifyInsert(String[] command)
		{
			if(verifyParams(command))
			{
				if(verifyDataManipulation(command[0]))
				{
					if(verifyNbType(command[2]))
					{
						if(tabTypeAndNbTypeIsOk(command))
						{
							if(verifyType(command))
							{
								return true;
							}
						}
					}			
				}
			}
			return false;
		}
		
		public static boolean verifySelect(String[] command)
		{
			return false;
		}
		
		public static boolean verifyDelete(String[] command)
		{
			return false;
		}
		
		public static boolean verifyUpdate(String[] command)
		{
			return false;
		}
		
		public static boolean verifyFill(String[] command)
		{
			if(command.length == 3)
			{
				if(command[2].contains(".csv"))
				{
					return true;
				}
				else
				{
					System.out.println("Fichier inconnu: il faut: nomFichier.csv");
					return false;
				}
			}
			else
			{
				System.out.println("nombre de paramettre invalide : il faut : Fill nomRelation nomFichier.csv");
				return false;
			}			
		}
		
		private static boolean verifyParams(String[] command)
		{
			
			if(command.length >= 4)
			{
				
				return true;
			}

			System.out.println("Le nombre de parametre invalide ! ");
			return false;
			
		}
		
		private static boolean verifyDataManipulation(String string)
		{
			String [] dataManipulation = {"CREATE","INSERT","DELETE","UPDATE", "FILL"};
			for (int i = 0; i < dataManipulation.length; i++)
			{
				if(string.equalsIgnoreCase(dataManipulation[i]))
					return true;
			}
			
			System.out.println(string + " n'est pas reconnu, il faut : CREATE ou INSERT ou DELETE ou UPDATE");
			return false;
		}
		
		private  static boolean verifyNbType(String string)
		{
			if(isNumber(string))
			{
				System.out.println("marche");
				return true;
			}
			else
			{
				System.out.println("erreur : "+ string+" is not number");
				return false;
			}
		}
		
		private static boolean isNumber(String string)
		{
			return string.matches("^\\d+$");
		}
		
		private static boolean tabTypeAndNbTypeIsOk(String [] command)
		{
			if( Integer.parseInt(command[2]) == command.length - 3)
			{
				return true;
			}
				
			else
			{
				System.out.println("pb : nbColonnes ne correspond pas aux nb de typesColonnes entrés");
				return false;
			}
		}
		
		private static boolean verifyType(String[] tabTypes)
		{
			String [] dataManipulation = {"int","float","string"};
			
			for (int i = 3; i < tabTypes.length; i++)
			{
				boolean isOk = false;
				for (int j = 0; j < dataManipulation.length; j++)
				{
					if(tabTypes[i].equalsIgnoreCase(dataManipulation[j]))
					{
						isOk = true;
						break;
					}
					
					else if (tabTypes[i].contains("string"))
					{
						String [] array = tabTypes[i].split("string");
						if(isNumber(array[1]))
						{
							isOk = true;
							break;
						}
						
						else
							break;
					}
				}
				
				if(!isOk)
				{
					System.out.println(tabTypes[i] + " n'est pas reconnu, il faut : stringT ou int ou float");
					return false;
				}
			}		

			return true;
		}
	}
}
