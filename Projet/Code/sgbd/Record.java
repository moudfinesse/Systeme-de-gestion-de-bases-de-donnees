package sgbd;

import java.util.ArrayList;

public class Record {
	RelDef relation;

	private ArrayList<String> values;

	public Record(RelDef relation) {
		this.values = new ArrayList<String>();
		this.relation = relation;
	}

	public Record() {
		this.values = new ArrayList<String>();
	}

	public ArrayList<String> getValues() {
		return values;
	}

	public void setValues(ArrayList<String> values) {
		this.values = values;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Record [values=" + values + "]";
	}

}
