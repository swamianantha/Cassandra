package main.java;

import java.util.ArrayList;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import main.java.PrimaryKey.SortOrder;

public class GenTestData {
	
	static final String			CASS_HOST		= "127.0.0.1";
	static final int			CASS_PORT		= 9042;
	static final String			CASS_KEYSPACE	= "nimblex";
	static final String			CASS_TABLENAME	= "testData";
	
	static final String			GETVER_QUERY	= "select release_version from system.local";

	CassandraConnector	cassConn	= null;

	public static void main (String args []) throws Exception {
		
		GenTestData	test	= new GenTestData ();
		test.init();

		// Create Table
		//---------------
		test.createTable (CASS_TABLENAME);
		test.printTableNames();
		
		// Insert data into the table
		//----------------------------
		int		start	= 0;
		for (int i = start; i < 5; i++) {
			test.writeData(CASS_TABLENAME, i);
			Thread.sleep(10000);
		}
		
		// Query data from table
		//-----------------------
		test.queryAll(CASS_TABLENAME);
		
		// Delete table
		//--------------
		test.cassConn.close();
	}
	
	public GenTestData () {
	}
	
	public void init () {
		cassConn	= new CassandraConnector ();
		cassConn.connect (CASS_HOST, CASS_PORT, CASS_KEYSPACE);
	}
	
	public void createTable (String tableName) {
		ArrayList<KeyVal>	columns	= new ArrayList<KeyVal> ();
		columns.add(new KeyVal ("tenant_id", "int"));
		columns.add(new KeyVal ("timestamp", "timestamp"));
		columns.add(new KeyVal ("site_id", "int"));
		columns.add(new KeyVal ("device_id", "int"));
			
		columns.add(new KeyVal ("device_state_metric", "tinyint"));
		columns.add(new KeyVal ("op_state", "tinyint"));
		columns.add(new KeyVal ("device_cpu_metric", "tinyint"));
		columns.add(new KeyVal ("cpu_util", "float"));
		columns.add(new KeyVal ("device_memory_metric", "tinyint"));
		columns.add(new KeyVal ("mem_used", "bigint"));
		columns.add(new KeyVal ("mem_free", "bigint"));
		columns.add(new KeyVal ("mem_total", "bigint"));
		columns.add(new KeyVal ("mem_util", "float"));
		
		PrimaryKey		primaryKey	= new PrimaryKey ();
		primaryKey.addKey("tenant_id");
		primaryKey.addKey("timestamp");
		primaryKey.addKey("site_id");
		primaryKey.addKey("device_id");
		primaryKey.addSortOrder("timestamp", SortOrder.DESC);
		
		cassConn.createTable(tableName, columns, primaryKey);
	}
	
	public void printTableNames () {
		System.out.println ("Tables in keyspace " + this.CASS_KEYSPACE + " are:");
		cassConn.printTableNames(CASS_KEYSPACE);
	}
	
	public void writeData (String tableName, int count) {
		ArrayList<String>	columns	= new ArrayList<String> ();
		columns.add("tenant_id");
		columns.add("timestamp");
		columns.add("site_id");
		columns.add("device_id");
		columns.add("device_state_metric");
		columns.add("op_state");
		columns.add("device_cpu_metric");
		columns.add("cpu_util");
		columns.add("device_memory_metric");
		columns.add("mem_used");
		columns.add("mem_free");
		columns.add("mem_total");
		columns.add("mem_util");

		final long currTime = System.currentTimeMillis();
		ArrayList<String>	colVals	= new ArrayList<String> ();
		colVals.add(String.valueOf(10000));
		colVals.add(String.valueOf(currTime));
		colVals.add(String.valueOf(count + 1000));
		colVals.add(String.valueOf(count));
		colVals.add("2");
		colVals.add("1");
		colVals.add("2");
		colVals.add("95");
		colVals.add("2");
		colVals.add("1000");
		colVals.add("1000");
		colVals.add("2000");
		colVals.add("50");
		
		cassConn.insertData (tableName, columns, colVals);
	}
	
	public void queryAll (String tableName) {
		cassConn.queryAll (tableName);
	}
}
