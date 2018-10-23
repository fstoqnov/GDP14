package code.checks;

import code.selenium_interface.Interface;

public class StatusMessages extends Check {

    protected StatusMessages() { super("Criterion 4.1.3 Status Messages"); }

    @Override
    public boolean runCheck(String urlContent, Interface inter) {
        // TODO Auto-generated method stub
        return false;
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