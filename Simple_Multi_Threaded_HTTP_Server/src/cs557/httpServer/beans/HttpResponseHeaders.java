package cs557.httpServer.beans;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cs557.httpServer.constants.Constants;
import cs557.httpServer.constants.NetworkProtocol;
import cs557.httpServer.constants.ResponseHeaders;
import cs557.httpServer.constants.ResponseStatus;
import cs557.httpServer.util.ResourceHelper;

/**
 * HttpResponse object generated by the server and sent over the socket to
 * client.
 * 
 * @author anandkulkarni
 *
 */
public class HttpResponseHeaders {
	private ResponseStatus status = null;
	private Long contentLength = null;
	private File resource = null;
	private NetworkProtocol protocol = null;
	private String contentType = null;
	private Date date;
	/**
	 * Server name is read from the config.properties from resources directory.
	 */
	private static final String serverName = Constants.SERVER_NAME;

	/**
	 * this field stores last modified date of the resource.
	 */
	private Date resourceLMDate;

	/**
	 * Response status format.
	 */
	private static final String STATUS_FORMAT = "%s %s %s\r\n";

	/**
	 * Response header format. applies to each header entity.
	 */
	private static final String HEADER_FORMAT = "%s: %s";

	private static final String HEADER_END_FORMAT = "\r\n";
	/**
	 * A pattern to denote end of header section.
	 */
	private static final String END_OF_HEADER = " \r\n\r\n";

	/**
	 * This method converts the date object into RFC7231 format.
	 * 
	 * @param date
	 *            date object to be converted.
	 * @return String representation of date in RFC7231 format.
	 */
	private String getRFC7231Formatted1Date(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(date);
	}

	/**
	 * This method builds the Http response object using the required status and
	 * header information.
	 * 
	 * @param statusIn
	 *            status of the HttpResponse based on whether the server could
	 *            find the requested resource or not.
	 * @return HttpResponse object
	 */
	public synchronized HttpResponseHeaders build(ResponseStatus statusIn) {
		status = statusIn;
		date = new Date();
		switch (statusIn) {
		case OK:
			contentLength = resource.length();
			contentType = ResourceHelper.getMimeType(resource.getName());
			resourceLMDate = new Date(resource.lastModified());
			break;
		case NOT_FOUND:
			contentLength = (long) Constants.ERR_RESPONSE.length();
			contentType = "text/html";
			resourceLMDate = new Date();
			break;
		}
		return this;
	}

	/**
	 * This method generates the String representation of a HttpRespose object.
	 * 
	 * @return
	 */
	public String getMessage() {
		switch (status) {
		case OK:
			return String.format(STATUS_FORMAT, protocol.getValue(), status.getCode(), status.getValue())
					+ appendIfNotNull(HEADER_FORMAT + HEADER_END_FORMAT, ResponseHeaders.DATE,
							getRFC7231Formatted1Date(date))
					+ appendIfNotNull(HEADER_FORMAT + HEADER_END_FORMAT, ResponseHeaders.SERVER, serverName)
					+ appendIfNotNull(HEADER_FORMAT + HEADER_END_FORMAT, ResponseHeaders.CONTENTLENGTH, contentLength)
					+ appendIfNotNull(HEADER_FORMAT + HEADER_END_FORMAT, ResponseHeaders.CONTENTTYPE, contentType)
					+ appendIfNotNull(HEADER_FORMAT, ResponseHeaders.RESOURCELMDATE,
							getRFC7231Formatted1Date(resourceLMDate))
					+ END_OF_HEADER;
		case NOT_FOUND:
			return String.format(STATUS_FORMAT, protocol.getValue(), status.getCode(), status.getValue())
					+ appendIfNotNull(HEADER_FORMAT + HEADER_END_FORMAT, ResponseHeaders.DATE,
							getRFC7231Formatted1Date(date))
					+ appendIfNotNull(HEADER_FORMAT, ResponseHeaders.SERVER, serverName) + END_OF_HEADER;
		}
		return null;
	}

	/**
	 * This method returns empty string if the header property has null value.
	 * 
	 * @param format
	 * @param label
	 * @param value
	 * @return
	 */
	private String appendIfNotNull(String format, ResponseHeaders label, Object value) {
		return value == null ? "" : String.format(format, label, value);
	}

	/**
	 * Protocol to be included in the HttpResponse object.
	 * 
	 * @param protocolIn
	 * @return
	 */
	public HttpResponseHeaders withProtocol(NetworkProtocol protocolIn) {
		protocol = protocolIn;
		return this;
	}

	/**
	 * Resource to be used while gathering information to be included in the
	 * response.
	 * 
	 * @param resourceIn
	 * @return
	 */
	public HttpResponseHeaders withResource(File resourceIn) {
		resource = resourceIn;
		return this;
	}

	public ResponseStatus getStatusCode() {
		return status;
	}

	public void setStatusCode(ResponseStatus statusCode) {
		this.status = statusCode;
	}

	public Long getContentLength() {
		return contentLength;
	}

	public void setContentLength(Long contentLength) {
		this.contentLength = contentLength;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getResourceLMDate() {
		return resourceLMDate;
	}

	public void setResourceLMDate(Date resourceLMDate) {
		this.resourceLMDate = resourceLMDate;
	}

	public static String getServername() {
		return serverName;
	}

	@Override
	public String toString() {
		return "HttpResponseHeaders [status=" + status + ", contentLength=" + contentLength + ", resource=" + resource
				+ ", protocol=" + protocol + ", contentType=" + contentType + ", date=" + date + ", resourceLMDate="
				+ resourceLMDate + "]";
	}
}