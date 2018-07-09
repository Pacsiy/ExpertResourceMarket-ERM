package edu.buaa.server.dataLayer.mapper;

import edu.buaa.server.dataLayer.domain.ExpertApply;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ApplyMapper {
    @Insert(" INSERT INTO applies(content, user_id, expert_id, expert_name, user_name) " +
            " values(#{content}, #{userID,jdbcType=BIGINT}, #{expertID,jdbcType=BIGINT}, #{expertName}, #{userName}) ")
    void addApply(@Param("userID") String userID, @Param("content") String content,
                  @Param("expertID") String expertID, @Param("expertName") String expertName,
                  @Param("userName") String userName);

    @Select("select count(*) from applies")
    Integer getRowCount();

    @Select("select * from applies order by id desc ")
    List<ExpertApply> getAll();
}
