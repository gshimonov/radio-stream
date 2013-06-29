package com.example.newradio;

import java.io.IOException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity implements OnPreparedListener {
	final String streamSource[] = {
			
			"mms://s4awm.castup.net/990310001-52.wmv",
			"mms://194.90.203.111/99fm"
	};
	
	
	//alarm tutorial:
	//http://www.techrepublic.com/blog/app-builder/use-androids-alarmmanager-to-schedule-an-event/2651
	
	private MediaPlayer mp;
	private AudioManager audioManager = null;
	private TextView buffered;
	private ImageButton bPlay;
	private ImageButton bPause;
	private Button testButton;
	private SeekBar volume;
	private ProgressBar progressBar;
	private PowerManager.WakeLock wl;
	private boolean firstPlay;
	private TimePicker timePicker;
	
	final static private long ONE_SECOND = 1000;
    final static private long TWENTY_SECONDS = ONE_SECOND * 20;
    static private long TEN_SECONDS = ONE_SECOND * 10;
    PendingIntent pi;
    BroadcastReceiver br;
    AlarmManager am;

	private int hour;
	private int min;
	//debug
	String playString = "Play detected";
	String otherString = "Play not detected";
	private Button btStart;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();
        
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        
        bPlay = (ImageButton) findViewById(R.id.imageButton1);
        bPause = (ImageButton) findViewById(R.id.bPause);
        testButton = (Button) findViewById(R.id.button1);
        volume = (SeekBar) findViewById(R.id.seekBar1);
        buffered = (TextView) findViewById(R.id.textView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        timePicker = (TimePicker) findViewById(R.id.timePicker1);
        firstPlay = true;
        
        //debug
        btStart = (Button) findViewById(R.id.button2);
        
        volume.setMax(maxVol);
        volume.setProgress(curVol);
        
             
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar bar) {
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {				
			}
			
			public void onProgressChanged(SeekBar bar, int progress,
				boolean fromUser) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);								
			}
        });   
        
        alarmSetup();
        
        mp = new MediaPlayer();
        mp.setOnPreparedListener(this);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
  //      startStream();
    }
    
    public void alarmSetup() {
    	br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent i) {
				if (i.getAction().equals("android.intent.action.BOOT_COMPLETED"))
		        {
					Toast.makeText(c, "Rise and Shine!", Toast.LENGTH_LONG).show();
					playRadio();
		        }
				
			}
        };
        registerReceiver(br, new IntentFilter("com.example.wakeywakey") );
        
        pi = PendingIntent.getBroadcast( this, 0, new Intent("com.example.wakeywakey"), 0 );
        am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
    }
    
    public void startAlarm(View v) {
    	am.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TEN_SECONDS, pi );
    	Context context = getApplicationContext();
    	Toast.makeText(context, "Alarm set", Toast.LENGTH_LONG).show();
    	
    }
    
    public void playClicked(View v) 
    {
    	playRadio();
    }
    
    //public void startStream() {
    public void playRadio()
    {   
    	if (firstPlay == false) {
    		mp.start();
    	}
    	else {
        	try {
        		//mp.reset();
        		mp.setDataSource(streamSource[0]);
        	} catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        	try {
    			mp.prepareAsync();
    		} catch (IllegalStateException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	firstPlay = false;
        	//debug
    		progressBar.setVisibility(View.VISIBLE);
    		buffered.setText(playString);
    		//
    	}
    	bPlay.setVisibility(View.INVISIBLE);
        bPause.setVisibility(View.VISIBLE);
    }
    
    public void pauseRadio(View v) {
    	bPlay.setVisibility(View.VISIBLE);
    	bPause.setVisibility(View.INVISIBLE);
    	mp.pause();
    }
    
    public void testPlay(View v) {
    	mp.start();
    }

    

/*	public void playRadio(View v) {
			progressBar.setVisibility(View.VISIBLE);
			buffered.setText(playString);
		//	onPrepared(mp);
	}
*/	
	public void onPrepared(MediaPlayer player) {
	    player.start();
	    progressBar.setVisibility(View.INVISIBLE);
	}
	

    
    @Override
    public void onPause() {
    	super.onPause();
    	mp.pause();   	
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	mp.start();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	mp.release();
    }
    
    @Override
    public void onDestroy() {
    	am.cancel(pi);
        unregisterReceiver(br);
        wl.release();
    	super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
