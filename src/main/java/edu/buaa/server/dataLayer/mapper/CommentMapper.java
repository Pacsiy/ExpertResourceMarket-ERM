package edu.buaa.server.dataLayer.mapper;

import edu.buaa.server.dataLayer.domain.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentMapper {
    @Insert("insert into comments(content, score, user_id, resource_id) " +
            "values(#{content},#{score}::float,#{userID,jdbcType=BIGINT},#{resourceID,jdbcType=BIGINT})")
    void addComment(@Param("score") String score,
                    @Param("content") String content,
                    @Param("userID") String userID,
                    @Param("resourceID") String resID);

    @Select("select * from comments where resource_id = #{resID,jdbcType=BIGINT}")
    List<Comment> getByResID(@Param("resID") String resID);


    @Select("select count(*) from comments where resource_id = #{resID,jdbcType=BIGINT}")
    Integer getRowCountByResID(@Param("resID") String resID);
}
