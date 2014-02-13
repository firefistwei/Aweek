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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Environment;
import android.util.Log;

import firefist.wei.main.MyConstants;

public class MyUploadService {

	/**
	 * @����
	 * @param file
	 * @return
	 */
	public static String uploadVideo(Map<String, String> params,
			File photoFile, File videoFile) {
		String url = MyConstants.WebURL + "status/add_2";
		
		String keys[] = new String[]{"owner", "extra_message", "latitude","longitude","display_time"};
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i], params.get(keys[i]));
		}

		String response = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		MultipartEntity multipartEntity = new MultipartEntity();
		try {
			multipartEntity.addPart("file", new FileBody(photoFile));
			multipartEntity.addPart("file", new FileBody(videoFile));
			
			for (int i = 0; i < keys.length; i++) {
				multipartEntity.addPart(
						keys[i],
						new StringBody(URLEncoder.encode(map.get(keys[i]),
								"UTF-8")));
			}
			
			httpPost.setEntity(multipartEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity responseEntity = httpResponse.getEntity();
			
			response = EntityUtils.toString(responseEntity);
			/*InputStream inputStream = responseEntity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {

				sb.append(line);
			}
			response = sb.toString();*/
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public static String uploadVoice(Map<String, String> params,
			File photoFile, File audioFile) {
		String url = MyConstants.WebURL + "status/add_1";
		
		String keys[] = new String[]{"owner", "extra_message", "latitude","longitude","display_time"};
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i], params.get(keys[i]));
		}

		String response = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		MultipartEntity multipartEntity = new MultipartEntity();
		try {
			multipartEntity.addPart("file", new FileBody(photoFile));
			multipartEntity.addPart("file", new FileBody(audioFile));
			
			for (int i = 0; i < keys.length; i++) {
				multipartEntity.addPart(
						keys[i],
						new StringBody(URLEncoder.encode(map.get(keys[i]),
								"UTF-8")));
			}
			
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

	
	/**
	 * @�û�
	 * @param file
	 * @return
	 */
	public static String uploadUserInfo(File headFile) {
		String url = MyConstants.WebURL + "user/setMultiple";

		String keys[] = {"uid","user_name", "signature", "job", "hobby",
				"school", "present" };
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i], MyConstants.User_Map.get(keys[i]));
		}

		String response = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		MultipartEntity multipartEntity = new MultipartEntity();
		try {
			if(headFile!=null){
				multipartEntity.addPart("file", new FileBody(headFile));
			}
			

			for (int i = 0; i < keys.length; i++) {
				multipartEntity.addPart(
						keys[i],
						new StringBody(URLEncoder.encode(map.get(keys[i]),
								"UTF-8")));
			}
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
	
	public static String uploadUserVideo(File photoFile, File videoFile) {
		String url = MyConstants.WebURL + "user/setVideo";

		String keys[] = {"uid" };
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i], MyConstants.User_Map.get(keys[i]));
		}

		String response = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		MultipartEntity multipartEntity = new MultipartEntity();
		try {
			multipartEntity.addPart("file", new FileBody(photoFile));
			multipartEntity.addPart("file", new FileBody(videoFile));

			for (int i = 0; i < keys.length; i++) {
				multipartEntity.addPart(
						keys[i],
						new StringBody(URLEncoder.encode(map.get(keys[i]),
								"UTF-8")));
			}
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
	
	
	
	/**
	 * @�
	 * @param file
	 * @return
	 */
	public static String uploadActive(File photoFile,File audioFile, File videoFile,
			HashMap<String, String> hashMap) {
		String url = MyConstants.WebURL + "activity/add";
		String keys[] = new String[]{ "owner", "start_time", "display_time",
				"location", "title","latitude", "longitude" };
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i], hashMap.get(keys[i]));
		}
		Log.e("TAG","Active map done");
		
		String response = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		MultipartEntity multipartEntity = new MultipartEntity();
		try{
			multipartEntity.addPart("file",new FileBody(photoFile));
			multipartEntity.addPart("file",new FileBody(audioFile));
			multipartEntity.addPart("file",new FileBody(videoFile));
			
			for(int i=0; i<keys.length; i++){
				multipartEntity.addPart(keys[i],new StringBody(
						URLEncoder.encode(map.get(keys[i]),"UTF-8")));
			}
			httpPost.setEntity(multipartEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity responseEntity = httpResponse.getEntity();
			InputStream inputStream = responseEntity.getContent();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder sb = new StringBuilder();
			String line =null;
			while((line = br.readLine())!= null){
				sb.append(line);
			}
			response = sb.toString();
			System.out.println(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}
}
