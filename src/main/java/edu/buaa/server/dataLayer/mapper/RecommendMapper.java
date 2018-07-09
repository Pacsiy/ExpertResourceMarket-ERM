package edu.buaa.server.dataLayer.mapper;

import edu.buaa.server.dataLayer.domain.Expert;
import edu.buaa.server.dataLayer.domain.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RecommendMapper {
    @Select("select * from experts where id in ${array}")
    List<Expert> getExpertArray(@Param("array") String expertIDArray);

    @Select("select id,type from resources where id in ${array} and is_user_visible = true;")
    List<Resource> getResourceArray(@Param("array") String resIDArray);

}
