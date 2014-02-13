package firefist.wei.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import firefist.wei.main.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;

/**
 * 工具类
 * 
 * @author wei
 * 
 */
public class Utils {
	/**
	 * 根据性别数字获取到性别图片
	 * 
	 * @param res
	 *            Resources对象
	 * @param gender
	 *            0代表女性,1代表男性
	 * @return 性别图片(Bitmap 类型)
	 */
	public static Bitmap getGender(Resources res, int gender) {
		switch (gender) {
		case 0:
			return BitmapFactory.decodeResource(res,
					R.drawable.profile_icon_girl);
		case 1:
			return BitmapFactory.decodeResource(res,
					R.drawable.profile_icon_boy);
		default:
			return BitmapFactory.decodeResource(res,
					R.drawable.profile_icon_boy);
		}
	}

	/**
	 * 根据性别数字获取到性别名称
	 * 
	 * @param gender
	 *            0代表女性,1代表男性
	 * @return 性别名称(String 类型)
	 */
	public static String getGender(int gender) {
		switch (gender) {
		case 0:
			return "女";
		case 1:
			return "男";
		default:
			return "未知";
		}
	}

	/**
	 * 转换long型日期格式
	 * 
	 * @param context
	 * @param date
	 * @return
	 */
	public static String formatDate(Context context, long date) {
		int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT
				| DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_CAP_AMPM
				| DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_DATE
				| DateUtils.FORMAT_SHOW_TIME;
		return DateUtils.formatDateTime(context, date, format_flags);
	}

	/**
	 * 转换long型日期格式
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(long date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date(date));
	}

	/**
	 * 获取当前的时间
	 * 
	 * @param context
	 * @return
	 */
	public static String getTime(Context context) {
		return formatDate(context, System.currentTimeMillis());
	}

	/**
	 * 获取当前的时间
	 * 
	 * @return
	 */
	public static String getTime() {
		return formatDate(System.currentTimeMillis());
	}

	/**
	 * 将inputStream 转成 String
	 * 
	 * @return
	 */
	public static String readInputStream(InputStream inputStream) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();

	}

	public static String createJsonString(String key, Object value)
			throws JSONException
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(key, value);
		return jsonObject.toString();

	}
}
