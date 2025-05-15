package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {
    @Select("select * from user where openid = #{openid}")
    User selectByOpenid(String openid);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into user(openid,name,create_time) values (#{openid},#{name},#{createTime})  ")
    void insert(User user);

    @Select("select * from user where id = #{userId}")
    User selectById(Long userId);
}
