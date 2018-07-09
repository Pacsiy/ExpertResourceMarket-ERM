package edu.buaa.server.dataLayer.mapper;


import edu.buaa.server.dataLayer.domain.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageMapper {
    @Insert("insert into messages (user_id, title, content) " +
            "values (#{userID,jdbcType=BIGINT}, #{title}, #{content})")
    void addMessage(@Param("userID") String userID, @Param("title") String title
            , @Param("content") String content);

    @Select("select * from messages")
    List<Message> getAll();

    @Select("select count(id) from messages")
    Integer getRowCount();
}
