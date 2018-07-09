package edu.buaa.server.serviceInterface;

import edu.buaa.server.dataLayer.domain.Expert;
import edu.buaa.server.dataLayer.domain.Resource;
import edu.buaa.server.dataLayer.domain.Tag;

import java.util.ArrayList;
import java.util.List;

public interface SpiderServiceIF {
    void insertTag(Tag tag);

    void insertExpertsByRes(ArrayList<Expert> experts);

    void insertResource(Resource resource, List<Tag> tagList, ArrayList<Expert> experts);

    void insertExpert(Expert expert, List<Tag> tagList);
}
