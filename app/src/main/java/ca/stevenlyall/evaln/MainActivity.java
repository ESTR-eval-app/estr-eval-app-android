package ca.stevenlyall.evaln;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import ca.stevenlyall.evaln.interfaces.INotifyUpdateAvailableDelegate;

public class MainActivity extends AppCompatActivity {

	private static final String DEFAULT_URL = "http://crossdisciplinary.tru.ca/evaluate";
	private VersionManager vm;
	private int longClicks;
	/**
	 * Handle long clicks in the web view.
	 * Five of them unlock the web app url setting dialog.
	 */
	View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			if (longClicks == 5) {
				showSetURLDialog();
			}
			longClicks++;
			return false;
		}
	};

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

		longClicks = 0;

		if (!vm.isEULAAccepted()) {
			finish();
		}

		ConnectionManager cm = new ConnectionManager(MainActivity.this);
		if (!cm.isNetworkConnected()) {
			cm.showNoConnectionMessage();
		} else {
			checkForNewAppVersion();
			showAppInWebView();
		}

	}

	/**
	 * Checks to see if a new version of the Android app is available.
	 */
	private void checkForNewAppVersion() {
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
	}

	/**
	 * Shows the evaluation web app in the web view.
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void showAppInWebView() {
		WebView webView = (WebView) findViewById(R.id.webView);

		// enable js for web view
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUserAgentString(getString(R.string.user_agent_string));

		webView.setWebViewClient(new WebViewClient());
		String url = getURL();
		webView.loadUrl(url);

		webView.setOnLongClickListener(longClickListener);

	}

	/**
	 * Shows dialog for changing the web app url.
	 */
	private void showSetURLDialog() {
		longClicks = 0;
		final EditText input = new EditText(this);
		AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("Set URL").setView(input).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String entered = String.valueOf(input.getText());
				boolean success = setURLSharedPreference(entered);
				String message;
				if (!success) {
					message = "Change not saved.";
				} else {
					message = "URL set to " + entered;
				}
				Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		dialog.show();
	}

	/**
	 * Retrieve the url for the web app from shared preference.
	 *
	 * @return the URL of the mobile web app
	 */
	private String getURL() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		// change default url here
		return preferences.getString("URL", DEFAULT_URL);
	}

	/**
	 * Updates the web app url in shared preferences
	 *
	 * @param newURL the URL for the web app
	 * @return true if committed successfully, false otherwise
	 */
	private boolean setURLSharedPreference(String newURL) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("URL", newURL);
		return editor.commit();

	}
}
