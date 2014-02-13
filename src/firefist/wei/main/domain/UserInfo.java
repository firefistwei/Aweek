package firefist.wei.main.domain;

public class UserInfo {

	private int uid;
	private String name;
	private String head;
	private int sex;
	private int ulevel;
	private int gid;
	private String gclass;
	private String gschool;

	public UserInfo() {

	}

	public UserInfo(int uid, String name, String head, int sex, int ulevel,
			int gid, String gclass, String gschool) {
		super();
		this.uid = uid;
		this.name = name;
		this.head = head;
		this.sex = sex;
		this.ulevel = ulevel;
		this.gid = gid;
		this.gclass = gclass;
		this.gschool = gschool;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getUlevel() {
		return ulevel;
	}

	public void setUlevel(int ulevel) {
		this.ulevel = ulevel;
	}

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public String getGclass() {
		return gclass;
	}

	public void setGclass(String gclass) {
		this.gclass = gclass;
	}

	public String getGschool() {
		return gschool;
	}

	public void setGschool(String gschool) {
		this.gschool = gschool;
	}

	@Override
	public String toString() {
		return "UserInfo [uid=" + uid + ", name=" + name + ", head=" + head
				+ ", sex=" + sex + ", ulevel=" + ulevel + ", gid=" + gid
				+ ", gclass=" + gclass + ", gschool=" + gschool + "]";
	}

}
