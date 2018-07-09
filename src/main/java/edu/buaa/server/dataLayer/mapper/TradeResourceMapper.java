package edu.buaa.server.dataLayer.mapper;

import edu.buaa.server.dataLayer.domain.TradeResource;
import edu.buaa.server.dataLayer.domain.TransferPatent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TradeResourceMapper {
    @Select("select * from trades where buyer_id = #{userID,jdbcType=BIGINT}")
    List<TradeResource> getTradeByBuyerID(@Param("userID") String userID);

    @Select("select * from trades where id = #{id,jdbcType=BIGINT}")
    TradeResource getTradeByID(@Param("id") String id);

    @Select("select * from trade(#{resID,jdbcType=BIGINT},#{type}::trade_type,#{buyerID,jdbcType=BIGINT},#{startTime}::TIMESTAMP,#{endTime}::TIMESTAMP)")
    @Options(statementType = StatementType.CALLABLE)
    TradeResource tradeResource(@Param("resID") String resourceID, @Param("type") String trade_type, @Param("buyerID") String buyerID,
                                @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("call transfer(#{price},#{content},#{resourceID,jdbcType=BIGINT},#{buyerExpertID,jdbcType=BIGINT})")
    @Options(statementType = StatementType.CALLABLE)
    TransferPatent transferResource(@Param("price") String price, @Param("content") String content, @Param("resourceID") String resourceID,
                                    @Param("buyerExpertID") String buyerExpertID);
}
