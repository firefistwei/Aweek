package firefist.wei.main.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.net.Uri;
import android.util.Xml;
import firefist.wei.main.MyConstants;
import firefist.wei.main.u3bdomain.U3B_Photo;
import firefist.wei.utils.MD5;

public class PhotosCategoryService {

	public static List<U3B_Photo> getRecentPhoto() throws Exception {
		String path = MyConstants.WebURL + "PhotosCategoryServlet";
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(3000);
		conn.setRequestMethod("GET");

		System.out.println("发送GET连接请求中");
		if (conn.getResponseCode() == 200) {
			InputStream inStream = conn.getInputStream();
			System.out.println("看来是连接成功了呀。");
			return parseXML(inStream);
		}
		System.out.println("失败了");
		return null;
	}

	private static List<U3B_Photo> parseXML(InputStream inStream) throws Exception {
		List<U3B_Photo> photos = new ArrayList<U3B_Photo>();
		U3B_Photo photo = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inStream, "UTF-8");
		int event = parser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				/*if ("photo".equals(parser.getName())) {
					int pid = Integer.valueOf(parser.getAttributeValue(0));
					photo = new Photo();
					photo.setPid(pid);
				} else if ("gid".equals(parser.getName())) {
					photo.setGid(Integer.valueOf(parser.nextText()));
				} else if ("pposition".equals(parser.getName())) {
					photo.setPposition(new String(parser.nextText()));
				} else if ("title".equals(parser.getName())) {
					photo.setTitle(parser.nextText());
				} else if ("uploader".equals(parser.getName())) {
					photo.setUploader(parser.nextText());
				} else if ("pdate".equals(parser.getName())) {
					photo.setPdate(parser.nextText());
				} else if ("description".equals(parser.getName())) {
					photo.setDescription(parser.nextText());
				} else if ("max_comment".equals(parser.getName())) {
					photo.setMax_comment(Integer.valueOf(parser.nextText()));
				} else if ("score".equals(parser.getName())) {
					photo.setScore(Integer.valueOf(parser.nextText()));
				}
				break;*/

			case XmlPullParser.END_TAG:
				if ("photo".equals(parser.getName())) {
					photos.add(photo);
					photo = null;
				}
				break;
			}
			event = parser.next();
		}
		return photos;
	}
	/*
	 * xml数据如下 <photos> <photo _id="10000"> <gid>100</gid>
	 * <position>/nwu/group0/Ahri_0.jpg</position> </photo> </photos>
	 */
	
	public static Uri getImage(String path,File cacheDir) throws Exception{
		
		File localFile = new File(cacheDir, MD5.getMD5(path)+
				path.substring(path.lastIndexOf(".")));
		if(localFile.exists()){
			return Uri.fromFile(localFile);
		}else{
			HttpURLConnection conn = (HttpURLConnection)new URL(path).openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			
			System.out.println(conn.getResponseCode());
			if(conn.getResponseCode() == 200){
				FileOutputStream outStream = new FileOutputStream(localFile);
				InputStream inputStream = conn.getInputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len = inputStream.read(buffer))!=-1){
					outStream.write(buffer,0,len);
				}
				inputStream.close();
				outStream.close();
				
				System.out.println(Uri.fromFile(localFile));
				return Uri.fromFile(localFile);
			}
		}
		return null;
	}
}
