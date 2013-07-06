package com.teamvat.budgetme;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.teamvat.budgetme.BudgetReaderContract.BudgetEntry;

public class Configure extends Activity {
	
	BudgetDbHelper bDbHelper;
	AsyncTask<Void, Void, Void> backTask;
	Double convRate;
	Context context;
	private static Float monthlyBudget;
	private static Float yearlyBudget;
	private static Boolean dailyStats;
	private static String oldCurrency;
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
		
		context = this;
		// displaying the heading properly
		TextView configHead = (TextView) findViewById(R.id.configLabel);
		configHead.setTypeface(null, Typeface.BOLD_ITALIC);
		// setting the spinner values
		Spinner curSpinner = (Spinner) findViewById(R.id.curSpinner);
		ArrayAdapter<String> populator = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencies);
		populator.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		curSpinner.setAdapter(populator);
		curSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				setConvRate();
			}
			
			public void onNothingSelected(AdapterView<?> adapterView) {
				setConvRate();
			}
			
		});
		
		// using the code below the commented out version as I need to access the same sharedPreferences
//		configValues = this.getPreferences(Context.MODE_PRIVATE);
		configValues = PreferenceManager.getDefaultSharedPreferences(this);
		configEdit = configValues.edit();
		
		monthlyBudget = configValues.getFloat("monthlyBudget", 0.00f);
		yearlyBudget = monthlyBudget * 12;
		dailyStats = configValues.getBoolean("dailyStats", false);
		currency = configValues.getString("currency", "CAD");
		oldCurrency = configValues.getString("oldCurrency", "CAD");
		
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
		
		Spinner spinCur = (Spinner) findViewById(R.id.curSpinner);
//		Configure.oldCurrency = currency;
//		Configure.currency = spinCur.getSelectedItem().toString().substring(0, 3);
		int pos = spinCur.getSelectedItemPosition();
		if(!oldCurrency.equals(currency) && convRate != 0.0) {
			updateDatabase();
			Configure.monthlyBudget = (float) (Configure.monthlyBudget * convRate);
		}
		
		Configure.yearlyBudget = monthlyBudget * 12;
		
		CheckBox statCheck = (CheckBox) findViewById(R.id.dailyStatCheck);
		Configure.dailyStats = statCheck.isChecked();
		
		configEdit.putFloat("monthlyBudget", monthlyBudget);
		configEdit.putBoolean("dailyStats", dailyStats);
		configEdit.putString("currency", currency);
		configEdit.putString("oldCurrency", oldCurrency);
		configEdit.commit();
		
		Context context = getApplicationContext();
		String msg = "Configuration Saved";
		// msg stays for 3.5 sec instead of 2 sec
		int duration = Toast.LENGTH_SHORT;
		Toast.makeText(context, msg, duration).show();
		
		setFields();
		// add code to update database
		
	}
	
	// method to set the fields to current values
	public void setFields() {
		TextView monBudget = (TextView) findViewById(R.id.monBudgetText);
		monBudget.setText("" + new DecimalFormat("#.##").format(monthlyBudget) + " " + currency);
		
		TextView yrBudget = (TextView) findViewById(R.id.yrBudgetText);
		yrBudget.setText("" + new DecimalFormat("#.##").format(yearlyBudget) + " " + currency);
		
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
	
	public void setConvRate() {
		// getting the conversion rate
		backTask = new AsyncTask<Void, Void, Void> () {
				HashMap summary = new HashMap();
				ProgressDialog dialog;
				protected void onPreExecute() {
					dialog = new ProgressDialog(context);
					dialog.setTitle("Processing...");
					dialog.setMessage("Getting Conversion Rate");
					dialog.setCancelable(false);
					dialog.show();
					// getting selected currency
					Spinner spinCur = (Spinner) findViewById(R.id.curSpinner);
					Configure.oldCurrency = currency;
					Configure.currency = spinCur.getSelectedItem().toString().substring(0, 3);
				}
				
				@Override
				protected Void doInBackground(Void... params) {
					try {
							String url = "http://rate-exchange.appspot.com/currency?from=" +
									oldCurrency + "&to=" + currency + "&q=1";
							summary = UrlJsonParser.getData(url);
			            } catch (IOException e) {
			                // TODO Auto-generated catch block
			                e.printStackTrace();
			            }
			            return null;
			    }

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					dialog.dismiss();
			            
					if(summary.get("rate") == null) {
						Context context = getApplicationContext();
						String msg = "Server Error: Currency update failed";
						// msg stays for 3.5 sec instead of 2 sec
						int duration = Toast.LENGTH_SHORT;
						Toast.makeText(context, msg, duration).show();
						convRate = 0.0;
			        }
					else {
						String conv = "" + summary.get("rate");
						convRate = Double.parseDouble(conv);
			        }
					Context context = getApplicationContext();
					String msg = "Conv rate: " + convRate + " " + oldCurrency + "->" + currency;
					// msg stays for 3.5 sec instead of 2 sec
					int duration = Toast.LENGTH_SHORT;
					Toast.makeText(context, msg, duration).show();
			    }
		};
		backTask.execute();
	}
	
	public void updateDatabase() {
		// getting the database to modify
		bDbHelper = new BudgetDbHelper(getApplicationContext());
		SQLiteDatabase db = bDbHelper.getWritableDatabase();
		
		Cursor rowPointer = db.rawQuery("UPDATE " + BudgetEntry.TABLE_NAME + 
										" SET " + BudgetEntry.COLUMN_NAME_EXPENSE_AMT + " = " + 
										BudgetEntry.COLUMN_NAME_EXPENSE_AMT + " * ?, " + 
										BudgetEntry.COLUMN_NAME_EXPENSE_CUR + " = ? WHERE " +
										BudgetEntry.COLUMN_NAME_EXPENSE_CUR + " = ?", 
										new String[] {convRate.toString(), currency, oldCurrency});
		rowPointer.moveToFirst();
		rowPointer.close();
		
		Context context = getApplicationContext();
		String msg = "Database updated. Currency Changed";
		// msg stays for 3.5 sec instead of 2 sec
		int duration = Toast.LENGTH_SHORT;
		Toast.makeText(context, msg, duration).show();
		
		db.close();
	}
}
