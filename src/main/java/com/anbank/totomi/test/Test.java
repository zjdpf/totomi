package com.anbank.totomi.test;

public class Test {
	public static String letterToH(String letter) {  
	    StringBuilder sb = new StringBuilder();  
	    for (int i = 0; i < letter.length(); i++) {  
	        char c = letter.charAt(i);  
	        sb.append(Integer.toHexString(c));  
	        sb.append(", ");  
	    }  
	    sb.deleteCharAt(sb.length() - 2);  
	    return sb.toString();  
	}  

	public static String hexTolLetter(String hex) {  
	    StringBuilder sb = new StringBuilder();  
	    String[] split = hex.split(",");  
	    for (String str : split) {  
	        int i = Integer.parseInt(str, 16);  
	        sb.append((char)i);  
	    }  
	    return sb.toString();  
	}  
	
	public static void main(String[] args) {
		
		System.out.println("a = " + ((int) (char)'a'));
		System.out.println("A = " + ((int) (char)'A'));
		
		String letter = "azAZ09";
		String hex = letterToH(letter);
		System.out.println(hex);
//		letter = hexTolLetter("6c6a");
//		System.out.println(letter);
		
//		String[] s = new String[] { "0" , "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
//		int st = 4 * 16 * 16 * 16 + 14 * 16 * 16;
//		int ed = 9 * 16 * 16 * 16 + 15 * 16 * 16 + 10 * 16 + 5;
//		int cnt = 0;
//		for (int i = st; i <= ed; i ++) {
//			int i1 = i / (16 * 16 * 16);
//			int i2 = i % (16 * 16 * 16) / (16 * 16);
//			int i3 = i % (16 * 16) / 16;
//			int i4 = i % 16;
//			String tmps = s[i1] + s[i2] + s[i3] + s[i4];
//			String res = hexTolLetter(tmps);
//			cnt ++;
//			System.out.print(res);
//			if (cnt % 40 == 0) {
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				System.out.println();
//			}
//		}
	}
	
}
