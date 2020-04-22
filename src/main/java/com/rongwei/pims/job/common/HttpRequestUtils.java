package com.rongwei.pims.job.common;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HttpRequestUtils {
    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */

    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class);


    public static StringBuffer sendPost(String url, String serverIp, String serverPort
    , String userID, String passWord) {
        String serverHost = (new StringBuffer(serverIp).append(":").append(serverPort)).toString();
    	String serverAdress = (new StringBuffer("http://").append(serverHost).append("/default/")).toString();
    	
    	url = (new StringBuffer(serverAdress).append(url)).toString();

    	String loginUrl = loginUrl = (new StringBuffer(serverAdress).append("com.pdiwt.portal.frame.login.flow")).toString();//登陆地址;

        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setCookiePolicy( 
                CookiePolicy.BROWSER_COMPATIBILITY);
        PostMethod postMethod = new PostMethod(loginUrl);
        PostMethod postMethod2 = new PostMethod(url); 
       
        NameValuePair[] data = {
                new NameValuePair("_eosFlowAction", "login"), 
                new NameValuePair("acOperator/userid",userID),   //企业微信登陆时 userid 设置为微信UserId
                new NameValuePair("acOperator/password", passWord),
                new NameValuePair("_eosFlowKey", "9d88422b-a934-4314-ab49-ed1dd659b724.view0")}; 
        postMethod.setRequestHeader("Referer", loginUrl); 
        postMethod2.setRequestHeader("Host", serverHost); 
        postMethod2 
        .setRequestHeader( 
                "User-Agent", 
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; InfoPath.3)"); 
        postMethod2 
        .setRequestHeader("Accept", 
                "application/json, text/javascript, */*; q=0.01"); 
        postMethod.setRequestBody(data); 
        StringBuffer returnJson = null;
        try { 
            httpClient.executeMethod(postMethod); 
            StringBuffer response = new StringBuffer(); 
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    postMethod.getResponseBodyAsStream(), "utf-8"));//以gb2312编码方式打印从服务器端返回的请求 
            String line; 
            while ((line = reader.readLine()) != null) { 
                response.append(line).append( 
                        System.getProperty("line.separator")); 
            } 
            reader.close(); 
            //Header header = postMethod.getResponseHeader("Set-Cookie"); 
            Cookie[] cookies=httpClient.getState().getCookies();//取出登陆成功后，服务器返回的cookies信息，里面保存了服务器端给的“临时证”
            String tmpcookies=""; 
            for(Cookie c:cookies){ 
                tmpcookies=tmpcookies+c.toString()+";"; 
            }

            postMethod2.setRequestHeader("cookie",tmpcookies);//将“临时证明”放入下一次的发贴请求操作中 
            postMethod2.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");//因为发贴时候有中文，设置一下请求编码
            postMethod2.setRequestHeader("Referer", 
            		(new StringBuffer(serverAdress)).toString());
            postMethod2.setRequestHeader("Host", serverHost);
            postMethod2.setRequestHeader("Content-Type","application/json");

            postMethod2 
                    .setRequestHeader( 
                            "User-Agent", 
                            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; InfoPath.3)"); 
            postMethod2 
                    .setRequestHeader("Accept", 
                            "application/json, text/javascript, */*; q=0.01");//以上操作是模拟浏览器的操作，使用服务器混淆 
             
            httpClient.executeMethod(postMethod2); 
            StringBuffer response1 = new StringBuffer(); 
            BufferedReader reader1 = new BufferedReader(new InputStreamReader( 
                    postMethod2.getResponseBodyAsStream(), "utf-8")); 
            String line1; 
            while ((line1 = reader1.readLine()) != null) { 
                response1.append(line1).append( 
                        System.getProperty("line.separator")); 
            } 
            reader1.close(); 
            returnJson = response1;
        } catch (Exception e) { 
        	logger.error(e.getMessage()); 
        } finally { 
            postMethod.releaseConnection(); 
            postMethod2.releaseConnection(); 
        } 
        return returnJson;
    }
}
