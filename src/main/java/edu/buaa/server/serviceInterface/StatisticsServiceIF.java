package edu.buaa.server.serviceInterface;

import edu.buaa.server.dataLayer.domain.Expert;
import edu.buaa.server.dataLayer.domain.HotTagsBean;
import edu.buaa.server.dataLayer.domain.Paper;
import edu.buaa.server.dataLayer.domain.Tag;

import java.util.List;

public interface StatisticsServiceIF {
    List<Expert> getTop5Experts();

    List<Integer> getRecent7DaysTradeByExpertID(String expertID);

    List<HotTagsBean> getTop10Tags();

    List<Expert> getRelationExpertByTag(String tagID, Tag tag);

    List<Paper> getRelationPapers(String paperID);

    List<Expert> getTop5ExpertsByTagID(Integer tagID);

    List<Expert> getTop5ExpertsByCitations();

    Paper getPaperByID(Integer paperID);

    List<Tag> getSimilarTags(Integer tagID);


}
