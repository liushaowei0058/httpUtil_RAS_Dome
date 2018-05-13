package com.demo.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;

/**
 * Created by liuh on 2018/3/18.
 */
public class HttpClientUtils {
	private static Logger logger = Logger.getLogger(HttpClientUtils.class.getName());

	private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
	private static CloseableHttpClient httpClient = null;
	private static final int DEFAULT_MAX_TOTAL_CONNECTION = 1024;
	private static final int DEFAULT_MAX_PER_ROUTE = 50;
	private static final String DEFAULT_ENCODING = "UTF-8"; // 默认的查询参数及返回结果的字符串编码
	private static final int DEFAULT_CONNECTION_TIME_OUT = 6000; // 默认连接超时时间,
																	// 60秒
	private static final int DEFAULT_READ_TIME_OUT = 6000; // 默认响应超时时间, 60秒

	static {
		logger.info("初始化開始:");
		poolingHttpClientConnectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTION);
		poolingHttpClientConnectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
		httpClient = HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager).build();
		logger.info("初始化結束:");
	}

	private HttpClientUtils() {
	}

	/**
	 * @param url
	 * @param params
	 * @return
	 */
	public static String invokeGet(String url, Map<String, String> params) {
		return invokeGet(url, params, DEFAULT_ENCODING, DEFAULT_CONNECTION_TIME_OUT, DEFAULT_READ_TIME_OUT);
	}

	/**
	 * @param url
	 * @param params
	 * @return
	 */
	public static String szylPost(String url, Map<String, String> params) {
		return szylPost(url, params, DEFAULT_ENCODING, DEFAULT_CONNECTION_TIME_OUT, DEFAULT_READ_TIME_OUT);
	}

	/**
	 * @param url
	 * @param params
	 * @return
	 */
	public static String ysPost(String url, String params) {
		return ysPost(url, params, DEFAULT_ENCODING, DEFAULT_CONNECTION_TIME_OUT, DEFAULT_READ_TIME_OUT);
	}

	/**
	 * @param url
	 * @param params
	 * @param encode
	 * @param connectTimeout
	 * @param readTimeout
	 * @return
	 */
	public static String szylPost(String url, Map<String, String> params, String encode, int connectTimeout,
			int readTimeout) {
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeout)
				.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();
		String responseString;
		// 封装请求参数
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		if (params != null) {
			Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				BasicNameValuePair nameValue = new BasicNameValuePair(entry.getKey(), entry.getValue());
				formParams.add(nameValue);
			}
		}
		HttpPost httpGet = new HttpPost(url);
		UrlEncodedFormEntity ueEntity = new UrlEncodedFormEntity(formParams, Charset.forName("UTF-8"));
		httpGet.setConfig(requestConfig);
		httpGet.setEntity(ueEntity);
		responseString = doSzylRequest(httpGet, encode);
		return responseString;
	}
	
	public static String post(String url, Map<String, Object> params) {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				if (params.get(key) != null) {
					parameters.add(new BasicNameValuePair(key, params.get(key).toString()));
				}
			}
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameters, "utf-8");
//			uefEntity.setContentType(CONTENT_TYPE_TEXT_JSON);
			httpPost.setEntity(uefEntity);
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					return EntityUtils.toString(entity, "utf-8");
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * 发送 post请求访问本地应用并根据传递参数不同返回不同结果
	 */
	public static String ysPost(String url, String jsonReq, String encode, int connectTimeout, int readTimeout) {
		String responseString = null;
		HttpPost httpGet = null;
		CloseableHttpResponse response = null;
		try {
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(readTimeout)
					.setConnectionRequestTimeout(connectTimeout).setSocketTimeout(connectTimeout).build();
			httpGet = new HttpPost(url);
			httpGet.setConfig(requestConfig);
			StringEntity entity = new StringEntity(jsonReq, "utf-8");// 解决中文乱码问题
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			httpGet.setEntity(entity);
			System.out.println("开始请求："+new Date(System.currentTimeMillis()));
			logger.error("开始");
			response = httpClient.execute(httpGet);

			try {
				HttpEntity respEntity = response.getEntity();
				try {
					if (respEntity != null) {
						responseString = EntityUtils.toString(respEntity, encode);
					}
				} finally {
					if (respEntity != null) {
						respEntity.getContent().close();
					}
				}
			} catch (Exception e) {
				logger.error(String.format("[HttpClientUtils.doRequest] get response error, url:%s", httpGet.getURI()),
						e);
				responseString = "";
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (SocketTimeoutException e) {
			logger.error(String.format("[HttpClientUtils.doRequest] invoke get timout error, url:%s", httpGet.getURI()),
					e);
			responseString = "";
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			httpGet.releaseConnection();
		}

		return responseString;
	}

	/**
	 * @param httpRequestBase
	 * @param encode
	 * @return
	 */
	private static String doSzylRequest(HttpRequestBase httpRequestBase, String encode) {

		String responseString = null;
		try {
			long start = System.currentTimeMillis();
			CloseableHttpResponse response = httpClient.execute(httpRequestBase);
			logger.info("HttpClientUtils Begin Invoke: " + httpRequestBase.getURI() + ", cost time "
					+ (System.currentTimeMillis() - start) + " ms");
			try {
				HttpEntity entity = response.getEntity();
				try {
					if (entity != null) {
						responseString = EntityUtils.toString(entity, encode);
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} catch (Exception e) {
				logger.error(String.format("[HttpClientUtils.doRequest] get response error, url:%s",
						httpRequestBase.getURI()), e);
				responseString = "";
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (SocketTimeoutException e) {
			logger.error(String.format("[HttpClientUtils.doRequest] invoke get timout error, url:%s",
					httpRequestBase.getURI()), e);
			responseString = "";
		} catch (Exception e) {
			logger.error(
					String.format("[HttpClientUtils.doRequest] invoke get error, url:%s", httpRequestBase.getURI()), e);
			responseString = "";
		} finally {
			httpRequestBase.releaseConnection();
		}
		logger.info("HttpClientUtils response : " + responseString);

		return responseString;
	}

	/**
	 * @param url
	 * @param params
	 * @param encode
	 * @param connectTimeout
	 * @param readTimeout
	 * @return
	 */
	public static String invokeGet(String url, Map<String, String> params, String encode, int connectTimeout,
			int readTimeout) {
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeout)
				.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();
		String responseString;
		String requestUrl;
		try {
			requestUrl = buildRequestUrl(url, params);
		} catch (UnsupportedEncodingException e) {
			logger.error("encode http get params error, params is " + params, e);
			return "";
		}
		HttpPost httpGet = new HttpPost(requestUrl);
		httpGet.setConfig(requestConfig);
		responseString = doRequest(httpGet, encode);
		return responseString;
	}

	/**
	 * @param requestUrl
	 * @param params
	 * @return
	 */
	public static String invokePost(String requestUrl, Map<String, Object> params) {
		return invokePost(requestUrl, params, null, null, DEFAULT_ENCODING, DEFAULT_CONNECTION_TIME_OUT,
				DEFAULT_READ_TIME_OUT);
	}

	/**
	 * @param requestUrl
	 * @param requestHeader
	 * @param requestBody
	 * @return
	 */
	public static String invokePost(String requestUrl, Map<String, String> requestHeader, String requestBody) {
		return invokePost(requestUrl, null, requestHeader, requestBody, DEFAULT_ENCODING, DEFAULT_CONNECTION_TIME_OUT,
				DEFAULT_READ_TIME_OUT);
	}
	//
	// /**
	// * @param requestUrl
	// * @param requestBody
	// * @return
	// */
	// public static String invokePostByJsonEntity(String requestUrl, String
	// requestBody) {
	// return invokePost(requestUrl, null, JSON_HEADER, requestBody,
	// DEFAULT_ENCODING, DEFAULT_CONNECTION_TIME_OUT, DEFAULT_READ_TIME_OUT);
	// }

	/**
	 * @param requestUrl
	 * @param params
	 * @param requestHeader
	 * @param requestBody
	 * @param encode
	 * @param connectTimeout
	 * @param readTimeout
	 * @return
	 */
	public static String invokePost(String requestUrl, Map<String, Object> params, Map<String, String> requestHeader,
			String requestBody, String encode, int connectTimeout, int readTimeout) {
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeout)
				.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();
		String responseString;
		HttpPost httpPost = new HttpPost(requestUrl);
		httpPost.setConfig(requestConfig);

		if (MapUtils.isNotEmpty(requestHeader)) {
			for (Map.Entry<String, String> entry : requestHeader.entrySet()) {
				httpPost.addHeader(entry.getKey(), entry.getValue());
			}
		}

		buildPostParams(httpPost, params, requestBody, encode);
		responseString = doRequest(httpPost, encode);
		return responseString;
	}

	/**
	 * @param httpPost
	 * @param params
	 * @param requestBody
	 * @param encode
	 */
	private static void buildPostParams(HttpPost httpPost, Map<String, Object> params, String requestBody,
			String encode) {
		try {
			if (MapUtils.isNotEmpty(params)) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				for (Map.Entry<String, Object> entry : params.entrySet()) {
					String value = null;
					if (entry.getValue() instanceof String) {
						value = (String) entry.getValue();
					} else {
						value = JSON.toJSONString(entry.getValue());
					}
					nameValuePairs.add(new BasicNameValuePair(entry.getKey(), value));
				}

				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, encode));
			}

			if (StringUtils.isNotBlank(requestBody)) {
				httpPost.setEntity(new StringEntity(requestBody, encode));
			}

		} catch (UnsupportedEncodingException e) {
			logger.error("HttpClientUtils.buildPostParams error, params = " + params, e);
		}
	}

	/**
	 * @param httpRequestBase
	 * @param encode
	 * @return
	 */
	private static String doRequest(HttpRequestBase httpRequestBase, String encode) {
		String responseString = null;
		try {
			long start = System.currentTimeMillis();
			CloseableHttpResponse response = httpClient.execute(httpRequestBase);
			logger.info("HttpClientUtils Begin Invoke: " + httpRequestBase.getURI() + ", cost time "
					+ (System.currentTimeMillis() - start) + " ms");
			try {
				HttpEntity entity = response.getEntity();
				try {
					if (entity != null) {
						responseString = EntityUtils.toString(entity, encode);
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} catch (Exception e) {
				logger.error(String.format("[HttpClientUtils.doRequest] get response error, url:%s",
						httpRequestBase.getURI()), e);
				responseString = "";
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (SocketTimeoutException e) {
			logger.error(String.format("[HttpClientUtils.doRequest] invoke get timout error, url:%s",
					httpRequestBase.getURI()), e);
			responseString = "";
		} catch (Exception e) {
			logger.error(
					String.format("[HttpClientUtils.doRequest] invoke get error, url:%s", httpRequestBase.getURI()), e);
			responseString = "";
		} finally {
			httpRequestBase.releaseConnection();
		}
		logger.info("HttpClientUtils response : " + responseString);

		return responseString;
	}

	/**
	 * @param url
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String buildRequestUrl(String url, Map<String, String> params) throws UnsupportedEncodingException {
		if (CollectionUtils.isEmpty(params)) {
			return url;
		}
		StringBuilder requestUrl = new StringBuilder();
		requestUrl.append(url);
		int i = 0;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (i == 0) {
				requestUrl.append("?");
			}
			requestUrl.append(entry.getKey());
			requestUrl.append("=");
			String value = entry.getValue();
			requestUrl.append(URLEncoder.encode(value, "UTF-8"));
			requestUrl.append("&");
			i++;
		}
		requestUrl.deleteCharAt(requestUrl.length() - 1);
		return requestUrl.toString();
	}
	//
	// public static boolean canWxPay(HttpServletRequest request) {
	// String userAgent = request.getHeader("User-Agent");
	// if (userAgent == null || userAgent.indexOf("MicroMessenger/") == -1) {
	// return false;
	// }
	// String version = StringUtils.substringAfter(userAgent,
	// "MicroMessenger/").substring(0, 3); // MicroMessenger/5.0
	// Float srcVersion = Float.parseFloat(version);
	// Float targetVersion = 5.0f;
	// return srcVersion > targetVersion;
	// }
}
