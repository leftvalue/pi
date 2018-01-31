package top.leftvalue.dk.model;

import java.util.HashMap;

/**
 * @author linxi
 * top.leftvalue.dk.model
 * 百度语音的配置项
 * Date 2017/12/10 上午1:02
 */
public class Voice {

    /**
     * 合成的文本，使用UTF-8编码，请注意文本长度必须小于1024字节	是
     */
    private String tex = "好男人就是我,我就是曾小贤";
    /**
     * 语言选择,填写zh	是
     */
    private String lang = "zh";
    /**
     * 客户端类型选择，web端填写1	是
     */
    private String ctp = "1";
    /**
     * 用户唯一标识，用来区分用户，填写机器 MAC 地址或 IMEI 码，长度为60以内	否
     */
    private String cuid = "abckxxx";
    /**
     * 语速，取值0-9，默认为5中语速	否
     */
    private int spd = 5;
    /**
     * 音调，取值0-9，默认为5中语调	否
     */
    private int pit = 5;
    /**
     * 音量，取值0-15，默认为5中音量	否
     */
    private int vol = 5;
    /**
     * 发音人选择, 0为女声，1为男声，3为情感合成-度逍遥，4为情感合成-度丫丫，默认为度丫丫，	否
     */
    private int per = 4;

    public void setTex(String tex) {
        this.tex = tex;
    }

    public void setSpd(int spd) {
        if (spd >= 0 && spd <= 9) {
            this.spd = spd;
        }
    }

    public void setPit(int pit) {
        if (pit >= 0 && pit <= 9) {
            this.pit = pit;
        }
    }

    public void setVol(int vol) {
        if (vol >= 0 && vol <= 15) {
            this.vol = vol;
        }
    }

    public void setPer(int per) {
        if (per >= 0 && per <= 4) {
            this.per = per;
        }
    }

    public HashMap<String, String> getVoiceData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("lan", lang);
        map.put("ctp", ctp);
        map.put("cuid", cuid);
        /**
         * 客制化
         */
        map.put("tex", tex);
        map.put("vol", String.valueOf(vol));
        map.put("pit", String.valueOf(pit));
        map.put("per", String.valueOf(per));
        map.put("spd", String.valueOf(spd));
        return map;
    }
}
