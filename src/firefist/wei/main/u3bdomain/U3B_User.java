package firefist.wei.main.u3bdomain;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class U3B_User {

/*	private long uid;
	private String email;
	private String password;
	
	private String user_name;
	private String head_url;
	
	private String signature;// 签名
	private int gender;// 性别   1男  0女
	private String birthday;
	private String school;
	
	private String goodat;
	private String job;
	private String register_day;*/


		private  Map<String ,Object>  map =new HashMap<String, Object>();
		
		public Map<String, Object> getMap() {
			return map;
		}

		public void setMap(Map<String, Object> map) {
			this.map = map;
		}

}
