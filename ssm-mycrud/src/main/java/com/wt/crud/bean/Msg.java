package com.wt.crud.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用的返回的类
 * @author 10573
 *
 */
public class Msg {
	//状态码:100成功，200失败
	private int code;
	
	//提示信息
	private String msg;
	
	//用户要返回给浏览器的数据
	private Map<String, Object> extend = new HashMap<String, Object>();
	
	//在controller中调用success方法，返回Msg对象
	public static Msg success() {
		Msg result=new Msg();
		result.setCode(100);
		result.setMsg("处理成功");
		return result;
		
	}
	public static Msg fail() {
		Msg result=new Msg();
		result.setCode(200);
		result.setMsg("处理失败");
		return result;
		
	}
	//可以链式操作
	public Msg add(String key,Object value) {
		this.getExtend().put(key, value);
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	public Map<String, Object> getExtend() {
		return extend;
	}
	public void setExtend(Map<String, Object> extend) {
		this.extend = extend;
	}
	
}