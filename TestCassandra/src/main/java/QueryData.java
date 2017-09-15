package main.java;

import java.util.ArrayList;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import main.java.PrimaryKey.SortOrder;

public class QueryData {
	
	static final String			CASS_HOST		= "127.0.0.1";
	static final int			CASS_PORT		= 9042;
	static final String			CASS_KEYSPACE	= "nimblex";
	static final String			CASS_TABLENAME	= "testData";
	
	CassandraConnector	cassConn	= null;

	public static void main (String args []) throws Exception {
		
		QueryData	test	= new QueryData ();
		test.init();

		// Query data from table
		//-----------------------
		test.queryAll (CASS_TABLENAME);
//		test.queryAndSort (CASS_TABLENAME);
		test.query (CASS_TABLENAME);
		
		test.cassConn.close();
	}
	
	public QueryData () {
	}
	
	public void init () {
		cassConn	= new CassandraConnector ();
		cassConn.connect (CASS_HOST, CASS_PORT, CASS_KEYSPACE);
	}
	
	public void queryAll (String tableName) {
		cassConn.queryAll (tableName);
	}
	
	public void queryByTime (String tableName) {
		StringBuffer		whereClause	= new StringBuffer ();
		whereClause.append(" WHERE tenant_id = " + 10000);
		String		startTime	= "'2017-09-14 02:36:10'";
		String		endTime		= "'2017-09-14 02:36:40'";
		whereClause.append(" AND timestamp > " + startTime);
		whereClause.append(" AND timestamp < " + endTime);
		
		cassConn.query (tableName, whereClause.toString(), "timestamp", SortOrder.DESC);
	}
	
	public void query (String tableName) {
		StringBuffer		whereClause	= new StringBuffer ();
		whereClause.append(" WHERE tenant_id = " + 10000);
		String		startTime	= "'2017-09-14 02:36:10'";
		String		endTime		= "'2017-09-14 02:36:40'";
		whereClause.append(" AND timestamp > " + startTime);
		whereClause.append(" AND timestamp < " + endTime);
		
		cassConn.query (tableName, whereClause.toString(), "timestamp", SortOrder.DESC);
	}
	
	public void queryAndSort (String tableName) {
		cassConn.queryAndSort (tableName, "tenant_id", 10000, "timestamp", SortOrder.DESC);
	}
}
