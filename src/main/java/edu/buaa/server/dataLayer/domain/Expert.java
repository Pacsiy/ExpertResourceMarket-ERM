package edu.buaa.server.dataLayer.domain;

import edu.buaa.server.util.JSON;
import org.json.JSONArray;

import java.math.BigInteger;

public class Expert {
    public BigInteger id;
    public BigInteger user_id;
    public String introduction;
    public String phone;
    public String constitution;
    public String email;
    public String name;
    public String major;
    public BigInteger state;
    public String background_img;

    //新增
    public String scholar_id;
    public Integer citation_times;
    public Integer article_numbers;
    public Integer h_index;
    public Integer g_index;
    public JSONArray history_article_numbers;
    public JSONArray history_citation_times;

    public JSON toJson() {
        JSON expert = new JSON();
        expert.put("name", name);
        expert.put("introduction", introduction);
        expert.put("background_img", background_img);
        expert.put("phone", phone);
        expert.put("email", email);
        expert.put("institution", constitution);
        expert.put("Quoted_amount", citation_times);
        expert.put("HIndex", h_index);
        expert.put("GIndex", g_index);
        expert.put("major", major);
        expert.put("scholar_id",scholar_id);
        expert.put("history_article_numbers", history_citation_times);
        expert.put("history_citation_times", history_article_numbers);
        return expert;
    }

    public JSON toJsonManageSearch() {
        JSON expert = new JSON();
        expert.put("id", id);
        expert.put("name", name);
        expert.put("institution", constitution);
        expert.put("email", email);
        expert.put("major", major);
        expert.put("phone", phone);
        expert.put("state", state);
        return expert;
    }
}
