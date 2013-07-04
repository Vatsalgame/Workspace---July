package com.teamvat.budgetme;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.teamvat.budgetme.BudgetReaderContract.BudgetEntry;

public class AddExpense extends Activity {
	
	BudgetDbHelper bDbHelper;
	SharedPreferences fieldValues;
	SharedPreferences.Editor fieldEdit;
	public static String[] categories = {
		"Bills", "Education", "Food", "Gas", "Groceries", "Rent", "Repairs", "Others"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_expense);
		// Populating the spinner
		Spinner spinner = (Spinner) findViewById(R.id.cat_spinner);
		ArrayAdapter<String> populator = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
		populator.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(populator);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_expense, menu);
		return true;
	}
	
	// called when user clicks on Add Expense
	public void enterExpense(View view) {
		bDbHelper = new BudgetDbHelper(getApplicationContext());
		SQLiteDatabase db = bDbHelper.getWritableDatabase();
		fieldValues = PreferenceManager.getDefaultSharedPreferences(this);
		fieldEdit = fieldValues.edit();
		
		// getting the correct ID
		BudgetEntry.entryID = fieldValues.getInt("entryID", 1);
		
		// getting the currency
		String currency = fieldValues.getString("currency", "CAD");
		
		// getting the amount
		EditText editAmt = (EditText) findViewById(R.id.getAmtText);
    	Double amt = Double.parseDouble(editAmt.getText().toString());
    	
    	// getting the category
    	Spinner spinCat = (Spinner) findViewById(R.id.cat_spinner);
    	String category = spinCat.getSelectedItem().toString();
    	
    	//creating the new expense row to be added
    	ContentValues values = new ContentValues();
    	// change to get apt values
    	values.put(BudgetEntry.COLUMN_NAME_EXPENSE_ID, BudgetEntry.entryID);
    	values.put(BudgetEntry.COLUMN_NAME_EXPENSE_AMT, amt);
    	values.put(BudgetEntry.COLUMN_NAME_EXPENSE_CUR, currency);
    	values.put(BudgetEntry.COLUMN_NAME_EXPENSE_CAT, category);
    	
    	// readying the date
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	Date curr_date = new Date();
    	values.put(BudgetEntry.COLUMN_NAME_EXPENSE_DATE, dateFormat.format(curr_date));
    	
    	//inserting the new expense
    	long newRowId = db.insert(BudgetEntry.TABLE_NAME, null, values);
    	
    	// incrementing ID value
    	BudgetEntry.entryID++;
    	fieldEdit.putInt("entryID", BudgetEntry.entryID);
    	fieldEdit.commit();
    	
    	Context context = getApplicationContext();
		String msg = "Expense Added";
		// msg stays for 3.5 sec instead of 2 sec
		int duration = Toast.LENGTH_SHORT;
		Toast.makeText(context, msg, duration).show();
	}

}
