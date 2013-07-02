package com.teamvat.budgetme;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Configure extends Activity {
	
	// monthly budget with a default value of 0.0;
	private static Float monthlyBudget;
	private static Float yearlyBudget;
	private static Boolean dailyStats;
	private static String currency;
	private static String[] currencies = {
		"CAD - Canadian Dollar", "INR - Indian Rupee", "USD - US Dollar", "CHF - Swiss Franc", "EUR - Euro", "GBP - British Pound", 
		"AED - Emirati Dirham", "KWD - Kuwaiti Dinar", "BHD - Bahraini Dinar", "OMR - Oman Rial", "LVL - Latvian Lat", 
		"JOD - Jordanian Dinar", "BSD - Bahamian Dollar", "CUP - Cuban Peso", "AUD - Australian Dollar"
	};
	
	SharedPreferences configValues;
	SharedPreferences.Editor configEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configure);
		// setting the spinner values
		Spinner curSpinner = (Spinner) findViewById(R.id.curSpinner);
		ArrayAdapter<String> populator = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencies);
		populator.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		curSpinner.setAdapter(populator);
		
		// using the code below the commented out version as I need to access the same sharedPreferences
//		configValues = this.getPreferences(Context.MODE_PRIVATE);
		configValues = PreferenceManager.getDefaultSharedPreferences(this);
		configEdit = configValues.edit();
		
		monthlyBudget = configValues.getFloat("monthlyBudget", 0.0f);
		yearlyBudget = monthlyBudget * 12;
		dailyStats = configValues.getBoolean("dailyStats", false);
		currency = configValues.getString("currency", "CAD");
		
		Context context = getApplicationContext();
    	ConnectivityManager cm = (ConnectivityManager) 
    			context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo ni = cm.getActiveNetworkInfo();
    	if (ni == null) {
    		curSpinner.setEnabled(false);
    	}
    	else {
    		if(!ni.isConnected()) {
    			curSpinner.setEnabled(false);
    		}
    	}
		
		setFields();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.configure, menu);
		return true;
	}
	
	// if activity is stopped and started again
		@Override
		public void onRestart() {
			super.onRestart();
			Spinner curSpinner = (Spinner) findViewById(R.id.curSpinner);
			Context context = getApplicationContext();
	    	ConnectivityManager cm = (ConnectivityManager) 
	    			context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    	NetworkInfo ni = cm.getActiveNetworkInfo();
	    	if (ni == null) {
	    		curSpinner.setEnabled(false);
	    	}
	    	else if(!ni.isConnected()) {
	    		curSpinner.setEnabled(false);
	    	}
	    	else {
	    		curSpinner.setEnabled(true);
	    	}
		}
		
	// if activity is paused and started again
		@Override
		public void onResume() {
			super.onResume();
			Spinner curSpinner = (Spinner) findViewById(R.id.curSpinner);
			Context context = getApplicationContext();
			ConnectivityManager cm = (ConnectivityManager) 
			    			context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if (ni == null) {
				curSpinner.setEnabled(false);
			}
			else if(!ni.isConnected()) {
				curSpinner.setEnabled(false);  
			}
			else {
				curSpinner.setEnabled(true);
			}
		}
	
	// method to update the configurations
	public void saveConfig(View view) {
		// getting the values to save
		EditText monBudget = (EditText) findViewById(R.id.monBudgetText);
		String monBudgetText = monBudget.getText().toString();
		String monBudgetNum;
		if(monBudgetText.length() > 4 && 
			monBudgetText.substring(monBudgetText.length() - 4, monBudgetText.length() - 3).equals(" ")) {
			monBudgetNum = monBudgetText.substring(0, monBudgetText.length() - 4);
		}
		else {
			monBudgetNum = monBudgetText;
		}
		Configure.monthlyBudget = Float.parseFloat(monBudgetNum);
		Configure.yearlyBudget = monthlyBudget * 12;
		Spinner spinCur = (Spinner) findViewById(R.id.curSpinner);
		Configure.currency = spinCur.getSelectedItem().toString().substring(0, 3);
		CheckBox statCheck = (CheckBox) findViewById(R.id.dailyStatCheck);
		Configure.dailyStats = statCheck.isChecked();
		
		configEdit.putFloat("monthlyBudget", monthlyBudget);
		configEdit.putBoolean("dailyStats", dailyStats);
		configEdit.putString("currency", currency);
		configEdit.commit();
		
		Context context = getApplicationContext();
		String msg = "Configuration Saved";
		// msg stays for 3.5 sec instead of 2 sec
		int duration = Toast.LENGTH_SHORT;
		Toast.makeText(context, msg, duration).show();
		
		setFields();
	}
	
	// method to set the fields to current values
	public void setFields() {
		TextView monBudget = (TextView) findViewById(R.id.monBudgetText);
		monBudget.setText("" + monthlyBudget + " " + currency);
		
		TextView yrBudget = (TextView) findViewById(R.id.yrBudgetText);
		yrBudget.setText("" + yearlyBudget + " " + currency);
		
		CheckBox statCheck = (CheckBox) findViewById(R.id.dailyStatCheck);
		statCheck.setChecked(dailyStats);
		
		int index = 0 ;
		for(int i = 0; i < currencies.length; i++) {
			//use .equals
			if(currency.equals(currencies[i].substring(0, 3))) {
				index = i;
			}
		}
		Spinner spinCur = (Spinner) findViewById(R.id.curSpinner);
		spinCur.setSelection(index);
	}
}
