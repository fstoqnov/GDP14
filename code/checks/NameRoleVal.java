package code.checks;

import code.Marker;
import code.interfaces.SeleniumInterface;

import java.util.List;

public class NameRoleVal extends Check{

    public NameRoleVal() { super("Criterion 4.1.2 Name, Role, Value");}

    @Override
    public void runCheck(String urlContent, List<Marker> markers, SeleniumInterface inter) { }

    @Override
    public String[] getHTMLPass() {
    }

    @Override
    public String[] getHTMLFail() {
    }

    @Override
    public void initialise() {
        // TODO Auto-generated method stub

    }

}
