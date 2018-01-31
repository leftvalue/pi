package top.leftvalue.dk.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author linxi top.leftvalue.dk.model 用于执行命令的回传 Date 2017/12/10 上午1:52
 */
public class ResponseJson {
	public static ResponseJson SUCCESS = new ResponseJson(1, "成功");
	public static ResponseJson FAIL = new ResponseJson(0, "失败");

	public static ResponseJson of(int state, String message) {
		return new ResponseJson(state, message);
	}

	@Override
	public String toString() {
		JSONObject o = new JSONObject();
		o.put("state", code);
		o.put("message", message);
		return JSON.toJSONString(o);
	}

	private int code;
	private String message;

	public ResponseJson(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public static void main(String[] args) {
		System.out.println(ResponseJson.SUCCESS.toString());
	}
}
