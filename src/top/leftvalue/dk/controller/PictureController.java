package top.leftvalue.dk.controller;

import java.io.File;

import com.jfinal.core.Controller;
import top.leftvalue.dk.model.ResponseJson;
import top.leftvalue.dk.service.CameraService;

public class PictureController extends Controller {
	public void take() {
		File file = CameraService.takepicture();
		if (file != null) {
			renderFile(file);
		} else {
			renderJson(new ResponseJson(0, "拍照失败"));
		}
	}
}
