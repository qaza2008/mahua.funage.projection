package com.example.mahua.funage.domain;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
	// "tel": "18511947566",
	// "order_no": "1422341658985800",
	// "name": "票机测试《爷们儿·叁》",
	// "show_date": "2015-01-30",
	// "show_time": "19:30:00",
	// "start_time": "1422617400",
	// "theater_name": "海淀剧院",
	// "trafficInfo": "中关村",
	// "theater_mobile": "",
	// "ticket_info": [
	// tel var 订单联系人电话
	// order_no char(16) 订单号
	// show_date date 演出日期
	// show_time time 开演时间
	// theater_name var 剧场名称
	// trafficInfo tinytext 乘车信息
	// theater_mobile varchar 场馆咨询电话
	
	private String tel;// "18511947566"订单联系人电话,
	
	private String order_no;// "1422341658985800"订单号,
	
	private String name;// : 票机测试《爷们儿·叁》,演出名
	
	private String show_date;// : "2015-01-30"演出日期,
	
	private String show_time;// : "19:30:00"开演时间,
	
	private String start_time;// : "1422617400",
	
	private String theater_name;// : "海淀剧院"剧场名称,
	
	private String trafficInfo;// : "中关村"乘车信息,
	
	private String theater_mobile;// : ""场馆咨询电话,
	
	private List<Ticket> ticket_info;// : [票的信息
	
	public String getTel() {
		return tel;
	}
	
	public void setTel(String tel) {
		this.tel = tel;
	}
	
	public String getOrder_no() {
		return order_no;
	}
	
	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getShow_date() {
		return show_date;
	}
	
	public void setShow_date(String show_date) {
		this.show_date = show_date;
	}
	
	public String getShow_time() {
		return show_time;
	}
	
	public void setShow_time(String show_time) {
		this.show_time = show_time;
	}
	
	public String getStart_time() {
		return start_time;
	}
	
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	
	public String getTheater_name() {
		return theater_name;
	}
	
	public void setTheater_name(String theater_name) {
		this.theater_name = theater_name;
	}
	
	public String getTrafficInfo() {
		return trafficInfo;
	}
	
	public void setTrafficInfo(String trafficInfo) {
		this.trafficInfo = trafficInfo;
	}
	
	public String getTheater_mobile() {
		return theater_mobile;
	}
	
	public void setTheater_mobile(String theater_mobile) {
		this.theater_mobile = theater_mobile;
	}
	
	public List<Ticket> getTicket_info() {
		return ticket_info;
	}
	
	public void setTicket_info(List<Ticket> ticket_info) {
		this.ticket_info = ticket_info;
	}

	@Override
	public String toString() {
		return "Order [tel=" + tel + ", order_no=" + order_no + ", name=" + name + ", show_date=" + show_date
			+ ", show_time=" + show_time + ", start_time=" + start_time + ", theater_name=" + theater_name
			+ ", trafficInfo=" + trafficInfo + ", theater_mobile=" + theater_mobile + ", ticket_info=" + ticket_info
			+ "]";
	}
	
}
