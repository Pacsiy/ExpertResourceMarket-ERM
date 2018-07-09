package edu.buaa.server.dataLayer.domain;

import edu.buaa.server.util.JSON;

public class Project extends Resource {
    public String institution;
    public String[] keywords;
    public String[] chinese_library_classification;
    public String[] subject_classification;
    public String category;
    public String level;
    public String duration;
    public String evaluation_form;
    public Integer storage_year;

    @Override
    public JSON subClassToJson() {
        JSON project = new JSON();
        project.put("type", "project");
        project.put("institution", institution);
        project.put("keywords", keywords);
        project.put("chinese_library_classification", chinese_library_classification);
        project.put("subject_classification", subject_classification);
        project.put("category", category);
        project.put("level", level);
        project.put("duration", duration);
        project.put("evaluation_form", evaluation_form);
        project.put("storage_year", storage_year);
        return project;
    }
}
