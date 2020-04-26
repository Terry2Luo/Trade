package com.rongwei.pims.job.common.workflow;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URLConnection;

import com.rongwei.pims.job.service.AcOperatorService;
import com.rongwei.pims.job.service.impl.AcOperatorServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.Action;
import java.util.Arrays;


@Component
public class WorkFlowManager {
    static Logger logger = LoggerFactory.getLogger(WorkFlowManager.class);

    //消息提示文本
    public static String Txt = "部门日常资金余额预警信息";
   //  参数
    private static String Access_Token_Param = "corpid=ID&corpsecret=SECRECT";
    
    //融为公司内部测试应用
  //  private static String Access_Token_Url ="https://qyapi.weixin.qq.com/cgi-bin/gettoken";
  //  private static String corpid = "wwbc199508a6bae683";
  //  private static String corpsecret = "1wL84Pz5EciYoXwLQdxsdwUjRZEi5a8bxWZyE-JUWjo";
  //  private static String agentId = "1000009";
   // public static String Message = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN"; 
   /* public static String indexUrl = "https://open.weixin.qq.com/connect/oauth2/authorize" +
    "?appid=wwbc199508a6bae683" +
    "&redirect_uri=1k71x82921.51mypc.cn%3a49529%2fPimsMobile%2fqywx.do" +
    "&response_type=code" +
    "&scope=snsapi_base" +
    "&state=STATE#wechat_redirect";*/
    
   
    //配置水规院生产经营正式应用
    //地址获取token
    private static String Access_Token_Url = "http://jjt.ccccltd.cn/cgi-bin/gettoken"; //?corpid=ID&corpsecret=SECRECT
    //企业id
    private static String corpid = "wl2c5e89d5c4";
    //应用密钥  
    private static String corpsecret = "9JjGdHrHykHm-6Qg_SPxFhsGtod11X3n6MzOZJFFr3Y";
    
    //应用id
    private static String agentId = "1001655";
    //  企业微信推送消息的地址
    public static String Message = "http://jjt.ccccltd.cn/cgi-bin/message/send?access_token=ACCESS_TOKEN";//?access_token=ACCESS_TOKEN
   
    //信科生产经营移动入口地址(就是点击链接的地址)
    public static String indexUrl = "http://jjt.ccccltd.cn/wwopen/sso/qrConnect?appid=wl2c5e89d5c4" +
            "&redirect_uri=http%3a%2f%2fscjym.pdiwt.com.cn%3a8080%2fPimsMobile%2fqywx.do" +
            "&response_type=code" +
            "&scope=snsapi_userinfo" +
            "&agentid=1001655" +
            "&state=web_login@gyoss9#wechat_redirect";

    @Autowired
    private AcOperatorService acOperatorService;






//    bw.ren 2019年11月20日13:54:38
//    参(ctrl)考(C)信科交建通消息推送
    public void sendMessage(String userids,String deptWarnMes){
        //消息模板
        String Mess = Txt;
        logger.info("推送消息模板："+Mess);
        logger.info("用户userids："+userids);
        System.out.println("推送消息模板："+Mess);
        System.out.println("用户userids："+userids);

        //流程节点中的用户id
        userids = userids.replace("|", ",");
        String [] users = userids.split(",");
        logger.info("userids="+userids);
        logger.info("users="+Arrays.toString(users));
        String touser = "";
        String Tousers = "";
       // Tousers = userids;
        //userid查询empid
        if(users.length > 0){
            for(int i = 0; i < users.length; i ++){
                logger.info("users[i]="+users[i]);
                String ID = acOperatorService.getEMPIdByUserId(users[i]);
                logger.info("id="+ID);
                if(ID != null){
                    if(i != users.length -1){
                        touser = touser + ID + "|";
                    }else{
                        touser = touser + ID;
                    }
                }
            }
            logger.info("touser="+touser);
            if(touser.endsWith("|")){
                touser = touser.substring(0, touser.length()-1);
            }
            Tousers = touser;
            logger.info("Touser="+Tousers);
        }
        logger.info("交建通用户Tousers："+Tousers);
        System.out.println("交建通用户Tousers："+Tousers);


        String accessToken = sendGet(Access_Token_Url,Access_Token_Param.replace("ID", corpid).replace("SECRECT", corpsecret));
        logger.info("交建通========获取accessToken对象===  " + accessToken);
        System.out.println("交建通========获取accessToken对象===  " + accessToken);

        JSONObject obj = JSONObject.parseObject(accessToken);
        String token = null;

        logger.info("交建通========获取accessToken对象===  " + obj);
        System.out.println("交建通========获取accessToken对象===  " + obj);
        if(obj != null ) {
            try {
                token = obj.getString("access_token");
                logger.info("交建通========获取accessToken " + token);
                System.out.println("交建通========获取accessToken " + token);
            }catch (Exception e) {
                logger.info("交建通========获取accessToken失败 ");
                System.out.println("交建通========获取accessToken失败 ");
            }
        }

        String contenttext = "{\"content\" : " + deptWarnMes +"}";
        if(token != null && (Tousers != null || !"".equals(Tousers))){
            String sendMessageUrl = Message.replace("ACCESS_TOKEN", token);
            String mes = "{\"touser\" : \""+ Tousers
                    +"\",\"msgtype\" : \"text\"," +
                    "\"agentid\" : " +agentId+"," +
                    "\"text\":"+contenttext+","
                    +"\"enable_duplicate_check\": 0,"
                    +"\"duplicate_check_interval\": 1800}";
            System.out.println(mes);
            logger.info("交建通消息发送体："+mes);
            String json = sendPost(sendMessageUrl,mes);
            if(json != null ) {
                System.out.println(json);
                logger.info("json="+json);
            }
        }
    }
    
    
    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"utf-8"));//防止乱码
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public String sendPost(String url, String param) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性

            conn.setRequestProperty("accept", "application/json");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
            //conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8"); 
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            //out = new PrintWriter(conn.getOutputStream(),"UTF-8");
            out = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");

            // 发送请求参数
            out.write(param);
            System.out.println(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

}
