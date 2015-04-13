package downloader;

public class Header {
	/**
	 * set all the header fields
	 */
	private String Connection = "";
	private String ContentType = "";
	private String ContentLength;
	private String Server = "";
	private String cookie = "";

	/**
	 * default constructor
	 */
	public Header() {
	}

	/**
	 * 
	 * @param Connection
	 * @param ContentType
	 * @param Server
	 * @param cookie
	 * @param ContentLength
	 * 
	 */
	public Header(String Connection, String ContentType, String Server,
			String cookie, String ContentLength) {
		this.Connection = Connection;
		this.ContentType = ContentType;
		this.ContentLength = ContentLength;
		this.Server = Server;
		this.cookie = cookie;
	}

	/**
	 * @return the connection
	 */
	public String getConnection() {
		return Connection;
	}

	/**
	 * @param connection
	 *            the connection to set
	 */
	public void setConnection(String connection) {
		Connection = connection;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return ContentType;
	}

	/**
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(String contentType) {
		ContentType = contentType;
	}

	/**
	 * @return the contentLength
	 */
	public String getContentLength() {
		return ContentLength;
	}

	/**
	 * @param contentLength
	 *            the contentLength to set
	 */
	public void setContentLength(String contentLength) {
		ContentLength = contentLength;
	}

	/**
	 * @return the server
	 */
	public String getServer() {
		return Server;
	}

	/**
	 * @param server
	 *            the server to set
	 */
	public void setServer(String server) {
		Server = server;
	}

	/**
	 * @return the cookie
	 */
	public String getCookie() {
		return cookie;
	}

	/**
	 * @param cookie
	 *            the cookie to set
	 */
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String [] getFields(){
		String[] rs = { "Connection", "Content-Type", "Content-Length",
				"Server", "Set-Cookie" };
		return rs;
	}

	@Override
	public String toString() {
		return getConnection() + getServer() + getContentType();
	}

}
