package code;

import code.interfaces.DatabaseInterface;
import com.google.common.collect.Lists;
import database_records.DBPage;
import database_records.DBSimplePage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConformanceReport {

    private String report;

    private final List<String> header = Lists.newArrayList("<h1 style=\"font-size: 48px; text-align: left;\">",
            "<strong>", "</strong>", "</h>");
    private final List<String> title = Lists.newArrayList("<p>", "<span style=\"font-size: 24px;\">",
            "<strong>", "</strong>", "</span>", "</p>");
    private final List<String> testPass = Lists.newArrayList("<p>",
            "<span style=\"font-size: 24px; color: #10640c;\">", "<em>", "<strong>", "</strong>", "</em>", "</span>",
            "</p>");
    private final List<String> testFail = Lists.newArrayList("<p>",
            "<span style=\"font-size: 24px; color: #b30000;\">", "<em>", "<strong>", "</strong>", "</em>", "</span>",
            "</p>");
    private final List<String> listWarningElement = Lists.newArrayList("<li>",
            "<span style=\"font-size: 18px; color: #715409;\">", "</span>", "</li>");
    private final List<String> listStart = Lists.newArrayList("<ul>", "<span style=\"font-size: 18px;\">");
    private final List<String> listEnd = Lists.newArrayList("</span>", "</ul>");
    private final List<String> listElement = Lists.newArrayList("<li>",
            "<span style=\"font-size: 18px; color: #b30000;\">", "</span>", "</li>");

    private String addURL(String url) { return formatLine("URL: " + url, title);}

    private String addPassTest(String test) { return formatLine("&#9745; " + test + " - Passed", testPass); }

    private String addFailTest(String test) {
        return formatLine("&#9746; " + test + ":", testFail) +
                formatLine("", listStart);
    }

    private String addWarningElement(String test, boolean serious) {
        String s;
        if(serious) {
            s = formatLine("Warning (Serious): " + test, listWarningElement);
        }
        else {
            s = formatLine("Warning : " + test, listWarningElement);
        }
        return s;
    }

    private String endList() { return formatLine("", listEnd); }

    private String addFailElement(String element) { return formatLine("Error: " + element, listElement); }

    public String getReport() { return report; }

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

    public void generateReportFromPage(DatabaseInterface db, String site) {

        try {
            List<DBSimplePage> pages;
            List<List<DBSimplePage>> groupedPages = db.groupPagesByTimestamp(db.getPagesForSite(site));
            pages = groupedPages.get(groupedPages.size() - 1);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date resultDate = new Date(pages.get(0).timestamp);
            report = "<html>";
            report += formatLine("Conformance Report", header) +
                    formatLine("Date: " + sdf.format(resultDate), title) +
                    addURL(site);

            HashMap<String, ArrayList<UnserialisedMarker>> checkMarkers = new HashMap<>();

            for (DBSimplePage dbsp : pages) {

                DBPage fullPage = dbsp.loadFullPage(db);
                for(UnserialisedMarker usm : fullPage.markers) {

                    if(checkMarkers.containsKey(usm.check.getName())) {
                        checkMarkers.get(usm.check.getName()).add(usm);
                    }
                    else {
                        ArrayList<UnserialisedMarker> alBuf = new ArrayList<>();
                        alBuf.add(usm);
                        checkMarkers.put(usm.check.getName(), alBuf);
                    }
                }
            }

            List<String> keys = new ArrayList<>(checkMarkers.size());
            keys.addAll(checkMarkers.keySet());
            Collections.sort(keys);

            for(String check : keys) {
                ArrayList<UnserialisedMarker> currentCheck = checkMarkers.get(check);

                boolean passed = true;

                if(!checkPass(currentCheck)) {
                    report += addFailTest(check);
                    passed = false;
                }

                for(UnserialisedMarker usm : currentCheck) {

                    if(passed) {
                        report += addPassTest(check);
                        break;
                    }
                    else if(usm.type == Marker.MARKER_ERROR) {
                        report += addFailElement(getFlagText(usm));
                    }
                    else {
                        report += addWarningElement(getFlagText(usm), usm.type == Marker.MARKER_AMBIGUOUS_SERIOUS);
                    }
                }

                if(!passed) {
                    report += endList();
                }
            }

            report += "</html>";

            writeToFile();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getFlagText(UnserialisedMarker usm) {
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

    public void writeToFile() throws Exception{

        File file = new File("report.html");
        OutputStream outputStream = new FileOutputStream(file.getAbsoluteFile());
        Writer writer = new OutputStreamWriter(outputStream);
        writer.write(report);
        writer.close();
    }
}
