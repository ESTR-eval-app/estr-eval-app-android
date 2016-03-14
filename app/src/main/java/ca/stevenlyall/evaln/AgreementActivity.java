package ca.stevenlyall.evaln;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
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

		if (!isNetworkConnected()) {
			showNoConnectionMessage();
		} else {
			showEulaInWebView();
			setButtonListeners();
		}

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

	// TODO move these two methods to separate class, used in both agreement activity and main
	private boolean isNetworkConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	private void showNoConnectionMessage() {
		AlertDialog.Builder builder = new AlertDialog.Builder(AgreementActivity.this);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// go to wifi settings
				AgreementActivity.this.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			}
		});
		builder.setTitle(R.string.no_connection).setMessage(R.string.no_connection_detail);

		builder.create().show();
	}

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

}
