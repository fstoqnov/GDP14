package code.checks;

import java.util.*;

import com.google.gson.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import code.Marker;
import code.interfaces.SeleniumInterface;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.log4j.PropertyConfigurator;

public class Parsing extends Check {

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

            for(JsonElement je : jsonResponse) {
                JsonObject jo = je.getAsJsonObject();

                String type = jo.get("type").getAsString();
                String message = jo.get("message").getAsString();
                message = message.replace('\u201C', '"');
                message = message.replace('\u201D', '"');
                String extract = "";

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
                    extract = jo.get("extract").getAsString().substring(start, end);
                }

                if (type.equals("error")) {
                    markers.add(new Marker(message, Marker.MARKER_ERROR, this, null));
                    success = false;
                }
                else if (type.equals("warning")) {
                    markers.add(new Marker(message, Marker.MARKER_AMBIGUOUS, this, null));
                    success = false;
                }
            }

            if(success) {
                markers.add(new Marker(Marker.MARKER_SUCCESS, this, null));
            }
        }
        catch(UnirestException e) {
            markers.add(new Marker(Marker.MARKER_ERROR, this, null));
        }
    }

    public JsonArray getValidationReport(SeleniumInterface inter, String validator, String content) throws UnirestException{

        String response;
        Map<String, Object> queryConf = new HashMap<String, Object>();
        queryConf.put("doc", content);
        queryConf.put("out", "json");

        HttpResponse<String> uniResponse = Unirest.get(validator)
                .queryString(queryConf)
                .asString();

        response = uniResponse.getBody();

        Gson g = new Gson();

        return g.fromJson(response, JsonObject.class).getAsJsonArray("messages");
    }

    @Override
    public String[] getHTMLPass() {
        return new String[] {
                "<!DOCTYPE html><html lang=\"en\"><head><title><div><img /></div></title></head></html>"
        };
    }

    @Override
    public String[] getHTMLFail() {
        return new String[] {
                "<!DOCTYPE html><html><head><div><img></img></div></head></html>",
                "<!DOCTYPE html><html><head><div><img /></head></div></html>"
        };
    }

    @Override
    public void initialise() {
        // TODO Auto-generated method stub

    }

}