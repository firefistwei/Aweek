package firefist.wei.main.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import firefist.wei.main.MyConstants;
import firefist.wei.main.service.CustomMultipartEntity.ProgressListener;

public class NewUploadService {

	private static HttpClient httpClient = null;

	private static final int DEFAULT_MAX_CONNECTIONS = 5;
	private static final int DEFAULT_HOST_CONNECTIONS = 3;
	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 10000000; // 10M

	private static synchronized HttpClient getHttpClient() {
		if (httpClient == null) {
			final HttpParams httpParams = new BasicHttpParams();

			// timeout: get connections from connection pool/* 从连接池中取连接的超时时间 */
			ConnManagerParams.setTimeout(httpParams, 2000);
			// timeout: connect to the server /* 连接超时 */
			HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
			// timeout: transfer data from server /* 请求超时 */
			HttpConnectionParams.setSoTimeout(httpParams, 20000);

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

		return httpClient;
	}

	public static class UploadVideoTask extends
			AsyncTask<String, Integer, String> {

		private String url = MyConstants.WebURL + "status/add_2";
		private String keys[] = new String[] { "owner", "extra_message",
				"latitude", "longitude" };
		private Context context;
		private ProgressDialog pd;
		private long totalSize;
		private Map<String, String> maps;
		private File photoFile, videoFile;

		public UploadVideoTask(Context context, Map<String, String> maps,
				File photoFile, File videoFile) {
			this.context = context;
			this.maps = maps;
			this.photoFile = photoFile;
			this.videoFile = videoFile;
		}

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(context);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setMessage("正在上传...");
			pd.setTitle("请稍候");
            pd.setProgress(100);
            pd.setIndeterminate(false);
			pd.setCancelable(true);
            pd.show();
		}

		@Override
		protected String doInBackground(String... params) {

			HashMap<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < keys.length; i++) {
				map.put(keys[i], maps.get(keys[i]));
			}

			String response = null;
			HttpClient httpClient = getHttpClient();
			HttpPost httpPost = new HttpPost(url);
			try {
				CustomMultipartEntity multipartEntity = new CustomMultipartEntity(

				new ProgressListener() {
					@Override
					public void transferred(long num) {
						publishProgress((int) ((num / (float) totalSize) * 100));
					}
				});
				multipartEntity.addPart("file", new FileBody(photoFile));
				multipartEntity.addPart("file", new FileBody(videoFile));

				for (int i = 0; i < keys.length; i++) {
					multipartEntity.addPart(
							keys[i],
							new StringBody(URLEncoder.encode(map.get(keys[i]),
									"UTF-8")));
				}
				totalSize = multipartEntity.getContentLength();

				httpPost.setEntity(multipartEntity);
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity responseEntity = httpResponse.getEntity();

				InputStream inputStream = responseEntity.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						inputStream));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {

					sb.append(line);
				}
				response = sb.toString();
				System.out.println(sb.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			return response;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			pd.setProgress((int) (progress[0]));
		}

		@Override
		protected void onPostExecute(String result) {	
			if(result == null){
				Toast.makeText(context, "上传失败！", Toast.LENGTH_LONG).show();
			}else{
				try {
					Log.e("TAG", result+"");
					JSONObject jsonObject = new JSONObject(result);

					int flag = Integer.valueOf(jsonObject.get("result").toString());

					if (flag > 0) { // 上传成功
						Toast.makeText(context, "上传成功！", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(context, "上传失败！", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			pd.dismiss();
		}

		@Override
		protected void onCancelled() {
			System.out.println("cancle");
		}

	}

}
