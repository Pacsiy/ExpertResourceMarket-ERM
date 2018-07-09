package edu.buaa.server.dataLayer.mapper;

import edu.buaa.server.dataLayer.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SearchMapper {
    @Select("Select count(*) from papers where " +
            "to_tsvector('testzhcfg', title || introduction) @@ " +
            " to_tsquery('testzhcfg', #{keyword})")
    Integer getPapersCount(@Param("keyword") String keyword);

    @Select("Select * from papers where " +
            "to_tsvector('testzhcfg', title || introduction) " +
            "@@ to_tsquery('testzhcfg', #{keyword})")
    List<Paper> getSearchPapers(@Param("keyword") String keyword);

    @Select("Select count(*) from patents where " +
            "to_tsvector('testzhcfg', title || introduction) " +
            "@@ to_tsquery('testzhcfg', #{keyword})")
    Integer getPatentsCount(@Param("keyword") String keyword);

    @Select("Select * from patents where " +
            "to_tsvector('testzhcfg', title || introduction) " +
            "@@ to_tsquery('testzhcfg', #{keyword})")
    List<Patent> getSearchPatents(@Param("keyword") String keyword);

    @Select("Select count(*) from projects where " +
            "to_tsvector('testzhcfg', title || introduction) " +
            "@@ to_tsquery('testzhcfg', #{keyword})")
    Integer getProjectsCount(@Param("keyword") String keyword);

    @Select("Select * from projects where " +
            "to_tsvector('testzhcfg', title || introduction) " +
            "@@ to_tsquery('testzhcfg', #{keyword})")
    List<Project> getSearchProjects(@Param("keyword") String keyword);

    @Select("Select count(*) from experts where name like #{keyword}")
    Integer getExpCount(@Param("keyword") String keyword);

    @Select("Select * from experts where name like #{keyword} order by citation_times DESC")
    List<Expert> getSearchExperts(@Param("keyword") String keyword);

    @Select("Select * from tags where name like #{tagName}")
    List<Tag> getTagByName(@Param("tagName") String tagName);
}
