package edu.buaa.server.dataLayer.mapper.Resource;

import edu.buaa.server.dataLayer.domain.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserResourceMapper {
    @Select(" select * from resources where leader_id = #{expertID,jdbcType=BIGINT} and is_user_visible = true ")
    List<Resource> getResourceByExpertID(@Param("expertID") String expertID);
}
