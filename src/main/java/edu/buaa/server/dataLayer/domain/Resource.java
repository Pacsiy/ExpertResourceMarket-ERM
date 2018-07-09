package edu.buaa.server.dataLayer.domain;

import edu.buaa.server.dataLayer.domain.EnumType.CheckState;
import edu.buaa.server.dataLayer.domain.EnumType.ResourceType;
import edu.buaa.server.util.JSON;
import org.json.JSONArray;

import java.math.BigInteger;
import java.sql.Timestamp;


public class Resource {
    public BigInteger id;
    public String title;
    public ResourceType type;
    public CheckState check_state;
    public BigInteger leader_id;
    public String introduction;
    public Double buy_price;
    public Double rent_price;
    public Boolean is_user_visible;
    public Boolean is_expert_visible;
    public String file_path;
    public Double score;
    public Timestamp created_at;
    public BigInteger state;
    public String html;
    public String url;
    public String hash;
    public Long[] member_id_list;
    public String[] authors;

    public JSON toJson() {
        JSON resource = new JSON();
        resource.put("id", id);
        resource.put("name", title);
        resource.put("type", type.toString().toLowerCase());
        resource.put("introduction", introduction);
        resource.put("create_time", created_at.toString());
        resource.put("file", file_path);
        resource.put("leader_id", leader_id);
        resource.put("check_state", check_state.toString().toLowerCase());
        resource.put("buy_price", buy_price);
        resource.put("rent_price", rent_price);
        resource.put("score", score);
        resource.put("enable", is_user_visible);
        resource.put("owner_name", authors[0]);
        resource.put("state", state);
        resource.put("authors", generateAuthors());
        return resource;
    }

    public JSON detailToJson() {
        JSON res = new JSON();
        res.put("name", title);
        res.put("type", type.toString().toLowerCase());
        res.put("introduction", introduction);
        res.put("file", file_path);
        res.put("enable", is_user_visible);
        res.put("rent_price", rent_price);
        res.put("buy_price", buy_price);
        res.put("state", state);
        res.put("create_time", created_at.toString());
        res.put("owner_id", leader_id);
        res.put("owner_name", authors[0]);
        res.put("score", score);
        res.put("authors", generateAuthors());
        return res;
    }

    public JSONArray generateAuthors() {
        JSONArray authorList = new JSONArray();
        for (int i=0;i<authors.length;i++) {
            JSON author = new JSON();
            author.put("id",member_id_list[i]);
            author.put("name", authors[i]);
            authorList.put(author);
        }
        return authorList;
    }

    public JSON subClassToJson() {
        return new JSON();
    }
}
