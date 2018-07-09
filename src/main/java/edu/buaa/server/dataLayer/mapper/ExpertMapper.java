package edu.buaa.server.dataLayer.mapper;


import edu.buaa.server.dataLayer.domain.Expert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ExpertMapper {
    @Select("select * from experts where name = #{realName} and email = #{email}")
    Expert getByRealNameAndEmail(@Param("realName") String realName, @Param("email") String email);

    @Select("select * from experts where user_id = #{userID,jdbcType=BIGINT}")
    Expert getByUserID(@Param("userID") String userID);

    @Select("select * from experts where id = #{id,jdbcType=BIGINT}")
    Expert getByID(@Param("id") String id);

    @Update("update experts " +
            "set introduction = #{intro}, phone = #{phone}, email = #{email}, constitution = #{constitution}," +
            " background_img = #{bgimg} " +
            "where id = #{expertID,jdbcType=BIGINT};")
    void updateExpertInfo(@Param("expertID") String expertID, @Param("phone") String phone, @Param("email") String email,
                          @Param("constitution") String constitution, @Param("intro") String intro,
                          @Param("bgimg") String backgroundImg);

    @Update("update experts " +
            "set user_id = (select user_id from applies where id = #{applyID,jdbcType=BIGINT})" +
            "where id = #{expertID,jdbcType=BIGINT};")
    void updateExpertUserID(@Param("expertID") String expertID, @Param("applyID") String applyID);

    @Update("update applies set state = 1 where id = #{applyID}")
    void updateApplyApproved(@Param("applyID") Integer applyID);

    @Update("update applies set state = -1 where id = #{applyID}")
    void updateApplyRejected(@Param("applyID") Integer applyID);


    @Update(" UPDATE experts " +
            " SET state = #{expertState} " +
            " WHERE id = #{expertID,jdbcType=BIGINT} ")
    void updateState(@Param("expertState") String expertState, @Param("expertID") String expertID);

    @Select(" SELECT count(*) FROM experts WHERE name LIKE CONCAT('%', #{keyword}, '%') ")
    Integer getRowCountByKey(@Param("keyword") String keyword);

    @Select(" SELECT * from experts WHERE name LIKE CONCAT('%', #{keyword}, '%') order by citation_times DESC")
    List<Expert> getAllByKey(@Param("keyword") String keyword);
}