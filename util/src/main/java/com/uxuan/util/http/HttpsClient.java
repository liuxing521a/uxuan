package com.uxuan.util.http;

import javax.net.ssl.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * https请求
 * 
 * @author liuzhen(liuxing521a@gmail.com)
 * @crateTime 2014年12月22日下午3:09:02
 */
public class HttpsClient extends AbstractHttp implements Https {

    public HttpsClient() {
    	super();
    }
    
    public HttpsClient(String charSet) {
    	super(charSet);
    }
    
	@Override
	protected String fetch(String url, String method) throws IOException   {
		 TrustManager[] tm = { new ItasX509TrustManager() }; 
         SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance("SSL", "SunJSSE"); 
			sslContext.init(null, tm, new java.security.SecureRandom());
		} catch (KeyManagementException|NoSuchProviderException|NoSuchAlgorithmException e) {
			throw new IOException(e);
		}
         // 从上述SSLContext对象中得到SSLSocketFactory对象 
         SSLSocketFactory ssf = sslContext.getSocketFactory(); 
         // 创建URL对象 
         URL myURL = new URL(url); 
         // 创建HttpsURLConnection对象，并设置其SSLSocketFactory对象 
         HttpsURLConnection conn = (HttpsURLConnection) myURL.openConnection(); 
         conn.setSSLSocketFactory(ssf); 
         conn.setRequestProperty("charset", charSet);
 		 conn.setRequestProperty("contentType", charSet);  
         // 取得该连接的输入流，以读取响应内容 
         

         conn.setConnectTimeout(getConnectTimeOut());
         conn.setReadTimeout(getReadTimeOut());
 
        // method
         conn.setRequestMethod(method);
 
         /*begin headers*/
         for (Entry<String, String> head : getHeaders().entrySet()) {
        	 conn.addRequestProperty(head.getKey(), head.getValue());
         }/*end*/
 
        conn.setDoOutput(true);
        conn.setDoInput(true);
        
        /*begin body*/
		byte[] bs = getBodys();
		if (Objects.nonNull(bs) && bs.length > 0) {
			try (OutputStream os = conn.getOutputStream()) {
				os.write(getBodys());
				os.flush();
			}
		}
        /*end*/
        
        boolean isGzip = isGzip(conn);
		InputStream input = null;
		try {
			if (conn.getResponseCode() >= 400) {
				input = conn.getErrorStream();
			} else {
				input = conn.getInputStream();
			}
			String charset = charEncoding(conn);
			return streamToString(input, charset, isGzip);
		} finally {
			if (input != null)
				input.close();
		}
	}
	

	private class ItasX509TrustManager implements X509TrustManager {

		ItasX509TrustManager()  {
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {

		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

}