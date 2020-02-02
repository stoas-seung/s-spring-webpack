package ift.batch.kicc.extract.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;

public class SHttpU {

	public void request(SHttpVO sHttpVO) {
		
		if(sHttpVO.getUrl().startsWith("https")) {
			https(sHttpVO);
		} else if(sHttpVO.getUrl().startsWith("http")) {
			http(sHttpVO);
		}
		
	}
	
	private void http(SHttpVO sHttpVO) {
		
		HttpURLConnection httpURLConnection = null;
		OutputStream      outputStream      = null;
		InputStreamReader inputStreamReader = null;
		
		try {
			
			sHttpVO.buildRequestBodyFields();
			URL url = new URL(sHttpVO.getUrl());
			
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(sHttpVO.getConnectionTimeout());
			httpURLConnection.setConnectTimeout(sHttpVO.getReadTimeout());
			httpURLConnection.setUseCaches(sHttpVO.isUseCaches());
			httpURLConnection.setDoInput(sHttpVO.isDoInput());
			httpURLConnection.setDoOutput(sHttpVO.isDoOutput());
			httpURLConnection.setRequestMethod(sHttpVO.getMethod());
			
			if(sHttpVO.getRequestHeader() != null) {
				for(String key : sHttpVO.getRequestHeader().keySet()) {
					httpURLConnection.setRequestProperty(key, sHttpVO.getRequestHeader().get(key));
				}
			}
			
			if(sHttpVO.getRequestBody() != null) {
				outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
				outputStream.write(sHttpVO.getRequestBody());
				outputStream.flush();
			}
			
			sHttpVO.setResponseCode(httpURLConnection.getResponseCode());
			sHttpVO.setResponseContentType(httpURLConnection.getContentType());
			sHttpVO.setResponseHeader(httpURLConnection.getHeaderFields());
			inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
			sHttpVO.setResponseBody(IOUtils.toByteArray(inputStreamReader, sHttpVO.getResponseEncoding()));
			sHttpVO.setResponseMessage(httpURLConnection.getResponseMessage());
			
		} catch (MalformedURLException e) {
			sHttpVO.setExceptionClass(e.getClass().getName());
			sHttpVO.setExceptionClass("" + e);
		} catch (IOException e) {
			sHttpVO.setExceptionClass(e.getClass().getName());
			sHttpVO.setExceptionClass("" + e);
		} finally {
			try {
				if(inputStreamReader != null) {
					inputStreamReader.close();
				}
				if(outputStream != null) {
					outputStream.close();
				}
				if(httpURLConnection != null) {
					httpURLConnection.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void https(SHttpVO sHttpVO) {
		
		HttpsURLConnection httpsURLConnection = null;
		OutputStream       outputStream       = null;
		InputStreamReader  inputStreamReader  = null;
		
		try {
			
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(
					null
					, new TrustManager[] {
							new X509TrustManager() {
								@Override
								public java.security.cert.X509Certificate[] getAcceptedIssuers() {
									return null;
								}
								@Override
								public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
								}
								@Override
								public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
								}
							}
					}
					, new SecureRandom()
					);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			
			sHttpVO.buildRequestBodyFields();
			URL url = new URL(sHttpVO.getUrl());
			
			httpsURLConnection = (HttpsURLConnection) url.openConnection();
			httpsURLConnection.setConnectTimeout(sHttpVO.getConnectionTimeout());
			httpsURLConnection.setConnectTimeout(sHttpVO.getReadTimeout());
			httpsURLConnection.setUseCaches(sHttpVO.isUseCaches());
			httpsURLConnection.setDoInput(sHttpVO.isDoInput());
			httpsURLConnection.setDoOutput(sHttpVO.isDoOutput());
			httpsURLConnection.setRequestMethod(sHttpVO.getMethod());
			
			if(sHttpVO.getRequestHeader() != null) {
				for(String key : sHttpVO.getRequestHeader().keySet()) {
					httpsURLConnection.setRequestProperty(key, sHttpVO.getRequestHeader().get(key));
				}
			}
			
			if(sHttpVO.getRequestBody() != null) {
				outputStream = new DataOutputStream(httpsURLConnection.getOutputStream());
				outputStream.write(sHttpVO.getRequestBody());
				outputStream.flush();
			}
			
			sHttpVO.setResponseCode(httpsURLConnection.getResponseCode());
			sHttpVO.setResponseContentType(httpsURLConnection.getContentType());
			sHttpVO.setResponseHeader(httpsURLConnection.getHeaderFields());
			inputStreamReader = new InputStreamReader(httpsURLConnection.getInputStream());
			sHttpVO.setResponseBody(IOUtils.toByteArray(inputStreamReader, sHttpVO.getResponseEncoding()));
			sHttpVO.setResponseMessage(httpsURLConnection.getResponseMessage());
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			sHttpVO.setExceptionClass(e.getClass().getName());
			sHttpVO.setExceptionClass("" + e);
		} catch (KeyManagementException e) {
			e.printStackTrace();
			sHttpVO.setExceptionClass(e.getClass().getName());
			sHttpVO.setExceptionClass("" + e);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			sHttpVO.setExceptionClass(e.getClass().getName());
			sHttpVO.setExceptionClass("" + e);
		} catch (IOException e) {
			e.printStackTrace();
			sHttpVO.setExceptionClass(e.getClass().getName());
			sHttpVO.setExceptionClass("" + e);
		} finally {
			try {
				if(inputStreamReader != null) {
					inputStreamReader.close();
				}
				if(outputStream != null) {
					outputStream.close();
				}
				if(httpsURLConnection != null) {
					httpsURLConnection.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
