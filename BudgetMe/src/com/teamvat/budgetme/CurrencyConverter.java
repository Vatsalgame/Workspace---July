package com.teamvat.budgetme;

import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CurrencyConverter extends Activity {
	
	// list of currencies
	private static String[] currencies = {
		"CAD - Canadian Dollar", "INR - Indian Rupee", "USD - US Dollar", "CHF - Swiss Franc", "EUR - Euro", "GBP - British Pound", 
		"AED - Emirati Dirham", "KWD - Kuwaiti Dinar", "BHD - Bahraini Dinar", "OMR - Oman Rial", "LVL - Latvian Lat", 
		"JOD - Jordanian Dinar", "BSD - Bahamian Dollar", "CUP - Cuban Peso", "AUD - Australian Dollar"
	};
	// Async task to get online data
	AsyncTask<Void, Void, Void> backTask;
	String url, fromCur, toCur;
	Button convert;
	Double amt;
	Spinner fromSpinner, toSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_currency_converter);
		
		fromSpinner = (Spinner) findViewById(R.id.fromCurSpin);
		toSpinner = (Spinner) findViewById(R.id.toCurSpin);
		ArrayAdapter<String> populator = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencies);
		populator.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fromSpinner.setAdapter(populator);
		toSpinner.setAdapter(populator);
		
		// setting up an async task to get currency conversion
		convert = (Button) findViewById(R.id.convButton);
//		final TextView amtText = (TextView) findViewById(R.id.enterAmtText);
//		final TextView convResText = (TextView) findViewById(R.id.convResText);
//		final TextView convRateText = (TextView) findViewById(R.id.convRateText);
//		
//		backTask = new AsyncTask<Void, Void, Void> () {
//			HashMap summary = new HashMap();
//	        @Override
//	        protected Void doInBackground(Void... params) {
//	            try {
//	            	amt = Double.parseDouble(amtText.getText().toString());
//	            	fromCur = fromSpinner.getSelectedItem().toString().substring(0, 3);
//	            	toCur = toSpinner.getSelectedItem().toString().substring(0, 3);
//	            	url = "http://rate-exchange.appspot.com/currency?from=" +
//	            			fromCur + "&to=" + toCur + "&q=" + amt;
////	                jsonString = getJsonFromServer(url);
//	            	summary = UrlJsonParser.getData(url);
//	            	
//	            } catch (IOException e) {
//	                // TODO Auto-generated catch block
////	                e.printStackTrace();
//	                Context context = getApplicationContext();
//	        		String msg = "Data Retrieval failed.";
//	        		// msg stays for 3.5 sec instead of 2 sec
//	        		int duration = Toast.LENGTH_SHORT;
//	        		Toast.makeText(context, msg, duration).show();
//	            }
//	            return null;
//	        }
//
//	        @Override
//	        protected void onPostExecute(Void result) {
//	            super.onPostExecute(result);
//	            convResText.setText("" + summary.get("value"));
//	            convRateText.setText("" + summary.get("rate"));
//	        }
//
//	    };
//
	    convert.setOnClickListener(new OnClickListener() {

	        public void onClick(View v) {
//	            backTask.execute();
	        	convert();
	        }
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.currency_converter, menu);
		return true;
	}
	
	public void convert() {
		final TextView amtText = (TextView) findViewById(R.id.enterAmtText);
		final TextView convResText = (TextView) findViewById(R.id.convResText);
		final TextView convRateText = (TextView) findViewById(R.id.convRateText);
		
		backTask = new AsyncTask<Void, Void, Void> () {
			HashMap summary = new HashMap();
	        @Override
	        protected Void doInBackground(Void... params) {
	            try {
	            	amt = Double.parseDouble(amtText.getText().toString());
	            	fromCur = fromSpinner.getSelectedItem().toString().substring(0, 3);
	            	toCur = toSpinner.getSelectedItem().toString().substring(0, 3);
	            	url = "http://rate-exchange.appspot.com/currency?from=" +
	            			fromCur + "&to=" + toCur + "&q=" + amt;
//	                jsonString = getJsonFromServer(url);
	            	summary = UrlJsonParser.getData(url);
	            	
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
//	                e.printStackTrace();
	                Context context = getApplicationContext();
	        		String msg = "Data Retrieval failed.";
	        		// msg stays for 3.5 sec instead of 2 sec
	        		int duration = Toast.LENGTH_SHORT;
	        		Toast.makeText(context, msg, duration).show();
	            }
	            return null;
	        }

	        @Override
	        protected void onPostExecute(Void result) {
	            super.onPostExecute(result);
	            convResText.setText("" + summary.get("value"));
	            convRateText.setText("" + summary.get("rate"));
	        }

	    };
	    backTask.execute();
	}
}
