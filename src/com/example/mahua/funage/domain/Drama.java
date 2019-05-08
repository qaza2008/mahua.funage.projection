package com.example.mahua.funage.domain;

import java.io.Serializable;

public class Drama implements Serializable {
	// "name": "票机测试《爷们儿·叁》",
	// "start_time": "1422358200"
	//"id": "12"
	private String name;
	
	private String start_time;
	
	private String id;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getStart_time() {
		return start_time;
	}
	
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	
}
