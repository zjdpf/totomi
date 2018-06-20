package com.anbank.totomi.po;

public class TotomiTableColumn {
	
	private String columnName;
	private String typeName;
	private int columnSize;
	private int decimalDigits;
	
	public TotomiTableColumn(String columnName, String typeName, int columnSize, int decimalDigits) {
		this.columnName = columnName;
		this.typeName = typeName;
		this.columnSize = columnSize;
		this.decimalDigits = decimalDigits;
	}
	
	@Override
	public String toString() {
		return columnName + ", " + typeName + ", " + columnSize + ", " + decimalDigits;
	}
	
	// getters and setters
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public int getColumnSize() {
		return columnSize;
	}
	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}
	public int getDecimalDigits() {
		return decimalDigits;
	}
	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}
	
}
