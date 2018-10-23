package code.checks;

import code.selenium_interface.Interface;
import org.apache.commons.exec.util.StringUtils;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parsing extends Check {

    protected Parsing() { super("Criterion 4.1.1 Parsing"); }

    @Override
    public boolean runCheck(String urlContent, Interface inter) {

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
                return false;
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
                    return false;
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
                    return false;
                }
                else if(parse.get(parse.size()-1).equals(eN.split("/")[1].split(">")[0])) {
                    parse.remove(parse.size()-1);
                }
                else return false;
            }
            else if(eN.contains("/>")) {
                continue;
            }
            else {
                parse.add(eN.split("<")[1].split("[ >]")[0]);
            }
        }

        if(parse.size() == 0) {
            return true;
        }
        else {
            return false;
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