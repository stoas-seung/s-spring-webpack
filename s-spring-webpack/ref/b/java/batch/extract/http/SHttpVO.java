package ift.batch.kicc.extract.http;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SHttpVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// common
	private int     connectionTimeout = 1000 * 3;
	private int     readTimeout       = 0;
	private boolean useCaches         = false;
	private boolean doInput           = true;
	private boolean doOutput          = true;
	private String  url               = "";
	private String  method            = "GET";
	
	// request
	private HashMap<String, String> requestHeader     = new HashMap<String, String>();
	private byte[]                  requestBody       = null;
	private HashMap<String, String> requestBodyFields = new HashMap<String, String>();
	private String                  requestEncoding   = "UTF-8";
	
	// response
	private int                       responseCode        = -1;
	private String                    responseEncoding    = "";
	private String                    responseContentType = "";
	private long                      responseLength      = -1l;
	private Map<String, List<String>> responseHeader      = new HashMap<String, List<String>>();
	private byte[]                    responseBody        = null;
	private String                    responseMessage     = "";
	
	// exception
	private String exceptionClass   = "";
	private String exceptionMessage = "";
	
	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public int getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	public boolean isUseCaches() {
		return useCaches;
	}
	public void setUseCaches(boolean useCaches) {
		this.useCaches = useCaches;
	}
	public boolean isDoInput() {
		return doInput;
	}
	public void setDoInput(boolean doInput) {
		this.doInput = doInput;
	}
	public boolean isDoOutput() {
		return doOutput;
	}
	public void setDoOutput(boolean doOutput) {
		this.doOutput = doOutput;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public HashMap<String, String> getRequestHeader() {
		return requestHeader;
	}
	public void setRequestHeader(HashMap<String, String> requestHeader) {
		this.requestHeader = requestHeader;
	}
	public void addRequestHeader(String key, String value) {
		this.requestHeader.put(key, value);
	}
	public byte[] getRequestBody() {
		return requestBody;
	}
	public void setRequestBody(byte[] requestBody) {
		this.requestBody = requestBody;
	}
	public HashMap<String, String> getRequestBodyFields() {
		return requestBodyFields;
	}
	public void setRequestBodyFields(HashMap<String, String> requestBodyFields) {
		this.requestBodyFields = requestBodyFields;
	}
	public void addRequestBodyFields(String key, String value) {
		this.requestBodyFields.put(key, value);
	}
	public void buildRequestBodyFields() throws UnsupportedEncodingException {
		if(this.requestBodyFields.size() > 0) {
			StringBuffer stringBuffer = null;
			stringBuffer = new StringBuffer();
			for(String key : this.requestBodyFields.keySet()) {
				stringBuffer.append("&");
				stringBuffer.append(key);
				stringBuffer.append("=");
				stringBuffer.append(this.requestBodyFields.get(key));
			}
			if("GET".equals(this.method.toUpperCase())) {
				if(this.url.indexOf("?") > -1) {
					if(this.url.indexOf("&") > -1) {
						this.url += stringBuffer.toString();
					} else {
						this.url += stringBuffer.toString().substring(1);
					}
				} else {
					this.url += "?" + stringBuffer.toString().substring(1);
				}
			} else if("POST".equals(this.method.toUpperCase())) {
				this.requestBody = stringBuffer.toString().substring(1).getBytes(requestEncoding);
			}
		}
	}
	public String getRequestEncoding() {
		return requestEncoding;
	}
	public void setRequestEncoding(String requestEncoding) {
		this.requestEncoding = requestEncoding;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseEncoding() {
		return responseEncoding;
	}
	public void setResponseEncoding(String responseEncoding) {
		this.responseEncoding = responseEncoding;
	}
	public String getResponseContentType() {
		return responseContentType;
	}
	public void setResponseContentType(String responseContentType) {
		this.responseContentType = responseContentType;
	}
	public long getResponseLength() {
		return responseLength;
	}
	public void setResponseLength(long responseLength) {
		this.responseLength = responseLength;
	}
	public Map<String, List<String>> getResponseHeader() {
		return responseHeader;
	}
	public void setResponseHeader(Map<String, List<String>> responseHeaderFields) {
		this.responseHeader = responseHeaderFields;
	}
	public byte[] getResponseBody() {
		return responseBody;
	}
	public void setResponseBody(byte[] responseBody) {
		this.responseBody = responseBody;
	}
	public String getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	public String getExceptionClass() {
		return exceptionClass;
	}
	public void setExceptionClass(String exceptionClass) {
		this.exceptionClass = exceptionClass;
	}
	public String getExceptionMessage() {
		return exceptionMessage;
	}
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
	
}
