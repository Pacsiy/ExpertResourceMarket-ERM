package edu.buaa.server.dataLayer.mapper;

import edu.buaa.server.dataLayer.domain.Paper;
import edu.buaa.server.dataLayer.domain.Tag;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TagMapper {

    // Tag

    @Select("select * from tags")
    List<Tag> getAllTag();

    @Select("select * from tags join user_tag on tags.id = user_tag.tag_id where user_id = #{userID,jdbcType=BIGINT};")
    List<Tag> getTagByUserID(@Param("userID") String userID);

    @Insert("insert into tags (name) values(#{name});")
    void addTag(@Param("name") String name);

    // User Tag

    @Insert("insert into user_tag (user_id, tag_id) values(#{userID,jdbcType=BIGINT}, #{tagID,jdbcType=BIGINT});")
    void addUserTag(@Param("tagID") String tagID, @Param("userID") String userID);

    @Delete("delete from user_tag " +
            "where tag_id = #{tagID,jdbcType=BIGINT} and user_id = #{userID,jdbcType=BIGINT};")
    void deleteUserTag(@Param("tagID") String tagID, @Param("userID") String userID);

    // Expert Tag

    @Select("select * from tags join expert_tag on tags.id = expert_tag.tag_id where expert_id = #{expertID,jdbcType=BIGINT};")
    List<Tag> getTagByExpertID(@Param("expertID") String expertID);

    @Insert(" INSERT INTO expert_tag (expert_id, tag_id) values(#{expertID,jdbcType=BIGINT}, #{tagID,jdbcType=BIGINT}) ")
    void addExpertTag(@Param("tagID") String tagID, @Param("expertID") String expertID);

    @Delete(" DELETE FROM expert_tag WHERE expert_id = #{expertID,jdbcType=BIGINT} ")
    void deleteExpertTag(@Param("expertID") String expertID);

    // Resource Tag

    @Select("insert into resource_tag (resource_id, tag_id) values(#{resourceID,jdbcType=BIGINT}, #{tagID,jdbcType=BIGINT});")
    void addResourceTag(@Param("tagID") String tagID, @Param("resourceID") String resourceID);

    @Select("select * from tags join resource_tag on tags.id = resource_tag.tag_id where resource_id = #{resourceID,jdbcType=BIGINT};")
    List<Tag> getTagByResourceID(@Param("resourceID") String resourceID);

    @Delete(" DELETE FROM resource_tag " +
            " WHERE resource_id = #{resourceID,jdbcType=BIGINT}")
    void deleteResourceTag(@Param("resourceID") String resourceID);

    //根据领域得论文
    @Select("select * from papers join resource_tag on papers.id = resource_tag.resource_id " +
            "where resource_tag.tag_id = #{tagID} and is_user_visible = true and check_state = 'APPROVED' order by citation_times DESC LIMIT 10 OFFSET 0")
    List<Paper> getPaperByTag(@Param("tagID") Integer tagID);

    @Select("select * from tags where id = #{tagID}")
    Tag getTagByID(@Param("tagID") Integer tagID);
}