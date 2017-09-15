package main.java;

import java.util.ArrayList;

public class PrimaryKey {
	
	ArrayList<String>	keyNames;
	ArrayList<KeyVal>	sortOrderList;
	
	public enum SortOrder {
		DESC,
		ASC
	}
	
	public PrimaryKey () {
		keyNames		= new ArrayList<String> ();
		sortOrderList	= new ArrayList<KeyVal> ();
	}
	
	public void addKey (String keyName) {
		keyNames.add(keyName);
	}
	public void addSortOrder (String keyName, SortOrder sortOrder) {
		sortOrderList.add(new KeyVal(keyName, sortOrder.name()));
	}
	
	public String getPrimaryKeyStr () {
		StringBuffer		primaryKeyStr	= new StringBuffer ();
		primaryKeyStr.append("PRIMARY KEY (");
		int		count	= 0;
		for (String keyName : keyNames) {
			if (count > 0) {
				primaryKeyStr.append(",");
			}
			primaryKeyStr.append(keyName);
			count++;
		}
		primaryKeyStr.append(")");
		primaryKeyStr.append(")");
		return primaryKeyStr.toString();
	}
	
	public String getSortOrderStr () {
		StringBuffer		retStr	= new StringBuffer ();
		if (sortOrderList.size() > 0) {
			retStr.append(" WITH CLUSTERING ORDER BY (");
			int		count	= 0;
			for (KeyVal sortOrder : sortOrderList) {
				if (count > 0) {
					retStr.append(",");
				}
				retStr.append(sortOrder.toString());
				count++;
			}
			retStr.append(")");
		}
		retStr.append(";");
		return retStr.toString();
	}

}
