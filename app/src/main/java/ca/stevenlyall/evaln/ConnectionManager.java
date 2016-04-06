package ca.stevenlyall.evaln;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class ConnectionManager {

	private Activity activity;

	public ConnectionManager(Activity activity) {
		this.activity = activity;
	}

	/**
	 * Checks for a network connection
	 *
	 * @return true if connected, false otherwise
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	/**
	 * Shows an alert dialog prompting the user to connect to a network.
	 * Opens wifi settings menu when dialog button clicked.
	 */
	public void showNoConnectionMessage() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// go to wifi settings
				activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			}
		});
		builder.setTitle(R.string.no_connection).setMessage(R.string.no_connection_detail);
		builder.create().show();
	}


}
