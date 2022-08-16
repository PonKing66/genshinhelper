package org.ponking.gih.push;

import org.ponking.gih.util.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author ponking
 * @Date 2021/5/7 14:49
 */
@Deprecated
public class ServerChanMessagePush implements MessagePush {

    private final String scKey;

    public final String SERVER_GIRL = "https://sc.ftqq.com/%s.send?text=%s&desp=%s";

    public ServerChanMessagePush(String scKey) {
        this.scKey = scKey;
    }

    @Override
    public void sendMessage(String text, String deps) {
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("desp", deps);
        HttpUtils.doGetDefault(getServerGirl(text, deps));
    }

    public String getServerGirl(String text, String desp) {
        return String.format(SERVER_GIRL, scKey, text, desp);
    }

}
