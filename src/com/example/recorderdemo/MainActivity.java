package com.example.recorderdemo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends ActionBarActivity {
	private TextView tvCounter = null;
	private Button btnReset = null;
	private Button btnRecord = null;
	private String savePath = null;
	private String str = "当前录音次数：";
	private String saveName;
	MediaRecorder recorder = new MediaRecorder();
	//RecordMainThreadOperator recordMainThreadOperator;
	SimpleDateFormat myFmt=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
	long startTime = 0;
	long stopTime = 0;
	private int i = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		savePath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"audioRecord"+File.separator;
		File file = new File(savePath);
		if(!file.exists()){
			file.mkdirs();
		}
		
		 
		 
		tvCounter = (TextView) findViewById(R.id.tvCounter);
		btnReset = (Button) findViewById(R.id.btnReset);
		btnRecord = (Button) findViewById(R.id.btnRecord);
		tvCounter.setText(str+i);
		btnReset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				i = 0;
				tvCounter.setText(str+i);
				
			}
		});
		btnRecord.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				//Toast.makeText(MainActivity.this, "touch", Toast.LENGTH_SHORT).show();
				   if(event.getAction() == MotionEvent.ACTION_DOWN){  
	                   // Toast.makeText(MainActivity.this, "ACTION_DOWN", Toast.LENGTH_SHORT).show();
					    recorder.reset();
	                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	            		recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
	            		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
	            		i++;
	            		saveName = myFmt.format(new Date());
	            		recorder.setOutputFile(savePath+saveName+".wav");
	            		 try {
	            			recorder.prepare();
	            		} catch (IllegalStateException e) {
	            			// TODO Auto-generated catch block
	            			e.printStackTrace();
	            		} catch (IOException e) {
	            			// TODO Auto-generated catch block
	            			e.printStackTrace();
	            		}
	            		 
	            		 btnRecord.setText("松手停止录音");
	            		 recorder.start();   // Recording is now started
	            		 startTime = System.currentTimeMillis();
	                    
	                }
				   if(event.getAction() == MotionEvent.ACTION_UP){
					   //Toast.makeText(MainActivity.this, "ACTION_U", Toast.LENGTH_SHORT).show();
					   recorder.setOnErrorListener(null);
						
					   
					   stopTime = System.currentTimeMillis();
					   
					   if(stopTime - startTime < 1000){
						   Toast.makeText(MainActivity.this,"时间过短，录音失败", Toast.LENGTH_SHORT).show();
						   i--;
						   File f = new File(savePath+saveName+".wav");
						   if(f.exists()){
							   f.delete();
						   }
					   }else{
						   
						   tvCounter.setText(str+i);
						   recorder.stop();
					   }
					   btnRecord.setText("按住录音");
					   recorder.reset();
					   
						   // You can reuse the object by going back to setAudioSource() step
					
				   }
				return false;
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		 recorder.release(); // Now the object cannot be reused
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
