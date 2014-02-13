package firefist.wei.main.domain;

public class Uploads {

	private int uid;
	private int num_upload;
	private int con_upload;

	public Uploads() {

	}

	public Uploads(int uid, int num_upload, int con_upload) {
		super();
		this.uid = uid;
		this.num_upload = num_upload;
		this.con_upload = con_upload;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getNum_upload() {
		return num_upload;
	}

	public void setNum_upload(int num_upload) {
		this.num_upload = num_upload;
	}

	public int getCon_upload() {
		return con_upload;
	}

	public void setCon_upload(int con_upload) {
		this.con_upload = con_upload;
	}

	@Override
	public String toString() {
		return "Uploads [uid=" + uid + ", num_upload=" + num_upload
				+ ", con_upload=" + con_upload + "]";
	}

}
