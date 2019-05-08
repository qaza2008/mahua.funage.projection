package com.example.mahua.funage.utils;

public interface Constants {
	// 打印服务器返回状态 0 正常 -1 异常
	public static final String HTTP_STATUS = "status";
	
	public static final String HTTP_STATUS_OK = "200";// 成功
	
	public static final String HTTP_STATUS_FAIL_201 = "201";// 没有这个场次
	
	public static final String HTTP_STATUS_FAIL_202 = "202";// 参数错误u
	
	// /打印服务器返回信息
	public static final String HTTP_MESSAGE = "message";
	
	public static final String HTTP_DATA = "data";
	
	/**
	 * 获取url前缀 http://
	 */
	public String URL_HTTP_HEAD = "http://";
	
	/**
	 * 主机名/域名
	 */
	//测试地址
//	public String URL_HTTP_AUTHORITY = "182.92.128.54/ticketOffice/modules/api/checking";
	//正式地址www.kaixinguoticket.com/modules/api
	public String URL_HTTP_AUTHORITY = "www.kaixinguoticket.com/modules/api/checking";
	
	/**
	 * 分割线
	 */
	public String URL_SEPARATOR = "/";
	
	/**
	 * 全路径 需要加path
	 */
	public String URL_PATH = URL_HTTP_HEAD + URL_HTTP_AUTHORITY + URL_SEPARATOR;
	
	/**
	 * 获取城市
	 */
	public static final String PATH_LOAD_CITY = "apiComm.php";
	
	/**
	 * 获取剧院
	 */
	public static final String PATH_LOAD_DRAMA = "apiComm.php";
	
	/**
	 * 获取时间
	 */
	public static final String PATH_LOAD_TIME = "apiComm.php";
	
	/**
	 * 获取order
	 */
	public static final String PATH_LOAD_ORDER = "apiPrint.php";
	
	/**
	 * 提交票务
	 */
	public static final String PATH_SUBMIT_TICKETS = "apiPrintBack.php";
	
	/**
	 * 已验证的进度
	 */
	public static final String PATH_LOAD_NUMBER = "apiComm.php";
//	act=drama_number&drama_id=
	
	/**
	 * 验证手持机是否有效
	 */
	public static final String PATH_AUTH = "apiAuth.php";
}
