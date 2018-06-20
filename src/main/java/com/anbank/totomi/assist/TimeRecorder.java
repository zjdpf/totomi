package com.anbank.totomi.assist;

import java.util.Date;

public class TimeRecorder {
	
	private int startTime;
	
	public TimeRecorder() {
		this.startTime = calTime();
	}
	
	private int calTime() {
		Date date = new Date();
		int day = date.getDate();
		int hour = date.getHours();
		int minutes = date.getMinutes();
		int seconds = date.getSeconds();
		return day * 24 * 3600 + hour * 3600 + minutes * 60 + seconds;
	}
	
	public String getTime() {
		int time = calTime();
		int delta = time - startTime;
		String res = "";
		int day = delta / (24 * 3600);
		if (day > 0) res += "" + day + "天";
		delta %= (24 * 3600);
		int hour = delta / 3600;
		int minutes = delta % 3600 / 60;
		int seconds = delta % 60;
		res += String.format("%02d小时%02d分%02d秒", hour, minutes, seconds);
		return res;
	}
}
