package simple.home.jtbuaa;

import java.util.HashMap;

import android.app.Application;

import simple.home.jtbuaa.SimpleBrowser.DownloadTask;

public class MyApp extends Application {
	HashMap<Integer, DownloadTask> downloadState;
	
    @Override  
    public void onCreate() {  
        super.onCreate();  
    	downloadState = new HashMap<Integer, DownloadTask>();
    	
        CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(getApplicationContext());  
    }  
}
