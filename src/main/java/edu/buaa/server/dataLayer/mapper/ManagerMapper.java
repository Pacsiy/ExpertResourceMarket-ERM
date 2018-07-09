package edu.buaa.server.dataLayer.mapper;

import edu.buaa.server.dataLayer.domain.Manager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ManagerMapper {
    @Select("select * from managers where phone = #{cellphone};")
    Manager getByCellphone(@Param("cellphone") String cellphone);
}
