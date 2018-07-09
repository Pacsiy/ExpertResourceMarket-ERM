package edu.buaa.server.dataLayer.domain;

import edu.buaa.server.util.JSON;

import java.math.BigInteger;

public class HotTagsBean {
    public BigInteger tag_id;
    public String name;
    public String value;

    public JSON toJson() {
        JSON tag = new JSON();
        tag.put("tag_id",tag_id);
        tag.put("theme", name);
        tag.put("value", value);
        return tag;
    }
}
