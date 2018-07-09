package edu.buaa.server.dataLayer.mapper;

import edu.buaa.server.dataLayer.domain.Expert;
import edu.buaa.server.dataLayer.domain.HotTagsBean;
import edu.buaa.server.dataLayer.domain.Paper;
import edu.buaa.server.dataLayer.domain.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StatisticMapper {
    @Select("SELECT * FROM HotTags_Top10")
    List<HotTagsBean> getTop10Tags();

    @Select("SELECT * FROM RecentTrade")
    List<Expert> getTop5Expert();

    @Select("SELECT count(*) FROM trades WHERE seller_id = #{expertID,jdbcType=BIGINT} " +
            " and started_time > ( now() - interval #{day1} day) " +
            " and started_time <= ( now() - interval #{day2} day)")
    Integer getXDayBefore(@Param("day1") String day,
                          @Param("day2") String day2,
                          @Param("expertID") String expertID);
    /*
    @Select("SELECT id,name from experts join expert_tag on experts.id = expert_tag.expert_id where tag_id=#{tagID}")
    */
    @Select(" select distinct experts.id,experts.name from experts " +
            "  join resources on resources.leader_id = experts.id " +
            "  join resource_tag on resource_tag.resource_id = resources.id " +
            " where resource_tag.tag_id = #{tagID}; ")
    List<Expert> getRelationExperts(@Param("tagID") Integer tagID);

    @Select("select * from tags where id= #{tagID}")
    Tag getTagByID(@Param("tagID") Integer tagID);

    @Select("select * from papers where id = #{paperID}")
    Paper getPaperByID(@Param("paperID") Integer paperID);

    @Select("select tag_id from resource_tag where resource_id = #{resID}")
    Integer[] getTagIDByPaperID(@Param("resID") Integer resID);

    /*
    @Select("select * from papers where id in " +
            " (select resource_id from resource_tag where tag_id = #{tagID}) " +
            " and id != #{paperID} LIMIT 5 OFFSET 0")
    */
    @Select(" select papers.* " +
            " from papers " +
            " join resource_tag on papers.id = resource_tag.resource_id " +
            " where tag_id = #{tagID} and papers.id != #{paperID}" +
            " LIMIT 5 OFFSET 0 ")
    List<Paper> getRelationPapers(@Param("tagID") Integer tagID,
                                  @Param("paperID") Integer paperID);

    /*
    @Select("Select * from experts join expert_tag " +
            " on expert_tag.expert_id = experts.id " +
            " where expert_tag.tag_id = #{tagID} and history_citation_times is not null" +
            " order by experts.citation_times DESC limit 5 offset 0")
    */
    @Select("select distinct experts.* " +
            "from experts " +
            "  join papers on papers.leader_id = experts.id " +
            "  join resource_tag on resource_tag.resource_id = papers.id " +
            "where resource_tag.tag_id = #{tagID} and history_citation_times is not null " +
            "order by experts.citation_times DESC limit 5 offset 0")
    List<Expert> getTop5ExpertByCitationtimes(@Param("tagID") Integer tagID);

    @Select("select * from experts where history_citation_times is not null order by citation_times DESC limit 5 offset 0")
    List<Expert> getTop5ExpertsByCite();

    /*
    @Select("select * from tags where id in " +
            "(select tag_id from resource_tag where resource_id in " +
            "(select resource_id from resource_tag where tag_id = #{tagID}))")
    */
    @Select(" select distinct tags.* " +
            " from resource_tag as base_rt " +
            "   join resource_tag as related_rt on ( " +
            "       base_rt.resource_id = related_rt.resource_id " +
            "       and base_rt.tag_id = #{tagID} " +
            "       ) " +
            "   join tags on tags.id = related_rt.tag_id ")
    List<Tag> getSimilarTags(@Param("tagID") Integer tagID);

    @Select(" select id,title,citation_times from papers " +
            " where citation_times is not null " +
            " order by citation_times DESC limit 10 offset 0 ")
    List<Paper> getHottestPapers();

    @Select("select id,title,created_at from papers order by created_at DESC limit 10 offset 0")
    List<Paper> getNewestPapers();

}
