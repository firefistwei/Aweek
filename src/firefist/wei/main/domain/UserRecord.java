package firefist.wei.main.domain;

public class UserRecord {

	private int uid;
	private int max_upload;
	private int max_collect;
	private int max_note;

	public UserRecord() {

	}

	public UserRecord(int uid, int max_upload, int max_collect, int max_note) {
		super();
		this.uid = uid;
		this.max_upload = max_upload;
		this.max_collect = max_collect;
		this.max_note = max_note;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getMax_upload() {
		return max_upload;
	}

	public void setMax_upload(int max_upload) {
		this.max_upload = max_upload;
	}

	public int getMax_collect() {
		return max_collect;
	}

	public void setMax_collect(int max_collect) {
		this.max_collect = max_collect;
	}

	public int getMax_note() {
		return max_note;
	}

	public void setMax_note(int max_note) {
		this.max_note = max_note;
	}

	@Override
	public String toString() {
		return "UserRecord [uid=" + uid + ", max_upload=" + max_upload
				+ ", max_collect=" + max_collect + ", max_note=" + max_note
				+ "]";
	}

}
