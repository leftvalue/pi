package top.leftvalue.dk.service;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

/**
 * 树莓派拍照服务 受限于莫名奇妙的问题 决定删掉原先考虑的延时拍照服务 防止 tomcat 占用过多内存而自动关闭
 * 
 * @author linxi
 *
 */
public class CameraService {
	private static String defaultPath = "photos/";
	/**
	 * 创建默认保存路径，是否有必要保存图片以后再考虑
	 */
	static {
		try {
			File defaultdir = new File(defaultPath);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("创建拍照默认目录失败,更改为当前文件夹");
			defaultPath = "";
		}
	}
	private static FFmpegFrameGrabber grabber;
	/**
	 * 指示器变量
	 */
	private static boolean isopening = false;

	private static boolean open() {
		try {
			if (grabber == null) {
				/**
				 * grabber 还没初始化，那就现在 init 好了
				 */
				grabber = new FFmpegFrameGrabber("/dev/video0");
				/**
				 * 更改默认分辨率(什么时候买得起一个高一点分辨率的摄像头啊啊啊）
				 */
				grabber.setImageWidth(640);
				grabber.setImageHeight(480);
				System.out.println("grabber初始化完毕");
			}
			if (!isopening) {
				System.out.println("开始摄像头");
				grabber.start();
				isopening = true;
				return true;
			} else {
				/**
				 * 已经打开了，你还打开个毛线
				 */
				return false;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
	}

	private static boolean close() {
		if (!isopening) {
			return false;
		} else {
			try {
				grabber.close();
				System.out.println("摄像头关闭成功");
				isopening = false;
				return true;
			} catch (Exception e) {
				System.err.println(e.getMessage());
				return false;
			}
		}
	}

	private static Java2DFrameConverter java2dConverter = new Java2DFrameConverter();

	public static File takepicture() {
		try {
			open();
			File target = new File(defaultPath + System.currentTimeMillis() + ".jpg");
			BufferedImage bufferedImage = java2dConverter.convert(grabber.grab());
			ImageIO.write(bufferedImage, "jpg", target);
			close();
			System.out.println("拍照成功");
			return target;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.out.println("拍照失败");
			return null;
		}
	}
}
