package com.center.omd.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lanshen on 2019/12/2.
 * 调用第三方接口工具
 */
public  class httpUtils {

    private static Logger log = LoggerFactory.getLogger(HttpClient.class);

    /*Java通过HTTP的短连接的方式发送JSON报文和接收JSON报文==纯参数===POST*/
    public static String JsonHttpRequestPost(String requestUrl, String requestMethod, String outputStr) throws IOException {
        StringBuffer rstsb = new StringBuffer();
        try {
            URL url = new URL(requestUrl);
            //打开和url之间的连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置发送数据
            conn.setDoOutput(true);
            //设置接受数据
            conn.setDoInput(true);
            //设置请求的方法类型
            conn.setRequestMethod(requestMethod);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            //设置请求的内容类型
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            //往服务器端写内容 也就是发起http请求需要带的参数
            if (outputStr != null) {
                //发送数据,使用输出流
                OutputStream os = conn.getOutputStream();
                os.write(outputStr.getBytes("utf-8"));
                os.close();
            }

            //读取服务器端返回的内容,获取URLConnection对象对应的输入流
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            //构造一个字符流缓存
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                rstsb.append(line);
                rstsb.append("\n");
            }
            br.close();

            //断开连接，最好写上，disconnect是在底层tcp socket链接空闲时才切断。如果正在被其他线程使用就不切断。
            //固定多线程的话，如果不disconnect，链接会增多，直到收发不出信息。写上disconnect后正常一些。
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("请求服务器出现异常");
        }

        //把返回的值转成json
        JSONObject json = (JSONObject) JSON.parse(rstsb.toString());
        log.info("调用第三方接口后，返回的值为：" + json);

        return rstsb.toString();
    }

    /*Java通过HTTP的短连接的方式发送FORM报文和接收JSON报文==参数+图片===POST*/
    @SuppressWarnings("rawtypes")
    public static String FormHttpRequestPost(String requestUrl, String requestMethod, Map<String, Object> parmMap, Map<String, String> fileMap, String contentType) throws Exception {
        String rstsb = "";
        HttpURLConnection conn = null;
        // boundary就是request头和上传文件内容的分隔符
        String BOUNDARY = "---------------------------123821742118716";
        try {
            URL url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            OutputStream out = new DataOutputStream(conn.getOutputStream());

            // paramMap
            if (parmMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator iter = parmMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    Object inputValue = entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    strBuf.append("\r\n").append("--").append(BOUNDARY)
                            .append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes());
            }

            // fileMap
            if (fileMap != null) {
                Iterator iter = fileMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    File file = new File(inputValue);
                    String filename = file.getName();

                    //没有传入文件类型，同时根据文件获取不到类型，默认采用application/octet-stream
                    contentType = new MimetypesFileTypeMap().getContentType(file);
                    //contentType非空采用filename匹配默认的图片类型 图片文件只支持jpg/jpeg/png 格式。
                    if (!"".equals(contentType)) {
                        if (filename.endsWith(".png")) {
                            contentType = "image/png";
                        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".jpe")) {
                            contentType = "image/jpeg";
                        }
                    }
                    if (contentType == null || "".equals(contentType)) {
                        contentType = "application/octet-stream";
                    }
                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("\r\n").append("--").append(BOUNDARY)
                            .append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"; filename=\"" + filename
                            + "\"\r\n");
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                    out.write(strBuf.toString().getBytes());
                    DataInputStream in = new DataInputStream(
                            new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();
                }
            }
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            // 读取返回数据
            StringBuffer strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            rstsb = strBuf.toString();
            reader.close();
            reader = null;
        } catch (Exception e) {
            log.info("发送POST请求出错。" + requestUrl);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }

        //把返回的值转成json
        JSONObject json = (JSONObject) JSON.parse(rstsb.toString());
        log.info("调用第三方接口后，返回的值为：" + json);

        return rstsb;
    }

    /*Java通过HTTP方式发送JSON报文和接收JSON报文===GET*/
    public static String JsonHttpRequestGet(String requestUrl, String requestMethod) throws IOException {
        StringBuffer rstsb = new StringBuffer();
        try {
            URL url = new URL(requestUrl);
            //打开和url之间的连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置发送数据
            conn.setDoOutput(true);
            //设置接受数据
            conn.setDoInput(true);
            //设置请求的方法类型
            conn.setRequestMethod(requestMethod);
            //设置请求参数的字节长度
            conn.setRequestProperty("Content-Length", String.valueOf(requestUrl.getBytes().length));
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            //设置请求的内容类型
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.connect();

            //往服务器端写内容 也就是发起http请求需要带的参数

                /*if (outputStr != null) {
                    //发送数据,使用输出流
                    OutputStream os = conn.getOutputStream();
                    os.write(outputStr.getBytes("utf-8"));
                    os.close();
                }*/

            //读取服务器端返回的内容,获取URLConnection对象对应的输入流
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            //构造一个字符流缓存
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                rstsb.append(line);
                rstsb.append("\n");
            }
            br.close();

            //断开连接，最好写上，disconnect是在底层tcp socket链接空闲时才切断。如果正在被其他线程使用就不切断。
            //固定多线程的话，如果不disconnect，链接会增多，直到收发不出信息。写上disconnect后正常一些。
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("请求服务器出现异常");
        }

        //把返回的值转成json
        JSONObject json = (JSONObject) JSON.parse(rstsb.toString());
        log.info("调用第三方接口后，返回的值为：" + json);

        return rstsb.toString();
    }

    /*Java通过HTTPClient方式发送JSON报文和接收JSON报文*/
    public static String JsonHttpRequestClient(String requestUrl,Map<String,String> map) throws Exception{
        /*获取连接客户端工具*/
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String entityStr = null;
        byte[] message = null;

        try {
            /*创建POST请求对象*/
            HttpPost httpPost = new HttpPost(requestUrl);

            /*添加请求头信息*/
            httpPost.setHeader(new BasicHeader("Content-Type","application/x-www-form-urlencoded"));
            //httpPost.setHeader(new BasicHeader("User-agent","userAgent"));
            httpPost.setHeader(new BasicHeader("user-agent","Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)"));
            httpPost.setHeader(new BasicHeader("Referer",""));

            /*添加请求参数*/
            List<NameValuePair> paramList = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
                paramList.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
            }

                /*BasicNameValuePair param1 = new BasicNameValuePair("ECARDEVID", "101004");
                BasicNameValuePair param2 = new BasicNameValuePair("SCHID", "10486");
                BasicNameValuePair param3 = new BasicNameValuePair("ccbParam", MyConstant.CCBPARAM);
                paramList.add(param1);
                paramList.add(param2);
                paramList.add(param3);*/

            /*使用URL实体转换工具*/
            System.out.println("请求的参数为："+paramList);
            httpPost.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));

            /*执行请求*/
            response = client.execute(httpPost);
            if(response.getStatusLine().getStatusCode() != 200){
                System.out.println("返回失败！");
            }else{
                /*获得响应的实体对象*/
                HttpEntity entity = response.getEntity();
                /*使用Apache提供的工具类进行转换成字符串*/
                entityStr = EntityUtils.toString(entity, "UTF-8");
            }
        }catch (Exception e){
            message = "失败".getBytes();
        }

        return entityStr;

    }
}
