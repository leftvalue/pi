package top.leftvalue.dk.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Recorder {
	public static void main(String[] args) {
		System.out.println("开始录音");
		Recorder recorder = new Recorder();
		recorder.capture(10);
		System.out.println("已经停止录音");
		recorder.save();
		System.out.println("保存完毕");
		System.out.println("正在播放");
		recorder.play();
		System.out.println("播放完毕");
	}

	private static boolean isBusy = false;

	public static boolean checkIfBusy() {
		return isBusy;
	}

	public synchronized static File recordAndSave(int seconds) {
		try {
			isBusy = true;
			Recorder recorder = new Recorder();
			recorder.capture(seconds);
			System.out.println("录音结束");
			File file = recorder.save();
			System.out.println("保存完毕");
			isBusy = false;
			if (file != null) {
				return file;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (Exception e) {
		}
	}

	/** 定义录音格式 **/
	AudioFormat audioFormat = null;
	/**
	 * 定义目标数据行,可以从中读取音频数据 ,该 TargetDataLine 接口提供 从目标数据行的缓冲区读取所捕获数据的方法。
	 **/
	TargetDataLine targetDataLine = null;
	/** 定义源数据行,源数据行是可以写入数据的数据行。它充当其混频器的源。应用程序将音频字节写入源数据行，这样可处理字节缓冲并将它们传递给混频器。 **/
	SourceDataLine sourceDataLine = null;
	/** 定义字节数组输入输出流 **/
	ByteArrayInputStream byteArrayInputStream = null;
	ByteArrayOutputStream byteArrayOutputStream = null;
	/** 定义音频输入流 **/
	AudioInputStream audioInputStream = null;
	/** 定义停止录音的标志，来控制全局录音线程的运行 **/
	static Boolean ifStopRecord = false;

	/**
	 * 开始录音 一旦开始无法结束
	 * 
	 * @param seconds
	 */
	public void capture(int seconds) {
		try {
			/** af为AudioFormat也就是音频格式 **/
			audioFormat = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
			targetDataLine = (TargetDataLine) (AudioSystem.getLine(info));
			/** 打开具有指定格式的行，这样可使行获得所有所需的系统资源并变得可操作。 **/
			targetDataLine.open(audioFormat);
			/** 允许某一数据行执行数据 I/O **/
			targetDataLine.start();
			/** 创建播放录音的线程 **/
			Record record = new Record(seconds);
			record.start();
			record.join();
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
			return;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 停止任何存在的录音 其实就是增加标志位 虽然同一时刻只能有一个录音启动 但是 stopAll 一看就知道是停止当前
	 */
	@Deprecated
	public static boolean stopAll() {
		if (isBusy) {
			ifStopRecord = true;
			return true;
		} else {
			return false;
		}
	}

	@Deprecated
	public void play() {
		/** 将baos中的数据转换为字节数据 **/
		byte audioData[] = byteArrayOutputStream.toByteArray();
		/** 转换为输入流 **/
		byteArrayInputStream = new ByteArrayInputStream(audioData);
		audioFormat = getAudioFormat();
		audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat,
				audioData.length / audioFormat.getFrameSize());
		try {
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();
			/** 创建播放进程 **/
			Play py = new Play();
			Thread t2 = new Thread(py);
			t2.start();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				/** 关闭流 **/
				if (audioInputStream != null) {
					audioInputStream.close();
				}
				if (byteArrayInputStream != null) {
					byteArrayInputStream.close();
				}
				if (byteArrayOutputStream != null) {
					byteArrayOutputStream.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static String basePath = "resords";
	static {
		try {
			File filePath = new File(basePath);
			if (!filePath.exists()) {
				filePath.mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 保存录音 **/
	public File save() {
		/** 取得录音输入流 **/
		audioFormat = getAudioFormat();
		byte audioData[] = byteArrayOutputStream.toByteArray();
		byteArrayInputStream = new ByteArrayInputStream(audioData);
		audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat,
				audioData.length / audioFormat.getFrameSize());
		File file = null;
		boolean flag = false;
		try {
			file = new File(basePath + "/" + System.currentTimeMillis() + ".mp3");
			AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (byteArrayInputStream != null) {
					byteArrayInputStream.close();
				}
				if (audioInputStream != null) {
					audioInputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (flag) {
			return file;
		} else {
			return null;
		}
	}

	/** 得到设置AudioFormat的参数 **/
	public AudioFormat getAudioFormat() {
		AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
		float rate = 8000f;
		int sampleSize = 16;
		boolean bigEndian = true;
		int channels = 1;
		return new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate, bigEndian);
	}

	/** 录音类 **/
	class Record extends Thread {
		/** 定义存放录音的字节数组,作为缓冲区 **/
		byte bts[] = new byte[10000];
		private int seconds;
		/**
		 * 结束录制时间
		 */
		private final long end;

		public Record(int seconds) {
			end = System.currentTimeMillis() + seconds * 1000;
		}

		/** 将字节数组包装到流里，最终存入到baos中 **/
		/** 重写run函数 **/
		public void run() {
			byteArrayOutputStream = new ByteArrayOutputStream();
			try {
				ifStopRecord = false;
				while (ifStopRecord != true && System.currentTimeMillis() <= end) {
					/** 当停止录音没按下时，该线程一直执行 **/
					/** 从数据行的输入缓冲区读取音频数据。 **/
					/** 要读取bts.length长度的字节,cnt 是实际读取的字节数 **/
					int cnt = targetDataLine.read(bts, 0, bts.length);
					if (cnt > 0) {
						byteArrayOutputStream.write(bts, 0, cnt);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					/** 关闭打开的字节数组流 **/
					if (byteArrayOutputStream != null) {
						byteArrayOutputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					targetDataLine.drain();
					targetDataLine.close();
				}
			}
		}

	}

	class Play implements Runnable {
		public void run() {
			byte bts[] = new byte[10000];
			try {
				int cnt;
				while ((cnt = audioInputStream.read(bts, 0, bts.length)) != -1) {
					if (cnt > 0) {
						/** 将音频数据写入到混频器 **/
						sourceDataLine.write(bts, 0, cnt);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				sourceDataLine.drain();
				sourceDataLine.close();
			}
		}
	}
}
