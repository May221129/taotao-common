package com.taotao.common.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.httpClient.HttpResult;


/**
 * 对外，访问外部提供的接口。
 * 问题：在单例对象中如何使用多例对象？见代码中的1、2、3、4、5步骤详解。当然，想要对象是多例的，前提是要在spring配置文件中配置该对象scope=prototype。
 */
@Service
public class ApiService implements BeanFactoryAware{//==>2.要获取多例对象的当前单例对象，实现BeanFactoryAware接口。

	private BeanFactory beanFactory;
	
	//==>1.CloseableHttpClient是多例的，不能采用注入的方式来获取：
//	@Autowired
//	private CloseableHttpClient httpClient;
	
	@Autowired(required = false)
	private RequestConfig requestConfig;
	
	/**
	 * 不带参数的get请求。 响应200：返回响应内容； 响应404、500：返回null。
	 */
	public String doGet(String url) throws ClientProtocolException, IOException {
		
		HttpGet httpGet = new HttpGet(url);

		// 伪装成浏览器：
		httpGet.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
		
		// 设置请求配置
		httpGet.setConfig(this.requestConfig);

		CloseableHttpResponse response = null;
		try {
			// 执行请求
			//==>5.具体代码中的使用：
			response = this.getCloseableHttpClient().execute(httpGet);
			// 判断返回状态是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}
		return null;
	}

	/**
	 * 带参数的get请求。响应200：返回响应内容； 响应404、500：返回null。
	 * @param url
	 * @param Map<String, String> params
	 */
	public String doGetHaveParams(String url, Map<String, String> params)
			throws ClientProtocolException, IOException, URISyntaxException {

		// URI uri = new URIBuilder("http://www.baidu.com/s").setParameter("wd", "java").build();
		URIBuilder uRIBuider = new URIBuilder(url);
		Set<Entry<String, String>> entrySet = params.entrySet();
		for (Entry<String, String> entry : entrySet) {
			uRIBuider.setParameter(entry.getKey(), entry.getValue());
		}
		URI uri = uRIBuider.build();
		
		//通过前面的代码，已经将参数设置到URI中了，下面就可以直接调用doGet()方法，而不用重新写重复的代码了。
		//或者也可以只写一个doGet(String url, Map<String, String> params)方法，判断params是否为空。参照doPost()方法。
		return this.doGet(uri.toString());
		
//		HttpGet httpGet = new HttpGet(uri);
//
//		// 伪装成浏览器：
//		httpGet.setHeader("User-Agent",
//				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
//
//		// 设置请求配置
//		httpGet.setConfig(requestConfig);
//
//		CloseableHttpResponse response = null;
//		try {
//			// 执行请求
//			response = httpClient.execute(httpGet);
//			// 判断返回状态是否为200
//			if (response.getStatusLine().getStatusCode() == 200) {
//				return EntityUtils.toString(response.getEntity(), "UTF-8");
//			}
//		} finally {
//			if (response != null) {
//				response.close();
//			}
//		}
//		return null;
	}

	/**
	 * 带有参数的post请求.
	 * 返回值是HttpResult类型，该类型是我自定义的。有两个属性：code状态码和body响应数据。 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public HttpResult doPostHaveParams(String url, Map<String, String> params) throws ClientProtocolException, IOException{

		// 创建http POST请求
		HttpPost httpPost = new HttpPost(url);

		// 因为B端的服务端也是我自己写的，其中并没有写需要验证请求是否来源于浏览器，所以这里可以不伪装成浏览器。
//		httpPost.setHeader("User-Agent",
//				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");

		// 判断是否有post参数:
		if(null != params){
			// parameters.add(new BasicNameValuePair("scope", "project"));
			// parameters.add(new BasicNameValuePair("q", "java"));
			// parameters.add(new BasicNameValuePair("fromerr", "Sfr6S8EK"));
			List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
			Set<Entry<String, String>> entrySet = params.entrySet();
			for (Entry<String, String> entry : entrySet) {
				parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			
			// 构造一个form表单式的实体
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters);
			
			// 将请求实体设置到httpPost对象中
			httpPost.setEntity(formEntity);
		}
		
		httpPost.setConfig(this.requestConfig);
		
		CloseableHttpResponse response = null;
		try {
			// 执行请求
			response = this.getCloseableHttpClient().execute(httpPost);
			return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), "UTF-8"));
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
	
	/**
	 * 不带参数的post请求.
	 * 返回值是HttpResult类型，该类型是我自定义的。有两个属性：code状态码和body响应数据。 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public HttpResult doPost(String url) throws ClientProtocolException, IOException{
		return this.doPostHaveParams(url, null);
	}
	
	/**
	 * 带json参数的post请求。响应200：返回响应内容； 响应404、500：返回null。
	 * @param url
	 * @param jsonData
	 */
	public HttpResult doPostHaveJsonParams(String url, String jsonData) throws ClientProtocolException, IOException{

		// 创建http POST请求
		HttpPost httpPost = new HttpPost(url);

		// 判断是否有post参数:
		if(StringUtils.isNotEmpty(jsonData)){
			// 构造一个json式的实体（注意，是和提交form表单不一样的。）
			StringEntity stringEntity = new StringEntity(jsonData, ContentType.APPLICATION_JSON);//第一参数是需要提交的json数据，第二个参数是提交的格式指定为json类型。
			
			// 将请求实体设置到httpPost对象中
			httpPost.setEntity(stringEntity);
		}
		
		httpPost.setConfig(this.requestConfig);
		
		CloseableHttpResponse response = null;
		try {
			// 执行请求
			response = this.getCloseableHttpClient().execute(httpPost);
			return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), "UTF-8"));
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
	
	//==>3.实现BeanFactoryAware接口的setBeanFactory(BeanFactory beanFactory)方法。
	//该方法是在spring容器初始化的时候（此时也初始化了自动注入的bean）被调用，并传入beanFactory
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	//==>4.通过自定义的getCloseableHttpClient()方法从beanFactory得到CloseableHttpClient对象，每次得到的对象都是新的。
	private CloseableHttpClient getCloseableHttpClient(){
		return this.beanFactory.getBean(CloseableHttpClient.class);
	}
	
}