package code;

import code.interfaces.DatabaseInterface;
import code.interfaces.SeleniumInterface;
import com.google.common.collect.Lists;
import database_records.DBPage;
import database_records.DBSimplePage;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ConformanceReport {

	private String reportFull;
	private int anchorPage = 0;
	private int anchorError = -1;

	private final String style = "" +
			"<style>" +
			"body {" +
			"   font-family: arial;" +
			"   word-wrap: break-word;" +
			"}" +
			"h1 {" +
			"   font-size: 48px;" +
			"   font-weight: bold;" +
			"   text-align: left;" +
			"}" +
			"urlTitle {" +
			"   font-size: 24px;" +
			"   font-weight: bold;" +
			"}" +
			"test {" +
			"   font-size: 24px;" +
			"   font-weight: bold;" +
			"   font-style: italic;" +
			"}" +
			"test.pass {" +
			"   color: #10640c;" +
			"}" +
			"test.fail {" +
			"   color: #b30000;" +
			"}" +
			"test.warning {" +
			"   color: #715409;" +
			"}" +
			"li {" +
			"   text-decoration: underline;" +
			"   cursor: pointer;" +
			"   font-size: 18px;" +
			"}" +
			"li.warning {" +
			"   color: #715409;" +
			"}" +
			"li.warning_serious {" +
			"   color: #715409;" +
			"}" +
			"li.fail {" +
			"   color: #b30000;" +
			"}" +
			"table {" +
			"   text-align: center;" +
			"   vertical-align: middle" +
			"   font-family: arial;" +
			"   border-collapse: collapse;" +
			"   width: 100%" +
			"}" +
			"tr, th, td  {" +
			"    border: 1px solid white;" +
			"    padding: 8px;" +
			"    font-weight: bold;" +
			"}" +
			"th {" +
			"   padding-top: 12px;" +
			"   padding-bottom: 12px;" +
			"   text-align: left;" +
			"   background-color: #4CAF50;" +
			"   color: white;" +
			"}" +
			"tr:nth-child(3n){" +
			"   background-color: #FF932E;" + //orange - #FF932E
			"}" +
			"tr:nth-child(3n+1){" +
			"   background-color: #F5FFA2;" + //yellow - #F5FFA2s
			"}" +
			"tr:nth-child(3n+2){" +
			"   background-color: #FF6161;" + //red - #FF6161
			"}" +
			"tr.hovercell:hover {" +
			"   background-color: white;" +
			"}" +
			"td.check {" +
			"   background-color: white;" +
			"   color: black;" +
			"}" +
			"metric {" +
			"   font-size: 18px" +
			"}" +
			"metric.pass {" +
			"   color: #10640c;" +
			"}" +
			"metric.fail {" +
			"   color: #b30000;" +
			"}" +
			"metric.warning_serious {" +
			"   color: #715409;" +
			"}" +
			"metric.warning {" +
			"   color: #715409;" +
			"}" +
			"xmp {" +
			"   font-size: 16px;" +
			"   white-space: pre-wrap;" +
			"   word-wrap: break-word;" +
			"}" +
			"</style>";

	private final String script = "" +
			"<script>" +
			"var eles = document.getElementsByTagName(\"code\");" +
			"for(var i=0; i<eles.length; i++) {" +
			"   eles[i].style.display = \"none\";" +
			"}" +
			"function toggleDisplay(x) {" +
			"   var ele = document.getElementById(x);" +
			"   if(ele.style.display === \"none\") {" +
			"       ele.style.display = \"block\";" +
			"   } else {" +
			"       ele.style.display = \"none\";" +
			"   }" +
			"}" +
			"</script>";

	private final List<String> header = Lists.newArrayList("<h1>", "</h1>");
	private final List<String> title = Lists.newArrayList("<urlTitle>", "</urlTitle>");
	private final List<String> testPass = Lists.newArrayList("<test class=\"pass\">", "</test>");
	private final List<String> testFail = Lists.newArrayList("<test class=\"fail\">", "</test>");
	private final List<String> testWarning = Lists.newArrayList("<test class=\"warning\">", "</test>");
	private final List<String> listStart = Lists.newArrayList("<ul>");
	private final List<String> listEnd = Lists.newArrayList("</ul>");
	private final List<String> table = Lists.newArrayList("<div style=\"overflow-x:auto;\">", "<table>", "</table>", "</div>");
	private final List<String> tableHeading = Lists.newArrayList("<th>", "</th>");
	private final List<String> tableCellHover = Lists.newArrayList("<td>", "</td>");
	private final List<String> tableCheck = Lists.newArrayList("<td class=\"check\"; rowspan=\"3\">", "</td>");
	private final List<String> metricPass = Lists.newArrayList("<metric class=\"pass\">", "</metric>");
	private final List<String> metricFail = Lists.newArrayList("<metric class=\"fail\">", "</metric>");
	private final List<String> metricWarningSerious = Lists.newArrayList("<metric class=\"warning_serious\">", "</metric>");
	private final List<String> metricWarning = Lists.newArrayList("<metric class=\"warning\">", "</metric>");
	private final List<String> sourceCode = Lists.newArrayList("<xmp>", "</xmp>");
	private final String newLine = "<br/>";

	private String date;

	public ConformanceReport() {
		new File("Reports").mkdir();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm");
		date = format.format(new Date());
		new File(getReportLocation()).mkdir();
	}

	private String getReportLocation() {
		return "Reports/" + date + "/";
	}

	private String addTable(String table) { return formatLine(table, this.table); }

	private String addTableRow(String Check, ArrayList<Result> results) {
		String row = "<tr class=\"hovercell\">";
		row += formatLine(Check, tableCheck);
		row += formatLine("&#9746; Fails", tableCellHover);
		for(Result r : results) {
			if (r.checked) {
				row += formatLine(r.fails + "", tableCellHover);
			} else {
				row += formatLine("", tableCellHover);
			}
		}
		row += "</tr> <tr class=\"hovercell\">";
		row += formatLine("&#9888; Serious Warnings", tableCellHover);
		for(Result r : results) {
			if (r.checked) {
				row += formatLine(r.warnSerious + "", tableCellHover);
			} else {
				row += formatLine("", tableCellHover);
			}
		}
		row += "</tr> <tr class=\"hovercell\">";
		row += formatLine(" &#9888; Warnings", tableCellHover);
		for(Result r : results) {
			if (r.checked) {
				row += formatLine(r.warn + "", tableCellHover);
			} else {
				row += formatLine("", tableCellHover);
			}
		}
		row += "</tr>";
		return row;
	}

	private String addTableHeading(ArrayList<String> headings) {
		String row = "<tr>";

		for(String h : headings) {
			row += formatLine(h, tableHeading);
		}

		return row + "</tr>";
	}

	private String addURL(String url) { return formatLine("Site: " + url, title); }

	private String addPage(String page) { return formatLine("Page: " + page, title); }

	private String addSource(String source) {return formatLine(source, sourceCode); }

	private String addImage(String path, String alt) {return "<img src=\"" + path + "\" alt=\"" + alt + "\">"; }

	private String addSourceID(String source, int ID) { return "<code id=\"" + ID + "\">" + source + "</code>"; }

	private String addPageMetrics(int passes, int fails, int warnings_serious, int warnings) {
		String pass = formatLine("Passes: " + passes, metricPass) + newLine;
		String warning = formatLine("Warnings: " + warnings, metricWarning) + newLine;
		String warningSerious = formatLine("Serious Warnings: " + warnings_serious, metricWarningSerious) + newLine;
		String fail = formatLine("Fails: " + fails, metricFail) + newLine;
		return pass + warning + warningSerious + fail;
	}

	private String addPassTest(String test) { return formatLine("&#9745; " + test + " - Passed", testPass); }

	private String addFailTest(String test) {
		return formatLine("&#9746; " + test + ":", testFail) +
				formatLine("", listStart);
	}

	private String addWarningTest(String test) {
		return formatLine("&#9888; " + test + ":", testWarning) +
				formatLine("", listStart);
	}

	private String addWarningElement(String test, int ID, boolean serious) {
		String s;
		if(serious) {
			s = "<li class=\"warning_serious\" onclick=\"toggleDisplay('" + ID +
					"')\">Warning (Serious): " + test + "</li>";
		}
		else {
			s = "<li class=\"warning\" onclick=\"toggleDisplay('" + ID +
					"')\">Warning: " + test + "</li>";
		}
		return s;
	}

	private String endList() { return formatLine("", listEnd); }

	private String addFailElement(String test, int ID) {
		return "<li class=\"fail\" onclick=\"toggleDisplay('" + ID +
				"')\">Error: " + test + "</li>";
	}

	public String getReport() { return reportFull; }

	private String formatLine(String line, List<String> params) {

		String formattedLine = "";

		for(int i=0; i<(params.size() / 2); i++) {
			formattedLine += params.get(i);
		}

		formattedLine += line;

		for(int i=(params.size() / 2); i<params.size(); i++) {
			formattedLine += params.get(i);
		}

		return formattedLine;
	}

	private String addAnchorTarget(String line, int anchor) { return "<a id=\"" + anchor + "\">" + line + "</a>"; }

	private String addAnchorName(String line, int anchor) { return "<a href=\"#" + anchor + "\">" + line + "</a>"; }

	public void generateReportFromPage(DatabaseInterface db, String url) {

		try {
			String site = db.partialiseFullURL(url).getKey();
			List<DBSimplePage> pages;
			List<List<DBSimplePage>> groupedPages = db.groupPagesByTimestamp(db.getPagesForSite(site));
			pages = groupedPages.get(groupedPages.size() - 1);

			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			Date resultDate = new Date(pages.get(0).timestamp);

			StringBuilder report = new StringBuilder();
			report.append("<html>");
			report.append(style);
			report.append("<body>");
			report.append(formatLine("Conformance Report", header));
			report.append(formatLine("Date: " + sdf.format(resultDate), title));
			report.append(newLine);
			report.append(newLine);
			report.append(newLine);
			report.append(addURL(site) + " - " + addAnchorName("Compare", anchorPage));
			anchorPage++;

			StringBuilder pMetBuff = new StringBuilder();
			StringBuilder pBuff = new StringBuilder();

			int passesSite = 0;
			int failsSite = 0;
			int warningsASite = 0;
			int warningsSSite = 0;

			for (DBSimplePage simpleP : pages) {

				boolean imagesPersistent = false;
				String pathReport = this.getReportLocation() + "images/" + simpleP.id + "/";
				File directoryReport = new File(pathReport);
				File persistentDir = new File(this.getReportLocation() + "ReportImageDatastore/" + simpleP.id);
				if(!directoryReport.exists()) {
					directoryReport.mkdirs();
				}
				if(persistentDir.exists()) {
					imagesPersistent = true;
					FileUtils.copyDirectory(persistentDir, directoryReport);
				}

				DBPage dbp = simpleP.loadFullPage(db);

				pMetBuff.append(addPage(addAnchorName(site + "/" + dbp.argURL, anchorPage)));
				pMetBuff.append(newLine);
				pBuff.append(addPage(addAnchorTarget(site + "/" + dbp.argURL, anchorPage)));
				pBuff.append(newLine);
				anchorPage++;

				HashMap<String, ArrayList<UnserialisedMarker>> checkMarkers = pagePerformance(dbp, db);

				List<String> keys = new ArrayList<>(checkMarkers.size());
				keys.addAll(checkMarkers.keySet());
				Collections.sort(keys);

				int passes = 0;
				int fails = 0;
				int warningsA = 0;
				int warningsS = 0;

				for(String check : keys) {
					ArrayList<UnserialisedMarker> currentCheck = checkMarkers.get(check);

					boolean passed = true;
					boolean failed = false;

					if(!checkPass(currentCheck) && checkFail(currentCheck)) {
						pBuff.append(addFailTest(check));
						passed = false;
						failed = true;
					}
					else if(!checkPass(currentCheck) && !checkFail(currentCheck)) {
						pBuff.append(addWarningTest(check));
						passed = false;
					}
					else {
						pBuff.append(addPassTest(check));
						pBuff.append(newLine);
						pBuff.append(newLine);
						passes++;
						continue;
					}

					for(UnserialisedMarker usm : currentCheck) {
						String pathDS = this.getReportLocation() + "ReportImageDatastore/" + simpleP.id + "/" + usm.id + ".png";
						File img = new File(pathDS);
						boolean image = img.exists();

						String pathReportImage = "images/" + simpleP.id + "/" + usm.id + ".png";

						if(usm.type == Marker.MARKER_ERROR) {
							pBuff.append(addFailElement(getFlagText(usm), anchorError));
							if(image && imagesPersistent) {
								String alt = usm.tag + " tag number " + usm.tagPos + ": " + usm.desc;
								pBuff.append(addSourceID(newLine + addImage(pathReportImage, alt) +
										addSource(usm.outerHTML), anchorError));
							}
							else {
								String oHTML = usm.outerHTML;
								String filteredoHTML = oHTML.replaceAll("<xmp>|</xmp>", "");
								pBuff.append(addSourceID(addSource(filteredoHTML), anchorError));
							}
							pBuff.append(newLine);
							anchorError--;
							fails++;
						}
						else if(!(usm.type == Marker.MARKER_SUCCESS)){
							pBuff.append(addWarningElement(getFlagText(usm), anchorError,
									usm.type == Marker.MARKER_AMBIGUOUS_SERIOUS));
							String oHTML = usm.outerHTML;
							String filteredoHTML = oHTML.replaceAll("<xmp>|</xmp>", "");
							if (image && imagesPersistent) {
								String alt = usm.tag + " tag number " + usm.tagPos + ": " + usm.desc;
								pBuff.append(addSourceID(newLine + addImage(pathReportImage, alt) +
										addSource(filteredoHTML), anchorError));
							}
							else {
								pBuff.append(addSourceID(addSource(filteredoHTML), anchorError));
							}
							pBuff.append(newLine);
							anchorError--;
							if(usm.type == Marker.MARKER_AMBIGUOUS) { warningsA++; }
							else { warningsS++; }
						}
					}

					if(!passed) {
						pBuff.append(endList());
					}
				}

				pMetBuff.append(addPageMetrics(passes, fails, warningsS, warningsA));
				pMetBuff.append(newLine);
				pMetBuff.append(newLine);

				pBuff.append(newLine);
				pBuff.append(newLine);

				passesSite += passes;
				failsSite += fails;
				warningsASite += warningsA;
				warningsSSite += warningsS;
			}

			report.append(newLine);
			report.append(addPageMetrics(passesSite, failsSite, warningsSSite, warningsASite));
			report.append(newLine);
			report.append(newLine);
			report.append(pMetBuff.toString());
			report.append(pBuff.toString());
			report.append(newLine);
			report.append("</body>");
			report.append(generateComparisonTable(db, groupedPages, site));
			report.append(script);
			report.append("</html>");
			reportFull = report.toString();

			writeToFile(reportFull, getReportLocation() + "report");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, ArrayList<UnserialisedMarker>> pagePerformance(DBPage dbp, DatabaseInterface db) throws Exception{

		HashMap<String, ArrayList<UnserialisedMarker>> checkMarkers = new HashMap<>();

		for(UnserialisedMarker usm : dbp.markers) {

			String check = usm.check.getName();
			if(checkMarkers.containsKey(check)) {
				checkMarkers.get(check).add(usm);
			}
			else {
				ArrayList<UnserialisedMarker> alBuf = new ArrayList<>();
				alBuf.add(usm);
				checkMarkers.put(usm.check.getName(), alBuf);
			}
		}

		return checkMarkers;
	}

	private String getFlagText(UnserialisedMarker usm) {
		String base = usm.desc != null ? "'" + usm.desc + "' " : "";
		String idString = usm.eleID != null && usm.eleID.length() > 0 ? "(" + usm.eleID + ")" : "";
		if (usm.tag != null) {
			if (usm.attribute != null) {
				return base + "at tag " + usm.tag + idString + "#" + usm.tagPos + " around attribute " + usm.attribute;
			} else {
				return base + "at tag " + usm.tag + idString + "#" + usm.tagPos;
			}
		}
		if (usm.position != -1) {
			return base + "at position " + usm.position;
		}
		return base;
	}

	private boolean checkPass(ArrayList<UnserialisedMarker> currentCheck) {
		boolean passed = true;
		for(UnserialisedMarker usm : currentCheck) {
			if(!(usm.type == Marker.MARKER_SUCCESS)) {
				passed = false;
				break;
			}
		}
		return passed;
	}

	private boolean checkFail(ArrayList<UnserialisedMarker> currentCheck) {
		boolean failed = false;
		for(UnserialisedMarker usm : currentCheck) {
			if(usm.type == Marker.MARKER_ERROR) {
				failed = true;
				break;
			}
		}
		return failed;
	}

	private String writeToFile(String source, String filename) throws Exception{
		File file = new File(filename + ".html");
		OutputStream outputStream = new FileOutputStream(file.getAbsoluteFile());
		Writer writer = new OutputStreamWriter(outputStream);
		writer.write(source);
		writer.close();
		return file.getAbsolutePath();
	}

	private String generateComparisonTable(DatabaseInterface db, List<List<DBSimplePage>> groupedPages, String url) throws Exception{

		String table = "";
		long currTimestamp = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");
		ArrayList<String> allChecks = new ArrayList<>();
		ArrayList<String> headers = new ArrayList<>();
		headers.add("Criterion");
		headers.add("");
		HashMap<String, ArrayList<Result>> tableHM = new HashMap<>();
		HashMap<String, ArrayList<Result>> siteHM = new HashMap<>();

		for(List<DBSimplePage> dbspList : groupedPages) {
			for(String check : pagePerformance(dbspList.get(0).loadFullPage(db), db).keySet()) {
				if(!allChecks.contains(check)) {
					allChecks.add(check);
				}
			}
		}
		Collections.sort(allChecks);
		for(String c : allChecks) {
			tableHM.put(c, new ArrayList<>());
			siteHM.put(c, new ArrayList<>());
		}

		for(List<DBSimplePage> dbspList : groupedPages) {
			currTimestamp = dbspList.get(0).timestamp;
			Date resultDate = new Date(currTimestamp);

			int siteFails = 0;
			int siteWarn = 0;
			int siteSerWarn = 0;
			Boolean sitePass = true;

			for(DBSimplePage dbsp : dbspList) {
				headers.add(sdf.format(resultDate) + " - " + url + "/" + dbsp.page);

				DBPage dbp = dbsp.loadFullPage(db);

				HashMap<String, ArrayList<UnserialisedMarker>> pagePerf = pagePerformance(dbp, db);
				for(String check : allChecks) {

					if(!pagePerf.keySet().contains(check)) {
						tableHM.get(check).add(new Result());
						continue;
					}

					int checkFails = 0;
					int checkWarn = 0;
					int checkSerWarn = 0;
					Boolean checkPass = true;

					for(UnserialisedMarker usm : pagePerf.get(check)) {
						if(usm.type == Marker.MARKER_ERROR) { checkFails++; siteFails++; }
						else if(usm.type == Marker.MARKER_AMBIGUOUS) { checkWarn++; siteWarn++; }
						else if(usm.type == Marker.MARKER_AMBIGUOUS_SERIOUS) { checkSerWarn++; siteSerWarn++; }
					}
					checkPass = (checkFails == 0 && checkSerWarn == 0 && checkWarn == 0);
					if(sitePass && !checkPass) { sitePass = false; }
					tableHM.get(check).add(new Result(checkFails, checkSerWarn, checkWarn));
				}
			}
		}

		String mainTable = "";
		for(String check : allChecks) {
			mainTable += addTableRow(check, tableHM.get(check));
		}
		table = addAnchorTarget(addTable(addTableHeading(headers) + mainTable), 0);
		return table;
	}

	private class Result {
		private int fails;
		private int warnSerious;
		private int warn;
		private Boolean checked;
		private Result(int fails, int warnSerious, int warn) {
			this.fails = fails;
			this.warnSerious = warnSerious;
			this.warn = warn;
			this.checked = true;
		}
		private Result() {
			this.checked = false;
		}
	}

	public void addCheckImages(DatabaseInterface db, String url, SeleniumInterface inter) throws Exception{

		String site = db.partialiseFullURL(url).getKey();
		List<DBSimplePage> pages;
		List<List<DBSimplePage>> groupedPages = db.groupPagesByTimestamp(db.getPagesForSite(site));
		pages = groupedPages.get(groupedPages.size() - 1);
		DBSimplePage simpleP = pages.get(pages.size() - 1);

		DBPage dbp = simpleP.loadFullPage(db);

		HashMap<String, ArrayList<UnserialisedMarker>> checkMarkers = pagePerformance(dbp, db);

		List<String> keys = new ArrayList<>(checkMarkers.size());
		keys.addAll(checkMarkers.keySet());
		Collections.sort(keys);

		String path = this.getReportLocation() + "ReportImageDatastore/" + simpleP.id + "/";
		File directory = new File(path);
		if(!directory.exists()) {
			directory.mkdirs();
		}

		int maxH = 1080;
		int maxW = 1920;
		int imgBuffer = 25;

		for(String check : keys) {

			ArrayList<UnserialisedMarker> currentCheck = checkMarkers.get(check);

			for (UnserialisedMarker usm : currentCheck) {
				if (usm.tag != null) {
					WebElement ele = inter.getElementsByTagName(usm.tag)[usm.tagPos];
					if (ele.getLocation().x + ele.getSize().width + (2 * imgBuffer) > maxW) {
						maxW = ele.getLocation().x + ele.getSize().width + (2 * imgBuffer);
					}
					if (ele.getLocation().y + ele.getSize().height + (2 * imgBuffer) > maxH) {
						maxH = ele.getLocation().y + ele.getSize().height + (2 * imgBuffer);
					}
				}
			}
		}

		//inter.driver.manage().window().setSize(new Dimension(maxW, maxH));
		BufferedImage bImg = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(inter.driver).getImage();

		for(String check : keys) {

			ArrayList<UnserialisedMarker> currentCheck = checkMarkers.get(check);

			/*
			int maxH = 1080;
			int maxW = 1920;
			int imgBuffer = 25;

			for(UnserialisedMarker usm : currentCheck) {
				if(usm.tag != null) {
					WebElement ele = inter.getElementsByTagName(usm.tag)[usm.tagPos];
					if(ele.getLocation().x + ele.getSize().width + (2 * imgBuffer) > maxW) {
						maxW = ele.getLocation().x + ele.getSize().width + (2 * imgBuffer);
					}
					if(ele.getLocation().y + ele.getSize().height + (2 * imgBuffer) > maxH) {
						maxH = ele.getLocation().y + ele.getSize().height + (2 * imgBuffer);
					}
				}
			}

			inter.driver.manage().window().setSize(new Dimension(maxW, maxH));
			File screesnshot = ((TakesScreenshot)inter.driver).getScreenshotAs(OutputType.FILE);
			File eleSSFile = ((TakesScreenshot)inter.driver).getScreenshotAs(OutputType.FILE);
			BufferedImage bImg = ImageIO.read(screesnshot);
			 */

			ColorModel cm = bImg.getColorModel();
			boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			WritableRaster raster = bImg.copyData(null);

			for(UnserialisedMarker usm : currentCheck) {

				if(usm.tag != null) {
					try {
						WebElement ele = inter.getElementsByTagName(usm.tag)[usm.tagPos];

						Point p = ele.getLocation();
						int eleW = ele.getSize().width;
						int eleH = ele.getSize().height;
						int px = p.x;
						int py = p.y;
						int rectX = 0;
						int rectY = 0;

						if(px > 0 && py > 0 && eleH > 0 && eleW > 0) {
							if(p.y + eleH + (2*imgBuffer) < inter.driver.manage().window().getSize().height) {
								eleH += 2 * imgBuffer;
							}
							if(p.x + eleW +  (2*imgBuffer) < inter.driver.manage().window().getSize().width) {
								eleW += 2 * imgBuffer;
							}
							if(px - imgBuffer > 0) {
								px -= imgBuffer;
								rectX = imgBuffer;
							}
							if(py - imgBuffer > 0) {
								py -= imgBuffer;
								rectY = imgBuffer;
							}

							if(!(ele.getLocation().y + ele.getSize().height + (2 * imgBuffer) > maxH) &&
									!(ele.getLocation().x + ele.getSize().width + (2 * imgBuffer) > maxW)) {
								BufferedImage eleSS = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
								/*
							if (px > bImg.getWidth() || py > bImg.getHeight()) {

								//TODO this often occurs!

								Graphics2D g = bImg.createGraphics();
								g.setColor(Color.RED);
								g.drawRect(rectX, rectY, ele.getSize().width, ele.getSize().height);
								ImageIO.write(bImg, "png", eleSSFile);
								FileUtils.copyFile(eleSSFile, new File(path + usm.id + ".png"));
							} else {
								 */
								try {
									eleSS = eleSS.getSubimage(px, py, Math.min(eleW, bImg.getWidth() - px), Math.min(eleH, bImg.getHeight() - py));
									Graphics2D g = eleSS.createGraphics();
									g.setColor(Color.RED);
									g.drawRect(rectX, rectY, ele.getSize().width, ele.getSize().height);
									ImageIO.write(eleSS, "png", new File(path + usm.id + ".png"));
								} catch (Exception e) { e.printStackTrace(); }
								//}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void deleteReport(File dir) {
		try {
			if(!dir.exists()) {
			}
			else {
				if(dir.isDirectory()) {
					if(dir.list().length == 0) {
						dir.delete();
					}
					else {
						String[] files = dir.list();
						for(String f : files) {
							File fileD = new File(dir, f);
							deleteReport(fileD);
						}
					}
					if(dir.list().length == 0) {
						dir.delete();
					}
				}
				else {
					dir.delete();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
