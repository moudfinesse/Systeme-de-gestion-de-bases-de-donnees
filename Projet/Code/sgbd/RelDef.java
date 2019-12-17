
package sgbd;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 */
public class RelDef implements Serializable{

	private String nomRelation;
	private int nbColonnes;
	private ArrayList<String> typeColonnes;
	
	
	private int recordSize;       // somme des tailles de tout les types = la taille d’un record
	
	private int slotCount;        // le nombre de cases (slots) disponibles sur une page pour la relation en question
	private int fileIdx;
	
	public int getFileIdx() {
		return fileIdx;
	}


	public void setFileId(int fileId) {
		this.fileIdx = fileId;
	}


	public RelDef() {
		nomRelation = null;
		nbColonnes = 0;
		typeColonnes = null;
		recordSize=0;
		slotCount=0;
	}
	
	
	public int getRecordSize() {
		return recordSize;
	}


	public void setRecordSize(int recordSize) {
		this.recordSize = recordSize;
	}


	public int getSlotCount() {
		return slotCount;
	}


	public void setSlotCount(int slotCount) {
		this.slotCount = slotCount;
	}


	public String getNomRelation() {
		return nomRelation;
	}


	public void setNomRelation(String nomRelation) {
		this.nomRelation = nomRelation;
	}


	public int getNbColonnes() {
		return nbColonnes;
	}


	public void setNbColonnes(int nbColonnes) {
		this.nbColonnes = nbColonnes;
	}


	public ArrayList<String> getTypeColonnes() {
		return typeColonnes;
	}


	public void setTypeColonnes(ArrayList<String> typeColonnes) {
		this.typeColonnes = typeColonnes;
	}

	@Override
	public String toString() {
		return "RelDef [nomRelation=" + nomRelation + ", nbColonnes=" + nbColonnes + ", typeColonnes=" + typeColonnes
				+ ", recordSize=" + recordSize+ "]";
	}
}