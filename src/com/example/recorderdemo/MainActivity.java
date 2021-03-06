package com.example.recorderdemo;

import java.io.File;
import java.nio.ShortBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.utl.MyRecorder;
import com.example.utl.WaveImageHelper;



public class MainActivity extends ActionBarActivity {
	private TextView tvCounter = null;
	private Button btnReset = null;
	private Button btnRecord = null;
	private String savePath = null;
	private ImageView waveImage = null;
	private String str = "��ǰ¼��������";
	private String saveName = null;
	//MediaRecorder recorder = new MediaRecorder();
	//RecordMainThreadOperator recordMainThreadOperator;
	SimpleDateFormat myFmt=new SimpleDateFormat("yyyy_MM_dd_ HH_mm_ss");
	MyRecorder myRecorder = new MyRecorder();
	long startTime = 0;
	long stopTime = 0;
	private int i = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		savePath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"audio123"+File.separator;
		File file = new File(savePath);
		if(!file.exists()){
			file.mkdirs();
		}
		
		 
		 
		tvCounter = (TextView) findViewById(R.id.tvCounter);
		btnReset = (Button) findViewById(R.id.btnReset);
		btnRecord = (Button) findViewById(R.id.btnRecord);
		waveImage = (ImageView) findViewById(R.id.waveImage);
		tvCounter.setText(str+i);
		btnReset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				i = 0;
				tvCounter.setText(str+i);
				WaveImageHelper.recylceWaveBitmap(waveImage);
				
			}
		});
		btnRecord.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				//Toast.makeText(MainActivity.this, "touch", Toast.LENGTH_SHORT).show();
				   if(event.getAction() == MotionEvent.ACTION_DOWN){  
	                   // Toast.makeText(MainActivity.this, "ACTION_DOWN", Toast.LENGTH_SHORT).show();
//					    recorder.reset();
//	                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//	            		recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
//	            		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
					    WaveImageHelper.recylceWaveBitmap(waveImage);
	            		i++;
	            		saveName = myFmt.format(new Date());
	            		myRecorder.startRec();
	            		//recorder.setOutputFile(savePath+saveName+".wav");
//	            		 try {
//	            			//recorder.prepare();
//	            		} catch (IllegalStateException e) {
//	            			// TODO Auto-generated catch block
//	            			e.printStackTrace();
//	            		} catch (IOException e) {
//	            			// TODO Auto-generated catch block
//	            			e.printStackTrace();
//	            		}
	            		 
	            		 btnRecord.setText("����ֹͣ¼��");
	            		// recorder.start();   // Recording is now started
	            		 startTime = System.currentTimeMillis();
	                    
	                }
				   if(event.getAction() == MotionEvent.ACTION_UP){
					   //Toast.makeText(MainActivity.this, "ACTION_U", Toast.LENGTH_SHORT).show();
					   //recorder.setOnErrorListener(null);
						
					   
					   stopTime = System.currentTimeMillis();
					   myRecorder.stopRecording(saveName + ".wav");
//					   if(stopTime - startTime < 1000){
//						   Toast.makeText(MainActivity.this,"ʱ����̣�¼��ʧ��", Toast.LENGTH_SHORT).show();
//						   i--;
//						   myRecorder.stopRecording(saveName + ".wav");
//						   File f = new File(savePath+saveName+".wav");
//						   if(f.exists()){
//							   f.delete();
//						   }
//					   }else{
//						   tvCounter.setText(str+i);				   
//					   }
					   tvCounter.setText(str+i);
					   btnRecord.setText("��ס¼��");
					   ShortBuffer buf = myRecorder.getBuf();
					   WaveImageHelper.showWave(buf, 0, buf.capacity(), waveImage); 
					   
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
		 //recorder.release(); // Now the object cannot be reused
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
