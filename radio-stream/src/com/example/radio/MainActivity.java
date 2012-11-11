package com.example.radio;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

		final String song_url[] = {
		
			"mms://s4awm.castup.net/990310001-52.wmv",
			"mms://s4awm.castup.net/990310001-52.wmv"
	};
	
	private MediaPlayer mediaPlayer;
	private AudioManager audioManager = null; 

	//////////////////////////////////
	//http://stackoverflow.com/questions/6283568/online-radio-streaming-app-for-android
	//////////////////////////////////
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ImageButton bPlay = (ImageButton) findViewById(R.id.imageButton1);
        ImageButton bPause = (ImageButton) findViewById(R.id.imageButton2);
        SeekBar sVolume = (SeekBar) findViewById(R.id.volume);
        final TextView tvVolume = (TextView) findViewById(R.id.vPercentage);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        boolean audioPlaying;
        final TextView textViewDebug = (TextView) findViewById(R.id.textViewDebug);
        
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        sVolume.setMax(maxVolume);
        sVolume.setProgress(curVolume);
        
        ///testing if audioManager reports buffering
        audioPlaying = audioManager.isMusicActive();
        if (audioPlaying = true) {
        	textViewDebug.setText("audio is buffered");
        }
        else  {
        	textViewDebug.setText("audio is buffering");
        }
        ///
        
        sVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar bar) {
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {				
			}
			
			public void onProgressChanged(SeekBar bar, int progress,
					boolean fromUser) {
				tvVolume.setText("" + progress + "%");
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);								
			}
		});
        

        bPlay.setOnClickListener(new View.OnClickListener() {
        
			
			public void onClick(View v) {
		        try {
					mediaPlayer.setDataSource(song_url[0]);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        try {
					mediaPlayer.prepare();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        mediaPlayer.start();
			}
		});
        
        bPause.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				mediaPlayer.pause();
				
			}
		});
    }
    
    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
		mediaPlayer.pause();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	mediaPlayer.start();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	mediaPlayer.release();
    
    }
    
}


