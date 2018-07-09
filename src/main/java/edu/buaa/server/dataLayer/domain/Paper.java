package edu.buaa.server.dataLayer.domain;


import edu.buaa.server.util.JSON;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;


public class Paper extends Resource {
    public String publish;
    public Integer citation_times;
    public String[] source;
    public String[] classification;
    public String[] keywords;
    public JSONArray annual_citation;

    @Override
    public JSON subClassToJson() {
        JSON paper = new JSON();
        paper.put("type", "paper");
        paper.put("publish", publish);
        paper.put("citation_times", citation_times);
        paper.put("source", getSource());
        paper.put("classification", classification);
        paper.put("keywords", new JSONArray(keywords));
        paper.put("annual_citation", annual_citation);
        return paper;
    }

    @NotNull
    public JSONArray getSource() {
        JSONArray sources = new JSONArray();
        if(source == null) return sources;
        for (String aSource : source) {
            sources.put(new JSON(aSource));
        }
        return sources;
    }

}
