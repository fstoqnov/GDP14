package database_records;

import code.interfaces.DatabaseInterface;

public class DBSimplePage {
	public long id;
	public String page; //index.php
	public long timestamp;
	public long parent;
	public int depth;
	public String event;
	
	public DBSimplePage(long id, long parent, String page, long timestamp, int depth, String event) {
		this.id = id;
		this.page = page;
		this.parent = parent;
		this.timestamp = timestamp;
		this.depth = depth;
		this.event = event;
	}
	
	public DBPage loadFullPage(DatabaseInterface db) throws Exception {
		return new DBPage(db, id);
	}
}
