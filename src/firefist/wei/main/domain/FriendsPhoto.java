package firefist.wei.main.domain;

public class FriendsPhoto {

	private int f_id;

	private String f_name;

	private String f_headurl; // Url+

	private String p_time;

	private String p_title;

	private String p_content;

	private int like_count;
	
	private int comment_count;

	private String photo_url;//Url+
	
	public FriendsPhoto(){
		
	}

	public FriendsPhoto(int f_id, String f_name, String f_headurl,
			String p_time, String p_title, String p_content, int like_count,
			int comment_count, String photo_url) {
		super();
		this.f_id = f_id;
		this.f_name = f_name;
		this.f_headurl = f_headurl;
		this.p_time = p_time;
		this.p_title = p_title;
		this.p_content = p_content;
		this.like_count = like_count;
		this.comment_count = comment_count;
		this.photo_url = photo_url;
	}



	public int getF_id() {
		return f_id;
	}

	public void setF_id(int f_id) {
		this.f_id = f_id;
	}

	public String getF_name() {
		return f_name;
	}

	public void setF_name(String f_name) {
		this.f_name = f_name;
	}

	public String getF_headurl() {
		return f_headurl;
	}

	public void setF_headurl(String f_headurl) {
		this.f_headurl = f_headurl;
	}

	public String getP_time() {
		return p_time;
	}

	public void setP_time(String p_time) {
		this.p_time = p_time;
	}

	public String getP_title() {
		return p_title;
	}

	public void setP_title(String p_title) {
		this.p_title = p_title;
	}

	public String getP_content() {
		return p_content;
	}

	public void setP_content(String p_content) {
		this.p_content = p_content;
	}

	public int getLike_count() {
		return like_count;
	}

	public void setLike_count(int like_count) {
		this.like_count = like_count;
	}

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}

	public String getPhoto_url() {
		return photo_url;
	}

	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}

	
}
