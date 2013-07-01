package com.teamvat.budgetme;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CurrencyConverter extends Activity {
	
	// list of currencies
	private static String[] currencies = {
		"CAD - Canadian Dollar", "INR - Indian Rupee", "USD - US Dollar", "CHF - Swiss Franc", "EUR - Euro", "GBP - British Pound", 
		"AED - Emirati Dirham", "KWD - Kuwaiti Dinar", "BHD - Bahraini Dinar", "OMR - Oman Rial", "LVL - Latvian Lat", 
		"JOD - Jordanian Dinar", "BSD - Bahamian Dollar", "CUP - Cuban Peso", "AUD - Australian Dollar"
	};
	// Async task to get online data
	AsyncTask<Void, Void, Void> backTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_currency_converter);
		
		Spinner fromSpinner = (Spinner) findViewById(R.id.fromCurSpin);
		Spinner toSpinner = (Spinner) findViewById(R.id.toCurSpin);
		ArrayAdapter<String> populator = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencies);
		populator.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fromSpinner.setAdapter(populator);
		toSpinner.setAdapter(populator);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.currency_converter, menu);
		return true;
	}

}
