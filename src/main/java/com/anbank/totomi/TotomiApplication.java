package com.anbank.totomi;

import com.anbank.totomi.handle.DbTransfer;

public class TotomiApplication {
	
	public static void main(String[] args) {
		DbTransfer transfer = new DbTransfer();
		transfer.handle();
	}
	
}
