package edu.buaa.server.dataLayer.domain;

import edu.buaa.server.util.JSON;

import java.math.BigInteger;
import java.sql.Timestamp;

public class Message {
    public BigInteger id;
    public BigInteger user_id;
    public String title;
    public String content;
    public BigInteger state;
    public Timestamp send_time;

    public JSON toJson() {
        JSON message = new JSON();
        message.put("id", id);
        message.put("user_id", user_id);
        message.put("title", title);
        message.put("content", content);
        message.put("create_time", send_time.toString());
        return message;
    }
}
