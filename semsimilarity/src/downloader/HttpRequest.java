package downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * @author mingchen
 * @date 2015.4.12
 * 
 */
public class HttpRequest {

	/**
	 * @param url
	 * @param parameters
	 * @return HttpResponse
	 * 
	 *         This method use the get mode to retrieve the page
	 */
	public static HttpResponse requestGet(String url, String parameters) {
		BufferedReader br = null;
		String content = "";
		HttpResponse response = null;
		int totalLength;
		try {
			String urlStr = url + "?" + parameters;
			URL realUrl = new URL(urlStr);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.connect();
			response = new HttpResponse(urlStr);
			// Initialize the Header
			Header header = new Header();
			Map<String, List<String>> map = conn.getHeaderFields();
			header.setConnection(map.get("Connection").toString());
			header.setContentLength(map.get("Content-Length").toString());
			header.setContentType(map.get("Content-Type").toString());
			header.setCookie(map.get("Set-Cookie").toString());
			header.setServer(map.get("Server").toString());
			response.setHeader(header);
			totalLength = Integer.valueOf(header.getContentLength().substring(1,header.getContentLength().length()-1));

			// read the content of the page
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = br.readLine();
			int count = 0, curTotal = 0;
			double ratio = 0.0, older = 0.0;
			while (line != null) {
				count += 1;
				curTotal += line.getBytes().length;
				content += line
						+ new Properties(System.getProperties())
				.getProperty("line.separator");

				ratio = curTotal * 1.0 / totalLength;
				if (ratio - older >= 0.01) {
					System.out.printf("%3.0f %s\n", ratio * 100, "%");
					older = ratio;
				}
				line = br.readLine();
			}
			System.out.println("100 % done");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return response;
	}

	public static HttpResponse requestPost(String url, String parameters) {
		BufferedReader br = null;
		String content = "";
		PrintWriter out = null;
		HttpResponse response = null;
		int totalLength = 0;
		try {
			String urlStr = url + "?" + parameters;
			URL realUrl = new URL(urlStr);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.write(parameters);
			out.flush();
			response = new HttpResponse(urlStr);
			Header header = new Header();
			Map<String, List<String>> map = conn.getHeaderFields();
			header.setConnection(map.get("Connection").toString());
			header.setContentLength(map.get("Content-Length").toString());
			header.setContentType(map.get("Content-Type").toString());
			header.setCookie(map.get("Set-Cookie")==null?"":map.get("Set-Cookie").toString());
			header.setServer(map.get("Server").toString());

			response.setHeader(header);
			totalLength = Integer.valueOf(header.getContentLength().substring(1,header.getContentLength().length()-1));
			System.out.println("Start to download...");
			br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf8"));
			int curTotal = 0, count = 0;
			double ratio = 0.0, older = 0.0;
			String line = br.readLine();
			while (line != null) {
				count += 1;
				curTotal += line.getBytes().length;
				content += line + new Properties(System.getProperties()).getProperty("line.separator");

				ratio = curTotal * 1.0 / totalLength;
				if (ratio - older >= 0.01) {
					System.out.printf("%3.0f %s\n", ratio * 100, "%");
					older = ratio;
				}
				line = br.readLine();
			}
			System.out.println("100 % done");
			response.setBody(content);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void main(String[] args) throws IOException {
		// HttpResponse response =
		// HttpRequest.requestPost("https://www.baidu.com/", "tn=sitehao123");
		// BufferedWriter bw = new BufferedWriter(new FileWriter(new
		// File("dataRepository/bdhome.txt")));
		// bw.write(response.getBody());
		// bw.close();

	}
}
