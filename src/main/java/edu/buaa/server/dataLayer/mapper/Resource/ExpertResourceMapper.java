package edu.buaa.server.dataLayer.mapper.Resource;

import edu.buaa.server.dataLayer.domain.Resource;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ExpertResourceMapper {
    @Select(" select * from resources where leader_id = #{expertID,jdbcType=BIGINT} and is_expert_visible = true ")
    List<Resource> getResourceByOwnerID(@Param("expertID") String expertID);

    @Insert("INSERT INTO projects(title, leader_id, introduction, file_path,member_id_list,authors)  " +
            " VALUES (#{title}, #{leader_id,jdbcType=BIGINT}, #{introduction}, #{file_path} ," +
            " #{member_id_list},#{authors,jdbcType=ARRAY})")
    @Options(useGeneratedKeys = true)
    void addProject(Resource resource);

    @Insert("INSERT INTO patents(title, leader_id, introduction, file_path,member_id_list,authors)  " +
            " VALUES (#{title}, #{leader_id,jdbcType=BIGINT}, #{introduction}, #{file_path}," +
            " #{member_id_list},#{authors,jdbcType=ARRAY})")
    @Options(useGeneratedKeys = true)
    void addPatent(Resource resource);

    @Insert("INSERT INTO papers(title, leader_id, introduction, file_path,member_id_list,authors)  " +
            " VALUES (#{title}, #{leader_id,jdbcType=BIGINT}, #{introduction}, #{file_path}," +
            " #{member_id_list},#{authors,jdbcType=ARRAY})")
    @Options(useGeneratedKeys = true)
    void addPaper(Resource resource);
    // fixme waiting for hash method, now set hash as default 0

    @Update(" update resources set is_expert_visible = false ,is_user_visible = false " +
            " where id = #{resID,jdbcType=BIGINT} and leader_id = #{expertID,jdbcType=BIGINT}; ")
    void deleteResource(@Param("resID") String resID, @Param("expertID") String expertID);

    @Update(" update resources " +
            " set rent_price = #{rent_price}, buy_price = #{buy_price}, is_user_visible = #{is_user_visible}, " +
            " introduction = #{introduction}, file_path = #{file_path} " +
            " where id = #{id,jdbcType=BIGINT} and leader_id = #{leader_id,jdbcType=BIGINT};")
    void updateResource(Resource resource);
    // fixme 如果filepath 可变，文件是否可变？hash是否改变？

    @Deprecated
    @Update(" UPDATE papers " +
            " SET magazine = #{magazine}, published_at = #{published_at}, " +
            " classify_number = #{classify_number}, level = #{level} " +
            " WHERE id = #{resID,jdbcType=BIGINT} ")
    void updatePaper(@Param("magazine") String magazine, @Param("published_at") String published_at,
                     @Param("classify_number") String classify_number, @Param("level") String level,
                     @Param("resID") String resID);

    @Deprecated
    @Update(" UPDATE patents " +
            " SET applied_at = #{apply_time}, authorized_at = #{authorized_time}, " +
            " major_classify_number = #{classify_number}, legal_person = #{legal_person}, " +
            " patent_number = #{patent_number} WHERE id = #{resID,jdbcType=BIGINT} ")
    void updatePatent(@Param("apply_time") String apply_time, @Param("authorized_time") String authorized_time,
                      @Param("classify_number") String classify_number, @Param("legal_person") String legal_person,
                      @Param("patent_number") String patent_number, @Param("resID") String resID);

    @Deprecated
    @Update(" UPDATE projects " +
            " SET developer = #{developer}, complished_at = #{complish_time}, " +
            " project_number = #{project_number}, bided_at = #{bid_time} " +
            " WHERE id = #{resID,jdbcType=BIGINT} ")
    void updateProject(@Param("developer") String developer, @Param("complish_time") String complish_time,
                       @Param("project_number") String project_number, @Param("bid_time") String bid_time,
                       @Param("resID") String resID);

    @Select("select count(*) from resources where leader_id = #{expertID,jdbcType=BIGINT} " +
            "and is_user_visible = true;")
    Integer getExpertResCountFromUserAspect(@Param("expertID") String expertID);

    @Select("select * from resources where leader_id = #{expertID,jdbcType=BIGINT} " +
            "and is_user_visible = true")
    List<Resource> getResByExpertIDFromUserAspect(@Param("expertID") String expertID);
}
