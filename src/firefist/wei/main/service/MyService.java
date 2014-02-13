package firefist.wei.main.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import firefist.wei.main.MyConstants;
import firefist.wei.main.tinything.Register;
import firefist.wei.main.tinything.Register2;

public class MyService {

	public static HttpClient httpClient = MyConstants.httpClient;

	private static synchronized HttpClient getHttpClient() {
		return httpClient;	
	}

	/**
	 * 用户服务
	 * 
	 * @param email
	 * @param passwd
	 * @return
	 * @throws Exception
	 */

	public static InputStream getRegister() throws Exception {

		String path = MyConstants.WebURL + "user/register";

		Map<String, String> params = new HashMap<String, String>();
		params.put("email", Register.uname);
		params.put("password", Register.upasswd);
		params.put("gender", Register2.gender);
		params.put("birthday", Register2.birthday);

		Log.e("TAG", params.get("birthday"));

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				pairs.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, "UTF-8");
		HttpPost httpPost = new HttpPost(path);
		httpPost.setEntity(entity);
		// DefaultHttpClient client = new DefaultHttpClient();
		HttpClient client = getHttpClient();
		HttpResponse response = client.execute(httpPost);
		if (response.getStatusLine().getStatusCode() == 200) {

			return response.getEntity().getContent();
		}
		return null;
	}

	public static InputStream getLogin(String email, String passwd)
			throws Exception {
		String path = MyConstants.WebURL + "user/login";

		Map<String, String> params = new HashMap<String, String>();
		params.put("email", email);
		params.put("password", passwd);

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				pairs.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, "UTF-8");
		HttpPost httpPost = new HttpPost(path);
		httpPost.setEntity(entity);
		// DefaultHttpClient client = new DefaultHttpClient();
		HttpClient client = getHttpClient();
		HttpResponse response = client.execute(httpPost);
		if (response.getStatusLine().getStatusCode() == 200) {

			return response.getEntity().getContent();
		} else {

			return null;
		}
	}

	public static InputStream getUserInfo(String uid) throws Exception {
		String path = MyConstants.WebURL + "user/uid";

		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", uid);
		params.put("latitude", MyConstants.Latitude + "");
		params.put("longitude", MyConstants.Longitude + "");

		return HttpPostBody(path, params);
	}

	/**
	 * 查看 附件 状态
	 * 
	 * @param offset
	 * @return
	 * @throws Exception
	 */
	public static InputStream getNearStatus(int offset) throws Exception {
		String path = MyConstants.WebURL + "status/list_statuses";

		Map<String, String> params = new HashMap<String, String>();
		params.put("owner", MyConstants.UserUid);
		params.put("page", offset + "");
		params.put("latitude", MyConstants.Latitude + "");
		params.put("longitude", MyConstants.Longitude + "");

		return HttpPostBody(path, params);
	}

	/**
	 * 查看 活动
	 * 
	 * @param offset
	 * @return
	 * @throws Exception
	 */
	public static InputStream getNearActive(int offset) throws Exception {
		String path = MyConstants.WebURL + "activity/list_activities";

		Map<String, String> params = new HashMap<String, String>();
		params.put("page", offset + "");
		params.put("latitude", MyConstants.Latitude + "");
		params.put("longitude", MyConstants.Longitude + "");

		return HttpPostBody(path, params);
	}

	public static InputStream getMyZone(String uid) throws Exception {
		String path = MyConstants.WebURL + "user/zone";

		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", uid);
		params.put("latitude", MyConstants.Latitude + "");
		params.put("longitude", MyConstants.Longitude + "");

		return HttpPostBody(path, params);
	}

	public static InputStream LeaveMessage(String action,
			Map<String, String> map) throws Exception {
		String path = MyConstants.WebURL + "LeaveMessageServlet";
		Map<String, String> params = new HashMap<String, String>();
		if (action.equals("chat")) {
			params.put(action, "chat");
			params.put("sender", map.get("sender"));
			params.put("receiver", map.get("receiver"));
			params.put("content", map.get("content"));
		} else {
			params.put("action", "activity_message");
			params.put("aid", map.get("aid"));
			params.put("uid", map.get("uid"));
			params.put("content", map.get("content"));
			params.put("reference_mid", map.get("reference_mid"));
			params.put("aid_owner", map.get("aid_owner"));

		}

		return HttpPostBody(path, params);
	}

	public static InputStream getShowComments(String action, String id)
			throws Exception {
		String path = "";
		Map<String, String> params = new HashMap<String, String>();
		if (action.equals("aid")) {
			path = MyConstants.WebURL + "activity/list_comments";
			params.put("aid", id);

		} else {
			path = MyConstants.WebURL + "status/list_comments";
			params.put("sid", id);
		}
		return HttpPostBody(path, params);

	}

	public static InputStream sendActiveComment(HashMap<String, String> map)
			throws Exception {
		String path = MyConstants.WebURL + "activity/comment";
		String[] keys = new String[] { "aid", "uid", "comment_content",
				"refrence_mid" };
		Map<String, String> params = new HashMap<String, String>();
		for (int i = 0; i < keys.length; i++) {
			params.put(keys[i], map.get(keys[i]));
		}

		return HttpPostBody(path, params);
	}

	public static InputStream sendStatusComment(HashMap<String, String> map)
			throws Exception {
		String path = MyConstants.WebURL + "status/comment";
		String[] keys = new String[] { "sid", "uid", "comment_content",
				"refrence_mid" };
		Map<String, String> params = new HashMap<String, String>();
		for (int i = 0; i < keys.length; i++) {
			params.put(keys[i], map.get(keys[i]));
		}

		return HttpPostBody(path, params);
	}
	
	public static InputStream getJoin(String aid)
			throws Exception {
		String path = MyConstants.WebURL + "activity/list_joins";
		Map<String, String> params = new HashMap<String, String>();
			params.put("aid", aid);

		return HttpPostBody(path, params);
	}
	
	public static InputStream goJoin(HashMap<String, String> map)
			throws Exception {
		String path = MyConstants.WebURL + "activity/join";
		String[] keys = new String[] { "aid", "uid"};
		Map<String, String> params = new HashMap<String, String>();
		for (int i = 0; i < keys.length; i++) {
			params.put(keys[i], map.get(keys[i]));
		}
		return HttpPostBody(path, params);
	}

	public static byte[] getImage(String path) throws Exception {
		URL url = new URL(path);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if ((conn.getResponseCode()) == 200) {
			InputStream inStream = conn.getInputStream();

			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			byte[] buf = outStream.toByteArray();
			outStream.close();
			inStream.close();

			return buf;
		}
		return null;
	}
	
	public static InputStream deleteStatus(String sid)
			throws Exception {
		String path = MyConstants.WebURL + "delete/status";

		Map<String, String> params = new HashMap<String, String>();
		params.put("sid", sid);

		return HttpPostBody(path, params);
	}
	
	public static InputStream deleteActive(String aid)
			throws Exception {
		String path = MyConstants.WebURL + "delete/activity";

		Map<String, String> params = new HashMap<String, String>();
		params.put("aid", aid);

		return HttpPostBody(path, params);
	}


	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * 通过HttpClient发送Post请求
	 * 
	 * @param path
	 *            请求路径
	 * @param params
	 *            请求参数
	 * @param encoding
	 *            编码
	 * @return 请求是否成功
	 */
	public static boolean sendHttpClientPOSTRequest(String path,
			Map<String, String> params, String encoding) throws Exception {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				pairs.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, encoding);
		HttpPost httpPost = new HttpPost(path);
		httpPost.setEntity(entity);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(httpPost);
		if (response.getStatusLine().getStatusCode() == 200) {

			return true;
		}
		return false;
	}

	/**
	 * 发送Post请求
	 * 
	 * @param path
	 *            请求路径
	 * @param params
	 *            请求参数
	 * @param encoding
	 *            编码
	 * @return 请求是否成功
	 */
	public static boolean sendPOSTRequest(String path,
			Map<String, String> params, String encoding) throws Exception {
		// title=liming&timelength=90
		StringBuilder data = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				data.append(entry.getKey()).append("=");
				data.append(URLEncoder.encode(entry.getValue(), encoding));
				data.append("&");
			}
			data.deleteCharAt(data.length() - 1);
		}
		byte[] entity = data.toString().getBytes();
		HttpURLConnection conn = (HttpURLConnection) new URL(path)
				.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
		OutputStream outStream = conn.getOutputStream();
		outStream.write(entity);
		if (conn.getResponseCode() == 200) {
			return true;
		}
		return false;
	}

	// ///////////////////////////////////////////////////////////////////

	private static InputStream HttpPostBody(String path,
			Map<String, String> params) throws Exception {

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				pairs.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, "UTF-8");
		HttpPost httpPost = new HttpPost(path);
		httpPost.setEntity(entity);
		//DefaultHttpClient client = new DefaultHttpClient();
		HttpClient client = getHttpClient();
		HttpResponse response = client.execute(httpPost);
		if (response.getStatusLine().getStatusCode() == 200) {

			return response.getEntity().getContent();
		}else{
			return null;
		}

	}

}
