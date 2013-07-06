package com.teamvat.budgetme;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
		
		// displaying the heading properly
		TextView curConvLabel = (TextView) findViewById(R.id.curConvLabel);
		curConvLabel.setTypeface(null, Typeface.BOLD_ITALIC);
		
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
	        	Context context = getApplicationContext();
	        	ConnectivityManager cm = (ConnectivityManager) 
	        			context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        	NetworkInfo ni = cm.getActiveNetworkInfo();
	        	if (ni != null) {
	        		if(ni.isConnected())
	        			convert();
	        	}
	        	else {
	        		String msg = "Please connect to the Internet";
	        		// msg stays for 3.5 sec instead of 2 sec
	        		int duration = Toast.LENGTH_SHORT;
	        		Toast.makeText(context, msg, duration).show();
	        	}
	        }
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.currency_converter, menu);
		return true;
	}
	
	// to switch currencies
	public void switchCurrency(View view) {
		int fromCurPos = fromSpinner.getSelectedItemPosition();
    	int toCurPos = toSpinner.getSelectedItemPosition();
    	fromSpinner.setSelection(toCurPos);
    	toSpinner.setSelection(fromCurPos);
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
	                e.printStackTrace();
//	                Context context = getApplicationContext();
//	        		String msg = "Data Retrieval failed.";
//	        		// msg stays for 3.5 sec instead of 2 sec
//	        		int duration = Toast.LENGTH_SHORT;
//	        		Toast.makeText(context, msg, duration).show();
	            }
	            return null;
	        }

	        @Override
	        protected void onPostExecute(Void result) {
	            super.onPostExecute(result);
	            
	            if(summary.get("rate") == null) {
	            	Context context = getApplicationContext();
	        		String msg = "Server Error";
	        		// msg stays for 3.5 sec instead of 2 sec
	        		int duration = Toast.LENGTH_SHORT;
	        		Toast.makeText(context, msg, duration).show();
	            }
	            else {
	            	// apparently, the json api can't handle same currency conversion
	            	// even though it's extremely easy
	            	// need a special case for it now
	            	if(fromCur.equals(toCur)) {
	            		convResText.setText("" + new DecimalFormat("#.##").format(amt) + " " + 
		            			fromCur + " = " + new DecimalFormat("#.##").format(amt) + " " + toCur);
			            convRateText.setText("@Rate: " + 
			            		new DecimalFormat("#.##").format(1.0));
	            	}
	            	else {
	            		convResText.setText("" + new DecimalFormat("#.##").format(amt) + " " + 
		            			fromCur + " = " + new DecimalFormat("#.##").format(summary.get("value")) + 
		            			" " + toCur);
			            convRateText.setText("@Rate: " + 
			            		new DecimalFormat("#.##").format(summary.get("rate")));
	            	}
	            }
	        }
	    };
	    backTask.execute();
	}
}
