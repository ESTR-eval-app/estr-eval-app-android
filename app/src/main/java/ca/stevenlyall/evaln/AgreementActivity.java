package ca.stevenlyall.evaln;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AgreementActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agreement);

		loadText();
		setButtonListeners();

	}

	private void loadText() {
		InputStream inputStream = getApplicationContext().getResources().openRawResource(R.raw.eula);

		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;
		StringBuilder text = new StringBuilder();

		try {
			while ((line = buffreader.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
		} catch (IOException e) {
			Log.e("eula", "Couldn't read license file from resources");
		}

		TextView textView = (TextView) findViewById(R.id.licenseText);
		textView.setText(text);
	}


	private void setButtonListeners() {
		Button declineButton = (Button) findViewById(R.id.declineEULAButton);
		declineButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// quit
				finish();
			}
		});

		Button acceptButton = (Button) findViewById(R.id.acceptEULAButton);
		acceptButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				VersionManager vm = new VersionManager(getApplicationContext());

				// set accepted and allow access to rest of app
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean(vm.getCurrentEULAKey(), true);
				editor.commit();

				proceedToMainActivity();
			}
		});
	}

	/**
	 * once license has been accepted
	 */
	private void proceedToMainActivity() {
		Intent startMain = new Intent(getBaseContext(), MainActivity.class);
		startActivity(startMain);
		finish();

	}

}
