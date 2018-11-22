package code;

import code.interfaces.DatabaseInterface;
import com.google.common.collect.Lists;
import database_records.DBPage;
import database_records.DBSimplePage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConformanceReport {

    private String reportFull;
    private int anchorPage = 0;
    private int anchorError = -1;

    private final String style = "" +
            "<style>" +
            "body {" +
            "   font-family: arial;" +
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
            "li {" +
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
            "</style>";

    private final List<String> header = Lists.newArrayList("<h1>", "</h1>");
    private final List<String> title = Lists.newArrayList("<urlTitle>", "</urlTitle>");
    private final List<String> testPass = Lists.newArrayList("<test class=\"pass\">", "</test>");
    private final List<String> testFail = Lists.newArrayList("<test class=\"fail\">", "</test>");
    private final List<String> listStart = Lists.newArrayList("<ul>");
    private final List<String> listElement = Lists.newArrayList("<li class=\"fail\">", "</li>");
    private final List<String> listWarningSeriousElement = Lists.newArrayList("<li class=\"warning_serious\">", "</li>");
    private final List<String> listWarningElement = Lists.newArrayList("<li class=\"warning\">", "</li>");
    private final List<String> listEnd = Lists.newArrayList("</ul>");
    private final List<String> metricPass = Lists.newArrayList("<metric class=\"pass\">", "</metric>");
    private final List<String> metricFail = Lists.newArrayList("<metric class=\"fail\">", "</metric>");
    private final List<String> metricWarningSerious = Lists.newArrayList("<metric class=\"warning_serious\">", "</metric>");
    private final List<String> metricWarning = Lists.newArrayList("<metric class=\"warning\">", "</metric>");
    private final String newLine = "<br/>";

    private String addURL(String url) { return formatLine("Site: " + url, title);}

    private String addPage(String page) { return formatLine("Page: " + page, title);}

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

    private String addWarningElement(String test, boolean serious) {
        String s;
        if(serious) {
            s = formatLine("Warning (Serious): " + test, listWarningSeriousElement);
        }
        else {
            s = formatLine("Warning : " + test, listWarningElement);
        }
        return s;
    }

    private String endList() { return formatLine("", listEnd); }

    private String addFailElement(String element) { return formatLine("Error: " + element, listElement); }

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
            report.append(addURL(site));

            StringBuilder pMetBuff = new StringBuilder();
            StringBuilder pBuff = new StringBuilder();

            int passesSite = 0;
            int failsSite = 0;
            int warningsASite = 0;
            int warningsSSite = 0;

            for (DBSimplePage simpleP : pages) {
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

                    if(!checkPass(currentCheck)) {
                        pBuff.append(addFailTest(check));
                        passed = false;
                    }

                    for(UnserialisedMarker usm : currentCheck) {

                        if(passed) {
                            pBuff.append(addPassTest(check));
                            pBuff.append(newLine);
                            pBuff.append(newLine);
                            passes++;
                            break;
                        }
                        else if(usm.type == Marker.MARKER_ERROR) {
                            pBuff.append(addFailElement(getFlagText(usm)));
                            fails++;
                        }
                        else {
                            pBuff.append(addWarningElement(getFlagText(usm),
                                    usm.type == Marker.MARKER_AMBIGUOUS_SERIOUS));
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
            report.append("</body>");
            report.append("</html>");
            reportFull = report.toString();

            writeToFile();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, ArrayList<UnserialisedMarker>> pagePerformance(DBPage dbp, DatabaseInterface db) throws Exception{

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

    private void writeToFile() throws Exception{

        File file = new File("report.html");
        OutputStream outputStream = new FileOutputStream(file.getAbsoluteFile());
        Writer writer = new OutputStreamWriter(outputStream);
        writer.write(reportFull);
        writer.close();
    }
}
