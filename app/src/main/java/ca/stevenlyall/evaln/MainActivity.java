package ca.stevenlyall.evaln;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ca.stevenlyall.evaln.interfaces.INotifyUpdateAvailableDelegate;

public class MainActivity extends AppCompatActivity {

	// url for web app to show in web view
	@SuppressWarnings("FieldCanBeLocal")
	private final String URL = "http://stevenlyall.me/evaluate";

	private VersionManager vm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		vm = new VersionManager(getApplicationContext());

		if (!vm.isEULAAccepted()) {
			Intent showEula = new Intent(this, AgreementActivity.class);
			startActivity(showEula);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!vm.isEULAAccepted()) {
			finish();
		}

		ConnectionManager cm = new ConnectionManager(MainActivity.this);
		if (!cm.isNetworkConnected()) {
			cm.showNoConnectionMessage();
		} else {

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
