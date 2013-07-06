package com.teamvat.budgetme;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class Credits extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credits);
		// displaying heading properly
		TextView devLabel = (TextView) findViewById(R.id.devLabel);
		devLabel.setTypeface(null, Typeface.BOLD_ITALIC);
		
		TextView testLabel = (TextView) findViewById(R.id.testLabel);
		testLabel.setTypeface(null, Typeface.BOLD_ITALIC);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.credits, menu);
		return true;
	}

}
