package simple.home.jtbuaa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class downloadControl extends Activity{
	Button btnPause, btnStop;
	boolean pause, stop;
	Intent intent;
	MyApp appstate;
	int id;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.pause);
        appstate = (MyApp) getApplicationContext();
        
        intent = getIntent();
        id = intent.getIntExtra("id", 0);
        Log.d("==============", "id: " + id);
        
        pause = appstate.downloadState.get(id).pauseDownload;
        stop = appstate.downloadState.get(id).stopDownload;
        
        btnPause = (Button) findViewById(R.id.pause);
		if (pause) {
			btnPause.setText(getString(R.string.resume));
		}
		else {
			btnPause.setText(getString(R.string.pause));
		}
		
        btnPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				appstate.downloadState.get(id).pauseDownload = !pause;
				finish();
			}
        });

        btnStop = (Button) findViewById(R.id.stop);
        btnStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				appstate.downloadState.get(id).stopDownload = !stop;
				finish();
			}
        });

	}
	
}
