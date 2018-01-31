package top.leftvalue.dk.service;

import java.io.File;
import java.util.Properties;

public class ArmConvert {
	public static void main(String[] args) {
		changeToMp3(
				"/Users/linxi/Documents/Programming/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/pi/upload/Record1513232440925.amr",
				"temp3.mp3");
	}

	static String FFMPEG = "";
	static {
		Properties props = System.getProperties(); // 获得系统属性集
		String osName = props.getProperty("os.name"); // 操作系统名称
		System.out.println(osName);
		if (osName.contains("Mac")) {
			FFMPEG = "/usr/local/bin/ffmpeg";
		} else {
			System.out.println("树莓派环境 ");
			FFMPEG = "/usr/bin/ffmpeg";
		}
	}

	public static void changeToMp3(String sourcePath, String targetPath) {
		Process process = null;
		try {
			File mp3 = new File(targetPath);
			if (mp3.exists()) {
				mp3.delete();
			}
			process = Runtime.getRuntime()
					.exec(new String[] { "sh", "-c", FFMPEG + " -i " + sourcePath + " " + mp3.getAbsolutePath() });
			StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "E");
			errorGobbler.start();// kick off stderr
			StreamGobbler outGobbler = new StreamGobbler(process.getInputStream(), "ST");
			outGobbler.start();// kick off stdout
			process.waitFor();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
