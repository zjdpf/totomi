package com.anbank.totomi.assist;

public class EncodingHelper {
	
//	private static final String[] s = new String[] { "0" , "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	
	public static String generateRandomChinese() {
		StringBuilder sb = new StringBuilder();
		int st = 4 * 16 * 16 * 16 + 14 * 16 * 16;
		int ed = 9 * 16 * 16 * 16 + 15 * 16 * 16 + 10 * 16 + 5 + 1;
		int num = st + (int) (Math.random() * (ed-st));
		sb.append((char) num);
		return sb.toString();
	}
	
	@Deprecated
	public static String generateRandomEnglish(int length) {
		String res = "";
		for (int i = 0; i < length; i ++) {
			boolean flag = Math.random() < 0.5;
			char c;
			if (flag) 
				c = (char) ((int) 'A' + (int) (Math.random() * 26));
			else
				c = (char) ((int) 'a' + (int) (Math.random() * 26));
			res += c;
		}
		return res;
	}
	
	public static String generateRandomNumber(int length) {
		String res = "";
		for (int i = 0; i < length; i ++) {
			int a = (int) (Math.random() * 10);
			res += a;
		}
		return res;
	}
	
	public static String getNextNumber(String numberString) {
		int length = numberString.length();
		int cnt = 1;
		String res = "";
		for (int i = length-1; i >= 0; i --) {
			char c = numberString.charAt(i);
			if (cnt == 0) {
				res = c + res;
			}
			else {
				int a = (int) c - (int) '0';
				if (a == 9) {
					cnt = 1;
					a = 0;
				}
				else {
					a ++;
					cnt = 0;
				}
				c = (char) ((int) '0' + a);
				res = c + res;
			}
		}
		return res;
	}
	
	
	public static void main(String[] args) {
		String num = generateRandomNumber(10);
		num = "99999999999";
		System.out.println("num = " + num);
		for (int i = 0; i < 12; i ++) {
			num = getNextNumber(num);
			System.out.println(num);
		}
	}
	
}
