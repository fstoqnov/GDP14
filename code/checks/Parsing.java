package code.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebElement;

import code.selenium_interface.Interface;

public class Parsing extends Check {

    protected Parsing() { super("Criterion 4.1.1 Parsing"); }

    @Override
	public void runCheck(String urlContent, List<Marker> markers, Interface inter) {

        String[] forbidden = {"area", "base", "br", "col", "command", "embed", "hr", "img", "input", "keygen", "link",
        "meta", "param", "source", "track", "wbr"};

        List<WebElement> el = inter.getAllElements();

        ArrayList<String> elementsN = new ArrayList<String>();
        ArrayList<String> parse = new ArrayList<String>();
        ArrayList<String> ids = new ArrayList<String>();

        String htmltag = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
        String idtag = " id=\"(.*?)\"";

        Pattern p = Pattern.compile(htmltag);
        Pattern idp = Pattern.compile(idtag);
        Matcher idMatcher = idp.matcher(urlContent);
        Matcher matcher = p.matcher(urlContent);

        while(matcher.find()) {
            elementsN.add(matcher.group(0));
        }

        while(idMatcher.find()) {
            if(ids.contains(idMatcher.group(0))) {
            	//TODO add error marker
            }
            else {
                ids.add(idMatcher.group(0));
            }
        }

        for( WebElement w : el) {
            ArrayList<String> attr = new ArrayList<String>();
            String outer = w.getAttribute("outerHTML");
            Pattern pattern = Pattern.compile("([a-z]+-?[a-z]+_?)=('?\"?)");
            Matcher m = pattern.matcher(outer);
            while (m.find()) {
                if(attr.contains(m.group(1))) {
                	//TODO add error marker
                }
                else {
                    attr.add(m.group(1));
                }
            }
        }

        for(String eN : elementsN) {
            if(eN.contains("<!")) {
                continue;
            }
            else if(eN.contains("</") ) {
                if(Arrays.asList(forbidden).contains(eN.split("/")[1].split(">")[0])) {
                    //TODO add error marker
                }
                else if(parse.get(parse.size()-1).equals(eN.split("/")[1].split(">")[0])) {
                    parse.remove(parse.size()-1);
                }
                else {
                	//TODO add error marker
                }
            }
            else if(eN.contains("/>")) {
                continue;
            }
            else {
                parse.add(eN.split("<")[1].split("[ >]")[0]);
            }
        }

        if(parse.size() == 0) {
        	//TODO add success marker
        }
        else {
        	//TODO add error marker
        }
    }

    @Override
    public String[] getHTMLPass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getHTMLFail() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void initialise() {
        // TODO Auto-generated method stub

    }

}