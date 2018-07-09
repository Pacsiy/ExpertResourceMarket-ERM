package edu.buaa.server.dataLayer.domain;

import edu.buaa.server.util.JSON;

import java.math.BigInteger;
import java.sql.Timestamp;

public class Comment {
    public BigInteger id;
    public String content;
    public Timestamp created_at;
    public Double score;
    public BigInteger user_id;
    public BigInteger resource_id;
    public BigInteger state;

    public JSON toJson() {
        JSON comment = new JSON();
        comment.put("content", content);
        comment.put("create_time", created_at.toString());
        comment.put("score", score);
        return comment;
    }
}
