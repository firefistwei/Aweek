package firefist.wei.main.domain;

public class MyActive {

	private int uid;
	private int aid;
	private int atype;
	private String aname; //unused
	private String atime;
	private String aposition;
	private String amember; //unused
	private String apeople_no; //unused
	private String adescrip; //unused
	private double alongi;
	private double alatitude;
	private int isfull;  //unused
	private int isfinished;

	public MyActive() {

	}

	public MyActive(int uid, int aid, int atype, String aname, String atime,
			String aposition, String amember, String apeople_no,
			String adescrip, double alongi, double alatitude, int isfull,
			int isfinished) {
		super();
		this.uid = uid;
		this.aid = aid;
		this.atype = atype;
		this.aname = aname;
		this.atime = atime;
		this.aposition = aposition;
		this.amember = amember;
		this.apeople_no = apeople_no;
		this.adescrip = adescrip;
		this.alongi = alongi;
		this.alatitude = alatitude;
		this.isfull = isfull;
		this.isfinished = isfinished;
	}

	 public String getAtime() {
		return atime;
	}

	public void setAtime(String atime) {
		this.atime = atime;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getAid() {
		return aid;
	}

	public void setAid(int aid) {
		this.aid = aid;
	}

	public int getAtype() {
		return atype;
	}

	public void setAtype(int atype) {
		this.atype = atype;
	}

	public String getAname() {
		return aname;
	}

	public void setAname(String aname) {
		this.aname = aname;
	}

	public String getAposition() {
		return aposition;
	}

	public void setAposition(String aposition) {
		this.aposition = aposition;
	}

	public String getAmember() {
		return amember;
	}

	public void setAmember(String amember) {
		this.amember = amember;
	}

	public String getApeople_no() {
		return apeople_no;
	}

	public void setApeople_no(String apeople_no) {
		this.apeople_no = apeople_no;
	}

	public String getAdescrip() {
		return adescrip;
	}

	public void setAdescrip(String adescrip) {
		this.adescrip = adescrip;
	}

	public double getAlongi() {
		return alongi;
	}

	public void setAlongi(double alongi) {
		this.alongi = alongi;
	}

	public double getAlatitude() {
		return alatitude;
	}

	public void setAlatitude(double alatitude) {
		this.alatitude = alatitude;
	}

	public int getIsfull() {
		return isfull;
	}

	public void setIsfull(int isfull) {
		this.isfull = isfull;
	}

	public int getIsfinished() {
		return isfinished;
	}

	public void setIsfinished(int isfinished) {
		this.isfinished = isfinished;
	}

}
