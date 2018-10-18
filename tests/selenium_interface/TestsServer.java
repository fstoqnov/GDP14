package tests.selenium_interface;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TestsServer implements Runnable {
	private ServerSocket ss;
	
	private boolean error = false;
	
	public void createServer(int port) throws Exception {
		ss = new ServerSocket(port);
		(new Thread(this)).start();
	}
	
	public void sendBasicHtml(Socket s) throws Exception {
		String html = "<html><head></head><body><div id=\"val\" class=\"test\"></div><script>document.getElementById(\"val\").innerHtml = \"hello world!\"</script></html>";
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		while ((br.readLine()) != null) {  }
		bw.write("HTTP/1.0 200 OK\r\n");
        bw.write("Date: Fri, 18 October 2018 23:59:59 GMT\r\n");
        bw.write("Server: Apache/0.8.4\r\n");
        bw.write("Content-Type: text/html\r\n");
        bw.write("Content-Length: " + html.length() + "\r\n");
        bw.write("Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n");
        bw.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
        bw.write("\r\n");
        bw.write(html);
        br.close();
        bw.close();
		s.close();
	}

	public static String renderedHTML = "<html><head></head><body><div id=\"val\" class=\"test\">hello world!</div><script>document.getElementById(\"val\").innerHtml = \"hello world!\"</script></html>";

	public void run() {
		try {
			sendBasicHtml(ss.accept());
		} catch (Exception e) { e.printStackTrace(); error = true; }
		try {
			ss.close();
		} catch (Exception e) { e.printStackTrace(); error = true; }
	}

	public boolean hadErrors() { return error; }
}