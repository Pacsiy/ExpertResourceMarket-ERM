package edu.buaa.server.dataLayer.domain;


import edu.buaa.server.util.JSON;

import java.math.BigInteger;
import java.sql.Timestamp;

public class ExpertApply {
    public BigInteger id;
    public Timestamp created_at;
    public String content;
    public BigInteger user_id;
    public BigInteger state;
    public BigInteger expert_id;
    public String user_name;
    public String expert_name;

    public JSON toJson() {
        JSON apply = new JSON();
        apply.put("id", id);
        apply.put("user_id", user_id);
        apply.put("create_time", created_at.toString());
        apply.put("content", content);
        apply.put("expert_id", expert_id);
        apply.put("name", expert_name);
        apply.put("state", state);
        apply.put("user_name", user_name);
        return apply;
    }
}
