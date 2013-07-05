package com.teamvat.budgetme;

import android.provider.BaseColumns;

public final class BudgetReaderContract {
	// To prevent BudgetReaderContract class from being instantiated
	public BudgetReaderContract(){}
	
	// Table BudgetTab
	public static abstract class BudgetEntry implements BaseColumns {
		
		// an ID constant(separate from SQLite's default) to keep track of the entries in the table
		public static int entryID = 1;
		
		public static final String TABLE_NAME = "BudgetTab";
		public static final String COLUMN_NAME_EXPENSE_ID = "Expense_Id";
		public static final String COLUMN_NAME_EXPENSE_AMT = "Amount";
		public static final String COLUMN_NAME_EXPENSE_CAT = "Category";
		public static final String COLUMN_NAME_EXPENSE_DATE = "Expense_Date";
		public static final String COLUMN_NAME_EXPENSE_CUR = "Expense_Currency";
		public static final String COLUMN_NAME_EXPENSE_DESC = "Expense_Description";
	}
	
}
