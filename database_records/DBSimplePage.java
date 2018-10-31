package database_records;

import code.interfaces.DatabaseInterface;

public class DBSimplePage {
	public long id;
	public String site;
	public String page;
	public long timestamp;
	
	public DBPage loadFullPage(DatabaseInterface db) {
		return new DBPage(id);
	}
}
