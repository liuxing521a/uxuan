package com.uxuan.util.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * https请求
 * 
 * @author liuzhen(liuxing521a@gmail.com)
 * @crateTime 2014年12月22日下午3:09:02
 */
public class HttpClient extends AbstractHttp implements Http {

    public HttpClient() {
    	super();
    }
    
    public HttpClient(String charSet) {
    	super(charSet);
    }
    
	@Override
	protected String fetch(String url, String method) throws IOException {
		// 创建URL对象
		URL myURL = new URL(url);
		
		// 创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
		HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
		conn.setRequestProperty("charset", charSet);
		conn.setRequestProperty("contentType", charSet);  

		conn.setConnectTimeout(getConnectTimeOut());
		conn.setReadTimeout(getReadTimeOut());

		// method
		conn.setRequestMethod(method);

		/* begin headers */
		for (Entry<String, String> head : getHeaders().entrySet()) {
			conn.addRequestProperty(head.getKey(), head.getValue());
		}/* end */

		conn.setDoOutput(true);
		conn.setDoInput(true);

		/* begin body */
		byte[] bs = getBodys();
		if (Objects.nonNull(bs) && bs.length > 0) {
			try (OutputStream os = conn.getOutputStream()) {
				os.write(getBodys());
				os.flush();
			}
		}

		/* end */

		boolean isGzip = isGzip(conn);

		InputStream is = null;
		try {
			if (conn.getResponseCode() >= 400) {
				is = conn.getErrorStream();
			} else {
				is = conn.getInputStream();
			}
			String charset = charEncoding(conn);
			return streamToString(is, charset, isGzip);
		} finally {
			if (Objects.nonNull(is))
				is.close();
		}
	}
	

}