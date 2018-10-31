package code.interfaces;

import java.util.List;
import java.util.Map.Entry;

import code.checks.Marker;
import database_records.DBSimplePage;
import database_records.DBSite;

public class DatabaseInterface {

	public DatabaseInterface() {
		//TODO connection stuff etc.
	}

	public static void insertIntoDatabase(List<Marker> markers, String fullURL, String pageContent) {
		//TODO
	}

	//a list of all the sites
	public List<DBSite> getURLs() {
		//TODO
		return null;
	}

	//a list of all the simple pages for a site
	public List<DBSimplePage> getPagesForSite(DBSite site) {
		//TODO
		return null;
	}

	//a list of all the simple pages for a site
	public List<DBSimplePage> getPagesForSite(String site) {
		//TODO
		return null;
	}

	//Turns https://www.google.com/index.php?arg1=val1 into "google.com" and "index.php?arg1=val1"
	public static Entry<String, String> partialiseFullURL(String fullURL) {
		//TODO
		return null;
	}
}