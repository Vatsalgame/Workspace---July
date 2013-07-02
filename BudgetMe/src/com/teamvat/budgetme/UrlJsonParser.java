package com.teamvat.budgetme;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import android.os.AsyncTask;
import android.util.JsonReader;

public class UrlJsonParser {
	
	public static HashMap getData(String url)  throws IOException {
		HashMap summary = new HashMap();
		
		URL jsonUrl = new URL(url);
		URLConnection dc = jsonUrl.openConnection();
		
		JsonReader reader = new JsonReader(new InputStreamReader(dc.getInputStream()));
		try{
			summary = readMessages(reader);
		}
		finally {
			reader.close();
		}
		return summary;
	}
	
	public static HashMap readMessages(JsonReader reader) throws IOException {
		String from = "", to = "";
		double rate = 0.0, val = 0.0;
		
		reader.beginObject();
		while (reader.hasNext()) {
			String key = reader.nextName();
			if (key.equals("to")) {
				to = reader.nextString();
			}
			else if (key.equals("from")) {
				from = reader.nextString();
			}
			else if (key.equals("rate")) {
				rate = reader.nextDouble();
			}
			else if (key.equals("v")) {
				val = reader.nextDouble();
			}
			else {
				reader.skipValue();
			}
		}
		reader.endObject();
		
		HashMap summary = new HashMap();
		summary.put("to", to);
		summary.put("from", from);
		summary.put("rate", rate);
		summary.put("value", val);
		
		return summary;
	}
}
