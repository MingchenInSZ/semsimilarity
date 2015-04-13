package downloader;

/**
 * 
 * @author mingchen
 * 
 *         HttpResponse Class
 */
public class HttpResponse {
	// fields
	private String body = "";
	private String refUrl = "";
	private Header header = null;

	// constructors
	public HttpResponse() {
	}

	public HttpResponse(String url) {
		refUrl = url;
		header = new Header();
	}

	/**
	 * @return the refUrl
	 */
	public String getRefUrl() {
		return refUrl;
	}

	/**
	 * @param refUrl
	 *            the refUrl to set
	 */
	public void setRefUrl(String refUrl) {
		this.refUrl = refUrl;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @return the headere
	 */
	public Header getHeader() {
		return header;
	}

	/**
	 * 
	 * @param header
	 */
	public void setHeader(Header header) {
		this.header = header;
	}

	/**
	 * @param body
	 */
	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String toString() {
		String rs = "[ URL ref:" + getRefUrl() + "]";
		return rs;
	}
}
