package edu.buaa.server.dataLayer.mapper;


import edu.buaa.server.dataLayer.domain.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {
    @Select(" select * from users where id = #{id,jdbcType=BIGINT} ")
    User getByID(@Param("id") String id);

    @Select(" select * from users where phone = #{phone} ")
    User getByPhone(@Param("phone") String phone);

    @Insert(" insert into users (nickname, phone, email, password) " +
            " values (#{username}, #{cellphone}, #{email}, #{password}) ")
    void setUserAccount(@Param("username") String username, @Param("password") String password
            , @Param("email") String email, @Param("cellphone") String cellphone);

    @Update(" UPDATE users SET balance = balance + #{balance,jdbcType=BIGINT} WHERE id = #{id,jdbcType=BIGINT} ")
    void updateBalance(@Param("balance") String balance, @Param("id") String id);

    @Update(" UPDATE users " +
            " SET nickname = #{name}, email = #{email}, avator = #{avator} " +
            " WHERE id = #{id,jdbcType=BIGINT} ")
    void updateUserInfo(@Param("name") String name, @Param("email") String email,
                        @Param("avator") String avator, @Param("id") String id);

    @Update(" UPDATE users " +
            " SET password = #{password} " +
            " WHERE id = #{id,jdbcType=BIGINT} ")
    void updatePassword(@Param("password") String password, @Param("id") String id);

    @Update(" UPDATE users " +
            " SET state = #{userState}::user_state " +
            " WHERE id = #{id,jdbcType=BIGINT} ")
    void updateState(@Param("userState") String userState, @Param("id") String id);

    @Select(" SELECT count(*) FROM users WHERE nickname LIKE CONCAT('%', #{keyword}, '%') ")
    Integer getRowCountByKey(@Param("keyword") String keyword);

    @Select(" SELECT * from users WHERE nickname LIKE CONCAT('%', #{keyword}, '%') ")
    List<User> getAllByKey(@Param("keyword") String keyword);
}
