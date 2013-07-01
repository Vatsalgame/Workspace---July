package com.example.velapan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HomePage extends Activity {
	
	AsyncTask<Void, Void, Void> mTask;
	String jsonString;

	String url = "http://rate-exchange.appspot.com/currency?from=USD&to=EUR&q=1";

	Button b;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);
		
		final TextView tv = (TextView) findViewById(R.id.showJSON);
		b = (Button) findViewById(R.id.button1);
		final TextView fromCur = (TextView) findViewById(R.id.fromCur);
		final TextView toCur = (TextView) findViewById(R.id.toCur);
		final TextView rateText = (TextView) findViewById(R.id.rateText);
		final TextView valText = (TextView) findViewById(R.id.valText);
		List summary = new ArrayList();
		
		
		mTask = new AsyncTask<Void, Void, Void> () {
			HashMap summary = new HashMap();
	        @Override
	        protected Void doInBackground(Void... params) {
	            try {
	            	
//	                jsonString = getJsonFromServer(url);
	            	summary = setData(url);
	            	
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	            return null;
	        }

	        @Override
	        protected void onPostExecute(Void result) {
	            super.onPostExecute(result);

//	            tv.setText(jsonString);
	            fromCur.setText("From: " + summary.get("from"));
            	toCur.setText("To: " + summary.get("to"));
            	rateText.setText("Rate: " + summary.get("rate"));
            	valText.setText("Value: " + summary.get("value"));
	        }

	    };

	    b.setOnClickListener(new OnClickListener() {

	        public void onClick(View v) {
	            mTask.execute();
	        }
	    });
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home_page, menu);
		return true;
	}
	
	public static String getJsonFromServer(String url) throws IOException {

	    BufferedReader inputStream = null;

	    URL jsonUrl = new URL(url);
	    URLConnection dc = jsonUrl.openConnection();

	    dc.setConnectTimeout(5000);
	    dc.setReadTimeout(5000);

	    inputStream = new BufferedReader(new InputStreamReader(
	            dc.getInputStream()));

	    // read the JSON results into a string
	    String jsonResult = inputStream.readLine();
	    return jsonResult;
	}
	
	public HashMap setData(String url)  throws IOException {
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
	
	public HashMap readMessages(JsonReader reader) throws IOException {
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
	
	public void getData(View view) {
		TextView viewText = (TextView) findViewById(R.id.showJSON);
		viewText.setText("works");
	}

}
