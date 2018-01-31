package top.leftvalue.dk.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import top.leftvalue.dk.model.ResponseJson;
import top.leftvalue.dk.model.Voice;
import top.leftvalue.dk.service.ArmConvert;
import top.leftvalue.dk.service.BaiduSpeechService;
import top.leftvalue.dk.service.Recorder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.LocalTime;

public class AudioController extends Controller {
	/**
	 * 停止当前存在的录音机（会延时几毫秒）
	 */
	public void stoprecord() {
		boolean flag = Recorder.stopAll();
		renderJson(ResponseJson.of(1, "当前状态" + (flag ? "已" : "未") + "更改"));
	}

	public void record() {
		try {
			if (Recorder.checkIfBusy()) {
				renderJson(ResponseJson.of(-1, "录音设备正忙，请等待当前任务结束或执行 stop 之后重试"));
			} else {
				String s_ = getPara("seconds");
				int seconds = 0;
				try {
					seconds = Integer.parseInt(s_);
				} catch (Exception e) {
					renderJson(ResponseJson.of(-1, "参数异常 " + s_ + ",请确认 seconds 为正整数"));
					return;
				}
				File audio = Recorder.recordAndSave(seconds);
				if (audio != null) {
					renderFile(audio);
				} else {
					renderJson(ResponseJson.of(-1, "录音失败 (Inner)"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResponseJson.of(-1, "录音失败 " + e.getMessage()));
		}
	}

	/**
	 * 上传音乐并下载（包括 android 端上传对讲音频并下载）->播放
	 */
	public void upload() {
		boolean flag = false;
		try {
			UploadFile audio_file_ = getFile();
			File audio_file = audio_file_.getFile();
			System.out.println(audio_file.getAbsolutePath());
			if (audio_file.getName().endsWith("amr")) {
				String target = "temp.mp3";
				ArmConvert.changeToMp3(audio_file.getAbsolutePath(), target);
				audio_file.delete();
				audio_file = new File(target);
			}
			flag = BaiduSpeechService.play(audio_file);
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResponseJson.FAIL);
		}
		if (flag) {
			renderJson(ResponseJson.SUCCESS);
		} else {
			renderJson(ResponseJson.FAIL);
		}
	}

	/**
	 * 朗诵指定文字 自定义朗诵音调速度男女以及是否翻译英文
	 */
	public void speak() {
		try {
			Voice voice = getVoiceConfigFromRequest(getRequest());
			boolean flag = BaiduSpeechService.say(voice);
			if (flag) {
				renderJson(ResponseJson.SUCCESS.toString());
			} else {
				renderJson(ResponseJson.FAIL.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(ResponseJson.FAIL.toString());
		}
	}

	/**
	 * 预览语音(得到指定文字的 baidu 语音)
	 */
	public void preview() {
		try {
			Voice voice = getVoiceConfigFromRequest(getRequest());
			boolean flag = BaiduSpeechService.download(voice);
			if (flag) {
				renderFile(new File(BaiduSpeechService.filePath));
			} else {
				renderJson(ResponseJson.FAIL.toString());
			}
		} catch (Exception e) {
			renderJson(ResponseJson.FAIL.toString());
		}
	}

	/**
	 * 调节系统音量
	 */
	public void setvolumn() {

	}

	/**
	 * 查看系统音量
	 */
	public void getvolumn() {

	}

	/**
	 * 播报当前时间
	 */
	public void now() {
		Voice voice = new Voice();
		LocalTime time = LocalTime.now();
		voice.setTex("现在是,北京时间," + time.getHour() + "时" + time.getMinute() + "分" + time.getSecond() + "秒");
		BaiduSpeechService.say(voice);
		renderJson(ResponseJson.SUCCESS.toString());
	}

	private Voice getVoiceConfigFromRequest(HttpServletRequest request) {
		String vol = request.getParameter("vol");
		String pit = request.getParameter("pit");
		String per = request.getParameter("per");
		String spd = request.getParameter("spd");
		String tex = request.getParameter("tex");
		Voice voice = new Voice();
		try {
			if (tex != null) {
				voice.setTex(tex);
			}
			if (vol != null) {
				voice.setVol(Integer.parseInt(vol));
			}
			if (pit != null) {
				voice.setPit(Integer.parseInt(pit));
			}
			if (per != null) {
				voice.setPer(Integer.parseInt(per));
			}
			if (spd != null) {
				voice.setSpd(Integer.parseInt(spd));
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return voice;
	}
}
