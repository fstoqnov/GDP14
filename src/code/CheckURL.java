package code;

public class CheckURL {
	public String checkURL;
	public boolean dynamic;
	public boolean loginRequired;
	
	public CheckURL(String checkURL, boolean dynamic, boolean loginRequired) {
		this.checkURL = checkURL;
		this.dynamic = dynamic;
		this.loginRequired = loginRequired;
	}
}
