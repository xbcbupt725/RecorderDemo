package com.example.utl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

public class MyRecorder {
	private AudioRecord audioRecord = null;
	private static int frequency = 8000;
	private static int channel = AudioFormat.CHANNEL_IN_MONO;// ��������
	private static int encodingBitRate = AudioFormat.ENCODING_PCM_16BIT;// ���ñ���
	private static final int recorder_bpp = 16;
	private int recBufSize = 0;
	private int playBufSize = 0;
	private boolean isRecording = false;// ¼����־λ
	private String tempFilePath;// ��ʱ�ļ���·��
	private String saveFilePath;// �����ļ���·��
	private String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
	String saveFoldPath;
	private static final String saveFold = "Audio123";// ������ļ�����
	private static final String temp_file = "record_temp.raw";// ��ʱ�ļ���
	private Thread recThread = null;//¼���߳�
	ShortBuffer buf;
	public ShortBuffer getBuf() {
		return buf;
	}

	public void setBuf(ShortBuffer buf) {
		this.buf = buf;
	}

	public MyRecorder() {
		super();
		saveFoldPath = SDPath + "/" + saveFold;
		File f = new File(saveFoldPath);
		if(!f.exists()){
			f.mkdirs();
		}
		tempFilePath = saveFoldPath + "/" + temp_file;  
	   // String fileName = DateHelp.getFormateDate(3);  
	
	}

	// ��ʼ¼��
		public void startRec() {
			recBufSize = AudioRecord.getMinBufferSize(frequency, channel,
					encodingBitRate);
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
					channel, encodingBitRate, recBufSize);
			audioRecord.startRecording();
			isRecording = true;
			recThread = new Thread(new Runnable() {
				public void run() {
					writeAudioDataToFile();
				}
			}, "AudioRecorder Thread");
			recThread.start();
		}

		// ����Ƶ����д���ļ�
		private void writeAudioDataToFile() {
			byte data[] = new byte[recBufSize];
			FileOutputStream os = null;
			try {
				os = new FileOutputStream(tempFilePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			int read = 0;
			if (null != os) {
				while (isRecording) {
					read = audioRecord.read(data, 0, recBufSize);
					if (AudioRecord.ERROR_INVALID_OPERATION != read) {
						try {
							os.write(data);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void stopRecording(String fileName) {
			if (null != audioRecord) {
				isRecording = false;
				audioRecord.stop();
				audioRecord.release();
				audioRecord = null;
				recThread = null;
			}
			//String fileName = DateHelp.getFormateDate(3);
			saveFilePath = saveFoldPath + "/" + fileName;
			copyWaveFile(tempFilePath, saveFilePath);
			deleteTempFile();
		}

		// ɾ����ʱ�ļ�
		private void deleteTempFile() {
			File file = new File(tempFilePath);
			file.delete();
		}
		
		private void copyWaveFile(String inFilename, String outFilename) {
			FileInputStream in = null;
			FileOutputStream out = null;
			long totalAudioLen = 0;
			long totalDataLen = totalAudioLen + 36;
			long longSampleRate = frequency;
			int channels = 1;
			if(channel == AudioFormat.CHANNEL_IN_MONO){
				channels = 1;
			}else if(channel == AudioFormat.CHANNEL_IN_STEREO){
				channels = 2;
			}
			
			long byteRate = recorder_bpp * frequency * channels / 8;

			byte[] data = new byte[recBufSize];

			try {
				in = new FileInputStream(inFilename);
				out = new FileOutputStream(outFilename);
				ByteArrayOutputStream chartBuf = new ByteArrayOutputStream();
				totalAudioLen = in.getChannel().size();
				totalDataLen = totalAudioLen + 36;
				WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
						longSampleRate, channels, byteRate);

				while (in.read(data) != -1) {
					out.write(data);
					chartBuf.write(data);
				}
				buf = ByteBuffer.wrap(chartBuf.toByteArray())
						.order(ByteOrder.nativeOrder()).asShortBuffer();
				in.close();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
				long totalDataLen, long longSampleRate, int channels, long byteRate)
				throws IOException {

			byte[] header = new byte[44];

			header[0] = 'R'; // RIFF/WAVE header
			header[1] = 'I';
			header[2] = 'F';
			header[3] = 'F';
			header[4] = (byte) (totalDataLen & 0xff);
			header[5] = (byte) ((totalDataLen >> 8) & 0xff);
			header[6] = (byte) ((totalDataLen >> 16) & 0xff);
			header[7] = (byte) ((totalDataLen >> 24) & 0xff);
			header[8] = 'W';
			header[9] = 'A';
			header[10] = 'V';
			header[11] = 'E';
			header[12] = 'f'; // 'fmt ' chunk
			header[13] = 'm';
			header[14] = 't';
			header[15] = ' ';
			header[16] = 16; // 4 bytes: size of 'fmt ' chunk
			header[17] = 0;
			header[18] = 0;
			header[19] = 0;
			header[20] = 1; // format = 1
			header[21] = 0;
			header[22] = (byte) channels;
			header[23] = 0;
			header[24] = (byte) (longSampleRate & 0xff);
			header[25] = (byte) ((longSampleRate >> 8) & 0xff);
			header[26] = (byte) ((longSampleRate >> 16) & 0xff);
			header[27] = (byte) ((longSampleRate >> 24) & 0xff);
			header[28] = (byte) (byteRate & 0xff);
			header[29] = (byte) ((byteRate >> 8) & 0xff);
			header[30] = (byte) ((byteRate >> 16) & 0xff);
			header[31] = (byte) ((byteRate >> 24) & 0xff);
			header[32] = (byte) (2 * 16 / 8); // block align
			header[33] = 0;
			header[34] = recorder_bpp; // bits per sample
			header[35] = 0;
			header[36] = 'd';
			header[37] = 'a';
			header[38] = 't';
			header[39] = 'a';
			header[40] = (byte) (totalAudioLen & 0xff);
			header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
			header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
			header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

			out.write(header, 0, 44);
		}

}
