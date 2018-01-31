package top.leftvalue.dk.controller;

import com.alibaba.fastjson.JSON;
import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import top.leftvalue.dk.model.ResponseJson;

import java.io.File;

public class FileController extends Controller {
    /**
     * 远程目录管理之查看指定目录文件
     */
    public void ls() {
        String path = "/";
        String temppath = getPara("path");
        if (temppath != null && new File(temppath).isDirectory()) {
            path = temppath;
        }
        File dir = new File(path);
        File[] files = dir.listFiles();
        renderJson(JSON.toJSONString(files));
    }

    /**
     * 下载指定文件
     */
    public void download() {
        String path = getPara("path");
        File f = new File(path);
        if (f.exists() && f.isFile()) {
            renderFile(f);
        } else {
            renderJson(ResponseJson.SUCCESS);
        }
    }

    /***
     * 上传文件到指定目录
     */
    public void upload() {
        try {
            getFile();
            boolean flag = false;
            String targetpath = getPara("path");
            if (targetpath != null) {
                File file = new File(targetpath);
                if (!file.exists() || file.isFile()) {
                    file.mkdirs();
                }
                UploadFile uploadFile = getFiles().get(0);
                File upload = uploadFile.getFile();
                upload.renameTo(new File(file.getAbsolutePath() + "/" + upload.getName()));
                upload.deleteOnExit();
                flag = true;
            }
            if (flag) {
                renderJson(ResponseJson.SUCCESS);
            } else {
                renderJson(ResponseJson.FAIL);
            }
        } catch (Exception e) {
            renderJson(ResponseJson.FAIL);
        }
    }

    /**
     * 指定目录,指定名称
     * 新建目录
     */
    public void mkdir() {
        String path = getPara("path");
        boolean flag = false;
        if (path != null) {
            File f = new File(path);
            if (!f.exists() || f.isFile()) {
                f.mkdirs();
                flag = true;
            }
        }
        if (flag) {
            renderJson(ResponseJson.SUCCESS);
        } else {
            renderJson(ResponseJson.FAIL);
        }
    }
}
