package com.teamvat.budgetme;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class HelpPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_page);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help_page, menu);
		return true;
	}

}
