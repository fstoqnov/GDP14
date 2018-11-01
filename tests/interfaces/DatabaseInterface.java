package tests.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import code.Marker;
import code.UnserialisedMarker;
import code.checks.IdentifyInputPurpose;
import code.checks.LabelsOrInstructions;
import database_records.DBPage;
import database_records.DBSimplePage;
import database_records.DBSite;
import tests.RunTests;

public class DatabaseInterface {


	private code.interfaces.DatabaseInterface inter;

	private static final String testURL = "https://www.google.com/index.php?arg1=val1&arg2=val2&arg3=val3";
	private static final String argURL = "index.php?arg1=val1&arg2=val2&arg3=val3";
	private static final String testContent = "<html>Test</html>";
	private static final String testSite = "google.com";
	private static final String testPage = "index.php";
	private static final String connString = "Server=127.0.0.1;Database=gdb14;Uid=gdpfourteen;Pwd=pa55word;Port=3306";

	private static final boolean runLiveDBTests = false;

	public static boolean runTests() throws Exception {
		DatabaseInterface testInter = new DatabaseInterface();
		boolean correct = true;
		correct = testInter.testPartialiseFullURL() && correct;
		correct = testInter.testGetURLArgs() && correct;
		if (runLiveDBTests) {
			correct = testInter.testInsertMarkers() && correct;
		}
		return correct;
	}

	private DatabaseInterface() {
		inter = new code.interfaces.DatabaseInterface();
	}

	private boolean testPartialiseFullURL() {
		return
				RunTests.test("DatabaseInterface(PartialiseFullURL - test site)", testSite, code.interfaces.DatabaseInterface.partialiseFullURL(testURL).getKey()) &&
				RunTests.test("DatabaseInterface(PartialiseFullURL - test page)", testPage, code.interfaces.DatabaseInterface.partialiseFullURL(testURL).getValue());
	}

	private boolean testGetURLArgs() {
		List<Entry<String, String>> map = code.interfaces.DatabaseInterface.getURLArgs(testURL);
		String correctMap = "arg1=val1 arg2=val2 arg3=val3 ";
		String actualMap = "";
		for (Entry<String, String> cur : map) {
			actualMap += cur.getKey() + "=" + cur.getValue();
			actualMap += " ";
		}
		return RunTests.test("DatabaseInterface(GetURLArgs)", correctMap, actualMap);
	}

	private boolean testInsertMarkers() throws Exception {
		boolean correct = true;

		inter.connect(connString);

		String firstName = new LabelsOrInstructions().getName();
		String secondName = new IdentifyInputPurpose().getName();

		List<Marker> markers = new ArrayList<Marker>();
		markers.add(new Marker("Error Message", Marker.MARKER_AMBIGUOUS, new LabelsOrInstructions(), 10));
		markers.add(new Marker(Marker.MARKER_SUCCESS, new IdentifyInputPurpose(), 2));
		inter.insertIntoDatabase(markers, testURL, testContent, null);

		List<DBSite> sites = inter.getSites();
		correct = RunTests.test("DatabaseInterface(InsertMarkers - site inserted)", 1, sites.size()) && correct;
		correct = RunTests.test("DatabaseInterface(InsertMarkers - site inserted correct)", testSite, sites.get(0).site) && correct;

		List<DBSimplePage> pages = inter.getPagesForSite(sites.get(0));
		correct = RunTests.test("DatabaseInterface(InsertMarkers - page inserted)", 1, pages.size()) && correct;
		correct = RunTests.test("DatabaseInterface(InsertMarkers - page inserted correct)", testPage, pages.get(0).page) && correct;

		DBPage page = pages.get(0).loadFullPage(inter);
		correct = RunTests.test("DatabaseInterface(InsertMarkers - args inserted)", argURL, page.argURL) && correct;
		correct = RunTests.test("DatabaseInterface(InsertMarkers - content inserted)", testContent, page.content) && correct;
		correct = RunTests.test("DatabaseInterface(InsertMarkers - markers inserted)", 2, page.markers.size()) && correct;

		UnserialisedMarker marker = page.markers.get(0);
		correct = RunTests.test("DatabaseInterface(InsertMarkers - marker 1 correct desc)", "Error Message", marker.desc) && correct;
		correct = RunTests.test("DatabaseInterface(InsertMarkers - marker 1 correct type)", Marker.MARKER_AMBIGUOUS, marker.type) && correct;
		correct = RunTests.test("DatabaseInterface(InsertMarkers - marker 1 correct check)", firstName, marker.check.getName()) && correct;
		correct = RunTests.test("DatabaseInterface(InsertMarkers - marker 1 correct position)", 10L, marker.position) && correct;

		marker = page.markers.get(1);
		correct = RunTests.test("DatabaseInterface(InsertMarkers - marker 2 correct desc)", null, marker.desc) && correct;
		correct = RunTests.test("DatabaseInterface(InsertMarkers - marker 2 correct type)", Marker.MARKER_SUCCESS, marker.type) && correct;
		correct = RunTests.test("DatabaseInterface(InsertMarkers - marker 2 correct check)", secondName, marker.check.getName()) && correct;
		correct = RunTests.test("DatabaseInterface(InsertMarkers - marker 2 correct position)", 2L, marker.position) && correct;

		inter.disconnect();
		return correct; //TODO
	}
}