package com.teamvat.budgetme;

import static com.googlecode.charts4j.Color.BLACK;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.PieChart;
import com.googlecode.charts4j.Slice;
import com.teamvat.budgetme.BudgetReaderContract.BudgetEntry;

public class ChartPie extends Activity {
	
	BudgetDbHelper bDbHelper;
	String screenSize;
	public static String[] statVariants = {
		"Stats (to Date)", "Stats (this Month)", "Stats (this Year)", "Stats (today)"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart_pie);
		// getting the layout type to determine size of chart
//		if(findViewById(R.id.pieLayout).getTag().equals("small")) {
//			screenSize = "small";
//		}
//		else if(findViewById(R.id.pieLayout).getTag().equals("medium")) {
//			screenSize = "medium";
//		}
//		else {
//			screenSize = "large";
//		}
		screenSize = findViewById(R.id.pieLayout).getTag().toString();
		Spinner spinner = (Spinner) findViewById(R.id.chartSpinner);
		ArrayAdapter<String> populator = new ArrayAdapter<String>(this, R.layout.spinner_item, statVariants);
//		populator.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(populator);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				createPie();
			}
			
			public void onNothingSelected(AdapterView<?> adapterView) {
				createPie();
			}
			
		});
		
		createPie();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chart_pie, menu);
		menu.clear();
		return true;
	}
	
	// creates the pie chart
	public void createPie() {
		// getting data
		bDbHelper = new BudgetDbHelper(getApplicationContext());
		SQLiteDatabase db = bDbHelper.getReadableDatabase();
		// getting total expenditure
		String[] projection = {
				"sum(" + BudgetEntry.COLUMN_NAME_EXPENSE_AMT + ") AS 'SUM'"
		};
		String selection;
    	String [] selectionArgs;
    	// getting the variant of stats demanded
    	Spinner statType = (Spinner) findViewById(R.id.chartSpinner);
    	String statDef = statType.getSelectedItem().toString();
    	// current date
    	// current date
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	Date curr_date = new Date();
    	String date = dateFormat.format(curr_date);
    	
    	// title for chart
    	String title;
    	
    	if(statDef.equals(statVariants[1])) {
    		selection = "Category like ? AND Expense_date like ?";
    		selectionArgs = new String[2];
    		selectionArgs[1] = date.substring(0, 7) + "%";
    		title = "Expenses for this month";
    	}
    	else if(statDef.equals(statVariants[2])) {
    		selection = "Category like ? AND Expense_date like ?";
    		selectionArgs = new String[2];
    		selectionArgs[1] = date.substring(0, 3) + "%";
    		title = "Expenses for this year";
    	}
    	else if(statDef.equals(statVariants[3])) {
    		selection = "Category like ? AND Expense_date like ?";
    		selectionArgs = new String[2];
    		selectionArgs[1] = date;
    		title = "Expenses for today";
    	}
    	else {
    		selection = "Category like ?";
        	selectionArgs = new String[1];
        	title = "Expenses to date";
    	}   
    	// expenses in different categories
    	Double[] catExpenses = new Double[AddExpense.categories.length];
    	Cursor rowPointer;
    	Double totalExpenses = 0.0;
    	for(int i = 0; i < AddExpense.categories.length; i++) {
    		catExpenses[i] = 0.0;
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
    		totalExpenses += catExpenses[i];
    	}
    	
    	// creating a pie chart as per selected data
    	Slice[] slices = new Slice[AddExpense.categories.length];
    	slices[0] = Slice.newSlice((int)Math.round(catExpenses[0]*100/totalExpenses), Color.newColor("EAEADB"), 
    								""+(int)Math.round(catExpenses[0]*100/totalExpenses), AddExpense.categories[0]);
    	slices[1] = Slice.newSlice((int)Math.round(catExpenses[1]*100/totalExpenses), Color.newColor("EBEE13"), 
				""+(int)Math.round(catExpenses[1]*100/totalExpenses), AddExpense.categories[1]);
    	slices[2] = Slice.newSlice((int)Math.round(catExpenses[2]*100/totalExpenses), Color.newColor("7ADF40"), 
    			""+(int)Math.round(catExpenses[2]*100/totalExpenses), AddExpense.categories[2]);
    	slices[3] = Slice.newSlice((int)Math.round(catExpenses[3]*100/totalExpenses), Color.newColor("34EACA"), 
				""+(int)Math.round(catExpenses[3]*100/totalExpenses), AddExpense.categories[3]);
    	slices[4] = Slice.newSlice((int)Math.round(catExpenses[4]*100/totalExpenses), Color.newColor("0E1FF2"), 
				""+(int)Math.round(catExpenses[4]*100/totalExpenses), AddExpense.categories[4]);
    	slices[5] = Slice.newSlice((int)Math.round(catExpenses[5]*100/totalExpenses), Color.newColor("B681D4"), 
				""+(int)Math.round(catExpenses[5]*100/totalExpenses), AddExpense.categories[5]);
    	slices[6] = Slice.newSlice((int)Math.round(catExpenses[6]*100/totalExpenses), Color.newColor("BD06A0"), 
				""+(int)Math.round(catExpenses[6]*100/totalExpenses), AddExpense.categories[6]);
    	slices[7] = Slice.newSlice((int)Math.round(catExpenses[7]*100/totalExpenses), Color.newColor("E32020"), 
				""+(int)Math.round(catExpenses[7]*100/totalExpenses), AddExpense.categories[7]);

        PieChart chart = GCharts.newPieChart(slices);

        if(screenSize.equals("small")) {
        	chart.setTitle(title, BLACK, 12);
        	chart.setSize(400, 210);
        }
        else if(screenSize.equals("medium")) {
        	chart.setTitle(title, BLACK, 16);
        	chart.setSize(425, 225);
        }
        else {
        	chart.setTitle(title, BLACK, 22);
        	chart.setSize(550, 250);
        }
        
        chart.setThreeD(true);
        String url = chart.toURLString();
        
        // for testing
        String expectedString = "http://chart.apis.google.com/chart?cht=p3" +
        		"&chs=500x200&chts=000000,16&chd=e:TNTNTNGa" +
        		"&chtt=A+Better+Web&chco=CACACA,DF7417,951800,01A1DB" +
        		"&chdl=Apple|Mozilla|Google|Microsoft&chl=Safari|Firefox|Chrome|Internet+Explorer";
        
        WebView webPie = (WebView) findViewById(R.id.webChart);
        webPie.loadUrl(url);
        webPie.getSettings().setBuiltInZoomControls(true);
        
        db.close();
	}

}
