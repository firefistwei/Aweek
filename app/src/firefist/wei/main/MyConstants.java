package firefist.wei.main;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public final class MyConstants {

	public final static String WebURL = "http://1.aweek.sinaapp.com/";

	/**
	 * 储存用户的信息
	 * 
	 */
	public static HashMap<String, String> User_Map = new HashMap<String, String>();

	public static String UserUid = "0";

	public static double Longitude = 0;

	public static double Latitude = 0;

	public static ArrayList<HashMap<String, String>> page1HashMapList = new ArrayList<HashMap<String, String>>();
	public static ArrayList<HashMap<String, String>> page2HashMapList = new ArrayList<HashMap<String, String>>();

	public static ArrayList<HashMap<String, String>> page3List_Status = new ArrayList<HashMap<String, String>>();
	public static ArrayList<HashMap<String, String>> page3List_Active = new ArrayList<HashMap<String, String>>();

	public static final String[] IMAGES = new String[] {
			"http://img3.chinaface.com/middle/11216lol4UryK6VMlgVOaLNiCRsKQ.jpg",
			"http://www.hinews.cn/pic/0/11/11/28/11112856_974903.jpg",
			"http://img.h9t.net/allimg/121116/1_121116232458_1.jpg",
			"http://photocdn.sohu.com/20061228/Img247320188.jpg",
			"http://x.limgs.cn/f2/g/130606/b201315616203951b046571f84a.jpg",
			"http://imgsrc.baidu.com/forum/pic/item/ed0ed979cd796f6e1e3089f4.jpg"

	};
	
	// for status
	public static HashMap<String,Integer> myLikeMap = new HashMap<String,Integer>();
	// for active
	public static HashMap<String,Integer> mySupportMap = new HashMap<String,Integer>();
	public static HashMap<String,Integer> myJoinMap = new HashMap<String,Integer>();

	
	//httpClient
	public static HttpClient httpClient = null;
	private static final int DEFAULT_MAX_CONNECTIONS = 10;
	private static final int DEFAULT_HOST_CONNECTIONS = 5;
	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 10000000; // 10M
	static {
		final HttpParams httpParams = new BasicHttpParams();

		// timeout: get connections from connection pool/* 从连接池中取连接的超时时间 */
		ConnManagerParams.setTimeout(httpParams, 5000);
		// timeout: connect to the server /* 连接超时 */
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		// timeout: transfer data from server /* 请求超时 */
		HttpConnectionParams.setSoTimeout(httpParams, 5000);

		// set max connections per host
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
				new ConnPerRouteBean(DEFAULT_HOST_CONNECTIONS));
		// set max total connections
		ConnManagerParams.setMaxTotalConnections(httpParams,
				DEFAULT_MAX_CONNECTIONS);

		// use expect-continue handshake
		HttpProtocolParams.setUseExpectContinue(httpParams, true);
		// disable stale check
		HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);

		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);

		HttpClientParams.setRedirecting(httpParams, false);

		// set user agent
		String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
		HttpProtocolParams.setUserAgent(httpParams, userAgent);

		// disable Nagle algorithm
		HttpConnectionParams.setTcpNoDelay(httpParams, true);

		HttpConnectionParams.setSocketBufferSize(httpParams,
				DEFAULT_SOCKET_BUFFER_SIZE);

		// scheme: http and https
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));

		ClientConnectionManager manager = new ThreadSafeClientConnManager(
				httpParams, schemeRegistry);
		httpClient = new DefaultHttpClient(manager, httpParams);
	}
}
