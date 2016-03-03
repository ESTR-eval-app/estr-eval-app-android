package ca.stevenlyall.estrevalapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ca.stevenlyall.estrevalapp.interfaces.INotifyUpdateAvailableDelegate;

public class MainActivity extends AppCompatActivity {

	// url for web app to show in web view
	@SuppressWarnings("FieldCanBeLocal")
	private final String URL = "http://stevenlyall.me/evaluate";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!isNetworkConnected()) {
			showNoConnectionMessage();
		} else {
			VersionManager vm = new VersionManager(getApplicationContext());

			// to ensure all tablets in a large group use most recent version, checks version code against one provided on server
			vm.checkForNewVersion(new INotifyUpdateAvailableDelegate() {
				@Override
				public void notifyUpdateRequired() {
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
							startActivity(browserIntent);
							finish();
						}
					});
					builder.setTitle(R.string.update_required_title).setMessage(R.string.update_required_body);
					builder.create().show();
				}
			});

			showAppInWebView();

		}

	}

	private boolean isNetworkConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	private void showNoConnectionMessage() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// go to wifi settings
				MainActivity.this.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			}
		});
		builder.setTitle(R.string.no_connection).setMessage(R.string.no_connection_detail);

		builder.create().show();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void showAppInWebView() {
		WebView webView = (WebView) findViewById(R.id.webView);

		// enable js for web view
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUserAgentString(getString(R.string.user_agent_string));

		webView.setWebViewClient(new WebViewClient());
		webView.loadUrl(URL);

	}


}
