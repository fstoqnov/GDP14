package database_records;

import java.util.List;

import code.checks.Marker;

public class DBPage {
	
	public List<Marker> markers;
	public List<String> args;
	public String content;
	public String site; //google.com
	public String page; //index.php
	public String fullPageURL; //google.com/index.php?arg1=val1
	public long timestamp;
	
	public DBPage(long id) {
		//TODO
	}
}
