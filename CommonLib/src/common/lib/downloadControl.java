package common.lib;

import base.lib.wrapAdView;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class downloadControl extends Activity {
	static boolean mAdAvailable;
	static {
		try {
			Class.forName("com.google.ads.AdView");
			mAdAvailable = true;
		} catch (Throwable t) {
			mAdAvailable = false;
		}
	}
	wrapAdView adview;
	LinearLayout adContainer;

	Button btnPause, btnStop;
	boolean pause = false, stop = false, failed = false;
	TextView tv;
	MyApp appstate;
	int id;
	DownloadTask dlt;
	
	NotificationManager nManager;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		init(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.pause);

		if (mAdAvailable) {
			adview = new wrapAdView(this, 3, "a1502880ce4208b", null);
			adContainer = (LinearLayout) findViewById(R.id.adContainer);
			adContainer.addView(adview.getInstance());
			adview.loadAd();
		}

		init(getIntent());
	}

	private void init(Intent intent) {
		tv = (TextView) findViewById(R.id.download_name);
		btnPause = (Button) findViewById(R.id.pause);
		btnStop = (Button) findViewById(R.id.stop);
		
		id = intent.getIntExtra("id", 0);
		nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		String errorMsg = intent.getStringExtra("errorMsg");
		
		appstate = (MyApp) getApplicationContext();
		dlt = appstate.downloadState.get(id);

		if (dlt != null) {
			pause = dlt.pauseDownload;
			stop = dlt.stopDownload;
			failed = dlt.downloadFailed;
			
			tv.setText(getString(R.string.download_hint) + "\n" + intent.getStringExtra("name") + "\n");
		} 
		else if (errorMsg != null) {
			failed = true;
			pause = false;
			
			tv.setText(intent.getStringExtra("name") + " " + getString(R.string.download_fail) + "\n\n" + errorMsg + "\n");
		}
		else {// the corresponding download state is deleted, so can't control it.
			finish();
			return;
		}

		if (pause)
			btnPause.setText(getString(R.string.resume));
		else if (failed)
			btnPause.setText(getString(R.string.retry));
		else
			btnPause.setText(getString(R.string.pause));

		if (failed) {
			final String url = intent.getStringExtra("url");
			btnPause.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {// start new task to download
					nManager.cancel(id);
					try {appstate.downloadState.remove(id);} catch(Exception e) {}
					
					Intent intent = new Intent(
							"simpleHome.action.START_DOWNLOAD");
					intent.putExtra("url", url);
					sendBroadcast(intent);
					finish();
				}
			});

			btnStop.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {// remove notification
					nManager.cancel(id);
					try {appstate.downloadState.remove(id);} catch(Exception e) {}
					
					finish();
				}
			});
		} else {
			btnPause.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (dlt != null)
						dlt.pauseDownload = !pause;
					finish();
				}
			});

			btnStop.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (dlt != null)
						dlt.stopDownload = !stop;
					finish();
				}
			});
		}
	}
}
