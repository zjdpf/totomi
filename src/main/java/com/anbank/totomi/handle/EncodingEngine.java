package com.anbank.totomi.handle;

import java.awt.Event;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.AbstractDocument.Content;
import javax.xml.validation.Validator;

import com.anbank.totomi.assist.EncodingHelper;
import com.anbank.totomi.config.TotomiConfigure;
import com.ibm.db2.jcc.am.o;
import com.ibm.db2.jcc.am.s;

public class EncodingEngine {
	
	private Map<String, String> keywordsMap;
	private RedisClient redisClient;
	
	public EncodingEngine() {
		// 首先从 key_words.txt 文件中导入固定码表
		keywordsMap = new HashMap<String, String>();
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(TotomiConfigure.Key_Word_File_Path), "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
	        String line = null;
	        while ((line = bufferedReader.readLine()) != null) {  
	            String[] arr = line.split("=");
	            String key = arr[0].trim();
	            String value = arr[1].trim();
	            keywordsMap.put(key, value);
	        }  
	        bufferedReader.close();  
	        inputStreamReader.close();  
		} catch (Exception e) {
			e.printStackTrace();
		}  
		redisClient = new RedisClient();
		// 使用前清空Redis数据库
		redisClient.forceClean();
	}
	
	private class InnerObj {
		public String content;
		public String type;
		public InnerObj(String content, String type) {
			this.content = content;
			this.type = type;
		}
		@Override
		public String toString() {
			return content + " ; " + type;
		}
	}
	
	private String encodingChinese(String string) {
		String res = "";
		for (int i = 0; i < string.length(); i ++) {
			String key = string.substring(i, i+1);
			String value = redisClient.get("c." + key);
			if (value != null) 
				res += value;
			else {
				while (true) {
					value = EncodingHelper.generateRandomChinese();
					if (redisClient.get("cv." + value) == null) {
						redisClient.set("c." + key, value);
						redisClient.set("cv." + value, key);
						res += value;
						break;
					}
				}
			}
		}
		return res;
	}
	
	
	private String encodingEnglish(String string) {
		int len = string.length();
		if (redisClient.get("e." + len) == null) {
			redisClient.set("e." + len, "true");
			for (int i = 0; i < len; i ++) {
				String key = "e." + len + "." + i;
				int shift = (int) (Math.random() * 26);
				redisClient.set(key, "" + shift);
			}
		}
		String res = "";
		for (int i = 0; i < len; i ++) {
			char c = string.charAt(i);
			if (c >= 'a' && c <= 'z') {
				c = (char) (((int) c - 97 + Integer.parseInt(redisClient.get("e." + len + "." + i))) % 26 + 97);
			} else if (c >= 'A' && c <= 'Z') {
				c = (char) (((int) c - 65 + Integer.parseInt(redisClient.get("e." + len + "." + i))) % 26 + 65);
			}
			res += c;
		}
		return res;
	}
	
	private String encodingNumber(String string) {
		if (redisClient.get("d." + string) != null) {
			return redisClient.get("d." + string);
		}
		String num = EncodingHelper.generateRandomNumber(string.length());
		while (redisClient.get("dv." + num) != null) 
			num = EncodingHelper.getNextNumber(num);
		redisClient.set("d." + string, num);
		redisClient.set("dv." + num, string);
		return num;
	}
	
	public void test() {
//		System.out.println(encodingEnglish("aaaaaaaaaaa"));
//		System.out.println(encodingEnglish("bbbbbbbbbbb"));
		for (String key : keywordsMap.keySet()) {
			System.out.println("pp: " + key + "\t" + keywordsMap.get(key));
		}
	}
	
	private static String getType(String s) {	// 确定 s 为单个字符
		// chinese
		String reg1 = "[\\u4e00-\\u9fa5]+";
		if (s.matches(reg1)) {
			return "Chinese";
		}
		// english 
		String reg2 = "[\\u0061-\\u007a]+";
		String reg3 = "[\\u0041-\\u005a]+";
		if (s.matches(reg2) || s.matches(reg3)) {
			return "English";
		}
		// number
		String reg4 = "[\\u0030-\\u0039]+";
		if (s.matches(reg4)) {
			return "Number";
		}
		// other
		return "Other";
	}
	
	public String encodingOneString(String string) {
//		System.out.println("Encoding String : " + string);
		// 首先根据固定关键字分隔
		List<InnerObj> list = new ArrayList<InnerObj>();
		String preS = "";
		while (string.length() > 0) {
			String needKey = null;
			int len = 0;
			for (String key : keywordsMap.keySet()) {
				if (string.startsWith(key) && (needKey == null || len < key.length())) {
					needKey = key;
					len = key.length();
				} 
			}
			if (len > 0) {
				if (preS.length() > 0) {
					String content = preS;
					String type = getType(preS.substring(0, 1));
					InnerObj innerObj = new InnerObj(content, type);
					list.add(innerObj);
					preS = "";
				}
				String content = needKey;
				String type = "Keyword";
				InnerObj innerObj = new InnerObj(content, type);
				list.add(innerObj);
				string = string.substring(len);
			}
			else {
				if (preS.length() == 0 || getType(preS.substring(0,1)) == getType(string.substring(0,1))) {
					preS += string.substring(0, 1);
				}
				else {
					String content = preS;
					String type = getType(preS.substring(0, 1));
					InnerObj innerObj = new InnerObj(content, type);
					list.add(innerObj);
					preS = string.substring(0,1);
				}
				string = string.substring(1);
			}
		}
		if (preS.length() > 0) {
			String content = preS;
			String type = getType(preS.substring(0, 1));
			InnerObj innerObj = new InnerObj(content, type);
			list.add(innerObj);
		}
		
		String res = "";
		for (InnerObj innerObj : list) {
			if (innerObj.type.equals("Keyword")) {
				res += keywordsMap.get(innerObj.content);
			}
			else if (innerObj.type.equals("Chinese")) {
				res += encodingChinese(innerObj.content);
			}
			else if (innerObj.type.equals("English")) {
				res += encodingEnglish(innerObj.content);
			}
			else if (innerObj.type.equals("Number")) {
				res += encodingNumber(innerObj.content);
			}
			else if (innerObj.type.equals("Other")) {
				res += innerObj.content;
			}
		}
//		System.out.println("after encoding: " + res);
		return res;
	}
	
	public String encode(String tableName, String columnName, String value) {
		return this.encodingOneString(value);
	}
	
	// main for test
	public static void main(String[] args) {
		EncodingEngine engine = new EncodingEngine();
		engine.encodingOneString("刘德华子非鱼哈哈02341不知道Azfc“及,zf87999");
//		engine.test();
	}
	
}
