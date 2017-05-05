package com.uxuan.util.http;

import java.io.IOException;
import java.util.Map;

public interface Http {

	/**
     * http GET请求
     * 
     * @param url  http地址
     * @return request Content
     * @throws IOException
     */
    public String get(String url) throws IOException;
   
    /**
     * 发送POST请求
     * 
     * @param url 请求地址
     * @return request Content
     * @throws IOException
     */
    public String post(String url) throws IOException;
    
    /**
     * get http headers
     * 
     * @return
     */
    public Map<String, String> getHaders();

    /**
     * add http header
     * 
     * @param key
     * @param value
     */
    public void addHeader(String key, String value) ;

    /**
     * set http headers
     * 
     * @param headers
     */
    public void setHeaders(Map<String, String> headers);

    /**
     * get http request params
     * 
     * @return
     */
    public Map<String, String> getParams();
    
    /**
     * add request param
     * 
     * @param key
     * @param value
     */
    public void addParam(String key, String value);
    
    /**
     * set request params
     * 
     * @param params
     */
	public void setParams(Map<String, String> params);
	
	/**
	 * get cookie
	 * 
	 * @return
	 */
	public String getCookie();
	
	/**
	 * set cookie
	 * 
	 * @param cookie
	 */
	public void setCookie(String cookie) ;
	
	/**
	 * get http request body
	 * 
	 * @return
	 */
	public String getBody();

	/**
	 * set http reqest body
	 * 
	 * @param body
	 */
	public void setBody(String body) ;
	
	/**
	 * get http request timeOut on second
	 * 
	 * @return
	 */
    public int getReadTimeOut();

    /**
     * set http request timeOut
     * 
     * @param readTimeOut time out on second
     */
	public void setReadTimeOut(int readTimeOut);

	/**
	 * get http connect time out time on second
	 * 
	 * @return
	 */
	public int getConnectTimeOut();

	/**
	 * set http connect time out time on second
	 * 
	 * @param connectTimeOut
	 */
	public void setConnectTimeOut(int connectTimeOut);
	
	/**
	 * get http request char encoding
	 * 
	 * @return
	 */
	public String getCharEncoding();

	/**
	 * set http request char encoding
	 * 
	 * @param charEncoding
	 */
	public void setCharEncoding(String charEncoding) ;
	
	
}
