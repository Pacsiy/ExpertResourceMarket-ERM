package edu.buaa.server.dataLayer.mapper.Resource;

import edu.buaa.server.dataLayer.domain.Paper;
import edu.buaa.server.dataLayer.domain.Patent;
import edu.buaa.server.dataLayer.domain.Project;
import edu.buaa.server.dataLayer.domain.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CommonResourceMapper {
    @Select(" select * from resources where id = #{id,jdbcType=BIGINT}; ")
    Resource getResourceByID(@Param("id") String id);

    @Select(" SELECT * FROM papers WHERE id = #{paperID,jdbcType=BIGINT}; ")
    Paper getPaperByID(@Param("paperID") String resID);

    @Select(" SELECT * FROM patents WHERE id =#{patentID,jdbcType=BIGINT}; ")
    Patent getPatentByID(@Param("patentID") String resID);

    @Select(" SELECT * FROM projects WHERE id = #{projectID,jdbcType=BIGINT}; ")
    Project getProjectByID(@Param("projectID") String resID);
}
