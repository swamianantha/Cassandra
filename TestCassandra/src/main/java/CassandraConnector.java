package main.java;

import com.datastax.driver.core.Cluster;  
import com.datastax.driver.core.Host;  
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.TableMetadata;

import main.java.PrimaryKey.SortOrder;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Iterator;  

/** 
 * Class used for connecting to Cassandra database. 
 */  
public class CassandraConnector  
{  
	/** Cassandra Cluster. */  
	private Cluster cluster;  

	/** Cassandra Session. */  
	private Session session;  
	
	/** 
	 * Connect to Cassandra Cluster specified by provided node IP 
	 * address and port number. 
	 * 
	 * @param node Cluster node IP address. 
	 * @param port Port of cluster host. 
	 */  
	public void connect(final String node, final int port)  
	{  
		this.cluster = Cluster.builder().addContactPoint(node).withPort(port).build();  
		final Metadata metadata = cluster.getMetadata();  
		out.printf("Connected to cluster: %s\n", metadata.getClusterName());  
		for (final Host host : metadata.getAllHosts())  
		{  
			out.printf("Datacenter: %s; Host: %s; Rack: %s\n",  
					host.getDatacenter(), host.getAddress(), host.getRack());  
		}  
		session = cluster.connect();  
	}  

	public void connect(final String node, final int port, final String keyspace)  
	{  
		this.cluster = Cluster.builder().addContactPoint(node).withPort(port).build();  
		final Metadata metadata = cluster.getMetadata();  
		out.printf("Connected to cluster: %s\n", metadata.getClusterName());  
		for (final Host host : metadata.getAllHosts())  
		{  
			out.printf("Datacenter: %s; Host: %s; Rack: %s\n",  
					host.getDatacenter(), host.getAddress(), host.getRack());  
		}  
		session = cluster.connect(keyspace);  
	}  

	public void printTableNames (String keyspace) {
		final Metadata metadata = cluster.getMetadata();  
		Iterator<TableMetadata> tm = metadata.getKeyspace(keyspace).getTables().iterator();

		while(tm.hasNext()){
			TableMetadata t = tm.next();
			System.out.println(t.getName());
		}
	}

	/** 
	 * Provide my Session. 
	 * 
	 * @return My session. 
	 */  
	public Session getSession()  
	{  
		return this.session;  
	}  

	public Cluster getCluster () {
		return this.cluster;
	}

	/** Close cluster. */  
	public void close()  
	{  
		cluster.close();  
	} 

	public ResultSet execute (final Statement statement, final String type) throws CassandraException {
		final long start = System.nanoTime();
		int responseCode = 200;
		int count = 0;
		try {
			final ResultSet result = this.session.execute(statement);
			count = result.getAvailableWithoutFetching();
			return result;
		}
		catch (final Exception e) {
			responseCode = 500;
			System.out.println ("Failed to execute " + type + " " + statement);
			throw new CassandraException ("Failed to execute " + type, e);
		}
		finally {
			System.out.println ("Response Code: " + responseCode +
					", Count: " + count +
					", Time Taken: " + MeasurementUtil.takenMs(start));
		}
	}
	
	public ResultSet createTable (String tableName, ArrayList<KeyVal> columns, PrimaryKey primaryKey) {
		StringBuffer	queryStr	= new StringBuffer();
		
		queryStr.append("CREATE TABLE " + tableName + "(");
		int		count	= 0;
		for (KeyVal column : columns) {
			queryStr.append(column.toString());
			queryStr.append(", ");
			count++;
		}
		if (primaryKey != null) {
			queryStr.append(primaryKey.getPrimaryKeyStr());
			queryStr.append(primaryKey.getSortOrderStr());
		}
		return execute (queryStr.toString());
	}

	public ResultSet insertData(String tableName, ArrayList<String> columns, ArrayList<String> colVals) {
		StringBuffer	queryStr	= new StringBuffer();
		
		queryStr.append("INSERT INTO " + tableName + "(");
		int		count	= 0;
		for (String column : columns) {
			if (count != 0) {
				queryStr.append(", ");
			}
			queryStr.append(column);
			count++;
		}

		queryStr.append(") VALUES (");
		count	= 0;
		for (String colVal : colVals) {
			if (count != 0) {
				queryStr.append(", ");
			}
			queryStr.append(colVal);
			count++;
		}
		queryStr.append(");");

		return execute (queryStr.toString());
	}

	public ResultSet queryAll(String tableName) {
		StringBuffer	queryStr	= new StringBuffer();
		
		queryStr.append("SELECT * FROM " + tableName + ";");
		return execute (queryStr.toString());
	}

	public ResultSet queryAndSort(String tableName, String whereField, int i, String sortField, SortOrder sortOrder) {
		StringBuffer	queryStr	= new StringBuffer();
		
		queryStr.append("SELECT * FROM " + tableName +
				" WHERE " + whereField + " = " + i +
				" ORDER BY " + sortField + " " + sortOrder + ";");
		return execute (queryStr.toString());
	}

	public ResultSet query (String tableName, String whereClause, String sortField, SortOrder sortOrder) {
		StringBuffer	queryStr	= new StringBuffer();
		
		queryStr.append("SELECT * FROM " + tableName +
				whereClause + 
				" ORDER BY " + sortField + " " + sortOrder + ";");
		return execute (queryStr.toString());
	}

	public ResultSet dropTable(String tableName) {
		StringBuffer	queryStr	= new StringBuffer();
		
		queryStr.append("DROP TABLE " + tableName + ";");
		return execute (queryStr.toString());
	}

	public ResultSet execute (String statement) {
		System.out.println(statement);
		ResultSet	resultSet	= this.session.execute(statement.toString());
		
		Iterator<Row>	rowIter	= resultSet.iterator();
		while (rowIter.hasNext()) {
			System.out.println(rowIter.next().toString());
		}
		
		return resultSet;
	}
}  