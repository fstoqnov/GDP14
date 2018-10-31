package database_records;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import code.checks.Check;
import code.checks.CheckList;
import code.checks.UnserialisedMarker;
import code.interfaces.DatabaseInterface;

public class DBPage {

	public List<UnserialisedMarker> markers;
	public List<Entry<String, String>> args;
	public String content;
	public String page; //index.php
	public String argURL; // index.php?arg1=val1
	public long timestamp;
	public long id;

	public DBPage(DatabaseInterface db, long id) throws Exception {
		this.id = id;
		String query =
				"SELECT page, timestamp, source FROM checkpage WHERE id = " + id;

		Statement stmt = db.getConn().createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while (rs.next()) {
			content = rs.getString("source");
			timestamp = rs.getLong("timestamp");
			page = rs.getString("page");
		}
		stmt.close();

		query =
				"SELECT name, value FROM variable WHERE checkpage = " + id;
		stmt = db.getConn().createStatement();
		rs = stmt.executeQuery(query);
		args = new ArrayList<Entry<String, String>>();
		while (rs.next()) {
			args.add(new AbstractMap.SimpleEntry<String, String>(rs.getString("name"), rs.getString("value")));
		}
		stmt.close();

		query =
				"SELECT severity, position, eleTagName, eleTagNum, attribute, check FROM marker WHERE checkpage = " + id;
		stmt = db.getConn().createStatement();
		rs = stmt.executeQuery(query);
		markers = new ArrayList<UnserialisedMarker>();
		List<Check> checks = new ArrayList<Check>();
		CheckList.addChecks(checks);
		while (rs.next()) {
			markers.add(new UnserialisedMarker(rs.getInt("severity"), rs.getString("eleTagName"), rs.getInt("eleTagNum"), rs.getString("attribute"), rs.getLong("position"), CheckList.getCheckFromCriterionNumber(checks, rs.getString("check"))));
		}
		stmt.close();

		argURL = page + (args.size() != 0 ? "?" : "");
		for (Entry<String, String> arg : args) {
			argURL += arg.getKey() + "=" + arg.getValue();
		}
	}
}
