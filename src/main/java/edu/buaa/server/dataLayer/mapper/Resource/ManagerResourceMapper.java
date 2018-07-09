package edu.buaa.server.dataLayer.mapper.Resource;

import edu.buaa.server.dataLayer.domain.Resource;
import edu.buaa.server.dataLayer.domain.TradeResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ManagerResourceMapper {
    @Update(" update resources " +
            " set check_state = 'REJECTED'::check_state, is_expert_visible = false " +
            " where id = #{resID,jdbcType=BIGINT}; ")
    void rejectResource(@Param("resID") String resID);

    @Update(" update resources " +
            " set check_state = 'APPROVED'::check_state, is_user_visible = true" +
            " where id = #{resID,jdbcType=BIGINT}; ")
    void approveResource(@Param("resID") String resID);

    @Select(" select count(*) from resources where check_state = 'PENDING' ")
    Integer getPendingRowCount();

    @Select(" select * from resources where check_state = 'PENDING' ")
    List<Resource> getPendingAll();

    @Select(" select * from trades ")
    List<TradeResource> getAllTradeResource();

    @Select(" select count(*) from trades ")
    Integer getTradeCount();
}
