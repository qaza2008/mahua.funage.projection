package com.example.mahua.funage.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateHttpUtils {
	/**
	 * 从网络上获取XML
	 * 
	 * @return XML输入流
	 * @throws IOException 网络文件不存在
	 */
	public static InputStream getVersionFromNetwork() throws IOException {
		// 确定请求服务器的地址
		// 注意在Android模拟器中访问本机服务器时不可以使用localhost与127字段
		// 因为模拟器本身是使用localhost绑定
//		String path = "http://换成你自己的空间名.u.qiniudn.com/version.xml";
		//已废弃
//		String path = "http://qaza2008.free.cscces.net/app/version.xml";
		String path = "http://qxu1193850050.my3w.com/app/version.xml";
		// 建立一个URL对象
		URL url = new URL(path);
		// 得到打开的链接对象
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		// 设置请求超时与请求方式
		conn.setReadTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		// 从链接中获取一个输入流对象
		InputStream inStream = conn.getInputStream();
		return inStream;
	}
}
