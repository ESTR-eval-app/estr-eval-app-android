package ca.stevenlyall.evaln;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import ca.stevenlyall.evaln.interfaces.INotifyUpdateAvailableDelegate;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by stevenlyall on 16-03-02.
 */
public class VersionManager {

	// URL of most current version resource
	private final String VERSION_CHECK_URL = "http://stevenlyall.ca/app-versioning/eval-n/latest_version.json";

	private Context context;

	public VersionManager(Context context) {
		this.context = context;
	}

	private PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return info;
	}

	/**
	 * Connects to a server that keeps the version code for the most recent release and compares it to the current one.
	 * Determines whether to notify the user that they should update.
	 *
	 * @param delegate called if there is a newer version
	 */
	public void checkForNewVersion(final INotifyUpdateAvailableDelegate delegate) {
		final int current = getPackageInfo().versionCode;
		new AsyncTask<Void, Void, Integer>() {

			private final String TAG = "VersionRequest";
			private int result = 0;

			@Override
			protected Integer doInBackground(Void... params) {
				final OkHttpClient client = new OkHttpClient();

				Request request = new Request.Builder().url(VERSION_CHECK_URL).build();

				Response response = null;
				try {
					response = client.newCall(request).execute();
					String respStr = response.body().string();
					JSONObject respObj = new JSONObject(respStr);
					result = respObj.getInt("latestVersion");
					Log.d(TAG, "doInBackground: retrieved latest version " + result);
				} catch (Exception e) {
					Log.e(TAG, "doInBackground: exception thrown while checking for new version", e);
				}
				return result;
			}

			@Override
			protected void onPostExecute(Integer integer) {
				super.onPostExecute(integer);
				if (current < integer) {
					delegate.notifyUpdateRequired();
				}
			}
		}.execute();
	}
}
