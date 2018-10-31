package database_records;

import code.interfaces.DatabaseInterface;

public class DBSimplePage {
	public long id;
	public String site; //google.com
	public String page; //index.php
	public String pageURL; //google.com/index.php (NO ARGS)
	public long timestamp;
	
	public DBPage loadFullPage(DatabaseInterface db) {
		return new DBPage(id);
	}
}
