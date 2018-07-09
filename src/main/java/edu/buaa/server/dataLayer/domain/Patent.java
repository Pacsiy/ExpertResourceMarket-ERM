package edu.buaa.server.dataLayer.domain;

import edu.buaa.server.util.JSON;

import java.sql.Timestamp;

public class Patent extends Resource {
    public String application_number;
    public String publication_number;
    public Timestamp application_date;
    public Timestamp publication_date;
    public String address;
    public String[] inventor;
    public String agency;
    public String[] agent;
    public String national_code;
    public String sovereignty_item;
    public Integer page_number;
    public String patent_main_class;
    public String[] patent_class;

    @Override
    public JSON subClassToJson() {
        JSON patent = new JSON();
        patent.put("type", "patent");
        patent.put("application_number", application_number);
        patent.put("publication_number", publication_number);
        patent.put("application_date", application_date.toString());
        patent.put("publication_date", publication_date.toString());
        patent.put("address", address);
        patent.put("inventor", inventor);
        patent.put("agency", agency);
        patent.put("agent", agent);
        patent.put("national_code", national_code);
        patent.put("sovereignty_item", sovereignty_item);
        patent.put("page_number", page_number);
        patent.put("patent_main_class", patent_main_class);
        patent.put("patent_class", patent_class);
        return patent;
    }
}
