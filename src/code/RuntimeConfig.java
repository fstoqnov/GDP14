package code;

import java.util.ArrayList;

public class RuntimeConfig {
	
	public ArrayList<CheckURL> urls;
	public boolean runDynamicChecks;
	public boolean controlPrior;
	
	public RuntimeConfig(ArrayList<CheckURL> urls) {
		this.urls = urls;
	}
	
	public boolean headedRequired() {
		for (int i = 0; i < urls.size(); i ++) {
			if (urls.get(i).dynamic || urls.get(i).loginRequired) {
				return true;
			}
		}
		return false;
	}
}