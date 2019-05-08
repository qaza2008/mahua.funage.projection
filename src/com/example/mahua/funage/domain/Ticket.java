package com.example.mahua.funage.domain;

import java.io.Serializable;

public class Ticket implements Serializable {
	// "id": "380022",
	// "drama_id": "470",
	// "ticketNo": "49040471",
	// "ticketCode": "2887798744136423",
	// "ticket_status": "2",
	// "grade": "A",
	// "price": "1080.00",
	// "isPKG": "0",
	// "isPrint": "0",
	// "row": "1",
	// "col": "24",
	// "floor": "1",
	// "f_remark": "包厢",
	// "s_remark": "座位",
	// "qrcode_content":
	// "http://www.kaixinguopiao.com/other/security/index?ZRAIWVFVf8UwIe4sZBeoQhpyTr9mublhglrtNKbFNKU=",
	// "seat_remark": "1层包厢1排24号座位"
	// Id int 票的ID
	// drama_id Int 场次的ID
	// ticket_status tinyint 票的状态
	//
	// 0=可售（默认）
	// 1=锁定
	// 2=已售
	// 3=已打票
	// 4=申请重打中
	// 5=重打申请通过
	// 6=重打申请不通过
	// 7=退票
	// ticketNo Char NO 票面上编码
	// ticketCode char TN票面上编码
	// grade char 票的等级
	// price decimal(7,2) 票价
	// isPKG tinyint 是否套票
	// isPrint tinyint 是否打印
	// row smallint 排号
	// col smallint 列号
	// floor smallint(6) 楼层
	// f_remark varchar(50) 楼层备注
	// s_remark varchar(50) 座位备注
	// qrcode_content varchar 二维图片的内容
	// seat_remark varchar(50) 座位信息
	
	private String id;// 票的ID
	
	private String drama_id;// 场次的ID
	
	private String ticket_status;// 票的状态
	
	private String ticketNo;// 票面上编码
	
	private String ticketCode;// 票面上编码
	
	private String grade;// 票的等级
	
	private String price;// 票价
	
	private String isPKG;// 是否套票
	
	private String isPrint;// 是否打印
	
	private String row;// 排号
	
	private String col;// 列号
	
	private String floor;// 楼层
	
	private String f_remark;// 楼层备注
	
	private String s_remark;// 座位备注/
	
	private String qrcode_content;// 二维图片的内容j
	
	private String seat_remark;// 座位信息
	
	private boolean isSelectd;// 是否选中并准备提交
	
	private boolean isChecked;// 是否点击了
	
	public boolean isSelectd() {
		return isSelectd;
	}
	
	public void setSelectd(boolean isSelectd) {
		this.isSelectd = isSelectd;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDrama_id() {
		return drama_id;
	}
	
	public void setDrama_id(String drama_id) {
		this.drama_id = drama_id;
	}
	
	public String getTicket_status() {
		return ticket_status;
	}
	
	public void setTicket_status(String ticket_status) {
		this.ticket_status = ticket_status;
	}
	
	public String getTicketNo() {
		return ticketNo;
	}
	
	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}
	
	public String getTicketCode() {
		return ticketCode;
	}
	
	public void setTicketCode(String ticketCode) {
		this.ticketCode = ticketCode;
	}
	
	public String getGrade() {
		return grade;
	}
	
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
	public String getPrice() {
		return price;
	}
	
	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getIsPKG() {
		return isPKG;
	}
	
	public void setIsPKG(String isPKG) {
		this.isPKG = isPKG;
	}
	
	public String getIsPrint() {
		return isPrint;
	}
	
	public void setIsPrint(String isPrint) {
		this.isPrint = isPrint;
	}
	
	public String getRow() {
		return row;
	}
	
	public void setRow(String row) {
		this.row = row;
	}
	
	public String getCol() {
		return col;
	}
	
	public void setCol(String col) {
		this.col = col;
	}
	
	public String getFloor() {
		return floor;
	}
	
	public void setFloor(String floor) {
		this.floor = floor;
	}
	
	public String getF_remark() {
		return f_remark;
	}
	
	public void setF_remark(String f_remark) {
		this.f_remark = f_remark;
	}
	
	public String getS_remark() {
		return s_remark;
	}
	
	public void setS_remark(String s_remark) {
		this.s_remark = s_remark;
	}
	
	public String getQrcode_content() {
		return qrcode_content;
	}
	
	public void setQrcode_content(String qrcode_content) {
		this.qrcode_content = qrcode_content;
	}
	
	public String getSeat_remark() {
		return seat_remark;
	}
	
	public void setSeat_remark(String seat_remark) {
		this.seat_remark = seat_remark;
	}
	
	public boolean isChecked() {
		return isChecked;
	}
	
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	@Override
	public String toString() {
		return "Ticket [id=" + id + ", drama_id=" + drama_id + ", ticket_status=" + ticket_status + ", ticketNo="
			+ ticketNo + ", ticketCode=" + ticketCode + ", grade=" + grade + ", price=" + price + ", isPKG=" + isPKG
			+ ", isPrint=" + isPrint + ", row=" + row + ", col=" + col + ", floor=" + floor + ", f_remark=" + f_remark
			+ ", s_remark=" + s_remark + ", qrcode_content=" + qrcode_content + ", seat_remark=" + seat_remark
			+ ", isSelectd=" + isSelectd + ", isChecked=" + isChecked + "]";
	}
	
}
