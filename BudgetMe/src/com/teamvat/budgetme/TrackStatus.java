package com.teamvat.budgetme;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.teamvat.budgetme.BudgetReaderContract.BudgetEntry;

public class TrackStatus extends Activity {
	
	BudgetDbHelper bDbHelper;
	SharedPreferences fieldValues;
	SharedPreferences.Editor fieldEdit;
	public static String[] statVariants = {
		"Stats (to Date)", "Stats (this Month)", "Stats (this Year)", "Stats (today)"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_status);
		
		Spinner spinner = (Spinner) findViewById(R.id.statLabel);
		ArrayAdapter<String> populator = new ArrayAdapter<String>(this, R.layout.spinner_item, statVariants);
//		populator.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(populator);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				updateStats();
			}
			
			public void onNothingSelected(AdapterView<?> adapterView) {
				updateStats();
			}
			
		});
		updateStats();
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		updateStats();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateStats();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.track_status, menu);
		return true;
	}
	
	// for testing webview
	public void goToPie(View view) {
		Context context = getApplicationContext();
    	ConnectivityManager cm = (ConnectivityManager) 
    			context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo ni = cm.getActiveNetworkInfo();
    	if (ni != null) {
    		if(ni.isConnected()) {
    			Intent switchToPie = new Intent(this, ChartPie.class);
    			startActivity(switchToPie);
    		}
    	}
    	else {
    		String msg = "Please connect to the Internet";
    		// msg stays for 3.5 sec instead of 2 sec
    		int duration = Toast.LENGTH_SHORT;
    		Toast.makeText(context, msg, duration).show();
    	}
	}	
	
	// for testing stats table
	public void goToTable(View view) {
		Intent switchToTable = new Intent(this, StatsTable.class);
		startActivity(switchToTable);
	}
	
	public void updateStats() {
		// getting the currency
		fieldValues = PreferenceManager.getDefaultSharedPreferences(this);
		fieldEdit = fieldValues.edit();
		String currency = fieldValues.getString("currency", "CAD");
		// getting a readable database
		bDbHelper = new BudgetDbHelper(getApplicationContext());
		SQLiteDatabase db = bDbHelper.getReadableDatabase();
		// write a similar code as following for different categories
		// update it to show sum as per the stat type specified
		String[] projection = {
				"sum(" + BudgetEntry.COLUMN_NAME_EXPENSE_AMT + ") AS 'SUM'"
		};
		Cursor rowPointer = db.query(BudgetEntry.TABLE_NAME, 
				 projection, 
				 null, 
				 null, 
				 null, 
				 null, 
				 null);
		Double totalExpense = 0.00;
		while (rowPointer.moveToNext()) {
    		totalExpense = rowPointer.getDouble(rowPointer.getColumnIndex("SUM"));
    	}
		TextView totalExpenses = (TextView) findViewById(R.id.totalExpense);
    	totalExpenses.setText("Total Expenditure (to date): " + 
    							new DecimalFormat("#.##").format(totalExpense) + " " + currency);
    	
    	TextView catExps = (TextView) findViewById(R.id.catWiseExp);
    	catExps.setText("Category-wise expenses (in " + currency + ")");
    	
    	// to populate all the views
    	TextView billExpenses = (TextView) findViewById(R.id.catBills);
    	TextView eduExpenses = (TextView) findViewById(R.id.catEducation);
    	TextView foodExpenses = (TextView) findViewById(R.id.catFood);
    	TextView gasExpenses = (TextView) findViewById(R.id.catGas);
    	TextView groExpenses = (TextView) findViewById(R.id.catGroceries);
    	TextView rentExpenses = (TextView) findViewById(R.id.catRent);
    	TextView repExpenses = (TextView) findViewById(R.id.catRepairs);
    	TextView othExpenses = (TextView) findViewById(R.id.catOthers);
    	
    	fieldEdit.putFloat("stringSize", billExpenses.getTextSize());
    	fieldEdit.commit();
    	
    	TextView[] catTextList = {
    			billExpenses, eduExpenses, foodExpenses, gasExpenses, groExpenses, rentExpenses, repExpenses, othExpenses
    	};
    	Double[] catExpenses = new Double[AddExpense.categories.length];
    	
    	// getting the variant of stats demanded
    	Spinner statType = (Spinner) findViewById(R.id.statLabel);
    	String statDef = statType.getSelectedItem().toString();
    	// current date
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	Date curr_date = new Date();
    	String date = dateFormat.format(curr_date);
    	// insert args
    	//
    	String selection;
    	String [] selectionArgs;
    	
    	if(statDef.equals(statVariants[1])) {
    		selection = "Category like ? AND Expense_date like ?";
    		selectionArgs = new String[2];
    		selectionArgs[1] = date.substring(0, 7) + "%";
    	}
    	else if(statDef.equals(statVariants[2])) {
    		selection = "Category like ? AND Expense_date like ?";
    		selectionArgs = new String[2];
    		selectionArgs[1] = date.substring(0, 3) + "%";
    	}
    	else if(statDef.equals(statVariants[3])) {
    		selection = "Category like ? AND Expense_date like ?";
    		selectionArgs = new String[2];
    		selectionArgs[1] = date;
    	}
    	else {
    		selection = "Category like ?";
        	selectionArgs = new String[1];
    	}   	
    	
    	for(int i = 0; i < AddExpense.categories.length; i++) {
    		catExpenses[i] = 0.00;
    		selectionArgs[0] = AddExpense.categories[i];
    		rowPointer = db.query(BudgetEntry.TABLE_NAME, 
					 			  projection, 
					 			  selection, 
					 			  selectionArgs, 
					 			  null, 
					 			  null, 
					 			  null);
    		while (rowPointer.moveToNext()) {
        		catExpenses[i] = rowPointer.getDouble(rowPointer.getColumnIndex("SUM"));
        	}
    		catTextList[i].setText(AddExpense.categories[i] + ": " + new DecimalFormat("#.##").format(catExpenses[i]));    		
    	}
	}

}
