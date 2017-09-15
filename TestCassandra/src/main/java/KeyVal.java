package main.java;

public class KeyVal {
	String		name;
	String		value;
	
	public KeyVal (String name, String value) {
		this.name	= name;
		this.value	= value;
	}

	public String toString () {
		return (name + " " + value);
	}

}
