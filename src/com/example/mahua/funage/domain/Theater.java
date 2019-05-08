package com.example.mahua.funage.domain;

import java.io.Serializable;

public class Theater implements Serializable {
	
	// "id": "1",
	// "name": "海淀剧院"
	private String id;
	
	private String name;
	
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
	
}
