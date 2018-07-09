package edu.buaa.server.dataLayer.domain;

import edu.buaa.server.util.JSON;

import java.math.BigInteger;
import java.sql.Timestamp;

enum TradeType {
    BUY, RENT
}

public class TradeResource {
    public BigInteger id;
    public Double price;
    public TradeType type;
    public Timestamp started_time;
    public Timestamp ended_time;
    public BigInteger seller_id;
    public BigInteger buyer_id;
    public BigInteger resource_id;
    public BigInteger state;


    public JSON toJson() {
        JSON trade = new JSON();
        trade.put("id", id);
        trade.put("price", price);
        trade.put("type", type.toString().toLowerCase());
        trade.put("start_time", started_time);
        trade.put("expire_time", ended_time == null ? "-" : ended_time);
        trade.put("resource_id", resource_id);
        return trade;
    }

    public JSON toJsonInAdmin() {
        JSON trade = new JSON();
        trade.put("id", id);
        trade.put("price", price);
        trade.put("type", type.toString().toLowerCase());
        trade.put("start_time", started_time);
        trade.put("expire_time", ended_time == null ? "-" : ended_time);
        trade.put("resource_id", resource_id);
        trade.put("seller_id", seller_id);
        trade.put("buyer_id", buyer_id);
        return trade;
    }
}
