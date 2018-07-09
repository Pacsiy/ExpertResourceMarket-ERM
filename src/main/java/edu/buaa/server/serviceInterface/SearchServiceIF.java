package edu.buaa.server.serviceInterface;

import edu.buaa.server.dataLayer.domain.Expert;
import edu.buaa.server.dataLayer.domain.Paper;
import edu.buaa.server.dataLayer.domain.Patent;
import edu.buaa.server.dataLayer.domain.Project;
import edu.buaa.server.util.IntegerWrapper;

import java.util.List;

public interface SearchServiceIF {
    //管理员搜索
    List<Object> managerSearch(String keyword, String type, String index, String size, IntegerWrapper userNum, IntegerWrapper expertNum);

    //用户普通搜索
    List<Paper> searchPapers(String keyword, String index, String size, IntegerWrapper pageNum);

    List<Patent> searchPatents(String keyword, String index, String size, IntegerWrapper pageNum);

    List<Project> searchProjects(String keyword, String index, String size, IntegerWrapper pageNum);

    List<Expert> searchExperts(String keyword, String index, String size, IntegerWrapper pageNum);
}
