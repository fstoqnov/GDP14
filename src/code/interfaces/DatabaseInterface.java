package code.interfaces;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import code.Marker;
import code.UnserialisedMarker;

import java.util.Properties;

import database_records.DBSimplePage;
import database_records.DBSite;

public class DatabaseInterface {

	private boolean connected;

	public DatabaseInterface() {
		connected = false;
	}

	public DatabaseInterface(String connectionString) throws Exception {
		connect(connectionString);
	}

	private Connection conn;

	public static final int DEFAULT_MYSQL_PORT = 3306;

	public DatabaseInterface(String user, String password, String server, String database) throws Exception {
		this(user, password, server, database, DatabaseInterface.DEFAULT_MYSQL_PORT);
	}

	public DatabaseInterface(String user, String password, String server, String database, int port) throws Exception {
		connect(user, password, server, database, port);
	}

	public void connect(String user, String password, String server, String database) throws Exception {
		connect(user, password, server, database, DatabaseInterface.DEFAULT_MYSQL_PORT);
	}

	public void connect(String user, String password, String server, String database, int port) throws Exception {
		connect("Server=" + server + ";Uid=" + user + ";Pwd=" + password + ";Database=" + database + ";Port=" + port + ";");
	}

	public void connect(String connectionString) throws Exception {
		if (connected) {
			throw new Exception("Already connected!");
		} else {
			int port = connectionString.split("Port=").length > 1 ? Integer.parseInt(connectionString.split("Port=")[1].split(";")[0]) : DatabaseInterface.DEFAULT_MYSQL_PORT;
			Properties connectionProps = new Properties();
			connectionProps.put("user", connectionString.split("Uid=")[1].split(";")[0].trim());
			connectionProps.put("password", connectionString.split("Pwd=")[1].split(";")[0].trim());

			conn = DriverManager.getConnection(
					"jdbc:mysql://" +
							connectionString.split("Server=")[1].split(";")[0] +
							":" +
							port +
							"/"
							+ connectionString.split("Database=")[1].split(";")[0],
							connectionProps);
			connected = true;
		}
	}

	public void disconnect() throws Exception {
		if (!connected) {
			throw new Exception("Not connected!");
		} else {
			connected = false;
			conn.close();
		}
	}

	public DBSite loadSite(String site) throws Exception {
		String query =
				"SELECT `id`, `url` FROM `site` WHERE `url` = ?";
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setString(1, site);
		ResultSet rs = stmt.executeQuery();
		DBSite siteDB = null;
		while (rs.next()) {
			siteDB = new DBSite(rs.getLong("id"), rs.getString("url"));
		}
		stmt.close();
		if (siteDB == null) {
			query =
					"INSERT INTO `site` (`url`) VALUES (?)";
			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, site);
			stmt.executeUpdate();
			ResultSet rs2 = stmt.getGeneratedKeys();
			rs2.next();
			int siteID = rs2.getInt(1);
			siteDB = new DBSite(siteID, site);
			stmt.close();
		}
		return siteDB;
	}

	public void insertIntoDatabase(List<Marker> markers, String fullURL, String pageContent, SeleniumInterface si) throws Exception {
		insertIntoDatabase(markers, fullURL, pageContent, System.currentTimeMillis(), si);
	}
	
	public void updateHiddenStatus(UnserialisedMarker marker) throws Exception {
		String first =
				"UPDATE marker SET `hidden` = ? WHERE `id` = ?";
		PreparedStatement stmt = conn.prepareStatement(first);
		stmt.setBoolean(1, marker.hidden);
		stmt.setLong(2, marker.id);
		stmt.execute();
		stmt.close();
	}

	public void insertIntoDatabase(List<Marker> markers, String fullURL, String pageContent, long timestamp, SeleniumInterface si) throws Exception {
		insertIntoDatabase(partialiseFullURL(fullURL).getKey(), markers, fullURL, pageContent, timestamp, si);
	}

	public void insertIntoDatabase(String site, List<Marker> markers, String fullURL, String pageContent, SeleniumInterface si) throws Exception {
		insertIntoDatabase(site, markers, fullURL, pageContent, System.currentTimeMillis(), si);
	}

	public void insertIntoDatabase(DBSite site, List<Marker> markers, String fullURL, String pageContent, SeleniumInterface si) throws Exception {
		insertIntoDatabase(site, markers, fullURL, pageContent, System.currentTimeMillis(), si);
	}

	public void insertIntoDatabase(String site, List<Marker> markers, String fullURL, String pageContent, long timestamp, SeleniumInterface si) throws Exception {
		insertIntoDatabase(loadSite(site), markers, fullURL, pageContent, timestamp, si);
	}

	//inserts the result of an accessibility check on the website into the database
	public DBSimplePage insertIntoDatabase(DBSite site, List<Marker> markers, String fullURL, String pageContent, long timestamp, SeleniumInterface si) throws Exception {
		return insertIntoDatabase(site, markers, fullURL, pageContent, timestamp, 0, si);
	}

	public DBSimplePage insertIntoDatabase(DBSite site, List<Marker> markers, String fullURL, String pageContent, long timestamp, long parent, SeleniumInterface si) throws Exception {
		String page;
		String first =
				"INSERT INTO checkpage (`site`, `page`, `timestamp`, `source`, `parent`) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement stmt = conn.prepareStatement(first, Statement.RETURN_GENERATED_KEYS);
		stmt.setLong(1, site.id);
		stmt.setString(2, page = partialiseFullURL(fullURL).getValue());
		stmt.setLong(3, timestamp);
		stmt.setString(4, pageContent);
		if (parent == 0) {
			stmt.setNull(5, java.sql.Types.INTEGER);
		} else {
			stmt.setLong(5, parent);
		}
		stmt.execute();
		ResultSet rs = stmt.getGeneratedKeys();
		rs.next();
		int checkpageID = rs.getInt(1);
		DBSimplePage checkpage = new DBSimplePage(checkpageID, parent, page, timestamp);
		stmt.close();
		String second =
				"INSERT INTO variable (`checkpage`, `name`, `value`) VALUES (?, ?, ?)";
		stmt = conn.prepareStatement(second);
		for (Entry<String, String> arg : getURLArgs(fullURL)) {
			stmt.setLong(1, checkpageID);
			stmt.setString(2, arg.getKey());
			stmt.setString(3, arg.getValue());
			stmt.execute();
			if (conn.getWarnings() != null) { throw new Exception(conn.getWarnings()); }
		}
		stmt.close();

		String third =
				"INSERT INTO marker (`checkpage`, `severity`, `position`, `eleTagName`, `eleTagNumber`, `attribute`, `check`, `desc`, `hidden`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, FALSE)";
		stmt = conn.prepareStatement(third);
		for (Marker marker : markers) {
			stmt.setLong(1, checkpageID);
			stmt.setInt(2, marker.getType());
			if (marker.getPosition() == -1) {
				stmt.setNull(3, java.sql.Types.BIGINT);
			} else {
				stmt.setLong(3, marker.getPosition());
			}
			if (marker.getElement() == null) {
				stmt.setNull(4, java.sql.Types.VARCHAR);
				stmt.setNull(5, java.sql.Types.INTEGER);
			} else {
				stmt.setString(4, marker.getElement().getTagName());
				stmt.setInt(5, si.getTagPosition(marker.getElement()));
			}
			if (marker.getAttribute() == null) {
				stmt.setNull(6, java.sql.Types.VARCHAR);
			} else {
				stmt.setString(6, marker.getAttribute());
			}
			stmt.setString(7, marker.getCheck().getName().split("Criterion ")[1].split(" ")[0]);
			if (marker.getDesc() == null) {
				stmt.setNull(8, java.sql.Types.VARCHAR);
			} else {
				stmt.setString(8, marker.getDesc());
			}
			stmt.execute();
			if (conn.getWarnings() != null) { throw new Exception(conn.getWarnings()); }
		}
		return checkpage;
	}

	public Connection getConn() { return conn; }

	//a list of all the sites
	public List<DBSite> getSites() throws Exception {
		String query =
				"SELECT id, url FROM site";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		List<DBSite> dbSites = new ArrayList<DBSite>();
		while (rs.next()) {
			dbSites.add(new DBSite(rs.getLong("id"), rs.getString("url")));
		}
		stmt.close();
		return dbSites;
	}

	//a list of all the simple pages for a site
	public List<DBSimplePage> getPagesForSite(DBSite site) throws Exception {
		return getPagesForSite(site.id);
	}

	//a list of all the simple pages for a site
	public List<DBSimplePage> getPagesForSite(String site) throws Exception {
		long site_id = 0;
		String query =
				"SELECT id FROM site WHERE url = ?";
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.setString(1, site);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			site_id = rs.getLong("id");
		}
		stmt.close();
		return getPagesForSite(site_id);
	}

	//a list of all the simple pages for a site
	public List<DBSimplePage> getPagesForSite(long site_id) throws Exception {
		List<DBSimplePage> pages = new ArrayList<DBSimplePage>();
		String query =
				"SELECT id, `page`, `timestamp`, `parent` FROM checkpage WHERE site = " + site_id + " ORDER BY timestamp";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			pages.add(new DBSimplePage(rs.getLong("id"), rs.getLong("parent"), rs.getString("page"), rs.getLong("timestamp")));
		}
		stmt.close();
		return pages;
	}

	public List<List<DBSimplePage>> groupPagesByTimestamp(List<DBSimplePage> pages) {
		List<List<DBSimplePage>> grouped = new ArrayList<List<DBSimplePage>>();
		grouped.add(new ArrayList<DBSimplePage>());
		long lastTimestamp = 0;
		if (pages.size() > 0) {
			grouped.get(0).add(pages.get(0));
			lastTimestamp = pages.get(0).timestamp;
		}
		for (int i = 1; i < pages.size(); i ++) {
			if (lastTimestamp != pages.get(i).timestamp) {
				grouped.add(new ArrayList<DBSimplePage>());
				lastTimestamp = pages.get(i).timestamp;
			}
			grouped.get(grouped.size() - 1).add(pages.get(i));
		}
		return grouped;
	}

	//Turns https://www.google.com/index.php?arg1=val1 into "google.com" and "index.php?arg1=val1"
	public static Entry<String, String> partialiseFullURL(String fullURL) {
		return new AbstractMap.SimpleEntry<String, String>(fullURL.split("://")[1].split("/")[0].replaceFirst("www\\.", ""), fullURL.split("://")[1].split("\\?")[0].substring(fullURL.split("://")[1].indexOf('/') + 1, fullURL.split("://")[1].split("\\?")[0].length()));
	}

	public static List<Entry<String, String>> getURLArgs(String fullURL) {
		List<Entry<String, String>> map = new ArrayList<Entry<String, String>>();
		if(fullURL.contains("?")) {
			String[] argString = fullURL.split("\\?")[1].split("&");

			for (String s : argString) {
				map.add(new AbstractMap.SimpleEntry<String, String>(s.split("=")[0], s.split("=")[1]));
			}
		}
		return map;
	}
}