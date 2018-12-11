package code.checks;

import java.util.*;

import com.google.gson.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import code.Marker;
import code.interfaces.SeleniumInterface;
import tests.Test;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebElement;

public class Parsing extends Check {

	private static enum ResultType implements ResultT {
		ERROR,
		SUCCESS,
		WARNING_HTML,
		WARNING_CONNECTION_FAILURE
	}
	
    public Parsing() { super("Criterion 4.1.1 Parsing"); }

    @Override
	public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) {

        Properties log4jProp = new Properties();
        log4jProp.setProperty("log4j.rootLogger", "WARN");
        PropertyConfigurator.configure(log4jProp);

        boolean success = true;

        try {
            JsonArray jsonResponse = getValidationReport(inter, "http://validator.w3.org/nu/",
                    inter.getCurrentURL());

            if((jsonResponse.get(0).getAsJsonObject().get("type").getAsString().equals("non-document-error"))) {
                jsonResponse = getValidationReportString(inter, "http://validator.w3.org/nu/",
                        urlContent);
            }

            List<WebElement> eles = inter.getAllElements();
            List<WebElement> elesContaining = new ArrayList<>();

            for(JsonElement je : jsonResponse) {
                JsonObject jo = je.getAsJsonObject();

                String type = jo.get("type").getAsString();

                String message = jo.get("message").getAsString();
                message = message.replace('\u201C', '"');
                message = message.replace('\u201D', '"');
                String extract = "";
                WebElement ele = null;

                String[] filtersArray = {"tag seen", "Stray end tag", "Bad start tag", "violates nesting rules",
                        "Duplicate ID", "first occurrence of ID", "Unclosed element", "not allowed as child of element",
                        "unclosed elements", "not allowed on element", "unquoted attribute value",
                        "Duplicate attribute"};
                if(Arrays.stream(filtersArray).parallel().noneMatch(message::contains)) {
                    continue;
                }

                if ((type.equals("error") || type.equals("warning")) && jo.has("extract")) {
                    int start = jo.get("hiliteStart").getAsInt();
                    int end = start + jo.get("hiliteLength").getAsInt();
                    extract = jo.get("extract").getAsString();//.substring(start, end);

                }

                if (type.equals("error")) {
                    markers.add(new Marker(message, Marker.MARKER_ERROR, this, extract, ResultType.ERROR));
                    success = false;
                }
                else if (type.equals("warning")) {
                    markers.add(new Marker(message, Marker.MARKER_AMBIGUOUS, this, extract, ResultType.WARNING_HTML));
                    success = false;
                }
            }

            if(success) {
                markers.add(new Marker("HTML succesfully validated by parser", Marker.MARKER_SUCCESS, this, ResultType.SUCCESS));
            }
        }
        catch(UnirestException e) {
            markers.add(new Marker("Could not connect to W3C Markup Validation service - check not run", Marker.MARKER_AMBIGUOUS, this, ResultType.WARNING_CONNECTION_FAILURE));
        }
    }

    public JsonArray getValidationReport(SeleniumInterface inter, String validator, String content) throws UnirestException{

        String response;
        Map<String, Object> queryConf = new HashMap<>();
        queryConf.put("doc", content);
        queryConf.put("out", "json");

        HttpResponse<String> uniResponse = Unirest.get(validator)
                .queryString(queryConf)
                .asString();

        response = uniResponse.getBody();

        Gson g = new Gson();

        return g.fromJson(response, JsonObject.class).getAsJsonArray("messages");
    }

    public JsonArray getValidationReportString(SeleniumInterface inter, String validator, String content) throws UnirestException{

        String response;
        Map<String, Object> queryConf = new HashMap<>();
        queryConf.put("out", "json");

        HttpResponse<String> uniResponse = Unirest.post(validator)
                .queryString(queryConf)
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(content)
                .asString();

        response = uniResponse.getBody();

        Gson g = new Gson();

        return g.fromJson(response, JsonObject.class).getAsJsonArray("messages");
    }



    
    public void setupTests() {
    	this.tests.add(new Test("<!DOCTYPE html><html lang=\"en\"><head><title><div><img /></div></title></head></html>", new ResultT[] {ResultType.SUCCESS}));
    	
    	this.tests.add(new Test("<!DOCTYPE html><html><head><div><img></img></div></html>", new ResultT[] {ResultType.ERROR}));

    	this.tests.add(new Test("<!DOCTYPE html><html><head><p id='id1'><p id='id1'><div><img /></head></div></html>", new ResultT[] {ResultType.ERROR}));

    	this.tests.add(new Test( "<!DOCTYPE html><html><head><div><img /></head></div></html>", new ResultT[] {ResultType.ERROR}));

    }

    @Override
    public void initialise() {
    }

}