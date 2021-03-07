package com.center.omd.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 客户端请求工具类
 * @author lixueyan
 *
 */
public class HttpClientUtil {
	private static Logger log = LoggerFactory.getLogger(HttpClient.class);

	/**
	 * 发起https请求并获取结果
	 * 
	 * @param requestUrl
	 *            请求地址
	 * @param requestMethod
	 *            请求方式（GET、POST）
	 * @param outputStr
	 *            提交的数据
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr, String sign) {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		HttpURLConnection httpUrlConn = null;
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			URL url = new URL(requestUrl);
			httpUrlConn = (HttpURLConnection) url.openConnection();
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			httpUrlConn.setRequestProperty("X-AUTH-TOKEN", sign);
			httpUrlConn.setRequestProperty("Accept", "*/*");
			httpUrlConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);
			if ("GET".equalsIgnoreCase(requestMethod)) {
				httpUrlConn.connect();
			}

			// 当有数据需要提交时
			if (null != outputStr) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			log.debug(buffer.toString());
			//对象格式，
			jsonObject = JSONObject.parseObject(buffer.toString());
		} catch (ConnectException ce) {
			LogUtil.info("Weixin server connection timed out.");
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.info("https request error:{}" + e.getMessage());
		} finally {
			try {
				httpUrlConn.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				LogUtil.info("http close error:{}" + e.getMessage());
			}
		}
		return jsonObject;
	}
	
	
	/**
	 * 发起http请求并获取结果
	 * 
	 * @param requestUrl
	 *            请求地址
	 * @param requestMethod
	 *            请求方式（GET、POST）
	 * @param outputStr
	 *            提交的数据
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	public static String httpRequest(String requestUrl, String requestMethod, String outputStr) {
		String res = "";
		StringBuffer buffer = new StringBuffer();
		HttpURLConnection httpUrlConn = null;
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			URL url = new URL(requestUrl);
			httpUrlConn = (HttpURLConnection) url.openConnection();
			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			httpUrlConn.setRequestProperty("Accept", "text/plain");
			httpUrlConn.setRequestProperty("Content-Type", "application/json");
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);
			if ("GET".equalsIgnoreCase(requestMethod)) {
				httpUrlConn.connect();
			}

			// 当有数据需要提交时
			if (null != outputStr) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			res = buffer.toString();
			log.debug(res);
		} catch (ConnectException ce) {
			LogUtil.info("Weixin server connection timed out.");
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.info("https request error:{}" + e.getMessage());
		} finally {
			try {
				httpUrlConn.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				LogUtil.info("http close error:{}" + e.getMessage());
			}
		}
		return res;
	}


   /**
    * @Title doGet
    * @Description TODO(get请求工具，参数为map类型格式的)
    * @date 2021/2/7 16:13
    * @param [url, param]
    * @return java.lang.String
    */
	public static String doGet(String url, Map<String, String> param) {

		// 创建Httpclient对象
		CloseableHttpClient httpclient = HttpClients.createDefault();

		String resultString = "";
		CloseableHttpResponse response = null;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(url);
			if (param != null) {
				for (String key : param.keySet()) {
					builder.addParameter(key, param.get(key));
				}
			}
			URI uri = builder.build();
			// 创建http GET请求
			HttpGet httpGet = new HttpGet(uri);

			// 执行请求
			response = httpclient.execute(httpGet);
			// 判断返回状态是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null) {
					response.close();
				}
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultString;
	}
    /**
     * @Title doGet
     * @Description TODO(参数为空值类型的get请求)
     * @date 2021/2/7 16:14
     * @param [url]
     * @return java.lang.String
     */
	public static String doGet(String url) {
		return doGet(url, null);
	}
    /**
     * @Title doPost
     * @Description TODO(post请求，参数为map类型格式的)
     * @date 2021/2/7 16:15
     * @param [url, param]
     * @return java.lang.String
     */
	public static String doPost(String url, Map<String, String> param) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
 		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			//设置请求头的属性相关配置
			httpPost.setHeader("Accept", "text/plain");
			httpPost.setHeader("Content-Type","application/json");
			// 创建参数列表
			if (param != null) {
				List<NameValuePair> paramList = new ArrayList<>();
				for (String key : param.keySet()) {
					paramList.add(new BasicNameValuePair(key, param.get(key)));
				}
				// 模拟表单
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
				httpPost.setEntity(entity);
			}
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return resultString;
	}

   /**
    * @Title doPost
    * @Description TODO(post请求，参数为空)
    * @date 2021/2/7 16:16
    * @param [url]
    * @return java.lang.String
    */
	public static String doPost(String url) {
		return doPost(url, null);
	}

	/**
	 * @Title doPostJson
	 * @Description TODO(post请求，参数为json字符串)
	 * @date 2021/2/7 16:17
	 * @param [url, json]
	 * @return java.lang.String
	 */
	public static String doPostJson(String url, String json) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return resultString;
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @param url
	 * 发送请求的URL
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url,String param) {
		StringBuilder result = new StringBuilder();
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
			//解决字符集乱码问题
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			connection.setRequestProperty("contentType", "UTF-8");
		/*	connection.setRequestProperty("contentType", "text/plain;charset=UTF-8");*/

			//设置网络超时
			connection.setConnectTimeout(6000);
			connection.setReadTimeout(6000);
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			//遍历所有的响应头字段
			for (String key : map.keySet()) {
				log.debug(key + "--->" + map.get(key));
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
//			System.out.println("发送GET请求出现异常！" + e);
			result = new StringBuilder("{\"resCode\":\"1\",\"errCode\":\"1001\",\"resData\":\"\"}");
			e.printStackTrace();
			log.error("远程服务未开启", e);
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
		//返回的字符串
		return result.toString();
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
	public static String sendPost(String url, String param) {

		PrintWriter out = null;
		BufferedReader in = null;
		StringBuilder result = new StringBuilder();
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			//解决字符集乱码的问题
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("contentType", "UTF-8");
			//设置网络超时
			conn.setConnectTimeout(6000);
			conn.setReadTimeout(6000);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			//设置响应的字符集
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream(),"utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
//			System.out.println("发送 POST 请求出现异常！" + e);
			result = new StringBuilder("{\"resCode\":\"1\",\"errCode\":\"1001\",\"resData\":\"\"}");
			e.printStackTrace();
			log.error("远程服务未开启", e);
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return result.toString();
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 获取数组格式的json
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static JSONArray sendPostGetJsonArray(String url, String param)throws Exception {
		JSONArray JSONArray = null;
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			//设置网络超时
			conn.setConnectTimeout(6000);
			conn.setReadTimeout(6000);
			//解决字符集乱码的问题
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("contentType", "UTF-8");
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader( new InputStreamReader(conn.getInputStream(),"UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
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
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		JSONArray = JSONObject.parseArray(result);
		return JSONArray;
	}


	/**
	 * 向指定 URL 发送POST方法的请求
	 * 获取jsonObjec对象的
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static JSONObject sendPostGetJsonObject(String url, String param)throws Exception {
		JSONObject jsonObject = null;
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			//设置网络超时
			conn.setConnectTimeout(6000);
			conn.setReadTimeout(6000);
			//解决字符集乱码的问题
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestProperty("contentType", "UTF-8");
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader( new InputStreamReader(conn.getInputStream(),"UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
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
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		jsonObject = JSONObject.parseObject(result);
		return jsonObject;
	}








}
