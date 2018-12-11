package code;

import java.util.ArrayList;

public class RunChecks {
	public static void main(String[] args) {
		CheckList cl = new CheckList();
		try {
			cl.runChecksAtURLs(getConfig(args));
		} catch (Exception e) {
			e.printStackTrace();
		}
		GlobalKeyListener.stop();
	}
	
	public static RuntimeConfig getConfig(String[] args) {
		ArrayList<CheckURL> urls = new ArrayList<CheckURL>();
		for (int i = 0; i < args.length; i += 3) {
			urls.add(new CheckURL(args[i], Boolean.parseBoolean(args[i + 1]), Boolean.parseBoolean(args[i + 2])));
		}
		return new RuntimeConfig(urls);
	}
}