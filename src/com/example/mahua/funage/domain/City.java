package com.example.mahua.funage.domain;

import java.io.Serializable;

public class City implements Serializable {
	
	// "region_id": "52",
	// "region_name": "北京"
	
	private String region_id;
	
	private String region_name;
	
	public String getRegion_id() {
		return region_id;
	}
	
	public void setRegion_id(String region_id) {
		this.region_id = region_id;
	}
	
	public String getRegion_name() {
		return region_name;
	}
	
	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}
	
}
