package com.teamvat.budgetme;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.teamvat.budgetme.BudgetReaderContract.BudgetEntry;

public class StatsTable extends Activity {
	
	TableLayout tl;
	BudgetDbHelper bDbHelper;
	SharedPreferences fieldValues;
	public static String[] statVariants = {
		"Stats (to Date)", "Stats (this Month)", "Stats (this Year)", "Stats (today)"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats_table);
		
		// setting up the spinner
		Spinner spinner = (Spinner) findViewById(R.id.tableSpinner);
		ArrayAdapter<String> populator = new ArrayAdapter<String>(this, R.layout.spinner_item, statVariants);
		spinner.setAdapter(populator);
		
		// trying to create a table
		tl = (TableLayout) findViewById(R.id.StatsTable);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				populateTable();
			}
			
			public void onNothingSelected(AdapterView<?> adapterView) {
				populateTable();
			}	
			
		});
		populateTable();
		
//		for(int i = 0; i < 30; i++) {
//			TableRow tr = new TableRow(this);
//			tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//			
//			for(int j = 0; j < 25; j++) {
//				TextView try1 = new TextView(this);
//				try1.setText("text1 - " + i + " - " + j);
//				try1.setPadding(20, 5, 20, 5);
//				try1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//				tr.addView(try1);
//			}
//			
//			
//			TextView try2 = new TextView(this);
//			try2.setText("text2 - " + i);
//			try2.setPadding(20, 5, 20, 5);
//			try2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//			tr.addView(try2);
//			
//			tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
//		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stats_table, menu);
		return true;
	}
	
	
	public void populateTable() {
		// clearing the existing table to create a fresh one
		tl.removeAllViews();
		// to get string size
		fieldValues = PreferenceManager.getDefaultSharedPreferences(this);
		Float stringSize = fieldValues.getFloat("stringSize", 18);
		
		// need to manually create the table row for column headings
		TableRow headings = new TableRow(this);
		headings.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		// for column Expense_Id
		TextView entryNumCol = new TextView(this);
		entryNumCol.setText("Entry");
		entryNumCol.setPadding(20, 5, 20, 5);
		entryNumCol.setTextSize(TypedValue.COMPLEX_UNIT_PX , stringSize + 4);
		entryNumCol.setTypeface(null, Typeface.BOLD_ITALIC);
		entryNumCol.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		headings.addView(entryNumCol);
		// for column Amount
		TextView amtCol = new TextView(this);
		amtCol.setText("Amount");
		amtCol.setPadding(20, 5, 20, 5);
		amtCol.setTextSize(TypedValue.COMPLEX_UNIT_PX ,stringSize + 4);
		amtCol.setTypeface(null, Typeface.BOLD_ITALIC);
		amtCol.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		headings.addView(amtCol);
		// for column Currency
		TextView curCol = new TextView(this);
		curCol.setText("Currency");
		curCol.setPadding(20, 5, 20, 5);
		curCol.setTextSize(TypedValue.COMPLEX_UNIT_PX ,stringSize + 4);
		curCol.setTypeface(null, Typeface.BOLD_ITALIC);
		curCol.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		headings.addView(curCol);
		// for column Category
		TextView catCol = new TextView(this);
		catCol.setText("Category");
		catCol.setPadding(20, 5, 20, 5);
		catCol.setTextSize(TypedValue.COMPLEX_UNIT_PX, stringSize + 4);
		catCol.setTypeface(null, Typeface.BOLD_ITALIC);
		catCol.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		headings.addView(catCol);
		// for column Date
		TextView dateCol = new TextView(this);
		dateCol.setText("Date");
		dateCol.setPadding(20, 5, 20, 5);
		dateCol.setTextSize(TypedValue.COMPLEX_UNIT_PX, stringSize + 4);
		dateCol.setTypeface(null, Typeface.BOLD_ITALIC);
		dateCol.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		headings.addView(dateCol);
		
		tl.addView(headings, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
		
		// getting the variant of stats demanded
    	Spinner statType = (Spinner) findViewById(R.id.tableSpinner);
    	String statDef = statType.getSelectedItem().toString();
		
    	// current date
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	Date curr_date = new Date();
    	String date = dateFormat.format(curr_date);
    	
    	// getting a readable database
    	bDbHelper = new BudgetDbHelper(getApplicationContext());
    	SQLiteDatabase db = bDbHelper.getReadableDatabase();
    	
		// creating the query, selecting everything
		String[] projection = {
				"*"
		};
		
		String selection;
    	String [] selectionArgs;
    	
    	if(statDef.equals(statVariants[1])) {
    		selection = "Expense_date like ?";
    		selectionArgs = new String[1];
    		selectionArgs[0] = date.substring(0, 7) + "%";
    	}
    	else if(statDef.equals(statVariants[2])) {
    		selection = "Expense_date like ?";
    		selectionArgs = new String[1];
    		selectionArgs[0] = date.substring(0, 3) + "%";
    	}
    	else if(statDef.equals(statVariants[3])) {
    		selection = "Expense_date like ?";
    		selectionArgs = new String[1];
    		selectionArgs[0] = date;
    	}
    	else {
    		selection = null;
    		selectionArgs = null;
    	}
		
		Cursor rowPointer = db.query(BudgetEntry.TABLE_NAME, 
									 projection, 
									 selection, 
									 selectionArgs, 
									 null, 
									 null, 
									 null);
		String[] columns = {
				BudgetEntry.COLUMN_NAME_EXPENSE_ID, BudgetEntry.COLUMN_NAME_EXPENSE_AMT,
				BudgetEntry.COLUMN_NAME_EXPENSE_CUR,
				BudgetEntry.COLUMN_NAME_EXPENSE_CAT, BudgetEntry.COLUMN_NAME_EXPENSE_DATE
		};
		while (rowPointer.moveToNext()) {
			TableRow row = new TableRow(this);
			for(int i = 0; i < columns.length; i++) {
				TextView entry = new TextView(this);
				entry.setText("" + rowPointer.getString(rowPointer.getColumnIndex(columns[i])));
				entry.setPadding(20, 5, 20, 5);
				entry.setTextSize(TypedValue.COMPLEX_UNIT_PX ,stringSize);
				entry.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
				row.addView(entry);
			}
			tl.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
		}
	}

}
