package edu.buaa.server.dataLayer.domain;

import edu.buaa.server.util.JSON;

import java.math.BigInteger;

public class Tag {
    public BigInteger id;
    public String name;
    public BigInteger state;

    public JSON toJson() {
        JSON tag = new JSON();
        tag.put("id", id);
        tag.put("name", name);
        return tag;
    }
}
