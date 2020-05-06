package com.rongwei.pims.job.common;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class EosSessionUtils {
    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */

    private static Logger logger = LoggerFactory.getLogger(EosSessionUtils.class);


    public static String getEosSession(String url, String serverIp, String serverPort
    , String userID, String passWord) {
        String serverHost = (new StringBuffer(serverIp).append(":").append(serverPort)).toString();
    	String serverAddress = (new StringBuffer("http://").append(serverHost).append("/default/")).toString();

    	String loginUrl  = (new StringBuffer(serverAddress).append("com.pdiwt.portal.frame.login.flow")).toString();//登陆地址;

        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setCookiePolicy( 
                CookiePolicy.BROWSER_COMPATIBILITY);
        PostMethod postMethod = new PostMethod(loginUrl);
       
        NameValuePair[] data = {
                new NameValuePair("_eosFlowAction", "login"), 
                new NameValuePair("acOperator/userid",userID),   //企业微信登陆时 userid 设置为微信UserId
                new NameValuePair("acOperator/password", passWord),
                new NameValuePair("_eosFlowKey", "9d88422b-a934-4314-ab49-ed1dd659b724.view0")}; 
        postMethod.setRequestHeader("Referer", loginUrl); 

        postMethod.setRequestBody(data);
        String tmpcookies="";
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

            for(Cookie c:cookies){ 
                tmpcookies=tmpcookies+c.toString()+";"; 
            }


        } catch (Exception e) { 
        	logger.error(e.getMessage()); 
        } finally { 
            postMethod.releaseConnection();
        } 
        return tmpcookies;
    }
}
