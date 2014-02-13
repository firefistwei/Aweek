package firefist.wei.main.u3bdomain;

public class U3B_ActiveBean {

	private long uid;
	private long aid;
	private String title;
	private String time;
	private String position;
	
	private double longitude;//经度     计算附近距离
	private double latitude;//纬度
	
	private int comment_count;
	private int like_count;
	private int isfinished;
	private String audio_url;
	private String video_url;
	
	public U3B_ActiveBean(){
		
	}
	
	public U3B_ActiveBean(long uid, long aid, String title, String time,
			String position, double longitude, double latitude,
			int comment_count, int like_count, int isfinished,
			String audio_url, String video_url) {
		super();
		this.uid = uid;
		this.aid = aid;
		this.title = title;
		this.time = time;
		this.position = position;
		this.longitude = longitude;
		this.latitude = latitude;
		this.comment_count = comment_count;
		this.like_count = like_count;
		this.isfinished = isfinished;
		this.audio_url = audio_url;
		this.video_url = video_url;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getAid() {
		return aid;
	}

	public void setAid(long aid) {
		this.aid = aid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}

	public int getLike_count() {
		return like_count;
	}

	public void setLike_count(int like_count) {
		this.like_count = like_count;
	}

	public int getIsfinished() {
		return isfinished;
	}

	public void setIsfinished(int isfinished) {
		this.isfinished = isfinished;
	}

	public String getAudio_url() {
		return audio_url;
	}

	public void setAudio_url(String audio_url) {
		this.audio_url = audio_url;
	}

	public String getVideo_url() {
		return video_url;
	}

	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}
	

}
