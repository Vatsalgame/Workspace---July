package com.teamvat.budgetme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;




public class BudgetDbHelper extends SQLiteOpenHelper{
	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "BudgetReader.db";
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String REAL_TYPE = " REAL";
	private static final String INT_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	private static final String CREATE_TABLE = 
			"CREATE TABLE IF NOT EXISTS " + BudgetReaderContract.BudgetEntry.TABLE_NAME + " (" + 
			BudgetReaderContract.BudgetEntry._ID + " INTEGER PRIMARY KEY," +
			BudgetReaderContract.BudgetEntry.COLUMN_NAME_EXPENSE_ID + INT_TYPE + COMMA_SEP +
			BudgetReaderContract.BudgetEntry.COLUMN_NAME_EXPENSE_AMT + REAL_TYPE + COMMA_SEP +
			BudgetReaderContract.BudgetEntry.COLUMN_NAME_EXPENSE_CUR + TEXT_TYPE + COMMA_SEP +
			BudgetReaderContract.BudgetEntry.COLUMN_NAME_EXPENSE_CAT + TEXT_TYPE + COMMA_SEP +
			BudgetReaderContract.BudgetEntry.COLUMN_NAME_EXPENSE_DATE + TEXT_TYPE + COMMA_SEP +
			BudgetReaderContract.BudgetEntry.COLUMN_NAME_EXPENSE_DESC + TEXT_TYPE + ");";
	
	private static final String DELETE_TABLE = 
			"DROP TABLE IF EXISTS " + BudgetReaderContract.BudgetEntry.TABLE_NAME;
	
	BudgetDbHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	// Not the final version of onCreate
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}
	
	// Not completely sure what functionality onUpgrade should have yet
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DELETE_TABLE);
		onCreate(db);
	}
	
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}