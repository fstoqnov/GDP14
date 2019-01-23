package code;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import code.interfaces.DatabaseInterface;

public class RunChecks {

	public static boolean driverExists = true;
	public static boolean couldRunLanguage = true;

	public static void main(String[] args) {
		checkChromeDriver();
		CheckList cl = new CheckList();
		if (args.length != 0) {
			try {
				if (args.length == 2) {
					cl.runChecksAtURLs(getConfigFromFile(args[1]), new DatabaseInterface(args[0]));
				} else {
					cl.runChecksAtURLs(getConfig(args), new DatabaseInterface(args[0]));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			new ViewFrame();
		}
	}

	public static void checkChromeDriver() {
		if (!new File("chromedriver.exe").exists()) {
			try {
				BufferedInputStream in = new BufferedInputStream(new URL("https://chromedriver.storage.googleapis.com/2.45/chromedriver_win32.zip").openStream());
				FileOutputStream fileOutputStream = new FileOutputStream("chromedriver.zip");
				byte dataBuffer[] = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
					fileOutputStream.write(dataBuffer, 0, bytesRead);
				}

				fileOutputStream.close();
				in.close();

				FileInputStream fis;
				byte[] buffer = new byte[1024];
				try {
					fis = new FileInputStream("chromedriver.zip");
					ZipInputStream zis = new ZipInputStream(fis);
					ZipEntry ze = zis.getNextEntry();
					while(ze != null){
						String fileName = ze.getName();
						File newFile = new File(fileName);
						FileOutputStream fos = new FileOutputStream(newFile);
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
						fos.close();
						zis.closeEntry();
						ze = zis.getNextEntry();
					}
					zis.closeEntry();
					zis.close();
					fis.close();
				} catch (IOException e) {
					driverExists = false;
				}

				new File("chromedriver.zip").delete();
			} catch (Exception e) {
				driverExists = false;
			}
		}
	}

	public static RuntimeConfig getConfig(String[] args) {
		ArrayList<CheckURL> urls = new ArrayList<CheckURL>();
		for (int i = 1; i < args.length; i += 3) {
			urls.add(new CheckURL(args[i], Boolean.parseBoolean(args[i + 1]), Boolean.parseBoolean(args[i + 2])));
		}
		return new RuntimeConfig(urls);
	}

	public static RuntimeConfig getConfigFromFile(String file) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String line;
		int i = 0;

		ArrayList<CheckURL> urls = new ArrayList<CheckURL>();
		String url = null;
		Boolean dynamic = null;
		Boolean login = null;

		while ((line = br.readLine()) != null) {
			if (line.trim().equals("")) { continue; }
			line = line.trim();
			if (i == 0) {
				url = line;
			} else if (i == 1) {
				dynamic = Boolean.parseBoolean(line);
			} else if (i == 2) {
				login = Boolean.parseBoolean(line);
			}
			i ++;
			if (i == 3) {
				urls.add(new CheckURL(url, dynamic, login));
			}
			i %= 3;
		}

		br.close();

		return new RuntimeConfig(urls);
	}
}