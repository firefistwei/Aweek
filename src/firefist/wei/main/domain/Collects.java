package firefist.wei.main.domain;

public class Collects {

	private int uid;
	private int num_collect;
	private int con_collect;

	public Collects() {

	}

	public Collects(int uid, int num_collect, int con_collect) {
		super();
		this.uid = uid;
		this.num_collect = num_collect;
		this.con_collect = con_collect;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getNum_collect() {
		return num_collect;
	}

	public void setNum_collect(int num_collect) {
		this.num_collect = num_collect;
	}

	public int getCon_collect() {
		return con_collect;
	}

	public void setCon_collect(int con_collect) {
		this.con_collect = con_collect;
	}

	@Override
	public String toString() {
		return "Collects [uid=" + uid + ", num_collect=" + num_collect
				+ ", con_collect=" + con_collect + "]";
	}

}
