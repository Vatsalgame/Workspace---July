package com.teamvat.budgetme;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.teamvat.budgetme.BudgetReaderContract.BudgetEntry;

public class RemoveExpense extends Activity {
	
	TableLayout tl;
	BudgetDbHelper bDbHelper;
	SharedPreferences fieldValues;
	SharedPreferences.Editor fieldEdit;
	public static String[] statVariants = {
		"Stats (to Date)", "Stats (this Month)", "Stats (this Year)", "Stats (today)"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remove_expense);
		
		// displaying the heading properly
		TextView remExpLabel = (TextView) findViewById(R.id.remExpLabel);
		remExpLabel.setTypeface(null, Typeface.BOLD_ITALIC);
		
		// setting up the spinner
		Spinner spinner = (Spinner) findViewById(R.id.statSpinner);
		ArrayAdapter<String> populator = new ArrayAdapter<String>(this, R.layout.spinner_item, statVariants);
		spinner.setAdapter(populator);
		
		// getting the layout to display the table in
		tl = (TableLayout) findViewById(R.id.remTableLay);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				populateTable();
			}
			
			public void onNothingSelected(AdapterView<?> adapterView) {
				populateTable();
			}	
			
		});
		populateTable();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.remove_expense, menu);
		return true;
	}
	
	public void removeExpense(View view) {
		// getting the context
		Context context = getApplicationContext();
		// getting the entry number
		EditText remEntryText = (EditText) findViewById(R.id.remEntryText);
		String remEntryNum = remEntryText.getText().toString();
		
		String msg = "Invalid Entry. Please choose an entry from the table";
		// msg stays for 3.5 sec instead of 2 sec
		int duration = Toast.LENGTH_SHORT;
		
		if (remEntryNum.equals("") || remEntryNum.equals("0")) {	
			Toast.makeText(context, msg, duration).show();
		}
		else {
			Integer entry = Integer.parseInt(remEntryNum);
			// getting the entry id for checking
			fieldValues = PreferenceManager.getDefaultSharedPreferences(this);
			fieldEdit = fieldValues.edit();
			BudgetEntry.entryID = fieldValues.getInt("entryID", 1);
			
			if (entry >= BudgetEntry.entryID) {
				Toast.makeText(context, msg, duration).show();
			}
			else {
				// getting a editable database
				bDbHelper = new BudgetDbHelper(getApplicationContext());
				SQLiteDatabase db = bDbHelper.getWritableDatabase();
				// deleting the selected expense
				Cursor rowPointer = db.rawQuery("DELETE FROM " + BudgetEntry.TABLE_NAME +
												" WHERE " + BudgetEntry.COLUMN_NAME_EXPENSE_ID +
												"=?", new String[] {entry.toString()});
				rowPointer.moveToFirst();
				rowPointer.close();
				
				
				// updating the database to fix the entry id's
				rowPointer = db.rawQuery("UPDATE " + BudgetEntry.TABLE_NAME +
										 " SET " + BudgetEntry.COLUMN_NAME_EXPENSE_ID + 
										 "=" + BudgetEntry.COLUMN_NAME_EXPENSE_ID + 
										 "-1 WHERE " + BudgetEntry.COLUMN_NAME_EXPENSE_ID +
										 ">?", new String[] {entry.toString()});
				rowPointer.moveToFirst();
				rowPointer.close();
				
				String msg2 = "Database updated. Expense Removed";
				// msg stays for 3.5 sec instead of 2 sec
				int duration2 = Toast.LENGTH_SHORT;
				Toast.makeText(context, msg2, duration2).show();
				
				populateTable();
				
				db.close();
				// decrementing ID value
		    	BudgetEntry.entryID--;
		    	fieldEdit.putInt("entryID", BudgetEntry.entryID);
		    	fieldEdit.commit();
				// refreshing the entry field
				remEntryText.setText("");
			}
		}
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
		entryNumCol.setTextSize(TypedValue.COMPLEX_UNIT_PX , stringSize);
		entryNumCol.setTypeface(null, Typeface.BOLD_ITALIC);
		entryNumCol.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		headings.addView(entryNumCol);
		// for column Amount
		TextView amtCol = new TextView(this);
		amtCol.setText("Amount");
		amtCol.setPadding(20, 5, 20, 5);
		amtCol.setTextSize(TypedValue.COMPLEX_UNIT_PX ,stringSize);
		amtCol.setTypeface(null, Typeface.BOLD_ITALIC);
		amtCol.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		headings.addView(amtCol);
		// for column Currency
		TextView curCol = new TextView(this);
		curCol.setText("Currency");
		curCol.setPadding(20, 5, 20, 5);
		curCol.setTextSize(TypedValue.COMPLEX_UNIT_PX ,stringSize);
		curCol.setTypeface(null, Typeface.BOLD_ITALIC);
		curCol.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		headings.addView(curCol);
		// for column Category
		TextView catCol = new TextView(this);
		catCol.setText("Category");
		catCol.setPadding(20, 5, 20, 5);
		catCol.setTextSize(TypedValue.COMPLEX_UNIT_PX, stringSize);
		catCol.setTypeface(null, Typeface.BOLD_ITALIC);
		catCol.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		headings.addView(catCol);
		// for column Date
		TextView dateCol = new TextView(this);
		dateCol.setText("Date");
		dateCol.setPadding(20, 5, 20, 5);
		dateCol.setTextSize(TypedValue.COMPLEX_UNIT_PX, stringSize);
		dateCol.setTypeface(null, Typeface.BOLD_ITALIC);
		dateCol.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		headings.addView(dateCol);
		// for column Description
		TextView descCol = new TextView(this);
		descCol.setText("Description");
		descCol.setPadding(20, 5, 20, 5);
		descCol.setTextSize(TypedValue.COMPLEX_UNIT_PX, stringSize);
		descCol.setTypeface(null, Typeface.BOLD_ITALIC);
		descCol.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		headings.addView(descCol);
		
		tl.addView(headings, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
		
		// getting the variant of stats demanded
    	Spinner statType = (Spinner) findViewById(R.id.statSpinner);
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
				BudgetEntry.COLUMN_NAME_EXPENSE_CUR, BudgetEntry.COLUMN_NAME_EXPENSE_CAT, 
				BudgetEntry.COLUMN_NAME_EXPENSE_DATE, BudgetEntry.COLUMN_NAME_EXPENSE_DESC
		};
		while (rowPointer.moveToNext()) {
			TableRow row = new TableRow(this);
			for(int i = 0; i < columns.length; i++) {
				TextView entry = new TextView(this);
				entry.setText("" + rowPointer.getString(rowPointer.getColumnIndex(columns[i])));
				entry.setPadding(20, 5, 20, 5);
				entry.setTextSize(TypedValue.COMPLEX_UNIT_PX ,stringSize - 4);
				entry.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
				row.addView(entry);
			}
			tl.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
		}
		db.close();
	}

}

