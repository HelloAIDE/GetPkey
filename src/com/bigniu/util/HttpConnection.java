package com.bigniu.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * ����Ӣ�����˹���
 * @author bigniu
 *
 */
public class HttpConnection {
	private static Map<String, String> localcookies = new HashMap<String, String>();

	private HttpConnection() {

	}

	private static final String ua = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
	private static String referrer = "http://lol.qq.com/";

	public static String getReferrer() {
		return referrer;
	}

	public static void setReferrer(String referrer) {
		HttpConnection.referrer = referrer;
	}

	public static String getJson(String url, Map<String, String> params, Map<String, String> cookies,
			String requestMethod) {
		String data = "";
		String temp = "";
		Response res;
		/**
		 * �������������ƴ��
		 */
		if (params != null && requestMethod.equals("get")) {
			for (String str : params.keySet()) {
				temp += str + "=" + params.get(str) + "&";
			}
			url = url + "?" + temp;
			temp = "";
		}

		// ��ӡ�����cookies
		try {
			if (!url.startsWith("http://captcha.qq.com/cap_union_new_verify")) {
				res = (Response) Jsoup.connect(url).cookies(cookies).data(params)
						.referrer(
								"http://xui.ptlogin2.qq.com/cgi-bin/xlogin?proxy_url=http%3A//qzs.qq.com/qzone/v6/portal/proxy.html&daid=5&&hide_title_bar=1&low_login=0&qlogin_auto_login=1&no_verifyimg=1&link_target=blank&appid=549000912&style=22&target=self&s_url=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&pt_qr_app=%E6%89%8B%E6%9C%BAQQ%E7%A9%BA%E9%97%B4&pt_qr_link=http%3A//z.qzone.com/download.html&self_regurl=http%3A//qzs.qq.com/qzone/v6/reg/index.html&pt_qr_help_link=http%3A//z.qzone.com/download.html")
						.userAgent(ua).header("Accept", "*/*").header("Connection", "keep-alive")
						.ignoreContentType(true).execute();
				Map<String, String> cookie1 = res.cookies();
				if(cookie1.get("skey")!=null)
				{
					localcookies.remove("skey");
				}
				localcookies.put("skey", cookie1.get("skey"));
				localcookies.putAll(cookie1);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Document el = null;
		try {
			if (requestMethod.equals("post")) {
				el = Jsoup.connect(url).cookies(cookies).data(params)
						.referrer(
								referrer)
						.userAgent(ua).header("Accept", "*/*").header("Connection", "keep-alive")
						.ignoreContentType(true).post();
			} else {
				el = Jsoup.connect(url).cookies(cookies)
						.referrer(
								referrer)
						.userAgent(ua).header("Accept", "*/*").header("Connection", "keep-alive")
						.ignoreContentType(true).get();
			}
			data = el.text();
		} catch (IOException e) {
			e.printStackTrace();
			data = "{\"retCode\": 2001, \"msg\": \"�������ӳ���\"}";
		}
		return data;
	}

	/**
	 * @param url
	 *            ���ӵ�ַ
	 * @param params
	 *            �������
	 * @return ����TGP�ٷ�Json����
	 */
	public static String getJson(String url, Map<String, String> params, Map<String, String> cookies) {
		return getJson(url, params, cookies, "get");
	}
	/**
	 * @param url
	 *            ���ӵ�ַ
	 */
	public static String getJson(String url) {
		return getJson(url, new HashMap<String,String>(),new HashMap<String,String>(), "get");
	}
	public static Map<String, String> getCookies() {
		return localcookies;
	}

	/**
	 * ��ָ�� URL ����POST����������
	 * 
	 * @param url
	 *            ��������� URL
	 * @param params
	 *            ����������������Ӧ���� name1=value1&name2=value2 ����ʽ��
	 * @return ������Զ����Դ����Ӧ���
	 */
	public static String sendPost(String url, Map<String, String> params) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// �򿪺�URL֮�������
			URLConnection conn = realUrl.openConnection();
			// ����ͨ�õ���������
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			conn.setRequestProperty("user-agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
			// ����POST�������������������
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// ��ȡURLConnection�����Ӧ�������
			out = new PrintWriter(conn.getOutputStream());
			// �����������
			out.print(params);
			// flush������Ļ���
			out.flush();
			// ����BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("���� POST ��������쳣��" + e);
			e.printStackTrace();
		}
		// ʹ��finally�����ر��������������
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	public static void clearCookies(){
		localcookies = null;
		localcookies = new HashMap<>();
	}
}
