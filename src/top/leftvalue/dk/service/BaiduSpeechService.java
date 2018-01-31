package top.leftvalue.dk.service;

import javazoom.jl.player.Player;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import top.leftvalue.dk.model.Voice;
import top.leftvalue.dk.service.jsonn.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Scanner;

public class BaiduSpeechService {
	public static final String filePath = "temp.mp3";
	private static final String apiKey = "3yFV0u1Kov8Axe9oarXEElXRwuwde7Mu";
	private static final String secretKey = "uCsLH7zeVFgZXhP5m5BIUBv0OvCxRe7U";

	public static void main(String[] args) throws Exception {
		play(new File("/Users/linxi/Documents/Programming/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/pi/upload/Record1513232440925.amr"));
		// sayTime();
		// Scanner scanner = new Scanner(System.in);
		// for (;;) {
		// String tex = scanner.nextLine().trim();
		// System.out.println("[" + tex + "]");
		// if (!tex.equals("")) {
		// say(tex);
		// }
		// }
	}

	public static boolean download(Voice v) {
		return download(v, filePath);
	}

	public static boolean download(Voice v, String path) {
		try {
			String token = getToken();
			getMusic(v, token, path);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void sayTime() {
		LocalDateTime time = LocalDateTime.now();
		if (time.getMinute() == 0) {
			say("现在是北京时间，" + time.getHour() + "时" + time.getMinute() + "分");
		} else {
			say("现在是北京时间，" + time.getHour() + "时" + time.getMinute() + "分");
		}
	}

	public static void say(String text) {
		try {
			String token = getToken();
			Voice voice = new Voice();
			voice.setTex(text);
			getMusic(voice, token, filePath);
			playMusic(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean play(File audio) {
		return playMusic(audio.getAbsolutePath());
	}

	private static boolean playMusic(String path) {
		try {
			File music = new File(path);
			BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(music));
			Player player = new Player(buffer);
			player.play();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static String getToken() throws Exception {
		String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" + "&client_id="
				+ apiKey + "&client_secret=" + secretKey;
		HttpURLConnection conn = (HttpURLConnection) new URL(getTokenURL).openConnection();
		String token = new JSONObject(printResponse(conn)).getString("access_token");
		return token;
	}

	private static void getMusic(Voice v, String token, String path) throws Exception {
		try {
			Connection.Response response = Jsoup.connect("http://tsn.baidu.com/text2audio").data("tok", token)
					.data(v.getVoiceData()).timeout(15000).ignoreContentType(true).execute();
			System.out.println(response.contentType());
			download2local(response, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean say(Voice v) {
		try {
			String token = getToken();
			getMusic(v, token, filePath);
			playMusic(filePath);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private static boolean download2local(Connection.Response response, String path) {
		System.out.println(path);
		try {
			FileOutputStream out = (new FileOutputStream(new File(path)));
			out.write(response.bodyAsBytes());
			out.close();
			System.out.println("下载到 " + path + " 完成");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static String printResponse(HttpURLConnection conn) throws Exception {
		if (conn.getResponseCode() != 200) {
			// request error
			return "";
		}
		InputStream is = conn.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuffer response = new StringBuffer();
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		return response.toString();
	}

}
