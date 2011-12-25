package simple.home.jtbuaa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class About extends Activity{
	

    String ip() {
        //network
    	StringBuffer sb = new StringBuffer("");
		try {
			Enumeration<NetworkInterface> enumNI = NetworkInterface.getNetworkInterfaces();
			while (enumNI.hasMoreElements()) {
				NetworkInterface ni = enumNI.nextElement();
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					InetAddress local = ips.nextElement();
					if (!local.isLoopbackAddress()) {
						if (sb.length() > 0) sb.append(", ");
						sb.append(local.getHostAddress());
						break;
					}
				}
			}
			return sb.toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
    
    String aboutMsg() {
    	DisplayMetrics dm = new DisplayMetrics();  
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		String res = runCmd("cat", "/proc/cpuinfo")
		+ "\nAndroid " + android.os.Build.VERSION.RELEASE
		+ "\n" + dm.widthPixels+" * "+dm.heightPixels;
		
		/*ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appList = am.getRunningAppProcesses(); 
        for (int i = 0; i < appList.size(); i++) {
    		RunningAppProcessInfo as = (RunningAppProcessInfo) appList.get(i);
    		if (as.processName.equals(myPackageName)) {
        		try {//memory used by me
        			Debug.MemoryInfo info = am.getProcessMemoryInfo(new int[] {pid.getInt(as)})[0];
        			Log.d("==============", myPackageName + " " + info.getTotalPss()+"kb");
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    			break;
    		}
        }*/

		String ipaddr = ip();
		if (!ipaddr.equals(""))
			res += "\n" + ipaddr;
		return res;
    }

    String runCmd(String cmd, String para) {//performance of runCmd is very low, may cause black screen. do not use it AFAC 
        String line = "";
        try {
            String []cmds={cmd, para};
            java.lang.Process proc;
            if (para != "")
                proc = Runtime.getRuntime().exec(cmds);
            else
                proc = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while((line=br.readLine())!=null) {
            	if ((line.contains("Processor")) || (line.contains("model name")) || (line.contains("MemTotal:"))) {
            		if (line.contains("processor	: 1")) continue;
            		line = line.split(":")[1].trim();
            		break;
            	}
            }
        	br.close();
        } catch (IOException e) {
            return e.toString();
        }
        return line;
    }

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.about);

        TextView tvHelp = (TextView) findViewById(R.id.help);
        tvHelp.setText(getString(R.string.help_message)
        		+ "\n\n" + getString(R.string.about_dialog_notes));
        
        TextView tvInfo = (TextView) findViewById(R.id.info);
        tvInfo.setText(aboutMsg());
        
        TextView tvMailTo = (TextView) findViewById(R.id.mailto);
        tvMailTo.setText(Html.fromHtml("<u>"+ getString(R.string.author) +"</u>"));
        tvMailTo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "jtbuaa@gmail.com", null));
				util.startActivity(intent, false, getBaseContext());
			}
		});
        
        Button btnShare = (Button) findViewById(R.id.share);
        btnShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
    	        String text = getString(R.string.sharetext) + getString(R.string.share_text1) 
       	        		+ "http://opda.co/?s=D/simple.home.jtbuaa"//opda will do the webpage reload for us.
       	        		+ getString(R.string.share_text2)
       	        		+ "https://market.android.com/details?id=simple.home.jtbuaa"
       	        		+ getString(R.string.share_text3);
        	        
    	        Intent intent = new Intent(Intent.ACTION_SEND);
    	        intent.setType("text/plain");  
    	        intent.putExtra(Intent.EXTRA_SUBJECT, R.string.share);
        		intent.putExtra(Intent.EXTRA_TEXT, text);
       			util.startActivity(Intent.createChooser(intent, getString(R.string.sharemode)), true, getBaseContext());
			}
        });
        
        Button btnVote = (Button) findViewById(R.id.vote);
        btnVote.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=simple.home.jtbuaa"));
				if (!util.startActivity(intent, false, getBaseContext())) {
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("https://market.android.com/details?id=simple.home.jtbuaa"));
					intent.setComponent(getComponentName());
					util.startActivity(intent, false, getBaseContext());
				}
			}
        });

        TextView tvFellow = (TextView) findViewById(R.id.fellow);
        tvFellow.setText(Html.fromHtml("<u>腾讯应用中心</u>"));
        tvFellow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://app.qq.com/g/s?aid=index&g_f=990424"));
				util.startActivity(intent, false, getBaseContext());
			}
		});
	}
}
