package database_records;

import code.interfaces.DatabaseInterface;

public class DBSimplePage {
	public long id;
	public String page; //index.php
	public long timestamp;
	
	public DBSimplePage(long id, String page, long timestamp) {
		this.id = id;
		this.page = page;
		this.timestamp = timestamp;
	}
	
	public DBPage loadFullPage(DatabaseInterface db) throws Exception {
		return new DBPage(db, id);
	}
}
