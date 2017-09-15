package main.java;

public class CassandraException extends Exception {
	private static final long serialVersionUID = 1L;

	public CassandraException(final String msg) {
		super(msg);
	}
	
	public CassandraException(final String msg, final Throwable cause) {
		super(msg, cause);
	}
}
