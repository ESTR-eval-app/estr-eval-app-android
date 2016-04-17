package ca.stevenlyall.evaln;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class AgreementActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agreement);

	}

	@Override
	protected void onResume() {
		super.onResume();

		ConnectionManager cm = new ConnectionManager(AgreementActivity.this);
		if (!cm.isNetworkConnected()) {
			cm.showNoConnectionMessage();
		} else {
			showEulaInWebView();
			setButtonListeners();
		}

	}

	/**
	 * Event handlers for accept and decline buttons.
	 */
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
	 * Shows the license agreement in the web view.
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void showEulaInWebView() {

		final String url = "http://stevenlyall.me/views/license_privacy/license.html";
		WebView webView = (WebView) findViewById(R.id.webView);

		// enable js for web view
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUserAgentString(getString(R.string.user_agent_string));

		webView.setWebViewClient(new WebViewClient());
		webView.loadUrl(url);

	}

	private void proceedToMainActivity() {
		Intent startMain = new Intent(getBaseContext(), MainActivity.class);
		startActivity(startMain);
		finish();

	}


}
