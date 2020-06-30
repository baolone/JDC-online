package com.jdc.app.util;

import java.text.DecimalFormat;
import java.text.ParseException;

import javafx.util.StringConverter;

public class TableFormatter extends StringConverter<Integer> {

	private static DecimalFormat DF = new DecimalFormat("#,##0");
	
	@Override
	public Integer fromString(String str) {
			try {
				if(!str.isEmpty()) {
					return DF.parse(str).intValue();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		return null;
	}

	@Override
	public String toString(Integer data) {
		if(null != data) {
			return DF.format(data); 
		}
		return null;
	}

}
